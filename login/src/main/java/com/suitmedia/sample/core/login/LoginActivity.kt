package com.suitmedia.sample.core.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.suitmedia.sample.core.login.R
import com.suitmedia.sample.core.R as Rbase
import com.suitmedia.sample.base.ui.BaseActivity
import com.suitmedia.sample.core.member.MemberActivity
import com.suitmedia.sample.helper.CommonConstant
import com.suitmedia.sample.helper.socialauth.facebook.FacebookListener
import com.suitmedia.sample.helper.socialauth.twitter.TwitterListener
import com.suitmedia.sample.helper.socialauth.google.GoogleListener
import com.suitmedia.sample.helper.CommonUtils
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by dodydmw19 on 7/18/18.
 */

class LoginActivity : BaseActivity(), LoginView, GoogleListener, FacebookListener, TwitterListener {

    private var loginPresenter: LoginPresenter? = null

//    private var mGoogleHelper: GoogleSignInHelper? = null
//    private var mTwitterHelper: TwitterHelper? = null
//    private var mFbHelper: FacebookHelper? = null

    override val resourceLayout: Int = R.layout.activity_login

    override fun onViewReady(savedInstanceState: Bundle?) {
        setupPresenter()
        setupSocialLogin()
        actionClicked()
    }

    private fun setupPresenter() {
        loginPresenter = LoginPresenter(this)
        loginPresenter?.attachView(this)
    }

    private fun setupSocialLogin() {
        // Google  initialization
//        mGoogleHelper = GoogleSignInHelper(this, R.string.default_web_client_id, this)

//         twitter initialization
//        mTwitterHelper = TwitterHelper(
//                R.string.twitter_api_key,
//                R.string.twitter_secret_key,
//                this,
//                this)

        // fb initialization
//        mFbHelper = FacebookHelper(this, getString(R.string.facebook_request_field))

        signOut()
    }

    private fun signOut() {
//        mGoogleHelper?.performSignOut()
//        mFbHelper?.performSignOut()
    }

    override fun onLoginSuccess(message: String?) {
        val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse(CommonConstant.INTENT_MEMBER_URL))
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onLoginFailed(message: String?) {
        message?.let {
            showToast(message.toString())
        }
    }

    override fun onGoogleAuthSignIn(authToken: String?, userId: String?) {
        // send token & user_id to server
        loginPresenter?.login()
    }

    override fun onGoogleAuthSignInFailed(errorMessage: String?) {
        showToast(errorMessage.toString())
    }

    override fun onFbSignInFail(errorMessage: String?) {
        showToast(errorMessage.toString())
    }

    override fun onFbSignInSuccess(authToken: String?, userId: String?) {
        // send token & user_id to server
        loginPresenter?.login()
    }

    override fun onTwitterError(errorMessage: String?) {
        showToast(errorMessage.toString())
    }

    override fun onTwitterSignIn(authToken: String?, secret: String?, userId: String?) {
        // send token & user_id to server
        loginPresenter?.login()
    }

    private fun actionClicked() {
        relGoogle.setOnClickListener {
//            mGoogleHelper?.performSignIn(this)
        }

        relFacebook.setOnClickListener {
//            mFbHelper?.performSignIn(this)
        }

        relTwitter.setOnClickListener {
            if (CommonUtils.checkTwitterApp(this)) {
//                mTwitterHelper?.performSignIn()
            } else {
                showToast(getString(Rbase.string.txt_twitter_not_installed))
            }
        }

        tvSkip.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                    Uri.parse(CommonConstant.INTENT_MEMBER_URL))
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (this.applicationContext != null) {
                intent.`package` = applicationContext.packageName
            }
            startActivity(intent)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
//            mGoogleHelper?.onActivityResult(requestCode, resultCode, data)
//            mTwitterHelper?.onActivityResult(requestCode, resultCode, data)
//            mFbHelper?.onActivityResult(requestCode, resultCode, data)
        }
    }

}