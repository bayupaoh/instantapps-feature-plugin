package com.suitmedia.sample.base.ui

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.ActionBar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Toast
import com.suitmedia.sample.core.R
import com.suitmedia.sample.base.presenter.MvpView
import com.suitmedia.sample.base.ui.recyclerview.BaseRecyclerView
import com.suitmedia.sample.helper.CommonLoadingDialog


abstract class BaseActivity : AppCompatActivity(), MvpView {

    private var mContext: Context
        get() = this
        set(value) = TODO()

    var toolbar: Toolbar? = null
        protected set

    private var mInflater: LayoutInflater? = null
    private var mActionBar: ActionBar? = null
    private var mCommonLoadingDialog: CommonLoadingDialog? = null

    private val baseFragmentManager: FragmentManager
        get() = super.getSupportFragmentManager()

    protected abstract val resourceLayout: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(resourceLayout)
        mInflater = LayoutInflater.from(mContext)
        onViewReady(savedInstanceState)
    }

    protected fun setupToolbar(baseToolbar: Toolbar, needHomeButton: Boolean) {
        baseToolbar.title = ""
        setupToolbar(baseToolbar, needHomeButton, null)
    }

    protected fun setupToolbarWithTitle(baseToolbar: Toolbar, title: String, needHomeButton: Boolean) {
        baseToolbar.title = title
        setupToolbar(baseToolbar, needHomeButton, null)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupToolbar(baseToolbar: Toolbar, needHomeButton: Boolean, onClickListener: View.OnClickListener?) {

        toolbar = baseToolbar
        setSupportActionBar(baseToolbar)
        mActionBar = supportActionBar
        if (mActionBar != null) {
            mActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(needHomeButton)
            supportActionBar!!.setDisplayShowTitleEnabled(true)
        }

        if (onClickListener != null)
            baseToolbar.setNavigationOnClickListener(onClickListener)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected fun setupToolbarWithoutBackButton(toolbar: Toolbar) {

        this.toolbar = toolbar
        setSupportActionBar(toolbar)
        mActionBar = supportActionBar
        if (mActionBar != null) {
            mActionBar!!.setHomeButtonEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setTitle(title: Int) {
        super.setTitle(title)
        if (mActionBar != null)
            mActionBar!!.title = getString(title)
    }

    override fun onBackPressed() {
        if (baseFragmentManager.backStackEntryCount > 0) {
            baseFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    protected fun showToast(message: String) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
    }

    protected abstract fun onViewReady(savedInstanceState: Bundle?)

    override fun showLoading(isBackPressedCancelable: Boolean, message: String?, currentPage: Int?) {
        if(currentPage == 1) {
            mCommonLoadingDialog?.let {
                hideLoading()
            }
            mCommonLoadingDialog = CommonLoadingDialog.createLoaderDialog(msg = message)
            mCommonLoadingDialog?.show(supportFragmentManager, CommonLoadingDialog.TAG)
        }
    }

    override fun showLoadingWithText(msg: String) {
        showLoading(message = msg)
    }

    override fun showLoadingWithText(msg: Int) {
        showLoading(message = getString(msg))
    }

    override fun hideLoading() {
        mCommonLoadingDialog?.dismiss()
    }

    override fun showConfirmationDialog(message: String, confirmCallback: () -> Unit) {
        val confirmDialog = AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.txt_dialog_yes) { _, _ -> confirmCallback() }
                .setNegativeButton(R.string.txt_dialog_no) { _, _ -> }
                .create()

        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        confirmDialog.show()
    }

    override fun showConfirmationSingleDialog(message: String, confirmCallback: () -> Unit) {
        val confirmDialog = AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.txt_dialog_yes) { _, _ -> confirmCallback() }
                .create()

        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        confirmDialog.show()
    }

    override fun showConfirmationDialog(message: Int, confirmCallback: () -> Unit) {
        val stringMessage = getString(message)
        showConfirmationDialog(stringMessage, confirmCallback)
    }

    override fun showAlertDialog(message: String) {
        val confirmDialog = AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.txt_dialog_ok) { d, _ -> d.dismiss() }
                .create()

        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        confirmDialog.show()
    }

    override fun showAlertDialog(message: Int) {
        val stringMessage = getString(message)
        showAlertDialog(stringMessage)
    }

    fun finishLoad(recycler: BaseRecyclerView?){
        recycler?.let {
            it.refreshComplete()
            it.loadMoreComplete()
        }
    }

    fun showEmptyState(value: Boolean, list: View?, emptyView: View?){
        when(value){
            false -> {
                list?.visibility = View.VISIBLE
                emptyView?.visibility = View.GONE
            }
            true -> {
                list?.visibility = View.GONE
                emptyView?.visibility = View.VISIBLE
            }
        }
    }

    fun clearRecyclerView(recyclerView: BaseRecyclerView?){
        recyclerView?.destroy()
    }

}