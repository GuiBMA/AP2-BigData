package br.edu.ibmec.cartao_credito.exception;

public class TransacaoException extends RuntimeException{
    public TransacaoException(String message){
        super(message);
    }
}
