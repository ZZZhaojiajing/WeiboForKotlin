package cn.jowan.logintest


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import cn.jowan.logintest.bean.LoginResponse
import cn.jowan.logintest.bean.RegisterResponse
import cn.jowan.logintest.presenter.LoginPresenter
import cn.jowan.logintest.presenter.LoginPresenterImpl
import cn.jowan.logintest.view.LoginView
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity(), View.OnClickListener, LoginView {

    private val IMAGE_REQUEST_CODE = 0
    private val CAMERA_REQUEST_CODE = 1
    private val RESIZE_REQUEST_CODE = 2

    private val IMAGE_FILE_NAME = "flower.jpg"

    private var mImageHeader: ImageView? = null





    var loginPresenter: LoginPresenter? = null
    var dialog: SweetAlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loginPresenter = LoginPresenterImpl(this)
        reset.setOnClickListener(this)
        register.setOnClickListener(this)
        btn_selectimage.setOnClickListener(this)
        btn_takephoto.setOnClickListener(this)

        setupViews()
    }


    /**
     * 点击
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.reset ->
                if (checkContent(true)) {
                    viewResult.text="重置成功！"
//                    dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
//                            .setTitleText("正在登录...")
//                    dialog?.setCancelable(false)
//                    dialog?.show()
//                    loginPresenter?.login(username.text.toString(), password.text.toString())
                }
            R.id.register ->
                if (checkContent(false)) {
                    viewResult.text="注册成功！"
//                    dialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE).setTitleText("正在注册...")
//                    dialog?.setCancelable(false)
//                    dialog?.show()
//                    loginPresenter?.register(username.text.toString(), password.text.toString(), email.text.toString())
//
                }


            R.id.btn_selectimage -> {
                val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)
                galleryIntent.type = "image/*" //图片
                startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE)
            }
            R.id.btn_takephoto -> if (isSdcardExisting()) {
                val cameraIntent = Intent(
                        "android.media.action.IMAGE_CAPTURE") //拍照
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri())
                cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0)
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
            } else {
                Toast.makeText(v.context, "请插入sd卡", Toast.LENGTH_LONG)
                        .show()
            }
        }
    }

    /**
     * 判断
     */
    private fun checkContent(login: Boolean): Boolean {
        username.error = null
        password.error = null
        confirmpassword.error=null
        email.error = null

        var cancel = false
        var focusView: View? = null


        if (!login) {
            if (TextUtils.isEmpty(email.text.toString())) {
                email.error = "Email不能为空"
                focusView = email
                cancel = true
            } else if (!isEmail(email.text.toString())) {
                email.error = "Email格式不正确"
                focusView = email
                cancel = true
            }
        }

        if (TextUtils.isEmpty(password.text.toString())) {
            password.error = "密码不能为空"
            focusView = password
            cancel = true
        } else if (password.text.length < 6) {
            password.error = "密码长度不能小于6位"
            focusView = password
            cancel = true
        }

        if (!password.text.toString().equals(confirmpassword.text.toString())){
            confirmpassword.error="两次密码输入不一致"
            focusView=confirmpassword
            cancel=true
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

    /**
     * 判断email地址
     * @param email
     * @return
     */
    fun isEmail(email: String?): Boolean {
        if (email == null) {
            return false
        }
        val regex = "\\w+(\\.\\w)*@\\w+(\\.\\w{2,3}){1,3}"
        val pattern = Pattern.compile(regex)
        return pattern.matcher(email).matches()
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







    private fun setupViews() {
        mImageHeader = findViewById(R.id.image_header) as ImageView
        val selectBtn1 = findViewById(R.id.btn_selectimage) as Button
        val selectBtn2 = findViewById(R.id.btn_takephoto) as Button
        selectBtn1.setOnClickListener(this)
        selectBtn2.setOnClickListener(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        } else {
            when (requestCode) {
                IMAGE_REQUEST_CODE -> resizeImage(data!!.data)
                CAMERA_REQUEST_CODE -> if (isSdcardExisting()) {
                    resizeImage(getImageUri())
                } else {
                    Toast.makeText(this@LoginActivity, "未找到存储卡，无法存储照片！",
                            Toast.LENGTH_LONG).show()
                }
                RESIZE_REQUEST_CODE -> data?.let { showResizeImage(it) }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun isSdcardExisting(): Boolean { //判断SD卡是否存在
        val state = Environment.getExternalStorageState()
        return if (state == Environment.MEDIA_MOUNTED) {
            true
        } else {
            false
        }
    }

    fun resizeImage(uri: Uri?) { //重塑图片大小
        val intent = Intent("com.android.camera.action.CROP")
        intent.setDataAndType(uri, "image/*")
        intent.putExtra("crop", "true") //可以裁剪
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", 150)
        intent.putExtra("outputY", 150)
        intent.putExtra("return-data", true)
        startActivityForResult(intent, RESIZE_REQUEST_CODE)
    }

    private fun showResizeImage(data: Intent) { //显示图片
        val extras = data.extras
        if (extras != null) {
            val photo = extras.getParcelable<Bitmap>("data")
            val drawable: Drawable = BitmapDrawable(photo)
            mImageHeader!!.setImageDrawable(drawable)
        }
    }

    private fun getImageUri(): Uri? { //获取路径
        return Uri.fromFile(File(Environment.getExternalStorageDirectory(),
                IMAGE_FILE_NAME))
    }
}

