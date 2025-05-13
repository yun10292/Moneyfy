package com.example.moneyfy.ui.home.bottomsheet;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneyfy.R;
import com.example.moneyfy.data.model.ExpenseItem;

import java.util.Calendar;

public class BottomSheetIncome extends Fragment {

    public static BottomSheetIncome newInstance(ExpenseItem item, boolean isEdit) {
        BottomSheetIncome fragment = new BottomSheetIncome();
        Bundle args = new Bundle();
        args.putSerializable("item", item);
        args.putBoolean("isEdit", isEdit);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etDate = view.findViewById(R.id.et_income_date);
        EditText etCost = view.findViewById(R.id.et_income_cost);
        AutoCompleteTextView etCategory = view.findViewById(R.id.et_income_category);
        AutoCompleteTextView etMethod = view.findViewById(R.id.et_income_method);

        // 드롭다운 초기화
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.category_list));
        etCategory.setAdapter(categoryAdapter);

        ArrayAdapter<String> methodAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.method_list));
        etMethod.setAdapter(methodAdapter);

        // DatePicker 연결
        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (DatePicker dp, int year, int month, int day) -> {
                String dateStr = String.format("%04d-%02d-%02d", year, month + 1, day);
                etDate.setText(dateStr);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 기존 데이터 표시
        Bundle args = getArguments();
        ExpenseItem item = null;
        boolean isEdit = false;

        if (args != null) {
            item = (ExpenseItem) args.getSerializable("item");
            isEdit = args.getBoolean("isEdit", false);
        }

        if (item != null) {
            etCost.setText(item.getCost());
            etCategory.setText(item.getCategory(), false);
            etMethod.setText(item.getMethod(), false);
            etDate.setText(item.getDate());
        }

        // 버튼 상태 전환
        if (isEdit) {
            view.findViewById(R.id.btn_income_submit).setVisibility(View.GONE);
            view.findViewById(R.id.btn_income_edit).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btn_income_delete).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.btn_income_submit).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btn_income_edit).setVisibility(View.GONE);
            view.findViewById(R.id.btn_income_delete).setVisibility(View.GONE);
        }
    }
}
