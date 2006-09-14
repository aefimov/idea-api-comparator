/* $Id$ */
package org.intellij.apiComparator.about;

import com.intellij.openapi.actionSystem.AnAction;
import org.intellij.plus.pattern.PhantomImpl;

import javax.swing.*;

/**
 * About manager. This manager get component for rendering About plugin box.
 *
 * @author <a href="mailto:aefimov@tengry.com">Alexey Efimov</a>
 * @version $Revision$
 */
public abstract class AboutManager {
    /**
     * Phantom Implementation singleton
     */
    private static final AboutManager instance = (AboutManager)PhantomImpl.newInstance(AboutManager.class);

    public static AboutManager getInstance() {
        return instance;
    }

    /**
     * Get default 'About' box component for plugin.
     */
    public abstract JComponent getAboutComponent();

    /**
     * Get action for component. Action just create {@link JDialog} and show it. Dialog will created without borders,
     * and will closed on click or other event.
     */
    public abstract AnAction getAboutAction();

    /**
     * Get action for component. Action just create {@link JDialog} and show it. Dialog will created without borders,
     * and will closed on click or other event.
     *
     * @param text Action text
     * @param component Component
     * @param icon Icon for action
     */
    public abstract AnAction getAboutAction(String text, JComponent component, Icon icon);

    /**
     * Get action for component. Action just create {@link JDialog} and show it. Dialog will created without borders,
     * and will closed on click or other event.
     *
     * @param text Action text
     * @param component Component
     */
    public AnAction getAboutAction(String text, JComponent component) {
        return getAboutAction(text, component, null);
    }

}
