/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package rs.ac.bg.fon.e_learning_platforma_njt.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 *
 * @author mikir
 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER}) // gde se može koristiti
@Retention(RetentionPolicy.RUNTIME)                 // koliko traje
@Constraint(validatedBy = OneOfLongValidator.class) // klasa koja proverava
public @interface OneOfLong {
    long[] value(); // skup dozvoljenih vrednosti

    String message() default "Value not allowed.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
