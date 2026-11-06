package rebirth.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.util.Identifier;
import rebirth.RebirthMod;
import rebirth.RebirthPayload;

public class RebirthClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(RebirthPayload.ID,
				(payload, context) -> RebirthAnimation.start(payload.durationTicks()));

		ClientTickEvents.END_CLIENT_TICK.register(RebirthAnimation::tick);

		HudElementRegistry.addLast(Identifier.of(RebirthMod.MOD_ID, "overlay"), RebirthAnimation::render);
	}
}
