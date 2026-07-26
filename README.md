# ForgeHUD 3.0.0 (MakeForge / PixelForge Studios)

Fabric 1.21.11 client-side HUD suite. Drawn entirely with `GuiGraphics` through Fabric's
`HudElementRegistry` - no mixins, no raw GL, no framebuffers, no textures. Every render and
tick path is wrapped in try/catch, which is why it holds up under VulkanMod.

## 30 modules, 6 categories

**World** - Coords, Nether conversion, Chunk, Light + spawn warning, Weather + moon phase,
Day cycle bar, Death point

**Player** - Vitals, Stats, XP, Gear durability, Held item, Effect timers, Speed, State badges

**Combat** - Target health, CPS, Keystrokes, Custom crosshair (4 styles)

**System** - Ping/TPS, Net graph, FPS graph, Frame time, Memory, Session, Motion FX

**Social** - Nearby players, Server info

**Waypoints** - Waypoint list, Compass bar

## Interface
Seven tabs: Dashboard, Modules, Presets, Layout, Waypoints, Visuals, About.

- **Dashboard** - live stat cards (FPS + frame time, ping + TPS, modules active, waypoints)
  and quick controls
- **Presets** - Clean, Balanced, PvP, Debug, Everything. One click sets modules and re-arranges
- **Modules** - all 30 toggles, grouped, colour coded per category, with per-group counts
- **Visuals** - accent (8 colours + animated rainbow), HUD text colour, glow, animations,
  panel style, crosshair style, motion strength, notifications

Rows animate on hover: gradient lift, growing accent bar, outer glow, sliding toggle knobs.
Clicks ride hidden vanilla Buttons underneath, so none of it depends on the 1.21.11
MouseButtonEvent signature.

## Layout
Anchor-point positioning (left / centre / right edge pinning, right-anchored text right-aligns
so nothing drifts), auto arrange, drag editor with category-coloured ghost footprints,
edge and centre snapping. Positions stored as screen fractions.

## Controls
Right Shift - menu | Right Ctrl - toggle HUD | B - add waypoint

Config: `config/forgehud.json`

## Build
Requires Gradle 9.2+ (Loom 1.14.10). Workflow uses `gradle-version: current`.
`.github/workflows/build.yml` must be created manually in the GitHub web editor.
