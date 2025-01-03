const { ComponentDialog, WaterfallDialog, TextPrompt } = require('botbuilder-dialogs');
const axios = require('axios'); // Importa o axios

const WATERFALL_DIALOG = 'waterfallDialog';
const CPF_PROMPT = 'cpfPrompt';

class Extrato extends ComponentDialog {
    constructor(id) {
        super(id);

        // Adicionar o TextPrompt para solicitar o CPF
        this.addDialog(new TextPrompt(CPF_PROMPT));

        // Configurar o WaterfallDialog com os novos passos
        this.addDialog(new WaterfallDialog(WATERFALL_DIALOG, [
            this.promptForIDStep.bind(this),
            this.processExtratoStep.bind(this),
            this.finalStep.bind(this)
        ]));

        this.initialDialogId = WATERFALL_DIALOG;
    }

    // Passo 1: Solicita o CPF ao usuário
    async promptForIDStep(stepContext) {
        return await stepContext.prompt(CPF_PROMPT, {
            prompt: 'Digite o ID do cartao para mostrar o extrato:'
        });
    }

    // Passo 2: Processa o CPF e chama a API
    async processExtratoStep(stepContext) {
        const cpf = stepContext.result;
        // no swagger ta puxando o id da transacao, no caso teria que pedir o cpf

        try {
            const response = await axios.get(`https://ap2-bigdata-20242-cartaocredito.azurewebsites.net/transacao/extrato-cartao/1`); // consertar caminho pois so esta pegando o primeiro
            const extrato = response.data;

            if (extrato && extrato.length > 0) {
                let mensagem = 'Extrato:\n\n';
                const campos = {
                    "Compra:": "id",
                    "Produto:": "comerciante",
                    "Preço:": "valor",
                    "Data da Compra:": "dataTransacao"
                };
            
                extrato.forEach(item => {
                    Object.entries(campos).forEach(([label, key]) => {
                        if (key === "valor") {
                            mensagem += `\n${label} R$ ${item["valor"]}\n`;
                        } else if (key === "dataTransacao") {
                            mensagem += `\n${label} ${new Date(item[key]).toLocaleString('pt-BR')}\n`;
                        } else {
                            mensagem += `\n${label} ${item[key]}\n`;
                        }
                    });
                    mensagem += `---------------------------------\n`;
                });
                await stepContext.context.sendActivity(mensagem);
            } else {
                await stepContext.context.sendActivity('Não foram encontrados registros.');}
            } catch (error) {
                console.error('Erro ao chamar a API:', error);
                await stepContext.context.sendActivity('Ocorreu um erro ao obter o extrato.');
            }
            return await stepContext.next();
    }

    async finalStep(stepContext) {
        await stepContext.context.sendActivity('Posso ajudar em mais alguma coisa?');
        return await stepContext.endDialog();
    }
}

module.exports.Extrato = Extrato;

