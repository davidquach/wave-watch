# Wave Watch

## Wave Watch Overview
Wave Watch is a mobile application focused on providing wave forecasts to beachgoers, ensuring they stay informed about safety conditions at different beaches.

### Features
- **Data Source:** The application retrieves information from the Open-Meteo Marine Weather API, collecting data from specified coordinates.
- **Data Normalization:** Collected data is normalized to generate an overall safety score, aiding users in assessing beach safety.
- **Asynchronous Data Retrieval:** Utilizing a separate thread, the application efficiently sends requests to the API for data collection.
- **Dynamic UI:** The application dynamically adjusts its interface, changing colors and themes based on the assessed safety level, enhancing user experience and safety awareness.

### Purpose
Wave Watch serves tourists and newcomers to beaches by providing real-time safety updates, leveraging technology to ensure a safer and informed beach experience.
