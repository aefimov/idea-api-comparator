package org.intellij.apiComparator.tree;

import org.intellij.apiComparator.spi.markup.TreeItemType;
import org.intellij.apiComparator.spi.nodes.TreeItem;
import org.intellij.apiComparator.spi.nodes.TreeItemAttributes;
import org.intellij.apiComparator.spi.nodes.TreeItemFilter;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Tree model for {@link TreeItem}
 *
 * @author Alexey Efimov
 */
public class TreeItemModel implements TreeModel {
    /**
     * Tree model listeners
     */
    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

    /**
     * Tree root item
     */
    private TreeItem root;

    /**
     * Filters
     */
    private List<TreeItemFilter> filters = new ArrayList<TreeItemFilter>();

    public void setRoot(TreeItem root) {
        this.root = root;
        rebuildFilteredChildren(root);
        sort();
    }

    private void sort() {
        if (root != null) {
            root.sort(treeComparator);
        }
    }

    /**
     * Comparator
     */
    private static final Comparator<TreeItem> treeComparator = new Comparator<TreeItem>() {
        public int compare(TreeItem t1, TreeItem t2) {
            if (t1 == null && t2 != null) {
                return 1;
            }
            if (t2 == null && t1 != null) {
                return -1;
            }
            if (t1 == null) {
                return 0;
            }

            TreeItemType type1 = t1.getType();
            TreeItemType type2 = t2.getType();
            if (type1 != null && type2 != null) {
                int result = type1.compareTo(type2);
                if (result != 0) {
                    return result;
                }
            }

            String name1 = (String) t1.getAttributeValue(TreeItemAttributes.ATTR_NAME);
            String name2 = (String) t2.getAttributeValue(TreeItemAttributes.ATTR_NAME);
            return name1 != null && name2 != null ? name1.compareTo(name2) : 0;
        }

        public boolean equals(Object obj) {
            return obj == this;
        }
    };

    public Object getRoot() {
        return root;
    }

    public int getChildCount(Object parent) {
        if (filters.size() > 0) {
            return ((TreeItem) parent).getFilteredChildren().size();
        } else {
            return ((TreeItem) parent).getChildren().size();
        }
    }

    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    public void addTreeModelListener(TreeModelListener l) {
        if (!listeners.contains(l)) {
            listeners.add(l);
        }
    }

    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    public Object getChild(Object parent, int index) {
        if (filters.size() > 0) {
            return ((TreeItem) parent).getFilteredChildren().get(index);
        } else {
            return ((TreeItem) parent).getChildren().get(index);
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        TreeItem childItem = (TreeItem) child;
        if (filters.size() > 0) {
            return ((TreeItem) parent).getFilteredChildren().indexOf(childItem);
        } else {
            return ((TreeItem) parent).getChildren().indexOf(childItem);
        }
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        for (TreeModelListener listener1 : listeners) {
            TreeModelEvent event = new TreeModelEvent(newValue, path);
            listener1.treeNodesChanged(event);
        }
    }

    public void addFilter(TreeItemFilter filter) {
        if (!filters.contains(filter)) {
            filters.add(filter);
            rebuildFilteredChildren(root);
            sort();
        }
    }

    public void removeFilter(TreeItemFilter filter) {
        filters.remove(filter);
        rebuildFilteredChildren(root);
        sort();
    }

    private void rebuildFilteredChildren(TreeItem item) {
        if (item != null) {
            List<TreeItem> children = item.getChildren();
            List<TreeItem> filteredChildren = item.getFilteredChildren();
            filteredChildren.clear();
            for (TreeItem aChildren : children) {
                if (isFiltersAccept(aChildren)) {
                    filteredChildren.add(aChildren);
                    rebuildFilteredChildren(aChildren);
                }
            }
        }
    }

    /**
     * Return true if one or more filters accept {@link TreeItem}.
     */
    private boolean isFiltersAccept(TreeItem item) {
        for (TreeItemFilter filter1 : filters) {
            if (!filter1.accept(item)) {
                return false;
            }
        }
        return filters.size() > 0;
    }
}
