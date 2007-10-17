package org.intellij.apiComparator;

/**
 * Listener of configuration changes in {@link ComparatorConfiguration}
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 */
public interface ComparatorConfigurationListener {
    /**
     * Event fired then recent list in configuration changed
     */
    public void recentListChanged();
}
