package com.calex.groov.view;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.calex.groov.R;
import com.calex.groov.data.entities.Challenge;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChallengeView {

  public interface Callbacks {
    void onDidButtonClicked();
    void onDidDifferentRepsClicked();
    void onCreateNewChallengeButtonClicked();
    void onSwitchChallenge(long challengeKey);
  }

  private final Spinner challengeSpinner;
  private final ChallengeSpinnerAdapter challengeSpinnerAdapter;
  private final TextView countView;
  private final TextView lastSetView;
  private final Button didButton;

  private Callbacks callbacks;

  public ChallengeView(View view) {
    challengeSpinner = Preconditions.checkNotNull(view.findViewById(R.id.challenge_spinner));
    countView = Preconditions.checkNotNull(view.findViewById(R.id.count));
    lastSetView = Preconditions.checkNotNull(view.findViewById(R.id.last_set));
    didButton = Preconditions.checkNotNull(view.findViewById(R.id.did_button));
    didButton.setOnClickListener(v -> callbacks.onDidButtonClicked());
    TextView didDifferentReps =
        Preconditions.checkNotNull(view.findViewById(R.id.did_different_reps));
    didDifferentReps.setPaintFlags(didDifferentReps.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    didDifferentReps.setText(Html.fromHtml(didDifferentReps.getText().toString(), 0));
    didDifferentReps.setOnClickListener(v -> callbacks.onDidDifferentRepsClicked());

    challengeSpinnerAdapter = new ChallengeSpinnerAdapter(view.getContext());
    challengeSpinner.setAdapter(challengeSpinnerAdapter);
    challengeSpinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(
              AdapterView<?> adapterView, View view, int position, long itemId) {
            if (itemId == ChallengeSpinnerAdapter.CREATE_CHALLENGE_ITEM_ID) {
              callbacks.onCreateNewChallengeButtonClicked();
            } else {
              callbacks.onSwitchChallenge(itemId);
            }
          }

          @Override
          public void onNothingSelected(AdapterView<?> adapterView) {}
        });
  }

  public void setCallbacks(Callbacks callbacks) {
    this.callbacks = callbacks;
  }

  public void setChallenges(List<Challenge> challenges, int selectedPosition) {
    challengeSpinnerAdapter.setChallenges(challenges);
    challengeSpinner.setSelection(selectedPosition);
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

  private static class ChallengeSpinnerAdapter extends BaseAdapter {

    public static final long CREATE_CHALLENGE_ITEM_ID = Long.MIN_VALUE;

    private final Context context;
    private final List<Challenge> challenges;

    public ChallengeSpinnerAdapter(Context context) {
      this.context = Preconditions.checkNotNull(context);
      challenges = new ArrayList<>();
    }

    public void setChallenges(List<Challenge> challenges) {
      this.challenges.clear();
      this.challenges.addAll(challenges);
      notifyDataSetChanged();
    }

    @Override
    public int getCount() {
      return challenges.size() + 1;
    }

    @Override
    public Challenge getItem(int position) {
      return challenges.get(position);
    }

    @Override
    public long getItemId(int position) {
      return position < challenges.size() ? getItem(position).getKey() : CREATE_CHALLENGE_ITEM_ID;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
      if (view == null) {
        view = View.inflate(context, R.layout.challenge_spinner_item, null);
      }
      if (position < challenges.size()) {
        ((TextView) view.findViewById(R.id.name)).setText(getItem(position).getName());
      } else {
        ((TextView) view.findViewById(R.id.name)).setText(R.string.create_challenge_spinner_item);
      }
      return view;
    }
  }
}
