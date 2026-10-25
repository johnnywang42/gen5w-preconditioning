# EV6: install and use manual battery preconditioning

**Release status:** these instructions accompany an EV6 contribution under
review. Wait for a maintainer release whose notes explicitly include EV6 US
260507 support. The existing release downloads may not contain it. This branch
does not provide a new signed installer.

## Check your car first

Tested vehicle: **US 2023 Kia EV6 Wind RWD**, with **Standard-class Gen5W
Navigation** and software **`CV_E.USA.S5W_M.V015.012.260507`**.

**Other model years:** US-market 2022–2024 EV6 vehicles with the same Gen5W
hardware and exact supported firmware may also be compatible, but have not
been tested beyond the vehicle above. The app checks device identity, software
version, and both firmware fingerprints before allowing preconditioning.
Reports from owners of matching 2022 and 2024 vehicles are welcome. Model year
alone does not establish compatibility.

Open **Setup -> General -> Software information/Update** to check your software
version. Match the **Software Version** line, not the separate Firmware Version
or map version. A different model year, market, or software version has not been
verified by this contribution. The app also checks internal firmware fingerprints
before allowing Start. See [compatibility details](COMPATIBILITY.md).

You need NEMESIS installed, a computer, and a FAT32 or exFAT USB drive. No OBD
adapter, phone connection, Wi-Fi, or ADB is needed to use this feature.

## 1. Install NEMESIS once

If NEMESIS already works on your car, skip this step.

Follow the [official NEMESIS installation guide](https://github.com/xwtk/hmg_nemesis#quick-installation-guidance).
It covers the separate Engineering Mode installation. Engineering Mode entry
depends on the head unit; this app guide does not supply a universal PIN or
button sequence. Once NEMESIS is installed, Launchpad installation and use do
not require Engineering Mode.

To open NEMESIS Manager, press **NAV three times quickly** on a default NEMESIS
installation. If you previously chose **Setup** in NEMESIS **Options**, use
**SETUP** three times instead. NAV/SETUP opens NEMESIS; holding TUNE opens
Launchpad after installation. These are separate shortcuts.

## 2. Prepare the USB

1. From the maintainer's [release page](https://github.com/paytondevs/ioniq5-preconditioning/releases),
   choose a release explicitly listing EV6 US 260507 support.
2. Download **`Launchpad-install-usb.zip`** for a first installation. If Launchpad
   is already installed, read **Updating** below first.
3. Use an empty drive, or back up its contents to your computer. Move any old
   `NEMESIS.data`, `NEMESIS.sh`, and `ota_package.tar` off the drive.
4. Extract the ZIP. Copy the **contents** of its folder onto the drive, rather
   than copying the folder itself. Do not extract the inner package ZIP.
5. Check that you can see this layout immediately when you open the drive:

```text
USB drive/
  NEMESIS.data
  launchpad/
    common.sh
    install.sh
    lib.sh
  packages/
    paytondev.launchpad.zip
```

The public installer uses a USB confirmation prompt. It is different from the
personal test USB that opened Manager and required **Run USB NEMESIS.sh**.

## 3. Install while parked

1. Keep the car parked and on **READY**. Wait for infotainment to finish starting.
2. If a manual conditioning session is already running or waiting, press
   **Stop** and wait for the car to report **Off** before updating anything.
3. Insert the drive into the USB **data port** used for wired Android Auto or
   CarPlay, not a charge-only port.
4. Accept NEMESIS's prompt to run the USB item. If it expires, remove and reinsert
   the drive once. This installer does not need the Manager's manual Run button.
5. Wait for the success result. If it fails, follow **If something goes wrong**
   below instead of repeatedly rerunning it.
6. After success, remove the USB and restart infotainment using its recessed
   reset button. This is an infotainment restart, not a factory reset.
7. Once infotainment is back, **press and hold the TUNE knob for about one second**.
   Launchpad should open. Hold the right-hand TUNE knob, not the volume knob.

You do not need to leave the USB plugged in.

## 4. Start and stop conditioning

1. In the Kia EV settings, enable **Battery conditioning mode**. Recheck it after
   an infotainment update; a previous setting is not proof it is still enabled.
2. For the first check, keep the car parked in READY with **no active Kia
   navigation route**. You do not need to select a charger in Kia navigation.
3. Hold **TUNE** to open Launchpad. Tap **Start** beside Preconditioning.
4. **Waiting for BMS** means the request was sent; it does not mean heating has
   started. **Active** means the car reports conditioning active. Activation is
   not guaranteed: the car decides whether temperature, charge, and other
   conditions allow it.
5. Tap **Stop** to cancel, even while waiting. Wait for **Session ended** and the
   Advanced screen's **Car indicator: Off** before considering cancellation
   confirmed. If it remains unknown or Stop reports an error, retain the logs.

Holding TUNE opens the app; it does not start heating. Closing the app or
returning to Android Auto does **not** stop an active manual request. Use Stop.
If it keeps waiting, you can stop the request and try again when needed. This
initial contribution does not include the later temperature display or automatic
waiting timeout from the local development build.

## Updating

Use **`Launchpad-update-usb.zip`** from the same maintainer's release series and
follow the installation steps above, starting with stopping any pending or
active session. Its USB layout has `update.sh` instead of `install.sh`. Update
logs are named **`launchpad-update.log`**.

If you installed a developer-signed or independently built APK, do not assume
the maintainer's updater can replace it. Ask for a migration plan first; the
signing certificates may differ. Do not clear app data to force an update.

## Removing Launchpad

Stop any pending or active session and confirm **Off**. Use the maintainer's
**`Launchpad-uninstall-usb.zip`**, accept the USB prompt, wait for success, then
remove the drive and restart infotainment. This removes Launchpad and its TUNE
service; it leaves NEMESIS installed. Its layout has `uninstall.sh` and no
`packages` folder. Keep `launchpad-uninstall.log` if anything fails.

## If something goes wrong

| What you see | What to do |
| --- | --- |
| Only "No supported files" and no NEMESIS prompt | Check NEMESIS is installed, the drive uses a data port, and `NEMESIS.data` is at the drive's top level. The media-player message alone does not diagnose NEMESIS. |
| Install/update reports failure | Keep the USB inserted. Open NEMESIS Manager, then **Logs -> Copy to USB**. Bring it back to the computer and keep `launchpad-install.log` (or the update/uninstall equivalent) and `NEMESIS-logs`. |
| Holding TUNE does nothing | Confirm you restarted infotainment after installation. In NEMESIS **Services**, check `paytondev.launchpad/knobd`. Report its status along with your software version. |
| Preconditioning is Unavailable | Read the reason shown in Advanced. Confirm READY and Battery conditioning mode; a firmware mismatch needs investigation, not disabled compatibility checks. |
| It stays Waiting for BMS | The car has not confirmed activation. Tap Stop; do not assume the battery is warm solely from this message. |

When reporting a problem, include the model/year/market, full software version,
release name, steps taken, and the relevant error. Review logs before posting
them publicly; redact personal identifiers, locations, and device information
unrelated to the fault. Do not upload complete vehicle firmware dumps.

Development and host testing were assisted by **OpenAI Codex**. **Abro86**
contributed the EV6 work and performed the vehicle tests. Launchpad remains the
original work of **paytondevs** and its contributors.
