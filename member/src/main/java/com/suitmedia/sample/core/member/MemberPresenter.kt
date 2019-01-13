package com.suitmedia.sample.core.member

import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.view.View
import com.facebook.shimmer.ShimmerFrameLayout
import com.suitmedia.sample.BaseApplication
import com.suitmedia.sample.core.R
import com.suitmedia.sample.base.presenter.BasePresenter
import com.suitmedia.sample.core.member.di.DaggerMemberComponent
import com.suitmedia.sample.data.api.APIService
//import com.suitmedia.sample.data.localdb.RealmHelper
import com.suitmedia.sample.data.prefs.DataConstant
import com.suitmedia.sample.data.prefs.SuitPreferences
import com.suitmedia.sample.helper.AppStatus
import com.suitmedia.sample.data.api.model.User
import com.suitmedia.sample.di.module.ContextModule
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by DODYDMW19 on 1/30/2018.
 */

class MemberPresenter(
        var context: Context,
        var shimmerContainer: ShimmerFrameLayout? = null) : BasePresenter<MemberView> {

    @Inject
    lateinit var apiService: APIService
    private var mvpView: MemberView? = null
    private var mCompositeDisposable: CompositeDisposable? = CompositeDisposable()

    init {
        DaggerMemberComponent.builder().contextModule(ContextModule(context)).build().inject(this)
    }


    fun getMember(currentPage: Int?) {
        mCompositeDisposable?.add(
                apiService.getMembers(10, currentPage!!)
                        .map {
                            saveToCache(it.arrayData, currentPage)
                            it
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe {
                            shimmerContainer?.startShimmer()
                        }
                        .doOnComplete {
                            shimmerContainer?.stopShimmer()
                            shimmerContainer?.visibility = View.GONE
                        }
                        .subscribe({ data ->
                            if (data != null) {
                                if (currentPage == 1) {
                                    if (data.arrayData?.isNotEmpty()!!) {
                                        mvpView?.onMemberLoaded(data.arrayData!!)
                                    } else {
                                        mvpView?.onMemberEmpty()
                                    }
                                } else {
                                    mvpView?.onMemberLoaded(data.arrayData!!)
                                }
                            } else {
                                mvpView?.onFailed(context.getString(R.string.txt_error_global))
                            }
                        }, { it ->
                            if (AppStatus.isOnline(context)) {
                                it?.message?.let { mvpView?.onFailed(it) }
                            } else {
                                mvpView?.onFailed(context.getString(R.string.txt_error_no_internet_connection))
                            }
                        })
        )
    }

    private fun saveToCache(data: List<User>?, currentPage: Int?) {
        if (data != null && data.isNotEmpty()) {
            if (currentPage == 1) {
                // remove current realm data
//                mRealm?.deleteData(User())
                // save array object to preference for pre view data loaded
                SuitPreferences.instance()?.saveObjectsList(DataConstant.KEY_MEMBER_CACHE, data)
            }

            // save to realm
//            mRealm?.saveList(data)
        }
    }

    override fun onStart() {
    }

    override fun onPause() {
        detachView()
    }

    override fun onDestroy() {
        detachView()
    }

    override fun attachView(view: MemberView) {
        mvpView = view
        // Initialize this presenter as a lifecycle-aware when a view is a lifecycle owner.
        if (mvpView is LifecycleOwner) {
            (mvpView as LifecycleOwner).lifecycle.addObserver(this)
        }
    }

    override fun detachView() {
        mvpView = null
        mCompositeDisposable.let { mCompositeDisposable?.clear() }

        if (shimmerContainer != null && shimmerContainer?.isShimmerStarted!!) {
            shimmerContainer?.stopShimmer()
        }

    }
}