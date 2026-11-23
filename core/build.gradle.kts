plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

tasks.test {
    useJUnit()
}

val ws66Preview = layout.buildDirectory.dir("generated/ws66-preview")
tasks.register<JavaExec>("generateWs66Preview") {
    val tests = sourceSets["test"]
    dependsOn(tests.classesTaskName)
    classpath = tests.runtimeClasspath
    mainClass.set("com.playereden.gridmates.wordsearch.core.Ws66PreviewFixture")
    args(ws66Preview.get().dir("assets/ws66-v110").asFile.absolutePath)
    inputs.files(tests.output, sourceSets["main"].output)
    outputs.dir(ws66Preview)
}
