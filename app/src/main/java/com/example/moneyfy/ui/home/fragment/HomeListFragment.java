package com.example.moneyfy.ui.home.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyfy.R;
import com.example.moneyfy.data.model.ExpenseItem;
import com.example.moneyfy.data.room.AppDatabase;
import com.example.moneyfy.data.room.Transaction;
import com.example.moneyfy.ui.home.adapter.HomeListAdapter;
import com.example.moneyfy.ui.home.bottomsheet.BottomSheetAdd;
import com.example.moneyfy.ui.home.bottomsheet.BottomSheetEditDel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

public class HomeListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private MaterialTextView dropdownDate;
    MaterialTextView sumIncome;
    MaterialTextView sumExpense;
    private String selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    @Override
    public void onResume() {
        super.onResume();
        loadData();
        updateDailySum();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dropdownDate = view.findViewById(R.id.dropdown_date_home);
        recyclerView = view.findViewById(R.id.recycler_view_list);
        btnAdd = view.findViewById(R.id.btn_add);
        sumIncome = view.findViewById(R.id.sum_income);
        sumExpense = view.findViewById(R.id.sum_expense);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dropdownDate.setText(selectedDate); // 기본 날짜 표시
        loadData(); // 기본 날짜 기준으로 데이터 로딩


        dropdownDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        // 날짜 문자열 갱신
                        selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        dropdownDate.setText(selectedDate);

                        // 데이터 로딩 및 합계 갱신
                        loadData();            // 리스트 필터링
                        updateDailySum();      // ← 이 함수 추가로 호출!
                    },
                    year, month, day);

            datePickerDialog.show();
        });


        btnAdd.setOnClickListener(v -> {
            BottomSheetAdd bottomSheet = new BottomSheetAdd();
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });


        getParentFragmentManager().setFragmentResultListener("refreshHomeList", getViewLifecycleOwner(), (key, bundle) -> {
            if (bundle.getBoolean("shouldRefresh", false)) {
                loadData();
                updateDailySum();
            }
        });
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            long[] range = getStartAndEndOfDate(selectedDate);
            List<Transaction> transactions = AppDatabase
                    .getInstance(requireContext())
                    .transactionDao()
                    .getBetweenDates(range[0], range[1]);

            List<ExpenseItem> uiList = new ArrayList<>();
            for (Transaction t : transactions) {
                ExpenseItem item = new ExpenseItem(
                        t.id,
                        String.valueOf(t.amount),
                        t.method,
                        convertMillisToDate(t.date),
                        t.category,
                        t.memo,
                        t.type
                );
                uiList.add(item);
            }

            requireActivity().runOnUiThread(() -> {
                HomeListAdapter adapter = new HomeListAdapter(uiList);
                adapter.setOnItemClickListener(item -> {
                    BottomSheetEditDel sheet = new BottomSheetEditDel();
                    Bundle args = new Bundle();
                    args.putSerializable("item", item);
                    sheet.setArguments(args);
                    sheet.show(getParentFragmentManager(), sheet.getTag());
                });
                recyclerView.setAdapter(adapter);
            });
        });
    }

    private String convertMillisToDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    private long[] getStartAndEndOfDate(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            long start = calendar.getTimeInMillis();

            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            long end = calendar.getTimeInMillis();

            return new long[]{start, end};
        } catch (Exception e) {
            e.printStackTrace();
            return new long[]{0, 0};
        }
    }

    private void updateDailySum() {
        long[] range = getStartAndEndOfDate(selectedDate);

        Executors.newSingleThreadExecutor().execute(() -> {
            Integer expenseSum = AppDatabase.getInstance(requireContext()).transactionDao().getDailyExpense(range[0], range[1]);
            Integer incomeSum = AppDatabase.getInstance(requireContext()).transactionDao().getDailyIncome(range[0], range[1]);

            int expense = expenseSum != null ? expenseSum : 0;
            int income = incomeSum != null ? incomeSum : 0;

            Log.d("HomeListFragment", "selectedDate: " + selectedDate);
            Log.d("HomeListFragment", "start=" + range[0] + ", end=" + range[1]);
            Log.d("HomeListFragment", "income=" + income + ", expense=" + expense);

            requireActivity().runOnUiThread(() -> {
                sumExpense.setText(String.format("- %,d", expense));
                sumIncome.setText(String.format("+ %,d", income));
            });
        });
    }
}
