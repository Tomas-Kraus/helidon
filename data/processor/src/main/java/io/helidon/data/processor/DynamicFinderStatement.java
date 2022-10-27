package io.helidon.data.processor;

import java.util.List;

// Purpose of this class is just separation of public API from TransformQuery.
// Public methods are needed only to retrieve result of target platform transformation.
/**
 * Dynamic finder statement translated to target query String,
 */
public interface DynamicFinderStatement {

    /**
     * Return target query language statement.
     *
     * @return statement build from AST content.
     */
    String statement();

    /**
     * Return list of query settings to be applied.
     *
     * @return query settings build from AST content
     */
    List<String> querySettings() ;

}
