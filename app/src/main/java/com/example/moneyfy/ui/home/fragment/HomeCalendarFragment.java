package com.example.moneyfy.ui.home.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneyfy.R;
import com.example.moneyfy.data.room.AppDatabase;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Executors;

public class HomeCalendarFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private TextView textDate, textIncome, textExpense;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        textDate = view.findViewById(R.id.text_date);
        textIncome = view.findViewById(R.id.text_income);
        textExpense = view.findViewById(R.id.text_expense);

        setupCalendar();

        return view;
    }

    private void setupCalendar() {
        Calendar today = Calendar.getInstance();

// 오늘 날짜 (예: 2025년 5월 13일)
        Calendar maxDate = (Calendar) today.clone();
        maxDate.set(Calendar.DAY_OF_MONTH, maxDate.getActualMaximum(Calendar.DAY_OF_MONTH)); // 5월 31일

// 세 달 전의 1일 (예: 3월 1일)
        Calendar minDate = (Calendar) today.clone();
        minDate.add(Calendar.MONTH, -2); // 두 달 전으로 설정하면 총 세 달이 됨
        minDate.set(Calendar.DAY_OF_MONTH, 1);

        calendarView.state().edit()
                .setMinimumDate(minDate)
                .setMaximumDate(maxDate)
                .commit();



        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            int year = date.getYear();
            int month = date.getMonth() + 1;
            int day = date.getDay();

            textDate.setText(String.format("%d.%d. %s", month, day, getDayOfWeek(date.getDate())));

            Executors.newSingleThreadExecutor().execute(() -> {
                int income = getIncome(year, month, day);
                int expense = getExpense(year, month, day);

                requireActivity().runOnUiThread(() -> {
                    textIncome.setText("수입: " + String.format("%,d원", income));
                    textExpense.setText("지출: " + String.format("%,d원", expense));
                });
            });
        });

    }

    private String getDayOfWeek(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.KOREAN);
        return sdf.format(date);
    }

    private int getIncome(int y, int m, int d) {
        long[] range = getStartAndEndOfDate(y, m, d);
        Integer result = AppDatabase.getInstance(requireContext()).transactionDao().getDailyIncome(range[0], range[1]);
        return result != null ? result : 0;
    }

    private int getExpense(int y, int m, int d) {
        long[] range = getStartAndEndOfDate(y, m, d);
        Integer result = AppDatabase.getInstance(requireContext()).transactionDao().getDailyExpense(range[0], range[1]);
        return result != null ? result : 0;
    }

    private long[] getStartAndEndOfDate(int y, int m, int d) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m - 1); // Calendar.MONTH는 0-based
        cal.set(Calendar.DAY_OF_MONTH, d);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long start = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long end = cal.getTimeInMillis();

        return new long[]{start, end};
    }


}
