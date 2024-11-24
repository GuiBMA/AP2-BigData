const path = require('path');
const dotenv = require('dotenv');
const restify = require('restify');

// Importação de serviços necessários
const {
    CloudAdapter,
    ConfigurationServiceClientCredentialFactory,
    createBotFrameworkAuthenticationFromConfiguration
} = require('botbuilder');

// Importação dos diálogos e gerenciamento de estado
const { ConversationState, MemoryStorage } = require('botbuilder');
const { MainDialog } = require('./dialogs/mainDialog');

// Carregando variáveis de ambiente
const ENV_FILE_PATH = path.join(__dirname, '.env');
dotenv.config({ path: ENV_FILE_PATH });

// Configuração do servidor HTTP
const server = restify.createServer();
server.use(restify.plugins.bodyParser());

server.listen(process.env.PORT || 3978, () => {
    console.log(`\nServidor rodando em ${server.url}`);
    console.log('\nAcesse o Bot Framework Emulator: https://aka.ms/botframework-emulator');
    console.log('\nAbra o Emulator e selecione "Open Bot" para interagir com o bot.');
});

// Configuração da autenticação do bot
const credentialFactory = new ConfigurationServiceClientCredentialFactory({
    MicrosoftAppId: process.env.MicrosoftAppId,
    MicrosoftAppPassword: process.env.MicrosoftAppPassword,
    MicrosoftAppType: process.env.MicrosoftAppType,
    MicrosoftAppTenantId: process.env.MicrosoftAppTenantId
});

const botAuth = createBotFrameworkAuthenticationFromConfiguration(null, credentialFactory);

// Configuração do adaptador
const botAdapter = new CloudAdapter(botAuth);

// Tratamento de erros
const errorHandler = async (context, error) => {
    console.error(`\n[Erro] Detalhes: ${error}`);
    await context.sendTraceActivity('Rastreamento de Erro', `${error}`, 'https://www.botframework.com/schemas/error', 'Erro');
    await context.sendActivity('Ocorreu um problema no bot. Por favor, verifique o código-fonte.');
    console.log(error);
};
botAdapter.onTurnError = errorHandler;

// Gerenciamento de estado e inicialização do diálogo principal
const storage = new MemoryStorage();
const convState = new ConversationState(storage);
const mainBotDialog = new MainDialog(convState);

// Configuração do bot
const bot = {
    execute: async (context) => {
        await mainBotDialog.run(context, convState.createProperty('dialogState'));
    }
};

// Endpoint para requisições de mensagens
server.post('/api/messages', async (req, res) => {
    await botAdapter.process(req, res, async (context) => {
        await bot.execute(context);
        await convState.saveChanges(context, false);
    });
});

// Configuração para requisições de streaming
server.on('upgrade', async (req, socket, head) => {
    const streamAdapter = new CloudAdapter(botAuth);
    streamAdapter.onTurnError = errorHandler;
    await streamAdapter.process(req, socket, head, (context) => bot.execute(context));
});
