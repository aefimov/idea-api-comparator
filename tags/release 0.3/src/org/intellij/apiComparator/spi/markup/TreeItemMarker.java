package org.intellij.apiComparator.spi.markup;

import org.phantom.lang.Enum;

/**
 * The mark for markering comparison results.
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 */
public final class TreeItemMarker extends Enum {
    /**
     * Not changed mark
     */
    public static final TreeItemMarker NOTCHANGED = new TreeItemMarker(0);

    /**
     * Changed mark
     */
    public static final TreeItemMarker CHANGED = new TreeItemMarker(1);

    /**
     * Mark of new added item
     */
    public static final TreeItemMarker ADDED = new TreeItemMarker(2);

    /**
     * Mark of removed item
     */
    public static final TreeItemMarker REMOVED = new TreeItemMarker(3);

    private TreeItemMarker(int i) {
        super(new Integer(i));
    }

    public static TreeItemMarker parseInt(int value) {
        return (TreeItemMarker)findValue(TreeItemMarker.class, new Integer(value));
    }
}
