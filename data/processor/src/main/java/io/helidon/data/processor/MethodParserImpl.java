package io.helidon.data.processor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import io.helidon.data.runtime.DynamicFinder;

class MethodParserImpl extends MethodParser {

    // Parser for the selection part of the method name.
    private final MethodSelectionParser selectionParser;
    // Parser for the criteria part of the method name.
    private final MethodCriteriaParser criteriaParser;
    // Parser for the order part of the method name.
    private final MethodOrderParser orderParser;
    // Properties are sorted from the longest String to the shortest.
    // This is required for parser state machine builder algorithm.
    private final List<String> sortedEntityProperties;
    // Run reset method on 2nd and later usage of parse method.
    // 1st usage does nothing, but triggers reset for subsequent runs.
    private Runnable reset;

    /**
     * Creates an instance of data repository query method parser.
     */
    MethodParserImpl(List<String> entityProperties) {
        this.sortedEntityProperties = entityProperties.stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .collect(Collectors.toList());
        this.selectionParser = new MethodSelectionParser(DynamicFinderBuilder.builder(), sortedEntityProperties);
        this.criteriaParser = new MethodCriteriaParser(sortedEntityProperties);
        this.orderParser = new MethodOrderParser(entityProperties);
        this.reset = this::firstReset;
    }

    // Skip reset on 1st call of parse method
    private void firstReset() {
        reset = this::nextReset;
    }

    // Reset parser to be used for another method name parsing. Active from 2nd call of parse method.
    private void nextReset() {
        selectionParser.reset(DynamicFinderBuilder.builder());
        criteriaParser.reset();
        orderParser.reset();
    }

    /**
     * Parse provided method name with provided arguments.
     *
     * @param methodName      name of the repository query method to be parsed
     * @param methodArguments {@link List} of method arguments in the same order as in method prototype
     * @return Helidon dynamic finder query
     */
    DynamicFinder parse(String methodName, List<String> methodArguments) {
        ParserContext context = new ParserContext(methodName);
        reset.run();
        ParserState.FinalState finalState = selectionParser.parse(context);
        switch (finalState) {
            case BY -> {
                finalState = criteriaParser.parse(selectionParser.selectionBuilder(), context, methodArguments);
                if (finalState == ParserState.FinalState.ORDER_BY) {
                    finalState = orderParser.parse(criteriaParser.criteriaBuilder(), context);
                }
            }
            case ORDER_BY -> finalState = orderParser.parse(selectionParser.selectionBuilder(), context);
        }
        if (finalState == ParserState.FinalState.END) {
            return context.query();
        }
        return null;
    }

}
