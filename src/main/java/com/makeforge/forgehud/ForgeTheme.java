package com.makeforge.forgehud;

import net.minecraft.client.gui.GuiGraphics;

/**
 * ForgeHUD's look: accents, gradients, glows, clipped panels, pills.
 * Everything is fill() rectangles - no textures, no sprites, no blit calls.
 */
public class ForgeTheme {

	public static final int[] ACCENTS = {
			0xFF5AC8FA, 0xFF9B7BFF, 0xFF4CE080, 0xFFFF7A45, 0xFFFF4D6D, 0xFFFFC53D, 0xFF00E5C0, 0xFFFF9AF0
	};

	public static final String[] ACCENT_NAMES = {
			"Ice", "Violet", "Lime", "Ember", "Rose", "Amber", "Teal", "Blossom"
	};

	public static final int BACKDROP = 0xE8080B11;
	public static final int PANEL_TOP = 0xF6161D29;
	public static final int PANEL_BOTTOM = 0xF60F141D;
	public static final int PANEL_EDGE = 0xFF27303F;
	public static final int ROW = 0xFF161C26;
	public static final int ROW_HOVER = 0xFF222C3B;
	public static final int TEXT = 0xFFE9EFF7;
	public static final int TEXT_DIM = 0xFF7B8899;
	public static final int OFF = 0xFF465264;
	public static final int DANGER = 0xFFFF4D6D;

	// ------------------------------------------------------------------ accent

	public static int accent() {
		if (HudConfig.get().rainbowAccent) {
			float hue = (System.currentTimeMillis() % 6000L) / 6000.0F;
			return hsv(hue, 0.62F, 1.0F);
		}
		int index = HudConfig.get().accentIndex;
		if (index < 0 || index >= ACCENTS.length) index = 0;
		return ACCENTS[index];
	}

	public static String accentName() {
		if (HudConfig.get().rainbowAccent) return "Rainbow";
		int index = HudConfig.get().accentIndex;
		if (index < 0 || index >= ACCENT_NAMES.length) index = 0;
		return ACCENT_NAMES[index];
	}

	public static void cycleAccent() {
		HudConfig config = HudConfig.get();
		if (config.rainbowAccent) {
			config.rainbowAccent = false;
			config.accentIndex = 0;
			return;
		}
		config.accentIndex++;
		if (config.accentIndex >= ACCENTS.length) {
			config.accentIndex = 0;
			config.rainbowAccent = true;
		}
	}

	/** Per-group tint so categories read apart at a glance. */
	public static int groupColor(HudGroup group) {
		return switch (group) {
			case WORLD -> 0xFF4CE080;
			case PLAYER -> 0xFF5AC8FA;
			case COMBAT -> 0xFFFF7A45;
			case SYSTEM -> 0xFF9B7BFF;
			case SOCIAL -> 0xFFFFC53D;
			case WAYPOINT -> 0xFF00E5C0;
		};
	}

	public static int hsv(float hue, float saturation, float value) {
		float h = (hue % 1.0F) * 6.0F;
		int sector = (int) h;
		float f = h - sector;
		float p = value * (1.0F - saturation);
		float q = value * (1.0F - saturation * f);
		float t = value * (1.0F - saturation * (1.0F - f));

		float r;
		float g;
		float b;
		switch (sector % 6) {
			case 0 -> { r = value; g = t; b = p; }
			case 1 -> { r = q; g = value; b = p; }
			case 2 -> { r = p; g = value; b = t; }
			case 3 -> { r = p; g = q; b = value; }
			case 4 -> { r = t; g = p; b = value; }
			default -> { r = value; g = p; b = q; }
		}
		return 0xFF000000 | (Math.round(r * 255) << 16) | (Math.round(g * 255) << 8) | Math.round(b * 255);
	}

	// ------------------------------------------------------------------ drawing

	public static int withAlpha(int color, int alpha) {
		return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
	}

	public static int lerpColor(int from, int to, float t) {
		t = Math.max(0.0F, Math.min(1.0F, t));
		int a = lerp((from >> 24) & 0xFF, (to >> 24) & 0xFF, t);
		int r = lerp((from >> 16) & 0xFF, (to >> 16) & 0xFF, t);
		int g = lerp((from >> 8) & 0xFF, (to >> 8) & 0xFF, t);
		int b = lerp(from & 0xFF, to & 0xFF, t);
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	private static int lerp(int from, int to, float t) {
		return from + Math.round((to - from) * t);
	}

	/** Vertical gradient, drawn as one-pixel rows. */
	public static void gradientV(GuiGraphics graphics, int x, int y, int width, int height, int top, int bottom) {
		if (height <= 0 || width <= 0) return;
		for (int i = 0; i < height; i++) {
			graphics.fill(x, y + i, x + width, y + i + 1, lerpColor(top, bottom, i / (float) height));
		}
	}

	public static void gradientH(GuiGraphics graphics, int x, int y, int width, int height, int left, int right) {
		if (height <= 0 || width <= 0) return;
		for (int i = 0; i < width; i++) {
			graphics.fill(x + i, y, x + i + 1, y + height, lerpColor(left, right, i / (float) width));
		}
	}

	/** Soft outer glow around a box. */
	public static void glow(GuiGraphics graphics, int x, int y, int width, int height, int color, int rings) {
		if (!HudConfig.get().uiGlow) return;
		for (int i = 1; i <= rings; i++) {
			int alpha = Math.max(0, 40 - i * (40 / Math.max(1, rings)));
			int ring = withAlpha(color, alpha);
			graphics.fill(x - i, y - i, x + width + i, y - i + 1, ring);
			graphics.fill(x - i, y + height + i - 1, x + width + i, y + height + i, ring);
			graphics.fill(x - i, y - i, x - i + 1, y + height + i, ring);
			graphics.fill(x + width + i - 1, y - i, x + width + i, y + height + i, ring);
		}
	}

	/** Panel with clipped corners and a vertical gradient body. */
	public static void panel(GuiGraphics graphics, int x, int y, int width, int height) {
		int right = x + width;
		int bottom = y + height;

		gradientV(graphics, x + 3, y, width - 6, height, PANEL_TOP, PANEL_BOTTOM);
		gradientV(graphics, x, y + 3, 3, height - 6, PANEL_TOP, PANEL_BOTTOM);
		gradientV(graphics, right - 3, y + 3, 3, height - 6, PANEL_TOP, PANEL_BOTTOM);
		graphics.fill(x + 1, y + 1, x + 3, y + 3, PANEL_TOP);
		graphics.fill(right - 3, y + 1, right - 1, y + 3, PANEL_TOP);
		graphics.fill(x + 1, bottom - 3, x + 3, bottom - 1, PANEL_BOTTOM);
		graphics.fill(right - 3, bottom - 3, right - 1, bottom - 1, PANEL_BOTTOM);

		graphics.fill(x + 3, y, right - 3, y + 1, PANEL_EDGE);
		graphics.fill(x + 3, bottom - 1, right - 3, bottom, PANEL_EDGE);
		graphics.fill(x, y + 3, x + 1, bottom - 3, PANEL_EDGE);
		graphics.fill(right - 1, y + 3, right, bottom - 3, PANEL_EDGE);
		graphics.fill(x + 1, y + 2, x + 2, y + 3, PANEL_EDGE);
		graphics.fill(x + 2, y + 1, x + 3, y + 2, PANEL_EDGE);
		graphics.fill(right - 2, y + 2, right - 1, y + 3, PANEL_EDGE);
		graphics.fill(right - 3, y + 1, right - 2, y + 2, PANEL_EDGE);
		graphics.fill(x + 1, bottom - 3, x + 2, bottom - 2, PANEL_EDGE);
		graphics.fill(x + 2, bottom - 2, x + 3, bottom - 1, PANEL_EDGE);
		graphics.fill(right - 2, bottom - 3, right - 1, bottom - 2, PANEL_EDGE);
		graphics.fill(right - 3, bottom - 2, right - 2, bottom - 1, PANEL_EDGE);
	}

	public static void accentBar(GuiGraphics graphics, int x, int y, int height, int color) {
		graphics.fill(x, y, x + 2, y + height, color);
	}

	public static void divider(GuiGraphics graphics, int x, int y, int width) {
		gradientH(graphics, x, y, width, 1, PANEL_EDGE, withAlpha(PANEL_EDGE, 0x00));
	}

	/** Toggle pill with an animated knob. */
	public static void pill(GuiGraphics graphics, int x, int y, boolean on, float animation) {
		int width = 18;
		int height = 9;
		int track = on
				? lerpColor(0xFF2A323E, withAlpha(accent(), 0x99), animation)
				: 0xFF2A323E;

		graphics.fill(x + 1, y, x + width - 1, y + height, track);
		graphics.fill(x, y + 1, x + 1, y + height - 1, track);
		graphics.fill(x + width - 1, y + 1, x + width, y + height - 1, track);

		int travel = Math.round((width - 8) * animation);
		int knobX = x + 1 + travel;
		int knob = on ? accent() : OFF;
		graphics.fill(knobX, y + 1, knobX + 6, y + height - 1, knob);
	}

	/** Panel behind HUD modules: 0 = off, 1 = subtle, 2 = solid, 3 = gradient. */
	public static int hudPanelColor() {
		return switch (HudConfig.get().panelStyle) {
			case 1 -> 0x40000000;
			case 2 -> 0x99070B12;
			case 3 -> 0xCC0B1018;
			default -> 0;
		};
	}
}
