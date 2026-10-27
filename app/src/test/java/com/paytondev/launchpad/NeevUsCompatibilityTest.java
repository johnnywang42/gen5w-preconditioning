package com.paytondev.launchpad;

import org.junit.Test;
import static org.junit.Assert.*;

public class NeevUsCompatibilityTest {
    private static boolean neev(int sdk, String device, String software, String framework, String service) {
        return PreconditioningProfile.matchesFirmware(sdk, device, software, framework, service);
    }
    @Test public void exactCollectedNeevUsFirmwareMatches() {
        assertTrue(PreconditioningProfile.supportsDevice(19, "wp_daudioplus_neev_us"));
        assertTrue(neev(19, "wp_daudioplus_neev_us", "NEEV.USA.0000.V015.012.260507",
            "dd72e04bce0a2b4bda1506308ac21f91002c1436553b8156c345cc4313661338",
            "3cf40fb00854c63afa49abea1dad18a0afcbc0dd68b17de9ed7ea3f5aeab4222"));
    }
    @Test public void mismatchedApiSoftwareOrEitherHashCannotEnableNeevUs() {
        String device = PreconditioningProfile.NEEV_US_DEVICE, software = PreconditioningProfile.NEEV_US_SOFTWARE;
        String framework = PreconditioningProfile.FRAMEWORK_SHA256, service = PreconditioningProfile.SERVICE_SHA256;
        assertFalse(neev(19, device, software,
            PreconditioningProfile.EV6_FRAMEWORK_SHA256, PreconditioningProfile.EV6_SERVICE_SHA256));
        assertFalse(neev(20, device, software, framework, service));
        assertFalse(neev(19, "wp_daudioplus_neev_us_can", software, framework, service));
        assertFalse(neev(19, device, "NEEV.USA.0000.V015.012.999999", framework, service));
        assertFalse(neev(19, device, PreconditioningProfile.EV6_SOFTWARE, framework, service));
        assertFalse(neev(19, device, software, "unknown", service));
        assertFalse(neev(19, device, software, framework, "unknown"));
        assertFalse(neev(19, device, null, framework, service));
        assertFalse(neev(19, device, software, null, null));
    }
    @Test public void neevUsSoftwareDoesNotChangeTheOtherProfiles() {
        assertFalse(neev(19, PreconditioningProfile.EV6_DEVICE, PreconditioningProfile.NEEV_US_SOFTWARE,
            PreconditioningProfile.FRAMEWORK_SHA256, PreconditioningProfile.SERVICE_SHA256));
        assertTrue(neev(19, PreconditioningProfile.DEVICE, "original product not constrained",
            PreconditioningProfile.FRAMEWORK_SHA256, PreconditioningProfile.SERVICE_SHA256));
    }
}
