package ellis.image.processing;

import java.awt.Component;

/**
 *  This image processing routine detects edges in an image.
 *
 *  @author Ellis Teer 8/09/99
 *  @see ImageUtilities
 *  @see AbstractFastOp
 */
public class EdgeFindSlowOp extends AbstractSlowOp{

	public static int			DEFAULT_ITERATIONS			= 30;
	public static double		DEFAULT_ALPHA 				= 500;
	public static int			DEFAULT_MESH_STEP_SIZE 		= 1;
	public static double		DEFAULT_RHO					= 0.02;
	public static boolean		DEFAULT_TWOPOINT_FORMULA	= true;
	public static boolean		DEFAULT_DISPLAY_EDGE		= false;
	public static boolean		DEFAULT_DISPLAY_DIFFUSED	= true;

	private int					iterations 			= DEFAULT_ITERATIONS;
	private int					currentIteration 	= 0;
	private double 				alpha 				= DEFAULT_ALPHA;
	private int					meshStepSize 		= DEFAULT_MESH_STEP_SIZE;
	private double				rho					= DEFAULT_RHO;
	private boolean				twoPoint			= DEFAULT_TWOPOINT_FORMULA;
	private boolean				displayEdgeOverlay	= DEFAULT_DISPLAY_EDGE;
	private boolean				displayDiffusedImage= DEFAULT_DISPLAY_DIFFUSED;

	private double				totalEdgeLength		= 0;


	public EdgeFindSlowOp(Component parentComponent){
		super(parentComponent);
		opLongName = "Edge_Find";
		opShortName = "edgeFind";
		maxProgress = iterations+2;
		minProgress = 0;
	}

	public void run(){

		if (originalImage == null 						||
			alpha <= 0 									||
			iterations < 1								||
			rho <=0	){
			setError(true);
			setMessage("Invalid Parameters.");
		} else {
			totalEdgeLength = 0;
			try{
				setStatus("Preparing Data");
				progress++;
				updateProgress();
				double[][] source = ImageUtilities.getDataFromBufferedImage( originalImage );

				int dimx		= originalImage.getWidth();
				int dimy		= originalImage.getHeight();
				int borderWidth = 1;

				//***Creating working arrays which are same size as source but with additional border***
				double[][] u			= new double[dimx+2*borderWidth][dimy+2*borderWidth];
				double[][] uZero		= new double[dimx+2*borderWidth][dimy+2*borderWidth];
				double[][] edgeStrength	= new double[dimx+2*borderWidth][dimy+2*borderWidth];

				//***Copy source data into larger array***
				for (int x=0; x<dimx; x++){
					for (int y=0; y<dimy; y++){
						u[x+borderWidth][y+borderWidth] = source[x][y];
						uZero[x+borderWidth][y+borderWidth] = source[x][y];
					}
				}

				//***Begin Iterative Calculations***
				for (currentIteration=0; currentIteration<iterations && !isCanceled(); currentIteration++){
					setStatus("Iteration "+(currentIteration+1)+" of "+iterations);
					progress++;
					updateProgress();

					ImageUtilities.reflectData(u, 1);
					ImageUtilities.calculateEdgeU( u, uZero, borderWidth, alpha, rho, meshStepSize, false);

					ImageUtilities.reflectData(u, 1);
					ImageUtilities.calculateEdgeU( u, uZero, borderWidth, alpha, rho, meshStepSize, true);

				}

				ImageUtilities.reflectData(u, 1);
				totalEdgeLength = ImageUtilities.calculateEdgeStrength( u, edgeStrength, borderWidth, meshStepSize, rho, twoPoint);

				double[][] result = new double[dimx][dimy];

				//***Overlay edge map
				if(displayDiffusedImage){
					//***Copy u into original sized array, rescale, and convert.
					for(int x=0; x<dimx; x++){
						for(int y=0; y<dimy; y++){
							result[x][y] = u[x+borderWidth][y+borderWidth];
						}
					}
					ImageUtilities.rescaleData( result, 0, 255 );
					if (displayEdgeOverlay) {
						for(int x=0; x<dimx; x++){
							for(int y=0; y<dimy; y++){
								result[x][y] *= edgeStrength[x+borderWidth][y+borderWidth];
							}
						}
					}
				} else {
					for(int x=0; x<dimx; x++){
						for(int y=0; y<dimy; y++){
							result[x][y] = edgeStrength[x+borderWidth][y+borderWidth];
						}
					}
					ImageUtilities.rescaleData( result, 0, 255 );
				}

				processedImage = ImageUtilities.getBufferedImageFromData( result );

				if (isCanceled()) {
					setError(true);
					setMessage("Operation cancelled.");
				} else {
					setMessage("Successful.  Total edge length is "+totalEdgeLength);
				}

				System.out.println( getMessage() );

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

	public void setRho(double rho){
		this.rho = rho;
	}
	public double getRho(){
		return this.rho;
	}

	public void setIterations(int iterations){
		this.iterations = iterations;
		maxProgress = iterations+2;
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

	public void setUseTwoPointFormula(boolean twoPoint){
		this.twoPoint = twoPoint;
	}
	public boolean getUseTwoPointFormula(){
		return this.twoPoint;
	}

	public void setDisplayDiffusedImage(boolean diffused){
		this.displayDiffusedImage = diffused;
	}
	public boolean getDisplayDiffusedImage(){
		return this.displayDiffusedImage;
	}

	public void setDisplayEdgeOverlay(boolean overlay){
		this.displayEdgeOverlay = overlay;
	}
	public boolean getDisplayEdgeOverlay(){
		return this.displayEdgeOverlay;
	}

	public double getEdgeCount(){
		return totalEdgeLength;
	}

	public String toString(){
		return opLongName + ": rho "+getRho()+", iterations "+getIterations()+", alpha "+getAlpha()+", mesh step size "+getMeshStepSize()+", two point formula "+getUseTwoPointFormula()+", display diffused image "+getDisplayDiffusedImage()+", display edges "+getDisplayEdgeOverlay();
	}

}