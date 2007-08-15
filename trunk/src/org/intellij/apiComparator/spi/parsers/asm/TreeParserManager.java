package org.intellij.apiComparator.spi.parsers.asm;

import org.intellij.apiComparator.spi.parsers.TreeParser;
import org.objectweb.asm.ClassReader;

import java.util.jar.JarFile;

/**
 * Manager for tree parsers.
 *
 * @author Alexey Efimov
 */
public class TreeParserManager {
    /**
     * Instance
     */
    private static final TreeParserManager instance = new TreeParserManager();

    private TreeParserManager() {
    }

    public static TreeParserManager getInstance() {
        return instance;
    }

    public TreeParser getParser(ClassReader source) {
        return new JavaClassParser(source);
    }

    public TreeParser getParser(JarFile source) {
        return new JarFileParser(source);
    }
}