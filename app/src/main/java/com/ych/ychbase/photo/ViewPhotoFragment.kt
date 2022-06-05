package com.ych.ychbase.photo

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.ActivityUtils
import com.ych.ychbase.R
import com.ych.ychbase.base.fragment.YchFragment
import com.ych.ychbase.util.RxBus
import com.ych.ychbase.util.loadFile
import kotlinx.android.synthetic.main.fragment_view_photo.*
import java.io.File

class ViewPhotoFragment : YchFragment() {

    companion object {
        const val KEY_IMAGE_FILE = "key_image_file"
    }

    override val layoutId: Int
        get() = R.layout.fragment_view_photo

    override val useDataBinding: Boolean
        get() = false

    override fun binding(binding: ViewDataBinding) {}

    override fun initialize(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    override fun initView() {
        super.initView()

        val photo = arguments?.getString(KEY_IMAGE_FILE)

        photo?.let {
            iv_photo.apply {
                loadFile(
                    File(it),
                    android.R.color.transparent,
                    false
                )
                RxBus.clicks(this) {
                    ActivityUtils.finishActivity(ViewPhotoActivity::class.java)
                }
            }
        }
    }
}