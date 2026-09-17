package com.xcarlost.firmwareflasher

import android.content.Context
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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

private data class EspChip(
    val displayName: String,
    val esptoolName: String,
    val flashOffset: String,
    val useNoStub: Boolean = false
)

private val supportedEspChips =
    listOf(
        EspChip("ESP8266", "esp8266", "0x0"),
        EspChip("ESP32", "esp32", "0x10000"),
        EspChip("ESP32-S2", "esp32s2", "0x10000"),
        EspChip("ESP32-S3", "esp32s3", "0x10000", useNoStub = true),
        EspChip("ESP32-C2", "esp32c2", "0x10000"),
        EspChip("ESP32-C3", "esp32c3", "0x10000"),
        EspChip("ESP32-C6", "esp32c6", "0x10000"),
        EspChip("ESP32-H2", "esp32h2", "0x10000"),
        EspChip("ESP32-P4", "esp32p4", "0x10000")
    )

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
        var uploadStatus by remember { mutableStateOf("Select a .bin firmware file to begin.") }
        var selectedFirmwarePath by remember { mutableStateOf<String?>(null) }
        var selectedChip by remember { mutableStateOf(supportedEspChips.first()) }

        Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Firmware file")
            Spacer(modifier = Modifier.height(8.dp))
            SelectFirmwareButton { result ->
                result.onSuccess { path ->
                    selectedFirmwarePath = path
                    uploadStatus = "Selected ${File(path).name}"
                }.onFailure { error ->
                    uploadStatus = error.message ?: "Unable to select firmware file."
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            SelectESP(selectedChip) { chip ->
                selectedChip = chip
                uploadStatus = "Selected ${chip.displayName}"
            }
            Spacer(modifier = Modifier.height(16.dp))
            UploadButton(selectedFirmwarePath, selectedChip) { status ->
                uploadStatus = status
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = uploadStatus, modifier = Modifier.padding(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(text = "Hello $name!", modifier = modifier)
    }

    @Composable
    fun SelectFirmwareButton(onFileSelected: (Result<String>) -> Unit) {
        val context = LocalContext.current
        val filePicker = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocument()
        ) { uri ->
            if (uri == null) {
                return@rememberLauncherForActivityResult
            }

            onFileSelected(runCatching { copyFirmwareToAppStorage(context, uri) })
        }

        Button(onClick = { filePicker.launch(arrayOf("*/*")) }) {
            Text(text = "Select .bin file")
        }
    }

    @Composable
    private fun SelectESP(selectedChip: EspChip, onChipSelected: (EspChip) -> Unit) {
        var menuExpanded by remember { mutableStateOf(false) }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "ESP chip")
            Spacer(modifier = Modifier.height(8.dp))
            Box {
                Button(onClick = { menuExpanded = true }) {
                    Text(text = selectedChip.displayName)
                }
                DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                ) {
                    supportedEspChips.forEach { chip ->
                        DropdownMenuItem(
                                text = { Text(text = chip.displayName) },
                                onClick = {
                                    menuExpanded = false
                                    onChipSelected(chip)
                                }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun UploadButton(
            firmwarePath: String?,
            chip: EspChip,
            onUploadStatusChange: (String) -> Unit
    ) {
        val context = LocalContext.current

        Button(
                onClick = {
                    if (firmwarePath == null) {
                        onUploadStatusChange("Select a .bin firmware file first.")
                        return@Button
                    }

                        val noStubArgument = if (chip.useNoStub) " --no-stub" else ""
                        val argument =
                            "--chip ${chip.esptoolName} --baud 460800 " +
                                    "--before default_reset --after hard_reset$noStubArgument " +
                                    "write_flash -z ${chip.flashOffset} $firmwarePath"
                    val result = Main().uploadFirmware(context, argument)
                    onUploadStatusChange(result)
                }
        ) { Text(text = "Upload Firmware") }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        FirmwareFlasherTheme { MainScreen() }
    }

    private fun copyFirmwareToAppStorage(context: Context, uri: android.net.Uri): String {
        val displayName = context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
        )?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        } ?: "firmware.bin"

        require(displayName.lowercase().endsWith(".bin")) {
            "Only .bin firmware files are supported."
        }

        val assetsDirectory = File(context.filesDir, "assets").apply { mkdirs() }
        val destination = File(assetsDirectory, displayName.substringAfterLast('/'))
        context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Unable to read the selected firmware file." }
            destination.outputStream().use { output -> input.copyTo(output) }
        }
        return destination.absolutePath
    }
}
