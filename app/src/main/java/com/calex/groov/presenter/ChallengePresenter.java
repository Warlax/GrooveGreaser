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
import android.widget.Toast;

import com.calex.groov.R;
import com.calex.groov.activity.Extras;
import com.calex.groov.data.GroovDatabase;
import com.calex.groov.data.entities.Challenge;
import com.calex.groov.data.entities.RepSet;
import com.calex.groov.util.Clock;
import com.calex.groov.view.ChallengeView;
import com.calex.groov.workers.RecordSetWorker;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class ChallengePresenter implements ChallengeView.Callbacks {

  private static final long LAST_REP_TEXT_UPDATE_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);
  private static final int NOT_LOADED = -1;
  private static final int INITIAL_LOAD = -1;

  public interface CreateNewChallengeFlow {
    void start();
  }

  private final Context context;
  private final ChallengeView view;
  private final Clock clock;
  private final Handler handler;
  private final Object handlerToken;
  private final GroovDatabase database;
  private final SharedPreferences sharedPreferences;
  private final LifecycleOwner lifecycleOwner;
  private final CreateNewChallengeFlow createNewChallengeFlow;

  private List<Challenge> challenges;
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
      Handler handler,
      CreateNewChallengeFlow createNewChallengeFlow) {
    this.context = Preconditions.checkNotNull(context);
    this.view = Preconditions.checkNotNull(view);
    this.clock = Preconditions.checkNotNull(clock);
    this.handler = Preconditions.checkNotNull(handler);
    this.database = Preconditions.checkNotNull(database);
    this.sharedPreferences = Preconditions.checkNotNull(sharedPreferences);
    this.lifecycleOwner = Preconditions.checkNotNull(lifecycleOwner);
    this.createNewChallengeFlow = Preconditions.checkNotNull(createNewChallengeFlow);

    handlerToken = new Object();
    view.setCallbacks(this);
    lifecycleOwner.getLifecycle().addObserver((GenericLifecycleObserver) (source, event) -> {
      switch (event) {
        case ON_RESUME:
          handler.postDelayed(
              this::onMinuteElapsed, handlerToken, LAST_REP_TEXT_UPDATE_INTERVAL_MS);
          loadData(challengeKey);
          break;

        case ON_PAUSE:
          handler.removeCallbacksAndMessages(handlerToken);
          break;
      }
    });
  }

  @Override
  public void onDidButtonClicked() {
    WorkManager.getInstance().enqueue(new OneTimeWorkRequest.Builder(RecordSetWorker.class)
        .setInputData(new Data.Builder()
            .putLong(Extras.KEY, challenge.getKey())
            .putInt(Extras.REPS, challenge.getRepsInSetGoal())
            .build())
        .build());
  }

  @Override
  public void onDidDifferentRepsClicked() {
    View view = View.inflate(context, R.layout.reps_input, null);
    EditText repsView = view.findViewById(R.id.reps);
    InputMethodManager imm =
        (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
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
        .setOnDismissListener(
            dialogInterface -> imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0))
        .show();
    repsView.requestFocus();
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
  }

  @Override
  public void onCreateNewChallengeButtonClicked() {
    createNewChallengeFlow.start();
  }

  @Override
  public void onSwitchChallenge(long challengeKey) {
    loadData(challengeKey);
  }

  private void loadData(long challengeKey) {
    totalReps = NOT_LOADED;
    database.challengeDao().getAllAsLiveData().observe(
        lifecycleOwner, this::onChallengesChanged);
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

  private void onChallengesChanged(List<Challenge> challenges) {
    this.challenges = challenges;
    updateView();
  }

  private void onChallengeChanged(Challenge challenge) {
    this.challenge = challenge;
    updateView();
  }

  private void onRepsChanged(@Nullable Integer totalReps) {
    if (totalReps == null) {
      totalReps = 0;
    }

    if (this.totalReps == totalReps) {
      return;
    }

    int diff = this.totalReps != NOT_LOADED ? totalReps - this.totalReps : INITIAL_LOAD;
    if (diff > 0) {
      Toast.makeText(
          context, context.getString(R.string.reps_added, diff), Toast.LENGTH_SHORT).show();
    }
    this.totalReps = totalReps;
    updateView();
  }

  private void updateView() {
    if (challenges == null || challenge == null || totalReps == NOT_LOADED) {
      return;
    }

    view.setCount(totalReps);
    int currentChallengePosition = 0;
    for (int position = 0; position < challenges.size(); position++) {
      if (this.challenge.getKey() == challenges.get(position).getKey()) {
        currentChallengePosition = position;
        break;
      }
    }
    view.setChallenges(challenges, currentChallengePosition);
    view.setDidButtonText(
        context.getString(R.string.did_button_template, challenge.getRepsInSetGoal()));
    view.setLastSetText(generateLastSetText());
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
