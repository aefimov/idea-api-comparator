/* $Id$ */
package org.intellij.apiComparator.spi.markup;

import com.intellij.openapi.fileTypes.StdFileTypes;
import org.phantom.lang.Enum;
import org.phantom.lang.Strings;
import org.phantom.swing.IconLoader;

import javax.swing.*;

/**
 * {@link org.intellij.apiComparator.spi.nodes.TreeItem} type.
 *
 * @author <a href="mailto:aefimov@tengry.com">Alexey Efimov</a>
 * @version $Revision$
 */
public final class TreeItemType extends Enum implements Comparable {
    public static final TreeItemType UNKNOWN = new TreeItemType(-1, -1, StdFileTypes.UNKNOWN.getIcon());
    public static final TreeItemType ARCHIVE = new TreeItemType(0, 0, StdFileTypes.ARCHIVE.getIcon());
    public static final TreeItemType FOLDER = new TreeItemType(1, 0, "/nodes/folder.png", "/nodes/folderOpen.png");
    public static final TreeItemType PACKAGE = new TreeItemType(
        2, 1, "/nodes/packageClosed.png", "/nodes/packageOpen.png"
    );
    public static final TreeItemType STATIC_CLASS = new TreeItemType(3, 2, "/nodes/staticClass.png");
    public static final TreeItemType CLASS = new TreeItemType(4, 2, "/nodes/class.png");
    public static final TreeItemType STATIC_INTERFACE = new TreeItemType(5, 2, "/nodes/staticInterface.png");
    public static final TreeItemType INTERFACE = new TreeItemType(6, 2, "/nodes/interface.png");
    public static final TreeItemType STATIC_FIELD = new TreeItemType(7, 3, "/nodes/staticField.png");
    public static final TreeItemType FIELD = new TreeItemType(8, 3, "/nodes/field.png");
    public static final TreeItemType STATIC_METHOD = new TreeItemType(9, 4, "/nodes/staticMethod.png");
    public static final TreeItemType METHOD = new TreeItemType(10, 4, "/nodes/method.png");

    private Icon closed;
    private Icon open;
    private int level;

    private TreeItemType(int value, int level, String icon) {
        this(value, level, Strings.softTrim(icon), Strings.softTrim(icon));
    }

    private TreeItemType(int value, int level, String iconClosed, String iconOpen) {
        this(value, level, IconLoader.getIcon(iconClosed), IconLoader.getIcon(iconOpen));
    }

    private TreeItemType(int value, int level, Icon icon) {
        this(value, level, icon, icon);
    }

    private TreeItemType(int value, int level, Icon iconClosed, Icon iconOpen) {
        super(new Integer(value));
        this.level = level;
        this.closed = iconClosed;
        this.open = iconOpen;
    }

    public static TreeItemType parseInt(int value) {
        return (TreeItemType)findValue(TreeItemType.class, new Integer(value));
    }

    public Icon getClosed() {
        return closed;
    }

    public Icon getOpen() {
        return open;
    }

    public boolean isMember() {
        return FIELD.equals(this) || STATIC_FIELD.equals(this) || METHOD.equals(this) || STATIC_METHOD.equals(this);
    }

    public boolean isField() {
        return FIELD.equals(this) || STATIC_FIELD.equals(this);
    }

    public boolean isMethod() {
        return METHOD.equals(this) || STATIC_METHOD.equals(this);
    }

    public boolean isPackage() {
        return PACKAGE.equals(this);
    }

    public boolean isClass() {
        return CLASS.equals(this) || STATIC_CLASS.equals(this) || INTERFACE.equals(this) || STATIC_INTERFACE.equals(
            this
        );
    }

    public Integer getLevel() {
        return new Integer(level);
    }

    public int compareTo(Object o) {
        return getLevel().compareTo(((TreeItemType)o).getLevel());
    }
}

