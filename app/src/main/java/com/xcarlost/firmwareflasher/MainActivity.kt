package com.xcarlost.firmwareflasher

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xcarlost.esptool_android.Main
import com.xcarlost.firmwareflasher.ui.theme.FirmwareFlasherTheme
import java.io.File


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FirmwareFlasherTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }


    @Composable
    fun MainScreen(modifier: Modifier = Modifier) {
        var uploadStatus by remember { mutableStateOf("Click the button to upload firmware.") }

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Greeting(name = "Android")
            Grateing(name = "Android")
            Spacer(modifier = Modifier.height(16.dp))
            UploadButton { status ->
                uploadStatus = status
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = uploadStatus, modifier = Modifier.padding(16.dp))
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Composable
    fun Grateing(name: String) {
        Text(text = "Press button:")
    }

    @Composable
    fun UploadButton(onUploadStatusChange: (String) -> Unit) {
        val context = LocalContext.current
        Button(onClick = {
            val firmwareData: ByteArray =
                context.assets.open("ESP32S3-Firmware.bin").use { inputStream ->
                    inputStream.readBytes()
                }


            val firmwarePath = saveAsFile(firmwareData)
            val arguments =
                "--chip esp32s3 --baud 115200 --before default_reset --after hard_reset --no-stub write_flash -z 0x10000 $firmwarePath"
            val result = Main().uploadFirmware(context, arguments)
            onUploadStatusChange(result)
        }) {
            Text(text = "Upload Firmware")
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        FirmwareFlasherTheme {
            MainScreen()
        }
    }

    private fun saveAsFile(content: ByteArray): String {
        val tempFile = File.createTempFile("firmware", ".bin")
        tempFile.writeBytes(content)
        println("Temp File Byte Count: ${tempFile.length()}")
        return tempFile.absolutePath
    }
}


