package org.openlyrics.jlyrics.song;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PropertiesTest {

    @Test
    void testCcliNoIsNegative() {
        Properties properties = new Properties().setCcliNo(-1);
        assertNull(properties.getCcliNo());
    }

    @Test
    void testCcliNoIsZero() {
        Properties properties = new Properties().setCcliNo(0);
        assertNull(properties.getCcliNo());
    }

    @Test
    void testCcliNoPositive() {
        Properties properties = new Properties().setCcliNo(100);
        assertEquals(100, properties.getCcliNo());
    }

    @Test
    void testReleasedVsReleaseDate() {
        Properties properties = new Properties().setReleaseDate("2023");
        assertEquals("2023", properties.getReleaseDate());
        assertEquals("2023", properties.getReleased());
    }

    @Test
    void testCustomVersionVsVersion() {
        Properties properties = new Properties().setCustomVersion("v1.12");
        assertEquals("v1.12", properties.getCustomVersion());
        assertEquals("v1.12", properties.getVersion());
    }

    @Test
    void testTrackNo() {
        Properties properties = new Properties().setTrackNo(12);
        assertEquals(12, properties.getTrackNo());
    }

    @Test
    void testTooLowTransposition() {
        Properties properties = new Properties().setTransposition(-100);
        assertNull(properties.getTransposition());
    }

    @Test
    void testLowTransposition() {
        Properties properties = new Properties().setTransposition(-99);
        assertEquals(-99, properties.getTransposition());
    }

    @Test
    void testHighTransposition() {
        Properties properties = new Properties().setTransposition(99);
        assertEquals(99, properties.getTransposition());
    }

    @Test
    void testTooHighTransposition() {
        Properties properties = new Properties().setTransposition(100);
        assertNull(properties.getTransposition());
    }
}
