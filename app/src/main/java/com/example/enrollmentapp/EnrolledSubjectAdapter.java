package com.example.enrollmentapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EnrolledSubjectAdapter extends RecyclerView.Adapter<EnrolledSubjectAdapter.SubjectViewHolder> {
    private Context context;
    private List<Subject> subjectList;

    // Constructor
    public EnrolledSubjectAdapter(Context context, List<Subject> subjectList) {
        this.context = context;
        this.subjectList = subjectList;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your item layout for summary view (without checkbox)
        View itemView = LayoutInflater.from(context).inflate(R.layout.subject_item, parent, false);
        return new SubjectViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.subjectName.setText(subject.getName());
        holder.subjectCredits.setText(String.valueOf(subject.getCredits()));

        // Hide checkbox in the summary adapter
        holder.subjectCheckBox.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    // ViewHolder class
    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName, subjectCredits;
        CheckBox subjectCheckBox;

        public SubjectViewHolder(View itemView) {
            super(itemView);
            subjectName = itemView.findViewById(R.id.subjectName);
            subjectCredits = itemView.findViewById(R.id.subjectCredits);
            subjectCheckBox = itemView.findViewById(R.id.subjectCheckBox);
        }
    }
}

