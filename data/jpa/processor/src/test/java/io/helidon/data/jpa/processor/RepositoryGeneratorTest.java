package io.helidon.data.jpa.processor;

import org.junit.jupiter.api.Test;

public class RepositoryGeneratorTest {

    @Test
    void testTemplateGeneration() {
        GenerateRepository g = GenerateRepository.create(
                RepositoryContent.builder()
                        .packageName("data.test")
                        .className("TestRepository")
                        .classInterface("TestInterface")
                        .addImport("java.util.Optional")
                        .addGetMethod(
                                RepositoryContent.GetMethod.builder()
                                        .name("getByNameAndType")
                                        .type("Optional<String>")
                                        .addParameter("name", "String")
                                        .addParameter("type", "String")
                                        .build()
                        )
                        .build(),
                "target"/*"/generated-test-sources"*/);
        g.generate();
    }





}
