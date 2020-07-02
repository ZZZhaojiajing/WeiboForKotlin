package com.jojo.weibodetail_demo

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


abstract class BaseFragment : Fragment() {
    internal var mContext: Context? = null
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(getContentViewId(), null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvents()
    }

    abstract fun initEvents()

    abstract fun getContentViewId(): Int
}