package com.sbpinilla.consultactivity.data.database.dao

import androidx.room.*

@Dao
interface BaseDAO<T> {
    @Insert
    fun insert(obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplace(obj: T)

    @Insert
    fun insert(vararg obj: T)

    @Update
    fun update(obj: T)

    @Delete
    fun delete(obj: T)
}