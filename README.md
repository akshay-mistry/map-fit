# map-fit
An Android application that helps users to stay active while staying connected with people nearby.

## Inspiration
Many people today lack the motivation to exercise simply because they do not have anyone to workout with. For most people, going outside and working out by themselves is less enjoyable than it is with a friend. MapFit effectively solves this problem by providing users with an easy way to track their workouts and meet new people nearby while exercising.

## What it does
MapFit is an Android application that helps users to meet new people while staying active! The main screen of MapFit is a Google Map with a marker updating the user's current location in real-time. From the app, users can start an activity, such as a running workout or a basketball workout, and see the icons of other MapFit users who are engaged in activities nearby them on a map. After clicking on the icon of a nearby user, an automated email message is generated for joining the workout. For example, users can go on a run together, start a bike trip, play pickup basketball, start a team volleyball game, etc with the help of MapFit. Users can also see a log of their past workouts from the app. Now, people will never have to worry about having to work out by themselves!

## How I built it
I built MapFit using Android Studio, Java, Google Maps SDK, Firebase Authentication, Firebase Realtime Database, FusedLocationProviderClient, and Intent services.

## Challenges I ran into
One of the main challenges I ran into was organizing and reading the data from the Firebase Realtime Database. To overcome this, I created two database references, "activeusers" and "workouts", in order to sort my ActivityInformation objects, which hold the data that is sent to Firebase. 

## Accomplishments that I'm proud of
I am proud of MapFit's appealing user interface, as the layout of the app is easy to navigate with a Google Maps homepage and buttons in the top toolbar of the app. With a simple design, MapFit is very user-friendly, helping it appeal to a wider audience of individuals who seek to meet new people while exercising. 

## What I learned
By creating MapFit, I learned how to effectively use Firebase and its features. Implementing both Firebase Authentication and Firebase Realtime Database in MapFit helped me to familiarize myself with a useful technology that I can continue to use in future projects. 

## What's next for MapFit
In the future, MapFit will implement the Google Fit API in order to track additional workout data including steps traveled and calories burned. By gathering additional information regarding users' workouts, MapFit will become a more effective tool to contribute to a healthier society altogether. 
