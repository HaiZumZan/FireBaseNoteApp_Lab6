package com.example.ktgiuaki


// on below line creating a data class for course,
data class Note(
    val id: String? = null, // Thêm id để lưu document id từ Firestore
    val title: String? = null,
    val description: String? = null
)
