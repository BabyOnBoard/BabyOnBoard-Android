package com.jonathanrufino.babyonboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.jonathanrufino.babyonboard.fragment.BabyCribFragment;
import com.jonathanrufino.babyonboard.fragment.BodySignsFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.navigation_bodysigns:
                        transaction.replace(R.id.main_container, new BodySignsFragment(), BodySignsFragment.TAG).commit();
                        return true;
                    case R.id.navigation_babycrib:
                        transaction.replace(R.id.main_container, new BabyCribFragment(), BodySignsFragment.TAG).commit();
                        return true;
                    case R.id.navigation_connection:
                        return true;
                }
                return false;
            }
        });
    }
}
