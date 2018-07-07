package com.calex.groov.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.calex.groov.data.entities.RepSet;

@Dao
public interface RepSetDao {
  @Query("SELECT SUM(reps) FROM repset WHERE challenge_key IS :challengeKey AND timestamp BETWEEN :startTimestamp AND :endTimestamp")
  LiveData<Integer> totalReps(
      long challengeKey, long startTimestamp, long endTimestamp);

  @Query("SELECT * FROM repset WHERE challenge_key IS :challengeKey AND timestamp BETWEEN :startTimestamp AND :endTimestamp ORDER BY timestamp DESC LIMIT 1")
  LiveData<RepSet> latestRepSet(long challengeKey, long startTimestamp, long endTimestamp);

  @Insert
  void insert(RepSet repSet);
}
