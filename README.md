# Welcome to OCR with Google ML kit Text recognition
>  **Objective**: OCR - optical character recognition of food products - nutrition facts, barcode scan, expiry date
Task1 : primary focus on Nutrition facts detection
## Table of contents
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Code Implementation](#code-implementation)
- [Run / Verify](#run--verify)
- [Testing & Results](#testing--results)

## Prerequisites
- OS: `Windows`
- Tools: `git`, `Android Studio`  
- Hardware : `<make sure C drive have atleast 100 GB space>`
- Framework: `Kotlin`

Verify versions:
## Installation
1. Download Android Studio Ladybug. Go to the official Android Studio download page:  
    👉 https://developer.android.com/studio
2. Run the downloaded `.exe` installer.
3. Follow the setup wizard → choose components (Android Studio + Android Virtual Device). Click **Next → Install → Finish**.
4.   Launch Android Studio.
5. settings → Select **Standard Installation** .
6.  It will download the **Android SDK 13**, **Emulator**, and **SDK Tools 13**.
7.  Once complete, create emulator with android Sdk 13 

## Code Implementation (walkthrough)

### 🔹 Step 1: Create New Android Studio Project
1.  Open **Android Studio** → `New Project`.    
2.  Choose **Empty Activity**.
3.  Name it → e.g. `OCRDemo`. 
4.  Select **Kotlin** as language.  
5.  Minimum SDK → API 21 (Android 4.0) is fine.
### 🔹 Step 2: Add Dependencies
In your `app/build.gradle` add ML Kit Text Recognition v2 dependency:
```
dependencies {
    implementation ("com.google.mlkit:text-recognition:16.0.0-beta6")
}
``` 
Sync the project.
### 🔹 Step 3: Add an ImageView and Button (UI)
Edit `res/layout/activity_main.xml` layout of image , followed by button and table area

👉 Place a test image in `app/src/main/res/drawable/sample_image.jpg`.

 MainActivity in Kotlin
`app\src\main\java\com\example\ocrdemofornutrition\MainActivity.kt`


## 🔹 Step 5: Run App

1.  Run on an **emulator** or real phone.
    
2.  You’ll see the sample image.
    
3.  Tap **Recognize Text** → ML Kit OCR runs → text appears in `TextView`.

## Run / Verify

play button to run app 

Expected output:
check WORD docx for expected result analysis


> Written with [StackEdit](https://stackedit.io/).
