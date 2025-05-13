package com.example.moneyfy.ui.home.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.moneyfy.ui.home.bottomsheet.BottomSheetExpense;
import com.example.moneyfy.ui.home.bottomsheet.BottomSheetIncome;
import com.example.moneyfy.data.model.ExpenseItem;
public class BottomSheetPagerAdapter extends FragmentStateAdapter {

    private final ExpenseItem item;
    private final boolean isEditMode;

    // 생성자 1: 새로 추가용 (BottomSheetAdd)
    public BottomSheetPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
        this.item = null;
        this.isEditMode = false;
    }

    // 생성자 2: 수정용 (BottomSheetEditDel)
    public BottomSheetPagerAdapter(@NonNull Fragment fragment, ExpenseItem item, boolean isEditMode) {
        super(fragment);
        this.item = item;
        this.isEditMode = isEditMode;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return BottomSheetExpense.newInstance(item, isEditMode);
        } else {
            return BottomSheetIncome.newInstance(item, isEditMode);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
