# GeoMarker Android Application

## Project Description

GeoMarker is an Android mobile application developed as part of the CSE 489 Mobile Application Development Lab Exam. The application interacts with a REST API to manage and display geographic entities (markers) on a map. Users can create, view, edit, and delete these entities, each of which includes a title, latitude, longitude, and an associated image. The map is centered on Bangladesh, and the application supports offline caching of entities.

## Features

*   **Map View:** Displays geographic entities as markers on an OpenStreetMap, centered on Bangladesh.
*   **Entity Management:**
    *   **Create Entity:** Add new entities by providing a title, automatically populated current GPS coordinates (latitude and longitude), and uploading an image (resized to 800x600 pixels before submission).
    *   **View Entities:** See all created entities on the map. Clicking a marker displays an info window with the entity's title, coordinates, and a small image preview. Clicking the image in the info window shows an enlarged version.
    *   **Edit Entity:** Modify existing entities, including updating their title, coordinates, and image.
    *   **Delete Entity:** Remove entities from the map and the remote server.
*   **Entity List:** View all entities in a scrollable list, with options to edit or delete each entry.
*   **Offline Caching (Bonus):** Entities fetched from the API are cached locally using the Room Persistence Library, allowing for viewing of previously fetched data even without an active internet connection.
*   **Error Handling:** Basic error handling is implemented to provide user feedback for API failures or invalid inputs.

## REST API Details

The application interacts with a provided REST API for CRUD operations on geographic entities.

*   **Base URL:** `https://labs.anontech.info/cse489/t3/`
*   **Endpoints (all relative to Base URL):**
    *   `POST api.php`: Create a new entity.
    *   `GET api.php`: Retrieve all entities.
    *   `PUT api.php`: Update an existing entity.
    *   `DELETE api.php`: Delete an entity.

## Technologies Used

*   **Kotlin:** Primary programming language for Android development.
*   **Android Jetpack Components:**
    *   **Lifecycle:** `LiveData`, `ViewModel` for managing UI-related data in a lifecycle-conscious way.
    *   **Navigation Component:** For managing in-app navigation between fragments.
    *   **Room Persistence Library:** For local SQLite database interactions and offline caching.
*   **Networking:**
    *   **Retrofit:** Type-safe HTTP client for making REST API calls.
    *   **OkHttp:** HTTP client, used by Retrofit, with a logging interceptor for debugging.
    *   **Gson Converter:** For converting JSON responses to Kotlin objects.
*   **Image Loading:**
    *   **Glide:** Fast and efficient image loading library for displaying images from URLs.
*   **Mapping:**
    *   **OSMDroid:** Open-source map library for displaying OpenStreetMap.
*   **Location Services:**
    *   **Google Play Services Location:** For obtaining the device's current GPS location.
*   **Kotlin Coroutines:** For asynchronous programming and managing background tasks.

## Setup and Installation

To set up and run the GeoMarker application on your local machine:

1.  **Clone the repository:**
    ```bash
    git clone <repository_url_here>
    cd GeoMarker
    ```
    *(Replace `<repository_url_here>` with the actual repository URL)*

2.  **Open in Android Studio:**
    *   Launch Android Studio.
    *   Select `File` > `Open` and navigate to the cloned `GeoMarker` directory.
    *   Android Studio will automatically sync the Gradle project. Ensure you have an active internet connection for dependency downloads.

3.  **Build and Run:**
    *   Connect an Android device or start an Android Emulator.
    *   Click the `Run` button (green triangle) in Android Studio to build and install the application on your device/emulator.

## Usage

1.  **Map View:** The application starts with a map centered on Bangladesh. Existing entities will appear as markers.
2.  **Add New Entity:**
    *   Click the floating action button (FAB) on the map screen.
    *   Fill in the title. The latitude and longitude fields will be pre-filled with your current GPS location (ensure location permissions are granted).
    *   Click "Select Image" to choose an image from your device.
    *   Click "Save" to create the entity.
3.  **View Entity Details:**
    *   Tap on a marker on the map to open an info window displaying the entity's title, coordinates, and a small image preview.
    *   Tap on the image within the info window to view an enlarged version of the image.
4.  **Entity List:**
    *   Access the entity list from the navigation drawer (if implemented) or a dedicated button (if available).
    *   The list displays all entities.
    *   **Edit:** Tap the "Edit" button next to an entity to open the form pre-filled with its details for modification.
    *   **Delete:** Tap the "Delete" button next to an entity to remove it.

## Screenshots

*(Insert screenshots of the application here: Map view with markers, Entity Form, Entity List, Enlarged Image View)*

## Error Handling

The application includes basic error handling for network requests and form validation. Error messages are typically displayed as `Toast` messages to the user.

## Future Improvements

*   **User Authentication:** Implement user authentication to secure API endpoints and restrict CRUD operations to authenticated users.
*   **Advanced Image Handling:** More robust image handling, including progress indicators for uploads and better error recovery.
*   **Map Customization:** Allow users to switch between different map providers or customize map layers.
*   **Search and Filter:** Add functionality to search and filter entities.
*   **UI/UX Enhancements:** Further refine the user interface and experience.
