package com.example.granzonamarciana.entity;

import androidx.room.PrimaryKey;

public abstract class DomainEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
