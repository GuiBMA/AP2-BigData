const { ComponentDialog, WaterfallDialog } = require('botbuilder-dialogs');
const axios = require('axios'); // Importa o axios

const WATERFALL_DIALOG = 'waterfallDialog';

class Produto extends ComponentDialog {
    constructor(id) {
        super(id);

        // Configurar o WaterfallDialog com os passos
        this.addDialog(new WaterfallDialog(WATERFALL_DIALOG, [
            this.fetchAllProductsStep.bind(this),
            this.finalStep.bind(this)
        ]));

        this.initialDialogId = WATERFALL_DIALOG;
    }

    // Passo 1: Busca todos os produtos da API
    async fetchAllProductsStep(stepContext) {
        try {
            // Faz a chamada à API para buscar todos os produtos
            const response = await axios.get(`https://colocar_api/ecommerce/products/produtos`);

            const produtos = response.data;

            if (produtos && produtos.length > 0) {
                let mensagem = 'Lista de Produtos Disponíveis:\n\n';
                const campos = {
                    "ID do Produto:": "productid",
                    "Categoria:": "productCategory",
                    "Nome do Produto:": "productName",
                    "Preço:": "price"
                };
            
                produtos.forEach(produto => {
                    Object.entries(campos).forEach(([label, key]) => {
                        if (key === "price") {
                            mensagem += `\n${label} R$ ${produto[key].toFixed(2)}\n`;
                        } else {
                            mensagem += `\n${label} ${produto[key]}\n`;
                        }
                    });
                    mensagem += `---------------------------------\n`;
                });
            
                await stepContext.context.sendActivity(mensagem);
            } else {
                await stepContext.context.sendActivity('Nenhum produto disponível.');
            }
            
        } catch (error) {
            console.error('Erro ao chamar a API:', error);
            await stepContext.context.sendActivity('Ocorreu um erro ao obter a lista de produtos.');
        }

        return await stepContext.next();
    }

    async finalStep(stepContext) {
        await stepContext.context.sendActivity('Posso ajudar em mais alguma coisa?');
        return await stepContext.endDialog();
    }
}

module.exports.Produto = Produto;

