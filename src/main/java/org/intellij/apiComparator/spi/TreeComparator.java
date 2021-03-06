package org.intellij.apiComparator.spi;

import org.intellij.apiComparator.spi.markup.TreeItemMarker;
import org.intellij.apiComparator.spi.nodes.TreeItem;

import java.util.List;

/**
 * Default Tree Comparator.
 *
 * @author Alexey Efimov
 */
public class TreeComparator {
    /**
     * Comparison for tree roots. All {@link TreeItem}s will be copied into Marked Tree with {@link
     * org.intellij.apiComparator.spi.markup.TreeItemMarker}. You can check that mark target insure what is changed or
     * not. The source and target is assumed mathed items, so it be a true, that items are mathed, but they may have
     * changed flag after comparison.
     *
     * @param source Source root tree item
     * @param target Target root tree item
     * @return Comparison result merged tree
     */
    public static TreeItem compare(TreeItem source, TreeItem target) {
        // Check for roots
        if (source.equals(target)) {
            source.setMarker(TreeItemMarker.NOTCHANGED);
        } else {
            source.setMarker(TreeItemMarker.CHANGED);
        }
        // Comparable child list
        List targetChildren = target.getChildren();
        // Search by source items
        List sourceChildren = source.getChildren();
        for (Object aSourceChildren : sourceChildren) {
            TreeItem sourceItem = (TreeItem) aSourceChildren;
            TreeItem targetItem = null;
            boolean isMatched = false;
            for (int j = 0; j < targetChildren.size() && !isMatched; j++) {
                targetItem = (TreeItem) targetChildren.get(j);
                isMatched = sourceItem.matched(targetItem);
                if (isMatched) {
                    // Add mathed comparing tree
                    compare(sourceItem, targetItem);
                }
            }
            if (isMatched) {
                // Remove source next search
                targetChildren.remove(targetItem);
            } else {
                // Create the 'removed' marked item
                sourceItem.setMarker(TreeItemMarker.REMOVED);

                // Add all children target this marked item
                markupDown(sourceItem);
            }
        }
        // Now add all not mathed items source comparable list
        for (Object aTargetChildren : targetChildren) {
            TreeItem targetItem = (TreeItem) aTargetChildren;
            // Create the 'add' marked item
            targetItem.setMarker(TreeItemMarker.ADDED);
            source.addChild(targetItem);

            // Add all children target this marked item
            markupDown(targetItem);
        }
        return source;
    }

    private static void markupDown(TreeItem item) {
        List children = item.getChildren();
        for (Object aChildren : children) {
            TreeItem child = (TreeItem) aChildren;
            child.setMarker(item.getMarker());

            // Mark recursive
            markupDown(child);
        }
    }
}
