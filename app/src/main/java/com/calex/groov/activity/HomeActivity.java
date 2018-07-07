package com.calex.groov.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.calex.groov.R;
import com.calex.groov.app.GroovApplication;
import com.calex.groov.data.GroovDatabase;
import com.calex.groov.data.entities.Challenge;

import javax.inject.Inject;

public class HomeActivity extends AppCompatActivity {

  private static final long NO_LAST_CHALLENGE_KEY = -1;

  @Inject public SharedPreferences sharedPreferences;
  @Inject public GroovDatabase database;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.home);
    ((GroovApplication) getApplication()).getApplicationComponent().inject(this);

    long latestChallengeKey = sharedPreferences.getLong(Extras.KEY, NO_LAST_CHALLENGE_KEY);
    if (latestChallengeKey != NO_LAST_CHALLENGE_KEY) {
      onRememberedChallengeKey(latestChallengeKey);
    } else {
      onNoRememberedChallengeKey();
    }
  }

  private void onRememberedChallengeKey(long challengeKey) {
    database.challengeDao().getAsLiveData(challengeKey).observe(
        this,
        challenge -> {
          if (challenge != null) {
            onRememberedChallengeLoaded(challenge);
          } else {
            onNoRememberedChallengeKey();
          }
          finish();
        });

  }

  private void onRememberedChallengeLoaded(@NonNull Challenge challenge) {
    startActivity(ChallengeActivity.newIntent(this, challenge.getKey()));
    finish();
  }

  private void onNoRememberedChallengeKey() {
    database.challengeDao().getAllAsLiveData().observe(this, challenges -> {
      if (challenges == null || challenges.isEmpty()) {
        startActivity(CreateChallengeActivity.newIntent(this));
        finish();
      } else {
        onRememberedChallengeLoaded(challenges.get(0));
      }
    });
  }
}
