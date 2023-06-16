package com.example.sleepontime.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    //用来定义常量和静态方法
    companion object {
        const val DATABASE_NAME = "user.db"
        const val DATABASE_VERSION = 2

        //用户表名和列名常量
        const val TABLE1_NAME = "users"  //表名
        const val COLUMN1_ID = "_id"  //用户id
        const val COLUMN1_USERNAME = "username"  //用户名
        const val COLUMN1_PASSWARD = "passward"  //密码
        const val COLUMN1_EMAIL = "email"    //邮箱

        const val TABLE2_NAME = "sleep_info"    //表名
        const val COLUMN2_ID = "id" //用户id
        const val COLUMN2_USERNAME = "username"  //用户名
        const val COLUMN2_WHITE = "white_list"  //白名单
        const val COLUMN2_AVAI = "available"    //剩余解锁数量
        const val COLUMN2_HOUR = "week_hour"    //列表，每周的睡眠时长
    }

    override fun onCreate(db: SQLiteDatabase) {

        //创建用户基本信息表
        val createTable1Query = "CREATE TABLE $TABLE1_NAME ($COLUMN1_ID INTEGER PRIMARY KEY, $COLUMN1_USERNAME TEXT, $COLUMN1_EMAIL TEXT, $COLUMN1_PASSWARD TEXT)"
        db.execSQL(createTable1Query)

        //创建用户睡眠信息表
        val defaultSleepHours = "[0, 0, 0, 0, 0, 0, 0]"
        val defaultAvailable = 5
        val createTable2Query = "CREATE TABLE $TABLE2_NAME ($COLUMN2_ID INTEGER PRIMARY KEY, $COLUMN2_USERNAME TEXT, $COLUMN2_AVAI INGETER DEFAULT '$defaultAvailable', $COLUMN2_WHITE TEXT, $COLUMN2_HOUR TEXT DEFAULT '$defaultSleepHours')"
        db.execSQL(createTable2Query)

    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 处理数据库升级
        if (oldVersion < newVersion) {
            // 执行升级操作
            if (oldVersion == 1 && newVersion >= 2) {
                //只针对版本1升级到版本2
                db.execSQL("ALTER TABLE $TABLE1_NAME RENAME TO temp_$TABLE1_NAME")  //将原来的表变为临时表
                db.execSQL("CREATE TABLE $TABLE1_NAME ($COLUMN1_ID INTEGER PRIMARY KEY, $COLUMN1_USERNAME TEXT, $COLUMN1_EMAIL TEXT)")  //创建一个新的表
                db.execSQL("INSERT INTO $TABLE1_NAME SELECT $COLUMN1_ID, $COLUMN1_USERNAME, $COLUMN1_EMAIL FROM temp_$TABLE1_NAME") //将原来的数据插入新的表
                db.execSQL("DROP TABLE temp_$TABLE1_NAME")  //删除原来的表/临时表

                db.execSQL("CREATE TEMPORARY TABLE ${TABLE2_NAME}_backup ($COLUMN2_ID INTEGER PRIMARY KEY, $COLUMN2_USERNAME TEXT, $COLUMN2_AVAI INGETER DEFAULT 5, $COLUMN2_WHITE TEXT, $COLUMN2_HOUR TEXT DEFAULT '[0, 0, 0, 0, 0, 0, 0]')")

                db.execSQL("INSERT INTO ${TABLE2_NAME}_backup SELECT $COLUMN2_ID, $COLUMN2_USERNAME, 5, $COLUMN2_WHITE, '[0, 0, 0, 0, 0, 0, 0]' FROM $TABLE2_NAME")

                db.execSQL("DROP TABLE $TABLE2_NAME")

                db.execSQL("CREATE TABLE $TABLE2_NAME ($COLUMN2_ID INTEGER PRIMARY KEY, $COLUMN2_USERNAME TEXT, $COLUMN2_AVAI INGETER DEFAULT 5, $COLUMN2_WHITE TEXT, $COLUMN2_HOUR TEXT DEFAULT '[0, 0, 0, 0, 0, 0, 0]')")

                db.execSQL("INSERT INTO $TABLE2_NAME SELECT $COLUMN2_ID, $COLUMN2_USERNAME, $COLUMN2_AVAI, $COLUMN2_WHITE, $COLUMN2_HOUR FROM ${TABLE2_NAME}_backup")

                db.execSQL("DROP TABLE ${TABLE2_NAME}_backup")

                println("++++++++++++++++++Upgrade to 2++++++++++++++++++")
            }
        }
    }

    //插入用户信息
    //Long 表示函数的返回类型
    fun insertUserInfo(username: String, email: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN1_USERNAME, username)
            put(COLUMN1_EMAIL, email)
        }
        val newRowId = db.insert(TABLE1_NAME, null, values)

        // 获取刚插入的用户信息的id和username
        val userQuery = "SELECT $COLUMN1_ID, $COLUMN1_USERNAME FROM $TABLE1_NAME WHERE $COLUMN1_EMAIL = ?"
        val userCursor = db.rawQuery(userQuery, arrayOf(email))
        val userId: Int
        val userName: String
        if (userCursor.moveToFirst()) {
            val idIndex = userCursor.getColumnIndex(COLUMN1_ID)
            val usernameIndex = userCursor.getColumnIndex(COLUMN1_USERNAME)
            userId = userCursor.getInt(idIndex)
            userName = userCursor.getString(usernameIndex)
        } else {
            // 处理获取用户信息失败的情况
            userCursor.close()
            db.close()
            return -1
        }
        userCursor.close()

        // 插入到sleep_info表中
        val sleepInfoValues = ContentValues().apply {
            put(COLUMN2_ID, userId)
            put(COLUMN2_USERNAME, userName)
            put(COLUMN2_AVAI, 5)
        }
        db.insert(TABLE2_NAME, null, sleepInfoValues)

        db.close()
        return newRowId
    }

    //倒计时退出
    fun isAvailable(id: Int): Boolean {
        val db = readableDatabase
        val quary = "SELECT $COLUMN2_AVAI FROM $TABLE2_NAME WHERE $COLUMN2_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.rawQuery(quary, selectionArgs)
        var available = false
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN2_AVAI)
            val count = cursor.getInt(columnIndex)
            available = count > 0
        }
        cursor.close()
        db.close()
        return available
    }

    //通过Email查询id
    fun getUserIdByEmail(email: String): Int {
        val db = readableDatabase
        val query = "SELECT $COLUMN1_ID FROM $TABLE1_NAME WHERE $COLUMN1_EMAIL = ?"
        val selectionArgs = arrayOf(email)
        val cursor = db.rawQuery(query, selectionArgs)
        var userId = -1
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN1_ID)
            userId = cursor.getInt(columnIndex)
        }
        cursor.close()
        db.close()
        return userId
    }

    //通过id查询weektime
    fun getWeekHour(id: Int): String? {
        val db = readableDatabase
        val query = "SELECT $COLUMN2_HOUR FROM $TABLE2_NAME WHERE $COLUMN2_ID = ?"
        val selectionArgs = arrayOf(id.toString())  // 将id参数转换为字符串
        val cursor = db.rawQuery(query, selectionArgs)
        var weekHour: String? = null
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex(COLUMN2_HOUR)
            weekHour = cursor.getString(columnIndex)
        }
        cursor.close()
        db.close()
        return weekHour
    }

    // 更新数据库中的 week_hour 列
    fun updateWeekHour(id: Int, weekHour: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN2_HOUR, weekHour)
        }
        val selection = "$COLUMN2_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        val rowsAffected = db.update(TABLE2_NAME, values, selection, selectionArgs)
        db.close()
        return rowsAffected
    }

    fun getUsernameAndAvailableById(id: Int): Pair<String?, Int> {
        val db = readableDatabase
        val query = "SELECT users.$COLUMN1_USERNAME, sleep_info.$COLUMN2_AVAI FROM $TABLE1_NAME INNER JOIN $TABLE2_NAME ON users.$COLUMN1_ID = sleep_info.$COLUMN2_ID WHERE users.$COLUMN1_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = db.rawQuery(query, selectionArgs)
        var username: String? = null
        var available = 0
        if (cursor.moveToFirst()) {
            val usernameIndex = cursor.getColumnIndex(COLUMN1_USERNAME)
            val availableIndex = cursor.getColumnIndex(COLUMN2_AVAI)
            username = cursor.getString(usernameIndex)
            available = cursor.getInt(availableIndex)
        }
        cursor.close()
        db.close()
        return Pair(username, available)
    }




}