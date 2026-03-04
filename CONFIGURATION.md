# Configuration Guide

GalaxiaNexus Browser can be configured via a JSON configuration file located at `config/galaxianexus-browser.json`.

## Configuration File

The configuration file is automatically created on first launch with default values. You can edit it to customize the browser behavior.

### Location
```
.minecraft/config/galaxianexus-browser.json
```

### Default Configuration

```json
{
  "mode": "EMBEDDED",
  "enableJavaScript": true,
  "enableCookies": true,
  "browserWidth": 1024,
  "browserHeight": 768,
  "defaultUrl": "about:blank"
}
```

## Configuration Options

### `mode`
**Type:** `String` (EMBEDDED | EXTERNAL)  
**Default:** `EMBEDDED`

Controls how the browser opens URLs:
- `EMBEDDED`: Opens URLs in the in-game browser interface
- `EXTERNAL`: Opens URLs in your system's default browser

**Why change this?**
- Use `EMBEDDED` for seamless in-game web browsing
- Use `EXTERNAL` if you experience performance issues or need full modern web support

**Example:**
```json
{
  "mode": "EXTERNAL"
}
```

### `enableJavaScript`
**Type:** `Boolean`  
**Default:** `true`

Enables or disables JavaScript execution in the embedded browser.

**Note:** Full JavaScript support requires additional setup (see JavaScript Support section below).

**Example:**
```json
{
  "enableJavaScript": true
}
```

### `enableCookies`
**Type:** `Boolean`  
**Default:** `true`

Enables or disables cookie storage in the embedded browser.

**Example:**
```json
{
  "enableCookies": false
}
```

### `browserWidth`
**Type:** `Integer`  
**Default:** `1024`

Sets the internal rendering width of the browser (in pixels).

**Example:**
```json
{
  "browserWidth": 1280
}
```

### `browserHeight`
**Type:** `Integer`  
**Default:** `768`

Sets the internal rendering height of the browser (in pixels).

**Example:**
```json
{
  "browserHeight": 720
}
```

### `defaultUrl`
**Type:** `String`  
**Default:** `"about:blank"`

The default URL to open when running `/browser` without arguments.

**Example:**
```json
{
  "defaultUrl": "https://yourserver.com/dashboard"
}
```

## JavaScript and AJAX Support

### Current Implementation

The embedded browser includes JavaFX WebView which provides:
- ✅ Full JavaScript (ES5+) support
- ✅ AJAX/Fetch API support
- ✅ DOM manipulation
- ✅ Modern web standards (WebKit engine)
- ✅ CSS3 and HTML5 support

### Limitations

While JavaFX WebView provides excellent JavaScript support, there are some considerations:
- **Performance:** May impact frame rates on lower-end systems
- **Memory:** Uses additional RAM for web rendering
- **Compatibility:** Some cutting-edge web features may not be supported

### Recommended Setup for Full Web Apps

For the best experience with modern web applications:

1. **Use Embedded Mode (Default)**
   ```json
   {
     "mode": "EMBEDDED",
     "enableJavaScript": true
   }
   ```
   - Supports React, Vue, Angular, and other modern frameworks
   - AJAX/Fetch requests work seamlessly
   - UUID injection is automatic

2. **Use External Mode (Fallback)**
   ```json
   {
     "mode": "EXTERNAL"
   }
   ```
   - If performance is an issue
   - For maximum compatibility
   - Full browser features available
   - UUID is still injected in the URL

## Performance Tuning

### Low-End Systems

If you experience lag or low frame rates:

```json
{
  "mode": "EXTERNAL",
  "enableJavaScript": false
}
```

### High-End Systems

For maximum capability:

```json
{
  "mode": "EMBEDDED",
  "enableJavaScript": true,
  "enableCookies": true,
  "browserWidth": 1920,
  "browserHeight": 1080
}
```

## Example Configurations

### Server Dashboard Setup
```json
{
  "mode": "EMBEDDED",
  "enableJavaScript": true,
  "enableCookies": true,
  "browserWidth": 1280,
  "browserHeight": 720,
  "defaultUrl": "https://myserver.com/dashboard"
}
```

### External Browser Only
```json
{
  "mode": "EXTERNAL",
  "enableJavaScript": false,
  "enableCookies": false,
  "defaultUrl": "https://myserver.com"
}
```

### Minimal Performance Impact
```json
{
  "mode": "EXTERNAL",
  "enableJavaScript": false,
  "enableCookies": false,
  "browserWidth": 800,
  "browserHeight": 600
}
```

## Troubleshooting

### JavaScript Not Working

**Problem:** JavaScript code doesn't execute in embedded browser.

**Solutions:**
1. Check that `enableJavaScript` is set to `true` in config
2. Ensure you're using `EMBEDDED` mode
3. Check the Minecraft log for JavaScript errors
4. Try the URL in external mode to verify it's a browser issue

### Performance Issues

**Problem:** Game lags when using embedded browser.

**Solutions:**
1. Switch to `EXTERNAL` mode
2. Reduce `browserWidth` and `browserHeight`
3. Close the browser when not in use
4. Allocate more RAM to Minecraft (recommended: 4GB+)

### Configuration Not Loading

**Problem:** Changes to config file don't take effect.

**Solutions:**
1. Restart Minecraft completely
2. Check that the JSON syntax is valid
3. Delete the config file and let it regenerate
4. Check Minecraft logs for config errors

## Advanced: Server-Side Configuration

Server administrators can provide a recommended configuration URL:

```java
// Server sends config suggestion to client
player.sendMessage(
    Component.text("[Download Recommended Browser Config]")
        .clickEvent(ClickEvent.openUrl("https://yourserver.com/browser-config.json"))
);
```

Players can then download and place this file in their config directory.

## Support

For issues with configuration:
- Check the [README](README.md) for general usage
- See [GitHub Issues](https://github.com/yourusername/galaxianexus-browser/issues)
- Join our Discord (coming soon)

---

**Tip:** After changing configuration, always restart Minecraft for changes to take effect.
