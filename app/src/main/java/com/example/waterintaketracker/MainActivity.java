
package com.example.waterintaketracker;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText edtAmount;
    Button btnAdd;
    TextView txtTotal;
    ListView listIntakes;

    DatabaseHelper dbHelper;

    ArrayList<String> intakeList;
    ArrayList<Integer> intakeIds;
    ArrayList<Integer> intakeAmounts;
    ArrayList<String> intakeDateTimes;
    ArrayAdapter<String> adapter;
    EditText edtGoal;
    Button btnSaveGoal;
    TextView txtProgress, txtRemaining;
    ProgressBar progressWater;

    SharedPreferences preferences;

    int dailyGoal = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtAmount = findViewById(R.id.edtAmount);
        btnAdd = findViewById(R.id.btnAdd);
        txtTotal = findViewById(R.id.txtTotal);
        listIntakes = findViewById(R.id.listIntakes);
        edtGoal = findViewById(R.id.edtGoal);
        btnSaveGoal = findViewById(R.id.btnSaveGoal);
        txtProgress = findViewById(R.id.txtProgress);
        txtRemaining = findViewById(R.id.txtRemaining);
        progressWater = findViewById(R.id.progressWater);

        preferences = getSharedPreferences(
                "WaterTrackerPrefs",
                MODE_PRIVATE
        );

        dailyGoal = preferences.getInt("dailyGoal", 2000);

        edtGoal.setText(String.valueOf(dailyGoal));

        btnSaveGoal.setOnClickListener(v -> saveGoal());

        dbHelper = new DatabaseHelper(this);

        intakeList = new ArrayList<>();
        intakeIds = new ArrayList<>();
        intakeAmounts = new ArrayList<>();
        intakeDateTimes = new ArrayList<>();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                intakeList
        );

        listIntakes.setAdapter(adapter);

        listIntakes.setOnItemClickListener((parent, view, position, id) -> {
            showEditDialog(position);
        });


        btnAdd.setOnClickListener(v -> addWater());


        listIntakes.setOnItemLongClickListener(
                (parent, view, position, id) -> {

                    int recordId = intakeIds.get(position);

                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("Delete Record")
                            .setMessage("Are you sure you want to delete this intake record?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                boolean deleted = dbHelper.deleteIntake(recordId);

                                if (deleted) {
                                    Toast.makeText(this,
                                            "Record deleted",
                                            Toast.LENGTH_SHORT).show();
                                    loadIntakes();
                                } else {
                                    Toast.makeText(this,
                                            "Unable to delete record",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();

                    return true;
                });


        loadIntakes();
    }

    private void showEditDialog(int position) {
        EditText input = new EditText(this);
        input.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
        );
        input.setHint("Water amount in ml");
        input.setText(
                String.valueOf(intakeAmounts.get(position))
        );

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Edit Water Intake")
                .setMessage("Enter the corrected amount:")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String text = input.getText().toString().trim();

                    if (text.isEmpty()) {
                        Toast.makeText(this,
                                "Please enter an amount",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int amount = Integer.parseInt(text);

                        if (amount <= 0) {
                            Toast.makeText(this,
                                    "Amount must be greater than zero",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        int recordId = intakeIds.get(position);
                        String dateTime =
                                intakeDateTimes.get(position);

                        boolean updated = dbHelper.updateIntake(
                                recordId, amount, dateTime
                        );

                        if (updated) {
                            Toast.makeText(this,
                                    "Record updated",
                                    Toast.LENGTH_SHORT).show();
                            loadIntakes();
                        } else {
                            Toast.makeText(this,
                                    "Unable to update record",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (NumberFormatException e) {
                        Toast.makeText(this,
                                "Enter a valid amount",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addWater() {
        String amountText = edtAmount.getText().toString().trim();

        if (amountText.isEmpty()) {
            edtAmount.setError("Enter water amount");
            return;
        }

        int amount;

        try {
            amount = Integer.parseInt(amountText);
        } catch (NumberFormatException e) {
            edtAmount.setError("Enter a valid amount");
            return;
        }

        if (amount <= 0) {
            edtAmount.setError("Amount must be greater than zero");
            return;
        }

        String dateTime = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        boolean inserted = dbHelper.addIntake(amount, dateTime);

        if (inserted) {
            Toast.makeText(this, "Water intake saved!",
                    Toast.LENGTH_SHORT).show();

            edtAmount.setText("");
            loadIntakes();
        } else {
            Toast.makeText(this, "Failed to save record",
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void saveGoal() {
        String goalText = edtGoal.getText().toString().trim();

        if (goalText.isEmpty()) {
            edtGoal.setError("Enter a daily goal");
            return;
        }

        int goal;

        try {
            goal = Integer.parseInt(goalText);
        } catch (NumberFormatException e) {
            edtGoal.setError("Enter a valid goal");
            return;
        }

        if (goal <= 0) {
            edtGoal.setError("Goal must be greater than zero");
            return;
        }

        dailyGoal = goal;

        preferences.edit()
                .putInt("dailyGoal", dailyGoal)
                .apply();

        Toast.makeText(this, "Daily goal saved!",
                Toast.LENGTH_SHORT).show();

        loadIntakes();
    }

    private void loadIntakes() {
        intakeList.clear();
        intakeIds.clear();
        intakeAmounts.clear();
        intakeDateTimes.clear();

        int total = 0;

        String today = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        Cursor cursor = dbHelper.getAllIntakes();

        try {
            int idIndex = cursor.getColumnIndexOrThrow("id");
            int amountIndex = cursor.getColumnIndexOrThrow("amount_ml");
            int dateIndex = cursor.getColumnIndexOrThrow("date_time");

            while (cursor.moveToNext()) {
                int recordId = cursor.getInt(idIndex);
                int amount = cursor.getInt(amountIndex);
                String dateTime = cursor.getString(dateIndex);

                intakeIds.add(recordId);

                intakeList.add(
                        amount + " ml  -  " + dateTime
                );
                intakeAmounts.add(amount);
                intakeDateTimes.add(dateTime);

                if (dateTime.startsWith(today)) {
                    total += amount;
                }
            }
        } finally {
            cursor.close();
        }

        txtTotal.setText(total + " ml");

        int percentage = (int) Math.min(
                100L,
                (long) total * 100 / dailyGoal
        );

        progressWater.setProgress(percentage);

        txtProgress.setText(percentage + "% completed");

        int remaining = Math.max(0, dailyGoal - total);

        if (remaining > 0) {
            txtRemaining.setText(remaining + " ml remaining");
        } else {
            txtRemaining.setText(
                    "Daily goal achieved! Great job!"
            );
        }

        adapter.notifyDataSetChanged();
    }
}
