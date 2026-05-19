package com.peter.financeapp.util.validator;

import com.peter.financeapp.util.ValidationResult;
import com.peter.financeapp.util.ValidationUtils;
import com.peter.financeapp.util.Validator;
import com.peter.financeapp.util.validationForm.CategoryForm;

public class CategoryValidator implements Validator<CategoryForm> {
    @Override
    public ValidationResult validate(CategoryForm form){
        ValidationResult result = new ValidationResult();

        ValidationUtils.requireNotBlank(form.getCategoryName(), "name",result,"Category name is required");
        ValidationUtils.requireNotBlank(form.getCategoryType(),"type",result,"Category type is required");

        return result;
    }
}
