/* $Id$ */
package org.intellij.apiComparator.about.impl;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataConstants;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.Surface;
import org.intellij.apiComparator.Plugin;
import org.intellij.apiComparator.about.AboutManager;
import org.intellij.plus.options.OptionsDescriptor;
import org.intellij.plus.options.OptionsManager;
import org.intellij.plus.plugin.PluginDescriptor;
import org.intellij.plus.plugin.PluginManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Implemenation of About manager
 *
 * @author <a href="mailto:aefimov@tengry.com">Alexey Efimov</a>
 * @version $Revision$
 */
class AboutManagerImpl extends AboutManager {
    public AnAction getAboutAction(String text, JComponent component, Icon icon) {
        return new DefaultAboutAction(text, component, icon);
    }

    public AnAction getAboutAction() {
        PluginDescriptor pluginDescriptor = PluginManager.getInstance().getDescriptor();
        return getAboutAction(pluginDescriptor.getPluginName(), new DefaultAboutComponent(pluginDescriptor), null);
    }

    public JComponent getAboutComponent() {
        PluginDescriptor pluginDescriptor = PluginManager.getInstance().getDescriptor();
        return new DefaultAboutComponent(pluginDescriptor);
    }

    /**
     * Default about component.
     */
    private static class DefaultAboutComponent extends Surface {
        /**
         * Mail to protocol
         */
        private static final String MAILTO_PROTOCOL = "mailto:";

        /**
         * Background (400 x 300)
         */
        private static final Icon BACKGROUND = IconLoader.getIcon("/org/intellij/apiComparator/resources/about.png");

        private Font titleFont;
        private Font textFont;

        private PluginDescriptor descriptor;
        private Map hovers = new Hashtable();
        private String hoverUrl;

        DefaultAboutComponent(PluginDescriptor descriptor) {
            this.descriptor = descriptor;
            Font font = UIManager.getFont("Label.font");
            titleFont = font.deriveFont(Font.BOLD, 11);
            textFont = font.deriveFont(Font.PLAIN, 10);

            setPreferredSize(new Dimension(BACKGROUND.getIconWidth(), BACKGROUND.getIconHeight()));
            setOpaque(true);

            addMouseMotionListener(
                new MouseMotionAdapter() {
                    public void mouseMoved(MouseEvent e) {
                        Iterator urls = hovers.keySet().iterator();
                        Cursor cursor = null;
                        while (urls.hasNext() && cursor == null) {
                            String url = (String)urls.next();
                            Rectangle r = (Rectangle)hovers.get(url);
                            if (r.contains(e.getPoint())) {
                                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                                hoverUrl = url;
                            }
                        }
                        if (cursor == null) {
                            cursor = Cursor.getDefaultCursor();
                            hoverUrl = null;
                        }
                        setCursor(cursor);
                    }
                }
            );
            addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (hoverUrl != null) {
                            e.consume();
                            OptionsManager optionsManager = OptionsManager.getInstance();
                            OptionsDescriptor options = optionsManager.getOptions(OptionsManager.IDE_GENERAL);
                            String browserPath = options.getComponentOption("GeneralSettings", "browserPath");
                            if (browserPath != null) {
                                try {
                                    Runtime.getRuntime().exec(browserPath + " " + hoverUrl);
                                } catch (IOException ex) {
                                    Plugin.logger.error(ex);
                                }
                            }
                        }
                    }
                }
            );
        }

        private void renderHoverText(Graphics2D g, int x, int y, String text, String link) {
            g.drawString(text, x, y);
            Rectangle rect = g.getFontMetrics().getStringBounds(text, g).getBounds();
            rect.setLocation(x, y - g.getFontMetrics().getAscent());
            hovers.put(link, rect);
        }

        public void render(int width, int height, Graphics2D g) {
            hovers.clear();
            BACKGROUND.paintIcon(this, g, 0, 0);

            g.setColor(Color.WHITE);

            g.setFont(titleFont);
            FontMetrics metrics = g.getFontMetrics();

            int left = 15;
            int top = 13 + metrics.getAscent();

            String version = descriptor.getPluginVersion();
            g.drawString(descriptor.getPluginName() + (version != null ? " " + version : ""), left, top);

            String pluginUrl = descriptor.getPluginURL();
            if (pluginUrl != null) {
                top += metrics.getHeight();

                g.setFont(textFont);
                renderHoverText(g, left, top, pluginUrl, pluginUrl);
            }

            // Not bright information
            g.setColor(new Color(178, 187, 217));

            String build = descriptor.getBuildNumber();
            if (build != null) {
                top += metrics.getHeight();

                g.setFont(textFont);
                g.drawString(MessageFormat.format("Build #{0}", new Object[]{build}), left, top);
            }

            String buildDate = descriptor.getBuildDate();
            if (buildDate != null) {
                top += metrics.getHeight();

                g.setFont(textFont);
                g.drawString(MessageFormat.format("Build on {0}", new Object[]{buildDate}), left, top);
            }

            long size;
            VirtualFile jarFile = descriptor.getPluginJarFile();
            VirtualFile home = descriptor.getPluginDirectory();

            top += metrics.getHeight();

            if (home != null) {
                size = getDirectorySize(home);
                g.setFont(textFont);
                renderHoverText(
                    g,
                    left,
                    top,
                    MessageFormat.format("Installed to {0}", new Object[]{home.getPresentableUrl()}),
                    home.getPresentableUrl()
                );
            } else {
                size = jarFile.getLength();
                g.setFont(textFont);
                renderHoverText(
                    g,
                    left,
                    top,
                    MessageFormat.format("Installed as {0}", new Object[]{jarFile.getPresentableUrl()}),
                    jarFile.getPresentableUrl()
                );
            }

            top += metrics.getHeight();

            g.setFont(textFont);
            g.drawString(MessageFormat.format("Size on disk {0} KB", new Object[]{new Long(size >> 10)}), left, top);

            String vendor = descriptor.getVendor();
            if (vendor != null) {
                g.setColor(Color.WHITE);
                g.setFont(titleFont);

                metrics = g.getFontMetrics();

                top = 256 + metrics.getAscent();
                String email = descriptor.getVendorEMail();
                if (email != null) {
                    email = email.toLowerCase();
                    if (!email.startsWith(MAILTO_PROTOCOL)) {
                        email = MAILTO_PROTOCOL + email;
                    }
                    renderHoverText(g, left, top, vendor, email);
                } else {
                    g.drawString(vendor, left, top);
                }
                top += metrics.getHeight();

                String vendorUrl = descriptor.getVendorURL();
                if (vendorUrl != null) {
                    g.setFont(textFont);
                    renderHoverText(g, left, top, vendorUrl, vendorUrl);
                }
            }
        }

        private long getDirectorySize(VirtualFile directory) {
            long size = 0;
            if (directory == null) {
                throw new IllegalArgumentException("directory is null");
            }

            VirtualFile[] dirFiles = directory.getChildren();
            if (dirFiles != null) {
                for (int i = 0; i < dirFiles.length; i++) {
                    VirtualFile file = dirFiles[i];
                    if (file.isDirectory()) {
                        size += getDirectorySize(file);
                    } else {
                        long fileSize = file.getLength();
                        if (fileSize > 0) {
                            size += fileSize;
                        }
                    }
                }
            }

            return size;
        }
    }

    /**
     * Default about action.
     */
    private static final class DefaultAboutAction extends AnAction {
        /**
         * UI Component
         */
        private JComponent component;

        public DefaultAboutAction(String text, JComponent component, Icon icon) {
            super(text, null, icon);
            this.component = component;
        }

        public void actionPerformed(AnActionEvent e) {
            Window window = WindowManager.getInstance().suggestParentWindow(
                (Project)e.getDataContext().getData(DataConstants.PROJECT)
            );
            JDialog dialog;
            if (window instanceof Frame) {
                dialog = new JDialog((Frame)window);
            } else {
                dialog = new JDialog((Dialog)window);
            }

            SelfDisposerListener listener = new SelfDisposerListener(dialog);

            dialog.setUndecorated(true);
            JPanel panel = new JPanel(new BorderLayout());
            component.addMouseListener(listener);
            panel.add(component, BorderLayout.CENTER);
            dialog.setContentPane(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(window);

            dialog.addWindowFocusListener(listener);
            dialog.addKeyListener(listener);
            dialog.addMouseListener(listener);
            dialog.setVisible(true);
        }

    }

    /**
     * Listener to close dialog on event
     */
    private static final class SelfDisposerListener extends MouseAdapter implements WindowFocusListener, KeyListener {
        private JDialog dialog;

        public SelfDisposerListener(JDialog dialog) {
            this.dialog = dialog;
        }

        public void windowGainedFocus(WindowEvent e) {
        }

        public void windowLostFocus(WindowEvent e) {
            dispose();
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE && e.getModifiers() == 0) {
                dispose();
            }
        }

        public void keyReleased(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
            if (!e.isConsumed()) {
                e.consume();
                dispose();
            }
        }

        private void dispose() {
            dialog.dispose();
        }
    }

}
