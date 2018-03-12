import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.event.*;

public class GIP extends JFrame{

	private GIPProcessEngine	processengine;
	private GIPImageDisplayer	imagedisplay;
	private GIPImageFileList	filelist;
	private GIPMenuBar			menubar;
	private GIPToolBar			toolbar;
	private JPanel				contentpane;
	private JPanel				mainpane;
	
	private GridBagLayout 		gridbag;
	private GridBagConstraints 	gbc;
	
	public String				hackprocesstype;
	public int					boxsize=5;

	public GIP(){
		super("Growth Image Processor");
		WindowListener wl = new WindowAdapter(){
			public void windowClosing(WindowEvent e) {System.exit(0);}
		};
		addWindowListener(wl);
		

		menubar = new GIPMenuBar(this);
		this.setJMenuBar(menubar);
		

		contentpane	= new JPanel();
		contentpane.setLayout(new BorderLayout());
		toolbar 	= new GIPToolBar(this);
		contentpane.add(toolbar, BorderLayout.NORTH);


		mainpane	= new JPanel();
			  
		gridbag = new GridBagLayout();
		gbc 	= new GridBagConstraints();
		gbc.insets = new Insets(10,5,10,5);			
		mainpane.setLayout( gridbag );

		gbc.gridx		= 0;
		gbc.gridy		= 0;
		gbc.gridwidth	= 1;
		gbc.weightx		= 1;
		gbc.weighty		= 1;
		gbc.fill 		= GridBagConstraints.BOTH;
		filelist = new GIPImageFileList(this);
		gridbag.setConstraints(filelist, gbc);
		mainpane.add(filelist);

		gbc.gridx		= 1;
		gbc.gridy		= 0;
		gbc.gridwidth	= 2;
		gbc.weightx		= 4;
		gbc.weighty		= 4;
		gbc.fill 		= GridBagConstraints.BOTH;
		imagedisplay = new GIPImageDisplayer();
		gridbag.setConstraints(imagedisplay, gbc);
		mainpane.add(imagedisplay);

		contentpane.add(mainpane, BorderLayout.CENTER);

		this.setContentPane(contentpane);

//		pack();
		setSize(600, 800);
		show();
	}
	
	public void singleProcess() {
		Image tempimg;

		tempimg = imagedisplay.getTopImageIcon().getImage();
		if (tempimg==null) {return; }
		processengine = new GIPProcessEngine(tempimg, filelist.getSelectedImageName());		
		if (hackprocesstype=="HistSum") {
			imagedisplay.setTextResult(processengine.getHistSum());
		} else if (hackprocesstype=="EdgeSum") {
			imagedisplay.setTextResult(processengine.getEdgeSum());
		} else if (hackprocesstype=="EdgeBox") {
			imagedisplay.setBottomImage( new ImageIcon(processengine.doEdgeBox(boxsize)) );
		}
	}

	public void batchProcess() {
	
	}

	public void setTopImage(ImageIcon img){
		imagedisplay.setTopImage(img);
		processengine = new GIPProcessEngine(img.getImage(), filelist.getSelectedImageName());
		imagedisplay.setTextResult(processengine.getInfo());
	}

	public void setBottomImage(ImageIcon img){
		imagedisplay.setBottomImage(img);
	}

	public void setFileList(){
		filelist.populateList();
		filelist.revalidate();
	}

	public static void main(String[] args){
		new GIP();
	}

}

class GIPToolBar extends JToolBar {

	private GIP 	gip;

	private GIPSingle			singleprocessaction;
	private GIPBatch			batchprocessaction;
	private ButtonGroup			processchoicegroup;
		private GIPEdgeBox			edgeboxaction;
		private GIPEdgeSum			edgesumaction;
		private GIPHistSum			histsumaction;
	
	private JButton				singleprocessbutton;
	private JButton				batchprocessbutton;
	private JButton				edgeboxbutton;
	private JButton				edgesumbutton;
	private JButton				histsumbutton;


	public GIPToolBar(GIP arg){
		super();
		gip = arg;

		singleprocessaction	= new GIPSingle(gip);
		batchprocessaction	= new GIPBatch(gip);
		edgeboxaction		= new GIPEdgeBox(gip);
		edgesumaction		= new GIPEdgeSum(gip);
		histsumaction		= new GIPHistSum(gip);

		singleprocessbutton = this.add(singleprocessaction);
			singleprocessbutton.setToolTipText("Process image and display.");
		batchprocessbutton = this.add(batchprocessaction);
			batchprocessbutton.setToolTipText("Batch process images, display, and save.");
		this.addSeparator();
		edgeboxbutton = this.add(edgeboxaction);
			edgeboxbutton.setToolTipText("Select Edge Box Routine.");
		edgesumbutton = this.add(edgesumaction);
			edgesumbutton.setToolTipText("Select Edge Sum Routine.");
		histsumbutton = this.add(histsumaction);
			histsumbutton.setToolTipText("Select Histogram Sum Routine.");
		processchoicegroup = new ButtonGroup();
			processchoicegroup.add(edgeboxbutton);
			processchoicegroup.add(edgesumbutton);
			processchoicegroup.add(histsumbutton);
	}

}

class GIPMenuBar extends JMenuBar implements ActionListener{

	private JMenu	filemenu;
	private JMenu	processmenu;
	private JMenu	helpmenu;
	private GIP		gip;
	
	public GIPMenuBar(GIP arg){		
		super();
		gip=arg;
		filemenu = new JMenu("File");
		JMenuItem openitem = new JMenuItem("Open...");
		openitem.addActionListener(this);
		filemenu.add(openitem);
		add(filemenu);


//		processmenu = new JMenu("Process");
//		JMenuItem selectprocessitem = new JMenuItem("Select...");
//		processmenu.add(selectprocessitem);
//		selectprocessitem.addActionListener(this);
//		add(processmenu);


		helpmenu = new JMenu("Help");
		JMenuItem aboutitem = new JMenuItem("About...");
		helpmenu.add(aboutitem);
		add(helpmenu);

	}

	public void actionPerformed(ActionEvent ae){
		String temp = ae.getActionCommand();
		if (temp.equals("Open...")) {
			gip.setFileList();
		}
	}

}

class GIPImageFileList extends JScrollPane implements ListSelectionListener {

	private JFileChooser		filechooser;
	private JList				list;
//	private ExtensionFileFilter	filter;
	private String[]			suffixes = {".jpg",".gif"};
	private SuffixFileFilter	sfilter;
	private File[]				files;
	private File				directory;
	private Vector				filenames;
	private int					returnval;
	private GIP					gip;

/* Cant' seem to get access to ExtensionFileFilter object */

	public GIPImageFileList(GIP arg){
		super();
		gip = arg;
		sfilter = new SuffixFileFilter(suffixes);
		filechooser = new JFileChooser(new File(System.getProperty("user.dir","")));
//		filter = new ExtensionFileFilter();
//		filter.addExtension("jpg");
//		filter.addExtension("gif");
//		filter.setDescription("JPG & GIF Images");
//		filechooser.setFileFilter(filter);
		filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		filechooser.setMultiSelectionEnabled(true);

		filenames = new Vector();
	
		initList();


	}

	private void initList() {
		list = new JList();
		directory = new File(System.getProperty("user.dir",""));
		if (directory!=null && directory.exists()) {
			filenames.clear();
			files=directory.listFiles(sfilter);				/********workaround*/
			if (files!=null && files.length>0) {
				for (int i=0; i<files.length; i++) {
					filenames.add(files[i].getName());
				}
				list.setListData(filenames);
			}
		}
		list.revalidate();
		list.addListSelectionListener(this);
		this.getViewport().setView(list);
	}

/*	The JFileChooser will not return a array of selected files...Using workaround */
	
	public void populateList(){
		returnval = filechooser.showOpenDialog(this);
//		files = filechooser.getSelectedFiles();        ********this is buggy using workaround
		directory = filechooser.getSelectedFile();
		if (directory!=null && returnval==JFileChooser.APPROVE_OPTION && directory.exists()/* && files.length>0 */) {
			filenames.clear();
			files=directory.listFiles(sfilter);				/********workaround*/
			if (files!=null && files.length>0) {
				for (int i=0; i<files.length; i++) {
					filenames.add(files[i].getName());
				}
				list.setListData(filenames);
			}
		}
		list.revalidate();
	}

	public String getSelectedImageName(){
		return files[list.getSelectedIndex()].getName();
	}
	
	public void valueChanged(ListSelectionEvent lse) {
		if (!lse.getValueIsAdjusting()) {
			gip.setTopImage( new ImageIcon( files[list.getSelectedIndex()].getPath() ) );
 		}
 	}   

}


class GIPImageDisplayer extends JSplitPane {

	private JScrollPane		topsection;
	private JLabel			toplabel;
	private ImageIcon		topimage;
	private JScrollPane		bottomsection;
	private JLabel			bottomlabel;
	private ImageIcon		bottomimage;

	private JTextArea		textresult;
	private JScrollPane		textresultsection;
	
	public GIPImageDisplayer(){
		super(VERTICAL_SPLIT, true);
		

		topimage = new ImageIcon("iguana.gif");
		toplabel = new JLabel(topimage);
		topsection = new JScrollPane(toplabel);
		add(topsection);

		bottomimage = new ImageIcon("iguana.gif");
		bottomlabel = new JLabel(bottomimage);
		bottomsection = new JScrollPane(bottomlabel);
		add(bottomsection);

		textresult = new JTextArea();
		textresultsection = new JScrollPane(textresult);
		
		resetToPreferredSizes();

	}

	public void setTopImage(ImageIcon img){
		if (img==null) {
			System.out.println("GIPImageDisplay received a null pass.");
			return;
		}
		topimage = img;
		toplabel.setIcon(topimage);
		toplabel.setPreferredSize( new Dimension(img.getIconWidth(),img.getIconHeight()) );
 		topsection.revalidate();
		resetToPreferredSizes();
	}

	public void setBottomImage(ImageIcon img){
		if (img==null) {
			System.out.println("GIPImageDisplay received a null pass.");
			return;
		}
		bottomimage = img;
		bottomlabel.setIcon(bottomimage);
		bottomlabel.setPreferredSize( new Dimension(img.getIconWidth(),img.getIconHeight()) );
		setBottomComponent(bottomsection);
		bottomsection.revalidate();
		resetToPreferredSizes();
	
	}

	public void setTextResult(String arg){
		textresult = new JTextArea(arg);
		textresultsection = new JScrollPane(textresult);
		setBottomComponent(textresultsection);
		textresultsection.revalidate();
		resetToPreferredSizes();
	}
	
	public ImageIcon getTopImageIcon(){
		return topimage;
	}

	public ImageIcon getBottomImageIcon(){
		return bottomimage;
	}

}

class GIPSingle extends AbstractAction{

	GIP		gip;

	public GIPSingle(GIP arg){
		super( "",new ImageIcon("singleprocess.gif") );
		gip = arg;
	}

	public void actionPerformed(ActionEvent ae) {
		gip.singleProcess();
	}

}

class GIPBatch extends AbstractAction{

	GIP		gip;

	public GIPBatch(GIP arg){
		super( "",new ImageIcon("batchprocess.gif") );
		gip = arg;
	}

	public void actionPerformed(ActionEvent ae) {
		gip.batchProcess();
	}

}

class GIPHistSum extends AbstractAction{

	GIP		gip;

	public GIPHistSum(GIP arg){
		super( "",new ImageIcon("histsumprocess.gif") );
		gip = arg;
	}

	public void actionPerformed(ActionEvent ae) {
		gip.hackprocesstype = "HistSum";
	}

}

class GIPEdgeSum extends AbstractAction{

	GIP		gip;

	public GIPEdgeSum(GIP arg){
		super( "",new ImageIcon("edgesumprocess.gif") );
		gip = arg;
	}

	public void actionPerformed(ActionEvent ae) {
		gip.hackprocesstype = "EdgeSum";
	}

}

class GIPEdgeBox extends AbstractAction{

	GIP		gip;

	public GIPEdgeBox(GIP arg){
		super( "",new ImageIcon("edgeboxprocess.gif") );
		gip = arg;
	}

	public int getBoxSize(){
		int temp= -1;
		
		String inputvalue = JOptionPane.showInputDialog("Please input odd box pixel size.");
		try { 
			temp = Integer.parseInt(inputvalue);
		} catch (NumberFormatException nfe) {
			return -1;
		}
		return temp;
	}

	public void actionPerformed(ActionEvent ae) {
		gip.boxsize = -1;
		while (gip.boxsize<=0 || (float)gip.boxsize/2==Math.floor(gip.boxsize/2)) gip.boxsize = getBoxSize();
		gip.hackprocesstype = "EdgeBox";
		
	}

}

class GIPProcessEngine {

	private Image savedimg;
	private String imgname;
	private Canvas can = new Canvas();
	private int[] pixels={};
	private int[] histogram=new int[256];
	private int width=0, height=0;
	private int a=0, red=0, green=0, blue=0;
	private String lnreturn;

	public GIPProcessEngine(Image img, String arg){
		imgname = arg;
		init(img);
	}

	public GIPProcessEngine(Image img){
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
