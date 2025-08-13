# GeoMarker Android Application - Project Report

## 1. Introduction

This document details the development of the GeoMarker Android application, focusing on its interaction with a REST API, key implementation choices, challenges encountered, and their respective solutions. The application aims to provide a mobile platform for managing and visualizing geographic entities on a map, fulfilling the requirements of the CSE 489 Mobile Application Development Lab Exam.

## 2. API Interaction Details

The GeoMarker application communicates with a remote REST API to perform Create, Read, Update, and Delete (CRUD) operations on geographic entities.

*   **Retrofit and OkHttp:** The application leverages Retrofit as a type-safe HTTP client for making API requests, with OkHttp as the underlying HTTP client. An `HttpLoggingInterceptor` is configured with OkHttp to log request and response bodies, which was invaluable during development and debugging.
*   **`ApiService.kt`:** This interface defines the API endpoints and their corresponding HTTP methods (`@GET`, `@POST`, `@PUT`, `@DELETE`). It correctly maps the required parameters for each operation, including `MultipartBody.Part` for image file uploads in `createEntity` and `FormUrlEncoded` for `updateEntity`.
*   **`Entity.kt` Data Model:** The `Entity` data class (`com.example.geomarker.model.Entity`) serves as the representation of a geographic entity within the application. It includes fields for `id`, `title`, `lat`, `lon`, and `imageUrl`. Crucially, the `imageUrl` property is annotated with `@SerializedName("image")` to correctly map the "image" field received from the API's JSON response to the `imageUrl` property in the Kotlin data class. This also clarified that the API expects a URL string for image updates, rather than a file.
*   **`EntityRepository.kt`:** This class (`com.example.geomarker.repository.EntityRepository`) acts as a single source of truth for entity data. Its primary role is to abstract the data sources, providing a clean API for the ViewModels.

## 3. Key Implementations and Challenges

### 3.1. Synchronizing API and Local Database (Initial Challenge & Solution)

**Challenge:** Initially, the `EntityRepository` was designed to primarily interact with the local Room database for Create, Update, and Delete (CUD) operations. While it fetched data from the API using `refreshEntities()`, changes made by the user (creating, updating, or deleting entities) were only reflected in the local database and not propagated to the remote API. This was a significant deviation from the project requirements, which explicitly stated that CUD operations should involve API requests.

**Solution:** The `EntityRepository.kt` was refactored to ensure that all CUD operations first make an API call. If the API call is successful, the corresponding change is then applied to the local Room database to maintain data consistency.
*   `createEntity(title, lat, lon, imageFile)`: Makes a `POST` API call. On success, it triggers `refreshEntities()` to fetch the newly created entity (and all others) from the API and update the local cache.
*   `updateEntity(id, title, lat, lon, imageUrl)`: Makes a `PUT` API call. On success, it directly updates the corresponding entity in the local Room database.
*   `deleteEntity(id)`: Makes a `DELETE` API call. On success, it deletes the entity from the local Room database.
This ensures that the remote API remains the authoritative source of truth, with the local database serving as a synchronized cache.

### 3.2. Image Upload and Resizing

**Implementation:**
*   **Image Selection:** The `EntityFormFragment` uses `ActivityResultContracts.GetContent()` to allow users to select an image from their device's gallery.
*   **Image Resizing:** A critical requirement was to resize images to 800x600 pixels before submission. This logic is handled within `EntityFormViewModel.kt`:
    *   `uriToBitmap()`: Converts the selected `Uri` to a `Bitmap`.
    *   `resizeBitmap()`: Scales the `Bitmap` to the target dimensions (800x600).
    *   `bitmapToFile()`: Saves the resized `Bitmap` to a temporary `File` in the app's cache directory. This `File` is then passed to `EntityRepository.createEntity` for the `MultipartBody` API upload.
*   **Image Display:** Glide is used throughout the application (e.g., in `CustomMarkerInfoWindow`) to efficiently load and display images from their URLs.

### 3.3. Map Integration (OSMDroid)

**Implementation:**
*   **Map View:** The `MapFragment` utilizes the OSMDroid library to display an OpenStreetMap.
*   **Centering and Zoom:** The map is programmatically centered on Bangladesh (`GeoPoint(23.6850, 90.3563)`) with an initial zoom level of 7.0, as specified in the requirements.
*   **Markers:** Geographic entities are displayed as `Marker` overlays on the map. Each marker's position is set using the entity's latitude and longitude.

### 3.4. Image Enlargement from Marker Info Window (Challenge & Solution)

**Challenge:** The requirement was to display an enlarged version of an entity's image when the image within the marker's info window is clicked. Directly navigating using `findNavController()` from within `CustomMarkerInfoWindow` (which extends `MarkerInfoWindow`) proved problematic because `MarkerInfoWindow` does not have direct access to the `NavController` of the hosting `MapFragment`. Attempting to cast `mapView.context` to a `FragmentActivity` and then finding the `NavController` was an incorrect approach that would lead to runtime errors.

**Solution:** A more robust and decoupled approach was implemented:
1.  **`ImageDetailFragment.kt`:** A new fragment was created to specifically handle the display of an enlarged image and its title.
2.  **Navigation Graph Update:** The `nav_graph.xml` was updated to include `ImageDetailFragment` as a destination with `imageUrl` and `title` arguments.
3.  **Lambda Callback:** The `CustomMarkerInfoWindow`'s constructor was modified to accept a lambda function (`onImageClick: (imageUrl: String, title: String) -> Unit`).
4.  **`MapFragment` Integration:** In `MapFragment`, when `CustomMarkerInfoWindow` is instantiated, a lambda is passed that captures the `findNavController()` of the `MapFragment` and performs the navigation to `ImageDetailFragment` with the appropriate arguments. This ensures that the navigation context is correct.

### 3.5. Current GPS Location Pre-filling

**Implementation:** The `EntityFormFragment` now automatically populates the latitude and longitude fields with the device's current GPS location when a new entity is being created.
*   **`FusedLocationProviderClient`:** Used to obtain the last known location.
*   **Permission Handling:** Location permissions (`Manifest.permission.ACCESS_FINE_LOCATION`) are requested using `ActivityResultContracts.RequestPermission()`. If permission is granted, `getCurrentLocation()` is called to fetch and display the coordinates.

### 3.6. Offline Caching (Bonus Task)

**Implementation:** The application implements offline caching using the Room Persistence Library, fulfilling a bonus requirement.
*   **`AppDatabase.kt`:** Defines the Room database, including the `Entity` class as an entity and providing access to the `EntityDao`. `fallbackToDestructiveMigration()` is used for simplified schema updates during development.
*   **`EntityDao.kt`:** A Data Access Object (DAO) interface that defines methods for interacting with the `entities` table (e.g., `getAllEntities()`, `insert()`, `update()`, `deleteById()`).
*   **Synchronization:** The `refreshEntities()` method in `EntityRepository` is responsible for fetching the latest data from the remote API, clearing the local database, and then inserting the fresh data. This ensures that the local cache is always up-to-date with the server. All UI components observe data from the local Room database, providing a seamless experience even with intermittent network connectivity.

## 4. Screenshots

*(This section is a placeholder. In a real submission, screenshots of the application's key screens would be inserted here, demonstrating the map view with markers, the entity creation/edit form, the entity list, and the enlarged image view.)*

*   **Screenshot 1: Map View with Markers**
*   **Screenshot 2: Entity Creation/Edit Form (with pre-filled GPS)**
*   **Screenshot 3: Entity List**
*   **Screenshot 4: Enlarged Image View**
*   **Screenshot 5: Marker Info Window**

## 5. Conclusion

The GeoMarker Android application successfully implements the core requirements of the lab exam, providing robust CRUD functionality for geographic entities synchronized with a REST API. Key architectural patterns (MVVM, Repository) and modern Android development practices (Kotlin Coroutines, Jetpack components) have been utilized. Challenges related to API synchronization, image handling, and inter-component communication (like navigation from info windows) were identified and addressed with appropriate solutions. The bonus task of offline caching using Room has also been successfully integrated.
