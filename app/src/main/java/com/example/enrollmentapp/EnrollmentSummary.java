package com.example.enrollmentapp;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentSummary extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RecyclerView enrolledSubjectsRecyclerView;
    private EnrolledSubjectAdapter enrolledSubjectsAdapter;
    private TextView totalCreditsTextView;

    private List<Subject> enrolledSubjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment_summary);

        enrolledSubjectsRecyclerView = findViewById(R.id.enrolledSubjectsRecyclerView);
        totalCreditsTextView = findViewById(R.id.totalCreditsTextView);

        enrolledSubjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with empty list
        enrolledSubjectsAdapter = new EnrolledSubjectAdapter(this, enrolledSubjects);
        enrolledSubjectsRecyclerView.setAdapter(enrolledSubjectsAdapter);

        loadEnrolledSubjects();
    }

    private void loadEnrolledSubjects() {
        String studentId = mAuth.getCurrentUser().getUid();

        db.collection("Students")
                .document(studentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get enrolled subject IDs and total credits
                        List<String> subjectIds = (List<String>) documentSnapshot.get("enrolledSubjects");
                        Long totalCredits = documentSnapshot.getLong("totalCredits");

                        // Update total credits display
                        if (totalCredits != null) {
                            totalCreditsTextView.setText("Total Credits: " + totalCredits);
                        }

                        // Load subject details if there are enrolled subjects
                        if (subjectIds != null && !subjectIds.isEmpty()) {
                            loadSubjectDetails(subjectIds);
                        } else {
                            Toast.makeText(EnrollmentSummary.this,
                                    "No subjects enrolled yet.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EnrollmentSummary.this,
                            "Failed to load enrollment data.", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadSubjectDetails(List<String> subjectIds) {
        enrolledSubjects.clear(); // Clear existing subjects

        // Create a batch query to get all subjects at once
        for (String subjectId : subjectIds) {
            db.collection("Subjects")
                    .document(subjectId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Subject subject = documentSnapshot.toObject(Subject.class);
                            if (subject != null) {
                                enrolledSubjects.add(subject);
                                enrolledSubjectsAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EnrollmentSummary.this,
                                "Failed to load some subject details.", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}