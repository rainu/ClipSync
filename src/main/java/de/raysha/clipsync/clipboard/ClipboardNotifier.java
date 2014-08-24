package de.raysha.clipsync.clipboard;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClipboardNotifier implements Runnable {
 
    private Thread workingThread;
    private boolean enabled;
    private List<ClipboardListener<?>> listenerList;
    private Transferable oldValue;
    private long frequency = 5000;
    private Clipboard clipboard;
 
    /**
     * Construct a new <code>ClipboardNotifier</code>
     */
    public ClipboardNotifier() {
        listenerList = new ArrayList<ClipboardListener<?>>();
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        oldValue = clipboard.getContents(null);
    }
 
    /**
     * Adds an <code>ClipboardListener</code> to the notifier.
     * 
     * @param listener
     *            the <code>ClipboardListener</code> to be added
     */
    public void addClipboardListener(ClipboardListener<?> listener) {
        listenerList.add(listener);
    }
 
    /**
     * Getter
     * 
     * @return delay between checks
     */
    public long getFrequency() {
        return frequency;
    }
 
    /**
     * Getter
     * 
     * @return <code>true</code> if this notifier is enabled, <code>false</code>
     *         otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }
 
    /**
     * Removes an <code>ClipboardListener</code> from the notifier
     * 
     * @param listener
     */
    public void removeClipboardListener(ClipboardListener<?> listener) {
        listenerList.remove(listener);
    }
 
    /**
     * Check if the new and old Value are equal
     * 
     * @param oldValue
     *            Old value of clipboard
     * @param newValue
     *            New value of clipboard
     * @param flavor
     *            <code>DataFlavor</code> of the new value for which the test
     *            should be done
     * @return <code>true</code> is the both cliboard values are equals,
     *         <code>false</code> is the values are not equals or the old value
     *         don't support this <code>DataFlavor</code>
     */
    private boolean checkEquality(Transferable oldValue, Transferable newValue,
            DataFlavor flavor) {
 
        if (oldValue.isDataFlavorSupported(flavor)
                && newValue.isDataFlavorSupported(flavor)) {
            try {
                return oldValue.getTransferData(flavor).equals(
                        newValue.getTransferData(flavor));
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void run() {
        while (enabled) {
            Transferable newValue = clipboard.getContents(null);
            for (ClipboardListener listener : listenerList) {
                if (newValue.isDataFlavorSupported(listener.getDataFlavor())) {
                    if (!checkEquality(oldValue, newValue,
                            listener.getDataFlavor())) {
                        try {
                            listener.valueChanged(
                                    oldValue.isDataFlavorSupported(listener
                                            .getDataFlavor()) ? oldValue
                                            .getTransferData(listener
                                                    .getDataFlavor()) : null,
                                    newValue.getTransferData(listener
                                            .getDataFlavor()));
                        } catch (UnsupportedFlavorException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            oldValue = newValue;
            try {
                Thread.sleep(frequency);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
 
    /**
     * Enable (or disable) the notifier
     * 
     * @param enabled
     *            true to enable the notifier, otherwise false
     */
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            if (enabled) {
                workingThread = new Thread(this);
                workingThread.setName("ClipboardNotifier-Daemon");
                workingThread.setDaemon(true);
                workingThread.start();
            }
        }
    }
 
    /**
     * Setter
     * 
     * @param frequency
     *            delay between checks
     */
    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }
 
}