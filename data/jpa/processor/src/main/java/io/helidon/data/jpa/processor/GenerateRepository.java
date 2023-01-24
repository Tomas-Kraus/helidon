package io.helidon.data.jpa.processor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

import io.helidon.data.DataException;

public class GenerateRepository {

    private static final String TEMPLATE_PATH = "/";
    private static final String TEMPLATE_SUFFIX = ".java.hbs";
    private static final TemplateLoader TL = initTemplateLoader();
    private static final Handlebars HB = new Handlebars(TL);

    private final RepositoryContent content;
    private final File target;

    public static GenerateRepository create(RepositoryContent content, String path) {
        return new GenerateRepository(content, path);
    }

    private GenerateRepository(RepositoryContent content, String path) {
        this.content = content;
        this.target = new File(path);
    }

    void generate() {
        verifyTargetPath(target);
        try (FileWriter fw = new FileWriter(classFilePath())) {
            Template t = HB.compile("Repository");
            t.apply(content, fw);
        } catch (IOException e) {
            throw new DataException("Repository class generation failed.", e);
        }
    }

    private File classFilePath() {
        String pkgPath = content.getPackageName().replace('.', '/');
        String classFile = content.getClassName() + ".java";
        File path = new File(target, pkgPath);
        verifyTargetPath(path);
        return new File(path, classFile);
    }

    private static TemplateLoader initTemplateLoader() {
        return new ClassPathTemplateLoader(TEMPLATE_PATH, TEMPLATE_SUFFIX);
    }

    private static void verifyTargetPath(File target) {
        if (!target.exists()) {
            if (!target.mkdirs()) {
                throw new DataException("Repository class file path creation failed.");
            }
        } else {
            if (!target.isDirectory()) {
                throw new DataException("Repository class file path is not a directory.");
            }
            if (!target.canWrite()) {
                throw new DataException("Repository class file path is not writeable.");
            }
        }
    }

}
