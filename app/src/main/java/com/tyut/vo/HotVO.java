package com.tyut.vo;

import java.util.List;

public class HotVO {

    private List<MysportVO> mysportVOList;
    private List<MyfoodVO> breakfastList;
    private List<MyfoodVO> lunchVOList;
    private List<MyfoodVO> dinnerVOList;

    private Integer breakfastHot;
    private Integer lunchHot;
    private Integer dinnerHot;
    private Integer sportHot;

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

    public List<MyfoodVO> getLunchVOList() {
        return lunchVOList;
    }

    public void setLunchVOList(List<MyfoodVO> lunchVOList) {
        this.lunchVOList = lunchVOList;
    }

    public List<MyfoodVO> getDinnerVOList() {
        return dinnerVOList;
    }

    public void setDinnerVOList(List<MyfoodVO> dinnerVOList) {
        this.dinnerVOList = dinnerVOList;
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
