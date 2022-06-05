package com.ych.ychbase.util

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.View
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout
import com.blankj.utilcode.util.FileUtils
import com.ych.ychbase.manager.CacheManager
import java.io.File
import java.util.*

/**
 * Created by LiMingYang on 2020/8/9. 22:43
 * Email:lmy1547124279@163.com
 *
 * 头像选择工具类
 */
object HeadUtil {

    private const val TAG: String = "HeadUtil"

    private const val RC_CHOOSE_PHOTO = 1
    private const val RC_PHOTO_PREVIEW = 2

    private var activity: Activity? = null
    private var photoView: BGASortableNinePhotoLayout? = null

    fun init(activity: Activity, photoView: BGASortableNinePhotoLayout) {
        this.activity = activity
        this.photoView = photoView
        photoView.data = null
        photoView.maxItemCount = 1
        photoView.isEditable = false
        photoView.isPlusEnable = false

        photoView.setDelegate(delegate)
    }

    private val delegate = object : BGASortableNinePhotoLayout.Delegate {
        override fun onClickNinePhotoItem(
            sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
            view: View?,
            position: Int,
            model: String?,
            models: ArrayList<String>?
        ) {
            chooseHead()
        }

        override fun onClickAddNinePhotoItem(
            sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
            view: View?,
            position: Int,
            models: ArrayList<String>?
        ) {}

        override fun onNinePhotoItemExchanged(
            sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
            fromPosition: Int,
            toPosition: Int,
            models: ArrayList<String>?
        ) {}

        override fun onClickDeleteNinePhotoItem(
            sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
            view: View?,
            position: Int,
            model: String?,
            models: ArrayList<String>?
        ) {}
    }

    fun chooseHead() {
        YchPermission.requestPhotoPermission { isGranted ->
            if (isGranted) {
                val parent = CacheManager.picturePath
                val child = "head"
                val file = File(parent, child)
                if (!file.exists()) {
                    FileUtils.createOrExistsDir(file)
                }

                val photoPickerIntent = BGAPhotoPickerActivity.IntentBuilder(activity)
                    .cameraFileDir(file) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                    .maxChooseCount(photoView!!.maxItemCount - photoView!!.itemCount) // 图片选择张数的最大值
                    .selectedPhotos(null) // 当前已选中的图片路径集合
                    .pauseOnScroll(false) // 滚动列表时是否暂停加载图片
                    .build()
                activity?.startActivityForResult(
                    photoPickerIntent,
                    RC_CHOOSE_PHOTO
                )
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, callback: (path: String) -> Unit) {
        if (resultCode == RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            photoView?.data = BGAPhotoPickerActivity.getSelectedPhotos(data)
            callback.invoke(photoView!!.data[0])
        } else if (requestCode == RC_PHOTO_PREVIEW) {
            photoView?.data = BGAPhotoPickerPreviewActivity.getSelectedPhotos(data)
        }
    }
}