package com.ych.ychbase.util

import android.app.Activity
import android.content.Intent
import android.view.View
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.StringUtils
import com.ych.ychbase.app.context
import com.ych.ychbase.manager.CacheManager
import java.io.File

/**
 * Created by LiMingYang on 2020/8/2. 12:40
 * Email:lmy1547124279@163.com
 */
object PhotoUtil {

    /** oss缩略图路径后缀 **/
    const val OSS_URL_SUFFIX = "?x-oss-process=image/resize,m_fill,h_105,w_105"

    private const val RC_CHOOSE_PHOTO = 1
    private const val RC_PHOTO_PREVIEW = 2

    private var activity: Activity? = null
    private var addPhotoView: BGASortableNinePhotoLayout? = null
    private var watchPhotoView: BGANinePhotoLayout? = null
    private var onItemChange: (data:String) -> Unit = {}

    fun addInit(activity: Activity, photoView: BGASortableNinePhotoLayout) {
        this.activity = activity
        this.addPhotoView = photoView

        this.addPhotoView?.setDelegate(addDelegate)
    }

    fun addInit(
        activity: Activity,
        photoView: BGASortableNinePhotoLayout,
        data: String,
        onItemChange: (data: String) -> Unit
    ) {
        this.activity = activity
        this.addPhotoView = photoView
        this.onItemChange = onItemChange
        this.addPhotoView?.setDelegate(addDelegate)

        val list = ArrayList<String>()
        if (!StringUtils.isEmpty(data)){
            if (data.contains(",")) {
                val arr = data.split(",")
                arr.forEach { url ->
                    list.add(url)
                }
            } else {
                list.add(data)
            }
        }
        addPhotoView?.data = list
    }

    private val addDelegate = object : BGASortableNinePhotoLayout.Delegate {

        override fun onClickAddNinePhotoItem(
            sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
            view: View?,
            position: Int,
            models: ArrayList<String>?
        ) {
            YchPermission.requestPhotoPermission { isGranted ->
                if (isGranted) {
                    val parent = CacheManager.picturePath
                    val child = "photo"
                    val file = File(parent, child)

                    if (!file.exists()) {
                        FileUtils.createOrExistsDir(file)
                    }

                    val photoPickerIntent = BGAPhotoPickerActivity.IntentBuilder(activity)
                        .cameraFileDir(file) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                        .maxChooseCount(addPhotoView!!.maxItemCount - addPhotoView!!.itemCount) // 图片选择张数的最大值
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

        override fun onClickDeleteNinePhotoItem(
            sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
            view: View?,
            position: Int,
            model: String?,
            models: ArrayList<String>?
        ) {
            addPhotoView?.removeItem(position)
            onItemChange(photoBuilder)
        }

        override fun onClickNinePhotoItem(
            sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
            view: View?,
            position: Int,
            model: String?,
            models: ArrayList<String>?
        ) {
            val photoPickerPreviewIntent = BGAPhotoPickerPreviewActivity.IntentBuilder(activity)
                .previewPhotos(models) // 当前预览的图片路径集合
                .selectedPhotos(models) // 当前已选中的图片路径集合
                .maxChooseCount(addPhotoView!!.maxItemCount) // 图片选择张数的最大值
                .currentPosition(position) // 当前预览图片的索引
                .isFromTakePhoto(false) // 是否是拍完照后跳转过来
                .build()
            activity?.startActivityForResult(
                photoPickerPreviewIntent,
                RC_PHOTO_PREVIEW
            )
        }

        override fun onNinePhotoItemExchanged(
            sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
            fromPosition: Int,
            toPosition: Int,
            models: ArrayList<String>?
        ) {
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            addPhotoView?.addMoreData(BGAPhotoPickerActivity.getSelectedPhotos(data))
            onItemChange(photoBuilder)
        } else if (requestCode == RC_PHOTO_PREVIEW) {
            addPhotoView?.data = BGAPhotoPickerActivity.getSelectedPhotos(data)
        }
    }

    fun watchInit(photoView: BGANinePhotoLayout, photos: String?) {
        photos?.let {
            watchPhotoView = photoView

            watchPhotoView?.setDelegate(watchDelegate)
            val list = ArrayList<String>()
            if (it.contains(",")) {
                val arr = it.split(",")
                arr.forEach { url ->
                    list.add("$url$OSS_URL_SUFFIX")
                }
            } else {
                list.add("$it$OSS_URL_SUFFIX")
            }
            watchPhotoView?.data = list
        }
    }

    private val watchDelegate = object : BGANinePhotoLayout.Delegate {
        override fun onClickNinePhotoItem(
            ninePhotoLayout: BGANinePhotoLayout?,
            view: View?,
            position: Int,
            model: String?,
            models: MutableList<String>?
        ) {
            YchPermission.requestReadWritePermission { isGranted ->
                if (isGranted) {
                    val file = File(CacheManager.picturePath, "download")
                    val photoPreviewIntentBuilder =
                        BGAPhotoPreviewActivity.IntentBuilder(context)
                    photoPreviewIntentBuilder.saveImgDir(file)

                    ninePhotoLayout?.let {
                        if (it.itemCount == 1) {
                            photoPreviewIntentBuilder.previewPhoto(
                                it.currentClickItem.replace(
                                    OSS_URL_SUFFIX,
                                    ""
                                )
                            )
                        }
                        if (it.itemCount > 1) {
                            val list = ArrayList<String>()
                            it.data.forEach { url ->
                                list.add(url.replace(OSS_URL_SUFFIX, ""))
                            }
                            photoPreviewIntentBuilder.previewPhotos(list)
                                .currentPosition(it.currentClickItemPosition)
                        }
                    }

                    ActivityUtils.startActivity(photoPreviewIntentBuilder.build())
                }
            }
        }

        override fun onClickExpand(
            ninePhotoLayout: BGANinePhotoLayout?,
            view: View?,
            position: Int,
            model: String?,
            models: MutableList<String>?
        ) {
            ninePhotoLayout?.setIsExpand(true)
            ninePhotoLayout?.flushItems()
        }
    }

    val photoBuilder: String
        get() {
            val list = addPhotoView!!.data
            val it = list.iterator()
            val sb = StringBuilder()
            while (it.hasNext()) {
                sb.append(it.next())
                if (it.hasNext()) {
                    sb.append(",")
                }
            }
            return sb.toString()
        }
}