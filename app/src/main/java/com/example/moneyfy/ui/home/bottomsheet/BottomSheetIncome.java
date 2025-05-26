package com.example.moneyfy.ui.home.bottomsheet;

import android.app.DatePickerDialog;
import android.content.Context;
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
import com.example.moneyfy.data.room.AppDatabase;
import com.example.moneyfy.data.room.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class BottomSheetIncome extends Fragment {

    private ExpenseItem item = null;

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
        Context context = requireContext();

        EditText etDate = view.findViewById(R.id.et_income_date);
        EditText etCost = view.findViewById(R.id.et_income_cost);
        AutoCompleteTextView etCategory = view.findViewById(R.id.et_income_category);
        AutoCompleteTextView etMethod = view.findViewById(R.id.et_income_method);

        // 드롭다운 초기화
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.income_category_list));
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
        // 신규 입력
        view.findViewById(R.id.btn_income_submit).setOnClickListener(v -> {
            String dateStr = etDate.getText().toString().trim();
            String costStr = etCost.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String method = etMethod.getText().toString().trim();

            if (dateStr.isEmpty() || costStr.isEmpty() || category.isEmpty()) {
                Log.w("BottomSheetIncome", "필수 입력값 누락");
                return;
            }

            long timestamp = convertDateToMillis(dateStr);

            Transaction t = new Transaction();
            t.type = "income";
            t.amount = Integer.parseInt(costStr);
            t.category = category;
            t.method = method;
            t.memo = ""; // 수입은 메모 필드가 없으므로 공백

            t.date = timestamp;

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(context).transactionDao().insert(t);
                Log.d("DB", "데이터 삽입 완료: " + t.toString());
            });

        });

        // 수정
        view.findViewById(R.id.btn_income_edit).setOnClickListener(v -> {
            if (item == null) return;

            String dateStr = etDate.getText().toString().trim();
            String costStr = etCost.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String method = etMethod.getText().toString().trim();

            if (dateStr.isEmpty() || costStr.isEmpty() || category.isEmpty()) {
                Log.w("BottomSheetIncome", "수정 - 필수 입력값 누락");
                return;
            }

            Transaction t = new Transaction();
            t.id = item.getId();  // 반드시 ID 포함 (수정 대상 식별)
            t.type = "income";
            t.amount = Integer.parseInt(costStr);
            t.category = category;
            t.method = method;
            t.memo = "";  // 수입은 메모 없음
            t.date = convertDateToMillis(dateStr);

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(context).transactionDao().update(t);
                Log.d("DB", "데이터 수정 완료: " + t.toString());
            });
        });

        // 삭제

        view.findViewById(R.id.btn_income_delete).setOnClickListener(v -> {
            if (item == null) return;

            Transaction t = new Transaction();
            t.id = item.getId();  // 반드시 ID 포함 (삭제 대상 식별)
            t.type = "income";
            t.amount = Integer.parseInt(item.getCost());
            t.category = item.getCategory();
            t.method = item.getMethod();
            t.memo = "";
            t.date = convertDateToMillis(item.getDate());

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(context).transactionDao().delete(t);
                Log.d("DB", "데이터 삭제 완료: " + t.toString());
            });

        });


    }

    private long convertDateToMillis(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateStr);
            return date.getTime();
        } catch (Exception e) {
            Log.e("DateParse", "날짜 파싱 실패: " + dateStr);
            return System.currentTimeMillis();
        }
    }
}

