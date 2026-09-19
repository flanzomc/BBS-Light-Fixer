package dev.bbslight;

import com.google.gson.JsonParser;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class DependencyVersionTest {
    @Test void shippedDependencyAcceptsBbsMinecraftSuffix() throws Exception {
        try (var stream = getClass().getResourceAsStream("/fabric.mod.json")) {
            assertNotNull(stream);
            var metadata = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            var predicate = VersionPredicate.parse(metadata.getAsJsonObject("depends").get("bbs").getAsString());
            assertTrue(predicate.test(Version.parse("2.5.2-1.20.4")));
            assertTrue(predicate.test(Version.parse("2.5.2")));
            assertTrue(predicate.test(Version.parse("2.6.1-1.20.4")));
            assertFalse(predicate.test(Version.parse("2.5.1-1.20.4")));
        }
    }
}
