package org.intellij.apiComparator;

import com.intellij.openapi.diff.DiffContent;
import com.intellij.openapi.diff.DiffRequest;
import com.intellij.openapi.diff.DiffTool;

/**
 * API comparator diff tool.
 *
 * @author Alexey Efimov
 */
public class ComparatorDiffTool implements DiffTool {
    public void show(DiffRequest request) {
        ComparatorDialog dialog = new ComparatorDialog(request.getProject());
        DiffContent[] contents = request.getContents();
        dialog.setFromPathContent(contents[0]);
        if (contents.length > 1) {
            dialog.setToPathContent(contents[1]);
        }
        dialog.show();
    }

    public boolean canShow(DiffRequest request) {
        ComparatorManager comparator = ComparatorManager.getInstance();

        if (request != null) {
            DiffContent[] contents = request.getContents();
            if (contents != null) {
                for (int i = 0; i < contents.length; i++) {
                    if (!comparator.isValidFile(contents[i].getFile())) {
                        return false;
                    }
                }
                return contents.length > 0;
            }
        }
        return false;
    }
}
