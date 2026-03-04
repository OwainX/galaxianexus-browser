# GalaxiaNexus Browser - Agent Memory

## Project Overview
This is a Minecraft Fabric mod that provides an embedded web browser with automatic UUID injection.

## Build System
- **Build Tool**: Gradle with Fabric Loom
- **Build Command**: `./gradlew build`
- **Run Client**: `./gradlew runClient`
- **Clean Build**: `./gradlew clean build`

## Project Structure
```
/workspace/project/
├── src/main/java/com/galaxianexus/browser/
│   ├── GalaxiaNexusBrowserMod.java      # Main entry point
│   ├── BrowserManager.java               # UUID injection & browser operations
│   ├── BrowserScreen.java                # In-game browser UI
│   └── WebContentRenderer.java           # Web content rendering
├── src/main/resources/
│   ├── fabric.mod.json                   # Mod metadata
│   ├── galaxianexus-browser.mixins.json  # Mixin configuration
│   └── assets/galaxianexus-browser/      # Assets (icons, etc.)
├── build.gradle                          # Build configuration
├── gradle.properties                     # Version properties
└── settings.gradle                       # Project settings
```

## Key Features
1. **UUID Injection**: Automatically injects player UUID and username into all web requests as query parameters
2. **Dual Browser Modes**: In-game renderer and external browser support
3. **Player Avatar**: Displays player's Minecraft skin using Crafatar API
4. **Commands**: `/browser` and `/browserexternal`

## Technical Details
- **Minecraft Version**: 1.20.4
- **Fabric Loader**: 0.15.3+
- **Java Version**: 17
- **Dependencies**: Fabric API 0.91.2+

## Development Notes
- The mod is client-side only
- Uses Crafatar API for avatar rendering: `https://crafatar.com/avatars/{uuid}`
- UUID injection format: `?player_uuid={uuid}&player_name={name}`
- Simple HTML parser strips tags and renders text content

## Common Tasks
- **Add new command**: Register in `GalaxiaNexusBrowserMod.registerCommands()`
- **Modify UUID injection**: Edit `BrowserManager.injectPlayerData()`
- **Update UI**: Modify `BrowserScreen.render()`
- **Change content rendering**: Edit `WebContentRenderer.parseHtmlContent()`

## Testing
- Use `/browser` command in-game to test
- Test with URLs: `https://example.com`, `https://httpbin.org/get`
- Check UUID injection in browser network tools

## Future Enhancements
- Server plugin for automatic browser launching via custom packets
- JavaScript execution support
- Cookie management
- Multi-tab support
