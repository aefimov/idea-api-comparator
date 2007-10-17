package org.intellij.apiComparator.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.intellij.apiComparator.ComparatorConfiguration;
import org.intellij.apiComparator.Plugin;
import org.intellij.apiComparator.spi.nodes.TreeItem;
import org.intellij.apiComparator.spi.nodes.TreeItemFilter;
import org.intellij.apiComparator.spi.markup.TreeItemMarker;
import org.intellij.apiComparator.tree.TreeItemModel;
import org.phantom.swing.IconLoader;

import javax.swing.*;

/**
 * Hide removed items in tree.
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 */
public class HideRemovedToggleAction extends ToggleAction {
    /**
     * Configuration
     */
    private static final ComparatorConfiguration configuration = ComparatorConfiguration.getInstance();

    /**
     * Filter
     */
    private TreeItemFilter filter = new TreeItemFilter() {
        public boolean accept(TreeItem item) {
            return !TreeItemMarker.REMOVED.equals(item.getMarker());
        }
    };

    /**
     * Tree
     */
    private JTree tree;

    public HideRemovedToggleAction(JTree tree) {
        super(
            Plugin.localizer.getString("comparator.toolbar.actions.hideremoved.text"),
            Plugin.localizer.getString("comparator.toolbar.actions.hideremoved.description"),
            IconLoader.getIcon("/org/intellij/apiComparator/resources/hideremoved.png")
        );

        this.tree = tree;

        if (configuration.isHideRemoved()) {
            ((TreeItemModel)tree.getModel()).addFilter(filter);
        }
    }

    public boolean isSelected(AnActionEvent e) {
        return configuration.isHideRemoved();
    }

    public void setSelected(AnActionEvent e, boolean state) {
        configuration.setHideRemoved(state);
        if (state) {
            ((TreeItemModel)tree.getModel()).addFilter(filter);
        } else {
            ((TreeItemModel)tree.getModel()).removeFilter(filter);
        }
        tree.updateUI();
    }
}
