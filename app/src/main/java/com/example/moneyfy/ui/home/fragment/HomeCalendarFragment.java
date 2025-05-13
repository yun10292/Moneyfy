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
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class HomeCalendarFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private TextView textDate, textIncome, textExpense;

    // 예시 데이터
    private final Map<String, Integer> incomeMap = new HashMap<>();
    private final Map<String, Integer> expenseMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        textDate = view.findViewById(R.id.text_date);
        textIncome = view.findViewById(R.id.text_income);
        textExpense = view.findViewById(R.id.text_expense);

        setupDummyData();
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



        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int year = date.getYear();
                int month = date.getMonth() + 1;
                int day = date.getDay();

                textDate.setText(String.format("%d.%d. %s", month, day, getDayOfWeek(date.getDate())));

                int income = getIncome(year, month, day);
                int expense = getExpense(year, month, day);

                textIncome.setText("수입: " + income + "원");
                textExpense.setText("지출: " + expense + "원");
            }
        });
    }

    private String getDayOfWeek(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("E", Locale.KOREAN);
        return sdf.format(date);
    }

    // 더미 데이터 초기화
    private void setupDummyData() {
        incomeMap.put("2024-3-1", 30000);
        expenseMap.put("2024-3-1", 25000);
    }

    private int getIncome(int y, int m, int d) {
        return incomeMap.getOrDefault(y + "-" + m + "-" + d, 0);
    }

    private int getExpense(int y, int m, int d) {
        return expenseMap.getOrDefault(y + "-" + m + "-" + d, 0);
    }
}
