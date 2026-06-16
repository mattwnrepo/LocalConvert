package com.localconvert.app.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.localconvert.app.R;
import com.localconvert.app.converter.ImageConverter;
import com.localconvert.app.utils.FileUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageConvertFragment extends Fragment {

    private Uri selectedUri;
    private ImageView imgPreview, iconPlaceholder;
    private TextView tvPickHint, tvFileName;
    private MaterialButton btnConvert;
    private LinearProgressIndicator progressBar;
    private ChipGroup formatChipGroup;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final ActivityResultLauncher<Intent> pickFileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    receiveFile(result.getData().getData());
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_convert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgPreview      = view.findViewById(R.id.imgPreview);
        iconPlaceholder = view.findViewById(R.id.iconPlaceholder);
        tvPickHint      = view.findViewById(R.id.tvPickHint);
        tvFileName      = view.findViewById(R.id.tvFileName);
        btnConvert      = view.findViewById(R.id.btnConvert);
        progressBar     = view.findViewById(R.id.progressBar);
        formatChipGroup = view.findViewById(R.id.formatChipGroup);

        view.findViewById(R.id.cardPickFile).setOnClickListener(v -> openFilePicker());
        btnConvert.setOnClickListener(v -> startConversion());
    }

    /** Called from MainActivity when a file is shared into the app. */
    public void receiveFile(Uri uri) {
        if (uri == null) return;
        selectedUri = uri;

        String name = FileUtils.getFileName(requireContext(), uri);
        tvFileName.setText(name);
        tvFileName.setVisibility(View.VISIBLE);
        tvPickHint.setVisibility(View.GONE);
        iconPlaceholder.setVisibility(View.GONE);
        imgPreview.setVisibility(View.VISIBLE);
        imgPreview.setImageURI(uri);

        btnConvert.setEnabled(true);
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        pickFileLauncher.launch(intent);
    }

    private void startConversion() {
        if (selectedUri == null) return;

        String format = getSelectedFormat();
        btnConvert.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            boolean success = ImageConverter.convert(requireContext(), selectedUri, format);
            requireActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                btnConvert.setEnabled(true);
                int msgRes = success ? R.string.conversion_success : R.string.conversion_failed;
                Toast.makeText(requireContext(), msgRes, Toast.LENGTH_LONG).show();
            });
        });
    }

    private String getSelectedFormat() {
        int id = formatChipGroup.getCheckedChipId();
        if (id == R.id.chipPng)  return "PNG";
        if (id == R.id.chipWebp) return "WEBP";
        if (id == R.id.chipBmp)  return "BMP";
        return "JPEG"; // default
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
