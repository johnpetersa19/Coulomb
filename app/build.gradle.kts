/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.5/userguide/building_java_projects.html in the Gradle documentation.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import java.util.zip.ZipFile

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    kotlin("jvm") version "2.0.20"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    //id("org.graalvm.buildtools.native") version "0.10.3"

}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is used by the application.
    implementation(libs.guava)

    implementation("com.github.bailuk:java-gtk:0.5.0")
    implementation("org.ejml:ejml-all:0.43")
    //compile group: 'org.ejml', name: 'ejml-all', version: '0.43'
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
    }
}

application {
    // Define the main class for the application.
    mainClass.set("Main")  
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}


abstract class Resources : DefaultTask() {
    @TaskAction
    fun action() {
        val resources = File("app/src/main/resources")
        File("app/src/main/java/resources").mkdir()

        var output = """package resources;
public class Resources {
    public static String[] resources = {
"""
        for (res_path in resources.walk()) {
            var res = res_path.toString().replace("app/src/main/resources" , "")
            if(res!= "" && !res_path.isDirectory())
                output += "\t\t\"${res}\",\n"
            //println("\"${res}\",")
        }
        output += "\t};\n" 

        for (res_path in resources.walk()) {
            var res = res_path.toString().replace("app/src/main/resources" , "")

            if(!(res.endsWith(".css") || res.endsWith(".svg")))
                continue;
            
            var path_components = res.split("/" , ".").reversed().joinToString(separator="_")
            var varName = path_components.replace("/" , "_").replace(" " , "_").replace("-" , "_").replace(".","_").replace(Regex("[^A-Za-z0-9_]") , "")

            if(res!= "" && !res_path.isDirectory() )
                output += "\tpublic static final String ${varName} = \"${res.drop(1)}\";\n"
        }
        

        output += "}"
        File("app/src/main/java/resources/Resources.java").writeText(output)

    }

}


fun unzip(zipFilePath : String, destDir : String) {
    File(destDir).mkdirs()
    ZipFile(zipFilePath).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            zip.getInputStream(entry).use { input ->
                File(destDir+"/"+entry.name).outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}

// tasks.register<DefaultTask>("downloadWindowsLibs") {
//     doFirst {
//         var done_file_path_str = "app/src/main/resources/lib/dlls-done"
//         val lib_dir = File("app/src/main/resources/lib/")

//         if(!File(done_file_path_str).exists()) {
//             if (lib_dir.exists()) {
//                 lib_dir.deleteRecursively()
//             }
//             lib_dir.mkdirs()
//             uri("https://github.com/hamza-Algohary/gtk-native-binaries/releases/download/v0.1/gtk4-win.zip").toURL().openStream().use { it.copyTo(FileOutputStream(File("app/src/main/resources/lib/win.zip"))) }            
//             unzip("app/src/main/resources/lib/win.zip" , "app/src/main/resources/lib/win")
//             File("app/src/main/resources/lib/win.zip").delete()
//             File(done_file_path_str).writeText("")   
//         }
//     }
// }

tasks.register<Resources>("resources") {

}

tasks.build {
    dependsOn("resources")
}

tasks.compileJava {
    dependsOn("resources")
}