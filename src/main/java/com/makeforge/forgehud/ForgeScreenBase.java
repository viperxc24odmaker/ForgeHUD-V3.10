package com.makeforge.forgehud;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Base for ForgeHUD's interface.
 *
 * Every clickable area is a real (empty label) vanilla Button underneath, and ForgeHUD paints
 * its own animated row over the top after super.render(). The look is entirely custom while
 * click handling rides the widget system that has been stable for years - no dependency on
 * the 1.21.11 MouseButtonEvent signature.
 */
public abstract class ForgeScreenBase extends Screen {

	protected int hoverX = -1;
	protected int hoverY = -1;

	private final List<Zone> zones = new ArrayList<>();
	private long lastFrame = 0L;

	public enum ZoneKind {
		TAB, TOGGLE, ACTION, HEADER, CARD
	}

	protected static class Zone {
		public int x;
		public int y;
		public int width;
		public int height;
		public String label = "";
		public String value = null;
		public String sub = null;
		public HudModule icon = null;
		public ZoneKind kind = ZoneKind.ACTION;
		public boolean on = false;
		public boolean selected = false;
		public boolean danger = false;
		public int tint = 0;

		/** 0..1 hover animation. */
		public float animation = 0.0F;
		/** 0..1 toggle animation. */
		public float toggleAnimation = 0.0F;
	}

	protected ForgeScreenBase(String title) {
		super(Component.literal(title));
	}

	protected void clearZones() {
		this.zones.clear();
	}

	protected Zone addHeader(int x, int y, int width, String label, int tint) {
		Zone zone = new Zone();
		zone.x = x;
		zone.y = y;
		zone.width = width;
		zone.height = 12;
		zone.label = label;
		zone.kind = ZoneKind.HEADER;
		zone.tint = tint;
		this.zones.add(zone);
		return zone;
	}

	/** Non-interactive stat card. No button is registered for it. */
	protected Zone addCard(int x, int y, int width, int height, String label, String sub, int tint) {
		Zone zone = new Zone();
		zone.x = x;
		zone.y = y;
		zone.width = width;
		zone.height = height;
		zone.label = label;
		zone.sub = sub;
		zone.kind = ZoneKind.CARD;
		zone.tint = tint;
		this.zones.add(zone);
		return zone;
	}

	protected Zone addZone(int x, int y, int width, int height, String label, Runnable action) {
		this.addRenderableWidget(Button.builder(Component.empty(), button -> action.run())
				.bounds(x, y, width, height).build());

		Zone zone = new Zone();
		zone.x = x;
		zone.y = y;
		zone.width = width;
		zone.height = height;
		zone.label = label;
		this.zones.add(zone);
		return zone;
	}

	protected boolean isHovered(Zone zone) {
		return this.hoverX >= zone.x && this.hoverX <= zone.x + zone.width
				&& this.hoverY >= zone.y && this.hoverY <= zone.y + zone.height;
	}

	private void advance(Zone zone, float step) {
		boolean hovered = isHovered(zone);
		float targetHover = (zone.selected || hovered) ? 1.0F : 0.0F;
		float targetToggle = zone.on ? 1.0F : 0.0F;

		if (!HudConfig.get().uiAnimations) {
			zone.animation = targetHover;
			zone.toggleAnimation = targetToggle;
			return;
		}

		zone.animation += (targetHover - zone.animation) * step;
		zone.toggleAnimation += (targetToggle - zone.toggleAnimation) * step;

		if (Math.abs(targetHover - zone.animation) < 0.01F) zone.animation = targetHover;
		if (Math.abs(targetToggle - zone.toggleAnimation) < 0.01F) zone.toggleAnimation = targetToggle;
	}

	protected void renderZones(GuiGraphics graphics) {
		long now = System.currentTimeMillis();
		float step = this.lastFrame == 0L ? 1.0F : Math.min(1.0F, (now - this.lastFrame) / 90.0F);
		this.lastFrame = now;

		int accent = ForgeTheme.accent();

		for (Zone zone : this.zones) {
			if (zone.kind == ZoneKind.HEADER) {
				int tint = zone.tint != 0 ? zone.tint : ForgeTheme.TEXT_DIM;
				graphics.fill(zone.x, zone.y + 2, zone.x + 2, zone.y + 9, tint);
				graphics.drawString(this.font, zone.label.toUpperCase(Locale.ROOT),
						zone.x + 5, zone.y + 2, tint, false);
				if (zone.value != null) {
					graphics.drawString(this.font, zone.value,
							zone.x + zone.width - this.font.width(zone.value), zone.y + 2,
							ForgeTheme.TEXT_DIM, false);
				}
				ForgeTheme.divider(graphics, zone.x, zone.y + 11, zone.width);
				continue;
			}

			advance(zone, step);

			int highlight = zone.danger ? ForgeTheme.DANGER : (zone.tint != 0 ? zone.tint : accent);
			int base = zone.kind == ZoneKind.CARD ? 0xFF121824 : ForgeTheme.ROW;
			int hovered = ForgeTheme.lerpColor(base, ForgeTheme.ROW_HOVER, zone.animation);

			ForgeTheme.gradientV(graphics, zone.x, zone.y, zone.width, zone.height,
					hovered, ForgeTheme.lerpColor(base, 0xFF0E141D, 0.6F));

			if (zone.animation > 0.01F) {
				int wash = ForgeTheme.withAlpha(highlight, Math.round(26 * zone.animation));
				graphics.fill(zone.x, zone.y, zone.x + zone.width, zone.y + zone.height, wash);
				ForgeTheme.glow(graphics, zone.x, zone.y, zone.width, zone.height,
						ForgeTheme.withAlpha(highlight, Math.round(120 * zone.animation)), 2);
			}

			// Accent bar grows out of the left edge on hover / selection.
			int barHeight = Math.round(zone.height * zone.animation);
			if (barHeight > 0) {
				ForgeTheme.accentBar(graphics, zone.x, zone.y + (zone.height - barHeight) / 2,
						barHeight, highlight);
			}

			int textX = zone.x + 7;
			if (zone.icon != null) {
				int iconColor = zone.kind == ZoneKind.TOGGLE && !zone.on ? ForgeTheme.OFF : highlight;
				HudIcons.draw(graphics, zone.icon, zone.x + 6, zone.y + (zone.height - 8) / 2, iconColor);
				textX = zone.x + 17;
			}

			int labelColor = zone.danger
					? 0xFFFF8B9B
					: (zone.kind == ZoneKind.TOGGLE && !zone.on
					? ForgeTheme.OFF
					: ForgeTheme.lerpColor(ForgeTheme.TEXT, highlight, zone.animation * 0.5F));

			int labelY = zone.sub != null ? zone.y + 4 : zone.y + (zone.height - 8) / 2;
			graphics.drawString(this.font, zone.label, textX, labelY, labelColor, false);

			if (zone.sub != null) {
				graphics.drawString(this.font, zone.sub, textX, zone.y + 14, ForgeTheme.TEXT_DIM, false);
			}

			if (zone.kind == ZoneKind.TOGGLE) {
				ForgeTheme.pill(graphics, zone.x + zone.width - 24,
						zone.y + (zone.height - 9) / 2, zone.on, zone.toggleAnimation);
			} else if (zone.value != null) {
				int valueX = zone.x + zone.width - 7 - this.font.width(zone.value);
				graphics.drawString(this.font, zone.value, valueX,
						zone.y + (zone.height - 8) / 2, highlight, false);
			}
		}
	}

	protected void rebuild() {
		this.clearWidgets();
		this.clearZones();
		this.init();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
