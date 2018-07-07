package com.calex.groov.presenter;

import android.app.AlertDialog;
import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.calex.groov.R;
import com.calex.groov.activity.Extras;
import com.calex.groov.data.GroovDatabase;
import com.calex.groov.data.entities.Challenge;
import com.calex.groov.data.entities.RepSet;
import com.calex.groov.util.Clock;
import com.calex.groov.view.ChallengeView;
import com.calex.groov.workers.RecordSetWorker;
import com.google.common.base.Preconditions;

import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class ChallengePresenter {

  private static final long LAST_REP_TEXT_UPDATE_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);

  private final Context context;
  private final ChallengeView view;
  private final Clock clock;
  private final Handler handler;
  private final Object handlerToken;

  private Challenge challenge;
  private RepSet latestRepSet;
  private int totalReps;

  public ChallengePresenter(
      long challengeKey,
      Context context,
      LifecycleOwner lifecycleOwner,
      GroovDatabase database,
      ChallengeView view,
      Clock clock,
      SharedPreferences sharedPreferences,
      Handler handler) {
    this.context = Preconditions.checkNotNull(context);
    this.view = Preconditions.checkNotNull(view);
    this.clock = Preconditions.checkNotNull(clock);
    this.handler = Preconditions.checkNotNull(handler);
    handlerToken = new Object();
    view.setCallbacks(new ChallengeView.Callbacks() {
      @Override
      public void onDidButtonClicked() {
        ChallengePresenter.this.onDidButtonClicked();
      }

      @Override
      public void onDidDifferentRepsClicked() {
        ChallengePresenter.this.onDidDifferentRepsClicked();
      }
    });
    lifecycleOwner.getLifecycle().addObserver((GenericLifecycleObserver) (source, event) -> {
      switch (event) {
        case ON_RESUME:
          handler.postDelayed(
              this::onMinuteElapsed, handlerToken, LAST_REP_TEXT_UPDATE_INTERVAL_MS);
          break;

        case ON_PAUSE:
          handler.removeCallbacksAndMessages(handlerToken);
          break;
      }
    });
    database.challengeDao().getAsLiveData(challengeKey)
        .observe(lifecycleOwner, this::onChallengeChanged);
    database.repSetDao().totalReps(
        challengeKey, clock.todayStartTimestamp(), clock.todayEndTimestamp()).observe(
            lifecycleOwner, this::onRepsChanged);
    database.repSetDao().latestRepSet(
        challengeKey, clock.todayStartTimestamp(), clock.todayEndTimestamp()).observe(
            lifecycleOwner, this::onLatestRepSetChanged);
    sharedPreferences.edit().putLong(Extras.KEY, challengeKey).apply();
  }

  private void onLatestRepSetChanged(RepSet latestRepSet) {
    this.latestRepSet = latestRepSet;
    updateView();
  }

  protected void onChallengeChanged(Challenge challenge) {
    this.challenge = challenge;
    updateView();
  }

  private void onRepsChanged(@Nullable Integer totalReps) {
    this.totalReps = totalReps != null ? totalReps : 0;
    updateView();
  }

  private void updateView() {
    if (challenge == null) {
      return;
    }

    view.setCount(totalReps);
    view.setChallengeName(challenge.getName());
    view.setDidButtonText(
        context.getString(R.string.did_button_template, challenge.getRepsInSetGoal()));
    view.setLastSetText(generateLastSetText());
  }

  private void onDidButtonClicked() {
    WorkManager.getInstance().enqueue(new OneTimeWorkRequest.Builder(RecordSetWorker.class)
        .setInputData(new Data.Builder()
            .putLong(Extras.KEY, challenge.getKey())
            .putInt(Extras.REPS, challenge.getRepsInSetGoal())
            .build())
        .build());
  }

  private void onDidDifferentRepsClicked() {
    View view = View.inflate(context, R.layout.reps_input, null);
    EditText repsView = view.findViewById(R.id.reps);
    new AlertDialog.Builder(context)
        .setView(view)
        .setPositiveButton(R.string.ok, (dialogInterface, which) -> {
            int reps =
              Integer.parseInt(repsView.getText().toString());
          if (reps < 1) {
            new AlertDialog.Builder(context)
                .setMessage(R.string.error_less_than_one_rep)
                .setNeutralButton(R.string.ok, null)
                .show();
            return;
          }
          WorkManager.getInstance().enqueue(
              new OneTimeWorkRequest.Builder(RecordSetWorker.class)
                  .setInputData(new Data.Builder()
                      .putLong(Extras.KEY, challenge.getKey())
                      .putInt(Extras.REPS, reps)
                      .build())
                  .build());
        })
        .setNegativeButton(R.string.cancel, null)
        .show();
    repsView.requestFocus();
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
  }

  private void onMinuteElapsed() {
    view.setLastSetText(generateLastSetText());
    handler.postDelayed(this::onMinuteElapsed, handlerToken, LAST_REP_TEXT_UPDATE_INTERVAL_MS);
  }

  private CharSequence generateLastSetText() {
    if (latestRepSet == null) {
      return context.getString(R.string.no_sets_yet);
    }

    return context.getString(
        R.string.last_set_template,
        latestRepSet.getReps(),
        DateUtils.getRelativeTimeSpanString(
            latestRepSet.getTimestamp(), clock.currentTimeMs(), LAST_REP_TEXT_UPDATE_INTERVAL_MS));
  }
}
