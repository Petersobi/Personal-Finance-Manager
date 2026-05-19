package com.peter.financeapp.util.validator;

import com.peter.financeapp.util.ValidationResult;
import com.peter.financeapp.util.ValidationUtils;
import com.peter.financeapp.util.Validator;
import com.peter.financeapp.util.validationForm.TransactionForm;

import java.math.BigDecimal;

public class TransactionValidator implements Validator<TransactionForm> {
    @Override
    public ValidationResult validate(TransactionForm form) {
        ValidationResult result = new ValidationResult();

        ValidationUtils.requireNotBlank(form.getAmount(),"amount",result,"Amount is required");
        ValidationUtils.requireNotNull(form.getCategoryID(),"category",result,"Category is required");
        ValidationUtils.requireNotNull(form.getDate(),"date",result,"Date is required");

        if (form.getAmount() != null && !form.getAmount().isBlank()){

                try { BigDecimal amount = new BigDecimal(form.getAmount());
                    if(amount.compareTo(BigDecimal.ZERO)<=0){

                result.addError("amount","Amount must be greater than zero");

            }
        } catch (Exception e) {
                    result.addError("amount","Invalid Amount");
                }


    }return result;
} }
