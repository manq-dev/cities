package pl.codest.cities.api;

import org.apache.commons.lang3.StringUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static pl.codest.cities.api.CityController.UpdateCityRequest;
import static pl.codest.cities.api.UpdateCityValidator.UpdateCityValidation;

public class UpdateCityValidator implements ConstraintValidator<UpdateCityValidation, UpdateCityRequest> {

    @Override
    public void initialize(UpdateCityValidation updateCity) {
    }

    @Override
    public boolean isValid(UpdateCityRequest object, ConstraintValidatorContext context) {
        if (object == null) {
            throw new NullPointerException("UpdateCityRequest cannot be null.");
        }
        return !StringUtils.isAnyBlank(object.name(), object.imageUrl());
    }

    @Constraint(validatedBy = UpdateCityValidator.class)
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UpdateCityValidation {
        String message() default "";
        Class<?>[] groups() default { };
        Class<? extends Payload>[] payload() default {};
    }
}
