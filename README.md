# 12-Step Recovery

12-Step Recovery is an Android companion app for recoverymeetingfinder.com. When it is finished it aims to provide reading materials, daily quotes, reading material, motivational images, an in-person meeting map and motivational speaker tapes.

This repository contains the source code for the Android Studio Java project.

We are looking for contributors who want to help finish the app. The issue list outlines the features which still need work or have not been implemented yet.

Here are some screenshots of the app runnning in development mode:

<img align='left' src='https://drive.google.com/uc?id=1wZGnJRKgVwqRjS_8PxRQfcI_8TUeUGV6' width='135'>
<img align='left' src='https://drive.google.com/uc?id=1hcHJoGvl8joUgJt66GMz8RhRSvwPsp4E' width='135'>
<img align='left' src='https://drive.google.com/uc?id=1CxvOFjjTj3Chsnuh7tPCo59j_-dzYWnw' width='135'>
<img align='left' src='https://drive.google.com/uc?id=1FolLCsYd05dBLEfzpOIKY4PV-_BHddFR' width='135'>
<img src='https://drive.google.com/uc?id=14LWpkYGTxzFeXYpwHZoiCNKUQHy3KNLk' width='135'>

The app can be downloaded from the Google Play store [here](https://play.google.com/store/apps/details?id=com.citex.twelve_step_recovery).

You can view a demo video of the app [here](https://www.youtube.com/watch?v=hT3zR4pld-w).

# Project Import and Gradle Build Issues

If you have a slow internet connection or project import and gradle build is taking a long time.

Download gradle build dependencies [here](https://services.gradle.org/distributions/gradle-8.9-bin.zip).

Place in the  `$installPath\12-Step-Recovery-Guide\12-Step Recovery\gradle\wrapper\` folder.

Open Gradle Scripts\gradle-wrapper.properties.

Comment out:

```
// distributionUrl=https\://services.gradle.org/distributions/gradle-8.9-bin.zip
```

and uncomment: 

```
distributionUrl=gradle-8.9-bin.zip
```

# Notes

If you want to work on the unfinished Meetings, Audio, Program or More fragment implementations:

Uncomment the fragment you want to work on in res/layout/menu/bottom_nav_menu.xml

