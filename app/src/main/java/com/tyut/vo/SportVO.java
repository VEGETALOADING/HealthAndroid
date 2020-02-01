package com.tyut.vo;

public class SportVO {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sport.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sport.name
     *
     * @mbg.generated
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sport.quantity
     *
     * @mbg.generated
     */
    private Integer quantity;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sport.unit
     *
     * @mbg.generated
     */
    private String unit;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column sport.calories
     *
     * @mbg.generated
     */
    private Integer calories;

    private String pic;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sport.id
     *
     * @return the value of sport.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sport.id
     *
     * @param id the value for sport.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sport.name
     *
     * @return the value of sport.name
     *
     * @mbg.generated
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sport.name
     *
     * @param name the value for sport.name
     *
     * @mbg.generated
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sport.quantity
     *
     * @return the value of sport.quantity
     *
     * @mbg.generated
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sport.quantity
     *
     * @param quantity the value for sport.quantity
     *
     * @mbg.generated
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sport.unit
     *
     * @return the value of sport.unit
     *
     * @mbg.generated
     */
    public String getUnit() {
        return unit;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sport.unit
     *
     * @param unit the value for sport.unit
     *
     * @mbg.generated
     */
    public void setUnit(String unit) {
        this.unit = unit == null ? null : unit.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column sport.calories
     *
     * @return the value of sport.calories
     *
     * @mbg.generated
     */
    public Integer getCalories() {
        return calories;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column sport.calories
     *
     * @param calories the value for sport.calories
     *
     * @mbg.generated
     */
    public void setCalories(Integer calories) {
        this.calories = calories;
    }
}