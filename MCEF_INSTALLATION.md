# MCEF Installation Guide

To enable the embedded browser with full JavaScript support, you need to install MCEF (Minecraft Chromium Embedded Framework) separately.

## What is MCEF?

MCEF is a library mod that embeds the Chromium browser engine into Minecraft, enabling full JavaScript, AJAX, and modern web support in-game.

## Installation Steps

### Option 1: Download Pre-built MCEF (Recommended)

1. **Download MCEF** for Minecraft 1.20.4:
   - Visit: https://github.com/montoyo/mcef/releases
   - Download the latest MCEF release for 1.20.x
   - Alternative: https://www.curseforge.com/minecraft/mc-mods/mcef (if available)

2. **Install MCEF**:
   - Place the MCEF `.jar` file in your `mods` folder
   - Make sure you have the correct version for Minecraft 1.20.4

3. **Launch Minecraft**:
   - MCEF will download the Chromium binaries on first launch
   - This may take a few minutes
   - Accept any firewall prompts if needed

4. **Test the Browser**:
   - Run `/browser https://example.com`
   - The embedded browser should open in-game

### Option 2: Build MCEF from Source

If pre-built MCEF is not available for 1.20.4:

1. **Clone MCEF Repository**:
   ```bash
   git clone https://github.com/montoyo/mcef.git
   cd mcef
   ```

2. **Build for 1.20.4**:
   ```bash
   ./gradlew build
   ```

3. **Install**:
   - Find the built JAR in `build/libs/`
   - Place it in your `mods` folder

### Option 3: Use External Browser Mode (No MCEF Needed)

If you can't install MCEF or prefer not to:

1. **Edit Config**:
   - Open `config/galaxianexus-browser.json`
   - Set `"mode": "EXTERNAL"`

2. **How it Works**:
   - URLs will open in your system browser (Chrome/Firefox/Edge)
   - JavaScript/AJAX still works perfectly
   - UUID is still automatically injected
   - Lower performance impact on Minecraft

## Troubleshooting

### MCEF Not Detected

**Symptom:** Browser opens in external mode even with MCEF installed

**Solution:**
- Check that MCEF is in the `mods` folder
- Verify MCEF version matches Minecraft version
- Check Minecraft logs for MCEF errors
- Restart Minecraft completely

### Chromium Binaries Not Downloading

**Symptom:** MCEF installed but browser doesn't work

**Solution:**
- Check internet connection
- Allow Minecraft through firewall
- Manually download Chromium binaries:
  - Windows: Place in `.minecraft/libraries/jcef/`
  - Linux: Place in `.minecraft/libraries/jcef/`
  - macOS: Place in `~/Library/Application Support/minecraft/libraries/jcef/`

### Black Screen in Browser

**Symptom:** Browser opens but shows black screen

**Solution:**
- Update graphics drivers
- Try disabling hardware acceleration in MCEF config
- Switch to external mode if issue persists

### Performance Issues

**Symptom:** Minecraft lags when browser is open

**Solution:**
- Allocate more RAM to Minecraft (recommended: 4GB+)
- Close the browser when not in use
- Switch to external mode: `"mode": "EXTERNAL"`
- Lower browser dimensions in config

## Configuration with MCEF

When MCEF is installed, these config options apply:

```json
{
  "mode": "EMBEDDED",           // Use embedded MCEF browser
  "enableJavaScript": true,     // Enable JavaScript in MCEF
  "enableCookies": true,        // Enable cookies
  "browserWidth": 1024,         // Browser render width
  "browserHeight": 768,         // Browser render height
  "defaultUrl": "about:blank"
}
```

## System Requirements for MCEF

### Minimum:
- **RAM:** 4GB allocated to Minecraft
- **Graphics:** OpenGL 3.0+ support
- **OS:** Windows 7+, Linux (most distros), macOS 10.10+

### Recommended:
- **RAM:** 6GB+ allocated to Minecraft
- **Graphics:** Dedicated GPU with OpenGL 4.0+
- **OS:** Windows 10+, Linux (Ubuntu 20.04+), macOS 11+

## Features Comparison

| Feature | With MCEF | Without MCEF (External) |
|---------|-----------|------------------------|
| JavaScript/AJAX | ✅ In-game | ✅ System browser |
| React/Vue/Angular | ✅ In-game | ✅ System browser |
| UUID Injection | ✅ Automatic | ✅ Automatic |
| Player Avatar | ✅ Displayed | ✅ Displayed |
| In-game Display | ✅ Yes | ❌ Opens externally |
| Performance Impact | ⚠️ Medium-High | ✅ Minimal |
| Setup Complexity | ⚠️ Requires MCEF | ✅ Works out of box |

## Support

For MCEF-specific issues:
- MCEF GitHub: https://github.com/montoyo/mcef/issues
- MCEF Discord: (check GitHub for invite)

For GalaxiaNexus Browser issues:
- GitHub Issues: https://github.com/yourusername/galaxianexus-browser/issues

## Alternative: Use External Mode

**Don't want to deal with MCEF installation?**

The mod works perfectly without MCEF by using external browser mode:

1. Your web apps still work with full JavaScript
2. UUID injection still automatic
3. No performance impact
4. Zero additional setup

Just set `"mode": "EXTERNAL"` in the config!

---

**Note:** MCEF is an optional enhancement. The mod works great without it using external browser mode.
