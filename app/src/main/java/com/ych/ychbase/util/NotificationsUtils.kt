package com.ych.ychbase.util

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.ych.ychbase.R
import java.lang.reflect.Field
import java.lang.reflect.Method


object NotificationsUtils {
    private const val CHECK_OP_NO_THROW = "checkOpNoThrow"
    private const val OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION"

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun isNotificationEnabled(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // < 8.0手机以上
            if ((context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).importance == NotificationManager.IMPORTANCE_NONE) {
                return false
            }
        }
        val mAppOps =
            context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val appInfo: ApplicationInfo = context.applicationInfo
        val pkg: String = context.applicationContext.packageName
        val uid = appInfo.uid
        val appOpsClass: Class<*>?
        try {
            appOpsClass = Class.forName(AppOpsManager::class.java.name)
            val checkOpNoThrowMethod: Method = appOpsClass.getMethod(
                CHECK_OP_NO_THROW,
                Integer.TYPE,
                Integer.TYPE,
                String::class.java
            )
            val opPostNotificationValue: Field =
                appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)
            val value = opPostNotificationValue.get(Int::class.java) as Int
            return checkOpNoThrowMethod.invoke(
                mAppOps,
                value,
                uid,
                pkg
            ) as Int == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 通知权限申请
     * @param context
     */
    fun requestNotify(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.tips)
            .setMessage(R.string.notification_hint)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.to_setting) { _, _ ->
                /** 跳到通知栏设置界面 **/
                val localIntent = Intent()
                ///< 直接跳转到应用通知设置的代码
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    localIntent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    localIntent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.O
                ) {
                    localIntent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    localIntent.putExtra("app_package", context.packageName)
                    localIntent.putExtra("app_uid", context.applicationInfo.uid)
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                    localIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    localIntent.addCategory(Intent.CATEGORY_DEFAULT)
                    localIntent.data = Uri.parse("package:" + context.packageName)
                } else {
                    ///< 4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
                    localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    if (Build.VERSION.SDK_INT >= 9) {
                        localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                        localIntent.data = Uri.fromParts("package", context.packageName, null)
                    } else if (Build.VERSION.SDK_INT <= 8) {
                        localIntent.action = Intent.ACTION_VIEW
                        localIntent.setClassName(
                            "com.android.settings",
                            "com.android.setting.InstalledAppDetails"
                        )
                        localIntent.putExtra(
                            "com.android.settings.ApplicationPkgName",
                            context.packageName
                        )
                    }
                }
                context.startActivity(localIntent)
            }
        dialog.show()
    }
}