<div align="center">

# 🏗️ Structures 

**Structures** is a flexible Minecraft plugin for creating and managing **custom structures** that can be **edited, copied, saved, and viewed** by players. It provides a robust interface for handling per-player structure displays, editing mechanics, and integrates with **PacketBlocks** for seamless client-side updates.

![Issues](https://img.shields.io/github/issues-raw/BitByLogics/Structures)
[![Stars](https://img.shields.io/github/stars/BitByLogics/Structures)](https://github.com/BitByLogics/Structures/stargazers)

<a href="#"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/compact/supported/spigot_46h.png" height="35"></a>
<a href="#"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/compact/supported/paper_46h.png" height="35"></a>
<a href="#"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/v3/assets/compact/available/modrinth_vector.svg" height="35"></a>

</div>

---

## 🔧 Features

- ✏️ **Edit structures** with per-player functionality
- ❄️ **Client-side visibility** powered by PacketBlocks
- 🏗️ Save and load structures directly to configuration
- 📦 **Copy, delete, and save** structure commands
- 📐 **Efficient structure management** with metadata-based blocks
- 🖥️ Debugging info in logs for structure operations
- 🌟 Animation and triggers for structure appear/hide events

---

## ✅ Requirements

- **Minecraft 1.21+** ([Paper](https://papermc.io/) server or compatible forks)
- **Java 21+**
- **Dependencies:**
    - [PacketEvents](https://modrinth.com/plugin/packetevents) 2.9.5+
    - [PacketBlocks](https://modrinth.com/plugin/packetblocks)

---

## 📥 Installation

1. Download the latest version from [Modrinth](https://modrinth.com/project/structures) or [GitHub](https://github.com/BitByLogics/Structures/releases).
2. Download the latest version of [PacketEvents](https://modrinth.com/plugin/packetevents)
3. Download the latest version of [PacketBlocks](https://modrinth.com/plugin/packetblocks)
2. Place all three `.jar` files in your server's `plugins/` folder.
3. Restart your server.

---

## ⚙️ Message Configuration

All plugin messages are configurable in the config.yml

<details>
<summary>View Default Message Configuration</summary>

```yaml
# Message Configuration #
Messages:
  Prefix: '#AB810E&lꜱ#B48A14&lᴛ#BE9219&lʀ#C79B1F&lᴜ#D0A424&lᴄ#DAAC2A&lᴛ#E3B52F&lᴜ#ECBE35&lʀ#F6C63A&lᴇ#FFCF40&lꜱ
    &8•'
  Configuration-Reloaded: '%prefix% &aꜱᴜᴄᴄᴇꜱꜱꜰᴜʟʟʏ ʀᴇʟᴏᴀᴅᴇᴅ ᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ ᴀɴᴅ ꜱᴛʀᴜᴄᴛᴜʀᴇꜱ.'
  Command-Help:
    - '%prefix% &aᴀᴠᴀɪʟᴀʙʟᴇ ᴄᴏᴍᴍᴀɴᴅꜱ'
    - '&2/ꜱᴛʀᴜᴄᴛᴜʀᴇ ʜᴇʟᴘ &8⁃ &7ᴠɪᴇᴡ ᴀᴠᴀɪʟᴀʙʟᴇ ᴄᴏᴍᴍᴀɴᴅꜱ'
    - '&2/ꜱᴛʀᴜᴄᴛᴜʀᴇ ʀᴇʟᴏᴀᴅ &8⁃ &7ʀᴇʟᴏᴀᴅ ᴛʜᴇ ᴄᴏɴꜰɪɢᴜʀᴀᴛɪᴏɴ ᴀɴᴅ ꜱᴛʀᴜᴄᴛᴜʀᴇꜱ'
    - '&2/ꜱᴛʀᴜᴄᴛᴜʀᴇ ɪɴꜰᴏ &8⁃ &7ᴠɪᴇᴡ ᴛʜᴇ ꜱᴛʀᴜᴄᴛᴜʀᴇ ɪᴅ ᴏꜰ ᴀ ʙʟᴏᴄᴋ'
    - '&2/ꜱᴛʀᴜᴄᴛᴜʀᴇ ᴇᴅɪᴛ <ɪᴅ> &8⁃ &7ᴇᴅɪᴛ ᴏʀ ᴄʀᴇᴀᴛᴇ ᴀ ꜱᴛʀᴜᴄᴛᴜʀᴇ'
    - '&2/ꜱᴛʀᴜᴄᴛᴜʀᴇ ᴅᴇʟᴇᴛᴇ <ɪᴅ> &8⁃ &7ᴅᴇʟᴇᴛᴇ ᴀ ꜱᴛʀᴜᴄᴛᴜʀᴇ'
    - '&2/ꜱᴛʀᴜᴄᴛᴜʀᴇ ᴠɪᴇᴡ <ɪᴅ> [ᴘʟᴀʏᴇʀ] &8⁃ &7ᴛᴏɢɢʟᴇ ᴠɪꜱɪʙɪʟɪᴛʏ ᴏꜰ ᴀ ꜱᴛʀᴜᴄᴛᴜʀᴇ'
    - '&2/ꜱᴛʀᴜᴄᴛᴜʀᴇ ᴄᴏᴘʏ <ɪᴅ> <ɴᴇᴡ ɪᴅ> &8⁃ &7ᴄᴏᴘɪᴇꜱ ᴀ ꜱᴛʀᴜᴄᴛᴜʀᴇ ᴛᴏ ᴛʜᴇ ʙʟᴏᴄᴋ ʏᴏᴜ''ʀᴇ
    ʟᴏᴏᴋɪɴɢ ᴀᴛ'
    - '&2/ꜱᴛʀᴜᴄᴛᴜʀᴇ ᴀɴɪᴍᴀᴛɪᴏɴ <ɪᴅ> <ꜱʜᴏᴡ/ʜɪᴅᴇ> <ᴀɴɪᴍᴀᴛɪᴏɴ ɪᴅ> &8⁃ &7ꜱᴇᴛ ᴀɴ ᴀɴɪᴍᴀᴛɪᴏɴ
    ꜰᴏʀ ᴛʜᴇ ꜱᴛʀᴜᴄᴛᴜʀᴇ'
  Info:
    Invalid-Location: '%prefix% &cʏᴏᴜ ᴍᴜꜱᴛ ʙᴇ ʟᴏᴏᴋɪɴɢ ᴀᴛ ᴀ ʙʟᴏᴄᴋ ᴛᴏ ᴠɪᴇᴡ ꜱᴛʀᴜᴄᴛᴜʀᴇ
      ɪɴꜰᴏ.'
    No-Structure: '%prefix% &cᴛʜᴀᴛ ʙʟᴏᴄᴋ ɪꜱɴ''ᴛ ᴘᴀʀᴛ ᴏꜰ ᴀ ꜱᴛʀᴜᴄᴛᴜʀᴇ.'
    Structure-Info: '%prefix% &aꜱᴛʀᴜᴄᴛᴜʀᴇ ɪᴅ&8: &2%id%'
  Editor:
    Missing-ID: '%prefix% &cᴘʟᴇᴀꜱᴇ ꜱᴘᴇᴄɪꜰʏ ᴀ ꜱᴛʀᴜᴄᴛᴜʀᴇ ɪᴅ.'
    Already-Editing:
      - '%prefix% &cʏᴏᴜ''ʀᴇ ᴀʟʀᴇᴀᴅʏ ᴇᴅɪᴛɪɴɢ ᴀɴᴏᴛʜᴇʀ ꜱᴛʀᴜᴄᴛᴜʀᴇ &8(&4%id%&8)&c.'
      - '%prefix% &cʀᴜɴ &8''&4/ꜱᴛʀᴜᴄᴛᴜʀᴇ ᴇᴅɪᴛ %id%&8'' &cᴛᴏ ꜱᴀᴠᴇ ɪᴛ ꜰɪʀꜱᴛ.'
    Started-Editing-New: '%prefix% &aꜱᴛᴀʀᴛᴇᴅ ᴇᴅɪᴛɪɴɢ&8: &8''&2%id%&8'' &a[ɴᴇᴡ]'
    Started-Editing: '%prefix% &aꜱᴛᴀʀᴛᴇᴅ ᴇᴅɪᴛɪɴɢ&8: &8''&2%id%&8'''
    Saved-Structure: '%prefix% &aꜱᴜᴄᴄᴇꜱꜱꜰᴜʟʟʏ ꜱᴀᴠᴇᴅ ꜱᴛʀᴜᴄᴛᴜʀᴇ &8''&2%id%&8'''
    Failed-To-Save: '%prefix% &cꜰᴀɪʟᴇᴅ ᴛᴏ ꜱᴀᴠᴇ ꜱᴛʀᴜᴄᴛᴜʀᴇ.'
    Deleted-Structure: '%prefix% &cꜱᴛʀᴜᴄᴛᴜʀᴇ &8''&4%id%&8'' &cʜᴀꜱ ʙᴇᴇɴ ᴅᴇʟᴇᴛᴇᴅ ᴀꜱ
      ɴᴏ ʙʟᴏᴄᴋꜱ ᴡᴇʀᴇ ᴅᴇꜰɪɴᴇᴅ.'
    Action-Bar:
      Block-Added: '&aᴀᴅᴅᴇᴅ ʙʟᴏᴄᴋ ᴀᴛ &8[&2%x%&8, &2%y%&8, &2%z%&8]'
      Block-Removed: '&cʀᴇᴍᴏᴠᴇᴅ ʙʟᴏᴄᴋ ᴀᴛ &8[&4%x%&8, &4%y%&8, &4%z%&8]'
  Copy:
    Invalid-Structure: '%prefix% &cᴀ ꜱᴛʀᴜᴄᴛᴜʀᴇ ᴡɪᴛʜ ᴛʜᴇ ɪᴅ &8''&4%id%&8'' &cᴅᴏᴇꜱ ɴᴏᴛ
      ᴇxɪꜱᴛ.'
    Duplicate-ID: '%prefix% &cᴀ ꜱᴛʀᴜᴄᴛᴜʀᴇ ᴡɪᴛʜ ᴛʜᴇ ɪᴅ &8''&4%id%&8'' &cᴀʟʀᴇᴀᴅʏ ᴇxɪꜱᴛꜱ.'
    Invalid-Location: '%prefix% &cʏᴏᴜ ᴍᴜꜱᴛ ʙᴇ ʟᴏᴏᴋɪɴɢ ᴀᴛ ᴀ ʙʟᴏᴄᴋ ᴛᴏ ᴄᴏᴘʏ ᴛᴏ.'
    Failed-To-Copy: '%prefix% &cꜰᴀɪʟᴇᴅ ᴛᴏ ᴄᴏᴘʏ ꜱᴛʀᴜᴄᴛᴜʀᴇ.'
    Successfully-Copied: '%prefix% &aꜱᴜᴄᴄᴇꜱꜱꜰᴜʟʟʏ ᴄᴏᴘɪᴇᴅ &2%sourceId% &8→ &2%newId%
      &aꜰᴀᴄɪɴɢ &2%direction%&a.'
  Delete:
    Invalid-ID: '%prefix% &cᴘʟᴇᴀꜱᴇ ꜱᴘᴇᴄɪꜰʏ ᴀ ᴠᴀʟɪᴅ ꜱᴛʀᴜᴄᴛᴜʀᴇ ɪᴅ.'
    Successfully-Deleted: '%prefix% &aꜱᴜᴄᴄᴇꜱꜱꜰᴜʟʟʏ ᴅᴇʟᴇᴛᴇᴅ ꜱᴛʀᴜᴄᴛᴜʀᴇ &8''&2%id%&8'''
    Failed-To-Delete: '%prefix% &cꜰᴀɪʟᴇᴅ ᴛᴏ ᴅᴇʟᴇᴛᴇ ꜱᴛʀᴜᴄᴛᴜʀᴇ &8''&4%id%&8'''
  View:
    Invalid-ID: '%prefix% &cᴘʟᴇᴀꜱᴇ ꜱᴘᴇᴄɪꜰʏ ᴀ ᴠᴀʟɪᴅ ꜱᴛʀᴜᴄᴛᴜʀᴇ ɪᴅ.'
    Viewing-Enabled: '%prefix% &aᴠɪᴇᴡɪɴɢ ꜱᴛʀᴜᴄᴛᴜʀᴇ &8''&2%id%&8'''
    Viewing-Disabled: '%prefix% &cɴᴏ ʟᴏɴɢᴇʀ ᴠɪᴇᴡɪɴɢ ꜱᴛʀᴜᴄᴛᴜʀᴇ &8''&4%id%&8'''
  Animation:
    Invalid-ID: '%prefix% &cᴘʟᴇᴀꜱᴇ ꜱᴘᴇᴄɪꜰʏ ᴀ ᴠᴀʟɪᴅ ꜱᴛʀᴜᴄᴛᴜʀᴇ ɪᴅ.'
    Invalid-Animation: '%prefix% &cᴘʟᴇᴀꜱᴇ ꜱᴘᴇᴄɪꜰʏ ᴀ ᴠᴀʟɪᴅ ᴀɴɪᴍᴀᴛɪᴏɴ ɪᴅ.'
    Show-Animation-Set: '%prefix% &aꜱᴜᴄᴇꜱꜱꜰᴜʟʟʏ ᴜᴘᴅᴀᴛᴇᴅ ꜱʜᴏᴡ ᴀɴɪᴍᴀᴛɪᴏɴ ᴛᴏ &8''&2%animationId%&8''
      &aꜰᴏʀ ꜱᴛʀᴜᴄᴛᴜʀᴇ &8''&2%id%&8'''
    Hide-Animation-Set: '%prefix% &aꜱᴜᴄᴇꜱꜱꜰᴜʟʟʏ ᴜᴘᴅᴀᴛᴇᴅ ʜɪᴅᴇ ᴀɴɪᴍᴀᴛɪᴏɴ ᴛᴏ &8''&2%animationId%&8''
      &aꜰᴏʀ ꜱᴛʀᴜᴄᴛᴜʀᴇ &8''&2%id%&8'''
```
</details>


---

## 📔 Commands

| Command                                                | Description                             | Permission          |
|--------------------------------------------------------|-----------------------------------------|---------------------|
| `/structure help`                                      | View available commands                 | `structures.admin`  |
| `/structure reload`                                    | Reload the configuration and structures | `structures.admin` |
| `/structure info`                                      | View the structure info of a block      | `structures.admin` |
| `/structure edit <id>`                                 | Start editing a structure               | `structures.admin`  |
| `/structure delete <id>`                               | Delete a structure                      | `structures.admin`  |
| `/structure view <id>`                                 | Toggle viewing for a structure          | `structures.view`   |
| `/structure copy <sourceId> <newId>`                   | Copy a structure                        | `structures.admin`  |
| `/structure animation <id> <show/hide> <animation id>` | Set an animation for the structure      | `structures.admin`  |

---

## 🧩 How It Works

1. **Editing:** Players with the permission `structures.admin` can edit structures using `/structure edit <id>`.
2. **Client-Side Updates:** Structures use PacketBlocks to ensure updates are only visible to specific players.
3. **Saving/Loading:** Structures persist in the configuration file and can be reloaded across server restarts.
4. **Copying:** Duplicate existing structures using the `/structure copy <id> <new id>` command.
5. **Displaying Structures:** Structures are visible by default, but can be hidden using the `/structure view <id> <player>` command.

---

## ⬜ PlaceholderAPI Support

If installed, the following PlaceholderAPI placeholders will be available:

- `%structures_viewing_<id>%` - Returns true/false depending on if the player is viewing the structure.

---

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. Fork the repository.
2. Make changes for new features/bug fixes.
3. Create a pull request with a detailed description of your updates.

---

## 📜 License

MIT License — see [LICENSE](./LICENSE).

---

## 👤 Author

Created by **BitByLogics**
Discord: `@BitByLogics`

Shout out to [Sulaxan](https://github.com/Sulaxan) for developing the original system for my old server!

---
