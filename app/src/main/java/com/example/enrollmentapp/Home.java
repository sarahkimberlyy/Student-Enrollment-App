package com.example.enrollmentapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Home extends AppCompatActivity {

    private Button subjectEnrollmentButton, enrollmentSummaryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        subjectEnrollmentButton = findViewById(R.id.subjectEnrollmentButton);
        enrollmentSummaryButton = findViewById(R.id.enrollmentSummaryButton);

        subjectEnrollmentButton.setOnClickListener(v -> startActivity(new Intent(Home.this, SubjectEnrollment.class)));
        enrollmentSummaryButton.setOnClickListener(v -> startActivity(new Intent(Home.this, EnrollmentSummary.class)));
    }
}
