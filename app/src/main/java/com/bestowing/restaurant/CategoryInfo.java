package com.bestowing.restaurant;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoryInfo implements Serializable {
    private String categoryTitle;
    private int categoryImage;
    private ArrayList<String> storeList;

    public CategoryInfo(String categoryTitle, int categoryImage, ArrayList<String> storeList) {
        this.categoryTitle = categoryTitle;
        this.categoryImage = categoryImage;
        this.storeList = storeList;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public int getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(int categoryImage) {
        this.categoryImage = categoryImage;
    }

    public ArrayList<String> getStoreList() {
        return storeList;
    }

    public void setStoreList(ArrayList<String> storeList) {
        this.storeList = storeList;
    }
}
