# BBS Light Fixer — experimental first build

Fabric **1.20.4**, BBS FS **2.5.2** API baseline. Requires Fabric API. No shader pack or Iris is required. Newer BBS FS releases are not yet runtime-verified. This is NOT a CML build.

## Use

In the BBS form editor, put tags in the form's **name**:

- `Torch [light=15] [glow]`: bright form plus a moving light.
- `Lamp [light=10]`: nearby illumination without making the form fullbright.
- `Flame [glow]`: fullbright form only.
- `Lamp [light=0]`: no emitted light.

For a flame/eyes-only glow, use a separate child form containing those parts. Attach the child to the desired model bone with BBS's existing body-part tools. The addon samples the transformed child position, including its animated transform. Use existing BBS visibility animation to switch the source off (invisible forms do not emit). Name tags persist with saved forms; no commands are needed to restore them.

`/bbslight` shows status; `/bbslight toggle` disables/enables the addon for the current session.

## What is implemented

Fullbright tagged forms and client-side vanilla lightmap illumination for terrain and renderers that use vanilla lightmap sampling. Moving/deleted sources invalidate their old terrain sections. No blocks are placed or altered. Up to 64 rendered sources, 128-block capture distance, 10 Hz updates, bounded terrain rebuild queue. Immutable light snapshots are safe for chunk worker reads.

## Important alpha limitations

- No bloom, colored lighting, shadows, or per-pixel `_e.png` masks yet. Fullbright is not bloom.
- Light can pass through walls; this is distance-based visual illumination, not Minecraft's server light propagation.
- Sources are discovered while BBS renders their forms. Off-screen/culled sources may stop emitting. Terrain changes have rebuild latency.
- Sodium and shader-pack pipelines are not supported/verified by this first vanilla-renderer build.
- GUI thumbnails are not world light sources. Fullbright applies where BBS calls the common form renderer; standalone preview paths may differ.
- No in-game client test is possible in CI; compilation/unit tests are not proof of visual correctness. Test in a copy of a scene first.

The earlier design includes selective emissive textures, bloom and broader renderer compatibility; this alpha does not claim those features are complete.
