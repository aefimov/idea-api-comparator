package org.intellij.apiComparator.spi.parsers;

import org.intellij.apiComparator.spi.nodes.TreeItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Default parse progress monitor implementation.
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 */
public abstract class AbstractTreeParser implements TreeParser, Runnable {
    /**
     * Listeners
     */
    private List listeners = new ArrayList();

    private int sourceSize;
    private int currentIndex;
    private TreeItem currentItem;
    private TreeItem result;

    private boolean started = false;
    private boolean completed = false;

    /**
     * Source object
     */
    protected Object source;

    protected AbstractTreeParser(Object source) {
        assertSource(source);
        this.source = source;
        this.currentItem = null;
        this.result = null;
        this.sourceSize = 0;
        this.currentIndex = 0;
    }

    /**
     * Method must check that source is valid, otherwize {@link IllegalArgumentException} must be thrown.
     *
     * @param source Source object
     */
    protected void assertSource(Object source) {
        if (source == null) {
            throw new IllegalArgumentException();
        }
    }

    public int getSourceSize() {
        return sourceSize;
    }

    public int getCurrentIndex() {
        if (!started) {
            throw new IllegalStateException();
        }
        return currentIndex;
    }

    public TreeItem getCurrentItem() {
        if (!started) {
            throw new IllegalStateException();
        }
        return currentItem;
    }

    protected void setSourceSize(int size) {
        this.sourceSize = size;
    }

    protected void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    protected void incCurrentIndex() {
        this.currentIndex++;
    }

    protected void setCurrentItem(TreeItem currentItem) {
        this.currentItem = currentItem;
        fireNextEvent();
    }

    public TreeItem getResult() {
        if (!completed) {
            throw new IllegalStateException();
        }
        return result;
    }

    protected void setResult(TreeItem result) {
        this.result = result;
    }

    public void removeTreeParserListener(TreeParserListener listener) {
        listeners.remove(listener);
    }

    public void addTreeParserListener(TreeParserListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Method must be called from at starting of parsing.
     */
    private void fireStartEvent() {
        if (source != null) {
            started = true;
            TreeParserEvent event = new TreeParserEvent(this, source);
            for (int i = 0; i < listeners.size(); i++) {
                TreeParserListener listener = (TreeParserListener)listeners.get(i);
                listener.start(event);
            }
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Method must be called from implementation of parser then parse process is going to next item parsing.
     */
    protected void fireNextEvent() {
        TreeParserEvent event = new TreeParserEvent(this, currentItem);
        for (int i = 0; i < listeners.size(); i++) {
            TreeParserListener listener = (TreeParserListener)listeners.get(i);
            listener.next(event);
        }
    }

    /**
     * Method must be called at end of parsing.
     */
    private void fireCompleteEvent() {
        completed = true;
        if (result != null) {
            TreeParserEvent event = new TreeParserEvent(this, result);
            for (int i = 0; i < listeners.size(); i++) {
                TreeParserListener listener = (TreeParserListener)listeners.get(i);
                listener.complete(event);
            }
        }
    }

    /**
     * Default thread behavior
     */
    public void run() {
        parse();
    }

    public final TreeItem parse() {
        fireStartEvent();
        performParse();
        fireCompleteEvent();
        return getResult();
    }

    /**
     * Proxy implementation of {@link TreeParser#parse()} method
     */
    protected abstract void performParse();
}
