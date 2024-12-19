[![](https://jitpack.io/v/xCarlost/esptool-android.svg)](https://jitpack.io/#xCarlost/esptool-android)

# esptool-android

The repository "esptool-android" is a library to provide esptool.py on Android. 

## Features

- Flash firmware to devices via serial communication.
- Demonstrates the integration of Python with Android using Chaquopy.
- Utilizes a custom fork of the `pyserial` library for serial communication.

## External Packages

This project makes use of the following external packages:

- **Chaquopy**: A plugin for Android Studio that allows you to run Python code in your Android apps.
- **pyserial fork**: A custom fork of the `pyserial` library tailored for this demo project.
- **esptool.py**: A Python-based, open-source tool for flashing firmware onto ESP8266 and ESP32 chips.
- **usb-serial-for-android**: Mik3y's implementation to integrate serial connection on Android devices.

## Getting Started

**1.** Add library to your project:

Add jitpack.io repository to your root build.gradle:
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Starting with gradle 6.8 you can alternatively add jitpack.io repository to your settings.gradle:
```gradle
dependencyResolutionManagement {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

If using gradle kotlin  use line
```gradle.kts
        maven(url = "https://jitpack.io")
```

Add library to dependencies
```gradle
dependencies {
    implementation 'com.github.xCarlost:esptool-android:v1.0.0' //replace "v1.0.0" with desired version
}
```

For a simple example, see
[app](https://github.com/xCarlost/esptool-android/tree/main/app)
folder in this project.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## Acknowledgements

- The Chaquopy team for their excellent plugin.
- The contributors to the `pyserial` library and its forks.
- The Mik3y team for a reliable implementation on Android regarding the serial connection.

For any questions or issues, please open an issue on the repository.

