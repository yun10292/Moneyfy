package com.example.moneyfy.ui.home.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneyfy.R;
import com.example.moneyfy.data.model.ExpenseItem;
import com.example.moneyfy.ui.home.adapter.HomeListAdapter;
import com.example.moneyfy.ui.home.bottomsheet.BottomSheetAdd;
import com.example.moneyfy.ui.home.bottomsheet.BottomSheetEditDel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;
import android.app.DatePickerDialog;
import java.util.Calendar;

public class HomeListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton btnAdd;
    private MaterialTextView dropdownDate;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 더미 데이터
        List<ExpenseItem> dummyData = new ArrayList<>();
        dummyData.add(new ExpenseItem("12000", "현금", "2025-05-12", "식비", "점심 김밥"));
        dummyData.add(new ExpenseItem("3000", "카드", "2025-05-11", "교통", "버스 탑승"));
        dummyData.add(new ExpenseItem("4500", "카드", "2025-05-10", "카페", "아이스 아메리카노"));
        dummyData.add(new ExpenseItem("29000", "카드", "2025-05-09", "쇼핑", "티셔츠 구매"));
        dummyData.add(new ExpenseItem("1000", "현금", "2025-05-08", "기타", "자판기 음료"));


        HomeListAdapter adapter = new HomeListAdapter(dummyData);
        adapter.setOnItemClickListener(item -> {
            BottomSheetEditDel sheet = new BottomSheetEditDel();
            Bundle args = new Bundle();
            args.putSerializable("item", item);
            sheet.setArguments(args);
            sheet.show(getParentFragmentManager(), sheet.getTag());
        });

        recyclerView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            BottomSheetAdd bottomSheet = new BottomSheetAdd();
            bottomSheet.show(getParentFragmentManager(), bottomSheet.getTag());
        });

        dropdownDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        // 월은 0부터 시작하므로 +1 해줘야 함
                        String formattedDate = String.format("%04d/%02d/%02d", selectedYear, selectedMonth + 1, selectedDay);
                        dropdownDate.setText(formattedDate);
                    },
                    year, month, day);

            datePickerDialog.show();
        });

    }
}
