package com.ych.ychbase.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.PermissionUtils
import com.ych.ychbase.R
import com.ych.ychbase.app.toast

/**
 * 权限申请工具类
 *
 * @author lmy
 */
object YchPermission {
    /** 是否需要权限申请 **/
    private val isNeedRequestPermission: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    /**
     * 请求本地读写权限
     *
     * @param callback
     */
    fun requestReadWritePermission(callback: (isGranted: Boolean) -> Unit) {
        request(PermissionConstants.STORAGE, callback)
    }

    /**
     * 请求相机权限
     *
     * @param callback
     */
    fun requestCameraPermission(callback: (isGranted: Boolean) -> Unit) {
        request(PermissionConstants.CAMERA, callback)
    }

    /**
     * 请求手机状态权限
     *
     * @param callback
     */
    fun requestPhoneStatePermission(callback: (isGranted: Boolean) -> Unit) {
        request(PermissionConstants.PHONE, callback)
    }

    /**
     * 请求定位权限
     *
     * @param callback
     */
    fun requestLocationPermission(callback: (isGranted: Boolean) -> Unit) {
        request(PermissionConstants.LOCATION, callback)
    }

    /**
     * 请求图片权限
     *
     * @param callback
     */
    fun requestPhotoPermission(callback: (isGranted: Boolean) -> Unit) {
        if (isNeedRequestPermission) {
            PermissionUtils.permission(PermissionConstants.CAMERA, PermissionConstants.STORAGE)
                .callback(object : PermissionUtils.FullCallback {
                    override fun onGranted(granted: MutableList<String>) {
                        callback.invoke(true)
                    }

                    override fun onDenied(
                        deniedForever: MutableList<String>,
                        denied: MutableList<String>
                    ) {
                        toast(R.string.permission_apply_hint)
                        callback.invoke(false)
                    }

                }).request()
        } else {
            callback.invoke(true)
        }
    }

    /**
     * 请求权限
     *
     * @param permission 权限名
     * @param callback 回调
     */
    private fun request(permission: String, callback: (isGranted: Boolean) -> Unit) {
        if (isNeedRequestPermission) {
            PermissionUtils.permission(permission)
                .callback(object : PermissionUtils.FullCallback {
                    override fun onGranted(granted: MutableList<String>) {
                        callback.invoke(true)
                    }

                    override fun onDenied(
                        deniedForever: MutableList<String>,
                        denied: MutableList<String>
                    ) {
                        toast(R.string.permission_apply_hint)
                        callback.invoke(false)
                    }

                }).request()
        } else {
            callback.invoke(true)
        }
    }

    private var permissionBuilder: AlertDialog.Builder? = null
    private var permissionDialog: AlertDialog? = null

    fun requestInstallPermission(activity: Activity, listener: (isGranted: Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.packageManager.canRequestPackageInstalls()) {
                if (permissionBuilder == null) {
                    permissionBuilder = AlertDialog.Builder(activity)
                    permissionBuilder?.setTitle(R.string.tips)
                    permissionBuilder?.setMessage(R.string.package_install_permission)
                    permissionBuilder?.setNegativeButton(R.string.cancel, null)
                    permissionBuilder?.setPositiveButton(
                        R.string.confirm
                    ) { _, _ ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (permissionDialog != null) {
                                permissionDialog?.dismiss()
                            }
                            startPackageInstallPermission(activity)
                        }
                    }
                    permissionDialog = permissionBuilder?.show()
                }
                return
            }
        }
        listener.invoke(true)
    }

    /**
     * 开启安装未知来源权限
     *
     * @param activity
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun startPackageInstallPermission(activity: Activity) {
        val packageURI = Uri.parse("package:" + activity.packageName)
        // 注意这个是8.0新API
        val intent =
            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
        activity.startActivityForResult(intent, 0)
    }

    fun onInstallApkResult(requestCode: Int, resultCode: Int, listener: () -> Unit) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                permissionDialog?.dismiss()
                listener.invoke()
            }
        }
    }
}