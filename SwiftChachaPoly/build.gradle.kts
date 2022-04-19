listOf("iphoneos", "iphonesimulator", "iphonesimulatorArm64").forEach { sdk ->
    println("##### SDK: $sdk")

    tasks.create<Exec>("build${sdk.capitalize()}") {
        group = "build"

        val sdkName = if(sdk.startsWith("iphoneos")) {
            "iphoneos"
        } else {
            "iphonesimulator"
        }

        println("SDK: $sdk, SDK NAME: $sdkName")

        commandLine(
            "xcodebuild",
            "-project", "SwiftChachaPoly.xcodeproj",
            "-target", "SwiftChachaPoly",
            "-sdk", sdkName
        )
        workingDir(projectDir)

        inputs.files(
            fileTree("$projectDir/SwiftChachaPoly.xcodeproj") { exclude("**/xcuserdata") },
            fileTree("$projectDir/SwiftChachaPoly")
        )
        outputs.files(
            fileTree("$projectDir/build/Release-${sdk}")
        )
    }
}

tasks.create<Delete>("clean") {
    group = "build"

    delete("$projectDir/build")
}
