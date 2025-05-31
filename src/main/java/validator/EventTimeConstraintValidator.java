package validator;

import java.time.LocalTime;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventTimeConstraintValidator implements ConstraintValidator<ValidEventTime, LocalTime> {

	private String minTime;
	
	@Override
	public void initialize(ValidEventTime constraintAnnotation) {
		this.minTime = constraintAnnotation.minTime();
	}
	
	@Override
	public boolean isValid(LocalTime value, ConstraintValidatorContext context) {
		
		LocalTime minLocalTime = LocalTime.parse(this.minTime);
		
		if(value == null) {
			return false;
		}
		
		return (value.equals(minLocalTime) || value.isAfter(minLocalTime));
	}

}
