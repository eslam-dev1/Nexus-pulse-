package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Transaction
    @Query("SELECT * FROM contacts ORDER BY name ASC")
    fun getContactsWithProfiles(): Flow<List<ContactWithProfiles>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getContactById(id: Long): Flow<ContactEntity?>

    @Transaction
    @Query("SELECT * FROM contacts WHERE id = :id")
    fun getContactWithProfiles(id: Long): Flow<ContactWithProfiles?>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactDirect(id: Long): ContactEntity?

    @Query("SELECT * FROM contacts")
    suspend fun getAllContactsDirect(): List<ContactEntity>

    @Transaction
    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContactWithProfilesDirect(id: Long): ContactWithProfiles?

    @Query("SELECT COUNT(*) FROM contacts")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: ContactEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<ContactEntity>)

    @Update
    suspend fun update(contact: ContactEntity)

    @Query("UPDATE contacts SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteById(id: Long)
}
