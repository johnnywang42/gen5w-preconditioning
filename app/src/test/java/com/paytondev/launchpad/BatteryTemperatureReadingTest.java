package com.paytondev.launchpad;

import org.junit.Test;
import static org.junit.Assert.*;

public class BatteryTemperatureReadingTest {
    @Test public void initialAndUnavailableStatesNeverInventZeroDegrees() {
        BatteryTemperatureReading reading = BatteryTemperatureReading.checking();
        assertEquals("Battery temp: checking…", reading.label(1000, true));
        assertEquals("Lowest Battery Pack Temperature: unavailable", reading.update(null, 1000).label(1000, false));
    }
    @Test public void validTemperatureShowsFahrenheitFirstAndReceiptAge() {
        BatteryTemperatureReading reading = BatteryTemperatureReading.checking().update(17, 1000);
        assertEquals("Battery temp: 63°F", reading.label(1000, true));
        assertEquals("Lowest Battery Pack Temperature: 63°F (17°C) · received 9s ago", reading.label(10000, false));
        assertFalse(reading.stale(10000));
    }
    @Test public void latestFailedReadMarksPreviousValueStaleImmediately() {
        BatteryTemperatureReading old = BatteryTemperatureReading.checking().update(20, 1000);
        BatteryTemperatureReading failed = old.update(null, 11000);
        assertEquals("Battery temp: stale", failed.label(11000, true));
        assertTrue(failed.label(11000, false).contains("last received 10s ago"));
        assertEquals(1000, failed.receivedAt);
        assertFalse(old.stale(11000)); // Published snapshots stay immutable.
    }
    @Test public void ageAloneMarksStaleAtThirtySeconds() {
        BatteryTemperatureReading reading = BatteryTemperatureReading.checking().update(20, 1000);
        assertFalse(reading.stale(30999));
        assertTrue(reading.stale(31000));
        assertTrue(reading.label(31000, false).contains("stale"));
    }
    @Test public void newSuccessfulReadRecoversFromStaleAndReplacesValue() {
        BatteryTemperatureReading reading = BatteryTemperatureReading.checking().update(17, 1000).update(null, 11000);
        reading = reading.update(18, 21000);
        assertEquals("Battery temp: 64°F", reading.label(21000, true));
        assertFalse(reading.stale(21000));
    }
    @Test public void malformedTemperatureCannotReplacePreviouslyValidValue() {
        for (Integer value : new Integer[]{null, -128, 61, 127, 255}) {
            BatteryTemperatureReading reading = BatteryTemperatureReading.checking().update(20, 1000).update(value, 11000);
            assertEquals(Integer.valueOf(20), reading.minimumC);
            assertTrue(reading.stale(11000));
        }
    }
    @Test public void zeroAndNegativeTemperaturesAreRealValues() {
        assertEquals("Battery temp: 32°F", BatteryTemperatureReading.checking().update(0,1000).label(1000,true));
        assertEquals("Battery temp: -40°F", BatteryTemperatureReading.checking().update(-40,1000).label(1000,true));
    }
    @Test public void clockResetCannotPresentAnOldValueAsFresh() {
        BatteryTemperatureReading reading = BatteryTemperatureReading.checking().update(20, 5000);
        assertTrue(reading.stale(1000));
        assertTrue(reading.label(1000,false).contains("0s ago"));
    }
    @Test public void unchangedValueStillUpdatesResponseAgeThroughoutActiveLengthInterval() {
        BatteryTemperatureReading reading = BatteryTemperatureReading.checking().update(16, 1000);
        for (long now = 11000; now <= 241000; now += 10000) {
            reading = reading.update(16, now);
            assertEquals("Battery temp: 61°F", reading.label(now, true));
            assertTrue(reading.label(now, false).endsWith("received 0s ago"));
            assertTrue(reading.label(now + 9000, false).endsWith("received 9s ago"));
            assertFalse(reading.stale(now + 9000));
        }
        assertEquals(241000, reading.receivedAt);
    }
}
