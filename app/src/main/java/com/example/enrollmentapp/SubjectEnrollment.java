package com.example.enrollmentapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectEnrollment extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RecyclerView recyclerView;
    private SubjectAdapter subjectAdapter;
    private List<Subject> subjectList = new ArrayList<>();
    private List<Subject> selectedSubjects = new ArrayList<>();
    private Map<Integer, String> subjectDocumentIds = new HashMap<>();
    private int totalCredits = 0;
    private int studentCurrentCredits = 0;
    private List<String> enrolledSubjectIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_enrollment);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter here
        subjectAdapter = new SubjectAdapter(this, subjectList);
        recyclerView.setAdapter(subjectAdapter);

        // Load student credits first, then subjects
        loadStudentCredits();
        loadSubjects();

        findViewById(R.id.enrollButton).setOnClickListener(v -> enrollSubjects());
    }

    private void loadStudentCredits() {
        String studentId = mAuth.getCurrentUser().getUid();

        db.collection("Students")
                .document(studentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        studentCurrentCredits = documentSnapshot.contains("totalCredits") ?
                                documentSnapshot.getLong("totalCredits").intValue() : 0;
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SubjectEnrollment.this, "Failed to load student data.", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadSubjects() {
        String studentId = mAuth.getCurrentUser().getUid();

        db.collection("Students")
                .document(studentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> tempEnrolledSubjects = (List<String>) documentSnapshot.get("enrolledSubjects");
                        enrolledSubjectIds = tempEnrolledSubjects != null ? tempEnrolledSubjects : new ArrayList<>();
                        loadAvailableSubjects();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SubjectEnrollment.this, "Failed to load student data.", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadAvailableSubjects() {
        db.collection("Subjects")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    subjectList.clear();
                    subjectDocumentIds.clear(); // Clear existing document IDs
                    int position = 0;
                    for (DocumentSnapshot document : querySnapshot) {
                        String documentId = document.getId();
                        if (!enrolledSubjectIds.contains(documentId)) {
                            Subject subject = document.toObject(Subject.class);
                            if (subject != null) {
                                subjectList.add(subject);
                                subjectDocumentIds.put(position, documentId); // Store the document ID with its position
                                position++;
                            }
                        }
                    }
                    subjectAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SubjectEnrollment.this, "Failed to load subjects.", Toast.LENGTH_SHORT).show();
                });
    }

    private void enrollSubjects() {
        selectedSubjects.clear();
        List<String> selectedSubjectIds = new ArrayList<>();
        totalCredits = studentCurrentCredits;

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View view = recyclerView.getChildAt(i);
            if (view != null) {
                CheckBox checkBox = view.findViewById(R.id.subjectCheckBox);
                if (checkBox != null && checkBox.isChecked()) {
                    Subject subject = subjectList.get(i);
                    selectedSubjects.add(subject);
                    String documentId = subjectDocumentIds.get(i);
                    if (documentId != null) {
                        selectedSubjectIds.add(documentId);
                    }
                    totalCredits += subject.getCredits();
                }
            }
        }

        if (selectedSubjects.isEmpty()) {
            Toast.makeText(this, "Please select at least one subject.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (totalCredits > 24) {
            Toast.makeText(this, "Total credits exceed 24. Please select fewer subjects.", Toast.LENGTH_SHORT).show();
            return;
        }

        enrollInSubjects(selectedSubjectIds);
    }

    private void enrollInSubjects(List<String> selectedSubjectIds) {
        String studentId = mAuth.getCurrentUser().getUid();

        db.collection("Students")
                .document(studentId)
                .update(
                        "enrolledSubjects", FieldValue.arrayUnion(selectedSubjectIds.toArray()),
                        "totalCredits", totalCredits
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SubjectEnrollment.this, "Enrollment Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SubjectEnrollment.this, EnrollmentSummary.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SubjectEnrollment.this, "Enrollment Failed. Try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
