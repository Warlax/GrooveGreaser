package com.calex.groov.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.calex.groov.R;
import com.calex.groov.app.GroovApplication;
import com.calex.groov.data.GroovDatabase;
import com.calex.groov.data.entities.Challenge;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.inject.Inject;

public class ChallengesActivity extends AppCompatActivity {

  public static Intent newIntent(Context context) {
    return new Intent(context, ChallengesActivity.class);
  }

  @Inject public GroovDatabase database;

  private RecyclerView challengesView;
  private View progressView;
  private View emptyView;
  private ChallengeAdapter adapter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.challenges);
    ((GroovApplication) getApplication()).getApplicationComponent().inject(this);

    challengesView = findViewById(R.id.challenges);
    progressView = findViewById(R.id.progress);
    emptyView = findViewById(R.id.empty);
    findViewById(R.id.create_button).setOnClickListener(view -> onCreateChallengeButtonClicked());

    challengesView.setLayoutManager(new LinearLayoutManager(this));
    adapter = new ChallengeAdapter(this);
    challengesView.setAdapter(adapter);

    challengesView.setVisibility(View.GONE);
    emptyView.setVisibility(View.GONE);
    database.challengeDao().getAllAsLiveData().observe(
        this,
        challenges -> {
          adapter.setChallenges(challenges);
          challengesView.setVisibility(!challenges.isEmpty() ? View.VISIBLE : View.GONE);
          emptyView.setVisibility(challenges.isEmpty() ? View.VISIBLE : View.GONE);
          progressView.setVisibility(View.GONE);
        });
  }

  private void onCreateChallengeButtonClicked() {
    startActivity(CreateChallengeActivity.newIntent(this));
  }

  private void onChallengeItemClicked(Challenge challenge) {
    startActivity(ChallengeActivity.newIntent(this, challenge.getKey(), true));
  }

  private class ChallengeAdapter extends RecyclerView.Adapter<ChallengeViewHolder> {
    private final Context context;
    private ImmutableList<Challenge> challenges;

    public ChallengeAdapter(Context context) {
      this.context = Preconditions.checkNotNull(context);
      challenges = ImmutableList.of();
    }

    public void setChallenges(List<Challenge> challenges) {
      this.challenges = ImmutableList.copyOf(challenges);
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(
        @NonNull ViewGroup viewGroup, int viewType) {
      return new ChallengeViewHolder(
          LayoutInflater.from(context).inflate(R.layout.challenge_list_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder viewHolder, int position) {
      Challenge challenge = challenges.get(position);
      viewHolder.nameView.setText(challenge.getName());
      viewHolder.bind(challenge);
    }

    @Override
    public int getItemCount() {
      return challenges.size();
    }
  }

  private class ChallengeViewHolder extends RecyclerView.ViewHolder {

    public final TextView nameView;
    private Challenge challenge;

    public ChallengeViewHolder(@NonNull View itemView) {
      super(itemView);
      nameView = Preconditions.checkNotNull(itemView.findViewById(R.id.name));
      itemView.setOnClickListener(view -> onChallengeItemClicked(challenge));
    }

    public void bind(Challenge challenge) {
      this.challenge = challenge;
    }
  }
}
