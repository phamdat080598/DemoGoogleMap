package com.example.demogooglemap

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
fun main() {
//    val dateTime: LocalDateTime = LocalDateTime.parse("2020-03-14T08:19:34.587Z")
//
//
//    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz")

    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//    val date= format.parse("2020-03-8T08:09:34.587Z")
    val c = Calendar.getInstance().time
//    c.time = date
    val dayOfWeek =

//    val strDate: String = format2.format(date)
    println("After : ${format.format(c)}")
}

