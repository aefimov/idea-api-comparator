package org.intellij.apiComparator.spi.parsers;

import org.apache.bcel.classfile.JavaClass;

import java.util.jar.JarFile;

/**
 * Manager for tree parsers.
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
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

    public TreeParser getParser(JavaClass source) {
        return new JavaClassParser(source);
    }

    public TreeParser getParser(JarFile source) {
        return new JarFileParser(source);
    }
}
