package guru.qa;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.model.ZipTestModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTest {
    @Test
    @DisplayName("Check JSON")
    public void testJsonParsing() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File("src/test/resources/test.json");

        List<ZipTestModel> animals = objectMapper.readValue(jsonFile, new TypeReference<>() {
        });

        assertEquals(2, animals.size());

        ZipTestModel animal1 = animals.get(0);
        assertEquals("cat", animal1.getAnimal());
        assertEquals("cute", animal1.getAppearance());

        ZipTestModel animal2 = animals.get(1);
        assertEquals("dog", animal2.getAnimal());
        assertEquals("also cute", animal2.getAppearance());
    }
}
