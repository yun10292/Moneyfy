package com.example.moneyfy.ui.home.bottomsheet;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
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

public class BottomSheetExpense extends Fragment {



    public static BottomSheetExpense newInstance(@Nullable ExpenseItem item, boolean isEdit) {
        BottomSheetExpense fragment = new BottomSheetExpense();
        Bundle args = new Bundle();
        if (item != null) args.putSerializable("item", item);
        args.putBoolean("isEdit", isEdit);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_expense, container, false);
        
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etDate = view.findViewById(R.id.et_expense_date);
        EditText etCost = view.findViewById(R.id.et_expense_cost);
        EditText etMemo = view.findViewById(R.id.et_expense_memo);
        AutoCompleteTextView etCategory = view.findViewById(R.id.et_expense_category);
        AutoCompleteTextView etPayment = view.findViewById(R.id.et_expense_payment);

        // 드롭다운 초기화
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.category_list));
        etCategory.setAdapter(categoryAdapter);

        ArrayAdapter<String> methodAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.method_list));
        etPayment.setAdapter(methodAdapter);

        // DatePicker 연결
        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (DatePicker dp, int year, int month, int day) -> {
                String dateStr = String.format("%04d-%02d-%02d", year, month + 1, day);
                etDate.setText(dateStr);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        // 안전한 데이터 추출
        Bundle args = getArguments();
        ExpenseItem item = null;
        boolean isEdit = false;

        if (args != null) {
            item = (ExpenseItem) args.getSerializable("item");
            isEdit = args.getBoolean("isEdit", false);
        }

        // 데이터 채우기
        if (isEdit && item != null) {
            etDate.setText(item.getDate());
            etCost.setText(item.getCost());
            etMemo.setText(item.getMemo());

            etCategory.setText(item.getCategory(), false);
            etPayment.setText(item.getMethod(), false);

            view.findViewById(R.id.btn_expense_submit).setVisibility(View.GONE);
            view.findViewById(R.id.btn_expense_edit).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btn_expense_delete).setVisibility(View.VISIBLE);
        } else {
            // 신규 입력: 값 비우기
            etDate.setText("");
            etCost.setText("");
            etMemo.setText("");
            etCategory.setText("");
            etPayment.setText("");

            view.findViewById(R.id.btn_expense_submit).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btn_expense_edit).setVisibility(View.GONE);
            view.findViewById(R.id.btn_expense_delete).setVisibility(View.GONE);
        }
    }
}
