package com.example.firmwareflasher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.firmwareflasher.ui.theme.FirmwareFlasherTheme
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.USB_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import com.chaquo.python.PyObject
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import java.io.IOException

private const val ACTION_USB_PERMISSION = "com.example.firmwareflasher.USB_PERMISSION"




class MainActivity : ComponentActivity() {
    private val permissionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_USB_PERMISSION) {
                val device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                val granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                if (granted && device != null) {
                    Log.i("UsbPermission", "Permission granted for device: ${device.deviceName}")
                } else {
                    Log.e("UsbPermission", "Permission denied for device: ${device?.deviceName}")
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        registerReceiver(permissionReceiver, IntentFilter(ACTION_USB_PERMISSION),
            RECEIVER_NOT_EXPORTED
        )

        // Initialize Python
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        applicationContext

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
        Button(onClick = { uploadFirmware(onUploadStatusChange) }) {
            Text(text = "Upload Firmware")
        }
    }



    private fun uploadFirmware(onUploadStatusChange: (String) -> Unit) {
        // Initialize Python and call the upload function
        val usbManager = getSystemService(USB_SERVICE) as UsbManager

        val python = Python.getInstance()
        val pyScript = python.getModule("upload_firmware")
        copyFirmwareToInternalStorage(this)

        // Get the list of connected USB devices
        val deviceList = usbManager.deviceList.values.toList()

        if (deviceList.isEmpty()) {
            onUploadStatusChange("No USB devices connected.")
            return
        }

        // Check for permissions and upload firmware
        val permissionIntent = PendingIntent.getBroadcast(
            this, 0, Intent(ACTION_USB_PERMISSION),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        for (device in deviceList) {
            if (!usbManager.hasPermission(device)) {
                usbManager.requestPermission(device, permissionIntent)
                onUploadStatusChange("Requesting permission for ${device.deviceName}...")
                return // Wait for permission before trying to upload
            }
        }

//         Call the upload function and capture output
        // Attempt firmware upload with error handling
        try {
            pyScript.callAttr("upload_firmware", this)
            onUploadStatusChange("Success")
        } catch (e: SecurityException) {
            usbManager.deviceList.values.forEach { device ->
                if (!usbManager.hasPermission(device)) {
                    usbManager.requestPermission(device, permissionIntent)
                }
            }
            onUploadStatusChange("Requesting USB permission...")
        } catch (e: Exception) {
            Log.e("UploadError", "Error uploading firmware", e)
            onUploadStatusChange("Upload failed: ${e.message}")
        }
    }

    fun copyFirmwareToInternalStorage(context: Context) {
        val assetManager = context.assets
        val filename = "strap_V3_1.0.bin"
        try {
            val inputStream = assetManager.open(filename)
            val outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            // Handle exceptions
            Log.e("FirmwareFlasher", "Error copying firmware to internal storage", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
            unregisterReceiver(permissionReceiver)
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        FirmwareFlasherTheme {
            MainScreen()
        }
    }}


