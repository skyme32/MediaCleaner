package my.skyme32.mediacleaner.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import my.skyme32.mediacleaner.db.MediaCleanerDatabase
import java.io.File

object DatabaseFactory {
    fun create(dbPath: String): MediaCleanerDatabase {
        val dbFile = File(dbPath)
        dbFile.parentFile?.mkdirs()
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:$dbPath")
        MediaCleanerDatabase.Schema.create(driver)
        return MediaCleanerDatabase(driver)
    }
}

