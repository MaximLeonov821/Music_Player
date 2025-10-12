package com.example.futurepast

import android.net.Uri
data class MusicData(
    val id: Long,
    val title: String,
    val author: String,
    val duration: Long,
    val  path: String,
    val contentUri: Uri
)
