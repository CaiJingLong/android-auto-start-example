package top.kikt.myapplication3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "CCC"

        // 使用新的AlarmReceiver来调度闹钟
        fun scheduleAlarm(context: Context) {
            Log.d(TAG, "通过BootReceiver调度闹钟")
            AlarmReceiver.scheduleAlarm(context)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "设备启动完成，调度闹钟")
            scheduleAlarm(context)
        }
    }
}
