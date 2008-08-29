package org.intellij.apiComparator.spi.nodes;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.util.InvalidDataException;
import org.intellij.apiComparator.Plugin;
import org.intellij.apiComparator.spi.markup.TreeItemAccessType;
import org.intellij.apiComparator.spi.markup.TreeItemMarker;
import org.intellij.apiComparator.spi.markup.TreeItemType;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;

import java.util.*;

/**
 * Default Tree Item implementation.
 *
 * @author Alexey Efimov
 */
public class TreeItem implements PersistentStateComponent<Element> {
    @NonNls
    public static final String JDOM_NODE_ITEM = "item";
    @NonNls
    public static final String JDOM_ATTR_MARK = "mark";
    @NonNls
    public static final String JDOM_ATTR_TYPE = "type";
    @NonNls
    public static final String JDOM_ATTR_ACCESS = "access";

    /**
     * Item attributes
     */
    private Map<String, Object> attributes;

    /**
     * Children
     */
    private List<TreeItem> children = new ArrayList<TreeItem>();

    /**
     * Children
     */
    private List<TreeItem> filteredChildren = new ArrayList<TreeItem>();

    /**
     * Parent {@link TreeItem}
     */
    private TreeItem parent;

    /**
     * Marker
     */
    private TreeItemMarker marker;

    /**
     * Type
     */
    private TreeItemType type;

    /**
     * Access type of item.
     */
    private TreeItemAccessType accessType;

    public TreeItem getParent() {
        return parent;
    }

    public void setParent(TreeItem parent) {
        this.parent = parent;
        parent.addChild(this);
        setParentMarker();
    }

    public List<TreeItem> getChildren() {
        return children;
    }

    /**
     * Add tree item to tree
     */
    public final void addChild(TreeItem child) {
        int index = indexOfChild(child);
        if (index != -1) {
            // Item already exists
            List<TreeItem> children = child.getChildren();
            // Get this existing item
            TreeItem oldChild = getChild(index);
            // Set types in case of inner classes
            if (oldChild.getType() == null) {
                oldChild.setType(child.getType());
            }
            if (oldChild.getAccessType() == null) {
                oldChild.setAccessType(child.getAccessType());
            }
            // Add all children (if not exists)
            for (TreeItem aChildren : children) {
                oldChild.addChild(aChildren);
            }
        } else {
            // Item not exists
            children.add(child);
            child.setParent(this);
        }
    }

    public int indexOfChild(TreeItem child) {
        return children.indexOf(child);
    }

    public TreeItem getChild(int index) {
        return children.get(index);
    }

    public List<TreeItem> getFilteredChildren() {
        return filteredChildren;
    }

    /**
     * Add attribute for atem
     */
    public void setAttribute(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        if (attributes == null) {
            synchronized (this) {
                if (attributes == null) {
                    attributes = new Hashtable<String, Object>();
                }
            }
        }
        attributes.put(name, value);
    }

    public Object getAttributeValue(String name) {
        return attributes != null ? attributes.get(name) : null;
    }

    public Map getAttributes() {
        return attributes != null ? Collections.unmodifiableMap(attributes) : Collections.EMPTY_MAP;
    }

    public TreeItem(Element element) throws InvalidDataException {
        loadState(element);
    }

    public TreeItem(String value, String name) {
        this(value);
        setAttribute(TreeItemAttributes.ATTR_NAME, name);
    }

    public TreeItem(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        setAttribute(TreeItemAttributes.ATTR_VALUE, value);
    }

    /**
     * By default mathing is performed only by {@link TreeItemAttributes#ATTR_NAME} attribute.
     */
    public boolean matched(TreeItem item) {
        Object name = getAttributeValue(TreeItemAttributes.ATTR_NAME);
        if (name != null && item.getClass().getName().equals(getClass().getName())) {
            return name.equals(item.getAttributeValue(TreeItemAttributes.ATTR_NAME));
        } else {
            return equals(item);
        }
    }

    public String toString() {
        String string = (String) getAttributeValue(TreeItemAttributes.ATTR_NAME);
        return string != null ? string : (String) getAttributeValue(TreeItemAttributes.ATTR_VALUE);
    }

    public boolean equals(Object item) {
        if (this == item) {
            return true;
        }
        if (item instanceof TreeItem) {
            // Compare class types
            String typeName = getClass().getName();

            Object value = getAttributeValue(TreeItemAttributes.ATTR_VALUE);
            return typeName.equals(item.getClass().getName()) && value != null && value.equals(
                    ((TreeItem) item).getAttributeValue(TreeItemAttributes.ATTR_VALUE)
            );
        }
        return false;
    }

    public TreeItemMarker getMarker() {
        return marker;
    }

    public void setMarker(TreeItemMarker marker) {
        this.marker = marker;
        setParentMarker();
    }

    private void setParentMarker() {
        if ((marker != null && !TreeItemMarker.NOTCHANGED.equals(marker)) && parent != null) {
            if (parent.getMarker() == null || TreeItemMarker.NOTCHANGED.equals(parent.getMarker())) {
                parent.setMarker(TreeItemMarker.CHANGED);
            }
        }
    }

    public TreeItemType getType() {
        return type;
    }

    public void setType(TreeItemType type) {
        this.type = type;
    }

    public TreeItemAccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(TreeItemAccessType accessType) {
        this.accessType = accessType;
    }

    public static Element createElement() {
        return new Element(JDOM_NODE_ITEM);
    }

    public void sort(Comparator<TreeItem> comparator) {
        Collections.sort(children, comparator);
        Collections.sort(filteredChildren, comparator);
        for (TreeItem aChildren : children) {
            aChildren.sort(comparator);
        }
    }

    public Element getState() {
        Element element = createElement();
        // Save attributes
        if (attributes != null) {
            for (String name : attributes.keySet()) {
                element.setAttribute(name, String.valueOf(attributes.get(name)));
            }
        }

        if (type != null) {
            element.setAttribute(JDOM_ATTR_TYPE, String.valueOf(type.ordinal()));
        }
        if (accessType != null) {
            element.setAttribute(JDOM_ATTR_ACCESS, String.valueOf(accessType.ordinal()));
        }
        if (marker != null) {
            element.setAttribute(JDOM_ATTR_MARK, String.valueOf(marker.ordinal()));
        }

        // Put children
        for (TreeItem aChildren : children) {
            element.addContent((aChildren).getState());
        }
        return element;
    }

    public void loadState(Element state) {
        if (JDOM_NODE_ITEM.equals(state.getName())) {
            List attributeList = state.getAttributes();
            if (attributes != null) {
                attributes.clear();
            }
            for (Object anAttributeList : attributeList) {
                Attribute attribute = (Attribute) anAttributeList;
                if (JDOM_ATTR_MARK.equals(attribute.getName())) {
                    try {
                        setMarker(TreeItemMarker.valueOf(attribute.getIntValue()));
                    } catch (DataConversionException e) {
                        Plugin.LOG.error(e);
                    }
                } else if (JDOM_ATTR_TYPE.equals(attribute.getName())) {
                    try {
                        setType(TreeItemType.valueOf(attribute.getIntValue()));
                    } catch (DataConversionException e) {
                        Plugin.LOG.error(e);
                    }
                } else if (JDOM_ATTR_ACCESS.equals(attribute.getName())) {
                    try {
                        setAccessType(TreeItemAccessType.valueOf(attribute.getIntValue()));
                    } catch (DataConversionException e) {
                        Plugin.LOG.error(e);
                    }
                } else {
                    setAttribute(attribute.getName(), attribute.getValue());
                }
            }

            // Read children
            List childList = state.getChildren(JDOM_NODE_ITEM);
            children.clear();

            for (Object aChildList : childList) {
                Element childElement = (Element) aChildList;
                try {
                    addChild(new TreeItem(childElement));
                } catch (InvalidDataException e) {
                    Plugin.LOG.error(e);
                }
            }
        }
    }
}
