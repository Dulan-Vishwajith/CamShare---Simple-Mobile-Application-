package com.mobiledev.camshare

import android.content.Intent
import androidx.compose.foundation.layout.fillMaxWidth
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mobiledev.camshare.ui.theme.CamShareTheme
import android.content.Context
import java.io.File
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import coil.compose.AsyncImage


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CamShareTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background

                ) { innerPadding ->
                    CamShare(modifier = Modifier.padding( innerPadding))
                }
            }
        }
    }
}


fun createImageFile(context: Context): File {

    val imageFile = File(
        context.cacheDir,
        "captured_photo.jpg"
    )
    return imageFile
}


fun createImageUri(context: Context): Uri {

    val imageFile = createImageFile(context)
    val imageUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        imageFile
    )
    return imageUri
}




@Composable
fun CamShare(modifier: Modifier = Modifier){
    val context = LocalContext.current

    var photoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }


    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->

        if (success) {
            photoUri = imageUri
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera Application",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(30.dp))

        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = "Captured Photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        } else {
            Text("No photo captured yet")
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {

                imageUri = createImageUri(context)

                cameraLauncher.launch(imageUri!!)
            }
        ) {
            Text("Take Photo")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val uri = photoUri

                if (uri != null) {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/jpeg"

                        putExtra(Intent.EXTRA_STREAM, uri)

                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    context.startActivity(
                        Intent.createChooser(
                            shareIntent,
                            "Share Photo"
                        )
                    )
                }
            },
            enabled = photoUri != null
        ) {
            Text("Share Photo")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun CamSharePreview(modifier: Modifier = Modifier){
    CamShare()

}