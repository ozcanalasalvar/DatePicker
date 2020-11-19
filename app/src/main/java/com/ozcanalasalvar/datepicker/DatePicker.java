package com.ozcanalasalvar.datepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.ozcanalasalvar.datepicker.factory.DatePickerFactory;
import com.ozcanalasalvar.datepicker.factory.FactoryListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatePicker extends LinearLayout implements FactoryListener {

    private Context context;
    private LinearLayout container;
    private int offset = 3;
    private DatePickerFactory factory;
    private WheelView dayView;
    private WheelView monthView;
    private WheelView yearView;
    private WheelView emptyView1;
    private WheelView emptyView2;
    private int monthMin = 0;
    private int textSize = 19;

    private final static int MAX_TEXT_SIZE = 19;


    public DatePicker(Context context) {
        super(context);
        init(context);
    }

    public DatePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DatePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DatePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.setOrientation(LinearLayout.HORIZONTAL);

        factory = new DatePickerFactory(this);

        container = new LinearLayout(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        container.setLayoutParams(layoutParams);
        container.setOrientation(LinearLayout.HORIZONTAL);
        this.addView(container);
        setUpInitialViews();
    }


    private void setUpInitialViews() {
        container.addView(createEmptyView1(context));
        container.addView(createMonthPickerView(context));
        container.addView(createDayPickerView(context));
        container.addView(createYearPickerView(context));
        container.addView(createEmptyView2(context));
        setUpCalendar();
    }

    public void setUpCalendar() {
        Log.i("Calendar", "setUp = " + factory.getSelectedDate().toString());
        setUpYearView();
        setUpMonthView();
        setUpDayView();
        setupEmptyViews();
    }

    private void setupEmptyViews() {
        List<String> array = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            array.add("");
        }
        emptyView1.setTextSize(textSize);
        emptyView2.setTextSize(textSize);
        emptyView1.setOffset(offset);
        emptyView2.setOffset(offset);
        emptyView1.setItems(array);
        emptyView2.setItems(array);
    }

    private void notifyMonthView() {
        setUpMonthView();
    }

    private void notifyDayView() {
        setUpDayView();
    }

    private void setUpYearView() {
        DateModel maxDate = factory.getMaxDate();
        DateModel minDate = factory.getMinDate();
        DateModel date = factory.getSelectedDate();

        Log.i("Calendar", "setUpYearView = " + date.toString());
        int yearCount = Math.abs(minDate.getYear() - maxDate.getYear()) + 1;
        List<String> years = new ArrayList<>();
        for (int i = 0; i < yearCount; i++) {
            years.add("" + (minDate.getYear() + i));
        }
        yearView.setOffset(offset);
        yearView.setTextSize(textSize);
        yearView.setAlignment(View.TEXT_ALIGNMENT_CENTER);
        yearView.setGravity(Gravity.END);
        yearView.setItems(years);
        yearView.setSelection(years.indexOf("" + date.getYear()));
    }

    private void setUpMonthView() {
        DateModel maxDate = factory.getMaxDate();
        DateModel minDate = factory.getMinDate();
        DateModel date = factory.getSelectedDate();
        Log.i("Calendar", "setUpMonthView = " + date.toString());

        List<String> monthsArray = Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");

        int max = monthsArray.size();
        if (date.getYear() == maxDate.getYear()) {
            max = maxDate.getMonth() + 1;
        }
        if (date.getYear() == minDate.getYear()) {
            monthMin = minDate.getMonth();
        } else
            monthMin = 0;

        List<String> months = new ArrayList<>();
        for (int i = monthMin; i < max; i++) {
            months.add(monthsArray.get(i));
        }

        Log.i("date", "month min= " + monthMin);
        Log.i("date", "month max= " + max);

        monthView.setTextSize(textSize);
        monthView.setGravity(Gravity.START);
        monthView.setAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        monthView.setOffset(offset);
        monthView.setItems(months);
        monthView.setSelection(date.getMonth() - monthMin);
    }

    private void setUpDayView() {
        DateModel maxDate = factory.getMaxDate();
        DateModel minDate = factory.getMinDate();
        DateModel date = factory.getSelectedDate();
        Log.i("Calendar", "setUpDayView = " + date.toString());

        int max = DateUtils.getMonthDayCount(date.getDate());
        int min = 0;
        if (date.getYear() == maxDate.getYear() && date.getMonth() == maxDate.getMonth()) {
            max = maxDate.getDay();
        }
        if (date.getYear() == minDate.getYear() && date.getMonth() == minDate.getMonth()) {
            min = minDate.getDay() - 1;
        }

        List<String> days = new ArrayList<>();
        for (int i = min; i < max; i++) {
            days.add("" + (i + 1));
        }

        Log.i("date", "day min= " + min);
        Log.i("date", "day max= " + max);

        dayView.setOffset(offset);
        dayView.setTextSize(textSize);
        dayView.setGravity(Gravity.END);
        dayView.setAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        dayView.setOffset(offset);
        dayView.setItems(days);
        dayView.setSelection((date.getDay() - 1)); //Day start from 1
    }

    private LinearLayout createYearPickerView(Context context) {
        yearView = new WheelView(context);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        yearView.setLayoutParams(lp);
        yearView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                factory.setSelectedYear(Integer.parseInt(item));
            }
        });
        LinearLayout ly = wheelContainerView(2.0f);
        ly.addView(yearView);
        return ly;
    }


    private LinearLayout createMonthPickerView(Context context) {
        monthView = new WheelView(context);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        monthView.setLayoutParams(lp);
        monthView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                factory.setSelectedMonth(monthMin + selectedIndex);
            }
        });
        LinearLayout ly = wheelContainerView(2.0f);
        ly.addView(monthView);
        return ly;
    }


    private LinearLayout createDayPickerView(Context context) {
        dayView = new WheelView(context);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dayView.setLayoutParams(lp);
        dayView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                factory.setSelectedDay(selectedIndex + 1);
            }
        });
        LinearLayout ly = wheelContainerView(2.0f);
        ly.addView(dayView);
        return ly;
    }


    private LinearLayout createEmptyView1(Context context) {
        emptyView1 = createEmptyWheel(context);
        LinearLayout ly = wheelContainerView(1.0f);
        ly.addView(emptyView1);
        return ly;
    }

    private LinearLayout createEmptyView2(Context context) {
        emptyView2 = createEmptyWheel(context);
        LinearLayout ly = wheelContainerView(1.0f);
        ly.addView(emptyView2);
        return ly;
    }

    private WheelView createEmptyWheel(Context context) {
        WheelView view = new WheelView(context);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return view;
    }

    private LinearLayout wheelContainerView(float weight) {
        LinearLayout layout = new LinearLayout(context);
        LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight);
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    /**
     * Implement current min date
     *
     * @param date
     */
    public void setMinDate(long date) {
        factory.setMinDate(date);
    }

    /**
     * Implement current max date
     *
     * @param date
     */
    public void setMaxDate(long date) {
        factory.setMaxDate(date);
    }

    /**
     * Implement current selected date
     *
     * @param date
     */
    public void setDate(long date) {
        factory.setSelectedDate(date);
    }

    /**
     * @return minDate
     */
    public long getMinDate() {
        return factory.getMinDate().getDate();
    }

    /**
     * @return maxDate
     */
    public long getMaxDate() {
        return factory.getMaxDate().getDate();
    }

    /**
     * @return date
     */
    public long getDate() {
        return factory.getSelectedDate().getDate();
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
        setUpCalendar();
    }

    public void setTextSize(int textSize) {
        this.textSize = Math.min(textSize, MAX_TEXT_SIZE);
        setUpCalendar();
    }

    @Override
    public void onYearChanged() {
        notifyMonthView();
        notifyDayView();
        notifyDateSelect();
    }

    @Override
    public void onMonthChanged() {
        notifyDayView();
        notifyDateSelect();
    }

    @Override
    public void onDayChanged() {
        notifyDateSelect();
    }

    @Override
    public void onConfigsChanged() {
        setUpCalendar();
    }

    public interface DataSelectListener {
        void onDateSelected(long date, int day, int month, int year);
    }

    private DataSelectListener dataSelectListener;

    public void setDataSelectListener(DataSelectListener dataSelectListener) {
        this.dataSelectListener = dataSelectListener;
    }

    private void notifyDateSelect() {
        DateModel date = factory.getSelectedDate();
        dataSelectListener.onDateSelected(date.getDate(), date.getDay(), date.getMonth(), date.getYear());
    }
}
