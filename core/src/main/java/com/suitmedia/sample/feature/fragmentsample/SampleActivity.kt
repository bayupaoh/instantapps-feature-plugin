package com.suitmedia.sample.feature.fragmentsample

import android.os.Bundle
import android.support.v4.app.Fragment
import com.suitmedia.sample.core.R
import com.suitmedia.sample.base.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_test.*

/**
 * Created by dodydmw19 on 7/30/18.
 */

class SampleActivity : BaseActivity(){

    private var mCurrentFragment: Fragment? = null

    override val resourceLayout: Int
        get() = R.layout.activity_test

    override fun onViewReady(savedInstanceState: Bundle?) {
        setupToolbar(mToolbar, true)
        setContentFragment(SampleFragment.newInstance())
    }

    private fun setContentFragment(fragment: Fragment?) {
        mCurrentFragment = fragment
        if (mCurrentFragment != null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, mCurrentFragment!!)
                    .commitAllowingStateLoss()
        }
    }
}