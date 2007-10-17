package org.intellij.apiComparator.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;
import org.intellij.apiComparator.ComparatorDialog;
import org.intellij.apiComparator.Plugin;

/**
 * Run API comparator tool.
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 */
public class RunComparatorAction extends AnAction {
    /**
     * Run comparator action ID
     */
    public static final String ID = "RunComparator";

    public RunComparatorAction() {
        super(Plugin.localizer.getString("comparator.actions.runComparator"));
    }

    public void actionPerformed(AnActionEvent e) {
        ComparatorDialog comparatorDialog = new ComparatorDialog(
            (Project)e.getDataContext().getData(DataConstants.PROJECT)
        );
        comparatorDialog.show();
    }
}
