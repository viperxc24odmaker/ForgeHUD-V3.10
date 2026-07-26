package com.makeforge.forgehud;

public enum HudGroup {
	WORLD("World"),
	PLAYER("Player"),
	COMBAT("Combat"),
	SYSTEM("System"),
	SOCIAL("Social"),
	WAYPOINT("Waypoints");

	public final String title;

	HudGroup(String title) {
		this.title = title;
	}
}
