# BBS Light Fixer — CML feature port

Fabric 1.20.4, BBS FS 2.5.2. This replaces the old display-name tags and commands with saved form properties.

Requires Fabric API and LambDynamicLights **2.3.4+1.20.4** in the mods folder. Enable dynamic lighting in LambDynamicLights settings. No external shader pack is required.

Model and Block form editors gain Color extras (glow and paint), Color grade (brightness, contrast, saturation and hue), and per-channel effect transforms. Block forms gain emission enable/intensity and breaking stage (0 off, 1–10 cracks). Block light follows CML: capped by the underlying block luminance, so a torch can emit but ordinary stone cannot. Glow adjusts the surface; it is not bloom or colored world lighting. World light uses LambDynamicLights' radius and terrain update behavior.

The values are stored under cml_effects in FS forms. They are not a CML form file converter. Old name tags no longer configure this replacement.

## Source and attribution

Color processing and effect-mask formulas are adapted from [BBS-CML](https://github.com/ElGatoPro300/BBS-CML), commit 06f0e53172aadb1fd2a201b03226c9cb59f135e5, MIT, Copyright 2025 McHorse / Copyright 2026 ElGatoPro300. Full license ships in META-INF/licenses/BBS-CML.txt. World lighting is provided by LambDynamicLights, not bundled/copied.

## Validation and scope

Experimental port. Compile/build checks do not replace Minecraft visual testing. Current render support is BBS model geometry (VAO and non-VAO) and ordinary baked Block forms with the vanilla renderer. Fluid/block-entity renderers and Iris shader-pack overlays need separate integration. Future FS versions are not automatically guaranteed compatible with these mixins.
