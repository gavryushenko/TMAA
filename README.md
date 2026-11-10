# My Weather App

<p align="left">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android"/>
  <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" alt="Gradle"/>
  <img src="https://img.shields.io/badge/OpenWeather_API-EB6E4B?style=for-the-badge&logo=openweathermap&logoColor=white" alt="OpenWeather API"/>
</p>

> An Android application developed using Kotlin.

This project was created as part of the **Tvorba mobilních aplikací pro Android (TMAA)** course at **Palacký University Olomouc**.

---

## About the Project
The application is developed for the Android operating system using Kotlin. It serves as a practical project for applying knowledge and skills acquired during the university course.

## Technologies
### Programming Language
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
### Platform
![Android](https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white)
### Build System
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white)
### External API
![OpenWeather](https://img.shields.io/badge/OpenWeather_API-EB6E4B?style=flat-square&logo=openweathermap&logoColor=white)

--

## API Configuration
This project uses the **OpenWeather API** to access weather data.
The API key is stored as an environment variable and is not committed to Git.

### Environment Setup
Before building or running the application, configure the following environment variable in PowerShell:
```powershell
$env:OPENWEATHER_API_KEY="your_api_key"
```

Replace ```your_api_key``` with your actual API key.
