const { ActivityHandler } = require('botbuilder');
const { ProductDialog } = require('./dialog/produtoDialog');

class DialogBot extends ActivityHandler {

    constructor(conversationState, userState, dialog) {
        super();
        
        // Validações de inicialização
        if (!conversationState) throw new Error('[DialogBot]: Missing parameter. conversationState is required');
        if (!userState) throw new Error('[DialogBot]: Missing parameter. userState is required');
        if (!dialog) throw new Error('[DialogBot]: Missing parameter. dialog is required');

        this.conversationState = conversationState;
        this.userState = userState;
        this.dialog = dialog;
        this.dialogState = this.conversationState.createProperty('DialogState');

        // Listener para mensagens
        this.onMessage(async (context, next) => {
            console.log('Running dialog with Message Activity.');

            try {
                // Verifica e executa o diálogo
                console.log('Dialog instance:', this.dialog);
                await this.dialog.run(context, this.dialogState);
            } catch (error) {
                console.error(`[onMessage] Error: ${error.message}`);
                console.error(error.stack);
                await context.sendActivity('The bot encountered an error or bug. Please try again later.');
            }

            await next();
        });

        // Listener para eventos de membros adicionados
        this.onMembersAdded(async (context, next) => {
            const membersAdded = context.activity.membersAdded;
            const welcomeText = 'Como posso ajudar?';

            for (let member of membersAdded) {
                if (member.id !== context.activity.recipient.id) {
                    await context.sendActivity(welcomeText);
                }
            }

            await next();
        });
    }

    /**
     * Override do método run do ActivityHandler
     */
    async run(context) {
        await super.run(context);

        // Salva mudanças de estado
        try {
            await this.conversationState.saveChanges(context, false);
            await this.userState.saveChanges(context, false);
        } catch (error) {
            console.error(`[run] Error saving state changes: ${error.message}`);
        }
    }
}

module.exports.DialogBot = DialogBot;
