package com.ssc.android.vs_digital_clock.data

object MockDataUtil {
    fun createMockData(): List<TimeZoneInfo> {
        val mockData = mutableListOf<TimeZoneInfo>()
        for (i in 0 until 10) {
            val data = TimeZoneInfo(
                time = 1741168451101,
                region = "Asia",
                city = "Taiwan $i"
            )
            mockData.add(data)
        }
        return mockData
    }
}