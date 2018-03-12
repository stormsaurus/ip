package ellis.image.processing;

import java.awt.Component;

/**
 *  This image processing routine denoises and deblurs an image.
 *
 *  @author Ellis Teer 6/30/99
 *  @see ImageUtilities
 *  @see AbstractFastOp
 */
public class DenoiseDeblurSlowOp extends AbstractSlowOp{

	public static int			DEFAULT_BLUR_RADIUS = 5;
	public static int			DEFAULT_ITERATIONS	= 10;
	public static double		DEFAULT_ALPHA 		= 4;
	public static int			DEFAULT_MESH_STEP_SIZE = 1;
	public static int			DEFAULT_MAX_VALUE_BEFORE_ERROR = 285;
	public static int			DEFAULT_MIN_VALUE_BEFORE_ERROR = -10;
	public static boolean		DEFAULT_AUTO_FIND_ALPHA = false;

	private int					blurRadius 			= DEFAULT_BLUR_RADIUS;
	private int					iterations 			= DEFAULT_ITERATIONS;
	private int					currentIteration 	= 0;
	private double 				alpha 				= DEFAULT_ALPHA;
	private int					meshStepSize 		= DEFAULT_MESH_STEP_SIZE;
	private int					minValueBeforeError	= DEFAULT_MIN_VALUE_BEFORE_ERROR;
	private int					maxValueBeforeError	= DEFAULT_MAX_VALUE_BEFORE_ERROR;
	private boolean				autoFindAlpha		= DEFAULT_AUTO_FIND_ALPHA;

	public DenoiseDeblurSlowOp(Component parentComponent){
		super(parentComponent);
		opLongName = "Denoise-Deblur";
		opShortName = "denoiseDeblur";
		maxProgress = iterations+3;
		minProgress = 0;
	}

	public void run(){

		if (originalImage == null 						||
			alpha <= 0 									||
			iterations < 1								||
			blurRadius <= 0								||
			blurRadius*2 >= originalImage.getWidth()/2 	||
			blurRadius*2 >= originalImage.getHeight()/2	){
			setError(true);
			setMessage("Invalid Parameters.");
		} else {
				try{
					setStatus("Preparing Data");
					updateProgress();
					double[][] source = ImageUtilities.getDataFromBufferedImage( originalImage );

					int dimx		= originalImage.getWidth();
					int dimy		= originalImage.getHeight();
					int borderWidth = blurRadius*2;

					//***Creating working arrays which are same size as source but with additional border***
					double[][] kuzero	= new double[dimx+2*borderWidth][dimy+2*borderWidth];		//formerly bn
					double[][] u		= new double[dimx+2*borderWidth][dimy+2*borderWidth];
					double[][] ku 		= new double[dimx+2*borderWidth][dimy+2*borderWidth];		//formerly bu

					//***Rescale, copy, and reflect source data into larger array***
					ImageUtilities.rescaleData( source, 0, 255 );
					for (int x=0; x<dimx; x++){
						for (int y=0; y<dimy; y++){
							u[x+borderWidth][y+borderWidth] = source[x][y];
						}
					}
					ImageUtilities.reflectData( u, borderWidth );

					setStatus("Calculating Ku zero");
					progress++;
					updateProgress();

					//***Compute kuzero from u***
					ImageUtilities.applyK( u, kuzero, borderWidth, blurRadius );

					//***Begin Iterative Calculations***
					for (currentIteration=0; currentIteration<iterations && !isCanceled(); currentIteration++){

						setStatus("Iteration "+(currentIteration+1)+" of "+iterations);
						progress++;
						updateProgress();

						//***Forward Direction***
						ImageUtilities.reflectData(u, borderWidth);
						ImageUtilities.applyK(u, ku, borderWidth, blurRadius);
							if( isCanceled() ) break;
						ImageUtilities.calculateUFromDivergence(u, ku, kuzero, borderWidth, alpha, meshStepSize, minValueBeforeError, maxValueBeforeError, blurRadius, false);
							if( isCanceled() ) break;

						//***Reverse Direction***
						ImageUtilities.reflectData(u, borderWidth);
						ImageUtilities.applyK(u, ku, borderWidth, blurRadius);
							if( isCanceled() ) break;
						ImageUtilities.calculateUFromDivergence(u, ku, kuzero, borderWidth, alpha, meshStepSize, minValueBeforeError, maxValueBeforeError, blurRadius, true);
					}

					setStatus("Rescaling");
					progress++;
					updateProgress();
					//***Copy u into original sized array, rescale, and convert.
					double[][] result = new double[dimx][dimy];
					for(int x=0; x<dimx; x++){
						for(int y=0; y<dimy; y++){
							result[x][y] = u[x+borderWidth][y+borderWidth];
						}
					}
					ImageUtilities.rescaleData( result, 0, 255 );

					processedImage = ImageUtilities.getBufferedImageFromData( result );

					if (isCanceled()) {
						setError(true);
						setMessage("Operation cancelled.");
					} else {
						setMessage("Successful.");
					}

				} catch (ArithmeticException e){
					setError(true);
					setMessage("An Exception occured.  "+e);
				} catch (IllegalArgumentException e){
					setError(true);
					setMessage("An Exception occured.  "+e);
				}

		}

		setProcessedImageOnFinish( processedImage );
	}

	public void setBlurRadius(int blurRadius){
		this.blurRadius = blurRadius;
	}

	public int getBlurRadius(){
		return this.blurRadius;
	}

	public void setIterations(int iterations){
		this.iterations = iterations;
		maxProgress = iterations+3;
	}

	public int getIterations(){
		return this.iterations;
	}

	public void setAlpha(double alpha){
		this.alpha = alpha;
	}

	public double getAlpha(){
		return this.alpha;
	}

	public void setMeshStepSize(int meshStepSize){
		this.meshStepSize = meshStepSize;
	}

	public int getMeshStepSize(){
		return this.meshStepSize;
	}

	public void setMinBeforeError(int minValueBeforeError){
		this.minValueBeforeError = minValueBeforeError;
	}

	public int getMinBeforeError(){
		return this.minValueBeforeError;
	}

	public void setMaxBeforeError(int maxValueBeforeError){
		this.maxValueBeforeError = maxValueBeforeError;
	}

	public int getMaxBeforeError(){
		return this.maxValueBeforeError;
	}

	public void setAutoFindAlpha(boolean autoFindAlpha){
		this.autoFindAlpha = autoFindAlpha;
	}

	public boolean getAutoFindAlpha(){
		return this.autoFindAlpha;
	}

	public String toString(){
		return opLongName+": blur radius "+getBlurRadius()+", iterations "+getIterations()+", alpha "+getAlpha()+", mesh step size " + getMeshStepSize()  +", min before error "+getMinBeforeError()+", max before error "+ getMaxBeforeError()+", autofind alpha "+ getAutoFindAlpha();
	}

}

