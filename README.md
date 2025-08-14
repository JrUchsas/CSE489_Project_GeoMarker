# GeoMarker Android Application

## Project Description

GeoMarker is an Android mobile application developed for the CSE 489 Mobile Application Development Lab Exam. The application interacts with a REST API to manage and display geographic entities (markers) on a map. Users can create, view, edit, and delete these entities. Each entity has a title, latitude, longitude, and an image. The map is centered on Bangladesh, and the application supports offline caching of entities.

## Features

*   **Map View:** Displays geographic entities as markers on an OpenStreetMap, centered on Bangladesh.
*   **Entity Management (CRUD Operations):**
    *   **Create:** Add new entities by providing a title, getting the current GPS location, and uploading an image.
    *   **Read:** View all created entities on the map and in a list.
    *   **Update:** Modify existing entities, including their title, coordinates, and image.
    *   **Delete:** Remove entities from the map and the remote server.
*   **Entity List:** View all entities in a scrollable list with options to edit or delete each entry.
*   **Offline Caching (Bonus Feature):** Entities fetched from the API are cached locally using the Room Persistence Library, allowing users to view previously fetched data without an internet connection.
*   **Error Handling:** The application provides user feedback for API failures or invalid inputs.

## Technologies Used

*   **Kotlin:** The primary programming language.
*   **Android Jetpack:**
    *   **ViewModel & LiveData:** For managing UI-related data in a lifecycle-conscious way.
    *   **Navigation Component:** For handling in-app navigation.
    *   **Room:** For local database and offline caching.
*   **Networking:**
    *   **Retrofit & Gson:** For making REST API calls and parsing JSON.
    *   **OkHttp:** For logging network requests and responses.
*   **UI & Mapping:**
    *   **OpenStreetMap (OSMDroid):** For displaying the map.
    *   **Glide:** For loading and displaying images.
*   **Location:**
    *   **Google Play Services Location:** For fetching the device's current GPS location.
*   **Asynchronous Programming:**
    *   **Kotlin Coroutines:** For managing background tasks.

## Setup and Installation

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    cd GeoMarker
    ```
2.  **Open in Android Studio:**
    *   Open Android Studio.
    *   Select `File` > `Open` and navigate to the cloned `GeoMarker` directory.
    *   Android Studio will sync the project. This may take a few minutes.
3.  **Build and Run:**
    *   Connect an Android device or start an emulator.
    *   Click the `Run` button in Android Studio.

## Project Structure

The project follows the MVVM (Model-View-ViewModel) architecture pattern. Here's a brief overview of the key components:

*   **`model`:** Contains the `Entity` data class and the `EntityDao` for Room database operations.
*   **`api`:** Contains the `ApiService` interface for defining Retrofit API endpoints.
*   **`repository`:** The `EntityRepository` class is responsible for fetching data from the API and the local database.
*   **`viewmodel`:** The `MapViewModel` and `EntityFormViewModel` prepare and manage data for the UI.
*   **`ui` (Fragments & Adapters):**
    *   `MapFragment`: Displays the map and markers.
    *   `EntityListFragment`: Displays the list of entities.
    *   `EntityFormFragment`: Provides the form for creating and editing entities.
    *   `EntityListAdapter`: The adapter for the RecyclerView in `EntityListFragment`.
*   **`AppDatabase`:** The Room database class.
*   **`MainActivity`:** The main activity that hosts the navigation graph.