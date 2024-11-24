const { ComponentDialog, WaterfallDialog, TextPrompt, ChoicePrompt, ChoiceFactory, DialogSet, DialogTurnStatus } = require('botbuilder-dialogs');
const { consultaExtrato } = require('./consultaExtrato');
const { consultaPedido } = require('./consultaPedido');
const { consultaProduto } = require('./consultaProduto');

const WATERFALL_DIALOG = 'waterfallDialog';
const CHOICE_PROMPT = 'choicePrompt';
let isWelcomeMessageShown = false;

class MainDialog extends ComponentDialog {
    constructor(id, userState) {
        super(id);

        this.userState = userState;

        // Adiciona os diálogos necessários
        this.addDialog(new ChoicePrompt(CHOICE_PROMPT));
        this.addDialog(new consultaPedido('consultaPedido'));
        this.addDialog(new consultaExtrato('consultaExtrato'));
        this.addDialog(new consultaProduto('consultaProduto'));

        // Define a estrutura do diálogo principal
        this.addDialog(new WaterfallDialog(WATERFALL_DIALOG, [
            this.promptUserChoice.bind(this),
            this.handleUserChoice.bind(this)
        ]));

        this.initialDialogId = WATERFALL_DIALOG;
    }

    /**
     * Método principal para executar o diálogo.
     * @param {*} turnContext
     * @param {*} accessor
     */
    async run(turnContext, accessor) {
        const dialogSet = new DialogSet(accessor);
        dialogSet.add(this);

        // Envia uma mensagem de boas-vindas se ainda não foi mostrada
        if (!isWelcomeMessageShown) {
            isWelcomeMessageShown = true;
            await turnContext.sendActivity(
                'Olá! Sou um assistente virtual e estou aqui para ajudar. Escolha uma das opções disponíveis e eu farei o possível para atendê-lo.'
            );
        }

        const dialogContext = await dialogSet.createContext(turnContext);
        const results = await dialogContext.continueDialog();

        if (results.status === DialogTurnStatus.empty) {
            await dialogContext.beginDialog(this.id);
        }
    }

    /**
     * Pergunta ao usuário qual opção ele deseja.
     * @param {WaterfallStepContext} stepContext
     */
    async promptUserChoice(stepContext) {
        const options = {
            prompt: 'Por favor, selecione uma das opções abaixo:',
            retryPrompt: 'Desculpe, não entendi. Tente escolher novamente:',
            choices: this.getAvailableChoices()
        };

        return await stepContext.prompt(CHOICE_PROMPT, options);
    }

    /**
     * Processa a escolha feita pelo usuário.
     * @param {WaterfallStepContext} stepContext
     */
    async handleUserChoice(stepContext) {
        const userChoice = stepContext.result.value;

        switch (userChoice) {
            case 'Consulta Pedidos':
                return await stepContext.beginDialog('consultaPedido');
            case 'Consulta Extrato':
                return await stepContext.beginDialog('consultaExtrato');
            case 'Consulta Produto':
                return await stepContext.beginDialog('consultaProduto');
            default:
                await stepContext.context.sendActivity('Opção inválida. Por favor, tente novamente.');
        }

        return await stepContext.endDialog();
    }

    /**
     * Configura as opções disponíveis para o ChoicePrompt.
     */
    getAvailableChoices() {
        return [
            { value: 'Consulta Pedidos', synonyms: ['1', 'pedido'] },
            { value: 'Consulta Extrato', synonyms: ['2', 'extrato'] },
            { value: 'Consulta Produto', synonyms: ['3', 'produto'] }
        ];
    }
}

module.exports.MainDialog = MainDialog;
