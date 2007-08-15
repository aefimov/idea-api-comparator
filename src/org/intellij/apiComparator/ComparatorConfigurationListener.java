package org.intellij.apiComparator;

/**
 * Listener of configuration changes in {@link ComparatorConfiguration}
 *
 * @author Alexey Efimov
 */
public interface ComparatorConfigurationListener {
    /**
     * Event fired then recent list in configuration changed
     */
    public void recentListChanged();
}
