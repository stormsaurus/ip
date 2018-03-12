package ellis.image.processing;

import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 *
 *  @author Ellis Teer 7/12/99
 *  @see ImageUtilities
 *  @see AbstractSlowOp
 *  @see AbstractFastOp
 */

public class BackgroundSubtractFastOp extends AbstractFastOp{

	public static double		DEFAULT_RATIO		= 0.5;
	public static boolean		DEFAULT_BOUNDED		= false;

	protected BufferedImage		backgroundImage		= null;

	private double				ratio				= DEFAULT_RATIO;
	private boolean				bounded				= DEFAULT_BOUNDED;

	public BackgroundSubtractFastOp(){
		super();
		opLongName = "Background-Subtract";
		opShortName = "bkgSubtract";
	}

	public BufferedImage filter(BufferedImage source){
		setError(false);
		setMessage("");
		processedImage = null;
		if( source==null 			||
			backgroundImage==null 	||
			source.getWidth()!=backgroundImage.getWidth() ||
			source.getHeight()!=backgroundImage.getHeight() ){
			setError(true);
			setMessage("Invalid Parameters.");
		} else {
			double[][] sourceData = ImageUtilities.getDataFromBufferedImage( source );
			for (int i=0; i<sourceData.length; i++){
				for (int j=0; j<sourceData[0].length; j++){
					sourceData[i][j]-=(double)ImageUtilities.getGrayFromRGB(backgroundImage.getRGB(i,j))*ratio;
					if (bounded){
						if (sourceData[i][j]>255) sourceData[i][j]=255;
						if (sourceData[i][j]<0  ) sourceData[i][j]=0;
					}
				}
			}
			ImageUtilities.rescaleData( sourceData, 0, 255);
			processedImage = ImageUtilities.getBufferedImageFromData( sourceData );
			if ( bounded ){
				setMessage( "Out of bound vales were bounded." );
			} else {
				setMessage( "Out of bound values were rescaled with image.");
			}
		}
		return processedImage;
	}

	public void setBackgroundImage(BufferedImage backgroundImage){
		this.backgroundImage = backgroundImage;
	}

	public void setBounded(boolean bounded){
		this.bounded = bounded;
	}

	public boolean getBounded(){
		return bounded;
	}

	public void setRatio(double ratio){
		this.ratio = ratio;
	}

	public double getRatio(){
		return ratio;
	}

	public String toString(){
		return opLongName + ": bounded " + getBounded() + ", ratio " + getRatio();
	}

}