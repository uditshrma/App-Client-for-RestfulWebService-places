# ClientApp-for-RestfulWebService-places
This app uses [Android architecture components](https://developer.android.com/topic/libraries/architecture/index.html). These are a collection of libraries that help you design robust, testable, and maintainable apps. The code uses [this](https://github.com/uditshrma/RestfulWebService-places) web service.

# Project Conventions
## MVVM Architecture 
The project follows the MVVM design pattern.<br>
MVVM(Model–view–viewmodel) is one of the architectural patterns which enhances separation of concerns, it allows separating the user interface logic from the business (or the back-end) logic.<br><br>
MVVM has mainly the following layers:
- Model<br>
Model represents the data and business logic of the app. One of the recommended implementation strategies of this layer, is to expose its data through observables to be decoupled completely from ViewModel or any other observer.
- ViewModel<br>
ViewModel interacts with model and also prepares observable(s) that can be observed by a View.
- View<br>
Finally, the view role in this pattern is to observe a ViewModel observable to get data in order to update UI elements accordingly.

# Following libraries are used in this app:

## Design
- [Dagger2](https://google.github.io/dagger/) for dependency injection.
- [RxJava](https://github.com/ReactiveX/RxJava) for composing asynchronous and event-based programs (Observer-Observable pattern).
- [RxAndroid](https://github.com/ReactiveX/RxAndroid) for added RxJava classes/functionality for Android.
- [AutoValue](https://github.com/google/auto/tree/master/value) Immutable value-type code generation for Java 1.6+.
## Data
- [RESTful API](https://restfulapi.net/) for online database.
- [Room](https://developer.android.com/topic/libraries/architecture/room) for local database.
## UI
- [Material Design](https://material.io/develop/android/docs/getting-started/) for helping creating UI in coordinance with Google's Material Design guidelines.
- [ConstraintLayout](https://developer.android.com/training/constraint-layout) for creating layouts.
- [Material Dialogs](https://github.com/afollestad/material-dialogs) for creating dialogs.
- [Material Calendar View](https://github.com/prolificinteractive/material-calendarview) for calendar.
## Networking
- [Retrofit](https://square.github.io/retrofit/) (+[okhttp for interceptor](https://github.com/square/okhttp)) for API calls.
- [Gson](https://github.com/google/gson) to serialize and deserialize Java objects to JSON.
## Map
- [Google Play Services](https://developers.google.com/android/guides/overview) for maps, locations & places.

# Architecture:
- MVVM: Model–view–viewmodel
- Repository: One repository per data type.

# Screenshots:


Screenshot 1 | Screenshot 2 | Screenshot 3
------------ | ------------- | -------------
![screenshot1](https://github.com/uditshrma/App-Client-for-RestfulWebService-places/blob/master/screen_shots/Screenshot_1.jpeg) | ![screenshot2](https://github.com/uditshrma/App-Client-for-RestfulWebService-places/blob/master/screen_shots/Screenshot_2.jpeg) | ![screenshot3](https://github.com/uditshrma/App-Client-for-RestfulWebService-places/blob/master/screen_shots/Screenshot_3.jpeg)
