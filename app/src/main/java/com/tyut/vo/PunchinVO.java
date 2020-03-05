package com.tyut.vo;


import java.util.List;

public class PunchinVO {

    private List<Punchin> punchinList;
    private Integer continueCount;

    public List<Punchin> getPunchinList() {
        return punchinList;
    }

    public void setPunchinList(List<Punchin> punchinList) {
        this.punchinList = punchinList;
    }

    public Integer getContinueCount() {
        return continueCount;
    }

    public void setContinueCount(Integer continueCount) {
        this.continueCount = continueCount;
    }


}
