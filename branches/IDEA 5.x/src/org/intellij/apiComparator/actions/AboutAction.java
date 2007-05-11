package org.intellij.apiComparator.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.intellij.apiComparator.Plugin;
import org.intellij.apiComparator.about.AboutManager;
import org.phantom.swing.IconLoader;

/**
 * Clear recent list
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 */
public class AboutAction extends AnAction {
    public AboutAction() {
        super(
            Plugin.localizer.getString("comparator.toolbar.actions.about.text"),
            Plugin.localizer.getString("comparator.toolbar.actions.about.description"),
            IconLoader.getIcon("/actions/help.png")
        );
    }

    public void actionPerformed(AnActionEvent e) {
        AboutManager aboutManager = AboutManager.getInstance();
        aboutManager.getAboutAction().actionPerformed(e);
    }
}
