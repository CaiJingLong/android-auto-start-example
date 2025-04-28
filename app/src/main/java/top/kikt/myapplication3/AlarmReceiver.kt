package top.kikt.myapplication3

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "CCC"
        private const val REQUEST_CODE = 234
        private const val ACTION_ALARM = "top.kikt.myapplication3.ACTION_ALARM"

        fun scheduleAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // 创建一个广播Intent，而不是直接启动服务
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_ALARM
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 设置10秒后触发闹钟
            val intervalMillis = 10 * 1000L // 10秒
            val triggerTime = SystemClock.elapsedRealtime() + intervalMillis

            // 根据Android版本使用不同的方法设置闹钟
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    // Android 12+
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            triggerTime,
                            pendingIntent
                        )
                        Log.d(TAG, "Android 12+: 设置精确闹钟")
                    } else {
                        // 没有精确闹钟权限，使用不精确的闹钟
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            triggerTime,
                            pendingIntent
                        )
                        Log.d(TAG, "Android 12+: 设置不精确闹钟（无精确闹钟权限）")
                    }
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    // Android 6.0-11
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                    Log.d(TAG, "Android 6-11: 设置精确闹钟")
                }
                else -> {
                    // 旧版Android
                    alarmManager.setExact(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                    Log.d(TAG, "旧版Android: 设置精确闹钟")
                }
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "收到闹钟广播: ${intent.action}")

        if (intent.action == ACTION_ALARM || intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // 启动服务
            val serviceIntent = Intent(context, MyAlarmService::class.java)

            // 在Android 14+上，我们不使用前台服务，直接启动普通服务
            context.startService(serviceIntent)
            Log.d(TAG, "通过startService启动服务")
        }
    }
}
