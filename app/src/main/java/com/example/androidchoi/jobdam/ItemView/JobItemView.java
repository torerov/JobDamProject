package com.example.androidchoi.jobdam.ItemView;

import android.content.Context;
import android.util.TypedValue;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.androidchoi.jobdam.Model.Job;
import com.example.androidchoi.jobdam.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Choi on 2015-10-18.
 */
public class JobItemView extends RelativeLayout implements Checkable {

    public static final int ONE_DAY_TIME_STAMP = 86400000;
    TextView mCorp;
    TextView mTitle;
    TextView mPeriod;
    TextView mDDay;
    TextView mTextQuestion;
    ImageView mImageJobLogo;
    RelativeLayout mLayout;
    boolean isChecked = false;

    public JobItemView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_job_item, this);
        mCorp = (TextView) findViewById(R.id.text_corp);
        mTitle = (TextView) findViewById(R.id.text_job_title);
        mPeriod = (TextView) findViewById(R.id.text_period);
        mDDay = (TextView) findViewById(R.id.text_job_dday);
        mTextQuestion = (TextView) findViewById(R.id.text_show_question);
        mImageJobLogo = (ImageView)findViewById(R.id.image_job_logo);
        mLayout = (RelativeLayout) findViewById(R.id.layout_job_item_container);
    }

    private void setPeriod(Date start, Date end, boolean checkDeadLine) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        StringBuilder period = new StringBuilder();
        period.append(dateFormat.format(start)).append(" ~ ");
        if (checkDeadLine) {
            period.append(dateFormat.format(end));
        } else {
            period.append("채용시까지");
        }
        mPeriod.setText(period.toString());
    }

    private boolean setDDay(Date end) {
        Calendar endDay = Calendar.getInstance();
        Calendar currentDay = Calendar.getInstance();
        currentDay.set(endDay.get(Calendar.YEAR), endDay.get(Calendar.MONTH), endDay.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        endDay.setTime(end);
        long endTime = endDay.getTimeInMillis();
        long todayTime = currentDay.getTimeInMillis();
        long timeGap = (endTime + 1000) - todayTime;
        int d_day = (int) (timeGap / ONE_DAY_TIME_STAMP);
        mDDay.setText("D-" + d_day);
        if (timeGap < 0) {
            mDDay.setText("마감");
            mDDay.setBackgroundResource(R.drawable.image_dday_box_end);
            mDDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_size_small));
            return true;
        }
        if (d_day == 0) {
            mDDay.setText("D-day");
            mDDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_size_min));
            mDDay.setBackgroundResource(R.drawable.image_dday_box_danger);
            return true;
        } else if (d_day < 7) {
            mDDay.setBackgroundResource(R.drawable.image_dday_box_danger);
        } else if (d_day < 15) {
            mDDay.setBackgroundResource(R.drawable.image_dday_box_warning);
        } else if (d_day > 200) {
            mDDay.setText("상시");
            mDDay.setBackgroundResource(R.drawable.image_dday_box_always);
            return false;
        } else {
            mDDay.setBackgroundResource(R.drawable.image_dday_box_default);
            if (d_day > 99) {
                mDDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_size_min));
                return true;
            }
        }
        mDDay.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_size_small));
        return true;
    }

    public void setItemData(final Job itemData, boolean check) {
        mCorp.setText(itemData.getCompanyName());
        mTitle.setText(itemData.getJobTitle());
        setChecked(false);
        Date start = new Date(itemData.getStart() * 1000L);
        Date end = new Date(itemData.getEnd() * 1000L);
        boolean checkDeadLine = setDDay(end);
        setPeriod(start, end, checkDeadLine);
        if (!check) {
            mTextQuestion.setVisibility(GONE);
        }
//        Glide.with(MyApplication.getContext()).load(itemData.getCompanyImage()).into(mImageJobLogo);
    }

    @Override
    public void setChecked(boolean checked) {
        this.isChecked = checked;
        if (checked) {
            mLayout.setSelected(true);
//            mLayout.setBackgroundResource(R.color.colorLightPrimary);
        } else {
            mLayout.setSelected(false);
//            mLayout.setBackgroundResource(R.drawable.image_job_container);
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }
}
