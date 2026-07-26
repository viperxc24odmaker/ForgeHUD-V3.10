package com.makeforge.forgehud;

public enum HudModule {
	//        label         group              on      x      y
	INFO("Coords", HudGroup.WORLD, true, 0.0F, 0.02F),
	NETHER("Nether", HudGroup.WORLD, false, 0.0F, 0.19F),
	CHUNK("Chunk", HudGroup.WORLD, false, 0.0F, 0.24F),
	LIGHT("Light", HudGroup.WORLD, true, 0.0F, 0.34F),
	WEATHER("Weather", HudGroup.WORLD, false, 0.0F, 0.40F),
	DAYCYCLE("Day cycle", HudGroup.WORLD, false, 0.5F, 0.10F),
	DEATH("Death", HudGroup.WORLD, true, 1.0F, 0.44F),

	VITALS("Vitals", HudGroup.PLAYER, false, 0.5F, 0.80F),
	STATS("Stats", HudGroup.PLAYER, true, 1.0F, 0.30F),
	XP("XP", HudGroup.PLAYER, false, 0.5F, 0.86F),
	DURABILITY("Gear", HudGroup.PLAYER, true, 0.0F, 0.46F),
	HELD("Held item", HudGroup.PLAYER, false, 0.5F, 0.72F),
	EFFECTS("Effects", HudGroup.PLAYER, true, 1.0F, 0.02F),
	SPEED("Speed", HudGroup.PLAYER, true, 0.0F, 0.28F),
	STATE("State", HudGroup.PLAYER, false, 0.5F, 0.66F),

	TARGET("Target", HudGroup.COMBAT, true, 0.5F, 0.56F),
	CPS("CPS", HudGroup.COMBAT, true, 0.0F, 0.90F),
	KEYSTROKES("Keys", HudGroup.COMBAT, true, 0.0F, 0.74F),
	CROSSHAIR("Crosshair", HudGroup.COMBAT, false, 0.5F, 0.5F),
	REACH("Reach", HudGroup.COMBAT, false, 0.5F, 0.62F),
	ATTACK("Attack", HudGroup.COMBAT, false, 0.5F, 0.65F),
	BOW("Bow charge", HudGroup.COMBAT, false, 0.5F, 0.69F),
	THREAT("Threat", HudGroup.COMBAT, false, 1.0F, 0.36F),

	NETWORK("Ping/TPS", HudGroup.SYSTEM, true, 0.0F, 0.21F),
	NETGRAPH("Net graph", HudGroup.SYSTEM, false, 1.0F, 0.74F),
	FPS_GRAPH("FPS graph", HudGroup.SYSTEM, false, 1.0F, 0.60F),
	FRAMETIME("Frame time", HudGroup.SYSTEM, false, 1.0F, 0.56F),
	MEMORY("Memory", HudGroup.SYSTEM, false, 1.0F, 0.50F),
	SESSION("Session", HudGroup.SYSTEM, false, 1.0F, 0.52F),
	MOTION("Motion FX", HudGroup.SYSTEM, false, 0.5F, 0.5F),

	PLAYERS("Nearby", HudGroup.SOCIAL, false, 1.0F, 0.20F),
	SERVER("Server", HudGroup.SOCIAL, false, 1.0F, 0.14F),

	WAYPOINTS("List", HudGroup.WAYPOINT, true, 1.0F, 0.16F),
	COMPASS("Compass", HudGroup.WAYPOINT, true, 0.5F, 0.02F);

	public final String label;
	public final HudGroup group;
	public final boolean defaultEnabled;
	public final float defaultX;
	public final float defaultY;

	HudModule(String label, HudGroup group, boolean defaultEnabled, float defaultX, float defaultY) {
		this.label = label;
		this.group = group;
		this.defaultEnabled = defaultEnabled;
		this.defaultX = defaultX;
		this.defaultY = defaultY;
	}

	public String id() {
		return name().toLowerCase();
	}
}
