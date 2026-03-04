# Quick Start Guide

Get GalaxiaNexus Browser up and running in 5 minutes!

## For Players

### Installation

1. **Install Fabric Loader**
   - Download from [fabricmc.net](https://fabricmc.net/use/)
   - Run the installer and select your Minecraft version (1.20.4)

2. **Install Fabric API**
   - Download from [Modrinth](https://modrinth.com/mod/fabric-api) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
   - Place in your `mods` folder

3. **Install GalaxiaNexus Browser**
   - Download the latest release
   - Place the JAR file in your `mods` folder

4. **Launch Minecraft**
   - Select the Fabric profile
   - Start the game!

### Usage

#### Open Browser Command
```
/browser
```
Opens the in-game browser to the welcome page.

```
/browser https://example.com
```
Opens the specified URL in the in-game browser.

#### Open External Browser Command
```
/browserexternal https://example.com
```
Opens the URL in your system's default browser with your player UUID injected.

### Features

- 🌐 **In-Game Browser**: Browse web content without leaving Minecraft
- 🔐 **Automatic UUID Injection**: Your player UUID is automatically added to requests
- 👤 **Avatar Display**: See your Minecraft skin in the browser toolbar
- 🖱️ **Easy Navigation**: Enter URLs in the address bar, press Enter to go
- 🌍 **External Browser Support**: Open links in Chrome, Firefox, or your default browser

### Tips

- Use the **Arrow Keys** to navigate the URL field
- Press **Enter** to load a URL
- Click **External** to open in your system browser
- Click **Close** or press **ESC** to close the browser

## For Developers

### Building from Source

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/galaxianexus-browser.git
   cd galaxianexus-browser
   ```

2. **Build the mod**
   ```bash
   ./gradlew build
   ```
   
   On Windows:
   ```cmd
   gradlew.bat build
   ```

3. **Find the output**
   The compiled JAR will be in `build/libs/galaxianexus-browser-1.0.0.jar`

### Development Environment

1. **Generate sources**
   ```bash
   ./gradlew genSources
   ```

2. **Import into IDE**
   - **IntelliJ IDEA**: File → Open → Select `build.gradle`
   - **Eclipse**: Run `./gradlew eclipse`, then import project

3. **Run in development**
   ```bash
   ./gradlew runClient
   ```

### Making Changes

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed development guidelines.

## For Server Admins

### Sending Browser Links to Players

You can send clickable chat messages that open the browser:

**Bukkit/Spigot Example:**
```java
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

Component message = Component.text("[Open Dashboard]")
    .color(NamedTextColor.AQUA)
    .clickEvent(ClickEvent.runCommand("/browser https://yourserver.com/dashboard"));

player.sendMessage(message);
```

### Web Server Integration

Your web server will receive requests with these query parameters:
- `player_uuid`: The player's UUID
- `player_name`: The player's username

**Example URL:**
```
https://yourserver.com/stats?player_uuid=123e4567-e89b-12d3-a456-426614174000&player_name=Steve
```

See [SERVER_INTEGRATION.md](SERVER_INTEGRATION.md) for complete integration guide.

## Troubleshooting

### Browser won't open
- Make sure you have Fabric API installed
- Check that you're using Minecraft 1.20.4
- Try running `/browser` without a URL first

### URLs don't load
- Check your internet connection
- Try opening in external browser with `/browserexternal`
- Some websites may not render well in the in-game browser

### Commands not working
- Make sure the mod is installed correctly
- Check the mods list in Minecraft (Mod Menu recommended)
- Look for error messages in the Minecraft log

### Avatar not showing
- This is normal if you're offline or the Crafatar API is down
- Avatar loads asynchronously, give it a few seconds
- Check your internet connection

## Common Issues

**Q: Can I use this on servers?**
A: Yes! This is a client-side mod. Install it on your client and it works on any server.

**Q: Do other players need the mod?**
A: No, but they won't be able to use the browser features without it.

**Q: Does this work with Optifine?**
A: Optifine compatibility is not guaranteed. Use Sodium + Iris for better performance with Fabric.

**Q: Can I customize the browser appearance?**
A: Not yet, but this is planned for future versions!

## Next Steps

- Read the [full README](README.md) for detailed information
- Check out [SERVER_INTEGRATION.md](SERVER_INTEGRATION.md) for server integration
- Join our community (Discord coming soon!)
- Report bugs on [GitHub Issues](https://github.com/yourusername/galaxianexus-browser/issues)

## Support

Need help? 
- 📚 [Documentation](README.md)
- 🐛 [Report Issues](https://github.com/yourusername/galaxianexus-browser/issues)
- 💬 Discord (coming soon)

---

**Happy browsing!** 🚀
