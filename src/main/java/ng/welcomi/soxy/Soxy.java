/*
 * Copyright (c) 2026 wellcoming
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

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
