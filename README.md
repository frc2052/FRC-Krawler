# FRC Krawler

<img src="https://raw.githubusercontent.com/frc2052/FRC-Krawler/master/art/logoformarketing.jpg" width="64"> [<img src="https://github.com/pioug/google-play-badges/blob/main/svg/English.svg" alt="Google Play" height="48"/>](https://play.google.com/store/apps/details?id=com.team2052.frckrawler&hl=en)

FRC Krawler is a *FIRST* Robotics Competition scouting app developed by Team 2052 - KnightKrawler.

## Project setup
You will need to set up some project secrets to properly build this project.

### TBA API Key
Update your `local.properties` file with the following:

 * `frckrawler.tba-api-key`: [An auth key for The Blue Alliance](https://www.thebluealliance.com/apidocs)

```
frckrawler.tba-api-key=abc123
```

### Firebase configuration (optional)
This repo includes a fake Firebase configuration file, and you should be able to run the app as-is.
If you want to test Firebase integration, you will need to replace the default configuration file 
with a real one.

Set up a Firebase project and [follow steps 1 & 2 here](https://firebase.google.com/docs/android/setup?authuser=1&hl=en#console) to add an Android app to your project.

Follow the instructions in step 3 to download the `google-services.json` file and replace the one in this project at `app/google-services.json`.
  
## Issues?
Please feel free to open feature requests and bug reports here on GitHub! We'll do our best to 
address them in a timely manner.

## Buidling documentation
Our docs site is published using [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/).
To get started, install python. Then run:
```shell
python3 -m venv venv
source venv/bin/activate
pip install mkdocs-material

mkdocs serve
```
