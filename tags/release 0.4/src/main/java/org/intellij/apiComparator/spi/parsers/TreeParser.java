package org.intellij.apiComparator.spi.parsers;

import org.intellij.apiComparator.spi.nodes.TreeItem;

/**
 * The tree loader to load tree structure from object
 *
 * @author Alexey Efimov
 */
public interface TreeParser {
    /**
     * Loading tree from source object.
     *
     * @return Tree
     */
    public TreeItem parse();

    /**
     * @return Size of items to be next in source object
     */
    public int getSourceSize();

    /**
     * Must called after parsing started. If parsing is not started, then {@link IllegalStateException} must be thrown.
     *
     * @return Curent parsing item index
     */
    public int getCurrentIndex();

    /**
     * Must called after parsing started. If parsing is not started, then {@link IllegalStateException} must be thrown.
     *
     * @return Curent parsing item
     */
    public TreeItem getCurrentItem();

    /**
     * Must called after parsing complete. If parsing is not complete, then {@link IllegalStateException} must be
     * thrown.
     *
     * @return Parsed {@link TreeItem} root
     */
    public TreeItem getResult();

    /**
     * Add listener
     *
     * @param listener Listener
     */
    public void addTreeParserListener(TreeParserListener listener);

    /**
     * Remove listener
     *
     * @param listener Listener
     */
    public void removeTreeParserListener(TreeParserListener listener);
}
