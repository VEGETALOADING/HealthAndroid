package com.tyut.vo;

import java.math.BigDecimal;

public class FoodVO {

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column food.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column food.name
     *
     * @mbg.generated
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column food.quantity
     *
     * @mbg.generated
     */
    private Integer quantity;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column food.unit
     *
     * @mbg.generated
     */
    private String unit;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column food.calories
     *
     * @mbg.generated
     */
    private Integer calories;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column food.pic
     *
     * @mbg.generated
     */
    private String pic;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column food.carbs
     *
     * @mbg.generated
     */
    private BigDecimal carbs;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column food.protein
     *
     * @mbg.generated
     */
    private BigDecimal protein;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column food.fat
     *
     * @mbg.generated
     */
    private BigDecimal fat;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column food.id
     *
     * @return the value of food.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column food.id
     *
     * @param id the value for food.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column food.name
     *
     * @return the value of food.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column food.name
     *
     * @param name the value for food.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column food.quantity
     *
     * @return the value of food.quantity
     *
     * @mbg.generated
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column food.quantity
     *
     * @param quantity the value for food.quantity
     *
     * @mbg.generated
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column food.unit
     *
     * @return the value of food.unit
     *
     * @mbg.generated
     */
    public String getUnit() {
        return unit;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column food.unit
     *
     * @param unit the value for food.unit
     *
     * @mbg.generated
     */
    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column food.calories
     *
     * @return the value of food.calories
     *
     * @mbg.generated
     */
    public Integer getCalories() {
        return calories;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column food.calories
     *
     * @param calories the value for food.calories
     *
     * @mbg.generated
     */
    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column food.pic
     *
     * @return the value of food.pic
     *
     * @mbg.generated
     */
    public String getPic() {
        return pic;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column food.pic
     *
     * @param pic the value for food.pic
     *
     * @mbg.generated
     */
    public void setPic(String pic) {
        this.pic = pic == null ? null : pic.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column food.carbs
     *
     * @return the value of food.carbs
     *
     * @mbg.generated
     */
    public BigDecimal getCarbs() {
        return carbs;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column food.carbs
     *
     * @param carbs the value for food.carbs
     *
     * @mbg.generated
     */
    public void setCarbs(BigDecimal carbs) {
        this.carbs = carbs;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column food.protein
     *
     * @return the value of food.protein
     *
     * @mbg.generated
     */
    public BigDecimal getProtein() {
        return protein;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column food.protein
     *
     * @param protein the value for food.protein
     *
     * @mbg.generated
     */
    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column food.fat
     *
     * @return the value of food.fat
     *
     * @mbg.generated
     */
    public BigDecimal getFat() {
        return fat;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column food.fat
     *
     * @param fat the value for food.fat
     *
     * @mbg.generated
     */
    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }

}
