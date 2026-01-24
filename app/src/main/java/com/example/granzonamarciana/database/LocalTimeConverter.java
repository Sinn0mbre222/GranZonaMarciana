package com.example.granzonamarciana.database;

import androidx.room.TypeConverter;

import java.time.LocalTime;

public class LocalTimeConverter {

    @TypeConverter
    public static LocalTime toLocalTime(Long value) {
        return value == null ? null : LocalTime.ofNanoOfDay(value);
    }

    @TypeConverter
    public static Long fromLocalTime(LocalTime date) {
        return date == null ? null : date.toNanoOfDay();
    }
}
