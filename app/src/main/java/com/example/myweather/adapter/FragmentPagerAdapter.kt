package com.example.myweather.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myweather.view.HomeFragment

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/2/27
 */
class FragmentPagerAdapter(_fragmentActivity: FragmentActivity, _fragments: List<HomeFragment>) :
    FragmentStateAdapter(_fragmentActivity) {

    private val fragments: List<HomeFragment> = _fragments

    override fun getItemCount(): Int =fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]


    override fun containsItem(itemId: Long): Boolean = false
}