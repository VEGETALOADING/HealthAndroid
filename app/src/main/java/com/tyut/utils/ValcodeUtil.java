package com.tyut.utils;

import java.util.Random;

public class ValcodeUtil{

    public static String generateValcode(){
        StringBuffer code = new StringBuffer();
        for (int i = 0; i < 6 ; i++) {
            Random random = new Random();
            code.append(random.nextInt(10));
        }
        return code.toString();
    }


}
