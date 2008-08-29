package org.intellij.apiComparator.spi.parsers;

/**
 * Listener for parsing events.
 *
 * @author Alexey Efimov
 */
public interface TreeParserListener {
    /**
     * Event for handling start event. Event fired then parser is starting. The source of event is source object to be
     * parsed.
     *
     * @param event Event
     */
    void start(TreeParserEvent event);

    /**
     * Event for handling parsing next item event. Event fired then parser going to next tree item creation. The source
     * of event is current item from parser.
     *
     * @param event Event
     */
    void next(TreeParserEvent event);

    /**
     * Event for handling complete event. Event fired then parser complete parsing. The source of event is root {@link
     * org.intellij.apiComparator.spi.nodes.TreeItem} (the parsing result).
     *
     * @param event Event
     */
    void complete(TreeParserEvent event);
}
