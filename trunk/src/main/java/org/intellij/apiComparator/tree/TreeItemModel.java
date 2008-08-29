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
    private List listeners = new ArrayList();

    /**
     * Tree root item
     */
    private TreeItem root;

    /**
     * Filters
     */
    private List filters = new ArrayList();

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
    private static final Comparator treeComparator = new Comparator() {
        public int compare(Object o1, Object o2) {
            TreeItem t1 = (TreeItem) o1;
            TreeItem t2 = (TreeItem) o2;
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
            return (TreeItem) ((TreeItem) parent).getFilteredChildren().get(index);
        } else {
            return (TreeItem) ((TreeItem) parent).getChildren().get(index);
        }
    }

    public int getIndexOfChild(Object parent, Object child) {
        if (filters.size() > 0) {
            return ((TreeItem) parent).getFilteredChildren().indexOf(child);
        } else {
            return ((TreeItem) parent).getChildren().indexOf(child);
        }
    }

    public void valueForPathChanged(TreePath path, Object newValue) {
        for (Object listener1 : listeners) {
            TreeModelListener listener = (TreeModelListener) listener1;
            TreeModelEvent event = new TreeModelEvent(newValue, path);
            listener.treeNodesChanged(event);
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
            List children = item.getChildren();
            List filteredChildren = item.getFilteredChildren();
            filteredChildren.clear();
            for (Object aChildren : children) {
                TreeItem child = (TreeItem) aChildren;
                if (isFiltersAccept(child)) {
                    filteredChildren.add(child);
                    rebuildFilteredChildren(child);
                }
            }
        }
    }

    /**
     * Return true if one or more filters accept {@link TreeItem}.
     */
    private boolean isFiltersAccept(TreeItem item) {
        for (Object filter1 : filters) {
            TreeItemFilter filter = (TreeItemFilter) filter1;
            if (!filter.accept(item)) {
                return false;
            }
        }
        return filters.size() > 0;
    }
}
