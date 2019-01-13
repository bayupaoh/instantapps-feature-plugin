package com.suitmedia.sample.core.member

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.jcodecraeer.xrecyclerview.XRecyclerView
import com.suitmedia.sample.core.member.R
import com.suitmedia.sample.core.R as Rbase
import com.suitmedia.sample.base.ui.BaseActivity
import com.suitmedia.sample.data.api.model.User
import com.suitmedia.sample.helper.CommonConstant
import kotlinx.android.synthetic.main.activity_member.*
import kotlinx.android.synthetic.main.layout_empty_member_shimmer.*

/**
 * Created by DODYDMW19 on 1/30/2018.
 */

class MemberActivity : BaseActivity(),
        MemberView,
        XRecyclerView.LoadingListener,
        MemberItemView.OnActionListener {


    private var memberPresenter: MemberPresenter? = null
    private var currentPage: Int = 1
    private var adapter: MemberAdapter? = null

    override val resourceLayout: Int = R.layout.activity_member

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MemberActivity::class.java)
        }
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        setupToolbarWithTitle(mToolbar, getString(Rbase.string.app_name), false)
        setupPresenter()
        actionClicked()
    }

    override fun onPause() {
        super.onPause()
        if (shimmerContainer != null && shimmerContainer.isShimmerStarted) {
            shimmerContainer.stopShimmer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearRecyclerView(memberList)
    }

    private fun setupPresenter() {
        memberPresenter = MemberPresenter(this, shimmerContainer)
        memberPresenter?.attachView(this)
        memberPresenter?.getMember(currentPage)
    }

    private fun initProduct() {
        adapter = MemberAdapter()
        adapter?.setOnActionListener(this)

        memberList.setUpAsList()

        memberList.adapter = adapter
        memberList.setPullRefreshEnabled(true)
        memberList.setLoadingMoreEnabled(true)
        memberList.setLoadingListener(this)
    }

    private fun setData(data: List<User>?) {
        if (currentPage == 1) {
            adapter.let {
                adapter?.clear()
            }
        }
        adapter?.add(data!!)
        showEmptyState(false, memberList, emptyState)
    }

    override fun onMemberCacheLoaded(members: List<User>?) {
        members.let {
            if (currentPage == 1) {
                initProduct()
            }
            //shimmer.visibility = View.GONE
            if (members?.isNotEmpty()!!) setData(members)
        }
        finishLoad(memberList)
    }

    override fun onMemberLoaded(members: List<User>?) {
        members.let {
            if (currentPage == 1) {
                initProduct()
            }
            if (members?.isNotEmpty()!!) setData(members)
        }
        finishLoad(memberList)
    }

    override fun onMemberEmpty() {
        showEmptyState(true, memberList, emptyState)
    }

    override fun onFailed(message: String?) {
        finishLoad(memberList)
        showEmptyState(true, memberList, emptyState)
        showToast(message!!)
    }

    override fun onLoadMore() {
        currentPage++
        memberPresenter?.getMember(currentPage)
    }

    override fun onRefresh() {
        currentPage = 1
        memberPresenter?.getMember(currentPage)
    }

    override fun onClicked(view: MemberItemView?) {
        view?.let { it ->
            showToast(it.getData().firstName!!)
        }
    }

    private fun actionClicked() {
        tvFragmentSample.setOnClickListener {
            val intent = Intent().setClassName(CommonConstant.BASE_PACKAGE, CommonConstant.INTENT_FRAGMENT_SAMPLE)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

}