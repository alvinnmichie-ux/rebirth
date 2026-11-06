package rebirth;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

public class RebirthMod implements ModInitializer {
	public static final String MOD_ID = "rebirth";
	public static final int DEFAULT_TICKS = 120; // 6 detik

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playS2C().register(RebirthPayload.ID, RebirthPayload.CODEC);

		// Perintah:
		//   /rebirth                      -> animasi untuk diri sendiri
		//   /rebirth <pemain>             -> untuk pemain lain
		//   /rebirth <pemain> <ticks>     -> dengan durasi sendiri (20..1200 tick)
		//
		// TODO: tambahkan pembatasan izin (OP) sebelum dipakai di server publik.
		// Cara menulisnya berubah di 1.21.9+, jadi sengaja tidak saya tebak di sini.
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(CommandManager.literal("rebirth")
						.executes(ctx -> run(ctx.getSource(),
								List.of(ctx.getSource().getPlayerOrThrow()), DEFAULT_TICKS))
						.then(CommandManager.argument("targets", EntityArgumentType.players())
								.executes(ctx -> run(ctx.getSource(),
										EntityArgumentType.getPlayers(ctx, "targets"), DEFAULT_TICKS))
								.then(CommandManager.argument("ticks", IntegerArgumentType.integer(20, 1200))
										.executes(ctx -> run(ctx.getSource(),
												EntityArgumentType.getPlayers(ctx, "targets"),
												IntegerArgumentType.getInteger(ctx, "ticks")))))));
	}

	private static int run(ServerCommandSource source, Collection<ServerPlayerEntity> targets, int ticks) {
		for (ServerPlayerEntity player : targets) {
			ServerPlayNetworking.send(player, new RebirthPayload(ticks));
		}
		source.sendFeedback(() -> Text.literal("Rebirth dimainkan untuk " + targets.size() + " pemain"), false);
		return targets.size();
	}
}
