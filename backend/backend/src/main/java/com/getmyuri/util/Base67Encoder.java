package com.getmyuri.util;

import org.springframework.stereotype.Component;

@Component
public class Base67Encoder {
    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.~!$";
    private static final int BASE = CHARSET.length();

    public String encode(long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(CHARSET.charAt((int) (number % BASE)));
            number /= BASE;
        }
        while (sb.length() < 7) {
            sb.append(CHARSET.charAt(0));
        }
        return sb.reverse().toString();
    }
}
