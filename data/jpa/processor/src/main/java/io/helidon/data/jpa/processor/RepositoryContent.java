package io.helidon.data.jpa.processor;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class RepositoryContent {

    private final String packageName;
    private final String className;
    private final String classInterface;
    private final List<String> imports;
    private final List<GetMethod> getMethods;

    private RepositoryContent(
            String packageName,
            String className,
            String classInterface,
            List<String> imports,
            List<GetMethod> getMethods
    ) {
        Objects.requireNonNull(packageName, "Name of the package shall not be null.");
        Objects.requireNonNull(className, "Name of the class shall not be null.");
        Objects.requireNonNull(classInterface, "Name of the implemented interface shall not be null.");
        Objects.requireNonNull(imports, "List of class imports shall not be null.");
        Objects.requireNonNull(getMethods, "List of finder get methods shall not be null.");
        this.packageName = packageName;
        this.className = className;
        this.classInterface = classInterface;
        this.imports = imports;
        this.getMethods = getMethods;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getClassInterface() {
        return classInterface;
    }

    public List<String> getImports() {
        return imports;
    }

    public List<GetMethod> getGetMethods() {
        return getMethods;
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder implements io.helidon.common.Builder<Builder, RepositoryContent> {
        private String packageName;
        private String className;
        private String classInterface;
        private final List<String> imports;
        private final List<GetMethod> getMethods;

        private Builder() {
            this.packageName = null;
            this.className = null;
            this.classInterface = null;
            this.imports = new LinkedList<>();
            this.getMethods = new LinkedList<>();
        }

        @Override
        public RepositoryContent build() {
            return new RepositoryContent(
                    packageName,
                    className,
                    classInterface,
                    List.copyOf(imports),
                    List.copyOf(getMethods)
            );
        }

        Builder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }


        Builder className(String className) {
            this.className = className;
            return this;
        }

        Builder classInterface(String classInterface) {
            this.classInterface = classInterface;
            return this;
        }

        Builder addImport(String value) {
            imports.add(value);
            return this;
        }

        Builder addGetMethod(GetMethod method) {
            getMethods.add(method);
            return this;
        }

    }

    public static class GetMethod {

        private final String name;
        private final String type;
        private final List<Parameter> parameters;

        private GetMethod(String name, String type, List<Parameter> parameters) {
            this.name = name;
            this.type = type;
            this.parameters = parameters;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public List<Parameter> getParameters() {
            return parameters;
        }

        static Builder builder() {
            return new Builder();
        }

        static final class Builder implements io.helidon.common.Builder<Builder, GetMethod> {
            private String name;
            private String type;
            private final List<Parameter> parameters;

            private Builder() {
                this.name = null;
                this.type = null;
                this.parameters = new LinkedList<>();
            }

            @Override
            public GetMethod build() {
                return new GetMethod(name, type, List.copyOf(parameters));
            }

            Builder name(String name) {
                this.name = name;
                return this;
            }

            Builder type(String type) {
                this.type = type;
                return this;
            }

            Builder addParameter(String name, String type) {
                parameters.add(new Parameter(name, type, parameters.isEmpty()));
                return this;
            }

        }

        static class Parameter {

            private final String name;
            private final String type;
            private final boolean first;

            private Parameter(String name, String type, boolean first) {
                this.name = name;
                this.type = type;
                this.first = first;
            }

            public String getName() {
                return name;
            }

            public String getType() {
                return type;
            }

            public boolean isFirst() {
                return first;
            }

        }

    }

}
