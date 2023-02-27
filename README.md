# CSGO Team Generator

<img src="https://i.imgur.com/uQ9KXlB.jpg"  width="600" height="340">

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84.svg?style=for-the-badge&logo=android-studio&logoColor=white)
![SQLite](https://img.shields.io/badge/sqlite-%2307405e.svg?style=for-the-badge&logo=sqlite&logoColor=white)
![Git](https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white)

Android app that finds 5 random players from database using their nicknames and profile pictures from Liquipedia.net site. Allows to manage database containing players.

## Introduction :raised_hand_with_fingers_splayed:

One day I was wondering how can we save Polish Counter Strike scene. Every month there was a shuffle changing top Polish teams' rosters, leading to no achievements.</br>
So why don't you generate new top team? 

**Features**:
* Find 5 random players from your database divided into roles (Sniper, Rifler, In-game leader) </br>
* Add pro players using their nickname (image will be downloaded from Liquipedia automatically)
* Add custom players using image from your gallery
* Browse and manage list of players (you can remove player you don't like)
* Share your generated team via email, Messenger etc.

<img src="https://i.imgur.com/FHNKrHH.mp4"  width="600" height="340">

## Goals of this project :white_check_mark:

* Develop first advanced project for Android
* Learn how to use SQLite Database in a project
* Use fragments everywhere in order to learn using them and their lifecycle (one activity pattern)
* Don't store pro players' images locally but download them from Liquipedia site using site scraping (Jsoup)
* Use asynchronous tasks with data and network code
* Allow user to add custom players with custom image from gallery (and store them in database)
* Allow user to add pro players using only their Liquipedia nickname (and check if nickname is correct)
* Avoid using ChatGPT, try to base on my notes and documentation
* Publish app on Google Play

## Technology :bulb:
* **Android Studio** framework using Java
* **Gradle** (managing project structure and dependencies)
* **Lombok** (generating getters, setters)
* **Glide** (loading GIF into ImageView, downloading image from URL)
* **jsoup** (site scraping in order to get image URL from Liquipedia's HTML code)
* **SQLite Database** (consists information about players and images of custom players)
* **JUnit 5** (unit tests for DataHandler class)


## Installation :hammer_and_wrench:	

* Press the Fork button (located on the top right corner of the page) to save copy of this project on your account.

* Download the repository files (project) from the download section or clone this project by typing in the bash the following command:
```
git clone https://github.com/matmirowski/csgo-team-generator.git
```
> :warning: Make sure that you've installed Lombok plugin for Android Studio :warning:
