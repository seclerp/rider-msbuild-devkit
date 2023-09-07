/**
* JetBrains Space Automation
* This Kotlin script file lets you automate build activities
* For more info, see https://www.jetbrains.com/help/space/automation.html
*/

job("Build and Test") {
    startOn {
        gitPush {
            anyRefMatching {
                +"refs/heads/main"
                // Some sort of workaround and replacement for incorrectly working `codeReviewOpened`
                // See https://youtrack.jetbrains.com/issue/SPACE-18517
                +"refs/merge/*/head"
            }
        }
    }
    container(image = "seclerp/rider-plugin-ubuntu:0.20230324.203320") {
        kotlinScript { api ->
            api.gradlew("prepare", "--stacktrace")
            api.gradlew("check", "--stacktrace")
            api.gradlew("checkDotnet", "--stacktrace")
            api.gradlew("buildPlugin", "--stacktrace")
        }
    }
}
