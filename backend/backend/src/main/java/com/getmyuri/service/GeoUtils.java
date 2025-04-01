package com.getmyuri.service;

import com.getmyuri.model.Location;

public class GeoUtils {
    public static boolean isWithinRadius(Location targetLoc, double userLat, double userLon,
            double radiusMeters) {
        double distance = haversine(targetLoc, userLat, userLon);
        return distance <= radiusMeters;
    }

    private static double haversine(Location targetLoc, double lat2, double lon2) {
        final int R = 6371000;

        double dLat = Math.toRadians(lat2 - targetLoc.getCoordinates().get(1));
        double dLon = Math.toRadians(lon2 - targetLoc.getCoordinates().get(0));

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(targetLoc.getCoordinates().get(0))) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

}
