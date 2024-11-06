import os
import esptool
from serial.android import set_android_context
from os.path import dirname, join
from com.hoho.android.usbserial.driver import UsbSerialProber # If using felHR85's fork
   # from com.felhr.usbserial.UsbSerialDevice import UsbSerialDevice # If using felHR85's fork
from android.content import Context



def upload_firmware(context:Context):
    print("IRGENDWAS")

    firmware_path = (join(dirname(__file__), 'ESP32S3-Firmware.bin'))

    if not os.path.isfile(firmware_path):
        raise Exception(f"Firmware file not found at {firmware_path}")

    # serial_connection(context)
    print("Starting firmware upload...")
    set_android_context(context)

    esptool.main(['--chip', 'esp32s3', '--baud', '230400', 'write_flash', '-z', '0x10000', firmware_path])

def serial_connection(context:Context):
    usb_manager = context.getSystemService(context.USB_SERVICE)
    device_list = usb_manager.getDeviceList()
    print(device_list)
    serial_device = None

    devices = device_list.values().toArray()

    # Iterate through the keys
    for device in devices:
        # Identify your serial device (e.g., by vendor ID and product ID)
        if device.getVendorId() == 0x303A and device.getProductId() == 0x1001:
            serial_device = device
            break

    if serial_device is None:
       raise Exception("Serial device not found")

    driver = UsbSerialProber.getDefaultProber().probeDevice(serial_device)
    if driver is None:
      raise Exception("No driver for serial device")

    port = driver.getPorts().get(0) # Assuming you want the first port

    print(port)
