package org.intellij.apiComparator.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import org.intellij.apiComparator.ComparatorDialog;
import org.intellij.apiComparator.util.APIComparatorBundle;

/**
 * Run API comparator tool.
 *
 * @author Alexey Efimov
 */
public class RunComparatorAction extends AnAction {
    /**
     * Run comparator action ID
     */
    public static final String ID = "RunComparator";

    public RunComparatorAction() {
        super(APIComparatorBundle.message("comparator.actions.runComparator"));
    }

    public void actionPerformed(AnActionEvent e) {
        ComparatorDialog comparatorDialog = new ComparatorDialog(
                DataKeys.PROJECT.getData(e.getDataContext())
        );
        comparatorDialog.show();
    }
}
