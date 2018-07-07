package com.calex.groov.view;

import android.graphics.Paint;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.calex.groov.R;
import com.google.common.base.Preconditions;

import java.util.Locale;

public class ChallengeView {

  public interface Callbacks {
    void onDidButtonClicked();
    void onDidDifferentRepsClicked();
  }

  private final TextView challengeNameView;
  private final TextView countView;
  private final TextView lastSetView;
  private final Button didButton;

  private Callbacks callbacks;

  public ChallengeView(View view) {
    this.challengeNameView = Preconditions.checkNotNull(view.findViewById(R.id.challenge_name));
    this.countView = Preconditions.checkNotNull(view.findViewById(R.id.count));
    this.lastSetView = Preconditions.checkNotNull(view.findViewById(R.id.last_set));
    this.didButton = Preconditions.checkNotNull(view.findViewById(R.id.did_button));
    didButton.setOnClickListener(v -> callbacks.onDidButtonClicked());
    TextView didDifferentReps =
        Preconditions.checkNotNull(view.findViewById(R.id.did_different_reps));
    didDifferentReps.setPaintFlags(didDifferentReps.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    didDifferentReps.setText(Html.fromHtml(didDifferentReps.getText().toString(), 0));
    didDifferentReps.setOnClickListener(v -> callbacks.onDidDifferentRepsClicked());
  }

  public void setCallbacks(Callbacks callbacks) {
    this.callbacks = callbacks;
  }

  public void setChallengeName(CharSequence challengeName) {
    challengeNameView.setText(challengeName);
  }

  public void setCount(int count) {
    countView.setText(String.format(Locale.getDefault(), "%d", count));
  }

  public void setLastSetText(CharSequence text) {
    lastSetView.setText(text);
  }

  public void setDidButtonText(CharSequence text) {
    didButton.setText(text);
  }
}
