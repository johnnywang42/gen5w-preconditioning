# Building Launchpad and the TUNE-knob service

The Android project builds Launchpad 1.2.0 (version code 3), package
`com.paytondev.launchpad`, for Android 4.4/API 19. The TUNE daemon source is in
`app/src/main/java/com/paytondev/launchpad/knob/`; its NEMESIS scripts and input
profile are in `tune-service/`.

## Requirements

- JDK 17 or later supported by the bundled Gradle/Android plugin (JDK 25 is used
  for local verification).
- Android SDK platform 37 and build-tools 36.0.0.
- Python 3 for packaging.

Set `JAVA_HOME` and `ANDROID_HOME` for your machine, or configure the SDK path
in an untracked `local.properties` file. The Gradle wrapper downloads Gradle;
the first build also downloads Android build dependencies.

## Tests and development builds

```sh
./gradlew :app:testDebugUnitTest :app:lintRelease :app:assembleDebug :app:assembleRelease
python3 tools/test_ev6_scripts.py
```

Debug uses Android's local debug signing key. On a ranchu/goldfish emulator, the
debug vehicle client simulates a pending request; it never reports heating from
a timer. Release uses the OEM client and rejects incompatible firmware.

The unsigned release is at
`app/build/outputs/apk/release/app-release-unsigned.apk`. Do not install it as a
finished release. Building does not connect to the car.

## Signed, API 19-compatible release

Signing keys and passwords are deliberately not included. Updates using the
existing release certificate must be signed by the maintainer through their
release process; contributors do not need the maintainer's private key.
A new key will produce a different certificate and may prevent
updates; do not delete installed app data or change verifier settings to work
around that difference.

Set these environment variables privately, without committing them:

- `LAUNCHPAD_KEYSTORE`: absolute path to the private keystore.
- `LAUNCHPAD_STORE_PASSWORD`: keystore password.
- `LAUNCHPAD_KEY_PASSWORD`: key password (defaults to the store password).
- `LAUNCHPAD_KEY_ALIAS`: key alias (defaults to `launchpad`).
- `ANDROID_HOME`: Android SDK location.
- `JAVA_HOME`: JDK location, if needed.

Then run:

```sh
sh tools/build_apk.sh
```

The script runs tests/lint, builds, removes prior ZIP signature metadata, signs
with the existing v1 SHA1/RSA procedure, aligns the APK, and verifies the
signature for API 19. The final artifact is `dist/Launchpad.apk`.

## NEMESIS package and USB install/update/uninstall bundles

```sh
python3 tools/build_usb_bundles.py            # uses dist/Launchpad.apk
python3 tools/build_usb_bundles.py --apk path/to/signed.apk
```

The builder requires a **signed** APK (by default the `dist/Launchpad.apk` from
`tools/build_apk.sh`), because the package installs it at
`/system/app/Launchpad.apk`. The same APK is also the **dex container** for the
root `app_process` TUNE listener (`KnobDaemon`, `Evdev`, `HoldDetector`), so the
visible app and the daemon always come from one build.

Outputs in `dist/`:

- `paytondev.launchpad.zip`: the NEMESIS package (`tune-service/` plus
  `app/Launchpad.apk`).
- `Launchpad-install-usb.zip`, `Launchpad-update-usb.zip`,
  `Launchpad-uninstall-usb.zip`: USB drive roots. Each has a `type=execute`
  `NEMESIS.data` that runs `launchpad/<action>.sh` (from `usb/launchpad/`, with
  a copy of `tune-service/scripts/common.sh`). Install and update also carry
  `packages/paytondev.launchpad.zip`.

`tools/usb/write-usb.sh` / `write-usb.ps1` write a bundle onto a drive. They
move any previous NEMESIS files on it into a backup folder rather than deleting
them.

How it works on the car:

- **install**: `nemesis install`. The package's `postinstall` stages and
  verifies the APK in `/data/local/tmp/launchpad`, then writes
  `/system/app/Launchpad.apk`. It checks free space first, compares the result
  byte for byte, and restores the previous APK if that check fails.
- **update**: `nemesis install --update`. NEMESIS runs the old package's
  `preuninstall`, which sees the new package in NEMESIS's stage directory and
  leaves the APK in place. The new `postinstall` then rewrites it in place, so
  app data survives. The `type=install` USB descriptor can't do this, because
  with Android up NEMESIS answers "uninstall first".
- **uninstall**: `nemesis uninstall`. `preuninstall` force-stops the app and
  removes `/system/app/Launchpad.apk`. The USB script also removes an APK left
  by a manual install.

Rule for these scripts: the only file under `/system` they may write or remove
is `/system/app/Launchpad.apk`. `/system` is remounted rw (`nemesis remount`)
only around that write, and only when the file actually changes. Car-side
scripts are toolbox-only `#!/system/bin/sh` (KitKat mksh: no `grep`, `awk`,
`cp` or busybox).

The service uses package id `paytondev.launchpad` and registry name
`paytondev.launchpad/knobd`. It opens `com.paytondev.launchpad/.MainActivity`
and passes the existing `from_knob` intent extra. It listens passively, without
grabbing the input device or injecting events. Its profile expects device name
`daudio_wp_daudioplus_neev23_us`, key code 17, and an 800 ms hold. On the exact
EV6 US API 19 / 260507 identity, `knob.conf` selects
`daudio_wp_daudioplus_cvev_us` with the same key and hold duration, and on the
exact NEEV US API 19 / 260507 identity it selects `daudio_wp_daudioplus_neev_us`.

Publish the three `Launchpad-*-usb.zip` files, `paytondev.launchpad.zip` (for
manual ADB installs) and `dist/Launchpad.apk` as GitHub release assets. These
scripts generate local artifacts only and never connect to a car.

See [COMPATIBILITY.md](COMPATIBILITY.md) for firmware and verification limits.
Icons retain the bundled [Phosphor license](app/src/main/assets/phosphor-LICENSE.txt).
