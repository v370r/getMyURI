package com.getmyuri.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.getmyuri.model.Location;

public class GeoUtilsTest {

    private Location location(double lon, double lat) {
        Location loc = new Location();
        loc.setCoordinates(Arrays.asList(lon, lat));
        return loc;
    }

    @Test
    void testSameLocation() {
        Location target = location(77.5946, 12.9716);
        assertTrue(GeoUtils.isWithinRadius(target, 12.9716, 77.5946, 10));
    }

    @Test
    void testNearbyLocationWithinRadius() {
        Location target = location(77.5946, 12.9716);
        assertTrue(GeoUtils.isWithinRadius(target, 12.9720, 77.5950, 100));
    }

    @Test
    void testLocationOutsideRadius() {
        Location target = location(77.5946, 12.9716);
        assertFalse(GeoUtils.isWithinRadius(target, 12.9816, 77.5946, 500));
    }

    @Test
    void testBoundaryLocation() {
        Location target = location(77.5946, 12.9716);
        assertTrue(GeoUtils.isWithinRadius(target, 12.9760, 77.5946, 500));
    }

    @Test
    void testFarLocationAllowedWithLargeRadius() {
        Location target = location(77.5946, 12.9716);
        assertTrue(GeoUtils.isWithinRadius(target, 13.0350, 77.5970, 10000));
    }
}
