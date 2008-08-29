package org.intellij.apiComparator.spi.markup;

import java.text.MessageFormat;

/**
 * The mark for markering comparison results.
 *
 * @author Alexey Efimov
 */
public enum TreeItemMarker {
    /**
     * Not changed mark
     */
    NOTCHANGED,

    /**
     * Changed mark
     */
    CHANGED,

    /**
     * Mark of new added item
     */
    ADDED,

    /**
     * Mark of removed item
     */
    REMOVED;

    public static TreeItemMarker valueOf(int value) {
        for (TreeItemMarker marker : TreeItemMarker.values()) {
            if (marker.ordinal() == value) {
                return marker;
            }
        }
        throw new IllegalArgumentException(MessageFormat.format("can''t get enum for value {0}", value));
    }
}
