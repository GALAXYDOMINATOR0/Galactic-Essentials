# 🌌 Galactic Essentials

**Galactic Essentials** is a lightweight, modern essentials plugin built for SMP servers. It includes core quality-of-life commands such as teleport requests, balance tracking, clickable messages, and full configuration support — all designed for simplicity, performance, and customization.

---

## ✨ Features

- 🧭 **TPA System**
  - Teleport request system with:
    - 5-second configurable delay before teleportation
    - Request expiration timer
    - Cooldown system
    - Cancel teleport on movement
    - Clickable `[Accept]` / `[Deny]` chat messages

- 💰 **Balance System**
  - Track player balances
  - Fully customizable start and max balance
  - `/pay <player> <amount>` to send money
  - MongoDB support for persistent storage

- 🖱 **Clickable Adventure Messages**
  - Modern MiniMessage format support
  - Hover and click actions in chat (accept/deny, info tooltips)

- ⚙️ **Fully Configurable**
  - All messages, delays, cooldowns, and behaviors editable in `config.yml`
  - MiniMessage-based formatting for rich, styled text

- 🗃 **MongoDB Integration **
  - Store persistent player data (balances, requests, etc.)
  - Designed to scale with larger SMPs


## 📁 Example `config.yml`

```yaml
tpa:
  cooldown-seconds: 60
  timeout-seconds: 30
  teleport-delay-seconds: 5
  messages:
    cooldown: "<red>Please wait before sending another TPA."
    request-sent: "<green>Sent TPA to <yellow>%target%</yellow>."
    request-received: "<yellow>%sender%</yellow> wants to teleport to you!"
    request-accepted: "<green>You accepted the request from %sender%."
    request-denied: "<red>You denied the request from %sender%."
    request-expired: "<gray>Request from %sender% expired."
    teleported: "<green>You have been teleported to <yellow>%target%</yellow>."

balance:
  starting-balance: 100
  max-balance: 1000000
  messages:
    balance-check: "<green>Your balance: <yellow>%balance%</yellow>"
    sent-money: "<green>You sent <yellow>%amount%</yellow> to <yellow>%target%</yellow>."
    received-money: "<green>You received <yellow>%amount%</yellow> from <yellow>%sender%</yellow>."
```

---

## 💻 Commands

| Command                | Description                             |
|------------------------|-----------------------------------------|
| `/tpa <player>`        | Send a teleport request to another player |
| `/tpaccept`            | Accept a pending TPA request            |
| `/tpdeny`              | Deny a pending TPA request              |
| `/balance` or `/bal`   | Check your balance                      |
| `/pay <player> <amt>`  | Send money to another player            |

---

## ✅ Requirements

- Minecraft 1.16+ (tested on 1.20.4+)
- Java 17+
- Spigot or Paper
- MongoDB (optional, for persistent data)

---

## 🚀 Setup

1. Download the latest release from the [Releases](https://github.com/yourname/Galactic-Essentials/releases) tab.
2. Place the `.jar` file in your server's `/plugins` directory.
3. Restart your server to generate configuration files.
4. Edit `config.yml` to customize behavior and messages.
5. Enjoy your new essentials!

---

## 🧠 Planned Features

- 📈 Top balances leaderboard
- 🔒 PvP toggle
- 📜 Server rules display command
- 📦 Kit system (custom kits via GUI or command)
- 🎁 Daily rewards & streaks
- 🔁 RTP (random teleport) system

---

## 🧑‍💻 Contributing

**Contributors**:

- [Galaxy_Dominator] – Project lead and maintainer

Open source and welcoming to contributors!  
Feel free to open an issue, suggest a feature, or submit a pull request.

---

## 📜 License

MIT License – Free to use, modify, and redistribute.  
See [`LICENSE`](LICENSE) for full terms.

---

## 🪐 Created with ❤️ by GalaxyDominator
