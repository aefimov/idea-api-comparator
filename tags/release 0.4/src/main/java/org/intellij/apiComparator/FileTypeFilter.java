package org.intellij.apiComparator;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import org.intellij.apiComparator.util.APIComparatorBundle;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Filter for JFileChooser.
 *
 * @author Alexey Efimov
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
        if (fileType != null) {
            if (f.isDirectory()) {
                return true;
            }

            FileTypeManager typeManager = FileTypeManager.getInstance();
            return fileType.equals(typeManager.getFileTypeByFileName(f.getName()));
        }
        return false;
    }

    public FileType getFileType() {
        return fileType;
    }

    public String getDescription() {
        return APIComparatorBundle.message(descriptionKey, fileType.getDefaultExtension());
    }
}
