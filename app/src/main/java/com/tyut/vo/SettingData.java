package com.tyut.vo;

import java.io.Serializable;

public class SettingData implements Serializable {

    private Boolean autoPunchin = false;
    private Boolean reminedPunchin = false;


    public SettingData() {
    }

    public SettingData(Boolean autoPunchin, Boolean reminedPunchin) {
        this.autoPunchin = autoPunchin;
        this.reminedPunchin = reminedPunchin;
    }

    public Boolean getAutoPunchin() {
        return autoPunchin;
    }

    public void setAutoPunchin(Boolean autoPunchin) {
        this.autoPunchin = autoPunchin;
    }

    public Boolean getReminedPunchin() {
        return reminedPunchin;
    }

    public void setReminedPunchin(Boolean reminedPunchin) {
        this.reminedPunchin = reminedPunchin;
    }
}
