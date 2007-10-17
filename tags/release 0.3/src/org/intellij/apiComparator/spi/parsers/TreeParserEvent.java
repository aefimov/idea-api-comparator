package org.intellij.apiComparator.spi.parsers;

import java.util.EventObject;

/**
 * Events will send to {@link TreeParserListener} while parsing process.
 *
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
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
     */
    public TreeParser getParser() {
        return parser;
    }
}
