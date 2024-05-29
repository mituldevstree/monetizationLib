package com.monetizationlib.data.base.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
class ViewPagerAdapter(parentFragment: FragmentManager, lifecycle: Lifecycle, private var fragmentList: ArrayList<Fragment>) :
    FragmentStateAdapter(parentFragment,lifecycle) {
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    fun getFragmentListSize(): Int {
        return fragmentList.size
    }
}