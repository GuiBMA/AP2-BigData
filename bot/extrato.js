const axios = require('axios').default;

class Extrato {
    urlApi = process.env.EXTRATO_URL_API; // URL base da API
    apimKey = process.env.APIM_KEY; // Chave de assinatura da APIM

    /**
     * Obtém o ID do cartão a partir do número do cartão e CPF.
     * @param {string} cpf - CPF do usuário.
     * @param {string} numeroCartao - Número do cartão.
     */
    async getIdCartao(cpf, numeroCartao) {
        const headers = {
            'ocp-apim-subscription-key': this.apimKey
        };

        try {
            const response = await axios.get(`${this.urlApi}/buscar-id-cartao`, {
                headers,
                params: { cpf, numeroCartao } // Envia CPF e número do cartão como query params
            });
            return response.data.id; // Supõe que o retorno tenha um campo "id"
        } catch (error) {
            console.error('Erro ao buscar ID do cartão:', error.message);
            throw new Error('Não foi possível encontrar o ID do cartão.');
        }
    }

    /**
     * Obtém o extrato do cartão a partir do ID.
     * @param {string} idCartao - ID do cartão.
     */
    async getExtrato(idCartao) {
        const headers = {
            'ocp-apim-subscription-key': this.apimKey
        };

        try {
            const response = await axios.get(`${this.urlApi}/transacao/transacao/extrato-cartao/${idCartao}`, {
                headers
            });
            return response.data; // Retorna os dados do extrato
        } catch (error) {
            console.error('Erro ao buscar extrato:', error.message);
            throw new Error('Não foi possível obter o extrato do cartão.');
        }
    }
}

module.exports.Extrato = Extrato;
