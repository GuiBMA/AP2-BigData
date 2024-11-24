const axios = require('axios').default;

class Extrato {
    constructor() {
        this.urlApi = process.env.EXTRATO_URL_API; // URL base da API
        this.apimKey = process.env.APIM_KEY;      // Chave de assinatura da APIM
    }
    /**
     * Obtém o ID do cartão a partir do número do cartão e CPF.
     * @param {string} numeroCartao - Número do cartão.
     */
    async getIdCartao(numeroCartao) {
        const headers = {
            'ocp-apim-subscription-key': this.apimKey
        };

        try {
            const response = await axios.get(`${this.urlApi}/buscar-id-cartao/ ${ numeroCartao }`, { headers });
            return response.data.id; // Supõe que o retorno tenha um campo "id"
        } catch (error) {
            console.error('Erro ao buscar ID do cartão:', error.message);
            throw new Error('Não foi possível encontrar o ID do cartão.');
        }
    }

    /**
     * Obtém o extrato do cartão a partir do ID.
     * @param {int} idCartao - ID do cartão.
     */
    async getExtrato(idCartao) {
        const headers = {
            'ocp-apim-subscription-key': this.apimKey
        };

        try {
            const response = await axios.get(`${this.urlApi}/extrato-cartao/${ idCartao }`, { headers });
            return response.data; // Retorna os dados do extrato
        } catch (error) {
            console.error('Erro ao buscar extrato:', error.message);
            throw new Error('Não foi possível obter o extrato do cartão.');
        }
    }
}

module.exports.Extrato = Extrato;
