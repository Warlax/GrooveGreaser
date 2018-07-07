package com.calex.groov.data.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(
    foreignKeys = {
      @ForeignKey(
          entity = Challenge.class,
          parentColumns = "model_key",
          childColumns = "challenge_key",
          onDelete = ForeignKey.CASCADE),
})
public class RepSet {
  @PrimaryKey(autoGenerate = true)
  @NonNull
  @ColumnInfo(name = "model_key")
  private long key;

  @ColumnInfo(name = "challenge_key")
  private long challengeKey;

  @ColumnInfo(name = "timestamp")
  private long timestamp;

  @ColumnInfo(name = "reps")
  private int reps;

  public long getKey() {
    return key;
  }

  public void setKey(long key) {
    this.key = key;
  }

  public long getChallengeKey() {
    return challengeKey;
  }

  public void setChallengeKey(long challengeKey) {
    this.challengeKey = challengeKey;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public int getReps() {
    return reps;
  }

  public void setReps(int reps) {
    this.reps = reps;
  }
}
