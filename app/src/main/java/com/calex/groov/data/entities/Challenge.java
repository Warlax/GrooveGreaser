package com.calex.groov.data.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Challenge {
  @PrimaryKey(autoGenerate = true)
  @NonNull
  @ColumnInfo(name = "model_key")
  private long key;

  @ColumnInfo(name = "name")
  private String name;

  @ColumnInfo(name = "reps_in_set_goal")
  private int repsInSetGoal;

  @ColumnInfo(name = "set_duration_mins")
  private int setDurationMins;

  @ColumnInfo(name = "remind")
  private boolean remind;

  public long getKey() {
    return key;
  }

  public void setKey(long key) {
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getRepsInSetGoal() {
    return repsInSetGoal;
  }

  public void setRepsInSetGoal(int repsInSetGoal) {
    this.repsInSetGoal = repsInSetGoal;
  }

  public int getSetDurationMins() {
    return setDurationMins;
  }

  public void setSetDurationMins(int setDurationMins) {
    this.setDurationMins = setDurationMins;
  }

  public boolean isRemind() {
    return remind;
  }

  public void setRemind(boolean remind) {
    this.remind = remind;
  }
}
