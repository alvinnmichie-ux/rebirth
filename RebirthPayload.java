package rebirth;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Paket server -> client: mulai animasi rebirth selama durationTicks tick. */
public record RebirthPayload(int durationTicks) implements CustomPayload {

	public static final CustomPayload.Id<RebirthPayload> ID =
			new CustomPayload.Id<>(Identifier.of(RebirthMod.MOD_ID, "play"));

	public static final PacketCodec<RegistryByteBuf, RebirthPayload> CODEC =
			PacketCodec.tuple(PacketCodecs.VAR_INT, RebirthPayload::durationTicks, RebirthPayload::new);

	@Override
	public CustomPayload.Id<? extends CustomPayload> getId() {
		return ID;
	}
}
