/* $Id$ */
package org.intellij.apiComparator.spi.nodes;

/**
 * Utility for checking obfuscated code.
 *
 * @author Alexey Efimov
 */
public class ObfuscatorUtil {
    public static boolean isScrambled(String name) {
        return name == null || (name.length() < 4 && name.toLowerCase().equals(name));
    }
}
