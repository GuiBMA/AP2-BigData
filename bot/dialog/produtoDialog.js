const { MessageFactory } = require('botbuilder');
const {
    AttachmentPrompt,
    ChoiceFactory,
    ChoicePrompt,
    ComponentDialog,
    ConfirmPrompt,
    DialogSet,
    DialogTurnStatus,
    NumberPrompt,
    TextPrompt,
    WaterfallDialog
} = require('botbuilder-dialogs');
const { Channels } = require('botbuilder-core');
const { ProdutoProfile } = require('../produtoProfile');
const {Produto} = require("../produto");
const { Extrato } = require('../extrato');

const NAME_PROMPT = 'NAME_PROMPT';
const CARTAO_NUMBER_PROMPT = 'CARTAO_NUMBER_PROMPT';
const CHOICE_PROMPT = 'CHOICE_PROMPT';
const PRODUCT_PROFILE = 'PRODUCT_PROFILE';
const WATERFALL_DIALOG = 'WATERFALL_DIALOG';


class ProductDialog extends ComponentDialog {
    constructor(userState) {
        super('productDialog');

        this.productProfile = userState.createProperty(PRODUCT_PROFILE);

        this.addDialog(new TextPrompt(NAME_PROMPT));
        this.addDialog(new TextPrompt(CARTAO_NUMBER_PROMPT));
        this.addDialog(new ChoicePrompt(CHOICE_PROMPT));

        this.addDialog(new WaterfallDialog(WATERFALL_DIALOG, [
            this.menuStep.bind(this),
            this.productNameStep.bind(this),
            this.cartaoNumberStep.bind(this),
            this.confirmStep.bind(this)
        ]));

        this.initialDialogId = WATERFALL_DIALOG;
    }

    /**
     * The run method handles the incoming activity (in the form of a TurnContext) and passes it through the dialog system.
     * If no dialog is active, it will start the default dialog.
     * @param {*} turnContext
     * @param {*} accessor
     */
    async run(turnContext, accessor) {
        const dialogSet = new DialogSet(accessor);
        dialogSet.add(this);

        const dialogContext = await dialogSet.createContext(turnContext);
        const results = await dialogContext.continueDialog();
        if (results.status === DialogTurnStatus.empty) {
            await dialogContext.beginDialog(this.id);
        }
    }

    async menuStep(step) {
        return await step.prompt(CHOICE_PROMPT, {
            prompt: 'Escolha a opção desejada',
            choices: ChoiceFactory.toChoices(['Consultar Pedidos', 'Consultar Produtos', 'Extrato de Compras'])        });
    }

    async productNameStep(step) {
        step.values.choice = step.result.value;

        switch(step.values.choice) {
            case "Consultar Pedidos":
            case "Extrato de Compras": {
                return await step.prompt(NAME_PROMPT, 'Digite o seu CPF');        
            }
            case "Consultar Produtos": {
                return await step.prompt(NAME_PROMPT, 'Digite o nome do produto');        
            }
        }
    }

    async cartaoNumberStep(step) {
        step.values.id = step.result;
        return await step.prompt(CARTAO_NUMBER_PROMPT, 'Digite o numero do cartão');        
    }

    async confirmStep(step) {
        switch (step.values.choice) {
            case "Consultar Pedidos":
            case "Extrato de Compras": {
                let id = step.values.id;
                let cardNumber = step.result;
                let extrato = new Extrato();
                let response = await extrato.getExtrato(id, cardNumber);
                let result = extrato.formatExtrato(response.data);
                let message = MessageFactory.text(result);
                await step.context.sendActivity(message);
                break;
            }
            case "Consultar Produtos": {
                let productName = step.values.id;
                let produto = new Produto();
                let response = await produto.getProduto(productName);
                let card = produto.createProductCard(response.data[0]);
                await step.context.sendActivity({ attachments: [card] });
                break
            }
        }
        return await step.endDialog();
    }

    // async confirmStep(step) {
    //     switch (step.values.choice) {
    //         case "Consultar Pedidos":
    //         case "Extrato de Compras": {
    //             const cpf = step.values.id; // O CPF digitado pelo usuário
    //             const cardNumber = step.result;
                
    //             let extrato = new Extrato();
    
    //             try {
    //                 // Obter o ID a partir do CPF
    //                 const idResponse = await extrato.getIdByCPF(cpf);
    //                 const userId = idResponse.data.id; // Supondo que a resposta tenha o campo "id"
    
    //                 // Obter o extrato usando o ID encontrado
    //                 const response = await extrato.getExtrato(userId, cardNumber);
    //                 const result = extrato.formatExtrato(response.data);
    
    //                 // Enviar a mensagem formatada ao usuário
    //                 const message = MessageFactory.text(result);
    //                 await step.context.sendActivity(message);
    //             } catch (error) {
    //                 console.error('Erro ao buscar ID ou extrato:', error);
    //                 await step.context.sendActivity('Houve um erro ao processar sua solicitação. Tente novamente mais tarde.');
    //             }
    //             break;
    //         }
    //         case "Consultar Produtos": {
    //             const productName = step.values.id;
    //             const produto = new Produto();
    //             const response = await produto.getProduto(productName);
    //             const card = produto.createProductCard(response.data[0]);
    //             await step.context.sendActivity({ attachments: [card] });
    //             break;
    //         }
    //     }
    
    //     return await step.endDialog();
    // }
    
}

module.exports.ProductDialog = ProductDialog;