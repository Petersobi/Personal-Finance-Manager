package com.peter.financeapp.util.validationForm;

import java.time.LocalDate;

public class TransactionForm {
    private String amount;
    private Long categoryID;
    private String description;
    private LocalDate date;

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCategoryID(Long categoryID) {
        this.categoryID = categoryID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public Long getCategoryID() {
        return categoryID;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }
}
