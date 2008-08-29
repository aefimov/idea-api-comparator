package org.intellij.apiComparator.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.IconLoader;
import org.intellij.apiComparator.ComparatorConfiguration;
import org.intellij.apiComparator.util.APIComparatorBundle;

/**
 * Clear recent list
 *
 * @author Alexey Efimov
 */
public class ClearRecentListAction extends AnAction {
    /**
     * Configuration
     */
    private static final ComparatorConfiguration configuration = ComparatorConfiguration.getInstance();

    public ClearRecentListAction() {
        super(
                APIComparatorBundle.message("comparator.toolbar.actions.clearrecentlist.text"),
                APIComparatorBundle.message("comparator.toolbar.actions.clearrecentlist.description"),
                IconLoader.getIcon("/actions/reset.png")
        );
    }

    public void actionPerformed(AnActionEvent e) {
        configuration.clearRecentEntries();
    }
}
