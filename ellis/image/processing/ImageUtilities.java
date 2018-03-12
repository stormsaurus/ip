package ellis.image.processing;

import java.awt.*;
import java.awt.image.*;


/**
 *
 *	<P>A collection of static <code>Image</code> to <code>BufferedImage</code> and <code>double[][]</code> conversion and manipulation utilities.</P>
 *
 *  @author Ellis Teer 8/1/99
 *  @see ImageUtilities
 *  @see ImageViewArea
 *  @see JScrollPane
 */


public class ImageUtilities{

	private static final Component 		component = new Component() {};
	private static final MediaTracker 	tracker = new MediaTracker(component);
	private static int 					id = 0;

    /**
     *	<P>Waits for an image to be loaded into memory.  This method blocks.</P>
     *	</P>NOTE - This method is modeled after code in Java 2D Graphics by Jonathan B. Knudsen
     *
	 * @param	image   an Image to be loaded
     * @return    boolean  indicating if the Image was loaded
     */
	public static boolean waitForImage(Image image){
		int currentId;
		synchronized(component) { currentId = id++; }
		tracker.addImage(image, currentId);
		try{ tracker.waitForID(currentId); }
		catch (InterruptedException e) { return false; }
		if ( tracker.isErrorID(currentId) ) return false;
		return true;
	}

    /**
     *	<P>Creates a BufferedImage from an Image.  This method blocks.</P>
     *	<P>NOTE - This method is modeled after code in Java 2D Graphics by Jonathan B. Knudsen</P>
     *
	 * @param	image   an Image to be loaded
     * @return    BufferedImage  the converted Image
     */
	public static BufferedImage createBufferedImage(Image image){
		return createBufferedImage(image, BufferedImage.TYPE_INT_RGB);
	}

    /**
     *	<P>Creates a BufferedImage from an Image.  This method blocks.</P>
     *	<P>NOTE - This method is modeled after code in Java 2D Graphics by Jonathan B. Knudsen</P>
     *
	 * @param	image   an Image to be loaded
	 * @param	imageType   one of the standard BufferedImage types such as BufferedImage.TYPE_INT_ARGB
     * @return    BufferedImage  the converted Image
     */
	public static BufferedImage createBufferedImage(Image image, int imageType){
		if (waitForImage(image)==false) return null;
		BufferedImage temp = new BufferedImage( image.getWidth(null), image.getHeight(null), imageType);
		Graphics2D g2 = temp.createGraphics();
		g2.drawImage( image, null, null );
		return temp;
	}

    /**
     *	<P>Converts a packed argb int pixel into a single averaged int value.  This is done by bit shifting the rgb
	 values, summing, and dividing by three.  The transparency information is discarded.</P>
     *
	 * @param	argbPackedPixel   the packed pixel colors
     * @return    int  the average color value
     */
	public static int getGrayFromRGB(int argbPackedPixel){

		int temp = ( ((argbPackedPixel&0x00ff0000)>>16) + ((argbPackedPixel&0x0000ff00)>>8) + ((argbPackedPixel&0x000000ff)) )/3;
		if ( ((argbPackedPixel&0xff000000)>>24)==-1) {
			return temp;
		} else {
			return ((argbPackedPixel&0xff000000)>>24) * temp;
		}
	}

    /**
     *	<P>Converts an int pixel into a packed argb value.  This is done by bit shifting the int value into rgb values.
	  The transparency bits are set to 0xff.</P>
     *
	 * @param	grayPixel   the gray value
     * @return    int  the packed pixel colors
     */
	public static int getRGBFromGray(int grayPixel){
		grayPixel = grayPixel&0xff;
		return (grayPixel<<16) ^ (grayPixel<<8) ^ (grayPixel);
	}

    /**
     *	<P>Determines the average color maximum and minimum values of a rgb type BufferedImage.</P>
     *
	 * @param	bufferedImage   the BufferedImage
     * @return    int[]  an array of size 2 with the first value being the min and the second being the max
     */
	public static int[] getMinMax(BufferedImage bufferedImage){
		int grayValue;
		int min=0;
		int max=0;
		if (bufferedImage!=null) {
			max = ImageUtilities.getGrayFromRGB(bufferedImage.getRGB(0,0));
			min = ImageUtilities.getGrayFromRGB(bufferedImage.getRGB(0,0));
			for (int i=0; i<bufferedImage.getWidth(); i++){
				for (int j=0; j<bufferedImage.getHeight(); j++){
					grayValue = ImageUtilities.getGrayFromRGB(bufferedImage.getRGB(i,j));
					if ( grayValue<min ) min = grayValue;
					if ( grayValue>max ) max = grayValue;
				}
			}
		}
		int[] temp = new int[2];
		temp[0] = min;
		temp[1] = max;
		return temp;
	}

    /**
     *	<P>Determines the maximum and minimum values in a <code>double[][]</code> array..</P>
     *
	 * @param	source   the source data
     * @return    double[]  an array of size 2 with the first value being the min and the second being the max
     */
	public static double[] getMinMax(double[][] source){
		double min=0;
		double max=0;
		if (source!=null) {
			max = source[0][0];
			min = source[0][0];
			for (int i=0; i<source.length; i++){
				for (int j=0; j<source[0].length; j++){
					if ( source[i][j]<min ) min = source[i][j];
					if ( source[i][j]>max ) max = source[i][j];
				}
			}
		}
		double[] temp = new double[2];
		temp[0] = min;
		temp[1] = max;
		return temp;
	}

    /**
     *	<P>Used to convert between a rgb type BufferedImage and a <code>double[][]</code> array.  Color information is lost by averaging.</P>
     *
	 * @param	source   the BufferedImage
     * @return    double[]  the averaged color representation of the BufferedImage
     */
	public static double[][] getDataFromBufferedImage(BufferedImage source){

		double[][] result = null;

		if (source!=null) {
			result = new double[source.getWidth()][source.getHeight()];
			for (int i=0; i<source.getWidth(); i++){
				for (int j=0; j<source.getHeight(); j++){
					result[i][j] = (double)getGrayFromRGB(source.getRGB(i,j));
				}
			}
		}
		return result;
	}

	public static BufferedImage getBufferedImageFromData(double[][] source){
		return getBufferedImageFromData(source, BufferedImage.TYPE_INT_RGB);
	}

	public static BufferedImage getBufferedImageFromData(double[][] source, int imageType){

		BufferedImage temp = null;

		if (source!=null){
			temp = new BufferedImage(source.length, source[0].length, BufferedImage.TYPE_INT_RGB );
			for (int i=0; i<source.length; i++){
				for (int j=0; j<source[0].length; j++){
					temp.setRGB(i,j,   getRGBFromGray( (int)source[i][j] )    );
				}
			}
		}
		return temp;
	}

	public static BufferedImage rescaleBufferedImage(BufferedImage source, int min, int max){

		BufferedImage temp = null;
		if (source!=null){
			int[] minmax = getMinMax(source);
			float slope = (float)(max-min)/(float)(minmax[1]-minmax[0]);
			float offset = min-slope*(float)minmax[0];
			RescaleOp rescaleOp = new RescaleOp( slope, offset, null);
			temp = rescaleOp.filter(source, null);
		}
		return temp;

	}

	public static void rescaleData(double[][] source, double min, double max){

		if (source!=null){
			double[] minmax = getMinMax(source);
			double slope = (max-min)/(minmax[1]-minmax[0]);
			double offset = min - slope*minmax[0];
				for (int i=0; i<source.length; i++){
					for (int j=0; j<source[0].length; j++){
						source[i][j] = source[i][j]*slope + offset;
					}
				}
		}
	}

	public static void reflectData(double[][] source, int distance){

		//make more efficient by reducing unneeded copying...if distance 1 a lot of cycles are wasted

		if (source==null || distance <=0 || distance>=source.length/2 || distance>=source[0].length/2) throw new IllegalArgumentException("ImageUtilites.reflectData( ... ) received invalid parameters.");;

		int dimx = source.length;
		int dimy = source[0].length;

		int k,l;
		for (int i=0; i<dimx; i++){
			k=i;
			if ( i<distance ) k=2*distance-i;
			if ( i>=dimx-distance ) k=2*(dimx-distance)-(i+1);
			for (int j=0; j< dimy; j++){
				l=j;
				if ( j<distance ) l=2*distance-j;
				if ( j>=dimy-distance ) l=2*(dimy-distance)-(j+1);
				source[i][j]=source[k][l];
			}
		}

	}

	//source and result arrays must be same dimensions, and the border must be at least as big as the blurring disk
	public static void applyK(double[][] source, double[][] result, int borderWidth, int blurRadius){


		if ( source==null || result==null || blurRadius<=0) throw new IllegalArgumentException("ImageUtilites.applyK( ... ) received invalid parameters.");

		int dimx = source.length;
		int dimy = source[0].length;

		if ( 	dimx!=result.length || dimy!=result[0].length ||
				dimx<borderWidth*2 || dimy<borderWidth*2 ||
				blurRadius>borderWidth ) throw new IllegalArgumentException("ImageUtilites.applyK( ... ) received invalid parameters.");

		double distance;
		double s;
		int x, y, dx, dy;

		for ( x=borderWidth-blurRadius; x<dimx-borderWidth+blurRadius; x++ ){
			for ( y=borderWidth-blurRadius; y<dimy-borderWidth+blurRadius; y++ ){
				result[x][y] = 0;
				s = 0;
				for( dx=-blurRadius; dx<=blurRadius; dx++ ){
					for( dy=-blurRadius; dy<=blurRadius; dy++ ){
						distance=Math.sqrt( dx*dx+dy*dy );
						if (distance>blurRadius) continue;
						if (distance<blurRadius) {
							if (distance<blurRadius-1){
								result[x][y] += source[x+dx][y+dy];
								s += 1.0;
							} else {
								result[x][y] += (0.5)*source[x+dx][y+dy];
								s += 0.5;
							}
							continue;
						}
						result[x][y] += (0.25)*source[x+dx][y+dy];
						s += 0.25;
					}
				}
				result[x][y] /= s;
			}
		}

	}

	public static void calculateUFromDivergence(double[][] u, double[][] ku, double[][] kuzero, int borderWidth, double alpha, double h, double min, double max, int blurRadius, boolean reverseDirection) throws ArithmeticException{

		if ( 	u==null || ku==null || kuzero==null ||
				alpha<=0 || h<=0 || max<=min ||
				blurRadius <=0 ) throw new IllegalArgumentException("ImageUtilites.calculateUFromDivergence( ... ) received invalid parameters.");

		int dimx=u.length;
		int dimy=u[0].length;

		//didn't check that u, ku, kuzero all same size but they must be
		if ( 	dimx<borderWidth*2 || dimy<borderWidth*2 ||
				blurRadius>borderWidth ) throw new IllegalArgumentException("ImageUtilites.applyK( ... ) received invalid parameters.");

		double eps = 0.000001;
		double twoh = 2*h;
		double hSquaredOverAlpha = h*h/alpha;
		double distance;
		double s, ss, kku, c1, c2, c3, c4, cc, ux, uy, du;

		//u -- u, bu -- ku, bn -- kuzero

		int x, y, dx, dy, xmin, xmax, ymin, ymax, step;

		if (!reverseDirection){

			for( x=borderWidth; x<dimx-borderWidth; x++){
				for( y=borderWidth; y<dimy-borderWidth; y++){
					kku=0;
					s=0;
					ss=0;
					for( dx=-blurRadius; dx<=blurRadius; dx++ ){
						for( dy=-blurRadius; dy<=blurRadius; dy++ ){
							distance=Math.sqrt( dx*dx+dy*dy );
							if (distance>blurRadius) continue;
							if (distance<blurRadius) {
								if (distance<blurRadius-1){
									kku += ku[x+dx][y+dy];
									s += 1.0;
									ss += 1;	//1 squared
								} else {
									kku += (0.5)*ku[x+dx][y+dy];
									s += 0.5;
									ss += 0.25 ; //0.5 squared
								}
								continue;
							}
							kku += (0.25)*ku[x+dx][y+dy];
							s += 0.25;
							ss += 0.0625;	//0.25 squared
						}
					}
					kku /= s;
					kku -= u[x][y]*ss / (s*s);

					ux = ( u[x+1][y  ]-u[x  ][y  ] ) / h;
					uy = ( u[x  ][y+1]-u[x  ][y-1] ) / twoh;
					du = Math.sqrt( eps+ux*ux+uy*uy );
					c1 = 1/du;

					ux = ( u[x  ][y  ]-u[x-1][y  ] ) / h;
					uy = ( u[x-1][y+1]-u[x-1][y-1] ) / twoh;
					du = Math.sqrt( eps+ux*ux+uy*uy );
					c2 = 1/du;

					ux = ( u[x+1][y  ]-u[x-1][y  ] ) / twoh;
					uy = ( u[x  ][y+1]-u[x  ][y  ] ) / h;
					du = Math.sqrt( eps+ux*ux+uy*uy );
					c3 = 1/du;

					ux = ( u[x+1][y-1]-u[x-1][y-1] ) / twoh;
					uy = ( u[x  ][y  ]-u[x  ][y-1] ) / h;
					du = Math.sqrt( eps+ux*ux+uy*uy );
					c4 = 1/du;

					cc = hSquaredOverAlpha * ss/(s*s) + c1 + c2 + c3 + c4;

					u[x][y] = (1/cc) * ( c1*u[x+1][y  ] + c2* u[x-1][y  ] + c3*u[x  ][y+1] + c4*u[x  ][y-1] + hSquaredOverAlpha*kuzero[x][y] - hSquaredOverAlpha*kku );

					if ( u[x][y]<min || u[x][y]>max ) throw new ArithmeticException("Alpha is too small.");
				}
			}
		} else {

			for( x=dimx-borderWidth-1; x>borderWidth-1; x--){
				for( y=dimy-borderWidth-1; y<borderWidth-1; y--){
					kku=0;
					s=0;
					ss=0;
					for( dx=-blurRadius; dx<=blurRadius; dx++ ){
						for( dy=-blurRadius; dy<=blurRadius; dy++ ){
							distance=Math.sqrt( dx*dx+dy*dy );
							if (distance>blurRadius) continue;
							if (distance<blurRadius) {
								if (distance<blurRadius-1){
									kku += ku[x+dx][y+dy];
									s += 1.0;
									ss += 1;	//1 squared
								} else {
									kku += (0.5)*ku[x+dx][y+dy];
									s += 0.5;
									ss += 0.25 ; //0.5 squared
								}
								continue;
							}
							kku += (0.25)*ku[x+dx][y+dy];
							s += 0.25;
							ss += 0.0625;	//0.25 squared
						}
					}
					kku /= s;
					kku -= u[x][y]*ss / (s*s);

					ux = ( u[x+1][y  ]-u[x  ][y  ] ) / h;
					uy = ( u[x  ][y+1]-u[x  ][y-1] ) / twoh;
					du = Math.sqrt( eps+ux*ux+uy*uy );
					c1 = 1/du;

					ux = ( u[x  ][y  ]-u[x-1][y  ] ) / h;
					uy = ( u[x-1][y+1]-u[x-1][y-1] ) / twoh;
					du = Math.sqrt( eps+ux*ux+uy*uy );
					c2 = 1/du;

					ux = ( u[x+1][y  ]-u[x-1][y  ] ) / twoh;
					uy = ( u[x  ][y+1]-u[x  ][y  ] ) / h;
					du = Math.sqrt( eps+ux*ux+uy*uy );
					c3 = 1/du;

					ux = ( u[x+1][y-1]-u[x-1][y-1] ) / twoh;
					uy = ( u[x  ][y  ]-u[x  ][y-1] ) / h;
					du = Math.sqrt( eps+ux*ux+uy*uy );
					c4 = 1/du;

					cc = hSquaredOverAlpha * ss/(s*s) + c1 + c2 + c3 + c4;

					u[x][y] = (1/cc) * ( c1*u[x+1][y  ] + c2* u[x-1][y  ] + c3*u[x  ][y+1] + c4*u[x  ][y-1] + hSquaredOverAlpha*kuzero[x][y] - hSquaredOverAlpha*kku );

					if ( u[x][y]<min || u[x][y]>max ) throw new ArithmeticException("Alpha is too small.");
				}
			}


		}

	}

	//orignalSource and source should be same size
	public static void calculateEdgeU(double[][] source, double[][] originalSource, int borderWidth, double alpha, double rho, double meshStepSize, boolean reverseDirection){

		int x, y;
		double ux, uy, du, c1, c2, c3, c4, cc;
		double meshSquaredOverAlpha = meshStepSize*meshStepSize/alpha;
		double twoMesh = 2*meshStepSize;
		double eps = 0.000001;

		int dimx=source.length;
		int dimy=source[0].length;

		if (!reverseDirection){

			for( x=borderWidth; x<dimx-borderWidth; x++){
				for( y=borderWidth; y<dimy-borderWidth; y++){

					ux = ( source[x+1][y  ]-source[x  ][y  ] ) / meshStepSize;
					uy = ( source[x  ][y+1]-source[x  ][y-1] ) / twoMesh;
					du = Math.sqrt( eps+ ux*ux + uy*uy );
					c1 = 2./((1.+rho*du*du)*(1.+rho*du*du));

					ux = ( source[x  ][y  ]-source[x-1][y  ] ) / meshStepSize;
					uy = ( source[x-1][y+1]-source[x-1][y-1] ) / twoMesh;
					du = Math.sqrt( eps+ ux*ux + uy*uy );
					c2 = 2./((1.+rho*du*du)*(1.+rho*du*du));

					ux = ( source[x+1][y  ]-source[x-1][y  ] ) / twoMesh;
					uy = ( source[x  ][y+1]-source[x  ][y  ] ) / meshStepSize;
					du = Math.sqrt( eps+ ux*ux + uy*uy );
					c3 = 2./((1.+rho*du*du)*(1.+rho*du*du));

					ux = ( source[x+1][y-1]-source[x-1][y-1] ) / twoMesh;
					uy = ( source[x  ][y  ]-source[x  ][y-1] ) / meshStepSize;
					du = Math.sqrt( eps+ ux*ux + uy*uy );
					c4 = 2./((1.+rho*du*du)*(1.+rho*du*du));

					cc = meshSquaredOverAlpha + c1 + c2 + c3 + c4;

					source[x][y] = (1./cc) * ( c1*source[x+1][y] + c2*source[x-1][y] + c3*source[x][y+1] + c4*source[x][y-1] + meshSquaredOverAlpha*originalSource[x][y] );
				}
			}
		} else {
			for( x=dimx-borderWidth-1; x>borderWidth-1; x--){
				for( y=dimy-borderWidth-1; y<borderWidth-1; y--){
					ux = ( source[x+1][y  ]-source[x  ][y  ] ) / meshStepSize;
					uy = ( source[x  ][y+1]-source[x  ][y-1] ) / twoMesh;
					du = Math.sqrt( eps+ ux*ux + uy*uy );
					c1 = 2./((1.+rho*du*du)*(1.+rho*du*du));

					ux = ( source[x  ][y  ]-source[x-1][y  ] ) / meshStepSize;
					uy = ( source[x-1][y+1]-source[x-1][y-1] ) / twoMesh;
					du = Math.sqrt( eps+ ux*ux + uy*uy );
					c2 = 2./((1.+rho*du*du)*(1.+rho*du*du));

					ux = ( source[x+1][y  ]-source[x-1][y  ] ) / twoMesh;
					uy = ( source[x  ][y+1]-source[x  ][y  ] ) / meshStepSize;
					du = Math.sqrt( eps+ ux*ux + uy*uy );
					c3 = 2./((1.+rho*du*du)*(1.+rho*du*du));

					ux = ( source[x+1][y-1]-source[x-1][y-1] ) / twoMesh;
					uy = ( source[x  ][y  ]-source[x  ][y-1] ) / meshStepSize;
					du = Math.sqrt( eps+ ux*ux + uy*uy );
					c4 = 2./((1.+rho*du*du)*(1.+rho*du*du));

					cc = meshSquaredOverAlpha + c1 + c2 + c3 + c4;

					source[x][y] = (1./cc) * ( c1*source[x+1][y] + c2*source[x-1][y] + c3*source[x][y+1] + c4*source[x][y-1] + meshSquaredOverAlpha*originalSource[x][y] );
				}
			}
		}

	}

	public static double calculateEdgeStrength(double[][] source, double[][] result, int borderWidth, double meshStepSize, double rho, boolean twoPoint ){

		int x, y;
		double ux, uy, duSquared, distance;
		double twoMesh = 2*meshStepSize;
		double totalEdgeLength = 0;


		int dimx=source.length;
		int dimy=source[0].length;

		for( x=borderWidth; x<dimx-borderWidth; x++){
			for( y=borderWidth; y<dimy-borderWidth; y++){

				if (twoPoint){
					//Edges 2-point formula
					ux = ( source[x+1][y  ] - source[x-1][y  ] ) / twoMesh;
					uy = ( source[x  ][y+1] - source[x  ][y-1] ) / twoMesh;
				} else {
					//Edges 1-point formula
					ux = (source[x+1][y  ] - source[x][y]) / meshStepSize;
					uy = (source[x  ][y+1] - source[x][y]) / meshStepSize;
				}
				duSquared = ux*ux+uy*uy;
				result[x][y] = 1./(1.+rho*duSquared);
				totalEdgeLength += (1.-result[x][y]);

			}
		}
		return totalEdgeLength;

	}

}




