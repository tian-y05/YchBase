package com.ych.ychbase.photo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPhotoAdapter constructor(
    fm: FragmentManager,
    list: ArrayList<ViewPhotoFragment>
) : FragmentStatePagerAdapter(fm) {

    private val fragmentList = list

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getCount(): Int = fragmentList.size
}