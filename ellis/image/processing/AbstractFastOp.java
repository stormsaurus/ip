package ellis.image.processing;

import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 *	<P>The abstract superclass of all fast or non-threaded image processing operations.</P>
 *
 *  <P>After instantiation an operation is begun by calling the filter method.</P>
 *
 *  @author Ellis Teer 7/1/99
 *  @see ImageUtilities
 *	@see AbstractSlowOp
 */

public abstract class AbstractFastOp extends AbstractDataOp{

    /**
     * Returns the most recent processed image which can be null.
     *
	 * @param	source   a BufferedImage containing the source data.
     * @return    a java.awt.BufferedImage representing the processed image
     */
	public abstract BufferedImage filter(BufferedImage source);

}