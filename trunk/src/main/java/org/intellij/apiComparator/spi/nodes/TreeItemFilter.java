package org.intellij.apiComparator.spi.nodes;

/**
 * Filter for tree model
 *
 * @author Alexey Efimov
 */
public interface TreeItemFilter {
    /**
     * Method must return true if {@link TreeItem} must be accepted
     *
     * @param item Checking item
     */
    public boolean accept(TreeItem item);
}
