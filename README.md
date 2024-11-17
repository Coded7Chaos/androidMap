# 🗺️ MapLibreDemo

## 🎯 About
A demonstration project showing how to implement beautiful offline maps in Android using MapLibre and Ramani Maps with Jetpack Compose. Perfect for hiking apps, travel companions, or any location-based service requiring offline functionality.

## ✨ Features
- Offline-first map implementation
- Vector tile support
- Custom map styling
- Seamless integration with Jetpack Compose
- Efficient asset management system
- Multiple approaches for map data acquisition:
  - Pre-packaged solutions (Quick start)
  - Paid solutions (MapTiler)
  - Custom generation (Tilemaker)

## 🛠️ Technical Stack
- Kotlin
- Jetpack Compose
- MapLibre
- Ramani Maps

## 📦 Dependencies
Add this to your app's `build.gradle.kts`:
```kotlin
implementation("org.ramani-maps:ramani-maplibre:0.7.0")
```

## 🚀 Getting Started
1. Clone the repository
2. Place your map assets in the assets directory:
   - `.mbtiles` file
   - `style.json`
   - sprites and glyphs

3. Update the style file with correct references:
```json
{
  "sources": {
    "openmaptiles": {
      "type": "vector",
      "url": "___FILE_URI___"
    }
  },
  "sprite": "asset://sprites/sprite",
  "glyphs": "asset://glyphs/{fontstack}/{range}.pbf"
}
```

## 📝 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
📖 For more detailed information, check out the full article on Medium: [Creating Delightful Offline Maps with MapLibre and Ramani Maps in Jetpack Compose](https://medium.com/@TonyGnk/creating-delightful-offline-maps-with-maplibre-and-ramani-maps-in-jetpack-compose-28d6781409f1)
