package org.intellij.apiComparator;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.util.JDOMExternalizable;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Comparator configuration
 *
 * @author Alexey Efimov
 */
public class ComparatorConfiguration implements ApplicationComponent, JDOMExternalizable, PersistentStateComponent<Element> {
    /**
     * Listeners
     */
    private List<ComparatorConfigurationListener> listeners = new ArrayList<ComparatorConfigurationListener>();

    /**
     * Show members toogle action state
     */
    private boolean showMembers = false;

    /**
     * Show changes only toggle action state
     */
    private boolean showChangesOnly = true;

    /**
     * Hide removed
     */
    private boolean hideRemoved = false;

    /**
     * Hide changed
     */
    private boolean hideChanged = false;

    /**
     * Hide added
     */
    private boolean hideAdded = false;

    /**
     * Recent entries
     */
    private List<String> recentEntries = new ArrayList<String>();

    /**
     * Recent limit
     */
    private int recentMaxCount = 15;

    // JDOM Names
    private static final String JDOM_ATTR_MEMBERS = "members";
    private static final String JDOM_ATTR_CHANGES_ONLY = "changesonly";
    private static final String JDOM_ATTR_HIDE_ADDED = "hideadded";
    private static final String JDOM_ATTR_HIDE_CHANGED = "hidechanged";
    private static final String JDOM_ATTR_HIDE_REMOVED = "hideremoved";
    private static final String JDOM_NODE_RECENT = "recent";
    private static final String JDOM_NODE_RECENT_MAXCOUNT = "maxcount";
    private static final String JDOM_NODE_RECENT_ENTRY = "entry";
    private static final String JDOM_ATTR_RECENT_ENTRY_PATH = "path";

    @NotNull
    public String getComponentName() {
        String className = getClass().getName();
        String[] names = className.split("\\.");
        return names[names.length - 1];
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }


    public void loadState(Element state) {
        readExternal(state);
    }

    public void readExternal(Element element) {
        ComparatorManager comparator = ComparatorManager.getInstance();

        showMembers = Boolean.valueOf(element.getAttributeValue(JDOM_ATTR_MEMBERS));
        showChangesOnly = Boolean.valueOf(element.getAttributeValue(JDOM_ATTR_CHANGES_ONLY));
        hideRemoved = Boolean.valueOf(element.getAttributeValue(JDOM_ATTR_HIDE_REMOVED));
        hideAdded = Boolean.valueOf(element.getAttributeValue(JDOM_ATTR_HIDE_ADDED));
        hideChanged = Boolean.valueOf(element.getAttributeValue(JDOM_ATTR_HIDE_CHANGED));

        Element recentElement = element.getChild(JDOM_NODE_RECENT);
        if (recentElement != null) {
            try {
                recentMaxCount = Integer.parseInt(recentElement.getAttributeValue(JDOM_NODE_RECENT_MAXCOUNT));
            } catch (NumberFormatException e) {
            }
            List entries = recentElement.getChildren(JDOM_NODE_RECENT_ENTRY);
            for (Object entry1 : entries) {
                Element entry = (Element) entry1;
                String path = entry.getAttributeValue(JDOM_ATTR_RECENT_ENTRY_PATH);
                if (comparator.isValidPath(path) && !recentEntries.contains(path)) {
                    recentEntries.add(path);
                }
            }

            // Shrink recent list by max recent
            shrinkRecentList();
        }
    }

    public Element getState() {
        Element state = new Element("configuration");
        writeExternal(state);
        return state;
    }

    public void writeExternal(Element element) {
        element.setAttribute(JDOM_ATTR_MEMBERS, Boolean.toString(showMembers));
        element.setAttribute(JDOM_ATTR_CHANGES_ONLY, Boolean.toString(showChangesOnly));
        element.setAttribute(JDOM_ATTR_HIDE_ADDED, Boolean.toString(hideAdded));
        element.setAttribute(JDOM_ATTR_HIDE_CHANGED, Boolean.toString(hideChanged));
        element.setAttribute(JDOM_ATTR_HIDE_REMOVED, Boolean.toString(hideRemoved));

        Element recentElement = new Element(JDOM_NODE_RECENT);
        recentElement.setAttribute(JDOM_NODE_RECENT_MAXCOUNT, String.valueOf(recentMaxCount));

        // Write all recent files
        for (String recentEntry : recentEntries) {
            Element entryElement = new Element(JDOM_NODE_RECENT_ENTRY);
            entryElement.setAttribute(JDOM_ATTR_RECENT_ENTRY_PATH, recentEntry);

            recentElement.addContent(entryElement);
        }

        element.addContent(recentElement);
    }

    public static ComparatorConfiguration getInstance() {
        return ApplicationManager.getApplication().getComponent(
                ComparatorConfiguration.class
        );
    }

    public boolean isShowMembers() {
        return showMembers;
    }

    public void setShowMembers(boolean showMembers) {
        this.showMembers = showMembers;
    }

    public boolean isShowChangesOnly() {
        return showChangesOnly;
    }

    public void setShowChangesOnly(boolean showChangesOnly) {
        this.showChangesOnly = showChangesOnly;
    }

    public boolean isHideRemoved() {
        return hideRemoved;
    }

    public void setHideRemoved(boolean hideRemoved) {
        this.hideRemoved = hideRemoved;
    }

    public boolean isHideChanged() {
        return hideChanged;
    }

    public void setHideChanged(boolean hideChanged) {
        this.hideChanged = hideChanged;
    }

    public boolean isHideAdded() {
        return hideAdded;
    }

    public void setHideAdded(boolean hideAdded) {
        this.hideAdded = hideAdded;
    }

    public String[] getRecentEntries() {
        return recentEntries.toArray(new String[recentEntries.size()]);
    }

    public void addRecentEntry(String path) {
        ComparatorManager comparator = ComparatorManager.getInstance();
        if (comparator.isValidPath(path) && !recentEntries.contains(path)) {
            recentEntries.add(0, path);
            shrinkRecentList();
            fireRecentListChanged();
        }
    }

    private void shrinkRecentList() {
        while (recentEntries.size() > recentMaxCount) {
            recentEntries.remove(recentEntries.size() - 1);
        }
    }

    public int getRecentMaxCount() {
        return recentMaxCount;
    }

    public void setRecentMaxCount(int recentMaxCount) {
        if (recentMaxCount > 2) {
            this.recentMaxCount = recentMaxCount;
            shrinkRecentList();
        }
    }

    public void clearRecentEntries() {
        recentEntries.clear();
        fireRecentListChanged();
    }

    public void addComparactoConfigurationListener(ComparatorConfigurationListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeComparactoConfigurationListener(ComparatorConfigurationListener listener) {
        listeners.remove(listener);
    }

    private void fireRecentListChanged() {
        for (ComparatorConfigurationListener listener1 : listeners) {
            listener1.recentListChanged();
        }
    }
}
