#Publishing

Before publishing, make sure the [VERSION_NAME](buildsystem/artifactory.gradle) is updated

In order to publish a new version to artifactory run in the command line the following commands:
- `./gradlew clean assemble`
- `./gradlew artifactoryPublish`

After this, both modules will be uploaded to artifactory under the same version.