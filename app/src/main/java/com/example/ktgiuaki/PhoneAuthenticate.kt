package com.example.ktgiuaki

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktgiuaki.ui.theme.KTGiuaKiTheme
import com.example.ktgiuaki.ui.theme.greenColor
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

class PhoneAuthenticate : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KTGiuaKiTheme {
                // A surface container using the
                // 'background' color from the theme
                Surface(
                    // on below line we are specifying modifier and color for our app
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    // on the below line we are specifying the theme as the scaffold.
                    Scaffold(
                        // in scaffold we are specifying the top bar.
                        topBar = {
                            // inside top bar we are specifying background color.
                            TopAppBar(
                                colors = TopAppBarDefaults.topAppBarColors(containerColor = greenColor),
                                // along with that we are specifying
                                // title for our top bar.
                                title = {
                                    // in the top bar we are specifying tile as a text
                                    Text(
                                        // on below line we are specifying
                                        // text to display in top app bar
                                        text = "GFG",
                                        // on below line we are specifying
                                        // modifier to fill max width
                                        modifier = Modifier.fillMaxWidth(),
                                        // on below line we are specifying
                                        // text alignment
                                        textAlign = TextAlign.Center,
                                        // on below line we are specifying
                                        // color for our text.
                                        color = Color.White
                                    )
                                })
                        }) {paddingValues ->
                        // Sử dụng paddingValues để bù đắp cho nội dung của bạn.
                        Box(modifier = Modifier.padding(paddingValues)) {
                            firebaseUI(LocalContext.current)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun firebaseUI(context: Context) {

    // on below line creating variable for course name,
    // course duration and course description.
    val phoneNumber = remember {
        mutableStateOf("")
    }

    val otp = remember {
        mutableStateOf("")
    }

    val verificationID = remember {
        mutableStateOf("")
    }

    val message = remember {
        mutableStateOf("")
    }

    // on below line creating variable
    // for firebase auth and callback
    var mAuth: FirebaseAuth = FirebaseAuth.getInstance();
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    // on below line creating a column
    // to display our retrieved image view.
    Column(
        // adding modifier for our column
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.White),
        // on below line adding vertical and
        // horizontal alignment for column.
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // on below line creating text field for phone number.
        TextField(
            // on below line we are specifying
            // value for our course name text field.
            value = phoneNumber.value,
            // on below line specifying key board type as number.
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

            // on below line we are adding on
            // value change for text field.
            onValueChange = { phoneNumber.value = it },

            // on below line we are adding place holder
            // as text as "Enter your course name"
            placeholder = { Text(text = "Enter your phone number") },

            // on below line we are adding modifier to it
            // and adding padding to it and filling max width
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),

            // on below line we are adding text style
            // specifying color and font size to it.
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),

            // on below line we are adding
            // single line to it.
            singleLine = true,
        )

        // on below line adding spacer.
        Spacer(modifier = Modifier.height(10.dp))

        // on below line creating button to send verification code.
        Button(
            onClick = {
                // on below line we are validating user inputs
                if (TextUtils.isEmpty(phoneNumber.value.toString())) {
                    Toast.makeText(context, "Please enter phone number..", Toast.LENGTH_SHORT)
                        .show()
                } else {

                    val number = "+84${phoneNumber.value}"
                    // on below line calling method to generate verification code.
                    sendVerificationCode(number, mAuth, context as Activity, callbacks)
                }
            },
            // on below line we are
            // adding modifier to our button.
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // on below line we are adding text for our button
            Text(text = "Generate OTP", modifier = Modifier.padding(8.dp))
        }

        // adding spacer on below line.
        Spacer(modifier = Modifier.height(10.dp))

        // on below line creating text field for otp
        TextField(
            // on below line we are specifying
            // value for our course duration text field.
            value = otp.value,
            //specifying key board on below line.
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            // on below line we are adding on
            // value change for text field.
            onValueChange = { otp.value = it },

            // on below line we are adding place holder
            // as text as "Enter your course duration"
            placeholder = { Text(text = "Enter your otp") },

            // on below line we are adding modifier to it
            // and adding padding to it and filling max width
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),

            // on below line we are adding text style
            // specifying color and font size to it.
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),

            // on below line we are adding
            // single line to it.
            singleLine = true,
        )

        // adding spacer on below line.
        Spacer(modifier = Modifier.height(10.dp))

        // on below line creating button to add
        // data to firebase firestore database.
        Button(
            onClick = {
                // on below line we are validating
                // user input parameters.
                if (TextUtils.isEmpty(otp.value.toString())) {
                    // displaying toast message on below line.
                    Toast.makeText(context, "Please enter otp..", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    // on below line generating phone credentials.
                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        verificationID.value, otp.value
                    )
                    // on below line signing within credentials.
                    signInWithPhoneAuthCredential(
                        credential,
                        mAuth,
                        context as Activity,
                        context,
                        message
                    )
                }
            },
            // on below line we are
            // adding modifier to our button.
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // on below line we are adding text for our button
            Text(text = "Verify OTP", modifier = Modifier.padding(8.dp))
        }

        // on below line adding spacer.
        Spacer(modifier = Modifier.height(5.dp))

        Text(
            // on below line displaying message for verification status.
            text = message.value,
            style = TextStyle(color = greenColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        )
    }

    // on below line creating callback
    callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            // on below line updating message
            // and displaying toast message
            message.value = "Verification successful"
            Toast.makeText(context, "Verification successful..", Toast.LENGTH_SHORT).show()
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            // on below line displaying error as toast message.
            message.value = "Fail to verify user : \n" + p0.message
            Toast.makeText(context, "Verification failed..", Toast.LENGTH_SHORT).show()
        }

        override fun onCodeSent(verificationId: String, p1: ForceResendingToken) {
            // this method is called when code is send
            super.onCodeSent(verificationId, p1)
            verificationID.value = verificationId
        }
    }
}

// on below line creating method to
// sign in with phone credentuals.
private fun signInWithPhoneAuthCredential(
    credential: PhoneAuthCredential,
    auth: FirebaseAuth,
    activity: Activity,
    context: Context,
    message: MutableState<String>
) {
    // on below line signing with credentials.
    auth.signInWithCredential(credential)
        .addOnCompleteListener(activity) { task ->
            // displaying toast message when
            // verification is successful
            if (task.isSuccessful) {
                message.value = "Verification successful"
                Toast.makeText(context, "Verification successful..", Toast.LENGTH_SHORT).show()
                // Thêm đoạn chuyển sang MainActivity
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
                //  Đóng màn hình PhoneAuthenticate
                (context as Activity).finish()
            } else {
                // Sign in failed, display a message
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code
                    // entered was invalid
                    Toast.makeText(
                        context,
                        "Verification failed.." + (task.exception as FirebaseAuthInvalidCredentialsException).message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
}

// below method is use to send
// verification code to user phone number.
private fun sendVerificationCode(
    number: String,
    auth: FirebaseAuth,
    activity: Activity,
    callbacks: OnVerificationStateChangedCallbacks
) {
    // on below line generating options for verification code
    val options = PhoneAuthOptions.newBuilder(auth)
        .setPhoneNumber(number) // Phone number to verify
        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
        .setActivity(activity) // Activity (for callback binding)
        .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
        .build()
    PhoneAuthProvider.verifyPhoneNumber(options)
}
