# Battery temperature, warm advisory, and pending-request timeout

This source contribution follows the exact EV6 US 260507 profile in
[upstream PR #2](https://github.com/paytondevs/ioniq5-preconditioning/pull/2).
It has no new signed release or public version bump. The
[EV6 quick-start guide](EV6-QUICK-START.md) describes the user-facing behavior.

## Scope

| Behavior | EV6 exact profile | Original Ioniq 5 profile |
| --- | --- | --- |
| Native minimum-temperature read/display | Enabled after firmware validation | Unavailable; no guessed native transactions |
| 20°C / 68°F advisory and ten-second reset | Enabled for a valid warm reading | Not activated without a supported temperature reader |
| Five-minute cancellation of an unconfirmed request | Enabled | Enabled through the shared controller |
| Existing Start/maintain/Stop command arguments and TUNE mapping | Preserved | Preserved |

The caller still requires the correct firmware, READY, enabled conditioning,
and an Off indicator. The BMS remains responsible for actual activation. No OBD
adapter, phone bridge, raw CAN write, heater override, or navigation route is
added.

## Native read contract and bounds

Only the guarded EV6 client reaches `Ev6BatteryTemperatureReader`. Its contract
is service `com.hkmc.micom.cluster`, descriptor `com.hkmc.cluster.IClusterService`,
registration 37, request 109 without arguments, and removal 38. The listener
descriptor is `com.hkmc.cluster.INavigationClusterListener`; callback 16 carries
an eight-integer array. Index 1 is interpreted as minimum temperature in whole
degrees Celsius. Other array fields are not exposed as calibrated readings.

The decoder rejects the wrong length, implausible temperature (-40 to 60°C is
the accepted envelope), or a first field outside 0 to 200. These are plausibility
checks, not a calibration proof. Reply payloads must match the expected shape;
callbacks before the explicit request is armed do not supply a reading.

Reads use one helper at a time, including across service recreation. Overlapping
display and Start callers may share an in-flight request with its original
1.8-second deadline. They never extend the deadline or reuse a completed late
result. Registration cleanup is attempted in `finally`; a permanently hung
Binder call cannot be forcibly cleaned up, but does not create additional
helpers. A missing or failed read returns unknown rather than zero or warm.

## Display behavior

While Launchpad is bound/visible, a separate worker polls about ten seconds
after each response. The temperature worker is separate from the serialized
control worker. Unbinding removes future polls; a generation check discards
completions from an earlier binding. Closing the app does not stop a requested
conditioning session.

Home shows `Battery temp: 61°F`. Advanced shows
`Lowest Battery Pack Temperature: 61°F (16°C)` and response age. Equal values
still refresh the receipt time. A failed refresh marks the old reading stale
immediately, and 30 seconds without a successful response also marks it stale.
Home hides the old number while stale; Advanced retains the number and age.
No sample shows checking/unavailable instead of an invented zero.

Receipt age is the app's response time, not a verified vehicle sensor timestamp.
Whole-degree Celsius readings yield stepped Fahrenheit values (16°C -> 61°F,
17°C -> 63°F). Constant readings do not by themselves establish a polling fault.

## Warm advisory

A normal Start requests temperature and then rechecks READY/Off before sending
control commands. A plausible value at or above 20°C shows a ten-second notice
without journaling ownership or sending Start, maintain, or cancel. Start returns
when the notice expires if vehicle conditions still permit it. A duplicate normal
Start cannot accept the warning; the user must choose the separate Start anyway
action while it is offered. An expired override or lost eligibility cannot send.
Unknown/late temperature follows the normal manual-request path.

The 20°C / 68°F working threshold is an advisory assumption informed by
[Kia US bulletin ELE291 Rev 1, page 1](https://static.nhtsa.gov/odi/tsbs/2023/MC-10238481-0001.pdf),
which describes heating below 68°F for its specified 2022-model-year retrofit
scope. It does not establish this 2023 vehicle's exact BMS calibration or which
temperature aggregation the BMS uses. The app does not claim that the BMS
explicitly refused heating because of temperature, that the pack has reached a
charging target, or that a certain charging speed is guaranteed. Start anyway
preserves a manual request path; vehicle constraints still apply.

## Pending-request timeout

At the first status refresh at or after five minutes, a request with no observed
Active indication sends the existing cancel sequence. A fresh Active indication
at the deadline wins. Sessions that have already been observed Active have no
five-minute cutoff. Unknown/missing feedback cannot indefinitely extend a pending
request. No repeated Start pulses or automatic retry loop are introduced.

Cancellation is not completion: ownership stays recorded until cancellation has
succeeded and a fresh Off indication is received. A failed cancellation retains
Stop for a user retry. An uncertain Off leaves the request unresolved. Only
confirmed completion restores Start and displays No activation. Recovery still
cancels uncertain ownership and never automatically restarts conditioning.

## Evidence and release checks

Field observations are from a US 2023 EV6 Wind RWD on the exact 260507 profile,
using the local development build:

- App Start/Active/Stop/Off and Ready after restart following Stop were confirmed
  previously. TUNE opened Launchpad after reboot.
- The native minimum response matched an owner-supplied OBD minimum-temperature
  reading at one comparison point. OBD is not used by the app.
- On October 25, 2026, the owner confirmed the new labels, successful responses
  about every ten seconds in Advanced, and a temperature increase after a little
  over five minutes of conditioning. Exact before/after numbers were not supplied.

Host tests cover the warm notice, override, unknown-temperature fallback,
eligibility loss, timeout boundaries, cancellation failure, retained ownership,
stale/equal-value display samples, and native registration/request/removal. Host
Binder fixtures also cover a hung call and overlapping readers. They do not
execute the Android service/activity lifecycle on a vehicle.

Before a public binary release, still verify the warm notice/ten-second reset
and explicit override in-car, pending-timeout cancellation/Off/reset in-car,
display pause/resume and failed-read recovery, and the chosen public installation,
update, removal, and signing-migration path. Active-session process/power-loss
behavior, native-navigation coexistence, and driving activation remain unverified.
These limits do not negate the owner's successful manual conditioning/display
tests; they keep separate behaviors from being marked tested by inference.

Native reads remain unverified on other firmware, trims, model years or markets.
Shared timeout changes need review for the original Ioniq 5 profile as well.
No proprietary firmware/disassembly, raw vehicle logs, APKs, private updater,
signing key, or password is included in this contribution.

EV6 development, documentation, and vehicle testing: **Abro86**. Development,
documentation, and host-test assistance: **OpenAI Codex**. Original application:
**paytondevs** and contributors.
