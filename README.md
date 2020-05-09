# Quarantine-Planner-App
A mobile application project for my CS4237 (Software Design for Handheld Devices) Course written in Kotlin in the Spring 2020 Semester.

# Libraries/APIs Used
- Google Play Services 
    - Firebase (for login/sign-up)
- Square's OkHttp Network Library 
- Square's Picasso Image Loading Library
- Jetbrains' Anko Networking Library
- MovieDB Movie/TV API
- Edamam Recipe API

# Project Description
Users can sign up for an account with the application, and once logged in (using FirebaseAuth) can access the core functionality of the application. Once logged in, the user selects from a number of preferences for meals and enterntainment, and then the application pings out to 2 API's to make reccomendations about what to eat and what to watch for a night in. The application also supports (albeit not perfectly translated) Spanish Language if the device is in that language.

# Testing Instructions
If you would like to test the application for yourself, you can download the ``` app_debug.apk ``` file and install it on a physical device over USB or on a virtualized device.

# Example Usage Screenshots
![Login Screen Image](https://github.com/reesealanj/Quarantine-Planner-App/blob/master/img/login_en.png)
![Preferences Screen Image](https://github.com/reesealanj/Quarantine-Planner-App/blob/master/img/preferences_en.png)
![Results Screen Image](https://github.com/reesealanj/Quarantine-Planner-App/blob/master/img/results_en.png)