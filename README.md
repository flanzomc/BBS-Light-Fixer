# BBS Light Fixer — clean BBS CML backport

Fabric 1.20.4 / BBS FS 2.5.2.

This branch is a clean rewrite. The previous `dev/bbslight/cml` implementation and its `cml_effects` side-data format are not part of this source tree.

The implementation is based on the open-source BBS CML behavior at commit `06f0e53172aadb1fd2a201b03226c9cb59f135e5` and adapts it to the BBS FS 1.20.4 renderer.

## Backported behavior

- CML-style `glow` and `paint` form values
- signed paint intensity (-1..1) and signed/unbounded glow intensity
- brightness, contrast, hue and saturation on the real Color value
- separate spatial transform for Color, Glow, Paint and every Color Grade channel
- CML box / circle / triangle mask math and mask extents
- CML-style Block light enable/intensity, capped by the selected block's own luminance
- Block breaking stage and CML repeat/biome values in form data
- deferred-translucent state capture without constructor mixins
- no display-name tags and no `cml_effects` compatibility layer

BBS CML source used for the backport is MIT licensed; its license is included under `META-INF/licenses/BBS-CML.txt`.
