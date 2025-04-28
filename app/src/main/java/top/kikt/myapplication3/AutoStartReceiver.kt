package top.kikt.myapplication3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 自启动广播接收器，用于监听各种系统事件以确保服务能够自启动
 */
class AutoStartReceiver : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "CCC"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d(TAG, "AutoStartReceiver收到广播: $action")
        
        // 对各种系统事件做出响应，确保服务能够自启动
        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_REBOOT,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON" -> {
                Log.d(TAG, "系统启动或应用更新，启动闹钟服务")
                AlarmReceiver.scheduleAlarm(context)
            }
            
            Intent.ACTION_USER_PRESENT,
            Intent.ACTION_SCREEN_ON -> {
                // 用户解锁屏幕或屏幕点亮时，检查并确保闹钟服务正在运行
                Log.d(TAG, "屏幕点亮或用户解锁，确保闹钟服务运行")
                AlarmReceiver.scheduleAlarm(context)
            }
        }
    }
}
