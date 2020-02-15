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
    public static Integer getDietTime(String time){
        switch (time){
            case "早餐":
                return 1;
            case "午餐":
                return 2;
            case "晚餐":
                return 3;
        }
        return null;
    }
    public static String getDietName(Integer time){
        switch (time){
            case 1:
                return "早餐";
            case 2:
                return "午餐";
            case 3:
                return "晚餐";
        }
        return null;
    }

}
