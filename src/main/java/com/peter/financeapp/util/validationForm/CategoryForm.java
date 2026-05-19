package com.peter.financeapp.util.validationForm;

import com.peter.financeapp.model.CategoryType;

public class CategoryForm {
    private String categoryName;
    private String categoryType;

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
