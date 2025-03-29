package com.example.ktgiuaki

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktgiuaki.ui.theme.KTGiuaKiTheme
import com.example.ktgiuaki.ui.theme.greenColor
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class EditNoteActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lấy dữ liệu từ Intent
        val noteId = intent.getStringExtra("noteId") ?: ""
        val noteTitle = intent.getStringExtra("noteTitle") ?: ""
        val noteDescription = intent.getStringExtra("noteDescription") ?: ""

        setContent {
            KTGiuaKiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Edit Note",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        color = Color.White
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = { finish() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = Color.White
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = greenColor
                                )
                            )
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            EditNoteUI(noteId, noteTitle, noteDescription, LocalContext.current)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditNoteUI(noteId: String, initialTitle: String, initialDescription: String, context: Context) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    // Biến lưu trữ URI của ảnh được chọn
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    // Launcher để chọn ảnh từ thiết bị
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TextField nhập Title
        TextField(
            value = title,
            onValueChange = { title = it },
            placeholder = { Text(text = "Enter Title") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // TextField nhập Description
        TextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text(text = "Enter Description") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(200.dp),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = false,
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Nút Update Data
        Button(
            onClick = {
                if (title.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập tiêu đề ghi chú", Toast.LENGTH_SHORT).show()
                } else if (description.isBlank()) {
                    Toast.makeText(context, "Vui lòng nhập mô tả ghi chú", Toast.LENGTH_SHORT).show()
                } else {
                    updateDataToFirebase(noteId, title, description, context)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Cập nhật ghi chú", modifier = Modifier.padding(8.dp))
        }
        //Nút thêm ảnh
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Pick Image", modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Nút Upload Image
        Button(
            onClick = {
                if (imageUri != null) {
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.reference.child("images/${UUID.randomUUID()}.jpg")
                    storageRef.putFile(imageUri!!)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Upload image thành công", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Upload image thất bại\n$e", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Vui lòng chọn ảnh trước", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Upload Image", modifier = Modifier.padding(8.dp))
        }

    }
}

fun updateDataToFirebase(noteId: String, title: String, description: String, context: Context) {
    val db = FirebaseFirestore.getInstance()
    db.collection("Notes").document(noteId)
        .update("title", title, "description", description)
        .addOnSuccessListener {
            Toast.makeText(context, "Ghi chú đã được cập nhật", Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener { e ->
            Toast.makeText(context, "Cập nhật dữ liệu thất bại\n$e", Toast.LENGTH_SHORT).show()
        }
}
