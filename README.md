# VMP Share Demo

A small Jetpack Compose prototype exploring how sharing from VMP Messenger to
other apps (WhatsApp, Telegram, Facebook) could pay off better for both the
sender and the person receiving it.

No backend — all data is mocked.

## Try it

- **In the browser:** [Appetize demo](https://appetize.io/app/b_6daee2hkr34vnll5zr27hkiaem)

## The three screens

1. **Feed** — tapping Share on a post sends the post text, its author and a
   tracked link through the Android share sheet.
2. **Invite** — shows a preview of the exact message before sending. The
   invite names the sender and includes their VMP number.
3. **Recipient's first launch** — the new user sees who invited them and can
   add them in one tap, instead of landing in an empty app.

## Where to look in the code

| File | What it does |
|---|---|
| `Share.kt` | Every outgoing share message, in one place. Links carry a Google Play Install Referrer (`&referrer=inv_…`, `&referrer=post_…`) so installs can be attributed to the share that caused them. |
| `MainActivity.kt` | The three screens and navigation between them. |

## Notes

- The "See what your friend lands on" button is a demo-only shortcut. In a
  real app, that screen would open through a deferred deep link on first
  launch after install.
- The browser emulator doesn't have WhatsApp installed, so its share sheet
  shows only a few apps.

## Build

Open in Android Studio and run, or:

    ./gradlew assembleDebug
