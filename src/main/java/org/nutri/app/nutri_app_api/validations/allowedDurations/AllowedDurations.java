package org.nutri.app.nutri_app_api.validations.allowedDurations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD }) // Esta anotação só pode ser usada em campos
@Retention(RetentionPolicy.RUNTIME) // A anotação precisa estar disponível em tempo de execução
@Constraint(validatedBy = AllowedDurationsValidator.class) // Aponta para a classe que contém a lógica
public @interface AllowedDurations {

    // Mensagem de erro padrão que será usada se a validação falhar
    String message() default "Duration value time must be 15, 30, 45 or 60";

    // Estes dois métodos são obrigatórios pela especificação de validação
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

