/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rs.ac.bg.fon.e_learning_platforma_njt.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 *
 * @author mikir
 */
public class OneOfLongValidator implements ConstraintValidator<OneOfLong, Long> {

    private Set<Long> allowed;

    @Override
    public void initialize(OneOfLong annotation) {
        allowed = LongStream.of(annotation.value())
                .boxed()
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // ako je null, neka drugi validatori (npr. @NotNull) reaguju
        }
        return allowed.contains(value);
    }
}
