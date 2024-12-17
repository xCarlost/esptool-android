# FirmwareFlasher

FirmwareFlasher is a demo project designed to showcase the usage of a custom fork of the `pyserial` library. This project is built using Android Studio and demonstrates how to integrate Python code within an Android application using Chaquopy.

## Features

- Flash firmware to devices via serial communication.
- Demonstrates the integration of Python with Android using Chaquopy.
- Utilizes a custom fork of the `pyserial` library for serial communication.

## External Packages

This project makes use of the following external packages:

- **Chaquopy**: A plugin for Android Studio that allows you to run Python code in your Android apps.
- **pyserial fork**: A custom fork of the `pyserial` library tailored for this demo project.
- **esptool.py**: A Python-based, open-source tool for flashing firmware onto ESP8266 and ESP32 chips.

## Getting Started

To get started with this project, follow these steps:

1. Clone the repository:
    ```sh
    git clone https://github.com/xCarlost/FirmwareFlasher.git
    ```
2. Open the project in Android Studio.
3. Ensure you have the necessary dependencies installed.
4. Build and run the project on your Android device.

## ESP32 Firmware Upload

This software uses `esptool.py` to upload firmware onto ESP32. The app is programmed for an ESP32-S3. You can change the chip in `upload_firmware.py` and the firmware file in the `app/src/main/python` folder.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## Acknowledgements

- The Chaquopy team for their excellent plugin.
- The contributors to the `pyserial` library and its forks.

For any questions or issues, please open an issue on the repository.

[![](https://jitpack.io/v/xCarlost/FirmwareFlasher.svg)](https://jitpack.io/#xCarlost/FirmwareFlasher)