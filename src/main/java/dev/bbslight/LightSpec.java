package dev.bbslight;

import java.util.regex.Pattern;

public record LightSpec(int level, boolean glow) {
    private static final Pattern LEVEL = Pattern.compile("\\[light=(\\d{1,2})]", Pattern.CASE_INSENSITIVE);
    public static LightSpec parse(String name) {
        var match = LEVEL.matcher(name == null ? "" : name);
        int level = match.find() ? Math.min(15, Integer.parseInt(match.group(1))) : 0;
        return new LightSpec(level, name != null && name.toLowerCase(java.util.Locale.ROOT).contains("[glow]"));
    }
    public static double intensity(double x, double y, double z, int level, double px, double py, double pz) {
        double dx = px-x, dy = py-y, dz = pz-z;
        return Math.max(0, level - Math.sqrt(dx*dx + dy*dy + dz*dz));
    }
    public static int merge(int packed, double intensity) {
        int block = Math.max(packed & 0xffff, Math.min(240, (int) (intensity * 16)));
        return (packed & 0xffff0000) | block;
    }
}
