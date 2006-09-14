package org.intellij.apiComparator;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diff.DiffContent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.DimensionService;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.intellij.apiComparator.actions.*;
import org.intellij.apiComparator.spi.TreeComparator;
import org.intellij.apiComparator.spi.markup.MarkupAttributes;
import org.intellij.apiComparator.spi.markup.MarkupModel;
import org.intellij.apiComparator.spi.markup.TreeItemMarker;
import org.intellij.apiComparator.spi.nodes.TreeItem;
import org.intellij.apiComparator.spi.nodes.TreeItemAttributes;
import org.intellij.apiComparator.spi.parsers.TreeParser;
import org.intellij.apiComparator.spi.parsers.TreeParserEvent;
import org.intellij.apiComparator.spi.parsers.TreeParserListener;
import org.intellij.apiComparator.spi.parsers.TreeParserManager;
import org.intellij.apiComparator.tree.TreeItemModel;
import org.intellij.apiComparator.tree.TreeItemRenderer;
import org.phantom.lang.Strings;
import org.phantom.swing.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;

/**
 * Comparator dialog.
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 */
public class ComparatorDialog extends DialogWrapper implements ActionListener, ComparatorConfigurationListener {
    /**
     * ID
     */
    private static final String ID = "ComparatorDialog";

    /**
     * Configuration
     */
    private static final ComparatorConfiguration configuration = ComparatorConfiguration.getInstance();

    /**
     * UI
     */
    private UI ui;

    /**
     * Action command for Source browse button
     */
    private static final String AC_FROMPATH = "FROMPATH";

    /**
     * Action command for Comparable browse button
     */
    private static final String AC_TOPATH = "TOPATH";

    /**
     * Project
     */
    private Project project;

    public ComparatorDialog(Project project) {
        super(project, true);
        this.project = project;

        setTitle(Plugin.localizer.getString("comparator.title"));

        ui = new UI();
        ui.getFromPathButton().setActionCommand(AC_FROMPATH);
        ui.getFromPathButton().addActionListener(this);
        ui.getToPathButton().setActionCommand(AC_TOPATH);
        ui.getToPathButton().addActionListener(this);

        // Change cancel text
        setCancelButtonText(Plugin.localizer.getString("comparator.actions.close"));
        setOKButtonText(Plugin.localizer.getString("comparator.actions.compare"));

        // Tree ui
        ui.getResultTree().setCellRenderer(
            new TreeItemRenderer(
                new MarkupModel() {
                    public MarkupAttributes getAttributes(TreeItemMarker marker) {
                        if (TreeItemMarker.ADDED.equals(marker)) {
                            return new MarkupAttributes(new Color(10, 119, 0));
                        } else if (TreeItemMarker.CHANGED.equals(marker)) {
                            return new MarkupAttributes(new Color(0, 50, 160));
                        } else if (TreeItemMarker.REMOVED.equals(marker)) {
                            return new MarkupAttributes(new Color(153, 51, 0));
                        } else if (TreeItemMarker.NOTCHANGED.equals(marker)) {
                            return new MarkupAttributes(Color.BLACK);
                        }
                        return null;
                    }
                }
            )
        );
        ui.getResultTree().setModel(new TreeItemModel());

        // Combo boxes
        ui.getFromPathCombo().setModel(new RecentFilesComboBoxModel());
        ui.getToPathCombo().setModel(new RecentFilesComboBoxModel());

        Dimension size = DimensionService.getInstance().getSize(ID);
        if (size != null) {
            ui.getRootPanel().setPreferredSize(size);
        }

        init();
    }

    protected void init() {
        configuration.addComparactoConfigurationListener(this);

        super.init();
    }

    protected void dispose() {
        DimensionService.getInstance().setSize(ID, ui.getRootPanel().getSize());

        super.dispose();
    }

    protected JComponent createNorthPanel() {
        ActionManager actionManager = ActionManager.getInstance();

        DefaultActionGroup actions = new DefaultActionGroup();
        // Add actions
        actions.add(new ShowChangesOnlyToggleAction(ui.getResultTree()));
        actions.add(new HideAddedToggleAction(ui.getResultTree()));
        actions.add(new HideChangedToggleAction(ui.getResultTree()));
        actions.add(new HideRemovedToggleAction(ui.getResultTree()));
        actions.add(new ShowMembersToggleAction(ui.getResultTree()));
        actions.addSeparator();
        actions.add(new CreateShapshotAction(ui.getResultTree()));
        actions.add(new LoadShapshotAction(ui.getResultTree()));
        actions.addSeparator();
        actions.add(new ClearRecentListAction());
        actions.addSeparator();
        actions.add(new AboutAction());

        ActionToolbar toolbar = actionManager.createActionToolbar(ActionPlaces.UNKNOWN, actions, true);
        return toolbar.getComponent();
    }

    protected JComponent createCenterPanel() {
        return ui.getRootPanel();
    }

    protected void doOKAction() {
        if (isOKActionEnabled()) {
            boolean fromPathIsValid = isValidJarPath(ui.getFromPathCombo());
            boolean toPathIsValid = isValidJarPath(ui.getToPathCombo());

            if (fromPathIsValid && toPathIsValid) {
                final File fromFile = getFile(ui.getFromPathCombo());
                final File toFile = getFile(ui.getToPathCombo());
                if (fromFile.equals(toFile)) {
                    Messages.showMessageDialog(
                        Plugin.localizer.getString("comparator.files.equals.message"),
                        Plugin.localizer.getString("comparator.title"),
                        Messages.getInformationIcon()
                    );
                } else {

                    configuration.addRecentEntry(fromFile.getPath());
                    configuration.addRecentEntry(toFile.getPath());

                    try {
                        // Comparing
                        ApplicationManager.getApplication().runProcessWithProgressSynchronously(
                            new Runnable() {
                                public void run() {
                                    ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();

                                    try {
                                        TreeItem from = parseJarFile(fromFile);
                                        TreeItem to = parseJarFile(toFile);

                                        // Now comparing
                                        indicator.setText(
                                            Plugin.localizer.getString("comparator.comparing.progress.wait")
                                        );
                                        indicator.setText2("");
                                        indicator.setFraction(0.0d);
                                        indicator.setIndeterminate(true);

                                        TreeItem result = TreeComparator.compare(from, to);
                                        if (indicator.isCanceled()) {
                                            throw new ProcessCanceledException();
                                        }
                                        if (result != null) {
                                            ((TreeItemModel)ui.getResultTree().getModel()).setRoot(result);
                                        }
                                    } catch (IOException e) {
                                        Plugin.logger.error(e);
                                    }
                                }
                            },
                            Plugin.localizer.getString("comparator.comparing.progress.title"),
                            true,
                            project
                        );
                        // Update ui
                        ui.getResultTree().updateUI();
                    } catch (ProcessCanceledException e) {
                    }
                }
            }
        }
    }

    private TreeItem parseJarFile(File file) throws IOException {
        TreeParserManager parserManager = TreeParserManager.getInstance();
        JarFile jar = new JarFile(file);
        TreeParser jarParser = parserManager.getParser(jar);

        ProgressAdapter listener = new ProgressAdapter(
            Plugin.localizer.format("comparator.comparing.progress.parsing", new Object[]{file.getPath()})
        );
        try {
            jarParser.addTreeParserListener(listener);
            return jarParser.parse();
        } finally {
            jarParser.removeTreeParserListener(listener);
        }
    }

    /**
     * Convert text control to real jar file
     */
    private JarFile getJarFile(JComboBox combo) throws IOException {
        return new JarFile(getFile(combo));
    }

    /**
     * Convert text control to file
     */
    private File getFile(JComboBox combo) {
        if (combo != null) {
            return new File((String)combo.getSelectedItem()).getAbsoluteFile();
        }
        return null;
    }

    /**
     * Convert text control to virtual file
     */
    private VirtualFile getVirtualFile(JComboBox combo) {
        String path = Strings.softTrim((String)combo.getSelectedItem()).replace(File.separatorChar, '/');
        return LocalFileSystem.getInstance().findFileByPath(path);
    }

    private boolean isValidJarPath(JComboBox combo) {
        if (Plugin.logger.assertTrue(combo != null)) {
            try {
                return getJarFile(combo) != null;
            } catch (Exception e) {
            }
        }
        return false;
    }

    /**
     * Browse for file
     */
    public void actionPerformed(ActionEvent e) {
        JComboBox target = null;
        String title = null;
        String description = null;

        if (AC_FROMPATH.equals(e.getActionCommand())) {
            target = ui.getFromPathCombo();
            title = Plugin.localizer.getString("comparator.files.fromPath.fileChooser.title");
            description = Plugin.localizer.getString("comparator.files.fromPath.fileChooser.description");
        } else if (AC_TOPATH.equals(e.getActionCommand())) {
            target = ui.getToPathCombo();
            title = Plugin.localizer.getString("comparator.files.toPath.fileChooser.title");
            description = Plugin.localizer.getString("comparator.files.toPath.fileChooser.description");
        }

        if (target != null) {
            FileChooserDescriptor fcd = new FileChooserDescriptor(false, false, true, true, false, false);

            // Find previos file to selecting
            VirtualFile filePath = getVirtualFile(target);

            fcd.setTitle(title);
            fcd.setDescription(description);
            fcd.setHideIgnored(false);

            VirtualFile[] files = FileChooser.chooseFiles(ui.getRootPanel(), fcd, filePath);
            if (files != null && files.length > 0) {
                String path = files[0].getPath().replace('/', File.separatorChar);
                target.addItem(path);
            }
        }
    }

    /**
     * Combobox model for recent files implementation
     */
    private class RecentFilesComboBoxModel extends AbstractListModel implements MutableComboBoxModel {
        /**
         * Current selected path
         */
        private String selected;
        private List elements = new ArrayList();

        public RecentFilesComboBoxModel() {
            update();
        }

        public void update() {
            elements.clear();
            elements.addAll(Arrays.asList(configuration.getRecentEntries()));
        }

        public void addElement(Object obj) {
            elements.add(obj);
            configuration.addRecentEntry((String)obj);
            setSelectedItem(obj);
        }

        public void removeElement(Object obj) {
            elements.remove(obj);
        }

        public void insertElementAt(Object obj, int index) {
            elements.add(index, obj);
            configuration.addRecentEntry((String)obj);
            setSelectedItem(obj);
        }

        public void removeElementAt(int index) {
            elements.remove(index);
        }

        public Object getSelectedItem() {
            return selected;
        }

        public void setSelectedItem(Object anItem) {
            selected = (String)anItem;
        }

        public int getSize() {
            return elements.size();
        }

        public Object getElementAt(int index) {
            return elements.get(index);
        }
    }

    /**
     * Set "From" file from {@link com.intellij.openapi.diff.DiffTool}
     */
    public void setFromPathContent(DiffContent content) {
        String path = content.getFile().getPath().replace('/', File.separatorChar);
        ui.getFromPathCombo().addItem(path);
    }

    /**
     * Set "To" file from {@link com.intellij.openapi.diff.DiffTool}
     */
    public void setToPathContent(DiffContent content) {
        String path = content.getFile().getPath().replace('/', File.separatorChar);
        ui.getToPathCombo().addItem(path);
    }

    public void recentListChanged() {
        ((RecentFilesComboBoxModel)ui.getFromPathCombo().getModel()).update();
        ((RecentFilesComboBoxModel)ui.getToPathCombo().getModel()).update();

        ui.getFromPathCombo().updateUI();
        ui.getToPathCombo().updateUI();
    }

    /**
     * UI for {@link ComparatorDialog}
     */
    private class UI {
        private JPanel rootPanel;
        private JTree resultTree;
        private JComboBox fromPathCombo;
        private JComboBox toPathCombo;
        private JButton fromPathButton;
        private JButton toPathButton;
        private JLabel toPathLabel;
        private JLabel fromPathLabel;

        public UI() {
            Icon ellipsisIcon = IconLoader.getIcon("/general/ellipsis.png");

            fromPathButton.setIcon(ellipsisIcon);
            fromPathButton.setText(null);

            toPathButton.setIcon(ellipsisIcon);
            toPathButton.setText(null);

            fromPathLabel.setLabelFor(fromPathCombo);
            toPathLabel.setLabelFor(toPathCombo);
        }

        public JPanel getRootPanel() {
            return rootPanel;
        }

        public JTree getResultTree() {
            return resultTree;
        }

        public JComboBox getFromPathCombo() {
            return fromPathCombo;
        }

        public JComboBox getToPathCombo() {
            return toPathCombo;
        }

        public JButton getFromPathButton() {
            return fromPathButton;
        }

        public JButton getToPathButton() {
            return toPathButton;
        }

    }

    private static class ProgressAdapter implements TreeParserListener {
        private String title;

        public ProgressAdapter(String title) {
            this.title = title;
        }

        private ProgressIndicator getIndicator() {
            ProgressManager progressManager = ProgressManager.getInstance();
            ProgressIndicator progressIndicator = progressManager.getProgressIndicator();
            if (progressIndicator == null || progressIndicator.isCanceled()) {
                throw new ProcessCanceledException();
            }
            return progressIndicator;
        }

        public void start(TreeParserEvent event) {
            ProgressIndicator progressIndicator = getIndicator();
            progressIndicator.setFraction(0.0d);
            progressIndicator.setText(title);
        }

        public void next(TreeParserEvent event) {
            TreeParser parser = event.getParser();

            ProgressIndicator progressIndicator = getIndicator();
            progressIndicator.setFraction(((double)parser.getCurrentIndex()) / parser.getSourceSize());
            String name = (String)parser.getCurrentItem().getAttributeValue(TreeItemAttributes.ATTR_VALUE);
            if (name != null) {
                progressIndicator.setText2(name);
            }
        }

        public void complete(TreeParserEvent event) {
            getIndicator().setFraction(1.0d);
        }
    }
}
