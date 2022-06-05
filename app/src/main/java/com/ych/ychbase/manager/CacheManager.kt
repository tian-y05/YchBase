package com.ych.ychbase.manager

import com.blankj.utilcode.util.SPUtils

/**
 * 应用缓存管理类
 *
 * @author lmy
 */
object CacheManager {
    private const val KEY_USER_ID = "key_user_id"
    private const val KEY_PHONE = "key_phone"
    private const val KEY_PASSWORD = "key_password"
    private const val KEY_SHIP_NAME = "key_ship_name"
    private const val KEY_SUM_SAILINGS = "key_sum_sailings"
    private const val KEY_SUM_MILEAGE = "key_sum_mileage"
    private const val KEY_HEAD_URL = "key_head_url"
    private const val KEY_NAME = "key_name"

    private const val KEY_CAR_NAME = "key_car_name"
    private const val KEY_TRACTOR_ID = "key_tractor_id"

    private const val KEY_ROOT_PATH = "KEY_ROOT_PATH"
    private const val KEY_PICTURE_PATH = "key_path"
    private const val KEY_DOWNLOAD_PATH = "key_download_path"
    private const val KEY_AUTHORITY = "key_authority"

    private const val KEY_IS_SELF_OPERATION = "KEY_IS_SELF_OPERATION"

    /** 用户id **/
    var userId: Long
        set(value) {
            SPUtils.getInstance().put(KEY_USER_ID, value)
        }
        get() = SPUtils.getInstance().getLong(KEY_USER_ID)

    /** 手机号 **/
    var phone: String
        set(value) {
            SPUtils.getInstance().put(KEY_PHONE, value)
        }
        get() = SPUtils.getInstance().getString(KEY_PHONE, "")

    /** 密码 **/
    var password: String
        set(value) {
            SPUtils.getInstance().put(KEY_PASSWORD, value)
        }
        get() = SPUtils.getInstance().getString(KEY_PASSWORD)

    /** 船名 **/
    var shipName: String
        set(value) {
            SPUtils.getInstance().put(KEY_SHIP_NAME, value)
        }
        get() = SPUtils.getInstance().getString(KEY_SHIP_NAME, "")

    /** 头像路径 **/
    var headUrl: String
        set(value) {
            SPUtils.getInstance().put(KEY_HEAD_URL, value)
        }
        get() = SPUtils.getInstance().getString(KEY_HEAD_URL, "")

    /** 船长名 **/
    var name: String
        set(value) {
            SPUtils.getInstance().put(KEY_NAME, value)
        }
        get() = SPUtils.getInstance().getString(KEY_NAME)

    /** 总航程 **/
    var sumSailings: Int
        set(value) {
            SPUtils.getInstance().put(KEY_SUM_SAILINGS, value)
        }
        get() = SPUtils.getInstance().getInt(KEY_SUM_SAILINGS)

    /** 总里程 **/
    var sumMileage: Float
        set(value) {
            SPUtils.getInstance().put(KEY_SUM_MILEAGE, value)
        }
        get() = SPUtils.getInstance().getFloat(KEY_SUM_MILEAGE)

    /** 是否为自营司机 **/
    var isSelfOperation: Int
        set(value) {
            SPUtils.getInstance().put(KEY_IS_SELF_OPERATION, value)
        }
        get() = SPUtils.getInstance().getInt(KEY_IS_SELF_OPERATION, 1)

    /** 车牌号 **/
    var car: String
        set(value) {
            SPUtils.getInstance().put(KEY_CAR_NAME, value)
        }
        get() = SPUtils.getInstance().getString(KEY_CAR_NAME)

    /** 牵引车id **/
    var tractorId: Long
        set(value) {
            SPUtils.getInstance().put(KEY_TRACTOR_ID, value)
        }
        get() = SPUtils.getInstance().getLong(KEY_TRACTOR_ID)

    /** app根路径 **/
    var appRootPath: String
        set(value) {
            SPUtils.getInstance().put(KEY_ROOT_PATH, value)
        }
        get() = SPUtils.getInstance().getString(KEY_ROOT_PATH, "")

    /** app图片路径 **/
    var picturePath: String
        set(value) {
            SPUtils.getInstance().put(KEY_PICTURE_PATH, value)
        }
        get() = SPUtils.getInstance().getString(KEY_PICTURE_PATH, "")

    /** app下载路径 **/
    var downloadPath: String
        set(value) {
            SPUtils.getInstance().put(KEY_DOWNLOAD_PATH, value)
        }
        get() = SPUtils.getInstance().getString(KEY_DOWNLOAD_PATH)

    /** fileProvider **/
    var authority: String
        set(value) {
            SPUtils.getInstance().put(KEY_AUTHORITY, value)
        }
        get() = SPUtils.getInstance().getString(KEY_AUTHORITY, "")

    private const val KEY_IS_ALIAS_SET_SUCCESS = "key_is_alias_set_success"
    /** 记录别名是否设置成功 **/
    var isAliasSetSuccess: Boolean
        set(value) {
            SPUtils.getInstance().put(KEY_IS_ALIAS_SET_SUCCESS, value)
        }
        get() = SPUtils.getInstance().getBoolean(KEY_IS_ALIAS_SET_SUCCESS, false)

    /** 清除缓存 **/
    fun clearCache() {
        SPUtils.getInstance().remove(KEY_USER_ID)
        SPUtils.getInstance().remove(KEY_PHONE)
        SPUtils.getInstance().remove(KEY_PASSWORD)
        SPUtils.getInstance().remove(KEY_SHIP_NAME)
        SPUtils.getInstance().remove(KEY_HEAD_URL)
        SPUtils.getInstance().remove(KEY_NAME)

        SPUtils.getInstance().remove(KEY_IS_SELF_OPERATION)
        SPUtils.getInstance().remove(KEY_CAR_NAME)
        SPUtils.getInstance().remove(KEY_TRACTOR_ID)

        SPUtils.getInstance().remove(KEY_ROOT_PATH)
        SPUtils.getInstance().remove(KEY_PICTURE_PATH)
        SPUtils.getInstance().remove(KEY_DOWNLOAD_PATH)
        SPUtils.getInstance().remove(KEY_AUTHORITY)

        SPUtils.getInstance().remove(KEY_IS_ALIAS_SET_SUCCESS)
    }
}