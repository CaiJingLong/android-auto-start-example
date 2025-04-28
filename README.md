# Android Alarm Service Demo

[![GitHub stars](https://img.shields.io/github/stars/kikt-blog/android-alarm-service-demo?style=social)](https://github.com/kikt-blog/android-alarm-service-demo/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/kikt-blog/android-alarm-service-demo?style=social)](https://github.com/kikt-blog/android-alarm-service-demo/network/members)
[![GitHub issues](https://img.shields.io/github/issues/kikt-blog/android-alarm-service-demo)](https://github.com/kikt-blog/android-alarm-service-demo/issues)
[![GitHub license](https://img.shields.io/github/license/kikt-blog/android-alarm-service-demo)](https://github.com/kikt-blog/android-alarm-service-demo/blob/main/LICENSE)

*[中文文档](README_CN.md)*

A demo project showcasing how to implement reliable scheduled tasks in Android that continue to work even after the app is killed.

## Project Introduction

This project demonstrates how to use AlarmManager to set up scheduled tasks on Android devices that run every 10 seconds and log the current time when executed. The implementation specifically focuses on maintaining task execution even after the app is killed, which is crucial for applications that need to perform background tasks periodically.

## Technical Highlights

<img src="screenshots/app_screenshot.png" alt="App Screenshot" width="300"/>

1. **AlarmManager Implementation**
   - Using Exact Alarms to ensure accurate task execution
   - Implementing different APIs for different Android versions
   - Handling Android 12+ restrictions on exact alarms

2. **BroadcastReceivers**
   - Using multiple broadcast receivers to listen for system events
   - Implementing boot-completed functionality
   - Handling alarm trigger events

3. **Services**
   - Implementing background service to log time
   - Sending notifications to display execution status
   - Handling Android restrictions on background services

4. **Permission Handling**
   - Dynamically requesting notification permission (Android 13+)
   - Requesting exact alarm permission (Android 12+)
   - Requesting battery optimization ignoring

5. **Adaptation for Different Android Versions**
   - Compatible with Android 6.0 to Android 14
   - Handling API changes and restrictions across versions
   - Providing solutions for foreground service limitations in newer Android versions

## Key Files

- **[`AlarmReceiver.kt`](app/src/main/java/top/kikt/myapplication3/AlarmReceiver.kt)**: Handles alarm trigger events and schedules the next alarm
- **[`MyAlarmService.kt`](app/src/main/java/top/kikt/myapplication3/MyAlarmService.kt)**: Service class that logs the current time and sends notifications
- **[`BootReceiver.kt`](app/src/main/java/top/kikt/myapplication3/BootReceiver.kt)**: Handles device boot completed events to ensure auto-start
- **[`AutoStartReceiver.kt`](app/src/main/java/top/kikt/myapplication3/AutoStartReceiver.kt)**: Listens to various system events to improve auto-start reliability
- **[`MainActivity.kt`](app/src/main/java/top/kikt/myapplication3/MainActivity.kt)**: Main UI, handles permission requests and user interactions
- **[`AndroidManifest.xml`](app/src/main/AndroidManifest.xml)**: Declares all necessary permissions and components

## Usage

1. Clone or download the project
2. Open the project in Android Studio
3. Run the app on a device or emulator
4. Grant all requested permissions (notifications, exact alarms, ignore battery optimization)
5. The app will start executing tasks every 10 seconds
6. You can verify task execution by checking logcat for logs with the TAG "CCC"

## Notes

- Different Android device manufacturers may have their own background process management policies, which may require explicitly allowing the app to auto-start in device settings
- On some devices, you may need to manually add the app to an "auto-start whitelist" or "background running whitelist"
- This project is primarily for learning and demonstration purposes; adjustments may be needed for specific requirements in real applications

## License

Apache License 2.0

## Contribution

Issues and suggestions for improvements are welcome!
