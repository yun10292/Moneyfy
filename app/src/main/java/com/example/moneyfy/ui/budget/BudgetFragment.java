package com.example.moneyfy.ui.budget;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneyfy.R;

import java.util.Calendar;

public class BudgetFragment extends Fragment {

    private TextView textStartDate, textEndDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_budget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        textStartDate = view.findViewById(R.id.text_start_date);
        textEndDate = view.findViewById(R.id.text_end_date);

        textStartDate.setOnClickListener(v -> showDatePicker(textStartDate));
        textEndDate.setOnClickListener(v -> showDatePicker(textEndDate));
    }

    private void showDatePicker(TextView targetTextView) {
        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            String dateStr = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
            targetTextView.setText(dateStr);
        }, y, m, d);

        dialog.show();
    }
}
