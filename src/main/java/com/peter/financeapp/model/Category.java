package com.peter.financeapp.model;

public class Category {
   private long id;
   private long userId;
   private String name;
   private CategoryType type;
   private boolean deleted;

   public Category (long id,long userId, String name,CategoryType type,boolean deleted){
       this.id = id; this.userId = userId; this.name = name; this.type = type;this.deleted = deleted;
   }

   public Category(long userId, String name, CategoryType type){
       this.userId = userId; this.name = name ; this.type = type;this.deleted = false;
   }

    public long getId() {
        return id;
    }

    public long getUserId() {
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

    public void setId(long id) {
        this.id = id;
    }
}
