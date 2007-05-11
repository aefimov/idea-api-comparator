/* $Id$ */
package org.intellij.apiComparator.spi.nodes.bcel;

import org.intellij.apiComparator.spi.nodes.TreeItem;

/**
 * @author <a href="mailto:aefimov@tengry.com">Alexey Efimov</a>
 * @version $Revision$
 */
public class ScrambleableTreeItem extends TreeItem {
    private boolean scrambled;

    public ScrambleableTreeItem(String value, String name, boolean isScrambled) {
        super(value, name);
        scrambled = isScrambled;
    }

    public boolean isScrambled() {
        return scrambled;
    }
}
