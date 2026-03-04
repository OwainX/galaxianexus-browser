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
  "mode": "EXTERNAL",
  "enableJavaScript": true,
  "enableCookies": false,
  "browserWidth": 1024,
  "browserHeight": 768,
  "defaultUrl": "about:blank"
}
```

## Configuration Options

### `mode`
**Type:** `String` (EMBEDDED | EXTERNAL)  
**Default:** `EXTERNAL`

Controls how the browser opens URLs:
- `EXTERNAL`: Opens URLs in your system's default browser (Full JavaScript/AJAX support)
- `EMBEDDED`: Opens URLs in the in-game browser interface (Basic HTML only)

**Why change this?**
- Use `EXTERNAL` (default) for full modern web support with JavaScript/AJAX
- Use `EMBEDDED` only for simple HTML viewing without JavaScript

**Example:**
```json
{
  "mode": "EMBEDDED"
}
```

### `enableJavaScript`
**Type:** `Boolean`  
**Default:** `true`

Controls JavaScript support preference. 

**Note:** Full JavaScript/AJAX support is only available in EXTERNAL mode (system browser). Embedded mode does not support JavaScript due to Minecraft's rendering limitations.

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

### How JavaScript Works

**JavaScript and AJAX support is provided via EXTERNAL mode:**

- ✅ **EXTERNAL Mode (Default):** Full JavaScript/AJAX support via your system browser
  - ES6+ JavaScript execution
  - AJAX and Fetch API
  - React, Vue, Angular, and all modern frameworks
  - Full browser features and DevTools
  - UUID automatically injected in URL

- ⚠️ **EMBEDDED Mode:** Basic HTML only, no JavaScript
  - Simple HTML rendering
  - No JavaScript execution
  - No AJAX/dynamic content
  - Best for static pages only

### Recommended Setup for Web Apps

**For Custom Web Apps with JavaScript/AJAX (Recommended):**
```json
{
  "mode": "EXTERNAL",
  "enableJavaScript": true
}
```
- Your React/Vue/Angular apps work perfectly
- AJAX requests function normally
- UUID is injected automatically
- Full debugging tools available

**For Simple Static Pages Only:**
```json
{
  "mode": "EMBEDDED"
}
```
- In-game viewing
- No JavaScript support
- Basic HTML rendering
- UUID still injected

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
