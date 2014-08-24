package de.raysha.clipsync.clipboard;

import java.awt.datatransfer.DataFlavor;

public abstract class ClipboardListener<T> {
 
    private DataFlavor flavor;
 
    /**
     * Constructs a <code>ClipboardListener</code> for a class <code>type</code>
     * 
     * @param type
     *            Class of the objects for which the listener is responsible
     */
    public ClipboardListener(Class<T> type) {
        flavor = new DataFlavor(type, type.getCanonicalName());
    }
 
    /**
     * Constructs a <code>ClipboardListener</code> for a specific DataFlavor
     * <code>flavor</code>
     * 
     * @param flavor
     *            DataFlavor for which the listener is responsible
     */
    public ClipboardListener(DataFlavor flavor) {
        this.flavor = flavor;
    }
 
    /**
     * Getter
     * 
     * @return <code>DataFlavor</code> for which the listener is responsible
     */
    public DataFlavor getDataFlavor() {
        return flavor;
    }
 
    /**
     * Invoked when the new value of the clipboard not equals to the old value.
     * Works correctly when objects override the {@link Object#equals(Object)}
     * method
     * 
     * @param oldValue
     *            Old value of the clipboard
     * @param newValue
     *            New value of the clipboard
     */
    public abstract void valueChanged(T oldValue, T newValue);
}