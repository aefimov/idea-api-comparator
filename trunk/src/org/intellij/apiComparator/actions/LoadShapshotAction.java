/* $Id$ */
package org.intellij.apiComparator.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import org.intellij.apiComparator.FileTypeFilter;
import org.intellij.apiComparator.Plugin;
import org.intellij.apiComparator.spi.nodes.TreeItem;
import org.intellij.apiComparator.tree.TreeItemModel;
import org.jdom.Document;
import org.jdom.Element;
import org.phantom.swing.IconLoader;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * Create snapshot from compared tree.
 *
 * @author <a href="mailto:aefimov@tengry.com">Alexey Efimov</a>
 * @version $Revision$
 */
public class LoadShapshotAction extends AnAction {
    /**
     * Tree
     */
    private JTree tree;

    public LoadShapshotAction(JTree tree) {
        super(
            Plugin.localizer.getString("comparator.toolbar.actions.loadSnapshot.text"),
            Plugin.localizer.getString("comparator.toolbar.actions.loadSnapshot.description"),
            IconLoader.getIcon("/actions/get.png")
        );
        this.tree = tree;
    }

    public void actionPerformed(AnActionEvent e) {
        final TreeItemModel model = (TreeItemModel)tree.getModel();
        // Standard action
        JFileChooser jfc = new JFileChooser();
        jfc.addChoosableFileFilter(
            new FileTypeFilter(FileType.ARCHIVE, "comparator.fileChooser.snapshot.filter.zip.description")
        );
        jfc.addChoosableFileFilter(
            new FileTypeFilter(FileType.XML, "comparator.fileChooser.snapshot.filter.xml.description")
        );
        jfc.setDialogTitle(Plugin.localizer.getString("comparator.fileChooser.snapshot.open.title"));
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);

        if (jfc.showOpenDialog(tree) == JFileChooser.APPROVE_OPTION) {
            final File file = jfc.getSelectedFile();
            final FileType fileType = ((FileTypeFilter)jfc.getFileFilter()).getFileType();
            if (file != null) {
                if (!file.exists() || file.isDirectory() || (file.exists() && !file.canRead())) {
                    Messages.showWarningDialog(
                        Plugin.localizer.format(
                            "comparator.fileChooser.snapshot.open.warning.message", file.getPath()
                        ),
                        Plugin.localizer.getString("comparator.fileChooser.snapshot.open.title")
                    );
                } else {
                    // Write file
                    try {
                        ApplicationManager.getApplication().runProcessWithProgressSynchronously(
                            new Runnable() {
                                public void run() {
                                    InputStream inputStream = null;
                                    try {
                                        ProgressManager progressManager = ProgressManager.getInstance();
                                        ProgressIndicator indicator = progressManager.getProgressIndicator();

                                        indicator.setText(
                                            Plugin.localizer.format(
                                                "comparator.loadsnapshot.progress.loading",
                                                new Object[]{file.getPath()}
                                            )
                                        );

                                        inputStream = new FileInputStream(file);
                                        if (fileType.equals(FileType.ARCHIVE)) {
                                            inputStream = new ZipInputStream(inputStream);
                                            ((ZipInputStream)inputStream).getNextEntry();
                                        }
                                        Document document = JDOMUtil.loadDocument(inputStream);
                                        if (indicator.isCanceled()) {
                                            throw new ProcessCanceledException();
                                        }
                                        indicator.setText(
                                            Plugin.localizer.getString("comparator.loadsnapshot.progress.building")
                                        );

                                        Element rootElement = document.getRootElement();
                                        model.setRoot(new TreeItem(rootElement));
                                    } catch (Exception e) {
                                        Plugin.logger.error(e);
                                        throw new ProcessCanceledException();
                                    } finally {
                                        try {
                                            inputStream.close();
                                        } catch (IOException ioe) {
                                        }
                                    }
                                }
                            },
                            Plugin.localizer.getString("comparator.loadsnapshot.progress.title"),
                            true,
                            (Project)e.getDataContext().getData(DataConstants.PROJECT)
                        );
                        tree.updateUI();
                    } catch (ProcessCanceledException ex) {
                        // Loading terminated
                    }
                }
                // Notification in status bar
                WindowManager windowManager = WindowManager.getInstance();
                Project project = (Project)e.getDataContext().getData(DataConstants.PROJECT);
                if (project != null) {
                    StatusBar statusBar = windowManager.getStatusBar(project);
                    statusBar.setInfo(
                        Plugin.localizer.format(
                            "comparator.fileChooser.snapshot.open.success.message", file.getPath()
                        )
                    );
                }
            }
        }

    }

}
