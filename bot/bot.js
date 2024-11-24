const { ActivityHandler, MessageFactory } = require('botbuilder');

class RepeaterBot extends ActivityHandler {
    constructor() {
        super();

        // Lida com mensagens enviadas pelo usuário
        this.onMessage(async (context, next) => {
            const echoedMessage = `Repetindo: ${context.activity.text}`;
            await context.sendActivity(MessageFactory.text(echoedMessage, echoedMessage));
            
            // Garante a execução do próximo manipulador, se existir
            await next();
        });

        // Lida com novos membros adicionados à conversa
        this.onMembersAdded(async (context, next) => {
            const newMembers = context.activity.membersAdded;
            const greetingMessage = 'Olá e seja bem-vindo!';
            
            for (const member of newMembers) {
                if (member.id !== context.activity.recipient.id) {
                    await context.sendActivity(MessageFactory.text(greetingMessage, greetingMessage));
                }
            }

            // Garante a execução do próximo manipulador, se existir
            await next();
        });
    }
}

module.exports.RepeaterBot = RepeaterBot;
