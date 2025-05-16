package data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import data.model.LogEntity
import data.model.MachineEntity
import data.model.MachineStatusEntity
import data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        MachineEntity::class,
        MachineStatusEntity::class,
        UserEntity::class,
        LogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun machineDao(): MachineDao
    abstract fun machineStatusDao(): MachineStatusDao
    abstract fun userDao(): UserDao
    abstract fun logDao(): LogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                val callback = object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                database.prepopulateData()
                            }
                        }
                    }
                }

                val database = Room
                    .databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "machine_db"
                    )
                    .fallbackToDestructiveMigration()
                    .addCallback(callback)
                    .build()

                INSTANCE = database
                database
            }
    }

    // функция для заполнения данными
    suspend fun prepopulateData() {
        val machineDao = machineDao()
        val statusDao = machineStatusDao()
        val userDao = userDao()

        val existingMachineIds = machineDao.getAll().map { it.id }.toSet()
        val testMachines = listOf(
            MachineEntity(1, "Токарный станок", true),
            MachineEntity(2, "Фрезерный станок", true),
            MachineEntity(3, "Печь", true)
        )
        // вставляем только тех, у кого ещё нет ID в БД
        machineDao.insertAll(testMachines.filter { it.id !in existingMachineIds })

        val existingStatusIds = statusDao.getAll().map { it.id }.toSet()
        val testStatuses = listOf(
            MachineStatusEntity(1, "Работает", "#4CAF50"),
            MachineStatusEntity(2, "Простой", "#FFEB3B"),
            MachineStatusEntity(3, "Ремонт", "#F44336")
        )
        statusDao.insertAll(testStatuses.filter { it.id !in existingStatusIds })

        // задаём учётки по умолчанию
        val testUsers = listOf(
            UserEntity(login = "admin", password = "admin"),
            UserEntity(login = "Богдан", password = "1234")
        )
        // вставляем только новых, чтобы не дублировать при перезапуске
        userDao.insertAll(testUsers)
    }
}