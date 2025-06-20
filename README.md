# 🚀 Notification Reminder

A lightweight, customizable **notification scheduling library** for Android using **WorkManager**.

Easily schedule **one-time** or **repeating notifications**, support for **custom payloads**, **image URLs**, **channels**, and **click actions** — all with a clean **Builder pattern**.

## 📦 Features

- ✅ Schedule **one-time** or **periodic** notifications
- 🕒 Use **delayed execution** with `TimeUnit`
- 🖼️ Supports **large icons** and **big images** from URL (Coil/Glide)
- 🔕 Customize notification: title, message, sound, visibility, category
- 🔁 Reuse or cancel with **unique notification IDs**
- 📥 Set **custom data payloads**
- 🔧 Built using `WorkManager` (reliable, OS-friendly)
- 💬 Click opens your app’s default launcher activity

## 🛠️ Installation

1. Add dependency to your `build.gradle`: (WIP, not implemented yet)

```kotlin
implementation("uz.jakhasoft:push-reminder:1.0.0")
```
> Or if local module:

```kotlin
implementation(project(":push-reminder"))
```

2. Minimum SDK: **23+**


## 🚀 Usage

### 🔧 Schedule a one-time notification:

```kotlin
ReminderBuilder()
    .setId("promo_offer") // required
    .setTitle("🎉 Special Offer!")
    .setMessage("Tap to unlock your gift 🎁")
    .setDelay(10, TimeUnit.MINUTES) // required
    .setSmallIcon(R.drawable.ic_notification) // required
    .setLargeIconUrl("https://example.com/icon.png")
    .setBigImageUrl("https://example.com/banner.png")
    .setCustomData("campaign_id", "summer2025")
    .build()

ReminderScheduler.schedule(context, builder)
```

### 🔁 Schedule a repeating notification:

```kotlin
ReminderBuilder()
    .setId("daily_reminder")
    .setTitle("Daily Tip")
    .setMessage("Come back for more!")
    .setDelay(5, TimeUnit.MINUTES)
    .setRepeatInterval(1, TimeUnit.DAYS)
    .setSmallIcon(R.drawable.ic_notification)
    .build()

ReminderScheduler.schedule(context, builder)
```

## ❌ Cancel a Notification

```kotlin
ReminderScheduler.cancel(context, "daily_reminder")
```

## 📤 Custom Data Handling

You can provide any number of key-value data using:

```kotlin
setCustomData("key", "value")
```

All data will be included in the `Intent` when the notification is clicked.

---

## 📦 What Happens on Click?

When the user taps the notification, the library opens your **default launcher activity** with the custom data passed via intent:

```kotlin
intent.getStringExtra("campaign_id") // Access your custom data
```

---

## 📸 Images & Icons

* `setLargeIconUrl()` and `setBigImageUrl()` download images using **Coil** internally.
* You can also extend this with Glide/Picasso easily.

## 🧱 Built With

* Kotlin
* AndroidX WorkManager
* Coil (for image loading)

## 💡 License

```
MIT License
Copyright (c) 2025 Jakhongir Mannonov
```

## 🤝 Contributions

PRs and suggestions are welcome!
Create an issue or fork the repo to contribute.

## 📬 Contact

Created by [Jakhongir Mannonov](https://github.com/Jakhadev) — feel free to reach out for improvements or questions.