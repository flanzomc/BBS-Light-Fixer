# BBS Light Fixer — clean BBS-CML port

Fabric 1.20.4 / BBS FS 2.5.2.

This implementation was rebuilt from scratch around the open-source BBS-CML effect implementation instead of continuing the previous experimental shader/mask code.

Reference: BBS-CML commit `06f0e53172aadb1fd2a201b03226c9cb59f135e5`.

Ported behavior:
- Glow color + intensity
- Paint color + intensity, including negative darkening
- Color Grade order: brightness, contrast, saturation, hue
- Independent transform masks
- Box / Circle / Triangle mask shapes
- Block breaking stage
- Block-form dynamic emission capped by the block's own luminance

The BBS-CML model fragment shader is used directly under its MIT license. Only the vertex/fog contract and FS integration points are adapted for Minecraft 1.20.4/BBS FS.

Iris world rendering intentionally remains on the original BBS/Iris shader path instead of forcing the custom shader and making models disappear.
