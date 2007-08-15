package org.intellij.apiComparator.spi.parsers;

import java.util.EventObject;

/**
 * Events will send to {@link TreeParserListener} while parsing process.
 *
 * @author Alexey Efimov
 */
public class TreeParserEvent extends EventObject {
    /**
     * Tree parser
     */
    private TreeParser parser;

    public TreeParserEvent(TreeParser parser, Object source) {
        super(source);
        this.parser = parser;
    }

    /**
     * Return parser sended this event
     *
     * @return Tree parser
     */
    public TreeParser getParser() {
        return parser;
    }
}
