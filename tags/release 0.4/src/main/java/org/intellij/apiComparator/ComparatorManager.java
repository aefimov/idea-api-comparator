package org.intellij.apiComparator;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diff.DiffManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.apiComparator.actions.RunComparatorAction;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Comparator component
 *
 * @author Alexey Efimov
 */
public class ComparatorManager implements ApplicationComponent {
    /**
     * Diff tool
     */
    private ComparatorDiffTool diffTool = new ComparatorDiffTool();

    /**
     * Supported file extensions
     */
    @NonNls
    private static final String[] SUPPORTED_EXTENSIONS = {"jar"};

    @NotNull
    public String getComponentName() {
        return "APIComparator.Manager";
    }

    public void initComponent() {
        // Tools action
        ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup toolsMenu = (DefaultActionGroup) actionManager.getAction("ToolsMenu");
        AnAction runComparator = new RunComparatorAction();
        actionManager.registerAction(RunComparatorAction.ID, runComparator);
        toolsMenu.add(runComparator, new Constraints(Anchor.LAST, null));

        // Diff tool
        DiffManager.getInstance().registerDiffTool(diffTool);
    }

    public void disposeComponent() {
        // Diff tool
        DiffManager.getInstance().unregisterDiffTool(diffTool);

        // Action
        ActionManager actionManager = ActionManager.getInstance();
        AnAction action = actionManager.getAction(RunComparatorAction.ID);
        actionManager.unregisterAction(RunComparatorAction.ID);

        DefaultActionGroup toolsMenu = (DefaultActionGroup) actionManager.getAction("ToolsMenu");
        toolsMenu.remove(action);
    }

    /**
     * Method check, that such path can be used in this plugin for comparison
     *
     * @param path Path to check
     * @return <code>true</code> if path is valid
     */
    public boolean isValidPath(String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                // Is diretory?
                if (file.isDirectory()) {
                    // Temparary not supported
                    return false;
                }
                if (file.isFile() && file.canRead()) {
                    // Check file extension
                    String extension = "";
                    String[] parts = file.getName().split("\\.");
                    if (parts.length > 0) {
                        extension = parts[parts.length - 1];
                    }
                    for (String ext : SUPPORTED_EXTENSIONS) {
                        if (ext.equalsIgnoreCase(extension)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static ComparatorManager getInstance() {
        return ApplicationManager.getApplication().getComponent(ComparatorManager.class);
    }

    /**
     * Check that file can be used in Comparator
     *
     * @param file File
     * @return <code>true</code> if file can be compared
     */
    public boolean isValidFile(VirtualFile file) {
        return file != null && isValidPath(file.getPath().replace('/', File.separatorChar));
    }
}
