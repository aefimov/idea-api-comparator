package org.intellij.apiComparator.spi.nodes.bcel;

import org.intellij.apiComparator.spi.markup.TreeItemType;
import org.intellij.apiComparator.spi.nodes.TreeItem;

/**
 * <p>Package tree item
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 */
public class PackageTreeItem extends TreeItem {
    public static final String REGEX_POINT = "\\.";

    public PackageTreeItem(String name) {
        super(name, getName(name));
        setType(TreeItemType.PACKAGE);
    }

    private static final String getName(String fullName) {
        String[] names = fullName.split(REGEX_POINT);
        return names[names.length - 1];
    }
}
