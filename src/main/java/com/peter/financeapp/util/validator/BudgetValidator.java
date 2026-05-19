package com.peter.financeapp.util.validator;

import com.peter.financeapp.util.ValidationResult;
import com.peter.financeapp.util.ValidationUtils;
import com.peter.financeapp.util.Validator;
import com.peter.financeapp.util.validationForm.BudgetForm;

import java.math.BigDecimal;

public class BudgetValidator implements Validator<BudgetForm> {
    @Override
    public ValidationResult validate(BudgetForm form) {
        ValidationResult result = new ValidationResult();

        ValidationUtils.requireNotNull(form.getCategoryId(),"category",result,"Category is required");
        ValidationUtils.requireNotNull(form.getMonth(),"date",result,"Date is required");
        ValidationUtils.requireNotBlank(form.getAmount(),"amount",result,"Amount is required");

        if (form.getAmount() != null && !form.getAmount().isBlank()){

            try { BigDecimal amount = new BigDecimal(form.getAmount());
                if(amount.compareTo(BigDecimal.ZERO)<=0){

                    result.addError("amount","Amount must be greater than zero");

                }
            } catch (Exception e) {
                result.addError("amount","Invalid Amount");
            }}


            return result;
    }
}
