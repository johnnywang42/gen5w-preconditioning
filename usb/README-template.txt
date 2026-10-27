Launchpad - @ACTION@ USB

This drive runs the @ACTION@ action. Use only ONE action bundle on a drive.
Install is for a first installation; update replaces an existing installation;
uninstall removes Launchpad and its TUNE shortcut, leaving NEMESIS installed.

BEFORE YOU BEGIN
Use a maintainer release whose notes explicitly support your head unit and
software. EV6 support was tested on a US 2023 EV6 Wind RWD, Gen5W software
CV_E.USA.S5W_M.V015.012.260507. Matching 2022-2024 US cars are untested.
NEMESIS must already be installed. Engineering Mode is not needed for this step.
If your installed APK was signed by another developer, arrange migration before
updating; do not clear app data to force a certificate change.

ON THE COMPUTER
1. Use an empty FAT32 or exFAT USB, or back it up first.
2. Move old NEMESIS.data, NEMESIS.sh, ota_package.tar, launchpad and packages
   items off the drive so another installer cannot run by accident.
3. Extract the outer ZIP. Copy its CONTENTS to the drive, not the outer folder.
   NEMESIS.data and this README.txt must be visible immediately on opening it.
   Leave packages/paytondev.launchpad.zip zipped (uninstall has no package ZIP).
4. Safely eject the drive.

IN THE CAR
1. Park and keep the car on READY. Wait for infotainment to finish starting.
2. Before update/removal, Stop any pending or active manual conditioning session
   and confirm Advanced shows Car indicator: Off. Closing Launchpad is not Stop.
3. Insert the drive into the USB DATA port used for wired Android Auto/CarPlay.
4. Accept the NEMESIS USB execution prompt. If it expires, reinsert the drive.
   Do not use Engineering Mode or Run USB NEMESIS.sh for this bundle.
5. Wait for success. On failure, keep the log and follow the instructions below.
6. After success, remove the drive and restart infotainment with its recessed
   reset button. This is not a factory reset.
7. After install/update, hold the right-hand TUNE knob for about one second to
   open Launchpad. TUNE opens the screen; it does not begin heating.

USING PRECONDITIONING
Enable Battery conditioning mode in the car's EV settings. Start requests
conditioning; Request sent is not confirmation. Active means the car reports
conditioning active. Stop cancels; wait for Session ended and Car indicator: Off.
On supported EV6 software, Battery warm offers a ten-second reset or explicit
Start anyway. The car still decides whether to activate. A request never seen
Active has a five-minute cancellation timer; this has automated test coverage,
but is not yet verified in-car. Active sessions do not use that timeout.

Battery temp is the lowest reported battery-pack sensor, not outside temperature.
If it becomes stale after leaving/returning, try reopening Launchpad. This
restored updates in the reported test after conditioning ended. A stale value
does not establish whether the battery is warm or conditioning is active.

IF THE ACTION FAILS
Keep launchpad-@ACTION@.log from this drive. Open NEMESIS Manager with three
quick NAV presses, or three SETUP presses if you configured that shortcut.
Use Logs -> Copy to USB. Retain NEMESIS-logs as well. Do not repeatedly reinstall
or clear app data. Report the release, model/year/market, full software version,
action and error. Remove personal information before posting logs publicly.
"No supported files" alone is a media-player message, not an installation result.

Full EV6 guide and release status:
https://github.com/paytondevs/ioniq5-preconditioning
Check the EV6-QUICK-START.md file in the source matching your release.

Original app: paytondevs and contributors.
EV6 development, documentation and vehicle testing: Abro86.
Development, documentation and host-test assistance: OpenAI Codex.
