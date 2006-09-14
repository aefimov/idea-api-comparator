/* $Id$ */
package org.intellij.apiComparator.spi.markup;

import org.phantom.lang.Enum;
import org.phantom.swing.IconLoader;

import javax.swing.*;

/**
 * Access icons.
 *
 * @author <a href="mailto:aefimov@tengry.com">Alexey Efimov</a>
 * @version $Revision$
 */
public class TreeItemAccessType extends Enum {
    public static final TreeItemAccessType PUBLIC = new TreeItemAccessType(0, "/nodes/c_public.png");
    public static final TreeItemAccessType PROTECTED = new TreeItemAccessType(1, "/nodes/c_protected.png");
    public static final TreeItemAccessType PRIVATE = new TreeItemAccessType(2, "/nodes/c_private.png");
    public static final TreeItemAccessType PLOCAL = new TreeItemAccessType(3, "/nodes/c_plocal.png");

    private Icon icon;

    private TreeItemAccessType(int value, String icon) {
        this(value, IconLoader.getIcon(icon));
    }

    private TreeItemAccessType(int value, Icon icon) {
        super(new Integer(value));
        this.icon = icon;
    }

    public static TreeItemAccessType parseInt(int value) {
        return (TreeItemAccessType)findValue(TreeItemAccessType.class, new Integer(value));
    }

    public Icon getIcon() {
        return icon;
    }
}
