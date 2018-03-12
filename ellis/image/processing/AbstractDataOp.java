package ellis.image.processing;

import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 *
 *	<P>The abstract superclass of all image processing operations.  A <code>BufferedImage</code> is converted to double data which
 is processed and then converted back to a BufferedImage.  Operations which wish to circumvent the data access
 structure of BufferedImage to allow for faster operation by lengthy calcultions by directly accessing a double data
 array should subclass this class.</P>
 *
 *	<P>A BufferedImage is passed to a subclassed object prior to processing by calling
 AbstractDataOp.setImage(BufferedImage image).</P>
 *
 *  <P>The constructors of subclasses should set the variables opLongName and opShortName appropriately.</P>
 *
 *  @author Ellis Teer 8/1/99
 *  @see ImageUtilities
 *  @see AbstractFastOp
 *  @see AbstractSlowOp
 */

public abstract class AbstractDataOp{

    /**
     * This value should be set in the constructor of any subclass.
     */
	public String				opLongName			= "unnamed";
    /**
     * This value should be set in the constructor of any subclass.  This name may be appended to image names to represent a sequence of operations.  The name should not contain illegal filename characters.
     */
	public String				opShortName			= "unnamed";

    /**
     * The source image.
     */
	protected BufferedImage		originalImage		= null;
    /**
     * Holds the final result after processing.
     */
	protected BufferedImage		processedImage		= null;

	private boolean				error				= false;
	private String				message				= "";

	public AbstractDataOp(){
	}

    /**
     * Sets the error flag.
     *
     * @param     error   the error state of the most recent processing attempt
     */
	protected void setError(boolean error){
		this.error = error;
	}

    /**
     * Sets a message such as successful or detailed information due to an error.  Each subclass should set this method prior to returning.
     *
     * @param     message   a message describing either a successful operation or the details of an error
     */
	protected void setMessage(String message){
		this.message = message;
	}

    /**
     * Sets a message such as successful or detailed information due to an error.
     *
     * @param     image   a BufferedImage to be processed
     * @see			java.lang.String
     */
	public void setImage(BufferedImage image){
		originalImage = image;
	}

    /**
     * Returns the most recent processed image which can be null.
     *
     * @return    a processed java.awt.BufferedImage
     */
	public BufferedImage getProcessedImage(){
		return processedImage;
	}

    /**
     * Checks if the lastest processing attempt is in error.
     *
     * @return    the error state
     */
	public boolean isError(){
		return error;
	}

    /**
     * Returns a message about the last processing attempt.  A message should be availible after any errors or upon completion of the operation.
     *
     * @return    the message
     */
	public String getMessage(){
		return message;
	}

	public String toString(){
		return opLongName;
	}

}

