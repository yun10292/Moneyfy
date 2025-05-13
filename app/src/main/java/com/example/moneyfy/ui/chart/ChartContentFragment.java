package com.example.moneyfy.ui.chart;

import com.example.moneyfy.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyfy.data.model.ChartCategoryItem;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChartContentFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    private String chartType;
    private Calendar currentMonth;

    public static ChartContentFragment newInstance(String type) {
        ChartContentFragment fragment = new ChartContentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chart_content, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        chartType = getArguments() != null ? getArguments().getString(ARG_TYPE, "income") : "income";
        currentMonth = Calendar.getInstance();

        TextView txtMonth = view.findViewById(R.id.txt_month);
        ImageButton btnPrev = view.findViewById(R.id.btn_prev);
        ImageButton btnNext = view.findViewById(R.id.btn_next);

        btnPrev.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            if (isWithinLastThreeMonths(currentMonth)) updateMonth(txtMonth);
            else currentMonth.add(Calendar.MONTH, 1);
        });

        btnNext.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            currentMonth.add(Calendar.MONTH, 1);
            if (!currentMonth.after(now)) updateMonth(txtMonth);
            else currentMonth.add(Calendar.MONTH, -1);
        });

        updateMonth(txtMonth);

        PieChart chart = view.findViewById(R.id.chart);
        setChartData(chart, chartType);

        RecyclerView recyclerView = view.findViewById(R.id.rv_categories);
        List<ChartCategoryItem> categoryList = new ArrayList<>();
        categoryList.add(new ChartCategoryItem(R.drawable.ic_add, "건강/현금", "32,330원"));

        ChartCategoryAdapter adapter = new ChartCategoryAdapter(categoryList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


    }

    private void updateMonth(TextView txtMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월", Locale.getDefault());
        txtMonth.setText(sdf.format(currentMonth.getTime()));
    }

    private boolean isWithinLastThreeMonths(Calendar target) {
        Calendar threeMonthsAgo = Calendar.getInstance();
        threeMonthsAgo.add(Calendar.MONTH, -3);
        return !target.before(threeMonthsAgo);
    }

    private void setChartData(PieChart chart, String type) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(40f, "식비"));
        entries.add(new PieEntry(30f, "교통"));
        entries.add(new PieEntry(30f, "기타"));

        PieDataSet dataSet = new PieDataSet(entries, type.equals("income") ? "수입" : "지출");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(dataSet);

        chart.setData(data);
        chart.invalidate();
    }


}
