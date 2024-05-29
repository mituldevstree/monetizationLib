package com.monetizationlib.data.base.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.monetizationlib.data.base.dependencyInjection.CompositionRoot
import com.monetizationlib.data.base.dependencyInjection.navigation.FragmentNavigator

abstract class BaseFragment<VDB : ViewDataBinding> : Fragment() {
    open fun onBackPressed(): Boolean = false

    protected var fragmentNavigator: FragmentNavigator? = null

    protected lateinit var binding: VDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentNavigator =
            activity?.supportFragmentManager?.let { CompositionRoot.getFragmentNavigator(it) }
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        //No call for super(). Bug on API Level > 11.
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
        if (container != null) {
            binding = inflateDataBinding(inflater, container)
        } else {
            return null
        }

        return binding.root
    }

    protected abstract fun inflateDataBinding(inflater: LayoutInflater, container: ViewGroup): VDB

}