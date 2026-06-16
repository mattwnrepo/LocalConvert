package com.localconvert.app.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import com.localconvert.app.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ChipGroup chipGroup = findViewById(R.id.chipGroup);

        // Show image fragment by default
        if (savedInstanceState == null) {
            loadFragment(new ImageConvertFragment());
        }

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipImage) {
                loadFragment(new ImageConvertFragment());
            } else {
                // Other categories not yet implemented
                Toast.makeText(this, getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                // Re-select image chip
                group.check(R.id.chipImage);
            }
        });

        // Handle files shared from other apps
        handleIncomingIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingIntent(intent);
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent == null) return;
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (uri != null) {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                if (f instanceof ImageConvertFragment) {
                    ((ImageConvertFragment) f).receiveFile(uri);
                }
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
