package com.graduation.design.bestellen.function.main.record

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.graduation.design.bestellen.R
import com.graduation.design.bestellen.base.BaseFragment
import com.graduation.design.bestellen.model.Record
import kotlinx.android.synthetic.main.fragment_reservation.*

/**
 * Created by pan on 2017/5/5.
 * Record Fragment,show booking record
 */
class RecordFragment : BaseFragment(),RecordContract.View{
    var mAdapter:RecordAdapter? = null
    val mPresenter = RecordPresenter(this,RecordData())

    override fun setPresenter(presenter: RecordContract.Presenter) {
    }

    override fun getDataSet(): ArrayList<Record>? {
        return mAdapter?.mData
    }

    override fun updateRecyclerView() {
        mAdapter?.notifyDataSetChanged()
    }

    override fun getLayout(): Int = R.layout.fragment_record

    override fun initData() {
        mPresenter.loadData()
    }

    override fun initViews() {
        mAdapter = RecordAdapter(activity, ArrayList())
        recyclerView.setLayoutManager(LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false))
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL), 0)
        recyclerView.setAdapter(mAdapter)
    }

}