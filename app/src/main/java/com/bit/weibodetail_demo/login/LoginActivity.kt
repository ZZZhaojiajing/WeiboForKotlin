package cn.jowan.logintest

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import cn.jowan.logintest.bean.LoginResponse
import cn.jowan.logintest.bean.RegisterResponse
import cn.jowan.logintest.presenter.LoginPresenter
import cn.jowan.logintest.presenter.LoginPresenterImpl
import cn.jowan.logintest.view.LoginView
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity(), View.OnClickListener, LoginView {

    var loginPresenter: LoginPresenter? = null
    var dialog: SweetAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginPresenter = LoginPresenterImpl(this)
        login.setOnClickListener(this)
        register.setOnClickListener(this)
    }

    /**
     * 点击
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.login ->
                if (checkContent(true)) {
                    viewResult.text="登录成功！"
                }
            R.id.register ->
                if (checkContent(false)) {

                }
        }
    }


    /**
     * 判断
     */
    private fun checkContent(login: Boolean): Boolean {
        username.error = null
        password.error = null
//        email.error = null

        var cancel = false
        var focusView: View? = null


        if (TextUtils.isEmpty(password.text.toString())) {
            password.error = "密码不能为空"
            focusView = password
            cancel = true
        } else if (password.text.length < 6) {
            password.error = "密码长度不能小于6位"
            focusView = password
            cancel = true
        }

        if (TextUtils.isEmpty(username.text.toString())) {
            username.error = "用户名不能为空"
            focusView = username
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null) {
                focusView.requestFocus()
            }
        } else {
            return true
        }
        return false
    }


    override fun loginSuccess(result: LoginResponse) {
        dialog?.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
        dialog?.titleText = result.msg
    }

    override fun loginFailed(message: String?) {
        dialog?.changeAlertType(SweetAlertDialog.ERROR_TYPE)
        dialog?.titleText = message
    }

    override fun registerSuccess(result: RegisterResponse) {
        dialog?.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
        dialog?.titleText = result.msg
    }

    override fun registerFailed(message: String?) {
        dialog?.changeAlertType(SweetAlertDialog.ERROR_TYPE)
        dialog?.titleText = message
    }

}

