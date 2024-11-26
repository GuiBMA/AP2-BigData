const { ComponentDialog, WaterfallDialog, TextPrompt } = require('botbuilder-dialogs');
const axios = require('axios'); // Importa o axios

const WATERFALL_DIALOG = 'waterfallDialog';
const ID_PROMPT = 'idPrompt';

class Pedido extends ComponentDialog {
    constructor(id) {
        super(id);

        // Adicionar o TextPrompt para solicitar o ID do pedido
        this.addDialog(new TextPrompt(ID_PROMPT));

        // Configurar o WaterfallDialog com os novos passos
        this.addDialog(new WaterfallDialog(WATERFALL_DIALOG, [
            this.promptForIdStep.bind(this),
            this.processIdStep.bind(this),
            this.finalStep.bind(this)
        ]));

        this.initialDialogId = WATERFALL_DIALOG;
    }

    // Passo 1: Solicita o ID do pedido ao usuário
    async promptForIdStep(stepContext) {
        return await stepContext.prompt(ID_PROMPT, {
            prompt: 'Digite o ID para mostrar as informações:'
        });
    }

    // Passo 2: Processa o ID e chama a API
    async processIdStep(stepContext) {
        const idProduto = stepContext.result;

        try {
            const response = await axios.get(`https://ecommerce-api-luis-cga2dqfsaqb4emhu.centralus-01.azurewebsites.net/ecommerce/orders/idProduto/${idProduto}`);
            const pedido = response.data;

            if (pedido) {
                let mensagem = `Aqui estão as informações do pedido:\n\n`;
                const campos = {
                    "Cpf do Responsável do Pedido:": "cpf",
                    "ID do Pedido:": "id",
                    "Produto:": "productName",
                    "Preço:": "price",
                    "Data da Compra:": "dataCompra"
                };

                Object.entries(campos).forEach(([label, key]) => {
                    const valor = pedido[key]; // Obtem o valor do pedido com base na chave
                    if (key === "price") {
                        const preco = parseFloat(valor); // Converte o valor para número
                        mensagem += `\n${label} R$ ${isNaN(preco) ? 'Valor indisponível' : preco.toFixed(2)}\n`;
                    } else if (key === "dataCompra") {
                        mensagem += `\n${label} ${valor ? new Date(valor).toLocaleString('pt-BR') : 'Data indisponível'}\n`;
                    } else {
                        mensagem += `\n${label} ${valor || 'Informação indisponível'}\n`;
                    }
                });

                await stepContext.context.sendActivity(mensagem);
            } else {
                await stepContext.context.sendActivity('Nenhum pedido encontrado.');
            }
        } catch (error) {
            console.error('Erro ao chamar a API:', error);
            await stepContext.context.sendActivity('Ocorreu um erro ao obter as informações do pedido.');
        }

        return await stepContext.next();
    }

    // Passo 3: Mensagem final
    async finalStep(stepContext) {
        await stepContext.context.sendActivity('Posso ajudar em mais alguma coisa?');
        return await stepContext.endDialog();
    }
}

module.exports.Pedido = Pedido;
