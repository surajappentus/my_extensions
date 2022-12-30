# my_extensions
Step 1. Add the JitPack repository to your build file. Add it in your root /build.gradle at the end of repositories:

        allprojects {
            repositories {
                ...
                maven { url 'https://jitpack.io' }
            }
        }
Step 2. Add the dependency in /app/build.gradle :

        dependencies {
            ...
            implementation 'com.github.DantSu:ESCPOS-ThermalPrinter-Android:3.2.0'
        }
