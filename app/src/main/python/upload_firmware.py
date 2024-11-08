import os
import esptool
from serial.android import set_android_context
from os.path import dirname, join
from android.content import Context



def upload_firmware(context:Context):
    firmware_path = (join(dirname(__file__), 'ESP32S3-Firmware.bin'))

    if not os.path.isfile(firmware_path):
        raise Exception(f"Firmware file not found at {firmware_path}")

    print("Starting firmware upload...")
    set_android_context(context)

    esptool.main(['--chip', 'esp32s3', '--baud', '230400', 'write_flash', '-z', '0x10000', firmware_path])

