const { MessageFactory } = require('botbuilder');
const {
    ChoiceFactory,
    ChoicePrompt,
    ComponentDialog,
    TextPrompt,
    WaterfallDialog,
    DialogSet,
    DialogTurnStatus
} = require('botbuilder-dialogs');
const { Produto } = require("../produto");
const { Extrato } = require('../extrato');

const NAME_PROMPT = 'NAME_PROMPT';
const CARTAO_NUMBER_PROMPT = 'CARTAO_NUMBER_PROMPT';
const CHOICE_PROMPT = 'CHOICE_PROMPT';
const WATERFALL_DIALOG = 'WATERFALL_DIALOG';

class ProductDialog extends ComponentDialog {
    constructor(userState) {
        super('productDialog');

        // Prompts necessários
        this.addDialog(new TextPrompt(NAME_PROMPT));
        this.addDialog(new TextPrompt(CARTAO_NUMBER_PROMPT));
        this.addDialog(new ChoicePrompt(CHOICE_PROMPT));

        // Configuração do fluxo
        this.addDialog(new WaterfallDialog(WATERFALL_DIALOG, [
            this.menuStep.bind(this),
            this.productNameStep.bind(this),
            this.optionalCartaoNumberStep.bind(this),
            this.confirmStep.bind(this),
            this.repeatOrEndStep.bind(this)
        ]));

        this.initialDialogId = WATERFALL_DIALOG;
    }

    async menuStep(step) {
        return await step.prompt(CHOICE_PROMPT, {
            prompt: 'Escolha a opção desejada:',
            choices: ChoiceFactory.toChoices(['Consultar Pedidos', 'Consultar Produtos', 'Extrato de Compras'])
        });
    }

    async productNameStep(step) {
        step.values.choice = step.result.value;

        if (step.values.choice === "Consultar Produtos") {
            return await step.prompt(NAME_PROMPT, 'Digite o nome do produto:');
        }

        return await step.prompt(NAME_PROMPT, 'Digite o seu CPF:');
    }

    async optionalCartaoNumberStep(step) {
        step.values.id = step.result;

        if (step.values.choice === "Extrato de Compras") {
            return await step.prompt(CARTAO_NUMBER_PROMPT, 'Digite o número do cartão:');
        }

        return step.next();
    }

    async confirmStep(step) {
        try {
            switch (step.values.choice) {
                case "Consultar Produtos": {
                    const productName = step.values.id;
                    const produto = new Produto();
                    const response = await produto.getProduto(productName);
                    const card = produto.createProductCard(response.data[0]);
                    await step.context.sendActivity({ attachments: [card] });
                    break;
                }

                case "Consultar Pedidos":
                case "Extrato de Compras": {
                    const id = step.values.id;
                    const cardNumber = step.values.choice === "Extrato de Compras" ? step.result : null;
                    const extrato = new Extrato();
                    const response = await extrato.getExtrato(id, cardNumber);
                    const result = extrato.formatExtrato(response.data);
                    await step.context.sendActivity(MessageFactory.text(result));
                    break;
                }
            }
        } catch (error) {
            console.error('Erro no processamento:', error);
            await step.context.sendActivity('Ocorreu um erro. Por favor, tente novamente.');
        }

        return step.next();
    }

    async repeatOrEndStep(step) {
        return await step.prompt(CHOICE_PROMPT, {
            prompt: 'Posso ajudar em mais alguma coisa?',
            choices: ChoiceFactory.toChoices(['Sim', 'Não'])
        });
    }

    async finalStep(step) {
        if (step.result.value === 'Sim') {
            return await step.replaceDialog(this.initialDialogId);
        }

        await step.context.sendActivity('Obrigado! Até mais.');
        return await step.endDialog();
    }

    async run(turnContext, accessor) {
        const dialogSet = new DialogSet(accessor);
        dialogSet.add(this);

        const dialogContext = await dialogSet.createContext(turnContext);
        const results = await dialogContext.continueDialog();

        if (results.status === DialogTurnStatus.empty) {
            await dialogContext.beginDialog(this.id);
        }
    }
}

module.exports.ProductDialog = ProductDialog;