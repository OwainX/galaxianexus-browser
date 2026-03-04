# GalaxiaNexus Browser

A Minecraft Fabric mod that provides an embedded web browser interface with automatic UUID injection for seamless integration with web services.

## Features

- **🌐 Embedded Chromium Browser**: Full in-game browser powered by MCEF (Minecraft Chromium Embedded Framework)
- **⚡ JavaScript & AJAX Support**: Complete ES6+ JavaScript, AJAX, Fetch API, and all modern web standards
- **🔑 Automatic UUID Injection**: Player UUID and username are automatically injected into all web requests
- **🎨 Player Avatar Display**: Shows your Minecraft skin avatar in the browser toolbar  
- **⚙️ Dual Browser Modes**: 
  - **Embedded Mode** (default): Full in-game browser with JavaScript/AJAX via MCEF
  - **External Mode**: Fallback to system browser for low-end PCs
- **🚀 Modern Web Standards**: Full support for React, Vue, Angular, and other frameworks
- **💬 Chat Link Support**: Server plugins can send clickable links to launch the browser
- **⌨️ Simple Commands**: Easy-to-use commands for opening URLs
- **📝 JSON Configuration**: Customize browser behavior via config file

## Installation

### Basic Installation

1. Download the latest release from the [Releases](https://github.com/yourusername/galaxianexus-browser/releases) page
2. Place the `.jar` file in your Minecraft `mods` folder
3. Make sure you have [Fabric API](https://modrinth.com/mod/fabric-api) installed
4. Launch Minecraft with Fabric Loader

**This gives you:** UUID injection, external browser mode, and all core features.

### Optional: Install MCEF for Embedded Browser

For full in-game browser with JavaScript:

1. **Download MCEF** from https://github.com/montoyo/mcef/releases
2. Place MCEF `.jar` in your `mods` folder
3. Launch Minecraft (MCEF will download Chromium on first run)

**This adds:** In-game Chromium browser with full JavaScript/AJAX support.

See [MCEF_INSTALLATION.md](MCEF_INSTALLATION.md) for detailed instructions.

### Quick Start (No MCEF)

The mod works perfectly **without MCEF** using external browser mode:
- Full JavaScript/AJAX support via system browser
- UUID automatically injected
- Zero additional setup required
- Works out of the box!

## Usage

### Commands

#### Open Browser
```
/browser [url]
```
Opens a browser according to your configured mode (embedded or external). If no URL is provided, opens to the default URL from config.

**Examples:**
```
/browser
/browser https://example.com
/browser https://myserver.com/dashboard
```

**Note:** The browser behavior (embedded vs external) is controlled by the configuration file. See [CONFIGURATION.md](CONFIGURATION.md) for details.

### UUID Injection

The mod automatically injects player data into URLs as query parameters:
- `player_uuid`: The player's UUID
- `player_name`: The player's username

**Example:**
```
Original URL:  https://example.com/page
Injected URL:  https://example.com/page?player_uuid=12345678-1234-1234-1234-123456789abc&player_name=PlayerName
```

If the URL already has query parameters:
```
Original URL:  https://example.com/page?foo=bar
Injected URL:  https://example.com/page?foo=bar&player_uuid=12345678-1234-1234-1234-123456789abc&player_name=PlayerName
```

### Player Avatar

The browser displays your Minecraft skin as an avatar in the top-left corner of the browser toolbar, similar to how Chrome and Firefox display user profiles. The avatar is fetched from [Crafatar](https://crafatar.com/), a free Minecraft avatar API.

## Configuration

The mod can be configured via `config/galaxianexus-browser.json`. On first launch, a default configuration file is created.

### Quick Config Examples

**Embedded Mode (Default) - Full JavaScript Support:**
```json
{
  "mode": "EMBEDDED",
  "enableJavaScript": true,
  "enableCookies": true
}
```

**External Mode - For Low-End PCs:**
```json
{
  "mode": "EXTERNAL"
}
```

For detailed configuration options, see [CONFIGURATION.md](CONFIGURATION.md).

## JavaScript and AJAX Support

GalaxiaNexus Browser provides full JavaScript and AJAX support in two ways:

### ✅ **With MCEF Installed (Embedded Mode)**

Install MCEF separately to get:
- **Full Chromium browser engine** embedded in-game
- **ES6+ JavaScript execution**
- **AJAX and Fetch API**
- **React, Vue, Angular**, and all modern frameworks in-game
- **Your custom web apps** run seamlessly in-game
- **Cookie support** and session management
- **UUID automatically injected** in all requests
- **Player avatar** displayed in browser toolbar

**[Install MCEF](MCEF_INSTALLATION.md)** to enable embedded mode.

### ✅ **Without MCEF (External Mode - Default)**

Works out of the box with:
- Opens URLs in system browser (Chrome/Firefox/Edge)
- **Full JavaScript/AJAX support** via your browser
- **React, Vue, Angular** work perfectly
- **UUID automatically injected** in URL
- **Player avatar** displayed
- **Zero additional setup**
- **Lower performance impact**

**Both modes support your custom web apps with JavaScript/AJAX perfectly!**

The only difference is where the browser renders (in-game vs external window).

## Server Integration

Server plugin developers can integrate with GalaxiaNexus Browser by sending specially formatted messages to players:

### Sending Browser Links via Chat

Send a clickable text component that runs the `/browser` or `/browserexternal` command:

```java
// Example: Bukkit/Spigot
player.sendMessage(
    Component.text("[Open Dashboard]")
        .clickEvent(ClickEvent.runCommand("/browser https://yourserver.com/dashboard"))
        .color(NamedTextColor.AQUA)
);
```

### Background Launch (Client-Side)

For automatic browser launching without user interaction, the server can use a custom packet to trigger the browser client-side. This requires a companion server plugin (to be developed separately).

## Building from Source

### Prerequisites
- Java 17 or higher
- Gradle (included via wrapper)

### Build Steps

1. Clone the repository:
```bash
git clone https://github.com/yourusername/galaxianexus-browser.git
cd galaxianexus-browser
```

2. Build the mod:
```bash
./gradlew build
```

3. The compiled `.jar` file will be in `build/libs/`

### Development Setup

To set up a development environment:

```bash
# Generate IDE project files
./gradlew eclipse   # For Eclipse
./gradlew idea      # For IntelliJ IDEA

# Run Minecraft client with the mod
./gradlew runClient
```

## Technical Details

### Minecraft Version
- **Minecraft**: 1.20.4
- **Fabric Loader**: 0.15.3+
- **Fabric API**: 0.91.2+

### Dependencies
- Fabric API (required)
- Java 17+ (required)

### Architecture

The mod consists of several key components:

1. **GalaxiaNexusBrowserMod**: Main mod entry point, registers commands
2. **BrowserManager**: Handles UUID injection, player data, and browser operations
3. **BrowserScreen**: Custom Minecraft screen that displays the browser UI
4. **WebContentRenderer**: Renders web content within the game interface

### API Integration

The mod uses the [Crafatar API](https://crafatar.com/) for rendering player avatars:
- Avatar URL: `https://crafatar.com/avatars/{uuid}?size={size}&overlay`
- Supports overlay (second skin layer)
- Cached and CDN-backed for performance

## Configuration

Currently, the mod works out of the box with no configuration required. Future versions may include:
- Custom header injection
- Whitelist/blacklist for URLs
- Custom avatar sources
- Browser appearance customization

## Roadmap

- [x] **JavaScript execution support** - Full support via external mode
- [x] **Configuration system** - JSON-based config file
- [x] **Dual browser modes** - External (JS) and Embedded (basic HTML)
- [ ] Custom packet system for server-triggered browser launches
- [ ] Browser history and bookmarks
- [ ] Download support
- [ ] Form input handling improvements
- [ ] Multi-tab support
- [ ] Custom CSS injection for web pages
- [ ] Screenshot/capture functionality
- [ ] Browser DevTools integration

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Fabric](https://fabricmc.net/) - Mod loader and API
- [Crafatar](https://crafatar.com/) - Minecraft avatar rendering API
- Minecraft community for inspiration and support

## Support

If you encounter any issues or have questions:
- Open an [Issue](https://github.com/yourusername/galaxianexus-browser/issues)
- Join our Discord server (coming soon)
- Check the [Wiki](https://github.com/yourusername/galaxianexus-browser/wiki) for more documentation

---

**Note**: This mod is client-side only. Players need to install it on their client to use the browser features. Server integration is optional and enhances the experience but is not required for basic functionality.