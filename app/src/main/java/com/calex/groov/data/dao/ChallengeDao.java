package com.calex.groov.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.calex.groov.data.entities.Challenge;

import java.util.List;

@Dao
public interface ChallengeDao {
  @Query("SELECT * FROM challenge WHERE model_key IS :key LIMIT 1")
  LiveData<Challenge> getAsLiveData(long key);

  @Query("SELECT * FROM challenge WHERE model_key IS :key LIMIT 1")
  Challenge get(long key);

  @Query("SELECT * FROM challenge")
  LiveData<List<Challenge>> getAllAsLiveData();

  @Insert
  long insert(Challenge challenge);

  @Update
  void update(Challenge challenge);
}
