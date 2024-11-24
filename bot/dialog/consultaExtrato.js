const { ComponentDialog, WaterfallDialog, TextPrompt } = require('botbuilder-dialogs');
const axios = require('axios'); // Biblioteca para requisições HTTP

const MAIN_DIALOG = 'mainDialog';
const CPF_PROMPT = 'cpfPrompt';

class ConsultaExtrato extends ComponentDialog {
    constructor(dialogId) {
        super(dialogId);

        // Adiciona um TextPrompt para capturar o CPF do usuário
        this.addDialog(new TextPrompt(CPF_PROMPT));

        // Configura o WaterfallDialog com as etapas do fluxo
        this.addDialog(new WaterfallDialog(MAIN_DIALOG, [
            this.requestCpfStep.bind(this),
            this.fetchExtratoStep.bind(this),
            this.concludeDialogStep.bind(this)
        ]));

        this.initialDialogId = MAIN_DIALOG;
    }

    /**
     * Etapa 1: Solicita ao usuário o CPF
     */
    async requestCpfStep(stepContext) {
        return await stepContext.prompt(CPF_PROMPT, {
            prompt: 'Para acessar seu extrato, por favor informe seu CPF:'
        });
    }

    /**
     * Etapa 2: Processa o CPF e realiza a chamada à API
     */
    async fetchExtratoStep(stepContext) {
        const cpfUsuario = stepContext.result;

        try {
            // Fazendo uma requisição para obter os dados do extrato
            const apiResponse = await axios.get(`https://apipedidosextratos.azurewebsites.net/ecommerce/extrato/buscar-por-cpf/${cpfUsuario}`);
            const extratoData = apiResponse.data;

            if (extratoData && extratoData.length > 0) {
                let extratoMensagem = '**Extrato de Compras Encontrado:**\n\n';
                extratoData.forEach(item => {
                    extratoMensagem += `**CPF:** ${item.cpf}\n`;
                    extratoMensagem += `**ID da Compra:** ${item.id}\n`;
                    extratoMensagem += `**Produto:** ${item.productName}\n`;
                    extratoMensagem += `**Valor:** R$ ${item.price.toFixed(2)}\n`;
                    extratoMensagem += `**Data:** ${new Date(item.dataCompra).toLocaleString('pt-BR')}\n`;
                    extratoMensagem += `---------------------------------\n`;
                });
                await stepContext.context.sendActivity(extratoMensagem);
            } else {
                await stepContext.context.sendActivity('Não encontramos registros de compras para o CPF informado.');
            }
        } catch (error) {
            console.error('Erro ao acessar a API:', error);
            await stepContext.context.sendActivity('Ocorreu um problema ao consultar o extrato. Por favor, tente novamente mais tarde.');
        }

        return await stepContext.next();
    }

    /**
     * Etapa 3: Finaliza o diálogo
     */
    async concludeDialogStep(stepContext) {
        await stepContext.context.sendActivity('Espero ter ajudado! Se precisar de mais alguma coisa, é só avisar.');
        return await stepContext.endDialog();
    }
}

module.exports.ConsultaExtrato = ConsultaExtrato;
