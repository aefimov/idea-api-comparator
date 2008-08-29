/* $Id$ */
package org.intellij.apiComparator.spi.nodes;

/**
 * @author Alexey Efimov
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
