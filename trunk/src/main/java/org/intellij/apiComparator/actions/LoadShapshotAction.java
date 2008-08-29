/* $Id$ */
package org.intellij.apiComparator.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import org.intellij.apiComparator.FileTypeFilter;
import org.intellij.apiComparator.Plugin;
import org.intellij.apiComparator.spi.nodes.TreeItem;
import org.intellij.apiComparator.tree.TreeItemModel;
import org.intellij.apiComparator.util.APIComparatorBundle;
import org.jdom.Document;
import org.jdom.Element;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

/**
 * Create snapshot from compared tree.
 *
 * @author Alexey Efimov
 */
public class LoadShapshotAction extends AnAction {
    /**
     * Tree
     */
    private JTree tree;

    public LoadShapshotAction(JTree tree) {
        super(
                APIComparatorBundle.message("comparator.toolbar.actions.loadSnapshot.text"),
                APIComparatorBundle.message("comparator.toolbar.actions.loadSnapshot.description"),
                IconLoader.getIcon("/actions/get.png")
        );
        this.tree = tree;
    }

    public void actionPerformed(AnActionEvent e) {
        final TreeItemModel model = (TreeItemModel) tree.getModel();
        // Standard action
        JFileChooser jfc = new JFileChooser();
        jfc.addChoosableFileFilter(
                new FileTypeFilter(StdFileTypes.ARCHIVE, "comparator.fileChooser.snapshot.filter.zip.description")
        );
        jfc.addChoosableFileFilter(
                new FileTypeFilter(StdFileTypes.XML, "comparator.fileChooser.snapshot.filter.xml.description")
        );
        jfc.setDialogTitle(APIComparatorBundle.message("comparator.fileChooser.snapshot.open.title"));
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);

        if (jfc.showOpenDialog(tree) == JFileChooser.APPROVE_OPTION) {
            final File file = jfc.getSelectedFile();
            final FileType fileType = ((FileTypeFilter) jfc.getFileFilter()).getFileType();
            if (file != null) {
                if (!file.exists() || file.isDirectory() || (file.exists() && !file.canRead())) {
                    Messages.showWarningDialog(
                            APIComparatorBundle.message(
                                    "comparator.fileChooser.snapshot.open.warning.message", file.getPath()
                            ),
                            APIComparatorBundle.message("comparator.fileChooser.snapshot.open.title")
                    );
                } else {
                    // Write file
                    try {
                        ProgressManager.getInstance().runProcessWithProgressSynchronously(
                                new Runnable() {
                                    public void run() {
                                        InputStream inputStream = null;
                                        try {
                                            ProgressManager progressManager = ProgressManager.getInstance();
                                            ProgressIndicator indicator = progressManager.getProgressIndicator();

                                            indicator.setText(
                                                    APIComparatorBundle.message(
                                                            "comparator.loadsnapshot.progress.loading",
                                                            file.getPath())
                                            );

                                            inputStream = new FileInputStream(file);
                                            if (fileType.equals(StdFileTypes.ARCHIVE)) {
                                                inputStream = new ZipInputStream(inputStream);
                                                ((ZipInputStream) inputStream).getNextEntry();
                                            }
                                            Document document = JDOMUtil.loadDocument(inputStream);
                                            if (indicator.isCanceled()) {
                                                throw new ProcessCanceledException();
                                            }
                                            indicator.setText(
                                                    APIComparatorBundle.message("comparator.loadsnapshot.progress.building")
                                            );

                                            Element rootElement = document.getRootElement();
                                            model.setRoot(new TreeItem(rootElement));
                                        } catch (Exception e) {
                                            Plugin.LOG.error(e);
                                            throw new ProcessCanceledException();
                                        } finally {
                                            try {
                                                if (inputStream != null) {
                                                    inputStream.close();
                                                }
                                            } catch (IOException ioe) {
                                            }
                                        }
                                    }
                                },
                                APIComparatorBundle.message("comparator.loadsnapshot.progress.title"),
                                true,
                                DataKeys.PROJECT.getData(e.getDataContext())
                        );
                        tree.updateUI();
                    } catch (ProcessCanceledException ex) {
                        // Loading terminated
                    }
                }
                // Notification in status bar
                WindowManager windowManager = WindowManager.getInstance();
                Project project = DataKeys.PROJECT.getData(e.getDataContext());
                if (project != null) {
                    StatusBar statusBar = windowManager.getStatusBar(project);
                    statusBar.setInfo(
                            APIComparatorBundle.message(
                                    "comparator.fileChooser.snapshot.open.success.message", file.getPath()
                            )
                    );
                }
            }
        }

    }

}
