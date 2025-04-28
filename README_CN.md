# Android 闹钟服务演示

[![GitHub stars](https://img.shields.io/github/stars/kikt-blog/android-alarm-service-demo?style=social)](https://github.com/kikt-blog/android-alarm-service-demo/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/kikt-blog/android-alarm-service-demo?style=social)](https://github.com/kikt-blog/android-alarm-service-demo/network/members)
[![GitHub issues](https://img.shields.io/github/issues/kikt-blog/android-alarm-service-demo)](https://github.com/kikt-blog/android-alarm-service-demo/issues)
[![GitHub license](https://img.shields.io/github/license/kikt-blog/android-alarm-service-demo)](https://github.com/kikt-blog/android-alarm-service-demo/blob/main/LICENSE)

*[English Document](README.md)*

一个演示如何在Android中实现可靠的定时任务的示例项目，即使在应用被杀死后也能继续工作。

## 项目介绍

这个项目演示了如何使用AlarmManager在Android设备上设置定时任务，每10秒执行一次，并在执行时记录当前时间。该实现特别关注在应用被杀死后保持任务继续运行的能力，这在需要定期执行后台任务的应用中非常重要。

<img src="screenshots/app_screenshot.png" alt="应用截图" width="300"/>

## 技术要点

1. **AlarmManager的使用**
   - 使用精确闹钟（Exact Alarms）确保定时任务准确执行
   - 根据不同Android版本使用不同的API设置闹钟
   - 处理Android 12+对精确闹钟的权限限制

2. **广播接收器（BroadcastReceiver）**
   - 使用多个广播接收器监听系统事件
   - 实现开机自启动功能
   - 处理闹钟触发事件

3. **服务（Service）**
   - 实现后台服务记录时间
   - 发送通知显示执行状态
   - 处理Android各版本对后台服务的限制

4. **权限处理**
   - 动态请求通知权限（Android 13+）
   - 请求精确闹钟权限（Android 12+）
   - 请求忽略电池优化

5. **适配不同Android版本**
   - 兼容Android 6.0到Android 14
   - 处理不同版本的API变化和限制
   - 针对高版本Android的前台服务限制提供解决方案

## 关键文件

- **[`AlarmReceiver.kt`](app/src/main/java/top/kikt/myapplication3/AlarmReceiver.kt)**: 处理闹钟触发事件，设置下一次闹钟
- **[`MyAlarmService.kt`](app/src/main/java/top/kikt/myapplication3/MyAlarmService.kt)**: 服务类，记录当前时间并发送通知
- **[`BootReceiver.kt`](app/src/main/java/top/kikt/myapplication3/BootReceiver.kt)**: 处理设备启动完成事件，确保开机自启动
- **[`AutoStartReceiver.kt`](app/src/main/java/top/kikt/myapplication3/AutoStartReceiver.kt)**: 监听多种系统事件以提高自启动可靠性
- **[`MainActivity.kt`](app/src/main/java/top/kikt/myapplication3/MainActivity.kt)**: 主界面，处理权限请求和用户交互
- **[`AndroidManifest.xml`](app/src/main/AndroidManifest.xml)**: 声明所有必要的权限和组件

## 使用方法

1. 克隆或下载项目
2. 在Android Studio中打开项目
3. 运行应用到设备或模拟器
4. 授予所有请求的权限（通知、精确闹钟、忽略电池优化）
5. 应用将开始每10秒执行一次任务
6. 可以通过logcat查看TAG为"CCC"的日志来确认任务执行情况

## 注意事项

- 不同厂商的Android设备可能有自己的后台进程管理策略，可能需要在设备设置中专门允许应用自启动
- 在某些设备上，可能需要手动将应用添加到"自启动白名单"或"后台运行白名单"中
- 该项目主要用于学习和演示目的，在实际应用中可能需要根据具体需求进行调整

## 许可

Apache License 2.0

## 贡献

欢迎提交问题和改进建议！
