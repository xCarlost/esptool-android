import esptool
from android.content import Context
from serial.android import set_android_context


def upload_firmware(context: Context, arguments: str):
    print("Starting firmware upload...")
    set_android_context(context)
    esptool.main(arguments.split(" "))
