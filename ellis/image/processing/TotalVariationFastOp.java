package ellis.image.processing;

import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 *
 *  @author Magdalia 8/03/99
 *  @see ImageUtilities
 *  @see AbstractDataOp
 *  @see AbstractFastOp
 */

public class TotalVariationFastOp extends AbstractFastOp{

	public static double		DEFAULT_MESH_STEP_SIZE	= 1;

	private double				h						= DEFAULT_MESH_STEP_SIZE;

	public TotalVariationFastOp(){
		super();
		opLongName = "Total Variation";
		opShortName = "totalvariation";
	}

	public BufferedImage filter(BufferedImage source){
		setError(false);
		setMessage("");
		processedImage = null;
		if( source==null ){
			setError(true);
			setMessage("Invalid Parameters.");
		} else {
			double[][] u = ImageUtilities.getDataFromBufferedImage( source );

			double ux, uy, grad, tv;
			int i, j;
			tv=0.0;
			for (i=1; i < u.length-1; i++){
				for (j=1; j < u[0].length-1; j++){
					ux= (u[i+1][j]-u[i-1][j])/ (2.0*h);
					uy= (u[i][j+1]-u[i][j-1])/ (2.0*h);
					grad= Math.sqrt (ux*ux + uy*uy);
					tv = tv + grad;
				}
			}

			processedImage = ImageUtilities.getBufferedImageFromData( u );
			setMessage( "Total Variation is " + tv);
		}
		return processedImage;
	}

	public String toString(){
		return opLongName + ": mesh step size " + h;
	}


}

