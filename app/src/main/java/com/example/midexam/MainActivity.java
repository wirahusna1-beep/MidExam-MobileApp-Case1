package com.example.midexam;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText etStudentName, etStudentId;
    SeekBar sbStudentAge;
    TextView tvAgeValue;
    Button btnAdd, btnEdit, btnDelete, btnView;
    int selectedAge = 17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);

        etStudentName = findViewById(R.id.etStudentName);
        etStudentId = findViewById(R.id.etStudentId);
        sbStudentAge = findViewById(R.id.sbStudentAge);
        tvAgeValue = findViewById(R.id.tvAgeValue);

        btnAdd = findViewById(R.id.btnAdd);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnView = findViewById(R.id.btnView);

        sbStudentAge.setMin(17);
        sbStudentAge.setMax(50);
        sbStudentAge.setProgress(17);

        sbStudentAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 17) {
                    progress = 17;
                    seekBar.setProgress(17);
                }
                selectedAge = progress;
                tvAgeValue.setText("Student Age: " + selectedAge);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        setupAddButton();
        setupEditButton();
        setupDeleteButton();
        setupViewButton();
    }

    private void setupAddButton() {
        btnAdd.setOnClickListener(v -> {
            String name = etStudentName.getText().toString().trim();
            String id = etStudentId.getText().toString().trim();

            if (name.isEmpty() || id.isEmpty()) {
                Toast.makeText(this, "Student Name dan Student ID harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isInserted = myDb.insertData(id, name, selectedAge);
            if (isInserted) {
                Toast.makeText(MainActivity.this, "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(MainActivity.this, "Gagal menambahkan data (Student ID sudah ada).", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEditButton() {
        btnEdit.setOnClickListener(v -> {
            String name = etStudentName.getText().toString().trim();
            String id = etStudentId.getText().toString().trim();

            if (name.isEmpty() || id.isEmpty()) {
                Toast.makeText(this, "Student Name dan Student ID harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isUpdated = myDb.updateData(id, name, selectedAge);
            if (isUpdated) {
                Toast.makeText(MainActivity.this, "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(MainActivity.this, "Student ID tidak ditemukan!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDeleteButton() {
        btnDelete.setOnClickListener(v -> {
            String id = etStudentId.getText().toString().trim();
            if (id.isEmpty()) {
                Toast.makeText(MainActivity.this, "Masukkan Student ID yang ingin dihapus!", Toast.LENGTH_SHORT).show();
                return;
            }

            Integer deletedRows = myDb.deleteData(id);
            if (deletedRows > 0) {
                Toast.makeText(MainActivity.this, "Data berhasil dihapus!", Toast.LENGTH_SHORT).show();
                clearFields();
            } else {
                Toast.makeText(MainActivity.this, "Student ID tidak ditemukan!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewButton() {
        btnView.setOnClickListener(v -> {
            Cursor res = myDb.getAllData();
            if (res.getCount() == 0) {
                showMessage("Informasi", "Belum ada data mahasiswa yang tersimpan.");
                return;
            }

            StringBuilder buffer = new StringBuilder();
            while (res.moveToNext()) {
                buffer.append("Student ID   : ").append(res.getString(0)).append("\n");
                buffer.append("Student Name : ").append(res.getString(1)).append("\n");
                buffer.append("Student Age  : ").append(res.getInt(2)).append("\n\n");
            }
            res.close();

            showMessage("Data Mahasiswa", buffer.toString());
        });
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void clearFields() {
        etStudentName.setText("");
        etStudentId.setText("");
        sbStudentAge.setProgress(17);
        selectedAge = 17;
        tvAgeValue.setText("Student Age: 17");
    }
}