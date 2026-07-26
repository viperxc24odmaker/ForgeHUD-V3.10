package com.makeforge.forgehud;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Tiny 8x8 pixel-art icons, drawn as filled squares.
 * No textures, no resource packs, no blit calls - it cannot fail to load and it cannot
 * break on a rendering backend, which is the whole point.
 */
public class HudIcons {

	private static final String[] COORDS = {
			"########",
			"#......#",
			"#.####.#",
			"#......#",
			"#.####.#",
			"#......#",
			"#.####.#",
			"########"
	};

	private static final String[] FLAME = {
			"...##...",
			"..####..",
			".###.##.",
			"###...##",
			"###..###",
			".###.##.",
			"..####..",
			"...##..."
	};

	private static final String[] SUN = {
			"...#....",
			"#..#..#.",
			".#####..",
			"..###...",
			"#.###..#",
			".#####..",
			"#..#..#.",
			"...#...."
	};

	private static final String[] SKULL = {
			".######.",
			"########",
			"##.##.##",
			"##.##.##",
			"########",
			".######.",
			".#.##.#.",
			".#.##.#."
	};

	private static final String[] SHIELD = {
			"########",
			"########",
			"#.####.#",
			"#.####.#",
			".#####..",
			"..###...",
			"...#....",
			"........"
	};

	private static final String[] PICK = {
			"..#####.",
			".##...#.",
			"##...#..",
			"....#...",
			"...#....",
			"..#.....",
			".#......",
			"#......."
	};

	private static final String[] POTION = {
			"...##...",
			"...##...",
			"..####..",
			".######.",
			"########",
			"########",
			"########",
			".######."
	};

	private static final String[] ARROW = {
			"........",
			"...#....",
			"...##...",
			"########",
			"########",
			"...##...",
			"...#....",
			"........"
	};

	private static final String[] CROSSHAIR = {
			"...##...",
			"...##...",
			"........",
			"##....##",
			"##....##",
			"........",
			"...##...",
			"...##..."
	};

	private static final String[] MOUSE = {
			"..####..",
			".##..##.",
			".##..##.",
			".######.",
			".######.",
			".######.",
			".######.",
			"..####.."
	};

	private static final String[] KEY = {
			"########",
			"#......#",
			"#.####.#",
			"#.####.#",
			"#.####.#",
			"#.####.#",
			"#......#",
			"########"
	};

	private static final String[] SIGNAL = {
			"......##",
			"......##",
			"...#..##",
			"...##.##",
			"#..##.##",
			"#..##.##",
			"##.##.##",
			"########"
	};

	private static final String[] CLOCK = {
			"..####..",
			".#....#.",
			"#..#...#",
			"#..#...#",
			"#..###.#",
			"#......#",
			".#....#.",
			"..####.."
	};

	private static final String[] FLAG = {
			"##......",
			"#####...",
			"#######.",
			"#####...",
			"##......",
			"##......",
			"##......",
			"##......"
	};

	private static final String[] NEEDLE = {
			"...#....",
			"..###...",
			".##.##..",
			"##...##.",
			".##.##..",
			"..###...",
			"...#....",
			"........"
	};

	private static final String[] GRAPH = {
			"#.......",
			"#.....#.",
			"#....##.",
			"#..#.##.",
			"#.##.##.",
			"#.##.###",
			"####.###",
			"########"
	};

	private static final String[] WIND = {
			"........",
			".#####..",
			"#....##.",
			"........",
			".######.",
			"........",
			"#....##.",
			".#####.."
	};


	private static final String[] GRID = {
			"########",
			"#..#..#.",
			"#..#..#.",
			"####.###",
			"#..#..#.",
			"#..#..#.",
			"####.###",
			"#..#..#."
	};

	private static final String[] CLOUD = {
			"........",
			"..####..",
			".######.",
			"########",
			"########",
			"..#..#..",
			".#..#...",
			"..#..#.."
	};

	private static final String[] DAYNIGHT = {
			"..####..",
			".##...#.",
			"##.....#",
			"##.....#",
			"##.....#",
			"##....#.",
			".#####..",
			"........"
	};

	private static final String[] HEART = {
			".##..##.",
			"########",
			"########",
			"########",
			".######.",
			"..####..",
			"...##...",
			"........"
	};

	private static final String[] XP_ORB = {
			"..####..",
			".#....#.",
			"#..##..#",
			"#.####.#",
			"#.####.#",
			"#..##..#",
			".#....#.",
			"..####.."
	};

	private static final String[] HAND = {
			"...##...",
			"..####..",
			".######.",
			"########",
			"..####..",
			"..####..",
			"..####..",
			"..####.."
	};

	private static final String[] BOLT = {
			"...###..",
			"..###...",
			".###....",
			"######..",
			"...###..",
			"..###...",
			".###....",
			"##......"
	};

	private static final String[] RETICLE = {
			"...##...",
			".######.",
			"##.##.##",
			"########",
			"########",
			"##.##.##",
			".######.",
			"...##..."
	};

	private static final String[] PULSE = {
			"........",
			"..#.....",
			"..#..#..",
			"#.#..#.#",
			"#.#.##.#",
			"#####..#",
			"#......#",
			"########"
	};

	private static final String[] TIMER = {
			"########",
			".######.",
			"..####..",
			"...##...",
			"...##...",
			"..####..",
			".######.",
			"########"
	};

	private static final String[] CHIP = {
			".#.##.#.",
			"########",
			"#.####.#",
			"##....##",
			"##....##",
			"#.####.#",
			"########",
			".#.##.#."
	};

	private static final String[] PEOPLE = {
			".##..##.",
			".##..##.",
			"........",
			"####.###",
			"########",
			"########",
			"##.##.##",
			"##.##.##"
	};

	private static final String[] TOWER = {
			"########",
			"#..##..#",
			"########",
			"........",
			"########",
			"#..##..#",
			"########",
			"...##..."
	};

	private static final String[] RULER = {
			"########",
			"#.#.#.#.",
			"#.#.#.#.",
			"#.......",
			"#.#.#.#.",
			"#.#.#.#.",
			"#.......",
			"########"
	};

	private static final String[] BLADE = {
			"......##",
			".....##.",
			"....##..",
			"...##...",
			"..##....",
			".###....",
			"###.....",
			"##......"
	};

	private static final String[] BOW_ICON = {
			"..###...",
			".##..##.",
			"##....##",
			"#.....##",
			"#.....##",
			"##....##",
			".##..##.",
			"..###..."
	};

	private static final String[] ALERT = {
			"...##...",
			"...##...",
			"..####..",
			"..####..",
			".##..##.",
			".##..##.",
			"##....##",
			"########"
	};

	public static String[] icon(HudModule module) {
		return switch (module) {
			case INFO -> COORDS;
			case NETHER -> FLAME;
			case LIGHT -> SUN;
			case DEATH -> SKULL;
			case STATS -> SHIELD;
			case DURABILITY -> PICK;
			case EFFECTS -> POTION;
			case SPEED -> ARROW;
			case TARGET -> CROSSHAIR;
			case CPS -> MOUSE;
			case KEYSTROKES -> KEY;
			case NETWORK -> SIGNAL;
			case SESSION -> CLOCK;
			case WAYPOINTS -> FLAG;
			case COMPASS -> NEEDLE;
			case FPS_GRAPH -> GRAPH;
			case MOTION -> WIND;
			case CHUNK -> GRID;
			case WEATHER -> CLOUD;
			case DAYCYCLE -> DAYNIGHT;
			case VITALS -> HEART;
			case XP -> XP_ORB;
			case HELD -> HAND;
			case STATE -> BOLT;
			case CROSSHAIR -> RETICLE;
			case NETGRAPH -> PULSE;
			case FRAMETIME -> TIMER;
			case MEMORY -> CHIP;
			case PLAYERS -> PEOPLE;
			case SERVER -> TOWER;
			case REACH -> RULER;
			case ATTACK -> BLADE;
			case BOW -> BOW_ICON;
			case THREAT -> ALERT;
		};
	}

	public static void draw(GuiGraphics graphics, HudModule module, int x, int y, int color) {
		String[] pattern = icon(module);
		for (int row = 0; row < pattern.length; row++) {
			String line = pattern[row];
			for (int col = 0; col < line.length(); col++) {
				if (line.charAt(col) == '#') {
					graphics.fill(x + col, y + row, x + col + 1, y + row + 1, color);
				}
			}
		}
	}
}
