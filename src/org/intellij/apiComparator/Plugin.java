/* $Id$ */
package org.intellij.apiComparator;

import com.intellij.openapi.diagnostic.Logger;
import org.intellij.plus.localizer.Localizer;
import org.intellij.plus.localizer.LocalizerManager;
import org.intellij.plus.plugin.PluginDescriptor;
import org.intellij.plus.plugin.PluginManager;

/**
 * Plugin info.
 *
 * @author <a href="mailto:aefimov@tengry.com">Alexey Efimov</a>
 * @version $Revision$
 */
public class Plugin {
    /**
     * Bundle name
     */
    private static final String BUNDLE_NAME = "org.intellij.apiComparator.resources.strings";

    /**
     * Localizer
     */
    public static final Localizer localizer = LocalizerManager.getInstance().getLocalizer(BUNDLE_NAME);

    /**
     * Descriptor
     */
    public static PluginDescriptor descriptor = PluginManager.getInstance().getDescriptor();

    /**
     * Default logger
     */
    public static final Logger logger = Logger.getInstance(descriptor.getPluginName());
}
