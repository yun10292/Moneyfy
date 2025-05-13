package com.example.moneyfy.ui.home.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.moneyfy.ui.home.fragment.HomeListFragment;
import com.example.moneyfy.ui.home.fragment.HomeCalendarFragment;

public class HomePagerAdapter extends FragmentStateAdapter {

    public HomePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new HomeListFragment();
        } else {
            return new HomeCalendarFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 일일지출 + 달력
    }
}
