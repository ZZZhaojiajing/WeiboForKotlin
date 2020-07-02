package com.jojo.weibodetail_demo

import kotlinx.android.synthetic.main.fragment_list.*


class FragmentList : BaseFragment() {
    override fun initEvents() {
        var mDatas = ArrayList<String>()
        for (i in 0..3) {
            mDatas.add("用户推荐$i")
        }
        var mAdapter = MyListAdapter(this!!.activity, mDatas)
        listview.adapter = mAdapter
    }

    override fun getContentViewId(): Int {
        return R.layout.fragment_list
    }
}