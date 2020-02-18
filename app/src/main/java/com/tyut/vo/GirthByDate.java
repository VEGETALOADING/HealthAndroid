package com.tyut.vo;

import java.util.List;
import java.util.Map;

public class GirthByDate {

    private Map<String, List<GirthVO>> girthMap;

    public GirthByDate(Map<String, List<GirthVO>> girthMap) {
        this.girthMap = girthMap;
    }

    public GirthByDate() {
    }

    public Map<String, List<GirthVO>> getGirthMap() {
        return girthMap;
    }

    public void setGirthMap(Map<String, List<GirthVO>> girthMap) {
        this.girthMap = girthMap;
    }
}

