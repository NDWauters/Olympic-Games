package validator;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventDateConstraintValidator implements ConstraintValidator<ValidEventDate, LocalDate> {

	private String minDate;
	private String maxDate;
	
	@Override
	public void initialize(ValidEventDate constraintAnnotation) 
	{
		this.minDate = constraintAnnotation.minDate();
		this.maxDate = constraintAnnotation.maxDate();
	}
	
	@Override
	public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
		
		if(value == null) {
			return false;
		}
		
		LocalDate minLocalDate = LocalDate.parse(this.minDate);
		LocalDate maxLocalDate = LocalDate.parse(this.maxDate);
		
		return ((value.equals(minLocalDate) || value.isAfter(minLocalDate)) && (value.equals(maxLocalDate) || value.isBefore(maxLocalDate)));
	}

}
