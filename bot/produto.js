const axios = require('axios').default;
const { CardFactory } = require('botbuilder');

class Produto {
    constructor() {
        this.urlApi = process.env.EXTRATO_URL_API; // URL base da API
        this.apimKey = process.env.APIM_KEY;      // Chave de assinatura da APIM
    }

    async getProduto(productName) {
        const headers = {
            'ocp-apim-subscription-key': this.apimKey
        };
        return await axios.get(`${this.urlApi}?productName=${productName}`, { headers });
    }
    createProductCard(response) {
        return CardFactory.thumbnailCard(
            response.productName,
            [{ url: response.urlFoto }],
            [],
            {
                subtitle: `Preço do produto: ${response.price}`,
                text: response.productDescription
            }
        );
    }
}

module.exports.Produto = Produto;