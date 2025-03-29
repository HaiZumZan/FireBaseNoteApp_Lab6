package com.example.ktgiuaki

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ktgiuaki.ui.theme.KTGiuaKiTheme
import com.example.ktgiuaki.ui.theme.greenColor
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KTGiuaKiTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "FlutterFire CRUD",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = greenColor
                            )
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = {
                                // Chuyển sang trang thêm mới Note
                                val intent = Intent(this@MainActivity, AddNoteActivity::class.java)
                                startActivity(intent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Note"
                            )
                        }
                    }
                ) { innerPadding ->
                    // Nội dung trang chính
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(Color(0xFF2E3E4E))
                    ) {
                        // Card mẫu như ảnh minh hoạ
                        InfoCard()
                    }
                }
            }
        }
    }

    @Composable
    fun InfoCard() {
        // Hiển thị danh sách các note thay cho nội dung mặc định
        NoteList()
    }

    @Composable
    fun NoteList() {
        // Sử dụng mutableStateListOf để lưu danh sách các note
        val notes = remember { mutableStateListOf<Note>() }
        val db = FirebaseFirestore.getInstance()
        val context = LocalContext.current

        // Hàm xử lý xóa note
        val deleteNote: (String) -> Unit = { noteId ->
            db.collection("Notes").document(noteId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "Ghi chú đã được xóa", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Xóa ghi chú thất bại: $e", Toast.LENGTH_SHORT).show()
                }
        }
        // Lắng nghe sự thay đổi trên collection "Notes"
        LaunchedEffect(Unit) {
            db.collection("Notes")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Bạn có thể thêm xử lý lỗi nếu cần
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        notes.clear()
                        for (document in snapshot.documents) {
                            val note = document.toObject(Note::class.java)
                            note?.let {
                                // Gán document id cho note
                                notes.add(it.copy(id = document.id))
                            }
                        }
                    }
                }
        }

        // Hiển thị danh sách các note dưới dạng LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2E3E4E))
        ) {
            items(notes) { note ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),

                    colors = CardDefaults.cardColors(containerColor = Color(0xFF394B5F))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)
                            .clickable {
                                val intent = Intent(context, EditNoteActivity::class.java)
                                intent.putExtra("noteId", note.id)
                                intent.putExtra("noteTitle", note.title)
                                intent.putExtra("noteDescription", note.description)
                                context.startActivity(intent)
                            }
                            .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                        ) {
                            note.title?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            note.description?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    maxLines = 5
                                )
                            }
                        }
                        // Nút xóa với icon thùng rác
                        IconButton(
                            onClick = {
                                note.id?.let { deleteNote(it) }
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Note",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

