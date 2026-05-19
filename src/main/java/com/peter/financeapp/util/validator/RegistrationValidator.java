package com.peter.financeapp.util.validator;

import com.peter.financeapp.util.ValidationResult;
import com.peter.financeapp.util.ValidationUtils;
import com.peter.financeapp.util.Validator;
import com.peter.financeapp.util.validationForm.CategoryForm;
import com.peter.financeapp.util.validationForm.RegistrationForm;

public class RegistrationValidator implements Validator<RegistrationForm> {
    @Override
    public ValidationResult validate(RegistrationForm form){
        ValidationResult result = new ValidationResult();

        ValidationUtils.requireNotBlank(form.getFirstname(), "firstname",result,"First name is required");
        ValidationUtils.requireNotBlank(form.getLastname(),"lastname",result,"Last name is required");
        ValidationUtils.requireNotBlank(form.getUsername(), "username",result,"username is required");
        ValidationUtils.requireNotBlank(form.getPassword(),"password",result,"password is required");


        return result;
    }

}
