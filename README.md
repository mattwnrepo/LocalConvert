# LocalConvert

A minimalist, fully offline file converter for Android. Converts images locally on-device — no uploads, no tracking.

**v1.0 supports:** JPG · PNG · WebP · BMP  
**Coming soon:** Video · Audio · Ebook

---

## How to open in Android Studio & test

### 1. Prerequisites
- [Android Studio Hedgehog or newer](https://developer.android.com/studio)
- JDK 17 (bundled with Android Studio — no separate install needed)
- An Android device running Android 8.0+ (API 26), **or** an emulator (API 26+)

### 2. Clone & open
```bash
git clone https://github.com/YOUR_USERNAME/LocalConvert.git
```
Then in Android Studio: **File → Open** → select the `LocalConvert` folder → click **OK**.

### 3. Sync Gradle
Android Studio will prompt *"Gradle files have changed"* — click **Sync Now**.  
If it doesn't appear: **File → Sync Project with Gradle Files**.

### 4. Run on a device
**Physical device:**
1. On your phone: Settings → About → tap *Build number* 7× → enable Developer Options
2. Settings → Developer Options → enable **USB Debugging**
3. Plug in via USB → Android Studio will detect it in the device dropdown
4. Press the green ▶ **Run** button

**Emulator:**
1. **Tools → Device Manager → Create Device**
2. Pick a phone (e.g. Pixel 6), choose API 26+ system image, finish
3. Press ▶ to launch

### 5. Build a release APK (for GitHub / F-Droid)
```
Build → Generate Signed Bundle / APK → APK → create or pick keystore → Release
```
Output lands in `app/build/outputs/apk/release/app-release.apk`

---

## Adding AdMob (when ready)

1. In `app/build.gradle`, uncomment the AdMob dependency line
2. In `AndroidManifest.xml`, uncomment the `<meta-data>` block and fill in your App ID
3. In `MainActivity.java`, uncomment and call `initAds()`
4. In `activity_main.xml`, change `adBannerPlaceholder` visibility to `visible` and swap the `<View>` tag for `<com.google.android.gms.ads.AdView>`

---

## Adding new conversion categories (Video, Audio, Ebook)

1. Create `VideoConvertFragment.java` (etc.) in `ui/`
2. Create a converter class in `converter/` (e.g. `AudioConverter.java`)
3. In `MainActivity.java`, add a case for the chip ID → `loadFragment(new VideoConvertFragment())`
4. In `activity_main.xml`, remove `android:enabled="false"` from the relevant chip

---

## Project structure

```
app/src/main/
├── java/com/localconvert/app/
│   ├── converter/
│   │   └── ImageConverter.java   ← conversion logic
│   ├── ui/
│   │   ├── MainActivity.java
│   │   ├── ImageConvertFragment.java
│   │   └── ConvertActivity.java  ← stub for future use
│   └── utils/
│       └── FileUtils.java
└── res/
    ├── layout/
    │   ├── activity_main.xml
    │   └── fragment_image_convert.xml
    └── values/ (colors, strings, themes)
```
