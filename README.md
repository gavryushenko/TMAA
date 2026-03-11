# TMAA
Application for the operating system Android in the programming language Kotlin.

Made by Matvii Gavryushenko

## OpenWeather API key
API key is not committed to git.

Set environment variable before build/run (PowerShell):

- PowerShell: `$env:OPENWEATHER_API_KEY="your_key"`

## Firebase backup verification
Cloud backup is implemented via Firebase Firestore.

1. Run the app and search a city.
2. Tap `Add to Favorites`.
3. Open the `Favorites` screen and tap `Backup`.
4. Open Firebase Console -> `Firestore Database` -> `Data`.
5. Check collection `backups`:
   - `cities`
   - `cityCount`
   - `updatedAt` / `updatedAtEpochMs`

## Reviewer access
Firebase project access was granted to your account:
- `matej.dostal01@upol.cz`

Thank you for your review :)