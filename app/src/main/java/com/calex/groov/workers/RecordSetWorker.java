package com.calex.groov.workers;

import android.support.annotation.NonNull;

import com.calex.groov.activity.Extras;
import com.calex.groov.app.GroovApplication;
import com.calex.groov.data.GroovDatabase;
import com.calex.groov.data.entities.Challenge;
import com.calex.groov.data.entities.RepSet;
import com.calex.groov.util.Clock;

import javax.inject.Inject;

import androidx.work.Data;
import androidx.work.Worker;

public class RecordSetWorker extends Worker {

  @Inject public GroovDatabase database;
  @Inject public Clock clock;

  @NonNull
  @Override
  public Result doWork() {
    ((GroovApplication) getApplicationContext()).getApplicationComponent().inject(this);

    Data inputData = getInputData();
    RepSet repSet = new RepSet();
    long challengeKey = inputData.getLong(Extras.KEY, -1);
    int reps = inputData.getInt(Extras.REPS, -1);
    repSet.setChallengeKey(challengeKey);
    repSet.setReps(reps);
    repSet.setTimestamp(clock.currentTimeMs());

    database.repSetDao().insert(repSet);

    Challenge challenge = database.challengeDao().get(challengeKey);
    if (challenge.getRepsInSetGoal() != reps) {
      challenge.setRepsInSetGoal(reps);
      database.challengeDao().update(challenge);
    }

    return Result.SUCCESS;
  }
}
