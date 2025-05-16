package data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.model.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE login = :login AND password = :password")
    suspend fun findUser(login: String, password: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(users: List<UserEntity>)
}