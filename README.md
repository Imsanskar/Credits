# Credit Token

## How to Run
* Build the project
```
    ./gradlew build 
```
The compiled jar bundle will be generated at ./irc2-token/build/libs/irc2-token-0.9.1-optimized.jar

* Optimize the jar
You need to optimize your jar bundle before you deploy it to local or ICON networks. This involves some pre-processing to ensure the actual deployment successful.

gradle-javaee-plugin is a Gradle plugin to automate the process of generating the optimized jar bundle. Run the optimizedJar task to generate the optimized jar bundle.
```
    ./gradlew optimizedJar
```
The output jar will be located at ./hello-world/build/libs/hello-world-0.1.0-optimized.jar.

* Deploy the optimized jar
You can deploy using either of the following commands. The build.gradle in hello-world has 4 endpoints included. To deploy on Sejong, run the following command. To deploy on other networks, for example Lison, change deployToSejong to deployToLisbon.

```
./gradlew hello-world:deployToSejong -PkeystoreName=<your_wallet_json> -PkeystorePass=<password>
```

## Example
```
./gradlew hello-world:deployToSejong -PkeystoreName='JavaTest.json' -PkeystorePass='p@ssw0rd'
```
To use the following command, make sure you have gradle.properties file with KeyWallet and password linked.
```
./gradlew hello-world:deployToSejong
```

## Deployment
[Deployed here](https://sejong.tracker.solidwallet.io/contract/cx2f97f267caf9f7da1a7855f2f75d331a47f8f4e7)
