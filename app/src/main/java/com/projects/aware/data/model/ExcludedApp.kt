package com.projects.aware.data.model

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ExcludedApp(
    @Id
    var id: Long = 0L,
    val packageName: String = "",
)