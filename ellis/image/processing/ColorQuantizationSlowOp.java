package ellis.image.processing;

import java.awt.Component;

/**
 *  This image processing routine color quantizes an image into a reduced set of optimized color choices.
 *
 *  @author Ellis Teer 8/09/99
 *  @see ImageUtilities
 *  @see AbstractFastOp
 */
public class ColorQuantizationSlowOp extends AbstractSlowOp{

	public static int			DEFAULT_NUMBER_OF_COLORS	= 6;
	public static int			DEFAULT_COLOR_RANGE_MIN		= 0;
	public static int			DEFAULT_COLOR_RANGE_MAX		= 255;

	private int					colorNum	 				= DEFAULT_NUMBER_OF_COLORS;
	private int					rangeMin					= DEFAULT_COLOR_RANGE_MIN;
	private int					rangeMax					= DEFAULT_COLOR_RANGE_MAX;

	private double[]			area 						= new double[colorNum];
	private double[]			col 						= new double[colorNum];

	public ColorQuantizationSlowOp(Component parentComponent){
		super(parentComponent);
		opLongName = "Color-Quantization";
		opShortName = "colorQuant";
		maxProgress = 5;
		minProgress = 0;
	}

	public void run(){
		if (originalImage == null 						||
			colorNum<1									||
			rangeMin>=rangeMax){
			setError(true);
			setMessage("Invalid Parameters.");
		} else {
			try{
				setStatus("Preparing Data");
				progress++;
				updateProgress();
				double[][] u = ImageUtilities.getDataFromBufferedImage( originalImage );

				int dimx		= originalImage.getWidth();
				int dimy		= originalImage.getHeight();
				int x, y, i, j, ls;
				double ds		= 1.;
				double twods	= 2.0*ds;
				double sum, xx, d1, d2, d;
				int mincol	= rangeMin-1;		//-1
				int maxcol	= rangeMax+1;		//256

				//***Creating working arrays***
				double[][] nu		= new double[dimx][dimy];
				double[]   nt		= new double[colorNum+1];
				double[]   h		= new double[rangeMax-rangeMin+5];	//0-260
				double[]   hh		= new double[rangeMax-rangeMin+5];  //0-260
						   col		= new double[colorNum];
						   area		= new double[colorNum];

				setStatus("Creating Histogram");
				progress++;
				updateProgress();
				sum = 0.0;
				for( ls=mincol-1; ls<=maxcol+1; ls++ ){			//-2 to 257
					for( x=0; x<dimx; x++ ){
						for( y=0; y<dimy; y++ ){
							sum += 1.0;
							if( u[x][y]<ls ) hh[ls+2] +=1.0;
						}
					}
				}

				for( ls=mincol-1; ls<=maxcol+1; ls++ ){
					hh[ls+2] /= sum;
				}

				for( ls=mincol; ls<=maxcol; ls++ ){				//-1 to 256
					h[ls+2] = ( hh[ls+1+2] - hh[ls-1+2] ) / twods;
				}

				xx = (double)(maxcol-mincol) / (double)colorNum;

				for( i=0; i<colorNum+1; i++){
					nt[i] = (double)mincol+(double)i*xx;
				}

				setStatus("Iterating");
				progress++;
				updateProgress();
				for( i=0; i<100; i++){							//kk loop
					for( j=0; j<colorNum; j++){					//k loop
						d1 = 0.0;
						d2 = 0.0;
						for( ls=(int)nt[j]; ls<=(int)nt[j+1]; ls++){
							d1 += ls*h[ls+2];					//correct offset? with ls??
							d2 += h[ls+2];						//correct offset? with ls??
						}
						if( Math.abs(d2)>0 ){
							col[j] = d1/d2;
						}
					}
					for( j=1; j<colorNum; j++){
						if( Math.abs( h[(int)nt[j]+2] )>0 ){
							nt[j] = 0.5*( col[j]+col[j-1] );
						}
					}
				}

				setStatus("Bining");
				progress++;
				updateProgress();
				for( x=0; x<dimx; x++){
					for( y=0; y<dimy; y++){
						for( i=0; i<colorNum; i++){
							if( u[x][y]>=nt[i] && u[x][y]<nt[i+1] ){
								u[x][y] = col[i];
								area[i] += 1;
							}
						}
					}
				}

				processedImage = ImageUtilities.getBufferedImageFromData( u );

				if (isCanceled()) {
					setError(true);
					setMessage("Operation cancelled.");
				} else {
					propertyChangeSupport.firePropertyChange( "processingMessage", "", this.opLongName+" dumping results..." );
					StringBuffer dataOutput = new StringBuffer("Color Choices:");
					for(i=0; i<colorNum; i++){
						dataOutput.append( "\t"+col[i] );
					}
					propertyChangeSupport.firePropertyChange( "processingMessage", "", dataOutput.toString() );
					dataOutput = new StringBuffer("Area of each Color:");
					for(i=0; i<colorNum; i++){
						dataOutput.append( "\t"+area[i] );
					}
					propertyChangeSupport.firePropertyChange( "processingMessage", "", dataOutput.toString() );
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

	public void setNumberOfColors(int colorNum){
		this.colorNum = colorNum;
	}

	public void setColorRangeMin(int min){
		this.rangeMin = min;
	}

	public void setColorRangeMax(int max){
		this.rangeMax = max;
	}

	public int getNumberOfColors(){
		return this.colorNum;
	}

	public double[] getColors(){
		if( col==null || col.length<1 ) return new double[1];
		double[] result = new double[col.length];
		System.arraycopy(col,0,result,0,col.length);
		return result;
	}

	public double[] getColorCounts(){
		if( area==null || area.length<1 ) return new double[1];
		double[] result = new double[area.length];
		System.arraycopy(area,0,result,0,area.length);
		return result;
	}

	public String toString(){
		return opLongName + ": number of colors " + getNumberOfColors();
	}

}