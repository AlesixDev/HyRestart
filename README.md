# 🔄 HyRestart - Automatic Server Restart Plugin

A comprehensive and configurable automatic restart plugin for Hytale servers with Discord integration and customizable warning messages.

---

## 📖 Why?
Server stability is crucial for multiplayer games. Regular restarts help maintain performance, clear memory leaks, and apply updates. HyRestart automates this process with:
- Scheduled restarts at configurable times
- Progressive player warnings before restarts
- Discord webhook notifications
- Fully customizable messages

Whether you're running a small community server or a large Hytale network, HyRestart ensures smooth, predictable server maintenance without surprising your players!

---

## ✨ Features

### 🕐 **Scheduled Restarts**
- Configure multiple daily restart times (24-hour format)
- Configurable restart threshold and delays

### ⚠️ **Player Warnings**
- Customizable warning intervals (30min, 15min, 5min, 1min by default)
- Broadcast messages to all online players
- Configurable warning messages with placeholders

### 🔔 **Discord Integration**
- Rich embed notifications via webhooks
- Customizable embed titles, descriptions, and colors
- Separate messages for warnings and final restart
- Time placeholder support

### 🔧 **Fully Configurable**
- All messages configurable in `config.yml`
- System log messages customizable
- Warning intervals and messages
- Discord webhook settings
- Easy to translate to any language

---

## 📥 Installation

1️⃣ **Download the plugin**
```bash
# Download HyRestart-1.0-SNAPSHOT.jar from releases
```

2️⃣ **Install on your server**
```bash
# Place the JAR in your mods folder
cp HyRestart-1.0-SNAPSHOT.jar /path/to/hytale/mods/
```

3️⃣ **Start your server**
The plugin will automatically create a default `config.yml` in `mods/HyRestart/`

4️⃣ **Configure your settings**
Edit `mods/HyRestart/config.yml` to customize restart times, messages, and Discord integration

---

## ⚙️ Configuration

### **Basic Configuration**
```yaml
# Restart times (24h format: HH:mm)
restartTimes:
  - "03:00"
  - "09:00"
  - "15:00"
  - "21:00"

# Cooldown after a restart to prevent immediate re-restart loops (seconds)
restartCooldownSeconds: 60

# Final restart message
finalRestartMessage: "[Restart] Restarting server NOW!"
```

### **Warning Configuration**
```yaml
# Restart warnings
warnings:
  - seconds: 1800  # 30 minutes
    message: "[Restart] The server will restart in 30 minutes."
    discordTime: "30 minutes"
  - seconds: 900   # 15 minutes
    message: "[Restart] The server will restart in 15 minutes."
    discordTime: "15 minutes"
  - seconds: 300   # 5 minutes
    message: "[Restart] The server will restart in 5 minutes. Get ready!"
    discordTime: "5 minutes"
  - seconds: 60    # 1 minute
    message: "[Restart] The server will restart in 1 minute. DISCONNECT NOW!"
    discordTime: "1 minute"
```

### **Discord Integration**
```yaml
discord:
  enabled: true
  webhookUrl: "https://discord.com/api/webhooks/YOUR_WEBHOOK_URL"
  embedTitle: "🔄 Server Restart"
  embedDescription: "The server will restart in **{time}**.\n\nPlease save your progress and disconnect."
  embedColor: 16711680  # Red color in decimal
  finalEmbedTitle: "⚠️ SERVER RESTARTING"
  finalEmbedDescription: "The server is restarting NOW.\n\nThe server will be back in a few minutes."
```

---

## 🎯 Placeholder System

HyRestart supports placeholders in messages for dynamic content:

- `{time}` - Restart time or time remaining
- `{count}` - Number of players
- `{message}` - Message content
- `{error}` - Error details
- `{username}` - Player username
- `{code}` - Response code
- `{discordTime}` - Human-readable time for Discord

**Example:**
```yaml
broadcastingToPlayers: "[HyRestart] Broadcasting to {count} players: {message}"
```

---

## 🚀 Building from Source

1️⃣ **Clone the repository**
```bash
git clone https://github.com/alesixdev/HyRestart.git
cd HyRestart
```

2️⃣ **Add libraries**
```bash
Add HytaleServer.jar on libraries
```

3️⃣ **Build with Gradle**
```bash
./gradlew clean build
```
4️⃣ **Ready!**
```bash
# The built plugin will be at:
build/libs/HyRestart-1.0-SNAPSHOT.jar
```

---

## 🔧 Development

### **Project Structure:**
```
HyRestart/
├── src/main/
│   ├── java/dev/alesixdev/hyrestart/
│   │   ├── HyRestartPlugin.java          # Main plugin entry
│   │   ├── config/
│   │   │   ├── ConfigManager.java        # Config loading/saving
│   │   │   ├── ConfigData.java           # Config data model
│   │   │   └── WarningConfig.java        # Warning data model
│   │   ├── scheduler/
│   │   │   └── RestartScheduler.java     # Restart logic
│   │   └── utils/
│   │       └── DiscordWebhook.java       # Discord integration
│   └── resources/
│       ├── config.yml                     # Default configuration
│       └── manifest.json                  # Plugin manifest
├── build.gradle                           # Build configuration
└── README.md
```
---

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/alesixdev/HyRestart/issues)

---

## 📜 License

Licensed under the **MIT License**. Use, modify, and redistribute freely.

---
