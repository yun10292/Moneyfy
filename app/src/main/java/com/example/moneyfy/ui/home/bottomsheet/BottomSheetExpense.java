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
import androidx.navigation.fragment.NavHostFragment;

import com.example.moneyfy.R;
import com.example.moneyfy.data.feedback.FeedbackApi;
import com.example.moneyfy.data.feedback.FeedbackData;
import com.example.moneyfy.data.model.ExpenseItem;
import com.example.moneyfy.data.room.AppDatabase;
import com.example.moneyfy.data.room.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BottomSheetExpense extends Fragment {

    private ExpenseItem item = null;

    //추가
    public String originalCategory;

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
        Context context = requireContext();

        EditText etDate = view.findViewById(R.id.et_expense_date);
        EditText etCost = view.findViewById(R.id.et_expense_cost);
        EditText etMemo = view.findViewById(R.id.et_expense_memo);
        AutoCompleteTextView etCategory = view.findViewById(R.id.et_expense_category);
        AutoCompleteTextView etPayment = view.findViewById(R.id.et_expense_payment);

        // 드롭다운 초기화
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.expenses_category_list));
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
        //ExpenseItem item = null;
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

            //추가
            originalCategory = item.getCategory();

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

        // 신규 입력
        view.findViewById(R.id.btn_expense_submit).setOnClickListener(v -> {
            // 입력값 추출
            String dateStr = etDate.getText().toString().trim();
            String costStr = etCost.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String method = etPayment.getText().toString().trim();
            String memo = etMemo.getText().toString().trim();

            if (dateStr.isEmpty() || costStr.isEmpty() || category.isEmpty()) {
                Log.w("BottomSheetExpense", "필수 입력값 누락");
                return;
            }

            // 날짜 문자열 → timestamp 변환
            long timestamp = convertDateToMillis(dateStr);

            // Transaction 객체 생성
            Transaction t = new Transaction();
            t.type = "expense";
            t.amount = Integer.parseInt(costStr);
            t.category = category;
            t.method = method;
            t.memo = memo;
            t.date = timestamp;

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(context).transactionDao().insert(t);
                Log.d("DB", "데이터 삽입 완료: " + t.toString());
            });
        });

        // 수정
        view.findViewById(R.id.btn_expense_edit).setOnClickListener(v -> {
            if (item == null) return;

            String dateStr = etDate.getText().toString().trim();
            String costStr = etCost.getText().toString().trim();
            String category = etCategory.getText().toString().trim();
            String method = etPayment.getText().toString().trim();
            String memo = etMemo.getText().toString().trim();

            if (dateStr.isEmpty() || costStr.isEmpty() || category.isEmpty()) {
                Log.w("BottomSheetExpense", "수정 - 필수 입력 누락");
                return;
            }

            Transaction t = new Transaction();
            t.id = item.getId(); // 필수: 기존 항목의 ID
            t.type = "expense";
            t.amount = Integer.parseInt(costStr);
            t.category = category;
            t.method = method;
            t.memo = memo;
            t.date = convertDateToMillis(dateStr);

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(context).transactionDao().update(t);
                Log.d("DB", "데이터 수정 완료: " + t.toString());

                //수정
                if (!originalCategory.equals(category)) {
                    sendFeedbackToServer(t.memo, t.category);
                    Log.d("BottomSheetExpense", "Send Feedback To Server");
                }
            });


        });

        // 삭제
        view.findViewById(R.id.btn_expense_delete).setOnClickListener(v -> {
            if (item == null) return;

            Transaction t = new Transaction();
            t.id = item.getId(); // 필수: 어떤 항목을 지울지 지정
            t.type = "expense";
            t.amount = Integer.parseInt(item.getCost());
            t.category = item.getCategory();
            t.method = item.getMethod();
            t.memo = item.getMemo();
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


    //추가
    private void sendFeedbackToServer(String store, String correctedCategory) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ner-sms-server.onrender.com/") //http://192.168.0.40:5000/
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FeedbackApi api = retrofit.create(FeedbackApi.class);
        FeedbackData data = new FeedbackData(store, correctedCategory);

        api.sendFeedback(data).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("Feedback", "카테고리 피드백 전송 성공");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Feedback", "카테고리 피드백 전송 실패: " + t.getMessage());
            }
        });
    }
}