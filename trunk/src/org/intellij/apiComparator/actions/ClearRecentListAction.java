package org.intellij.apiComparator.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.intellij.apiComparator.ComparatorConfiguration;
import org.intellij.apiComparator.Plugin;
import org.phantom.swing.IconLoader;

/**
 * Clear recent list
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 */
public class ClearRecentListAction extends AnAction {
    /**
     * Configuration
     */
    private static final ComparatorConfiguration configuration = ComparatorConfiguration.getInstance();

    public ClearRecentListAction() {
        super(
            Plugin.localizer.getString("comparator.toolbar.actions.clearrecentlist.text"),
            Plugin.localizer.getString("comparator.toolbar.actions.clearrecentlist.description"),
            IconLoader.getIcon("/actions/reset.png")
        );
    }

    public void actionPerformed(AnActionEvent e) {
        configuration.clearRecentEntries();
    }
}
