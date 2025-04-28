package top.kikt.myapplication3

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import top.kikt.myapplication3.ui.theme.MyApplication3Theme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "CCC"
    }

    // State to track permissions
    private val hasExactAlarmPermission = mutableStateOf(false)
    private val hasNotificationPermission = mutableStateOf(false)
    private val isIgnoringBatteryOptimizations = mutableStateOf(false)

    // 通知权限请求
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            hasNotificationPermission.value = true
            Log.d(TAG, "通知权限已授予")
        } else {
            Log.d(TAG, "通知权限被拒绝")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 检查并请求所需的权限
        checkAndRequestPermissions()

        // 设置闹钟
        scheduleAlarm()

        // 初始化权限状态
        updatePermissionState()

        setContent {
            MyApplication3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AlarmPermissionScreen(
                        hasExactAlarmPermission = hasExactAlarmPermission.value,
                        hasNotificationPermission = hasNotificationPermission.value,
                        onRequestExactAlarmPermission = { requestExactAlarmPermission() },
                        onRequestNotificationPermission = { requestNotificationPermission() },
                        isIgnoringBatteryOptimizations = isIgnoringBatteryOptimizations.value,
                        onRequestIgnoreBatteryOptimization = { requestIgnoreBatteryOptimization() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        // 检查通知权限（Android 13+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                hasNotificationPermission.value = true
            }
        } else {
            // 旧版Android不需要请求通知权限
            hasNotificationPermission.value = true
        }

        // 检查是否已忽略电池优化
        checkBatteryOptimizationStatus()
    }

    private fun checkBatteryOptimizationStatus() {
        val packageName = packageName
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        isIgnoringBatteryOptimizations.value = pm.isIgnoringBatteryOptimizations(packageName)
        Log.d(TAG, "电池优化状态: 已${if (isIgnoringBatteryOptimizations.value) "忽略" else "未忽略"}")
    }

    private fun requestIgnoreBatteryOptimization() {
        val intent = Intent().apply {
            action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            data = Uri.parse("package:$packageName")
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        // 检查当返回应用时权限是否已授予
        updatePermissionState()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarmManager.canScheduleExactAlarms()) {
                // 权限已授予，重新调度闹钟以使用精确计时
                Log.d(TAG, "精确闹钟权限已授予，重新调度闹钟")
                AlarmReceiver.scheduleAlarm(this)
            }
        }

        // 检查电池优化状态
        checkBatteryOptimizationStatus()
    }

    private fun updatePermissionState() {
        // 检查精确闹钟权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            hasExactAlarmPermission.value = alarmManager.canScheduleExactAlarms()
            Log.d(TAG, "精确闹钟权限状态: ${hasExactAlarmPermission.value}")
        } else {
            // 旧版Android总是有权限
            hasExactAlarmPermission.value = true
        }

        // 检查通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasNotificationPermission.value = checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            Log.d(TAG, "通知权限状态: ${hasNotificationPermission.value}")
        } else {
            hasNotificationPermission.value = true
        }
    }

    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Toast.makeText(
                this,
                "请授予精确闹钟权限",
                Toast.LENGTH_LONG
            ).show()

            Intent().apply {
                action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                data = Uri.fromParts("package", packageName, null)
                startActivity(this)
            }

            Log.d(TAG, "从界面请求精确闹钟权限")
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun scheduleAlarm() {
        Log.d(TAG, "设置每10秒触发一次的闹钟")

        // 检查Android 12+上的精确闹钟权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // 没有权限，但仍然设置闹钟（将使用不精确的计时直到获得权限）
                AlarmReceiver.scheduleAlarm(this)

                // 显示UI，提供请求权限的按钮
                Log.d(TAG, "没有精确闹钟权限，使用不精确闹钟")
            } else {
                // 有权限，设置闹钟
                AlarmReceiver.scheduleAlarm(this)
                Log.d(TAG, "已有精确闹钟权限")
            }
        } else {
            // 对于旧版Android，不需要特殊权限
            AlarmReceiver.scheduleAlarm(this)
        }
    }
}

@Composable
fun AlarmPermissionScreen(
    hasExactAlarmPermission: Boolean,
    hasNotificationPermission: Boolean,
    onRequestExactAlarmPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
    modifier: Modifier = Modifier,
    isIgnoringBatteryOptimizations: Boolean = false,
    onRequestIgnoreBatteryOptimization: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "定时闹钟服务演示",
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 显示精确闹钟权限状态
        Text(
            text = "精确闹钟权限: ${if (hasExactAlarmPermission) "已授予" else "未授予"}",
            color = if (hasExactAlarmPermission) Color.Green else Color.Red,
            modifier = Modifier.padding(8.dp)
        )

        // 显示通知权限状态
        Text(
            text = "通知权限: ${if (hasNotificationPermission) "已授予" else "未授予"}",
            color = if (hasNotificationPermission) Color.Green else Color.Red,
            modifier = Modifier.padding(8.dp)
        )

        // 精确闹钟权限按钮
        if (!hasExactAlarmPermission) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onRequestExactAlarmPermission) {
                Text("请求精确闹钟权限")
            }

            Text(
                text = "应用需要精确闹钟权限才能正常工作",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // 通知权限按钮
        if (!hasNotificationPermission) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onRequestNotificationPermission) {
                Text("请求通知权限")
            }

            Text(
                text = "应用需要通知权限才能显示通知",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // 显示电池优化状态
        Text(
            text = "电池优化忽略: ${if (isIgnoringBatteryOptimizations) "已忽略" else "未忽略"}",
            color = if (isIgnoringBatteryOptimizations) Color.Green else Color.Red,
            modifier = Modifier.padding(8.dp)
        )

        // 电池优化按钮
        if (!isIgnoringBatteryOptimizations) {
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onRequestIgnoreBatteryOptimization) {
                Text("请求忽略电池优化")
            }

            Text(
                text = "应用需要忽略电池优化才能在后台持续运行",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (hasExactAlarmPermission && hasNotificationPermission) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "闹钟已设置，每10秒运行一次",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = "即使应用被关闭，闹钟也会继续工作",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AlarmPermissionScreenPreview() {
    MyApplication3Theme {
        AlarmPermissionScreen(
            hasExactAlarmPermission = false,
            hasNotificationPermission = false,
            onRequestExactAlarmPermission = {},
            onRequestNotificationPermission = {},
            isIgnoringBatteryOptimizations = false,
            onRequestIgnoreBatteryOptimization = {}
        )
    }
}