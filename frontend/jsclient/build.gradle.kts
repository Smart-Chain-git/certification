import com.moowork.gradle.node.npm.NpmTask

plugins {
    id("com.github.node-gradle.node")
}

node {

    // If true, it will download node using above parameters.
    // If false, it will try to use globally installed node.
    download = true

    // Version of node to use
    version = "10.16.3"

    // Set the download directory for unpacking node
    workDir = file("../.gradle/nodejs")

    // Set the download directory for NPM
    npmWorkDir = file("../.gradle/npm")

}


/**
 * Define the task that runs the unit tests
 */
task("npm_test_unit", NpmTask::class) {
    setNpmCommand("run", "test:unit")
}

task("npm_test_cypress", NpmTask::class) {
    setNpmCommand("run", "cypress:run")
}

task("npm_test_ci", NpmTask::class) {
    setNpmCommand("run", "ci")
}

tasks {
    clean {
        delete("$projectDir/build")
        delete("$projectDir/dist")
    }
}

// dependance des taches
tasks["npm_install"].dependsOn(tasks.npmInstall)
tasks["npm_run_serve"].dependsOn(tasks["npm_install"])
tasks["npm_run_build"].dependsOn(tasks["npm_install"])
tasks["npm_test_unit"].dependsOn(tasks["npm_install"])
tasks["npm_test_cypress"].dependsOn(tasks["npm_install"])
tasks["npm_test_ci"].dependsOn(tasks["npm_install"])
tasks["assemble"].dependsOn(tasks["npm_run_build"])

