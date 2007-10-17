/* $Id$ */
package org.intellij.apiComparator;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.Arrays;

/**
 * Filter for JFileChooser.
 *
 * @author <a href="mailto:aefimov@tengry.com">Alexey Efimov</a>
 */
public class FileTypeFilter extends FileFilter {
    /**
     * File Type
     */
    private FileType fileType;

    /**
     * Description
     */
    private String descriptionKey;

    public FileTypeFilter(FileType fileType, String descriptionKey) {
        this.fileType = fileType;
        this.descriptionKey = descriptionKey;
    }

    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        // Check for extension
        String fileName = f.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            String extension = fileName.substring(dotIndex + 1);

            return Arrays.asList(FileTypeManager.getInstance().getAssociatedExtensions(fileType)).contains(extension);
        }
        return false;
    }

    public FileType getFileType() {
        return fileType;
    }

    public String getDescription() {
        return Plugin.localizer.format(descriptionKey, fileType.getDefaultExtension());
    }
}
