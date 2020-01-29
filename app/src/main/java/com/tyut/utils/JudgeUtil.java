package com.tyut.utils;

public class JudgeUtil {

    public static Integer getGenderData(String gender){
        switch (gender){
            case "男":
                return 1;
            case "女":
                return  0;
        }
        return null;
    }
    public static Integer getGoalData(String type){
        switch (type){
            case "减脂":
                return 0;
            case "保持":
                return 1;
            case "增肌":
                return 2;
        }
        return null;
    }

}
