<!-- PROJECT LOGO -->
<br />
<div align="center">
  <h3 align="center">F1bot</h3>

  <p align="center">
    A Formula 1 Discord bot made in Kotlin with <a href="https://github.com/DV8FromTheWorld/JDA">JDA<a/> and the <a href="https://ergast.com/mrd/">Ergast</a> api.
    <br />
      If you want to test the bot you can use this <a href = "https://discord.com/api/oauth2/authorize?client_id=1091027933298180137&permissions=8&scope=bot+applications.commands">invite link.</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#examples-and-screenshots">Examples/screenshots</a></li>
  </ol>
</details>



## About The Project

![getrace](https://i.imgur.com/eLIGCvC.png) <br>

## Built With

* [Kotlin](https://kotlinlang.org/)
* [Gradle](https://gradle.org/)
* [JDA](https://github.com/DV8FromTheWorld/JDA)
* [Jib](https://github.com/GoogleContainerTools/jib)
* [org.json](https://github.com/stleary/JSON-java)

Additionally the bot relies on the [Ergast](https://ergast.com/mrd/) api for information about Formula 1

## Getting Started

To run the bot you first need to create a Docker image. I do this using Jib and Gradle without the need for a Dockerfile.
To create an image you can use gradlew. <br>
```sh
./gradlew build jibDockerBuild
```
The bot can be started and put into a docker container by running f1bot.sh. <br>

``` sh
./f1bot.sh
```
Once you have setup the Discord dev settngs under prerequisites you can create an invite link for the bot.

### Prerequisites
* A Discord account with an app created at the ![Discord developer portal](https://discord.com/developers/docs/getting-started).
* A Discord bot for the app with its token put into /src/main/resources/token/token.txt.
* The bot must have Message Content Intent enabled, you can read more about this at the developer portal.
* Docker

### Alternatively
If you do not wish to use Docker then you should be able to run the bot without it. You can either create a fatjar and run it or build the project and run it through Main.kt <br>
However I recommend using Docker.



## Usage
An invite link to test the bot is available [here](https://discord.com/api/oauth2/authorize?client_id=1091027933298180137&permissions=8&scope=bot+applications.commands) <br>
The link requires a discord account and a server where you can add the bot. This can be done at discords [website](https://discord.com). You dont have to download anything as discord can be used in the browser.

Once the bot has been added to a server you can simply input a "/" in any text-channel and you should be able to select the bot and view its slash-commands.
The commands have their own descriptions within discord.

## Examples and screenshots

![getrace](https://i.imgur.com/eLIGCvC.png)
![getraceresult](https://i.imgur.com/UdlHbaH.png)
![driverprofile](https://i.imgur.com/dLOXc0H.png)
![drivers](https://i.imgur.com/jlvAdnB.png)
