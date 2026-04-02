![soxy.png](src/main/resources/assets/soxy/icon.png)

# Soxy

Soxy is a server-side helper mod for Voxy.

It runs on a dedicated server and generates Voxy cache data directly from the server world, so the cache can be prepared on the server instead of requiring a client to import region files manually.

## Usage
```text
/soxy generate [dimension_id]
```

> [!WARNING]
> ARM servers are unsupported due to missing LWJGL native libraries; you must build the mod yourself if needed.