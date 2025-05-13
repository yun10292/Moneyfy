package com.example.moneyfy;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔹 nav_host_fragment 가져오기
        NavHostFragment navHostFragment = (NavHostFragment)
                getSupportFragmentManager().findFragmentById(R.id.host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            // 🔹 BottomNavigationView와 NavController 연결
            BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }
    }
}
