package org.intellij.apiComparator.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.util.IconLoader;
import org.intellij.apiComparator.ComparatorConfiguration;
import org.intellij.apiComparator.spi.markup.TreeItemMarker;
import org.intellij.apiComparator.spi.nodes.TreeItem;
import org.intellij.apiComparator.spi.nodes.TreeItemFilter;
import org.intellij.apiComparator.tree.TreeItemModel;
import org.intellij.apiComparator.util.APIComparatorBundle;

import javax.swing.*;
import java.util.List;

/**
 * Hide changed items in tree.
 *
 * @author Alexey Efimov
 */
public class HideChangedToggleAction extends ToggleAction {
    /**
     * Configuration
     */
    private static final ComparatorConfiguration configuration = ComparatorConfiguration.getInstance();

    /**
     * Filter
     */
    private TreeItemFilter filter = new TreeItemFilter() {
        public boolean accept(TreeItem item) {
            boolean result = !TreeItemMarker.CHANGED.equals(item.getMarker());
            if (!result) {
                // Check children
                List children = item.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    TreeItem child = (TreeItem) children.get(i);
                    if (TreeItemMarker.CHANGED.equals(child.getMarker()) && accept(child)) {
                        return true;
                    } else if (
                            (!configuration.isShowChangesOnly() && (child.getMarker() == null || TreeItemMarker.NOTCHANGED.equals(child.getMarker()))) ||
                                    (!configuration.isHideAdded() && TreeItemMarker.ADDED.equals(child.getMarker())) ||
                                    (!configuration.isHideRemoved() && TreeItemMarker.REMOVED.equals(child.getMarker()))) {
                        return true;
                    }
                }
            }
            return result;
        }
    };

    /**
     * Tree
     */
    private JTree tree;

    public HideChangedToggleAction(JTree tree) {
        super(
                APIComparatorBundle.message("comparator.toolbar.actions.hidechanged.text"),
                APIComparatorBundle.message("comparator.toolbar.actions.hidechanged.description"),
                IconLoader.getIcon("/org/intellij/apiComparator/resources/hidechanged.png")
        );

        this.tree = tree;

        if (configuration.isHideChanged()) {
            ((TreeItemModel) tree.getModel()).addFilter(filter);
        }
    }

    public boolean isSelected(AnActionEvent e) {
        return configuration.isHideChanged();
    }

    public void setSelected(AnActionEvent e, boolean state) {
        configuration.setHideChanged(state);
        if (state) {
            ((TreeItemModel) tree.getModel()).addFilter(filter);
        } else {
            ((TreeItemModel) tree.getModel()).removeFilter(filter);
        }
        tree.updateUI();
    }
}
