package com.tyut.vo;

import java.util.Date;

public class Myfood {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column myfood.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column myfood.userid
     *
     * @mbg.generated
     */
    private Integer userid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column myfood.type
     *
     * @mbg.generated
     */
    private Integer type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column myfood.foodid
     *
     * @mbg.generated
     */
    private Integer foodid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column myfood.quantity
     *
     * @mbg.generated
     */
    private Integer quantity;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column myfood.create_time
     *
     * @mbg.generated
     */
    private String createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column myfood.update_time
     *
     * @mbg.generated
     */
    private Date updateTime;

    private Integer cal;

    public Integer getCal() {
        return cal;
    }

    public void setCal(Integer cal) {
        this.cal = cal;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column myfood.id
     *
     * @return the value of myfood.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column myfood.id
     *
     * @param id the value for myfood.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column myfood.userid
     *
     * @return the value of myfood.userid
     *
     * @mbg.generated
     */
    public Integer getUserid() {
        return userid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column myfood.userid
     *
     * @param userid the value for myfood.userid
     *
     * @mbg.generated
     */
    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column myfood.type
     *
     * @return the value of myfood.type
     *
     * @mbg.generated
     */
    public Integer getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column myfood.type
     *
     * @param type the value for myfood.type
     *
     * @mbg.generated
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column myfood.foodid
     *
     * @return the value of myfood.foodid
     *
     * @mbg.generated
     */
    public Integer getFoodid() {
        return foodid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column myfood.foodid
     *
     * @param foodid the value for myfood.foodid
     *
     * @mbg.generated
     */
    public void setFoodid(Integer foodid) {
        this.foodid = foodid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column myfood.quantity
     *
     * @return the value of myfood.quantity
     *
     * @mbg.generated
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column myfood.quantity
     *
     * @param quantity the value for myfood.quantity
     *
     * @mbg.generated
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column myfood.create_time
     *
     * @return the value of myfood.create_time
     *
     * @mbg.generated
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column myfood.create_time
     *
     * @param createTime the value for myfood.create_time
     *
     * @mbg.generated
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column myfood.update_time
     *
     * @return the value of myfood.update_time
     *
     * @mbg.generated
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column myfood.update_time
     *
     * @param updateTime the value for myfood.update_time
     *
     * @mbg.generated
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}