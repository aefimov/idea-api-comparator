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

/**
 * Hide added items in tree.
 *
 * @author Alexey Efimov
 */
public class HideAddedToggleAction extends ToggleAction {
    /**
     * Configuration
     */
    private static final ComparatorConfiguration configuration = ComparatorConfiguration.getInstance();

    /**
     * Filter
     */
    private TreeItemFilter filter = new TreeItemFilter() {
        public boolean accept(TreeItem item) {
            return !TreeItemMarker.ADDED.equals(item.getMarker());
        }
    };

    /**
     * Tree
     */
    private JTree tree;

    public HideAddedToggleAction(JTree tree) {
        super(
                APIComparatorBundle.message("comparator.toolbar.actions.hideadded.text"),
                APIComparatorBundle.message("comparator.toolbar.actions.hideadded.description"),
                IconLoader.getIcon("/org/intellij/apiComparator/resources/hideadded.png")
        );

        this.tree = tree;

        if (configuration.isHideAdded()) {
            ((TreeItemModel) tree.getModel()).addFilter(filter);
        }
    }

    public boolean isSelected(AnActionEvent e) {
        return configuration.isHideAdded();
    }

    public void setSelected(AnActionEvent e, boolean state) {
        configuration.setHideAdded(state);
        if (state) {
            ((TreeItemModel) tree.getModel()).addFilter(filter);
        } else {
            ((TreeItemModel) tree.getModel()).removeFilter(filter);
        }
        tree.updateUI();
    }
}
