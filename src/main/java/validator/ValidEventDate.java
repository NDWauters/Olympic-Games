package validator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = EventDateConstraintValidator.class)
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
public @interface ValidEventDate {
	String message() default "{outOfRange.game.date}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	String minDate();
	String maxDate();
}
