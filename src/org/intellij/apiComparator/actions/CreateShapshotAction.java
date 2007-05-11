/* $Id$ */
package org.intellij.apiComparator.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Create snapshot from compared tree.
 *
 * @author <a href="mailto:aefimov@tengry.com">Alexey Efimov</a>
 * @version $Revision$
 */
public class CreateShapshotAction extends AnAction {
    /**
     * Tree
     */
    private JTree tree;

    public CreateShapshotAction(JTree tree) {
        super(
            Plugin.localizer.getString("comparator.toolbar.actions.createSnapshot.text"),
            Plugin.localizer.getString("comparator.toolbar.actions.createSnapshot.description"),
            IconLoader.getIcon("/actions/dump.png")
        );
        this.tree = tree;
    }

    public void actionPerformed(AnActionEvent e) {
        try {
            // Standard action
            JFileChooser jfc = new JFileChooser();
            jfc.setAcceptAllFileFilterUsed(false);
            jfc.addChoosableFileFilter(
                new FileTypeFilter(StdFileTypes.XML, "comparator.fileChooser.snapshot.filter.xml.description")
            );
            jfc.addChoosableFileFilter(
                new FileTypeFilter(StdFileTypes.ARCHIVE, "comparator.fileChooser.snapshot.filter.zip.description")
            );
            jfc.setDialogTitle(Plugin.localizer.getString("comparator.fileChooser.snapshot.save.title"));
            jfc.setDialogType(JFileChooser.SAVE_DIALOG);

            if (jfc.showSaveDialog(tree) == JFileChooser.APPROVE_OPTION) {
                File file = jfc.getSelectedFile();
                final FileType fileType = ((FileTypeFilter)jfc.getFileFilter()).getFileType();
                if (file != null) {
                    if (!file.exists()) {
                        String fileName = file.getName();
                        int dotIndex = fileName.lastIndexOf('.');
                        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
                            String extension = fileName.substring(0, dotIndex + 1);
                            if (extension.toLowerCase().equals(fileType.getDefaultExtension().toLowerCase())) {
                                fileName = fileName.substring(0, dotIndex);
                            }
                        }
                        file = new File(file.getParentFile(), fileName + "." + fileType.getDefaultExtension());
                    }
                    if (file.isDirectory() || (file.exists() && !file.canWrite())) {
                        Messages.showWarningDialog(
                            Plugin.localizer.format(
                                "comparator.fileChooser.snapshot.save.warning.message", file.getPath()
                            ),
                            Plugin.localizer.getString("comparator.fileChooser.snapshot.save.title")
                        );
                    } else {
                        boolean canWrite = file.createNewFile();
                        if (!canWrite) {
                            canWrite = Messages.showYesNoDialog(
                                Plugin.localizer.format(
                                    "comparator.fileChooser.snapshot.save.fileexists.message", file.getPath()
                                ),
                                Plugin.localizer.getString("comparator.fileChooser.snapshot.save.title"),
                                Messages.getQuestionIcon()
                            ) == 0;
                            if (canWrite) {
                                // Clean up it
                                file.delete();
                            }
                        }
                        if (canWrite) {
                            // Write file
                            final File snapshotFile = file;
                            ProgressManager.getInstance().runProcessWithProgressSynchronously(
                                new Runnable() {
                                    public void run() {
                                        try {
                                            OutputStream outputStream = new FileOutputStream(snapshotFile);
                                            try {
                                                ProgressManager progressManager = ProgressManager.getInstance();
                                                ProgressIndicator indicator = progressManager.getProgressIndicator();

                                                if (fileType.equals(StdFileTypes.ARCHIVE)) {
                                                    outputStream = new ZipOutputStream(outputStream);
                                                    ((ZipOutputStream)outputStream).putNextEntry(
                                                        new ZipEntry("snapshot.xml")
                                                    );
                                                }
                                                TreeItemModel model = (TreeItemModel)tree.getModel();
                                                TreeItem item = (TreeItem)model.getRoot();
                                                Element element = TreeItem.createElement();
                                                indicator.setText(
                                                    Plugin.localizer.getString(
                                                        "comparator.createsnapshot.progress.preparing"
                                                    )
                                                );
                                                // Write to JDOM
                                                item.writeExternal(element);
                                                if (indicator.isCanceled()) {
                                                    throw new ProcessCanceledException();
                                                }

                                                indicator.setText(
                                                    Plugin.localizer.format(
                                                        "comparator.createsnapshot.progress.saving",
                                                        new Object[]{snapshotFile.getPath()}
                                                    )
                                                );
                                                // Write to file
                                                JDOMUtil.writeDocument(new Document(element), outputStream, "\n");
                                            } finally {
                                                outputStream.close();
                                            }
                                        } catch (Exception e) {
                                            Plugin.logger.error(e);
                                            throw new ProcessCanceledException();
                                        }
                                    }
                                },
                                Plugin.localizer.getString("comparator.createsnapshot.progress.title"),
                                true,
                                (Project)e.getDataContext().getData(DataConstants.PROJECT)
                            );
                            // Notification in status bar
                            WindowManager windowManager = WindowManager.getInstance();
                            Project project = (Project)e.getDataContext().getData(DataConstants.PROJECT);
                            if (project != null) {
                                StatusBar statusBar = windowManager.getStatusBar(project);
                                statusBar.setInfo(
                                    Plugin.localizer.format(
                                        "comparator.fileChooser.snapshot.save.success.message", file.getPath()
                                    )
                                );
                            }

                        }
                    }
                }
            }
        } catch (Exception ex) {
            Plugin.logger.error(ex);
        }

    }

    public void update(AnActionEvent event) {
        TreeItemModel model = (TreeItemModel)tree.getModel();
        TreeItem item = (TreeItem)model.getRoot();
        event.getPresentation().setEnabled(item != null);
    }
}
