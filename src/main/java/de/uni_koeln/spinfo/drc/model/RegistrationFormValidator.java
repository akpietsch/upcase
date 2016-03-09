package de.uni_koeln.spinfo.drc.model;

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RegistrationFormValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return RegistrationForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "NotBlank.registrationForm.firstName");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "NotBlank.registrationForm.lastName");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotBlank.registrationForm.email");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotBlank.registrationForm.password");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "NotBlank.registrationForm.confirmPassword");

		RegistrationForm form = (RegistrationForm) target;
		
		EmailValidator emailValidator = new EmailValidator();
		if(!emailValidator.isValid(form.getEmail(), null)) {
			errors.rejectValue("email", "Invalid.registrationForm.email");
		}
		if (!form.getConfirmPassword().equals(form.getPassword())) {
			errors.rejectValue("confirmPassword", "NotEqual.registrationForm.confirmPassword");
		}
	}

}
