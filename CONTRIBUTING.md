# contributing

i'm accepting any and all contributions: bug fixes, new vehicles, features, docs, whatever. if you're not sure something's worth a PR, open it anyway and we'll figure it out.

## versioning

the version lives in one place: the [`VERSION`](VERSION) file at the root of the repo. bump it in your PR:

| what you changed | bump | example |
| --- | --- | --- |
| bug fix | **+0.0.1** | `1.3.0` → `1.3.1` |
| vehicle port | **+0.1** (and reset the last number) | `1.3.2` → `1.4.0` |
| docs, CI or other changes the car never sees | none | |

if your change doesn't fit, pick what seems right and say so in the PR. i'll sort it out.

**put the new version number in your PR description.** if another PR gets merged first with the same number, i'll ask you to bump it again.

don't edit the version anywhere else. the app, the NEMESIS package and the release all read it from `VERSION`. see [BUILDING.md](BUILDING.md).

## vehicle ports

a vehicle port adds a new car or head unit, usually a new profile in `PreconditioningProfile.java`, a TUNE entry in `tune-service/knob.conf`, and a section in [COMPATIBILITY.md](COMPATIBILITY.md).

**please include a demonstration that it works on the vehicle.** a video is best. it should show:

- pressing **Start** and Launchpad reaching **Active** (the car's own conditioning indicator too, if it has one)
- pressing **Stop** and the car going back to **Off**
- holding TUNE opening Launchpad, if you added a TUNE profile

also include in the PR:

- the model, model year, trim and market (e.g. US 2022 IONIQ 5 SE AWD)
- the head unit's device name and full software version
- what you tested and what you didn't

profiles match exact firmware on purpose. don't loosen a check to make another car or firmware pass; add a new profile for it instead.

blur your VIN, license plate and location before posting anything. don't upload firmware dumps, disassembly or raw vehicle logs to the repo.

## before you open a PR

run the tests from [BUILDING.md](BUILDING.md):

```sh
./gradlew :app:testDebugUnitTest :app:lintRelease :app:assembleDebug :app:assembleRelease
python3 tools/test_ev6_scripts.py
python3 tools/test_temperature_binder.py
python3 tools/test_usb_bundles.py
```

CI runs the Gradle checks on every PR too. you don't need a signing key, since CI signs releases itself.

if you touch anything that runs on the car (`tune-service/` or `usb/`):

- the only file under `/system` anything may write or remove is `/system/app/Launchpad.apk`. everything else goes in `/data/local/tmp` or the package's own folder.
- scripts run under KitKat's `/system/bin/sh` with toolbox only: no `grep`, `awk`, `cp` or busybox, and toolbox commands have fewer flags than you'd expect (`cmp` has no `-s`). check before you use one.
- keep LF line endings.

never commit signing keys, passwords, APKs or anything from `dist/`.

## after it's merged

every merge to `main` builds the app and the USB installer and adds them to a draft release for the current version. i test it and publish it from there, so you don't need to build release files yourself.
