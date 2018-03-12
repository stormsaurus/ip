package ellis.GIP;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

public class ComputationEngine {

	private Image savedimg;
	private String imgname;
	private Canvas can = new Canvas();
	private int[] pixels={};
	private int[] histogram=new int[256];
	private int width=0, height=0;
	private int a=0, red=0, green=0, blue=0;
	private String lnreturn;

	public ComputationEngine(Image img, String arg){
		imgname = arg;
		init(img);
	}

	public ComputationEngine(Image img){
		imgname = "unknown";
		init(img);
	}

	private void init(Image img){
		lnreturn = System.getProperty("line.separator","\n");
		if (img==null) { System.out.println("GIPProcessEngine received null."); return; } 
		savedimg = img;

		while ((width=savedimg.getWidth(can))==-1 || (height=savedimg.getHeight(can))==-1) {};
		pixels = new int[width*height];
		PixelGrabber pg = new PixelGrabber (savedimg, 0, 0, width, height, pixels, 0, width);
		try {
			pg.grabPixels();
		} catch (InterruptedException e){
			e.printStackTrace();
		}
	
	}

	private void calcHist() {

		int i=0;

		for (i=0; i<histogram.length; i++) {
			histogram[i]=0;
		}

		for (i=0; i<width*height; i++) {
			a    =(pixels[i]&0xff000000)>>24; 
			red  =(pixels[i]&0x00ff0000)>>16; 
			green=(pixels[i]&0x0000ff00)>>8; 
			blue =(pixels[i]&0x000000ff);
			histogram[red]++;
		}
	}

	private int calcAverage(Rectangle rectangle){

		int i=0, j=0;
		int tempavg = 0;
		
		if ( rectangle.x<0 || rectangle.y<0 || (rectangle.x + rectangle.width) > width || (rectangle.y + rectangle.height) > height ) {
			return 0;
		}
				
		for (j=rectangle.y; j<rectangle.y+rectangle.height; j++) {
			for (i=rectangle.x; i<rectangle.x+rectangle.width; i++){
				a    =(pixels[j*width+i]&0xff000000)>>24; 
				red  =(pixels[j*width+i]&0x00ff0000)>>16; 
				green=(pixels[j*width+i]&0x0000ff00)>>8; 
				blue =(pixels[j*width+i]&0x000000ff);
				tempavg += (red+blue+green)/3;
			}
		}
		
		tempavg /= rectangle.getWidth()*rectangle.getHeight();
	
		return tempavg;
	}

	private int getMax(Rectangle rectangle){

		int i=0, j=0;
		int rgb=0;
		int tempmax = 0;

		if ( rectangle.x<0 || rectangle.y<0 || (rectangle.x + rectangle.width) > width || (rectangle.y + rectangle.height) > height ) {
			System.out.println("GIPProcessEngine.getMax received bad rectangle.");		
			return 0;
		}
		
//		System.out.print("{Xo,Yo,dY,dY:"+ rectangle.x + "," + rectangle.y + "," +rectangle.width+ "," + rectangle.height +"}");
		
		for (j=rectangle.y; j<rectangle.y+rectangle.height; j++) {
			for (i=rectangle.x; i<rectangle.x+rectangle.width; i++){
				a    =(pixels[j*width+i]&0xff000000)>>24; 
				red  =(pixels[j*width+i]&0x00ff0000)>>16; 
				green=(pixels[j*width+i]&0x0000ff00)>>8; 
				blue =(pixels[j*width+i]&0x000000ff);
				rgb  =(red+blue+green)/3;
//				System.out.print("("+i+","+j+")"+rgb+".");
				if (rgb>tempmax) tempmax=rgb;
			}
		}
//		System.out.println("***"+tempmax);
		
		return tempmax;
	}
	
	public void drawLine(int[] processedpixels, int x0, int y0, int x1, int y1){
		if (processedpixels==null) return;
		double distance = Math.pow( (Math.pow((x0-x1),2)+Math.pow((y0-y1),2)), 0.5 );
		double dx = (x0-x1)/distance;
		double dy = (y0-y1)/distance;
		for (double i=0; i<=distance; i+=0.5) {
			x0+=dx;
			y0+=dy;
			processedpixels[y0*width+x0] = 0xffffffff;
		}
		
	}

	public String getInfo(){
		int avg=0;
		
		avg = calcAverage( new Rectangle(0,0,width,height) );

		return "Info for "+imgname+lnreturn+"\tAverage = "+avg+lnreturn;
	}

	public Image doEdgeBox(int boxsize){
	
		int[] processedpixels;
		int i=0, j=0;
		int avg=0;
		int xnumboxes=0;
		int ynumboxes=0;
		int boxcenteroffset=0;
		int rgb=0;

		//test to verify box and avgmask size and oddness
		if ( boxsize<1 || boxsize>width || boxsize>height || ((float)boxsize)/2==Math.floor(boxsize/2) ) {
			System.out.println("GIPProcessEngine.doEdgeBox received bad boxsize.");		
			return savedimg;			
		}
		
		boxcenteroffset = (int)Math.floor(boxsize/2);   //add this to get center from edge of box

		xnumboxes = (int)Math.floor(  width/boxsize ); 
		ynumboxes = (int)Math.floor( height/boxsize ); 
		
		System.out.println("Width by Height = "+width+" x "+height);
		System.out.println("Boxes x,y = "+xnumboxes+"\t"+ynumboxes);

		processedpixels = new int[width * height];
		for (i=0; i<width*height; i++) {
			processedpixels[i] = 255<<24;
		}

		avg = calcAverage(new Rectangle(0,0,width,height));
		System.out.println("Avg = "+avg);
		for (j=0; j<ynumboxes*boxsize; j+=boxsize) {
			for (i=0; i<xnumboxes*boxsize; i+=boxsize) {
//				System.out.print("Maxing Rectangle coords ("+i+","+j+")   -   ");
				if ( getMax(new Rectangle(i,j,boxsize,boxsize)) > avg ) processedpixels[(j+boxcenteroffset)*width+(i+boxcenteroffset)] = 0xffffffff;
			}
		}


		//***Connect Neighbors
		/* Looking for neighboring active points in four directions from each active point.
			      /
				 /	
				.-----
				|\
				| \
				|  \
		*/


		if (ynumboxes>2 && xnumboxes>2) {
			for (j=0; j<(ynumboxes)*boxsize; j+=boxsize) {
				for (i=0; i<(xnumboxes)*boxsize; i+=boxsize) {
//					System.out.println(""+(processedpixels[(j+boxcenteroffset)*width+(i+boxcenteroffset)]==255<<24?"off":"on"));
					if ( processedpixels[(j+boxcenteroffset)*width+(i+boxcenteroffset)] != 255<<24){
//						System.out.println("Found white one.");
//						if ( processedpixels[(j+boxcenteroffset-boxsize)*width+(i+boxcenteroffset)+boxsize] == 0xffffffff) drawLine(processedpixels, i,j,i+boxsize,j-boxsize);
//						if ( processedpixels[(j+boxcenteroffset        )*width+(i+boxcenteroffset)+boxsize] == 0xffffffff) drawLine(processedpixels, i,j,i+boxsize,j        );
//						if ( processedpixels[(j+boxcenteroffset+boxsize)*width+(i+boxcenteroffset)+boxsize] == 0xffffffff) drawLine(processedpixels, i,j,i+boxsize,j+boxsize);
//						if ( processedpixels[(j+boxcenteroffset+boxsize)*width+(i+boxcenteroffset)        ] == 0xffffffff) drawLine(processedpixels, i,j,i        ,j+boxsize);
					}
				}
			}
		}
	
	
	//processedpixels[i] = (pixels[i]&0xff000000) | ( (red>avg ? red : 0)<< 16) | (green>avg ? green : 0)<<8 | (blue>avg ? blue : 0);		

		return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, processedpixels, 0, width));
	}
	
	public String getHistSum(){
		int i=0;
		String tempstring;
		
		this.calcHist();
		tempstring = "Non-zero histogram elements for "+imgname+lnreturn;

		for (i=0; i<histogram.length; i++) {
			if (histogram[i]!=0) {
				tempstring += ""+i+"\t"+histogram[i]+lnreturn;
			}
		}
		return tempstring;
	
	}

	public String getEdgeSum(){
		//***Rountine only counts edges inside a 1 pixel border around the image
		//***Uses modified histogram routine which skips 1 pixel border
		int i=0, j=0, k=0;
		int[] edgecount=new int[256];
		String tempstring;

		for (i=0; i<edgecount.length; i++) {
			edgecount[i]=0;
		}

		for (i=0; i<histogram.length; i++) {
			histogram[i]=0;
		}

		if (width>2 && height>2) {
			for (i=1; i<(width-1)*(height-1); i++) {
				blue =(pixels[i]&0x000000ff);
				histogram[blue]++;
			}
		}

		for (i=0; i<histogram.length; i++) {
			if (histogram[i]!=0 && width>2 && height>2) {
				for (j=1; j<height-1; j++) {
					for (k=1; k<width-1; k++) {
						//blue channel only
						if ( 	(pixels[k+j    *width]&0x000000ff) == i && 
						(        (pixels[(k-1)+(j-1)*width]&0x000000ff)!=i	|| (pixels[k+(j-1)*width]&0x000000ff)!=i	||	(pixels[(k+1)+(j-1)*width]&0x000000ff)!=i
					    		|| (pixels[(k-1)+j    *width]&0x000000ff)!=i	|| false						||	(pixels[(k+1)+j    *width]&0x000000ff)!=i
					    		|| (pixels[(k-1)+(j+1)*width]&0x000000ff)!=i	|| (pixels[k+(j+1)*width]&0x000000ff)!=i	||	(pixels[(k+1)+(j+1)*width]&0x000000ff)!=i  )) {

							edgecount[i]++;
						}
					}
				}
			}

		}

		tempstring = "Edge counts for non-zero histogram elements of "+imgname+lnreturn;

		for (i=0; i<edgecount.length; i++) {
			if (edgecount[i]!=0) {
				tempstring += ""+i+"\t"+histogram[i]+"\t"+edgecount[i]+lnreturn;
			}
		}
		return tempstring;
	}

	public void screendumpHist(){
		System.out.println(this.getHistSum());
	}

	public void screendumpEdge(){
		System.out.println(this.getEdgeSum());
	}

}
