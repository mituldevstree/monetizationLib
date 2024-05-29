package com.monetizationlib.data.base.dependencyInjection

import androidx.fragment.app.FragmentManager
import com.monetizationlib.data.base.dependencyInjection.navigation.FragmentNavigator

/**
 * @author Teodor N. Dimitrov on 3/10/2019.
 */

object CompositionRoot {
    private var fragmentNavigator: FragmentNavigator? = null

    fun getFragmentNavigator(fragmentManager: FragmentManager): FragmentNavigator {
        var fragmentNavigator = fragmentNavigator

        if (fragmentNavigator == null) {
            fragmentNavigator = FragmentNavigator(fragmentManager = fragmentManager)
        }

        return fragmentNavigator
    }
}