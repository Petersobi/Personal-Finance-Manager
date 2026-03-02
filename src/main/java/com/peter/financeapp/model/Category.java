package com.peter.financeapp.model;

public class Category {
   private Long id;
   private Long userId;
   private String name;
   private CategoryType type;
   private boolean deleted;

   public Category (Long id,Long userId, String name,CategoryType type,boolean deleted){
       this.id = id; this.userId = userId; this.name = name; this.type = type;this.deleted = deleted;
   }

   public Category(Long userId, String name, CategoryType type){
       this.userId = userId; this.name = name ; this.type = type;this.deleted = false;
   }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public CategoryType getType() {
        return type;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
