package com.tyut.vo;

import java.util.List;

public class HotVO {

    private List<MysportVO> mysportVOList;
    private List<MyfoodVO> breakfastList;
    private List<MyfoodVO> lunchList;
    private List<MyfoodVO> dinnerList;

    private Integer breakfastHot;
    private Integer lunchHot;
    private Integer dinnerHot;
    private Integer sportHot;

    private Float proteinConsumed;
    private Float fatConsumed;
    private Float carbConsumed;

    public Float getProteinConsumed() {
        return proteinConsumed;
    }

    public void setProteinConsumed(Float proteinConsumed) {
        this.proteinConsumed = proteinConsumed;
    }

    public Float getFatConsumed() {
        return fatConsumed;
    }

    public void setFatConsumed(Float fatConsumed) {
        this.fatConsumed = fatConsumed;
    }

    public Float getCarbConsumed() {
        return carbConsumed;
    }

    public void setCarbConsumed(Float carbConsumed) {
        this.carbConsumed = carbConsumed;
    }

    public List<MysportVO> getMysportVOList() {
        return mysportVOList;
    }

    public void setMysportVOList(List<MysportVO> mysportVOList) {
        this.mysportVOList = mysportVOList;
    }

    public List<MyfoodVO> getBreakfastList() {
        return breakfastList;
    }

    public void setBreakfastList(List<MyfoodVO> breakfastList) {
        this.breakfastList = breakfastList;
    }

    public List<MyfoodVO> getLunchList() {
        return lunchList;
    }

    public void setLunchList(List<MyfoodVO> lunchList) {
        this.lunchList = lunchList;
    }

    public List<MyfoodVO> getDinnerList() {
        return dinnerList;
    }

    public void setDinnerList(List<MyfoodVO> dinnerList) {
        this.dinnerList = dinnerList;
    }

    public Integer getBreakfastHot() {
        return breakfastHot;
    }

    public void setBreakfastHot(Integer breakfastHot) {
        this.breakfastHot = breakfastHot;
    }

    public Integer getLunchHot() {
        return lunchHot;
    }

    public void setLunchHot(Integer lunchHot) {
        this.lunchHot = lunchHot;
    }

    public Integer getDinnerHot() {
        return dinnerHot;
    }

    public void setDinnerHot(Integer dinnerHot) {
        this.dinnerHot = dinnerHot;
    }

    public Integer getSportHot() {
        return sportHot;
    }

    public void setSportHot(Integer sportHot) {
        this.sportHot = sportHot;
    }
}
