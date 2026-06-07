# Ancient Warfare 2 - Core Module Migration Index (1.12.2 -> 1.21.1)

Due to the sheer size of the `core` module and the massive changes between Minecraft 1.12.2 and 1.21.1 (and Forge to NeoForge), the migration process must be broken down into manageable phases. This index tracks the progress of migrating each subsystem.

## Strategy

The strategy is to migrate packages with the fewest dependencies first, gradually moving up the dependency chain.

### Phase 1: Foundation (No/Minimal internal dependencies)
These packages form the base of the mod and rely mostly on standard Java or core Minecraft APIs.
- [ ] `config` - Configuration definitions.
- [ ] `owner` - Ownership system logic.
- [ ] `upgrade` - Upgrade interfaces.
- [ ] `entity` - Basic entity registry classes.
- [ ] `datafixes` - Data fixers.
- [ ] `gamedata` - World data saving/loading.

### Phase 2: Utilities and Interfaces
These packages rely on Phase 1 and are heavily used by the rest of the mod.
- [ ] `util` - General utility methods (BlockTools, ItemTools, NBTHelper, etc.).
- [ ] `interfaces` - Core mod interfaces.

### Phase 3: Registration and Research Framework
Core registry systems and the research progression logic.
- [ ] `registry` - Central registry loaders.
- [ ] `research` - Research trees and progression logic.
- [ ] `manual` - In-game manual content registry.

### Phase 4: Items, Inventory, and Crafting
The core functional items, custom inventories, and custom recipe systems.
- [ ] `inventory` - Custom item handlers and slot implementations.
- [ ] `crafting` - Research crafting and custom recipe wrappers.
- [ ] `item` - Core mod items.

### Phase 5: Blocks and Tiles
Physical representation in the world.
- [ ] `block` - Core mod blocks.
- [ ] `tile` - BlockEntities (TileEntities) containing logic.

### Phase 6: Networking and Commands
Server/Client communication and server commands.
- [ ] `network` - Network packet definitions and handlers.
- [ ] `command` - Server commands.
- [ ] `input` - Client keybind handling.

### Phase 7: GUI and Containers
The user interface, which relies on almost every other system (Tiles, Items, Network, Crafting).
- [ ] `container` - Server-side container logic.
- [ ] `gui` - Client-side screens and widgets.

### Phase 8: Rendering and Compat
The final layer.
- [ ] `render` - Custom block and item renderers.
- [ ] `compat` - Compatibility with other mods (e.g., JEI).

### Phase 9: Core Hookup
Connecting all the migrated pieces together.
- [ ] `proxy` - Client/Server proxy setup.
- [ ] `init` - Final initialization hooks.
- [ ] `AncientWarfareCore.java` - The main mod file for the core module.
