# RoundsTestTask

📱 **Description**:

Summary
The purpose of this test project is to implement a library for downloading and displaying images.
The test project consists of the following steps:
1. Implement a library for image downloading and caching (Without using external libraries as Glide).
2. Fetch a JSON with a list of images to be used with the image loading library from here.
3. Build an example app (In the same project) that uses the image downloading library with the images list provided.

Assignment Details
1. Image loading library
Implement a library that will download and cache an image. It should receive the following input parameters:
    URL
    Placeholder
    Some view, that the image loading library will assign the image to
Cached image should be valid for 4 hours. Also, the library should support manual cache invalidation.

2. Example app
    Fetch a JSON with the images URL list
    Build a screen with the list of loaded images. Each view should display the loaded image (or a placeholder while loading) and its ID (both ID and URL can be found within the JSON)
    Add a button that will invalidate the cache on tap
    The app can be built either with Java or Kotlin, and the library should support both
    The app should be implemented using Android View, not Jetpack Compose

## 🛠 Technologies
- Kotlin
- Android SDK version: **35**

## ⚙️ Installation
Clone the repository:
``bash
git clone https://github.com/vasichevda/DevExperts.git

## 📜 License
MIT License  
Copyright (c) 2025 Denis Vasichev