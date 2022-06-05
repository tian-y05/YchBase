package com.ych.ychbase.photo

import android.graphics.Color
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.viewpager.widget.ViewPager
import com.ych.ychbase.R
import com.ych.ychbase.base.activity.YchActivity
import kotlinx.android.synthetic.main.activity_view_photo.*

/**
 * 查看照片
 *
 * @author lmy
 */
class ViewPhotoActivity : YchActivity() {

    companion object {
        const val KEY_PHOTO_LIST = "key_photo_list"
        const val KEY_CURRENT_ITEM = "key_current_item"
    }

    @Suppress("UNCHECKED_CAST")
    private val photoList by lazy {
        intent.getSerializableExtra(KEY_PHOTO_LIST) as List<Photo>
    }

    private val currentItem by lazy {
        intent.getIntExtra(KEY_CURRENT_ITEM, 0)
    }

    private val fragmentList = ArrayList<ViewPhotoFragment>()

    override val layoutId: Int
        get() = R.layout.activity_view_photo

    override val useDataBinding: Boolean
        get() = false

    override fun binding(binding: ViewDataBinding) {}

    override fun initialize() {
        initView()
    }

    override fun initView() {
        super.initView()
        photoList.forEach {
            val file = it.file
            val fragment = ViewPhotoFragment()
            val bundle = Bundle()
            bundle.putString(
                ViewPhotoFragment.KEY_IMAGE_FILE,
                file.absolutePath
            )
            fragment.arguments = bundle
            fragmentList.add(fragment)
        }

        val navigator = ScaleCircleNavigator(this)
        navigator.setCircleCount(fragmentList.size)
        navigator.setNormalCircleColor(Color.LTGRAY)
        navigator.setSelectedCircleColor(Color.DKGRAY)
        navigator.setCircleClickListener { index ->
            vp_photo.currentItem = index
        }
        indicator.navigator = navigator

        vp_photo.apply {
            adapter = ViewPhotoAdapter(
                supportFragmentManager,
                fragmentList
            )
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                    indicator.onPageScrollStateChanged(state)
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    indicator.onPageScrolled(
                        position,
                        positionOffset,
                        positionOffsetPixels
                    )
                }

                override fun onPageSelected(position: Int) {
                    indicator.onPageSelected(position)
                }

            })
        }

        vp_photo.currentItem = currentItem
    }
}