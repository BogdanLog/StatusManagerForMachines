package data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import data.model.MachineEntity
import data.model.MachineStatusEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MachineEntity::class, MachineStatusEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun machineDao(): MachineDao
    abstract fun machineStatusDao(): MachineStatusDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "machine_db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).prepopulateData()
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // функция для заполнения данными
    private suspend fun prepopulateData() {
        val machineDao = machineDao()
        val statusDao = machineStatusDao()

        if (machineDao.getAll().isEmpty()) {
            val testMachines = listOf(
                MachineEntity(1, "Токарный станок", true),
                MachineEntity(2, "Фрезерный станок", true),
                MachineEntity(3, "3D-принтер", true)
            )
            machineDao.insertAll(testMachines)
        }

        if (statusDao.getAll().isEmpty()) {
            val testStatuses = listOf(
                MachineStatusEntity(1, "Работает", "#4CAF50"),
                MachineStatusEntity(2, "Простой", "#FFEB3B"),
                MachineStatusEntity(3, "Ремонт", "#F44336")
            )
            statusDao.insertAll(testStatuses)
        }
    }
}