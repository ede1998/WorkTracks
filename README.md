# WorkTracks

WorkTacks is a simple app to track the time when you've been working.

## Features

* __Tracking__: track the time you have worked
* __Statistics__:
  * Shows the total you worked on a day
  * Shows the difference to the time you should have worked
  * Shows the difference to the time you should have worked, accumulated for each week until this day
* __Import/Export__: You can import and export your data as a CSV file.
* __Privacy__:
  * does not contain any in-app analytics
  * does not have Google Play Services (I think)
  * has no cloud integration

## Screenshots

<div>
    <img src="screenshots/01-overview.jpg" width="25%" alt="Overview page" />
    <img src="screenshots/02-add-time.jpg" width="25%" alt="Add time page" />    
    <img src="screenshots/03-settings.jpg" width="25%" alt="Settings page" />
</div>

## Building

This app uses the Gradle build system. To build this project, use the "gradlew build" command or use "Import Project" in Android Studio.

## Planned features

- [ ] create real and signed release
- [ ] remove destructive migration to prevent possible data loss
- [ ] move add button to top bar so it does not overlap other information
- [ ] add work time per week parameter
- [ ] remove unused settings
- [ ] swipe left/right for new week
- [ ] edit/delete workday by clicking
- [ ] do not allow empty input when saving
- [ ] design icon
- [ ] enable preview of work time for entire week in add or edit fragment