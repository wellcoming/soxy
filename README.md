![soxy.png](src/main/resources/assets/soxy/icon.png)

# Soxy

Soxy is a simple server-side helper mod for Voxy that generates `.voxy` cache data directly from the server world.

## Why Soxy?
Currently, many server-side voxy LoD distribution mods suffer from various bugs, and some are just "AI Slop". Other alternatives either consume excessive network bandwidth or fail to properly update modified chunks. Soxy solves this by providing a straightforward way to prepare the cache on the server, saving clients from having to import region files manually.

## Features
* **No Server-side Sodium Required:** Soxy removes Voxy's dependency on Sodium for the dedicated server. You only need to install Soxy and Voxy on your server.
* **Easy Distribution:** The generated `.voxy` files can be efficiently distributed to players via a file server or CDN, or automatically synchronized using mods like `automodpack`. 
* **Recommended Workflow:** Periodically run `/soxy generate` via a scheduled task and use `automodpack` to automatically load the generated cache for players.

## Installation & Usage
1. Install both **Soxy** and **Voxy** on your dedicated server.
2. Run the following command:
```text
/soxy generate [dimension_id]
```
3. The `.voxy` cache folder will be generated in your server's instance directory.

> [!WARNING]
> ARM servers are unsupported due to missing LWJGL native libraries; you must build the mod yourself if needed.