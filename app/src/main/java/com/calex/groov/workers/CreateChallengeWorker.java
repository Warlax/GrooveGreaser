package com.calex.groov.workers;

import android.support.annotation.NonNull;

import com.calex.groov.activity.Extras;
import com.calex.groov.app.GroovApplication;
import com.calex.groov.data.GroovDatabase;
import com.calex.groov.data.entities.Challenge;

import javax.inject.Inject;

import androidx.work.Data;
import androidx.work.Worker;

public class CreateChallengeWorker extends Worker {

  @Inject public GroovDatabase database;

  @NonNull
  @Override
  public Result doWork() {
    ((GroovApplication) getApplicationContext()).getApplicationComponent().inject(this);
    Data data = getInputData();
    Challenge challenge = new Challenge();
    challenge.setName(data.getString(Extras.NAME, null));
    challenge.setRepsInSetGoal(data.getInt(Extras.REPS, -1));
    challenge.setSetDurationMins(data.getInt(Extras.DURATION, -1));
    challenge.setRemind(data.getBoolean(Extras.REMIND, false));
    long key = database.challengeDao().insert(challenge);

    setOutputData(new Data.Builder().putLong(Extras.KEY, key).build());

    return Result.SUCCESS;
  }
}
