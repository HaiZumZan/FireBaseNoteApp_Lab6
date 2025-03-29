package com.example.ktgiuaki

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddNoteActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                                        text = "Firebase CRUD",
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
                    ) {
                        Box(modifier = Modifier.padding(it)) {
                            FirebaseUI(LocalContext.current)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FirebaseUI(context: Context) {
    // Biến lưu trữ Title và Description
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
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
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TextField nhập Title
        TextField(
            value = title.value,
            onValueChange = { title.value = it },
            placeholder = { Text(text = "Enter Title") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // TextField nhập Description (to hơn)
        TextField(
            value = description.value,
            onValueChange = { description.value = it },
            placeholder = { Text(text = "Enter Description") },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(200.dp), // Chiều cao lớn hơn
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = false,
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Nút Add Data
        Button(
            onClick = {
                if (TextUtils.isEmpty(title.value)) {
                    Toast.makeText(context, "Vui lòng nhập tiêu đề ghi chú", Toast.LENGTH_SHORT).show()
                } else if (TextUtils.isEmpty(description.value)) {
                    Toast.makeText(context, "Vui lòng nhập mô tả ghi chú", Toast.LENGTH_SHORT).show()
                } else {
                    addDataToFirebase(title.value, description.value, context)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Thêm ghi chú", modifier = Modifier.padding(8.dp))
        }

        // Nút Pick Image
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

fun addDataToFirebase(
    title: String,
    description: String,
    context: Context
) {
    // Tạo instance của Firestore
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Tạo reference tới collection "Posts" (hoặc tuỳ ý)
    val dbPosts: CollectionReference = db.collection("Notes")

    // Tạo đối tượng data (bạn có thể đổi tên class tuỳ ý)
    val newPost = Note(id = null, title, description)

    // Thêm dữ liệu vào Firestore
    dbPosts.add(newPost).addOnSuccessListener {
        Toast.makeText(
            context,
            "Ghi chú của bạn đã được thêm vào Firebase",
            Toast.LENGTH_SHORT
        ).show()
    }.addOnFailureListener { e ->
        Toast.makeText(context, "Thêm dữ liệu thất bại\n$e", Toast.LENGTH_SHORT).show()
    }
}
