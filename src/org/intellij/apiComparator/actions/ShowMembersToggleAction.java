package org.intellij.apiComparator.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.intellij.apiComparator.ComparatorConfiguration;
import org.intellij.apiComparator.Plugin;
import org.intellij.apiComparator.spi.markup.TreeItemType;
import org.intellij.apiComparator.spi.nodes.TreeItem;
import org.intellij.apiComparator.spi.nodes.TreeItemFilter;
import org.intellij.apiComparator.tree.TreeItemModel;
import org.phantom.swing.IconLoader;

import javax.swing.*;

/**
 * Changes only/Full tree
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 */
public class ShowMembersToggleAction extends ToggleAction {
    /**
     * Configuration
     */
    private static final ComparatorConfiguration configuration = ComparatorConfiguration.getInstance();

    /**
     * Filter
     */
    private TreeItemFilter filter = new TreeItemFilter() {
        public boolean accept(TreeItem item) {
            TreeItemType type = item.getType();
            return type == null || !type.isMember();
        }
    };

    /**
     * Tree
     */
    private JTree tree;

    public ShowMembersToggleAction(JTree tree) {
        super(
            Plugin.localizer.getString("comparator.toolbar.actions.showmembers.text"),
            Plugin.localizer.getString("comparator.toolbar.actions.showmembers.description"),
            IconLoader.getIcon("/objectBrowser/showMembers.png")
        );

        this.tree = tree;

        if (!configuration.isShowMembers()) {
            ((TreeItemModel)tree.getModel()).addFilter(filter);
        }
    }

    public boolean isSelected(AnActionEvent e) {
        return configuration.isShowMembers();
    }

    public void setSelected(AnActionEvent e, boolean state) {
        configuration.setShowMembers(state);
        if (state) {
            ((TreeItemModel)tree.getModel()).removeFilter(filter);
        } else {
            ((TreeItemModel)tree.getModel()).addFilter(filter);
        }
        tree.updateUI();
    }
}
