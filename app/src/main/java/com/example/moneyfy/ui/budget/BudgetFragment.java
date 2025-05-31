package com.example.moneyfy.ui.budget;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneyfy.R;
import com.example.moneyfy.data.room.AppDatabase;
import com.example.moneyfy.data.room.Asset;
import com.example.moneyfy.data.room.Budget;
import com.example.moneyfy.data.room.SavingGoal;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class BudgetFragment extends Fragment {

    private TextView textStartDate, textEndDate, textRemain, textPercent;
    EditText etAsset;
    LinearProgressIndicator pbProgress;

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
        textRemain = view.findViewById(R.id.tv_saving_remain);
        textPercent = view.findViewById(R.id.tv_saving_percent);
        pbProgress = view.findViewById(R.id.pb_saving_progress);


        EditText etAsset = view.findViewById(R.id.et_total_asset);
        Button btnSaveAsset = view.findViewById(R.id.btn_save_asset);

        Button btnSaveGoal = view.findViewById(R.id.btn_save_goal);
        EditText etGoal = view.findViewById(R.id.et_saving_goal);

        EditText etBudget = view.findViewById(R.id.et_month_budget);
        textStartDate.setOnClickListener(v -> showDatePicker(textStartDate));
        textEndDate.setOnClickListener(v -> showDatePicker(textEndDate));


        Button btnSaveBudget = view.findViewById(R.id.btn_save_budget);
        btnSaveBudget.setOnClickListener(v -> {
            String budgetStr = etBudget.getText().toString().trim();
            if (budgetStr.isEmpty()) {
                Toast.makeText(requireContext(), "예산을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            int budgetAmount = Integer.parseInt(budgetStr);
            int yearMonthInt = getTodayYearMonth();
            String yearMonthStr = String.valueOf(yearMonthInt);

            EditText etRemain = requireView().findViewById(R.id.et_remaining_budget);

            Budget budget = new Budget();
            budget.yearMonth = yearMonthInt;
            budget.budgetAmount = budgetAmount;

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                db.budgetDao().insertOrUpdate(budget);

                Integer totalExpense = db.transactionDao().getMonthlyExpense(yearMonthStr);
                int expenseAmount = (totalExpense != null) ? totalExpense : 0;

                int remaining = budgetAmount - expenseAmount;

                requireActivity().runOnUiThread(() -> {
                    etRemain.setText(String.valueOf(remaining)); // 남은 예산 표시
                    Toast.makeText(requireContext(), "예산이 저장되었습니다", Toast.LENGTH_SHORT).show();
                });
            });
        });


        Executors.newSingleThreadExecutor().execute(() -> {
            int yearMonth = getTodayYearMonth();
            Budget budget = AppDatabase.getInstance(requireContext()).budgetDao().getByYearMonth(yearMonth);

            if (budget != null) {
                requireActivity().runOnUiThread(() -> {
                    etBudget.setText(String.valueOf(budget.budgetAmount));
                });
            }
        });

        Executors.newSingleThreadExecutor().execute(() -> {
            int yearMonthInt = getTodayYearMonth();
            String yearMonthStr = String.valueOf(yearMonthInt); // strftime('%Y%m') 포맷과 일치

            AppDatabase db = AppDatabase.getInstance(requireContext());
            Budget budget = db.budgetDao().getByYearMonth(yearMonthInt);
            Integer totalExpense = db.transactionDao().getMonthlyExpense(yearMonthStr);

            int budgetAmount = (budget != null) ? budget.budgetAmount : 0;
            int expenseAmount = (totalExpense != null) ? totalExpense : 0;
            int remaining = budgetAmount - expenseAmount;

            requireActivity().runOnUiThread(() -> {
                EditText et_Budget = requireView().findViewById(R.id.et_month_budget);
                EditText etRemain = requireView().findViewById(R.id.et_remaining_budget);

                et_Budget.setText(String.valueOf(budgetAmount));
                etRemain.setText(String.valueOf(remaining));
            });
        });

        btnSaveGoal.setOnClickListener(v -> {
            String startStr = textStartDate.getText().toString().trim();
            String endStr = textEndDate.getText().toString().trim();
            String goalStr = etGoal.getText().toString().trim();

            if (startStr.equals("시작일") || endStr.equals("종료일") || goalStr.isEmpty()) {
                Toast.makeText(requireContext(), "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            long startDate = parseDateToMillis(startStr);
            long endDate = parseDateToMillis(endStr);
            int goalAmount = Integer.parseInt(goalStr);

            SavingGoal goal = new SavingGoal();
            goal.startDate = startDate;
            goal.endDate = endDate;
            goal.goalAmount = goalAmount;
            goal.currentSavedAmount = 0;


            Executors.newSingleThreadExecutor().execute(() -> {
                // 1) DB에 목표 저장
                AppDatabase.getInstance(requireContext()).savingGoalDao().insert(goal);
                Log.d("Goal", "목표 저장 완료: " + goalAmount);

                // 2) 저장한 후, 다시 DB에서 최신 목표 가져오기
                long today = System.currentTimeMillis();
                List<SavingGoal> activeGoals = AppDatabase.getInstance(requireContext())
                        .savingGoalDao().getActiveGoals(today);

                if (!activeGoals.isEmpty()) {
                    SavingGoal latestGoal = activeGoals.get(0);

                    String newStartStr = formatMillisToDate(latestGoal.startDate);
                    String newEndStr = formatMillisToDate(latestGoal.endDate);
                    String newGoalStr = String.valueOf(latestGoal.goalAmount);

                    Integer savedAmount = AppDatabase.getInstance(requireContext())
                            .transactionDao().getTotalSaving(latestGoal.startDate, latestGoal.endDate);
                    int saved = (savedAmount != null) ? savedAmount : 0;
                    int remain = latestGoal.goalAmount - saved;
                    int percent = (latestGoal.goalAmount > 0) ? (100 * saved / latestGoal.goalAmount) : 0;

                    // 3) UI 스레드에서 화면 갱신
                    requireActivity().runOnUiThread(() -> {
                        textStartDate.setText(newStartStr);
                        textEndDate.setText(newEndStr);
                        etGoal.setText(newGoalStr);
                        textRemain.setText(remain + "원");
                        pbProgress.setProgress(percent);
                        textPercent.setText(percent + "% 달성");
                        Toast.makeText(requireContext(), "저축 목표가 저장되고 UI에 반영되었습니다", Toast.LENGTH_SHORT).show();
                    });
                }
            });

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(requireContext()).savingGoalDao().insert(goal);
                Log.d("Goal", "목표 저장 완료: " + goalAmount);
            });


            Toast.makeText(requireContext(), "저축 목표가 저장되었습니다", Toast.LENGTH_SHORT).show();
        });


        btnSaveAsset.setOnClickListener(v -> {
            String assetStr = etAsset.getText().toString().trim();
            if (assetStr.isEmpty()) {
                Toast.makeText(requireContext(), "자산을 입력해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount = Integer.parseInt(assetStr);
            Asset asset = new Asset();
            asset.totalAmount = amount;

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase.getInstance(requireContext()).assetDao().insertOrUpdate(asset);
            });

            Toast.makeText(requireContext(), "자산이 저장되었습니다", Toast.LENGTH_SHORT).show();
        });

        Executors.newSingleThreadExecutor().execute(() -> {
            Asset asset = AppDatabase.getInstance(requireContext()).assetDao().get();
            if (asset != null) {
                requireActivity().runOnUiThread(() -> {
                    etAsset.setText(String.valueOf(asset.totalAmount));
                });
            }
        });


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

    private int getTodayYearMonth() {
        Calendar calendar = Calendar.getInstance(); // 오늘 날짜
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // MONTH는 0부터 시작하므로 +1
        return year * 100 + month;
    }

    private long parseDateToMillis(String dateStr) {
        // "yyyy-MM-dd" -> long millis
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    private String formatMillisToDate(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(millis));
    }


}
