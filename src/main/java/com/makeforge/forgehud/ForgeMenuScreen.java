package com.makeforge.forgehud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class ForgeMenuScreen extends ForgeScreenBase {

	private enum Tab {
		DASHBOARD("Dashboard", HudModule.INFO),
		MODULES("Modules", HudModule.KEYSTROKES),
		PRESETS("Presets", HudModule.STATS),
		LAYOUT("Layout", HudModule.COMPASS),
		WAYPOINTS("Waypoints", HudModule.WAYPOINTS),
		VISUALS("Visuals", HudModule.MOTION),
		ABOUT("About", HudModule.SERVER);

		final String title;
		final HudModule icon;

		Tab(String title, HudModule icon) {
			this.title = title;
			this.icon = icon;
		}
	}

	private static Tab activeTab = Tab.DASHBOARD;

	private int panelX;
	private int panelY;
	private int panelWidth;
	private int panelHeight;
	private int contentX;
	private int contentY;
	private int contentWidth;
	private int contentHeight;

	private int waypointPage = 0;

	public ForgeMenuScreen() {
		super("ForgeHUD");
	}

	@Override
	protected void init() {
		clearZones();

		this.panelWidth = Math.min(this.width - 16, 372);
		this.panelHeight = Math.min(this.height - 16, 202);
		this.panelX = (this.width - this.panelWidth) / 2;
		this.panelY = (this.height - this.panelHeight) / 2;

		int sidebarWidth = 72;
		this.contentX = this.panelX + sidebarWidth + 8;
		this.contentY = this.panelY + 30;
		this.contentWidth = this.panelWidth - sidebarWidth - 16;
		this.contentHeight = this.panelHeight - 40;

		int tabY = this.panelY + 30;
		for (Tab tab : Tab.values()) {
			Tab target = tab;
			Zone zone = addZone(this.panelX + 5, tabY, sidebarWidth - 7, 14, tab.title, () -> {
				activeTab = target;
				this.waypointPage = 0;
				this.rebuild();
			});
			zone.kind = ZoneKind.TAB;
			zone.icon = tab.icon;
			zone.selected = activeTab == tab;
			tabY += 16;
		}

		Zone close = addZone(this.panelX + 5, this.panelY + this.panelHeight - 20,
				sidebarWidth - 7, 14, "Close", this::onClose);
		close.danger = true;

		switch (activeTab) {
			case DASHBOARD -> buildDashboard();
			case MODULES -> buildModules();
			case PRESETS -> buildPresets();
			case LAYOUT -> buildLayout();
			case WAYPOINTS -> buildWaypoints();
			case VISUALS -> buildVisuals();
			case ABOUT -> {
			}
		}
	}

	// ------------------------------------------------------------------ dashboard

	private void buildDashboard() {
		Minecraft client = Minecraft.getInstance();
		int gap = 6;
		int cardWidth = (this.contentWidth - gap) / 2;
		int y = this.contentY;

		addCard(this.contentX, y, cardWidth, 26, HudTrackers.fps() + " FPS",
				(Math.round(HudTrackers.frameTimeMs() * 10.0D) / 10.0D) + " ms per frame", 0xFF4CE080);
		addCard(this.contentX + cardWidth + gap, y, cardWidth, 26,
				HudTrackers.ping(client) + " ms ping",
				"tps " + (Math.round(HudTrackers.tps() * 10.0D) / 10.0D), 0xFF5AC8FA);
		y += 30;

		addCard(this.contentX, y, cardWidth, 26, HudPresets.enabledCount() + " / "
				+ HudModule.values().length + " modules", "active right now", 0xFF9B7BFF);
		addCard(this.contentX + cardWidth + gap, y, cardWidth, 26,
				HudConfig.get().waypoints.size() + " waypoints",
				HudTrackers.worldId(client), 0xFF00E5C0);
		y += 34;

		addHeader(this.contentX, y, this.contentWidth, "Quick controls", ForgeTheme.accent());
		y += 15;

		int half = (this.contentWidth - gap) / 2;

		Zone[] master = new Zone[1];
		master[0] = addZone(this.contentX, y, half, 17, "HUD enabled", () -> {
			HudConfig.get().masterEnabled = !HudConfig.get().masterEnabled;
			master[0].on = HudConfig.get().masterEnabled;
			HudConfig.save();
		});
		master[0].kind = ZoneKind.TOGGLE;
		master[0].on = HudConfig.get().masterEnabled;

		Zone[] glow = new Zone[1];
		glow[0] = addZone(this.contentX + half + gap, y, half, 17, "UI glow", () -> {
			HudConfig.get().uiGlow = !HudConfig.get().uiGlow;
			glow[0].on = HudConfig.get().uiGlow;
			HudConfig.save();
		});
		glow[0].kind = ZoneKind.TOGGLE;
		glow[0].on = HudConfig.get().uiGlow;
		y += 20;

		Zone arrange = addZone(this.contentX, y, half, 17, "Auto arrange", () -> {
			HudLayout.autoArrange(this.width, this.height);
		});
		arrange.value = "run";

		Zone move = addZone(this.contentX + half + gap, y, half, 17, "Move modules", () -> {
			if (this.minecraft != null) this.minecraft.setScreen(new HudEditorScreen(this));
		});
		move.value = "open";
	}

	// ------------------------------------------------------------------ modules

	private void buildModules() {
		int columns = 3;
		int gap = 6;
		int columnWidth = (this.contentWidth - gap * (columns - 1)) / columns;

		int[] columnY = new int[columns];
		for (int i = 0; i < columns; i++) {
			columnY[i] = this.contentY;
		}

		int column = 0;
		int bottom = this.contentY + this.contentHeight;

		for (HudGroup group : HudGroup.values()) {
			int count = 0;
			int on = 0;
			for (HudModule module : HudModule.values()) {
				if (module.group != group) continue;
				count++;
				if (HudConfig.get().module(module).enabled) on++;
			}

			int needed = 15 + count * 16 + 6;
			if (columnY[column] + needed > bottom && column < columns - 1) {
				column++;
			}

			int x = this.contentX + column * (columnWidth + gap);
			int y = columnY[column];

			Zone header = addHeader(x, y, columnWidth, group.title, ForgeTheme.groupColor(group));
			header.value = on + "/" + count;
			y += 15;

			for (HudModule module : HudModule.values()) {
				if (module.group != group) continue;

				HudConfig.ModuleData data = HudConfig.get().module(module);
				Zone[] holder = new Zone[1];
				holder[0] = addZone(x, y, columnWidth, 15, module.label, () -> {
					data.enabled = !data.enabled;
					holder[0].on = data.enabled;
					HudConfig.save();
				});
				holder[0].kind = ZoneKind.TOGGLE;
				holder[0].icon = module;
				holder[0].on = data.enabled;
				holder[0].tint = ForgeTheme.groupColor(group);
				y += 16;
			}

			columnY[column] = y + 6;
		}
	}

	// ------------------------------------------------------------------ presets

	private void buildPresets() {
		int y = this.contentY;

		addHeader(this.contentX, y, this.contentWidth, "One click setups", ForgeTheme.accent());
		y += 16;

		for (HudPresets.Preset preset : HudPresets.Preset.values()) {
			HudPresets.Preset target = preset;
			Zone zone = addZone(this.contentX, y, this.contentWidth, 24, preset.title, () -> {
				HudPresets.apply(target, this.width, this.height);
				this.rebuild();
			});
			zone.sub = preset.description;
			zone.value = "apply";
			y += 27;
		}
	}

	// ------------------------------------------------------------------ layout

	private void buildLayout() {
		int width = this.contentWidth;
		int y = this.contentY;

		addHeader(this.contentX, y, width, "Placement", ForgeTheme.accent());
		y += 16;

		Zone arrange = addZone(this.contentX, y, width, 16, "Auto arrange everything", () -> {
			HudLayout.autoArrange(this.width, this.height);
		});
		arrange.value = "run";
		y += 19;

		Zone move = addZone(this.contentX, y, width, 16, "Move modules by hand", () -> {
			if (this.minecraft != null) this.minecraft.setScreen(new HudEditorScreen(this));
		});
		move.value = "open";
		y += 19;

		Zone reset = addZone(this.contentX, y, width, 16, "Reset all positions", () -> {
			for (HudModule module : HudModule.values()) {
				HudConfig.ModuleData data = HudConfig.get().module(module);
				data.x = module.defaultX;
				data.y = module.defaultY;
			}
			HudConfig.save();
			ForgeToasts.show("Positions reset");
		});
		reset.danger = true;
		y += 25;

		addHeader(this.contentX, y, width, "Behaviour", ForgeTheme.accent());
		y += 16;

		Zone[] master = new Zone[1];
		master[0] = addZone(this.contentX, y, width, 16, "HUD enabled", () -> {
			HudConfig.get().masterEnabled = !HudConfig.get().masterEnabled;
			master[0].on = HudConfig.get().masterEnabled;
			HudConfig.save();
		});
		master[0].kind = ZoneKind.TOGGLE;
		master[0].on = HudConfig.get().masterEnabled;
		y += 19;

		Zone[] shadow = new Zone[1];
		shadow[0] = addZone(this.contentX, y, width, 16, "Text shadow", () -> {
			HudConfig.get().textShadow = !HudConfig.get().textShadow;
			shadow[0].on = HudConfig.get().textShadow;
			HudConfig.save();
		});
		shadow[0].kind = ZoneKind.TOGGLE;
		shadow[0].on = HudConfig.get().textShadow;
	}

	// ------------------------------------------------------------------ visuals

	private void buildVisuals() {
		int gap = 6;
		int half = (this.contentWidth - gap) / 2;
		int leftX = this.contentX;
		int rightX = this.contentX + half + gap;
		int y = this.contentY;

		addHeader(leftX, y, half, "Colour", ForgeTheme.accent());
		addHeader(rightX, y, half, "Feel", ForgeTheme.accent());
		y += 16;

		Zone accent = addZone(leftX, y, half, 16, "Accent", () -> {
			ForgeTheme.cycleAccent();
			HudConfig.save();
			this.rebuild();
		});
		accent.value = ForgeTheme.accentName();

		Zone[] glow = new Zone[1];
		glow[0] = addZone(rightX, y, half, 16, "Glow", () -> {
			HudConfig.get().uiGlow = !HudConfig.get().uiGlow;
			glow[0].on = HudConfig.get().uiGlow;
			HudConfig.save();
		});
		glow[0].kind = ZoneKind.TOGGLE;
		glow[0].on = HudConfig.get().uiGlow;
		y += 19;

		Zone text = addZone(leftX, y, half, 16, "HUD text", () -> {
			HudColors.cycle();
			HudConfig.save();
			this.rebuild();
		});
		text.value = HudColors.currentName();

		Zone[] animations = new Zone[1];
		animations[0] = addZone(rightX, y, half, 16, "Animations", () -> {
			HudConfig.get().uiAnimations = !HudConfig.get().uiAnimations;
			animations[0].on = HudConfig.get().uiAnimations;
			HudConfig.save();
		});
		animations[0].kind = ZoneKind.TOGGLE;
		animations[0].on = HudConfig.get().uiAnimations;
		y += 19;

		Zone panels = addZone(leftX, y, half, 16, "Panels", () -> {
			HudConfig.get().panelStyle = (HudConfig.get().panelStyle + 1) % 4;
			HudConfig.save();
			this.rebuild();
		});
		panels.value = switch (HudConfig.get().panelStyle) {
			case 1 -> "subtle";
			case 2 -> "solid";
			case 3 -> "deep";
			default -> "off";
		};

		Zone[] toasts = new Zone[1];
		toasts[0] = addZone(rightX, y, half, 16, "Notifications", () -> {
			HudConfig.get().toastsEnabled = !HudConfig.get().toastsEnabled;
			toasts[0].on = HudConfig.get().toastsEnabled;
			HudConfig.save();
			if (HudConfig.get().toastsEnabled) ForgeToasts.show("Notifications on");
		});
		toasts[0].kind = ZoneKind.TOGGLE;
		toasts[0].on = HudConfig.get().toastsEnabled;
		y += 25;

		addHeader(leftX, y, this.contentWidth, "Effects", ForgeTheme.accent());
		y += 16;

		Zone motion = addZone(leftX, y, half, 16, "Motion FX", () -> {
			HudConfig.get().motionIntensity = (HudConfig.get().motionIntensity + 1) % 4;
			HudConfig.save();
			this.rebuild();
		});
		motion.icon = HudModule.MOTION;
		motion.value = switch (HudConfig.get().motionIntensity) {
			case 1 -> "light";
			case 2 -> "medium";
			case 3 -> "heavy";
			default -> "off";
		};

		Zone scaleZone = addZone(leftX, y + 19, half, 16, "HUD scale", () -> {
			float next = Math.round((HudConfig.get().hudScale + 0.15F) * 100.0F) / 100.0F;
			if (next > 1.45F) next = 0.75F;
			HudConfig.get().hudScale = next;
			HudConfig.save();
			this.rebuild();
		});
		scaleZone.value = Math.round(HudConfig.get().hudScale * 100.0F) + "%";

		Zone crosshair = addZone(rightX, y, half, 16, "Crosshair", () -> {
			HudConfig.get().crosshairStyle = (HudConfig.get().crosshairStyle + 1) % 4;
			HudConfig.save();
			this.rebuild();
		});
		crosshair.icon = HudModule.CROSSHAIR;
		crosshair.value = switch (HudConfig.get().crosshairStyle) {
			case 1 -> "dot";
			case 2 -> "circle";
			case 3 -> "corners";
			default -> "cross";
		};
	}

	// ------------------------------------------------------------------ waypoints

	private void buildWaypoints() {
		int width = this.contentWidth;
		int y = this.contentY;

		Zone add = addZone(this.contentX, y, width / 2 - 3, 18, "Add waypoint here", () -> {
			addWaypointHere();
			this.rebuild();
		});
		add.value = "B";
		add.icon = HudModule.WAYPOINTS;

		Zone[] all = new Zone[1];
		all[0] = addZone(this.contentX + width / 2 + 3, y, width / 2 - 3, 18, "All worlds", () -> {
			HudConfig.get().waypointsAllWorlds = !HudConfig.get().waypointsAllWorlds;
			all[0].on = HudConfig.get().waypointsAllWorlds;
			HudConfig.save();
		});
		all[0].kind = ZoneKind.TOGGLE;
		all[0].on = HudConfig.get().waypointsAllWorlds;

		y += 23;

		List<Waypoint> waypoints = HudConfig.get().waypoints;
		int rows = Math.max(1, (this.contentY + this.contentHeight - y - 22) / 17);

		int maxPage = Math.max(0, (waypoints.size() - 1) / rows);
		if (this.waypointPage > maxPage) this.waypointPage = maxPage;
		int start = this.waypointPage * rows;

		for (int i = start; i < Math.min(start + rows, waypoints.size()); i++) {
			Waypoint waypoint = waypoints.get(i);

			Zone[] row = new Zone[1];
			row[0] = addZone(this.contentX, y, width - 78, 16,
					waypoint.name + "  " + waypoint.x + " " + waypoint.y + " " + waypoint.z, () -> {
						waypoint.enabled = !waypoint.enabled;
						row[0].on = waypoint.enabled;
						HudConfig.save();
					});
			row[0].kind = ZoneKind.TOGGLE;
			row[0].on = waypoint.enabled;
			row[0].tint = waypoint.color();

			Zone colour = addZone(this.contentX + width - 74, y, 38, 16, "", () -> {
				waypoint.colorIndex = (waypoint.colorIndex + 1) % WaypointColors.COLORS.length;
				HudConfig.save();
				this.rebuild();
			});
			colour.value = WaypointColors.name(waypoint.colorIndex);
			colour.tint = waypoint.color();

			Zone delete = addZone(this.contentX + width - 34, y, 34, 16, "Del", () -> {
				HudConfig.get().waypoints.remove(waypoint);
				HudConfig.save();
				ForgeToasts.show("Waypoint deleted");
				this.rebuild();
			});
			delete.danger = true;

			y += 17;
		}

		int footerY = this.contentY + this.contentHeight - 18;

		addZone(this.contentX, footerY, 42, 18, "Prev", () -> {
			if (this.waypointPage > 0) {
				this.waypointPage--;
				this.rebuild();
			}
		});

		addZone(this.contentX + 46, footerY, 42, 18, "Next", () -> {
			if ((this.waypointPage + 1) * rows < HudConfig.get().waypoints.size()) {
				this.waypointPage++;
				this.rebuild();
			}
		});

		Zone clear = addZone(this.contentX + width - 64, footerY, 64, 18, "Clear all", () -> {
			HudConfig.get().waypoints.clear();
			HudConfig.save();
			ForgeToasts.show("Waypoints cleared");
			this.rebuild();
		});
		clear.danger = true;
	}

	/** Adds a waypoint at the player's feet, tagged with the current world and dimension. */
	public static void addWaypointHere() {
		Minecraft client = Minecraft.getInstance();
		if (client.player == null || client.level == null) return;

		Waypoint waypoint = new Waypoint(
				"WP " + (HudConfig.get().waypoints.size() + 1),
				client.player.blockPosition().getX(),
				client.player.blockPosition().getY(),
				client.player.blockPosition().getZ(),
				ForgeHudRenderer.dimensionOf(client),
				HudTrackers.worldId(client),
				HudConfig.get().waypoints.size() % WaypointColors.COLORS.length);

		HudConfig.get().waypoints.add(waypoint);
		HudConfig.save();
		ForgeToasts.show("Waypoint added");
	}

	// ------------------------------------------------------------------ render

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		this.hoverX = mouseX;
		this.hoverY = mouseY;

		super.render(graphics, mouseX, mouseY, delta);

		int accent = ForgeTheme.accent();

		graphics.fill(0, 0, this.width, this.height, ForgeTheme.BACKDROP);
		ForgeTheme.glow(graphics, this.panelX, this.panelY, this.panelWidth, this.panelHeight,
				ForgeTheme.withAlpha(accent, 0xFF), 4);
		ForgeTheme.panel(graphics, this.panelX, this.panelY, this.panelWidth, this.panelHeight);

		// Header stripe
		ForgeTheme.gradientH(graphics, this.panelX + 3, this.panelY + 1, this.panelWidth - 6, 2,
				accent, ForgeTheme.withAlpha(accent, 0x00));

		graphics.drawString(this.font, "FORGEHUD", this.panelX + 8, this.panelY + 8, accent, false);
		graphics.drawString(this.font, "v3.1.0", this.panelX + 10 + this.font.width("FORGEHUD"),
				this.panelY + 8, ForgeTheme.TEXT_DIM, false);

		String hint = "Right Shift  menu    Right Ctrl  toggle    B  waypoint";
		graphics.drawString(this.font, hint,
				this.panelX + this.panelWidth - 8 - this.font.width(hint), this.panelY + 8,
				ForgeTheme.TEXT_DIM, false);

		ForgeTheme.divider(graphics, this.panelX + 6, this.panelY + 22, this.panelWidth - 12);
		graphics.fill(this.panelX + 79, this.panelY + 26, this.panelX + 93,
				this.panelY + this.panelHeight - 8, ForgeTheme.PANEL_EDGE);

		renderZones(graphics);

		if (activeTab == Tab.ABOUT) {
			renderAbout(graphics);
		}
		if (activeTab == Tab.WAYPOINTS) {
			String world = HudConfig.get().waypointsAllWorlds
					? "showing every world"
					: "world: " + HudTrackers.worldId(Minecraft.getInstance());
			graphics.drawString(this.font, world, this.contentX + 92,
					this.contentY + this.contentHeight - 13, ForgeTheme.TEXT_DIM, false);
		}
	}

	private void renderAbout(GuiGraphics graphics) {
		String[] lines = {
				"ForgeHUD  -  MakeForge / PixelForge Studios",
				"",
				"30 modules across 6 categories, all optional,",
				"all movable, all drawn with GuiGraphics only.",
				"No mixins, no raw GL, no framebuffers, no",
				"textures. That is why it holds up on Vulkan.",
				"",
				"Right Shift   this menu",
				"Right Ctrl    toggle the whole HUD",
				"B             drop a waypoint"
		};

		int y = this.contentY + 4;
		for (String line : lines) {
			boolean key = line.startsWith("Right") || line.startsWith("B ");
			graphics.drawString(this.font, line, this.contentX, y,
					key ? ForgeTheme.accent() : ForgeTheme.TEXT_DIM, false);
			y += 11;
		}
	}

	@Override
	public void onClose() {
		HudConfig.save();
		if (this.minecraft != null) {
			this.minecraft.setScreen(null);
		}
	}
}
