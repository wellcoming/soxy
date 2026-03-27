package ng.welcomi.soxy;

import me.cortex.voxy.common.config.ConfigBuildCtx;
import me.cortex.voxy.common.config.section.SectionStorage;
import me.cortex.voxy.common.config.section.SectionStorageConfig;
import me.cortex.voxy.common.StorageConfigUtil;
import me.cortex.voxy.commonImpl.VoxyInstance;
import me.cortex.voxy.commonImpl.WorldIdentifier;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;

public class SoxyInstance extends VoxyInstance {
    private final Path basePath;
    private final Config config;
    
    private static class Config {
        public int version = 1;
        public boolean disabled = false;
        public SectionStorageConfig sectionStorageConfig;
    }

    private static final Config DEFAULT_STORAGE_CONFIG;
    static {
        var config = new Config();
        config.sectionStorageConfig = StorageConfigUtil.createDefaultSerializer();
        DEFAULT_STORAGE_CONFIG = config;
    }

    public SoxyInstance(MinecraftServer server) {
        super();
        this.basePath = server.getServerDirectory().resolve(".voxy").resolve("saves").resolve(server.getWorldData().getLevelName());
        this.config = StorageConfigUtil.getCreateStorageConfig(Config.class, c->c.version==1&&c.sectionStorageConfig!=null, ()->DEFAULT_STORAGE_CONFIG, this.basePath);
        this.updateDedicatedThreads();
    }

    @Override
    public void updateDedicatedThreads() {
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        int targetThreads = Math.max(1, availableProcessors - 2);
        this.setNumThreads(targetThreads);
    }

    @Override
    protected SectionStorage createStorage(WorldIdentifier identifier) {
        var ctx = new ConfigBuildCtx();
        ctx.setProperty(ConfigBuildCtx.BASE_SAVE_PATH, this.basePath.toString());
        ctx.setProperty(ConfigBuildCtx.WORLD_IDENTIFIER, identifier.getWorldId());
        ctx.setProperty(ConfigBuildCtx.PLAYER_UUID, "server");
        ctx.pushPath(ConfigBuildCtx.DEFAULT_STORAGE_PATH);
        return this.config.sectionStorageConfig.build(ctx);
    }
}
