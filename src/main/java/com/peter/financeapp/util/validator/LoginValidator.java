package com.peter.financeapp.util.validator;

import com.peter.financeapp.util.ValidationResult;
import com.peter.financeapp.util.ValidationUtils;
import com.peter.financeapp.util.Validator;
import com.peter.financeapp.util.validationForm.CategoryForm;
import com.peter.financeapp.util.validationForm.LoginForm;

public class LoginValidator implements Validator<LoginForm> {
    @Override
    public ValidationResult validate(LoginForm form){
        ValidationResult result = new ValidationResult();

        ValidationUtils.requireNotBlank(form.getUsername(), "username",result,"Username name is required");
        ValidationUtils.requireNotBlank(form.getPassword(),"password",result,"Password is required");

        return result;
    }
}
