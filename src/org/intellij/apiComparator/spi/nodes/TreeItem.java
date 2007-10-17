package org.intellij.apiComparator.spi.nodes;

import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.intellij.apiComparator.spi.markup.TreeItemAccessType;
import org.intellij.apiComparator.spi.markup.TreeItemMarker;
import org.intellij.apiComparator.spi.markup.TreeItemType;
import org.jdom.Attribute;
import org.jdom.DataConversionException;
import org.jdom.Element;

import java.util.*;

/**
 * Default Tree Item implementation.
 * 
 * @author <a href="mailto:aefimov@tengry.com">Alexey Efimov</a>
 */
public class TreeItem implements JDOMExternalizable {
    /**
     * Item attributes
     */
    private Map attributes;

    /**
     * Children
     */
    private List children = new ArrayList();

    /**
     * Children
     */
    private List filteredChildren = new ArrayList();

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

    public static final String JDOM_NODE_ITEM = "item";
    public static final String JDOM_ATTR_MARK = "mark";
    public static final String JDOM_ATTR_TYPE = "type";
    public static final String JDOM_ATTR_ACCESS = "access";

    public TreeItem getParent() {
        return parent;
    }

    public void setParent(TreeItem parent) {
        this.parent = parent;
        parent.addChild(this);
        setParentMarker();
    }

    public List getChildren() {
        return children;
    }

    /**
     * Add tree item to tree
     */
    public final void addChild(TreeItem child) {
        int index = indexOfChild(child);
        if (index != -1) {
            // Item already exists
            List children = child.getChildren();
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
            for (int i = 0; i < children.size(); i++) {
                oldChild.addChild((TreeItem)children.get(i));
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
        return (TreeItem)children.get(index);
    }

    public List getFilteredChildren() {
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
                    attributes = new Hashtable();
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
        readExternal(element);
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
        String string = (String)getAttributeValue(TreeItemAttributes.ATTR_NAME);
        return string != null ? string : (String)getAttributeValue(TreeItemAttributes.ATTR_VALUE);
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
                ((TreeItem)item).getAttributeValue(TreeItemAttributes.ATTR_VALUE)
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

    public void readExternal(Element element) throws InvalidDataException {
        if (JDOM_NODE_ITEM.equals(element.getName())) {
            List attributeList = element.getAttributes();
            if (attributes != null) {
                attributes.clear();
            }
            for (int i = 0; i < attributeList.size(); i++) {
                Attribute attribute = (Attribute)attributeList.get(i);
                if (JDOM_ATTR_MARK.equals(attribute.getName())) {
                    try {
                        setMarker(TreeItemMarker.parseInt(attribute.getIntValue()));
                    } catch (DataConversionException e) {
                    }
                } else if (JDOM_ATTR_TYPE.equals(attribute.getName())) {
                    try {
                        setType(TreeItemType.parseInt(attribute.getIntValue()));
                    } catch (DataConversionException e) {
                    }
                } else if (JDOM_ATTR_ACCESS.equals(attribute.getName())) {
                    try {
                        setAccessType(TreeItemAccessType.parseInt(attribute.getIntValue()));
                    } catch (DataConversionException e) {
                    }
                } else {
                    setAttribute(attribute.getName(), attribute.getValue());
                }
            }

            // Read children
            List childList = element.getChildren(JDOM_NODE_ITEM);
            children.clear();

            for (int i = 0; i < childList.size(); i++) {
                Element childElement = (Element)childList.get(i);
                addChild(new TreeItem(childElement));
            }
        }
    }

    public void writeExternal(Element element) throws WriteExternalException {
        // Save attributes
        if (attributes != null) {
            Iterator keys = attributes.keySet().iterator();
            while (keys.hasNext()) {
                String name = (String)keys.next();
                element.setAttribute(name, String.valueOf(attributes.get(name)));
            }
        }

        if (type != null) {
            element.setAttribute(JDOM_ATTR_TYPE, String.valueOf(type.getValue()));
        }
        if (accessType != null) {
            element.setAttribute(JDOM_ATTR_ACCESS, String.valueOf(accessType.getValue()));
        }
        if (marker != null) {
            element.setAttribute(JDOM_ATTR_MARK, String.valueOf(marker.getValue()));
        }

        // Put children
        for (int i = 0; i < children.size(); i++) {
            Element childElement = createElement();
            ((TreeItem)children.get(i)).writeExternal(childElement);
            element.addContent(childElement);
        }
    }

    public static Element createElement() {
        return new Element(JDOM_NODE_ITEM);
    }

    public void sort(Comparator comparator) {
        Collections.sort(children, comparator);
        Collections.sort(filteredChildren, comparator);
        for (int i = 0; i < children.size(); i++) {
            TreeItem item = (TreeItem)children.get(i);
            item.sort(comparator);
        }
    }
}
