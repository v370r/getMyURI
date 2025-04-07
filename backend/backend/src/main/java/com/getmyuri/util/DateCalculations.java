package com.getmyuri.util;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateCalculations {
    public static Date calculateExpiryFrom(Date baseTime, String ttlStr) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(baseTime.toInstant(), ZoneOffset.UTC);

        Pattern pattern = Pattern.compile("(\\d+)([Mdhm])");
        Matcher matcher = pattern.matcher(ttlStr);

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "M":
                    zdt = zdt.plusMonths(value);
                    break;
                case "d":
                    zdt = zdt.plusDays(value);
                    break;
                case "h":
                    zdt = zdt.plusHours(value);
                    break;
                case "m":
                    zdt = zdt.plusMinutes(value);
                    break;
            }
        }

        return Date.from(zdt.toInstant());
    }
}
