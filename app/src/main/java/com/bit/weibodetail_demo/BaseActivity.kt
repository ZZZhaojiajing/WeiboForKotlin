package com.jojo.weibodetail_demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentViewId())
        initEvents()
    }

    abstract fun getContentViewId(): Int

    abstract fun initEvents()
}