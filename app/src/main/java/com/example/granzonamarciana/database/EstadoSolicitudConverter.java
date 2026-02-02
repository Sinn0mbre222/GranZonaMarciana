package com.example.granzonamarciana.database;
import androidx.room.TypeConverter;
import com.example.granzonamarciana.entity.EstadoSolicitud;

public class EstadoSolicitudConverter {
    @TypeConverter
    public static String fromEstado(EstadoSolicitud estado) {
        return estado == null ? null : estado.name();
    }
    @TypeConverter
    public static EstadoSolicitud toEstado(String estadoStr) {
        return estadoStr == null ? null : EstadoSolicitud.valueOf(estadoStr);
    }
}