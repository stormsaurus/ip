package ellis.image.processing;

import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 *
 *  @author Ellis Teer 8/03/99
 *  @see ImageUtilities
 *  @see AbstractDataOp
 *  @see AbstractFastOp
 */

public class RescaleFastOp extends AbstractFastOp{

	public static double		DEFAULT_HIGHER_BOUND= 255;
	public static double		DEFAULT_LOWER_BOUND	= 0;

	private double				upperBound			= DEFAULT_HIGHER_BOUND;
	private double				lowerBound			= DEFAULT_LOWER_BOUND;

	public RescaleFastOp(){
		super();
		opLongName = "Rescale";
		opShortName = "rescale";
	}

	public BufferedImage filter(BufferedImage source){
		setError(false);
		setMessage("");
		processedImage = null;
		if( source==null ){
			setError(true);
			setMessage("Invalid Parameters.");
		} else {
			double[][] sourceData = ImageUtilities.getDataFromBufferedImage( source );
			double[] minmax = ImageUtilities.getMinMax(sourceData);
			double slope = (upperBound-lowerBound)/(minmax[1]-minmax[0]);
			double offset = lowerBound - slope*minmax[0];
			for (int i=0; i<sourceData.length; i++){
				for (int j=0; j<sourceData[0].length; j++){
					sourceData[i][j] = sourceData[i][j]*slope + offset;
				}
			}
			processedImage = ImageUtilities.getBufferedImageFromData( sourceData );
			setMessage( "Image was rescaled between "+lowerBound+" and "+upperBound+"." );
		}
		return processedImage;
	}

	public void setUpperBound(double upperBound){
		this.upperBound = upperBound;
	}

	public double getUpperBound(){
		return this.upperBound;
	}

	public void setLowerBound(double lowerBound){
		this.lowerBound = lowerBound;
	}

	public double getLowerBound(){
		return this.lowerBound;
	}

	public String toString(){
		return opLongName + ": upper bound " + getUpperBound() +", lowerbound " + getLowerBound();
	}


}