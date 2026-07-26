package com.makeforge.forgehud;

/** One-click module sets. Applying a preset also re-arranges the HUD for the current screen. */
public class HudPresets {

	public enum Preset {
		CLEAN("Clean", "Coords and compass only"),
		BALANCED("Balanced", "The everyday setup"),
		PVP("PvP", "Combat, gear and latency"),
		DEV("Debug", "Every performance readout"),
		FULL("Everything", "All modules on");

		public final String title;
		public final String description;

		Preset(String title, String description) {
			this.title = title;
			this.description = description;
		}
	}

	private static final HudModule[] CLEAN_SET = {
			HudModule.INFO, HudModule.COMPASS
	};

	private static final HudModule[] BALANCED_SET = {
			HudModule.INFO, HudModule.LIGHT, HudModule.DEATH, HudModule.STATS, HudModule.DURABILITY,
			HudModule.EFFECTS, HudModule.SPEED, HudModule.TARGET, HudModule.CPS, HudModule.KEYSTROKES,
			HudModule.NETWORK, HudModule.WAYPOINTS, HudModule.COMPASS
	};

	private static final HudModule[] PVP_SET = {
			HudModule.TARGET, HudModule.CPS, HudModule.KEYSTROKES, HudModule.VITALS, HudModule.STATS,
			HudModule.DURABILITY, HudModule.EFFECTS, HudModule.NETWORK, HudModule.CROSSHAIR,
			HudModule.PLAYERS, HudModule.STATE, HudModule.REACH, HudModule.ATTACK,
			HudModule.BOW, HudModule.THREAT
	};

	private static final HudModule[] DEV_SET = {
			HudModule.INFO, HudModule.CHUNK, HudModule.NETWORK, HudModule.NETGRAPH, HudModule.FPS_GRAPH,
			HudModule.FRAMETIME, HudModule.MEMORY, HudModule.SESSION, HudModule.SERVER, HudModule.LIGHT
	};

	public static void apply(Preset preset, int screenWidth, int screenHeight) {
		HudConfig config = HudConfig.get();

		for (HudModule module : HudModule.values()) {
			config.module(module).enabled = false;
		}

		HudModule[] set = switch (preset) {
			case CLEAN -> CLEAN_SET;
			case BALANCED -> BALANCED_SET;
			case PVP -> PVP_SET;
			case DEV -> DEV_SET;
			case FULL -> HudModule.values();
		};

		for (HudModule module : set) {
			config.module(module).enabled = true;
		}

		HudLayout.autoArrange(screenWidth, screenHeight);
		HudConfig.save();
		ForgeToasts.show(preset.title + " preset applied");
	}

	public static int enabledCount() {
		int count = 0;
		for (HudModule module : HudModule.values()) {
			if (HudConfig.get().module(module).enabled) count++;
		}
		return count;
	}
}
