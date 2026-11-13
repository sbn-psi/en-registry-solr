package gov.nasa.pds.harvest.search.doc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import gov.nasa.pds.harvest.search.oodt.metadata.Metadata;
import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.Slot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;

class SearchDocGeneratorForkTest {
    @TempDir Path directory;

    private Document generate(String labelUrl) throws Exception {
        Path config = directory.resolve("product.xml");
        Files.writeString(config, """
            <product>
              <specification>
                <title>Fork resource fixture</title>
                <query><registryPath>objectType</registryPath><value>Fork_Resource_Fixture</value></query>
              </specification>
              <indexFields>
                <field name="title" type="string"><registryPath>name</registryPath></field>
                <field name="file_ref_url" type="string"><registryPath>file_ref_access_url</registryPath></field>
                <field name="resource_url" type="string"><registryPath>resource_url</registryPath></field>
              </indexFields>
            </product>
            """);
        var product = new ExtrinsicObject();
        product.setObjectType("Fork_Resource_Fixture");
        product.setLid("urn:test:fork:resource");
        product.setName("Resource without a label URL");
        product.addSlot(new Slot("resource_url", List.of("https://example.org/resource")));
        if (labelUrl != null) {
            product.addSlot(new Slot("file_ref_access_url", List.of(labelUrl)));
        }
        var generator = new SearchDocGenerator(config.toFile(), directory.resolve("output").toFile());
        assertDoesNotThrow(() -> generator.generate(product, new Metadata(), new SearchDocState()));
        return DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(directory.resolve("output/solr_doc_0.xml").toFile());
    }

    @Test void emptyMappedLabelUrlDoesNotDropTheDocumentOrItsResourceUrl() throws Exception {
        var document = generate(null);
        assertEquals("https://example.org/resource", value(document, "resource_url"));
        assertEquals("Resource without a label URL", value(document, "title"));
    }

    @Test void populatedLabelUrlStillProvidesTheSbnFolderUrl() throws Exception {
        assertEquals("https://example.org/data/", value(generate("https://example.org/data/label.xml"), "resource_url"));
    }

    private String value(Document document, String name) {
        var fields = document.getElementsByTagName("field");
        for (int i = 0; i < fields.getLength(); i++) {
            var node = fields.item(i);
            if (name.equals(node.getAttributes().getNamedItem("name").getNodeValue())) return node.getTextContent();
        }
        throw new AssertionError("Missing field " + name);
    }
}
