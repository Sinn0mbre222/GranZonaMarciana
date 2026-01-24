package com.example.granzonamarciana.database;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class LocalDateConverter {
    @TypeConverter
    public static LocalDate fromLocalDate(Long value) {
        return value == null ? null : LocalDate.ofEpochDay(value);
    }

    @TypeConverter
    public static Long dateToLocalDate(LocalDate date) {
        return date == null ? null : date.toEpochDay();
    }
}
