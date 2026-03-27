package ng.welcomi.soxy;

import me.cortex.voxy.commonImpl.VoxyCommon;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class Soxy implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            if (!VoxyCommon.isAvailable()) {
                VoxyCommon.setInstanceFactory(() -> new SoxyInstance(server));
                VoxyCommon.createInstance();
            }
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            if (VoxyCommon.getInstance() != null) {
                VoxyCommon.shutdownInstance();
            }
        });

        SoxyCommand.register();
    }
}
