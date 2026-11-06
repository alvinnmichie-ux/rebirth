package rebirth.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

/**
 * Animasi rebirth di sisi client:
 *  1. layar hitam, lalu kelopak mata membuka lewat celah horizontal (ada satu "kedip")
 *  2. flash putih yang memudar + kabut putih tipis (perkiraan efek blur)
 *  3. kamera dipaksa orang ketiga selama animasi, lalu dikembalikan
 *
 * Waktu dihitung dari jam dinding (bukan tick) supaya mulus walau FPS naik turun.
 */
public final class RebirthAnimation {
	private static long startMs = -1;
	private static long durationMs;
	private static Perspective previousPerspective;

	// Keyframe {progress, nilai}
	private static final double[][] EYELID = {
			{0.00, 0.00}, {0.10, 0.00}, {0.22, 0.28}, {0.30, 0.28},
			{0.36, 0.06}, {0.42, 0.06}, {0.70, 1.00}, {1.00, 1.00}
	};
	private static final double[][] FLASH = {
			{0.00, 0.00}, {0.38, 0.00}, {0.50, 0.90}, {0.75, 0.35}, {1.00, 0.00}
	};

	private RebirthAnimation() {}

	public static boolean active() {
		return startMs >= 0;
	}

	public static void start(int ticks) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (!active()) {
			// simpan sudut kamera asli hanya sekali, supaya paket ganda tidak merusaknya
			previousPerspective = mc.options.getPerspective();
		}
		startMs = System.currentTimeMillis();
		durationMs = ticks * 50L;
		mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
		mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, 1.0f));
	}

	public static void tick(MinecraftClient mc) {
		if (!active()) return;
		if (System.currentTimeMillis() - startMs >= durationMs) {
			startMs = -1;
			if (previousPerspective != null) {
				mc.options.setPerspective(previousPerspective);
			}
		}
	}

	public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
		if (!active()) return;

		double p = Math.min(1.0, (System.currentTimeMillis() - startMs) / (double) durationMs);
		int w = ctx.getScaledWindowWidth();
		int h = ctx.getScaledWindowHeight();

		// 1) kabut/flash putih
		double haze = Math.pow(1.0 - p, 2) * 0.35;
		double alpha = Math.max(haze, sample(FLASH, p));
		int a = (int) (Math.min(1.0, alpha) * 255);
		if (a > 0) {
			ctx.fill(0, 0, w, h, (a << 24) | 0xFFFFFF);
		}

		// 2) kelopak mata
		double open = sample(EYELID, p);
		if (open < 1.0) {
			int gap = (int) (open * h);
			int topEnd = h / 2 - gap / 2;
			int bottomStart = h / 2 + gap / 2;
			int feather = (int) (h * 0.07);

			ctx.fill(0, 0, w, topEnd, 0xFF000000);
			ctx.fill(0, bottomStart, w, h, 0xFF000000);
			if (open > 0.0) {
				// tepi lembut seperti di videomu
				ctx.fillGradient(0, topEnd, w, topEnd + feather, 0xFF000000, 0x00000000);
				ctx.fillGradient(0, bottomStart - feather, w, bottomStart, 0x00000000, 0xFF000000);
			}
		}
	}

	/** Interpolasi smoothstep antar keyframe. */
	private static double sample(double[][] keys, double p) {
		for (int i = 1; i < keys.length; i++) {
			if (p <= keys[i][0]) {
				double t = (p - keys[i - 1][0]) / (keys[i][0] - keys[i - 1][0]);
				t = t * t * (3 - 2 * t);
				return keys[i - 1][1] + (keys[i][1] - keys[i - 1][1]) * t;
			}
		}
		return keys[keys.length - 1][1];
	}
}
