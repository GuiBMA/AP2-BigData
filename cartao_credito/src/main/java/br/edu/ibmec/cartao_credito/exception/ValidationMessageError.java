package br.edu.ibmec.cartao_credito.exception;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationMessageError {
    private String message = "Sua requisição possui erros. Por favor, verifique e tente novamente.";
    private List<ValidationError> errors = new ArrayList<>();
}
