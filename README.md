# EnomaTestPractical
Sample of recent practical done in Compose

> This repo is my playground for quick Android experiments (Jetpack Compose, Kotlin, animations, deep links, etc.).
> First demo: a **₦ amount drag stepper** that increments/decrements by **₦100** while you hold/drag.

## 🎬 Demo
If you commit the video to `videos/nairaincrement.mp4`, it will render inline on GitHub:

<video src="videos/nairaincrement.mp4" controls muted playsinline style="max-width:100%; border-radius:12px;"></video>

Fallback link (after commit): `videos/nairaincrement.mp4`

---

## 🧪 Current Experiment — Naira Amount Increment
A draggable pill that auto-steps an integer counter past a threshold and formats it as a **₦ amount** (₦100 per tick).

- Drag **right** → increases; **left** → decreases
- Accelerates while held past the edge
- Snaps back to center on release
- Shows **₦100, ₦200, ₦300, …** (formatted for NG locale)

### Usage
```kotlin
@Composable
fun DemoScreen() {
    Box(Modifier.fillMaxSize()) {
        NairaAmountIncrement()
    }
}