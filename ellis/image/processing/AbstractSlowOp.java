package ellis.image.processing;

import java.awt.Component;
import java.awt.event.*;
import java.awt.image.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 *	<P>The abstract superclass of all slow or threaded image processing operations.</P>
 *
 *  <P>After instantiation the computation is begun by calling the start method.  This class is meant to perform one
 computation at a time.</P>
 *
 *	<P>This class provides support for slow image processing operations by providing a thread and progress monitor.
 The <code>run()</code> method should not be called directly.  Instead
 the <code>start()</code> method should be used which initializes the <code>ProgressMonitor</code>, creates a new thread, and then
 calls the <code>run()</code> method.</P>
 *
 * <P>Subclasses should:</P>
 <UL>
 <LI>Provide their own constructor which in turn calls <code>super(parentComponent)</code>.</LI>
 <LI>Override the <code>run()</code> method with the image processing code.</LI>
 <LI>At periodic intervals during the <code>run()</code> method include calls to the following methods to properly
 update the <code>ProgressMonitor</code> and state variables.  Call <code>setStatus(String status)</code> followed by
 <code>updateProgress()</code>.</LI>
 <LI>At periodic intervals check the <code>isCanceled()</code> method to see if the user wish to abort the operation.</LI>
 <LI>After completion of the processing call <code>setProcessedImageOnFinish()</code>.  This will set the image, alert
 any registered <code>PropertyChangeListener</code> objects that the image is ready, close the
 <code>ProgressMonitor</code>, and terminate the <code>Thread</code>.</LI>
 </UL>
 *
 *  <P>This class fires a PropertyChangeEvent to indicate that an image has been processed.  The property name is
 "processedImage" of type BufferedImage.  The original image is sent as the old value.</P>
 *
 *
 *  @author Ellis Teer 7/1/99
 *  @see ImageUtilities
 *	@see AbstractFastOp
 */

public abstract class AbstractSlowOp extends AbstractDataOp implements Runnable{

	/**
	*  <P>Used to store the maximum number of tasks that must be performed before the <code>ProgressMonitor</code> is
	completed.</P>
	*/
	protected int				maxProgress			= 10;
	/**
	*  <P>Used to store the minimum number of tasks that must be performed before the <code>ProgressMonitor</code> is
	completed.  Generally this should be zero.</P>
	*/
	protected int				minProgress			= 0;
	/**
	*  <P>Used to store the current number of tasks that have been completed.  The <code>ProgressMonitor</code> is
	updated to this value during <code>updateProgress()</code>.</P>
	*/
	protected int				progress;
	/**
	*  <P>The <code>Thread</code> in which the current operation is being performed.  May be <code>null</code> if an
	operation is not currently running.</P>
	*/
	protected Thread			thread				= null;
	protected PropertyChangeSupport	propertyChangeSupport;

	private Component			monitorParentComponent = null;
	private ProgressMonitor		progressMonitor;
	private Runnable			updateProgressMon;

	private String				status				= "Ready";

	public AbstractSlowOp(Component parentComponent){
		monitorParentComponent = parentComponent;
		propertyChangeSupport = new PropertyChangeSupport(this);
		updateProgressMon = new Runnable(){
			public void run(){
				progressMonitor.setNote(status);
				progressMonitor.setProgress(progress);
			}
		};
	}

	public void start(){
		if(thread==null){
			setMessage("");
			setError(false);
			processedImage = null;

			status="Initializing";
			progress = minProgress;
			progressMonitor = new ProgressMonitor( monitorParentComponent, opLongName, status, minProgress, maxProgress );
			progressMonitor.setProgress(progress);
			progressMonitor.setNote(status);
			progressMonitor.setMillisToPopup( 0 );
			progressMonitor.setMillisToDecideToPopup( 0 );

			thread = new Thread(this);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}
	}

	public abstract void run();

	protected boolean isCanceled(){
		thread.yield();
		return progressMonitor.isCanceled();
	}

	protected void updateProgress(){
		thread.yield();
		SwingUtilities.invokeLater(updateProgressMon);
	}

	protected void setProcessedImageOnFinish(BufferedImage processedImage){
		this.processedImage = processedImage;
		propertyChangeSupport.firePropertyChange( "processedImage", originalImage, processedImage );
		progressMonitor.close();
		progressMonitor = null;
		thread = null;
	}

	protected void setStatus(String status){
		this.status = status;
	}

	protected String getStatus(){
		return status;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener){
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener){
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

}