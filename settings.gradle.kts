/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/7.4.2/userguide/multi_project_builds.html
 * This project uses @Incubating APIs which are subject to change.
 */


                      
rootProject.name = "Gradle Demo"

println("In the settings.gradle.kts file - This is executable during initialisation phase")
println("In the settings,gradle.kts file - Determine which project take part in the build")
include("app")

plugins {
  id("com.gradle.enterprise") version("3.9)
  }
  
gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}
