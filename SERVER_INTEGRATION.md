# Server Integration Guide

This guide explains how to integrate GalaxiaNexus Browser with your Minecraft server plugins.

## Overview

GalaxiaNexus Browser is a client-side mod that allows players to browse web content with their UUID automatically injected. Server plugins can trigger the browser to open specific URLs through chat messages or custom packets.

## Method 1: Chat-Based Links (Simple)

The easiest way to open the browser is by sending clickable chat messages that run commands.

### Bukkit/Spigot/Paper Example

```java
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class BrowserIntegration {
    
    /**
     * Opens a URL in the player's in-game browser
     */
    public static void openBrowser(Player player, String url) {
        Component message = Component.text("[Open Browser]")
            .color(NamedTextColor.AQUA)
            .clickEvent(ClickEvent.runCommand("/browser " + url));
        
        player.sendMessage(message);
    }
    
    /**
     * Opens a URL in the player's external browser
     */
    public static void openExternalBrowser(Player player, String url) {
        Component message = Component.text("[Open in External Browser]")
            .color(NamedTextColor.GREEN)
            .clickEvent(ClickEvent.runCommand("/browserexternal " + url));
        
        player.sendMessage(message);
    }
    
    /**
     * Example: Send a player to their stats page
     */
    public static void openPlayerStats(Player player) {
        String statsUrl = "https://yourserver.com/stats";
        // The mod will automatically append ?player_uuid=<uuid>&player_name=<name>
        
        Component message = Component.text()
            .append(Component.text("View your stats: ").color(NamedTextColor.GRAY))
            .append(Component.text("[Click Here]")
                .color(NamedTextColor.AQUA)
                .clickEvent(ClickEvent.runCommand("/browser " + statsUrl)))
            .build();
        
        player.sendMessage(message);
    }
}
```

### Fabric Server Example

```java
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BrowserIntegration {
    
    public static void openBrowser(ServerPlayerEntity player, String url) {
        Text message = Text.literal("[Open Browser]")
            .formatted(Formatting.AQUA)
            .styled(style -> style
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/browser " + url))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                    Text.literal("Click to open " + url)))
            );
        
        player.sendMessage(message, false);
    }
}
```

### Velocity Proxy Example

```java
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class BrowserIntegration {
    
    public static void openBrowser(Player player, String url) {
        Component message = Component.text("[Open Browser]")
            .color(NamedTextColor.AQUA)
            .clickEvent(ClickEvent.runCommand("/browser " + url));
        
        player.sendMessage(message);
    }
}
```

## Method 2: Custom Packets (Advanced)

For background browser launching without requiring player interaction, you can implement a custom packet system.

### Client-Side Handler (Fabric Mod Extension)

Add this to the GalaxiaNexus Browser mod or create a separate mod:

```java
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class BrowserPacketHandler {
    public static final Identifier OPEN_BROWSER_PACKET = 
        new Identifier("galaxianexus", "open_browser");
    
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(OPEN_BROWSER_PACKET, 
            (client, handler, buf, responseSender) -> {
                String url = buf.readString();
                boolean external = buf.readBoolean();
                
                client.execute(() -> {
                    if (external) {
                        GalaxiaNexusBrowserMod.getInstance()
                            .getBrowserManager()
                            .openExternalBrowser(url);
                    } else {
                        BrowserScreen.open(url);
                    }
                });
            });
    }
}
```

### Server-Side Sender (Bukkit/Spigot)

```java
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class BrowserPacketSender {
    
    public static void sendOpenBrowserPacket(Player player, String url, boolean external) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(url);
        out.writeBoolean(external);
        
        player.sendPluginMessage(yourPlugin, "galaxianexus:open_browser", out.toByteArray());
    }
    
    // Register the channel in your plugin's onEnable():
    // getServer().getMessenger().registerOutgoingPluginChannel(this, "galaxianexus:open_browser");
}
```

### Server-Side Sender (Fabric Server)

```java
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import io.netty.buffer.Unpooled;

public class BrowserPacketSender {
    public static final Identifier OPEN_BROWSER_PACKET = 
        new Identifier("galaxianexus", "open_browser");
    
    public static void sendOpenBrowserPacket(ServerPlayerEntity player, String url, boolean external) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(url);
        buf.writeBoolean(external);
        
        ServerPlayNetworking.send(player, OPEN_BROWSER_PACKET, buf);
    }
}
```

## UUID Injection Details

The browser mod automatically injects player data into URLs:

### Query Parameters

The mod appends the following query parameters:
- `player_uuid`: Player's UUID (e.g., `123e4567-e89b-12d3-a456-426614174000`)
- `player_name`: Player's username (e.g., `Steve`)

### Example

```
Original URL:  https://example.com/dashboard
Injected URL:  https://example.com/dashboard?player_uuid=123e4567-e89b-12d3-a456-426614174000&player_name=Steve

Original URL:  https://example.com/page?foo=bar
Injected URL:  https://example.com/page?foo=bar&player_uuid=123e4567-e89b-12d3-a456-426614174000&player_name=Steve
```

### Server-Side Validation

Your web server should validate the UUID to ensure it matches the claimed username:

```javascript
// Node.js/Express example
app.get('/dashboard', async (req, res) => {
    const playerUuid = req.query.player_uuid;
    const playerName = req.query.player_name;
    
    // Verify UUID matches username via Mojang API
    const mojangData = await fetch(`https://sessionserver.mojang.com/session/minecraft/profile/${playerUuid}`);
    const profile = await mojangData.json();
    
    if (profile.name !== playerName) {
        return res.status(403).send('Invalid credentials');
    }
    
    // Player is verified, show their dashboard
    res.render('dashboard', { player: profile });
});
```

```python
# Python/Flask example
from flask import Flask, request, abort
import requests

@app.route('/dashboard')
def dashboard():
    player_uuid = request.args.get('player_uuid')
    player_name = request.args.get('player_name')
    
    # Verify with Mojang API
    response = requests.get(f'https://sessionserver.mojang.com/session/minecraft/profile/{player_uuid}')
    profile = response.json()
    
    if profile.get('name') != player_name:
        abort(403, 'Invalid credentials')
    
    return render_template('dashboard.html', player=profile)
```

## Use Cases

### Player Stats Dashboard
```java
openBrowser(player, "https://yourserver.com/stats");
```

### Shop/Store Integration
```java
openBrowser(player, "https://yourserver.com/shop");
```

### Vote Rewards
```java
openBrowser(player, "https://yourserver.com/vote");
```

### Rules & Information
```java
openBrowser(player, "https://yourserver.com/rules");
```

### Dynamic Content
```java
// Example: Event details
String eventId = "summer-2024";
openBrowser(player, "https://yourserver.com/events/" + eventId);
```

## Avatar Integration

The browser displays the player's Minecraft skin avatar using the Crafatar API:

```
Avatar URL: https://crafatar.com/avatars/{uuid}?size=32&overlay
```

You can use the same API in your web application:

```html
<!-- Show player avatar in your web UI -->
<img src="https://crafatar.com/avatars/{{ player_uuid }}?size=64&overlay" 
     alt="{{ player_name }}" />
```

## Security Considerations

1. **Always validate UUIDs** on your server - don't trust client input
2. **Use HTTPS** for all URLs to protect player data
3. **Rate limit** API endpoints to prevent abuse
4. **Don't expose sensitive data** without additional authentication
5. **Consider session tokens** for enhanced security (implement custom header injection)

## Testing

1. Start your server with the integration code
2. Join with a client that has GalaxiaNexus Browser installed
3. Trigger the browser open command/packet
4. Verify the URL includes UUID and username parameters
5. Check your server logs for incoming requests

## Support

For issues or questions:
- [GitHub Issues](https://github.com/yourusername/galaxianexus-browser/issues)
- [Discord Community](#) (coming soon)

## Examples

Complete example plugins are available in the `examples/` directory:
- `examples/bukkit-integration/` - Full Bukkit/Spigot integration
- `examples/fabric-server-integration/` - Fabric server-side mod
- `examples/velocity-integration/` - Velocity proxy integration
