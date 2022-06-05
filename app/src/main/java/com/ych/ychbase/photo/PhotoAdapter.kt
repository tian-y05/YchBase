package com.ych.ychbase.photo

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.blankj.utilcode.util.ActivityUtils
import com.ych.ychbase.R
import com.ych.ychbase.app.context
import com.ych.ychbase.manager.CacheManager
import com.ych.ychbase.util.YchPermission
import java.io.File

class PhotoAdapter(recyclerView: RecyclerView, list: ArrayList<String>) :
    BGARecyclerViewAdapter<String>(recyclerView, R.layout.item_watch_photo) {

    private val photoList = list

    override fun fillData(helper: BGAViewHolderHelper?, position: Int, model: String?) {
        val photos: BGANinePhotoLayout? = helper?.getView(R.id.photos)
        photos?.setDelegate(delegate)
        photos?.data = photoList
    }

    private val delegate = object : BGANinePhotoLayout.Delegate {
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
                            photoPreviewIntentBuilder.previewPhoto(it.currentClickItem)
                        }
                        if (it.itemCount > 1) {
                            photoPreviewIntentBuilder.previewPhotos(it.data)
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
}