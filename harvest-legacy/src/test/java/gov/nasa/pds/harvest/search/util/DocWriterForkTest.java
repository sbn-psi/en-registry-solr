package gov.nasa.pds.harvest.search.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Element;

class DocWriterForkTest {
    @TempDir Path directory;

    @Test void preservesForkReferenceVersionsAndTrimmingWithUpstreamXmlEscaping() throws Exception {
        Map<String, List<String>> fields = Map.of(
            "target_ref", List.of("urn:nasa:pds:context:target:moon"),
            "instrument_ref", List.of("urn:nasa:pds:context:instrument:test::2.0"),
            "title", List.of("  Moon & <dust>  "));
        new DocWriter(fields, directory.toFile(), 1, "Product_Context",
            Map.of("target_ref", "string", "instrument_ref", "string", "title", "string")).write();
        Path output;
        try (var files = Files.list(directory)) { output = files.findFirst().orElseThrow(); }
        var document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(output.toFile());
        var nodes = document.getElementsByTagName("field");
        Map<String, String> expected = Map.of(
            "target_ref", "urn:nasa:pds:context:target:moon::1.0",
            "instrument_ref", "urn:nasa:pds:context:instrument:test::2.0",
            "title", "Moon & <dust>");
        assertEquals(3, nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            var field = (Element) nodes.item(i);
            assertEquals(expected.get(field.getAttribute("name")), field.getTextContent());
        }
    }
}
