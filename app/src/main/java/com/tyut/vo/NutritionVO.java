package com.tyut.vo;

public class NutritionVO {

    private Integer hot;
    private Integer carb;
    private Integer protein;
    private Integer fat;
    private Float standardWeight;

    public Float getStandardWeight() {
        return standardWeight;
    }

    public void setStandardWeight(Float standardWeight) {
        this.standardWeight = standardWeight;
    }

    public NutritionVO() {
    }

    public NutritionVO(Integer hot, Integer carb, Integer protein, Integer fat, Float standardWeight) {
        this.hot = hot;
        this.carb = carb;
        this.protein = protein;
        this.fat = fat;
        this.standardWeight = standardWeight;
    }

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }

    public Integer getCarb() {
        return carb;
    }

    public void setCarb(Integer carb) {
        this.carb = carb;
    }

    public Integer getProtein() {
        return protein;
    }

    public void setProtein(Integer protein) {
        this.protein = protein;
    }

    public Integer getFat() {
        return fat;
    }

    public void setFat(Integer fat) {
        this.fat = fat;
    }
}
