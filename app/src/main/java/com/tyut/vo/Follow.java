package com.tyut.vo;

import java.util.Date;

public class Follow {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column follow.id
     *
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column follow.followerid
     *
     * @mbg.generated
     */
    private Integer followerid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column follow.followtime
     *
     * @mbg.generated
     */
    private Date followtime;

    private Integer rel;

    public Integer getRel() {
        return rel;
    }

    public void setRel(Integer rel) {
        this.rel = rel;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column follow.id
     *
     * @return the value of follow.id
     *
     * @mbg.generated
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column follow.id
     *
     * @param id the value for follow.id
     *
     * @mbg.generated
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column follow.followerid
     *
     * @return the value of follow.followerid
     *
     * @mbg.generated
     */
    public Integer getFollowerid() {
        return followerid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column follow.followerid
     *
     * @param followerid the value for follow.followerid
     *
     * @mbg.generated
     */
    public void setFollowerid(Integer followerid) {
        this.followerid = followerid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column follow.followtime
     *
     * @return the value of follow.followtime
     *
     * @mbg.generated
     */
    public Date getFollowtime() {
        return followtime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column follow.followtime
     *
     * @param followtime the value for follow.followtime
     *
     * @mbg.generated
     */
    public void setFollowtime(Date followtime) {
        this.followtime = followtime;
    }
}