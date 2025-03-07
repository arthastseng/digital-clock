package com.ssc.android.vs_digital_clock.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TimeZone(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "index_key") val indexKey: String?,
    @ColumnInfo(name = "region") val region: String?,
    @ColumnInfo(name = "city") val city: String?,
)
