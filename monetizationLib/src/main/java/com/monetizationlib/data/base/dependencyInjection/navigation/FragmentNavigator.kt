package com.monetizationlib.data.base.dependencyInjection.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * @author Teodor N. Dimitrov on 3/10/2019.
 */

class FragmentNavigator(private val fragmentManager: FragmentManager) {
    fun toFragment(
        fragment: Fragment,
        frameId: Int,
        addToBackStack: Boolean = true,
        fragmentManager: FragmentManager = this.fragmentManager
    ) {
        val simpleName = fragment::class.java.simpleName

        fragmentManager.inTransaction({
            replace(frameId, fragment, simpleName)
        }, addToBackStack, simpleName)
    }
}

inline fun FragmentManager.inTransaction(
    func: FragmentTransaction.() -> Unit,
    addToBackStack: Boolean = false,
    tag: String? = null
) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    if (addToBackStack) fragmentTransaction.addToBackStack(tag)
    fragmentTransaction.commitAllowingStateLoss()
}