package com.example.geomarker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "entities")
data class Entity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "lat") val lat: String?,
    @ColumnInfo(name = "lon") val lon: String?,
    @ColumnInfo(name = "image_url") @SerializedName("image") val imageUrl: String?
)

