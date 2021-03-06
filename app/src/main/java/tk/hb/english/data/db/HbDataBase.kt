package tk.hb.english.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tk.hb.english.MyApplication
import tk.hb.english.data.db.dao.WordDao
import tk.hb.english.data.db.entity.WordBean


/**
 * Created by HONGBO on 2020/7/27 19:30
 */
@Database(entities = [WordBean::class], version = 1, exportSchema = false)
abstract class HbDataBase : RoomDatabase() {

    companion object {

        private val DB_NAME = "english.db"

        val instance: HbDataBase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(
                MyApplication.getAppContext(),
                HbDataBase::class.java, DB_NAME
            ).createFromAsset(DB_NAME).build()
        }
    }

    abstract fun wordDao(): WordDao?
}