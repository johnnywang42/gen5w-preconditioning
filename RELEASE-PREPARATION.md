# EV6 release handoff

Status: source contribution for maintainer review; not a published binary release.
The compatibility contribution is PR #2 and temperature/timeout follow-up is PR #3.
The follow-up depends on the compatibility changes. Public app numbering remains
1.2.0 / version code 3 in this branch; the maintainer selects the release version
and a suitable version code before publishing. Personal ev6.* build numbers are
not the proposed public release sequence.

## What owners can expect

- Hold TUNE to open Launchpad, then use Start/Stop without a Kia navigation route.
- Exact firmware checks for the tested US 2023 EV6 Wind RWD / Gen5W 260507 profile.
- Native lowest battery-pack temperature, Fahrenheit on Home and both units in
  Advanced. No OBD adapter or phone bridge is required.
- A ten-second warm-battery advisory with an explicit Start anyway option.
- Cancellation of a request never observed Active after five minutes, retaining
  the unresolved session until cancellation succeeds and fresh Off is received.

The owner has confirmed manual Start/Active/Stop, Ready after restart following
Stop, the TUNE shortcut, temperature refresh and a rising temperature, the warm
notice reset, and explicit Start anyway. An observed stop near a displayed
70°F / 21°C suggests a possible temperature-based stop; it is not a proven fixed
BMS cutoff. See [the detailed evidence](TEMPERATURE-AND-TIMEOUT.md).

## Limits to include in the release notes

- The five-minute pending timeout is covered by automated tests, not an in-car
  timeout observation. This is disclosed rather than requiring a manufactured
  failure before release preparation can proceed.
- Temperature can remain stale after leaving and returning during an active
  session. Reopening restored updates after the reported session ended. Do not
  use the stale value to infer current temperature or heating status.
- Only the stated US 2023 vehicle/profile has been tested. US 2022-2024 vehicles
  with matching hardware and exact firmware are possible candidates, untested.
- Driving activation, native-navigation coexistence, active-session process or
  power loss, and failed native-read recovery are not claimed verified.
- The native temperature reader/advisory is EV6-only. The pending timeout and
  Request sent label also affect the shared Ioniq 5 controller.

## Maintainer decisions before publishing

1. Review the two contributions, including the shared Ioniq controller behavior.
2. Clarify project licensing and the permitted distribution route. This branch
   does not add a license on behalf of the original author.
3. Choose the release version and signing certificate. Use the established
   certificate for updates to the existing release series. Do not request or
   distribute private signing keys. Independently signed development builds need
   a separate migration path; do not advertise them as ordinary updates.
4. Build and sign using [BUILDING.md](BUILDING.md), verify the actual APK for
   API 19, then build the USB bundles from that exact signed APK.
5. Check a first installation, a same-certificate update retaining app data,
   and removal with the intended release artifacts. Stop and confirm Off before
   each change. Verify TUNE after reboot and that removal leaves NEMESIS intact.
   Host packaging and hook tests support these checks but do not replace them.
6. Publish the matching APK, NEMESIS package and install/update/uninstall ZIPs
   with SHA-256 hashes. Identify the source commit and signing certificate
   fingerprint in the release record. Never publish the signing key.

The ZIPs contain action-specific README.txt instructions. Link owners to
[EV6-QUICK-START.md](EV6-QUICK-START.md) from the release page. Keep the status at
the top of that guide accurate when the first supporting release is available.
Only then propose the released feature for the NEMESIS community catalog, linking
the public release and guide rather than private development USB packages.

EV6 development, documentation and vehicle testing: **Abro86**. Development,
documentation and host-test assistance: **OpenAI Codex**. Original application:
**paytondevs** and contributors.
