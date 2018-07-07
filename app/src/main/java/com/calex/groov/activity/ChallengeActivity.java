package com.calex.groov.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.calex.groov.R;
import com.calex.groov.app.GroovApplication;
import com.calex.groov.data.GroovDatabase;
import com.calex.groov.presenter.ChallengePresenter;
import com.calex.groov.util.Clock;
import com.calex.groov.view.ChallengeView;

import javax.inject.Inject;

public class ChallengeActivity extends AppCompatActivity {

  public static Intent newIntent(Context context, long challengeKey, boolean fromChallengeList) {
    Intent intent = new Intent(context, ChallengeActivity.class);
    intent.putExtra(Extras.KEY, challengeKey);
    intent.putExtra(Extras.FROM_CHALLENGE_LIST, fromChallengeList);
    return intent;
  }

  @Inject public GroovDatabase database;
  @Inject public Clock clock;
  @Inject public SharedPreferences sharedPreferences;
  @Inject public Handler handler;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.challenge);
    ((GroovApplication) getApplication()).getApplicationComponent().inject(this);

    long challengeKey = getIntent().getLongExtra(Extras.KEY, -1);

    ChallengeView view = new ChallengeView(findViewById(android.R.id.content));
    new ChallengePresenter(
        challengeKey, this, this, database, view, clock, sharedPreferences, handler);
    getSupportActionBar().setDisplayHomeAsUpEnabled(
        getIntent().getBooleanExtra(Extras.FROM_CHALLENGE_LIST, false));
  }
}