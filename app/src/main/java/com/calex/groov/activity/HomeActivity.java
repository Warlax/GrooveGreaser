package com.calex.groov.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.calex.groov.R;
import com.calex.groov.app.GroovApplication;
import com.calex.groov.data.GroovDatabase;

import javax.inject.Inject;

public class HomeActivity extends AppCompatActivity {

  @Inject public SharedPreferences sharedPreferences;
  @Inject public GroovDatabase database;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.home);
    ((GroovApplication) getApplication()).getApplicationComponent().inject(this);

    long latestChallengeKey = sharedPreferences.getLong(Extras.KEY, -1);
    if (latestChallengeKey == -1) {
      startActivity(ChallengesActivity.newIntent(this));
      finish();
      return;
    }

    database.challengeDao().getAsLiveData(latestChallengeKey).observe(
        this,
        challenge -> {
          if (challenge != null) {
            startActivity(ChallengeActivity.newIntent(this, challenge.getKey()));
          } else {
            startActivity(ChallengesActivity.newIntent(this));
          }
          finish();
        });
  }
}
