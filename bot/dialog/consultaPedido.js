const { ComponentDialog, WaterfallDialog, TextPrompt } = require('botbuilder-dialogs');
const axios = require('axios'); // Biblioteca para chamadas HTTP

const MAIN_DIALOG = 'mainDialog';
const ORDER_ID_PROMPT = 'orderIdPrompt';

class PedidoConsulta extends ComponentDialog {
    constructor(dialogId) {
        super(dialogId);

        // Adiciona um TextPrompt para capturar o ID do pedido
        this.addDialog(new TextPrompt(ORDER_ID_PROMPT));

        // Configura o WaterfallDialog com os passos do fluxo
        this.addDialog(new WaterfallDialog(MAIN_DIALOG, [
            this.requestOrderIdStep.bind(this),
            this.retrieveOrderDetailsStep.bind(this),
            this.concludeDialogStep.bind(this)
        ]));

        this.initialDialogId = MAIN_DIALOG;
    }

    /**
     * Etapa 1: Solicita ao usuário o ID do pedido
     */
    async requestOrderIdStep(stepContext) {
        return await stepContext.prompt(ORDER_ID_PROMPT, {
            prompt: 'Informe o ID do pedido para que eu possa localizar as informações desejadas:'
        });
    }

    /**
     * Etapa 2: Processa o ID e faz a chamada à API
     */
    async retrieveOrderDetailsStep(stepContext) {
        const orderId = stepContext.result;

        try {
            // Realiza a requisição para buscar as informações do pedido
            const apiResponse = await axios.get(`https://apipedidosextratos.azurewebsites.net/ecommerce/extrato/buscar-por-id/${orderId}`);
            const orderDetails = apiResponse.data;

            if (orderDetails) {
                let responseMessage = `**Detalhes do Pedido:**\n\n`;
                responseMessage += `**CPF do Cliente:** ${orderDetails.cpf}\n`;
                responseMessage += `**ID do Pedido:** ${orderDetails.id}\n`;
                responseMessage += `**Produto:** ${orderDetails.productName}\n`;
                responseMessage += `**Valor Total:** R$ ${orderDetails.price.toFixed(2)}\n`;
                responseMessage += `**Data de Compra:** ${new Date(orderDetails.dataCompra).toLocaleString('pt-BR')}\n`;

                await stepContext.context.sendActivity(responseMessage);
            } else {
                await stepContext.context.sendActivity('Não encontramos um pedido correspondente ao ID informado.');
            }
        } catch (error) {
            console.error('Erro ao buscar informações do pedido:', error);
            await stepContext.context.sendActivity('Ocorreu um erro ao consultar os dados. Por favor, tente novamente mais tarde.');
        }

        return await stepContext.next();
    }

    /**
     * Etapa 3: Finaliza o diálogo
     */
    async concludeDialogStep(stepContext) {
        await stepContext.context.sendActivity('Caso precise de algo mais, estarei por aqui para ajudar!');
        return await stepContext.endDialog();
    }
}

module.exports.PedidoConsulta = PedidoConsulta;
