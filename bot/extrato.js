const axios = require('axios').default;
const { CardFactory } = require('botbuilder');

const format = require('date-format');

class Extrato {

    urlApi = process.env.EXTRATO_URL_API;
    apimKey = process.env.APIM_KEY;

    async getIdByCPF(cpf) {
        const headers = {
            'ocp-apim-subscription-key': apimKey
        };
        return await axios.get(`${this.urlApi}/buscar-id?cpf=${cpf}`, { headers });
    }
    async getExtrato(cpfUser, numeroCartao) {
        const headers = {
            'ocp-apim-subscription-key': apimKey
        };
        return await axios.get(`${this.urlApi}/${getIdByCPF(cpfUser)}?numeroCartao=${numeroCartao}`);
    }

    formatExtrato(response) {
        let table = `| **DATA COMPRA** | **DESCRICAO** | **VALOR** |\n\n
        `;
        response.forEach(element => {
            table += `\n\n| **${format("dd/MM/yyyy", new Date(element.dataTransacao))}** | **${element.comerciante}** | **R$ ${element.valor}** |\n\n`
        });

        return table;
    }
}

module.exports.Extrato = Extrato;