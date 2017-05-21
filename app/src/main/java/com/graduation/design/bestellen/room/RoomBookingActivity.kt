package com.graduation.design.bestellen.room

import android.app.DatePickerDialog
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.View
import com.graduation.design.bestellen.R
import com.graduation.design.bestellen.base.BaseActivity
import com.graduation.design.bestellen.common.Logs
import com.graduation.design.bestellen.model.RoomDetail
import com.graduation.design.bestellen.model.RoomDevice
import kotlinx.android.synthetic.main.activity_booking.*
import java.text.SimpleDateFormat
import java.util.*


class RoomBookingActivity : BaseActivity(), RoomBookingContract.View {
    /**
     * cache status bar height
     */
    var mStatusBarHeight: Int = 0
    var mAdapter: FormAdapter? = null

    val mPresenter = RoomBookingPresenter(this, RoomOccupationData())
    var mDatePicker: DatePickerDialog? = null

    override fun getDataSet(): Array<FormRow>? {
        return mAdapter?.mDataList
    }

    override fun updateForm() {
        mAdapter?.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
    }

    override fun initViews() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        if (intent.extras != null && intent.extras.containsKey("detail")) {
            val detail: RoomDetail = intent.extras["detail"] as RoomDetail
            titleText.text = detail.name
            locationText.text = detail.location
            appendText.text = detail.append
            capacityText.text = detail.capacity.toString()
            netEnableIcon.visibility = if (detail.deviceEnable[RoomDevice.net]) View.VISIBLE else View.GONE
            projectionEnableIcon.visibility = if (detail.deviceEnable[RoomDevice.projection]) View.VISIBLE else View.GONE
            micEnableIcon.visibility = if (detail.deviceEnable[RoomDevice.microphone]) View.VISIBLE else View.GONE
        }

        mStatusBarHeight = getStatusBarHeight()
        formView.post {
            var marginTop = selector.height
            (colorDescription.layoutParams as CoordinatorLayout.LayoutParams).topMargin = marginTop
            marginTop += colorDescription.height
            (formView.layoutParams as CoordinatorLayout.LayoutParams).topMargin = marginTop
            marginTop = supportActionBar?.height as Int + mStatusBarHeight
            (detailLayout.layoutParams as CollapsingToolbarLayout.LayoutParams).topMargin = marginTop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                appbar.stateListAnimator = null
            }
        }
        initLegend()
        mAdapter = FormAdapter(this, Array(28) { RoomBookingActivity.FormRow("", Array(7) { 0 }) })
        formView.setAdapter(mAdapter)
        mPresenter.loadRoomOccupationData("1", "1")
        val calendar = Calendar.getInstance()
        mDatePicker = DatePickerDialog(this,
                { _, year, month, dayOfMonth ->
                    selector.text = getString(R.string.date_format, year.toString(), (month + 1).toString(), dayOfMonth.toString())
                }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH] + 1)
        mDatePicker?.datePicker?.minDate = System.currentTimeMillis()
    }

    private fun initLegend() {
        var drawable = ContextCompat.getDrawable(this, R.drawable.drawable_color_closed)
        val drawableSize = (closedLegend.paint.fontMetrics.bottom - closedLegend.paint.fontMetrics.top).toInt()
        drawable.setBounds(0, 0, drawableSize, drawableSize)
        closedLegend.setCompoundDrawables(drawable, null, null, null)

        drawable = ContextCompat.getDrawable(this, R.drawable.drawable_color_available)
        drawable.setBounds(0, 0, drawableSize, drawableSize)
        availableLegend.setCompoundDrawables(drawable, null, null, null)

        drawable = ContextCompat.getDrawable(this, R.drawable.drawable_color_selected)
        drawable.setBounds(0, 0, drawableSize, drawableSize)
        selectedLegend.setCompoundDrawables(drawable, null, null, null)

        drawable = ContextCompat.getDrawable(this, R.drawable.drawable_color_occupied)
        drawable.setBounds(0, 0, drawableSize, drawableSize)
        occupiedLegend.setCompoundDrawables(drawable, null, null, null)
    }


    override fun initListener() {
        fab.setOnClickListener { view ->
        }
        appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val fraction = verticalOffset / (supportActionBar?.height!! - appBarLayout.height + mStatusBarHeight)
            val elevation = 8f * fraction //12f or 0f
            roomNameTitle.post {
                ViewCompat.setElevation(selector, elevation)
                ViewCompat.setElevation(appbar, elevation)
                if (fraction == 1) {
                    roomNameTitle.text = titleText.text
                } else {
                    roomNameTitle.text = ""
                }
            }
        }
        val sDateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
        selector.text = sDateFormat.format(java.util.Date())
        selector.setOnClickListener {
            mDatePicker?.show()
        }
    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }


    /**
     *-1 closed
     * 0 available
     * 1 occupied
     * 2 selected
     */
    data class FormRow(
            var period: String,
            val statusList: Array<Int>
    ) {
        fun getRowContent(colNumber: Int): Any {
            if (colNumber == 0) return period
            else return statusList[colNumber - 1]
        }

        fun getSize() = statusList.size + 1
    }

}
