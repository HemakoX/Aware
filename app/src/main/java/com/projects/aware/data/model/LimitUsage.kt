package com.projects.aware.data.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class LimitAppUsage(
    @Id var id: Long = 0,
    val dailyLimit: Long,
    val packageName: String,
)