package org.intellij.apiComparator.spi.nodes.asm;

import org.intellij.apiComparator.spi.markup.TreeItemAccessType;
import org.intellij.apiComparator.spi.nodes.ScrambleableTreeItem;
import org.intellij.apiComparator.spi.nodes.TreeItemAttributes;

/**
 * Base class for Asm based implementation {@link org.intellij.apiComparator.spi.nodes.TreeItem}.
 *
 * @author <a href="mailto:thibaut.fagart@gmail.com">Thibaut Fagart</a>
 */
public abstract class AbstractAsmTreeItem extends ScrambleableTreeItem {
    public AbstractAsmTreeItem(String value, String name, boolean scrambled, int accessFlags) {
        super(value, name, scrambled);
        setAttribute(TreeItemAttributes.ATTR_TEXT, value);

        if (AsmUtil.isPublic(accessFlags)) {
            setAccessType(TreeItemAccessType.PUBLIC);
        } else if (AsmUtil.isProtected(accessFlags)) {
            setAccessType(TreeItemAccessType.PROTECTED);
        } else if (AsmUtil.isPrivate(accessFlags)) {
            setAccessType(TreeItemAccessType.PRIVATE);
        } else {
            setAccessType(TreeItemAccessType.PLOCAL);
        }
    }

    public String toString() {
        return (String)getAttributeValue(TreeItemAttributes.ATTR_TEXT);
    }
}
