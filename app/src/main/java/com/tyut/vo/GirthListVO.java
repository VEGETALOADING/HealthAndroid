package com.tyut.vo;

import java.util.List;

public class GirthListVO {

    private List<GirthVO> yaoList;
    private List<GirthVO> datuiList;
    private List<GirthVO> xiaotuiList;
    private List<GirthVO> tunList;
    private List<GirthVO> xiongList;
    private List<GirthVO> shoubiList;

    public GirthListVO() {
    }

    public GirthListVO(List<GirthVO> yaoList, List<GirthVO> datuiList, List<GirthVO> xiaotuiList, List<GirthVO> tunList, List<GirthVO> xiongList, List<GirthVO> shoubiList) {
        this.yaoList = yaoList;
        this.datuiList = datuiList;
        this.xiaotuiList = xiaotuiList;
        this.tunList = tunList;
        this.xiongList = xiongList;
        this.shoubiList = shoubiList;
    }

    public List<GirthVO> getYaoList() {
        return yaoList;
    }

    public void setYaoList(List<GirthVO> yaoList) {
        this.yaoList = yaoList;
    }

    public List<GirthVO> getDatuiList() {
        return datuiList;
    }

    public void setDatuiList(List<GirthVO> datuiList) {
        this.datuiList = datuiList;
    }

    public List<GirthVO> getXiaotuiList() {
        return xiaotuiList;
    }

    public void setXiaotuiList(List<GirthVO> xiaotuiList) {
        this.xiaotuiList = xiaotuiList;
    }

    public List<GirthVO> getTunList() {
        return tunList;
    }

    public void setTunList(List<GirthVO> tunList) {
        this.tunList = tunList;
    }

    public List<GirthVO> getXiongList() {
        return xiongList;
    }

    public void setXiongList(List<GirthVO> xiongList) {
        this.xiongList = xiongList;
    }

    public List<GirthVO> getShoubiList() {
        return shoubiList;
    }

    public void setShoubiList(List<GirthVO> shoubiList) {
        this.shoubiList = shoubiList;
    }
}
