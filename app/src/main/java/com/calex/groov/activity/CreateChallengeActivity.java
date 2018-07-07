package com.calex.groov.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.calex.groov.R;
import com.calex.groov.app.GroovApplication;
import com.calex.groov.data.GroovDatabase;
import com.calex.groov.workers.CreateChallengeWorker;

import javax.inject.Inject;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.State;
import androidx.work.WorkManager;

public class CreateChallengeActivity extends AppCompatActivity {

  public static Intent newIntent(Context context) {
    return new Intent(context, CreateChallengeActivity.class);
  }

  @Inject public GroovDatabase database;

  private EditText nameView;
  private EditText repsInSetView;
  private EditText restDurationView;
  private CheckBox remindView;
  private View progressView;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.create_challenge);
    ((GroovApplication) getApplication()).getApplicationComponent().inject(this);

    nameView = findViewById(R.id.name);
    repsInSetView = findViewById(R.id.reps_in_set);
    restDurationView = findViewById(R.id.rest_duration);
    remindView = findViewById(R.id.remind);
    progressView = findViewById(R.id.progress);
    progressView.setVisibility(View.GONE);

    findViewById(R.id.create_button).setOnClickListener(view -> onCreateButtonClicked());
    findViewById(R.id.cancel_button).setOnClickListener(view -> onCancelButtonClicked());
  }

  private void onCreateButtonClicked() {
    progressView.setVisibility(View.VISIBLE);
    OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(CreateChallengeWorker.class)
        .setInputData(new Data.Builder()
                .putString(Extras.NAME, nameView.getText().toString())
                .putInt(Extras.REPS, Integer.parseInt(repsInSetView.getText().toString()))
                .putInt(Extras.DURATION, Integer.parseInt(restDurationView.getText().toString()))
                .putBoolean(Extras.REMIND, remindView.isChecked())
                .build())
        .build();
    WorkManager.getInstance().enqueue(workRequest);
    WorkManager.getInstance().getStatusById(workRequest.getId()).observe(
        this,
        workStatus -> {
          if (workStatus != null && workStatus.getState() == State.SUCCEEDED) {
            finish();
            startActivity(
                ChallengeActivity.newIntent(
                    CreateChallengeActivity.this,
                    workStatus.getOutputData().getLong(Extras.KEY, -1)));
          }
        });
  }

  private void onCancelButtonClicked() {
    finish();
  }
}
