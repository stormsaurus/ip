import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.color.*;
import java.io.*;
import java.util.*;
import java.beans.*;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.event.*;

import ellis.image.processing.*;
import Acme.JPM.Encoders.GifEncoder;

public class IP implements ListSelectionListener, ActionListener, PropertyChangeListener{

	private static String		VERSION ="1.8.3";

	private JFrame 				mainFrame;
	private JMenuBar			mainMenuBar;

	private JMenu				fileMenu;
		private JMenuItem			openFileItem;
		private JMenuItem			saveFileItem;
		private JMenuItem			saveLogFileItem;
		private JMenuItem			exitFileItem;
	private JMenu				setMenu;
		private JMenuItem			backgroundSetItem;
		private JMenuItem			denoiseDeblurSetItem;
		private JMenuItem			edgeFindSetItem;
		private JMenuItem			colorQuantizationSetItem;
		private JMenuItem			bicSetItem;
	private JMenu				processMenu;
		private JMenuItem			infoProcessItem;
		private JMenuItem			rescaleProcessItem;
		private JMenuItem			totalVariationItem;
		private JMenuItem			backgroundSubtractItem;
		private JMenuItem			denoiseDeblurProcessItem;
		private JMenuItem			edgeFindProcessItem;
		private JMenuItem			colorQuantizationProcessItem;
	private JMenu				batchProcessMenu;
		private JMenuItem			bicItem;
	private JMenu				logMenu;
		private JMenuItem			clearLogItem;
	private JMenu				helpMenu;
		private JMenuItem			aboutItem;

	private JPanel				contentPane;
	private JSplitPane			hsplit;
	private JSplitPane			vsplit;

	private ImageFileList		imageList;
	private ImageViewArea		imageView;
	private StatusMessageArea	statusArea;

	private boolean				isSaved = true;
	private boolean				doingMultiOp = false;

	private JFileChooser		fileChooser;
	private JFileChooser		logFileChooser;

	private BackgroundSubtractFastOp	backgroundSubtract;
		private VariableOptionDialog		backgroundSubtractionDialog;
	private RescaleFastOp				rescaleOp;
	private TotalVariationFastOp			totalVariationOp;

	private DenoiseDeblurSlowOp			denoiseDeblur;
		private VariableOptionDialog		denoiseDeblurDialog;
	private EdgeFindSlowOp				edgeFind;
		private VariableOptionDialog		edgeFindDialog;
	private ColorQuantizationSlowOp		colorQuantization;
		private VariableOptionDialog		colorQuantizationDialog;
	private MultiOp						bicOp;
		private VariableOptionDialog		bicOpDialog;

	public IP(){

		mainFrame = new JFrame("IP - Image Processor");
		mainFrame.addWindowListener( new WindowAdapter(){
			public void windowClosing(WindowEvent e) {System.exit(0);}
		});

		//***Create MenuBar
		mainMenuBar = new JMenuBar();

		//***Create File Menu
		fileMenu = new JMenu("File");
		openFileItem = new JMenuItem("Set Image Directory...");
		openFileItem.addActionListener(this);
		saveFileItem = new JMenuItem("Save...");
		saveFileItem.addActionListener(this);
		saveLogFileItem = new JMenuItem("Save Log File...");
		saveLogFileItem.addActionListener(this);
		exitFileItem = new JMenuItem("Exit");
		exitFileItem.addActionListener(this);
		fileMenu.add(openFileItem);
		fileMenu.add(saveFileItem);
		fileMenu.add(saveLogFileItem);
		fileMenu.add(new JSeparator());
		fileMenu.add(exitFileItem);
		mainMenuBar.add(fileMenu);

		//***Create Set Menu
		setMenu = new JMenu("Set");
		backgroundSetItem 			= new JMenuItem("Background-Subtract...");
		backgroundSetItem.addActionListener(this);
		denoiseDeblurSetItem		= new JMenuItem("Denoise-Deblur...");
		denoiseDeblurSetItem.addActionListener(this);
		edgeFindSetItem 			= new JMenuItem("Edge-Find...");
		edgeFindSetItem.addActionListener(this);
		colorQuantizationSetItem	= new JMenuItem("Color-Quantization...");
		colorQuantizationSetItem.addActionListener(this);
		bicSetItem	= new JMenuItem("BAM Image Converter v1.0...");
		bicSetItem.addActionListener(this);
		setMenu.add(backgroundSetItem);
		setMenu.add(denoiseDeblurSetItem);
		setMenu.add(edgeFindSetItem);
		setMenu.add(colorQuantizationSetItem);
		setMenu.add( new JSeparator() );
		setMenu.add(bicSetItem);
		mainMenuBar.add(setMenu);

		//***Create Process Menu
		processMenu = new JMenu("Process");
		infoProcessItem = new JMenuItem("Image information");
		infoProcessItem.addActionListener(this);
		rescaleProcessItem = new JMenuItem("Rescale between 0 & 255");
		rescaleProcessItem.addActionListener(this);
		totalVariationItem = new JMenuItem("Calculate Total Variation");
		totalVariationItem.addActionListener(this);
		backgroundSubtractItem = new JMenuItem("Background-Subtract");
		backgroundSubtractItem.addActionListener(this);
		denoiseDeblurProcessItem = new JMenuItem("Denoise-Deblur");
		denoiseDeblurProcessItem.addActionListener(this);
		edgeFindProcessItem = new JMenuItem("Edge-Find");
		edgeFindProcessItem.addActionListener(this);
		colorQuantizationProcessItem = new JMenuItem("Color-Quantization");
		colorQuantizationProcessItem.addActionListener(this);
		processMenu.add(infoProcessItem);
		processMenu.add(rescaleProcessItem);
		processMenu.add(totalVariationItem);
		processMenu.add( new JSeparator() );
		processMenu.add(backgroundSubtractItem);
		processMenu.add(denoiseDeblurProcessItem);
		processMenu.add(edgeFindProcessItem);
		processMenu.add(colorQuantizationProcessItem);
		mainMenuBar.add(processMenu);

		//***Create Batch Menu
		batchProcessMenu = new JMenu("Batch");
		bicItem = new JMenuItem("BAM Image Converter v1.0");
		bicItem.addActionListener(this);
		batchProcessMenu.add( bicItem );
		mainMenuBar.add(batchProcessMenu);

		//***Create Log Menu
		logMenu = new JMenu("Log");
		clearLogItem = new JMenuItem("Clear Log");
		clearLogItem.addActionListener( this );
		logMenu.add(clearLogItem);
		mainMenuBar.add(logMenu);

		//***Create Help Menu
		helpMenu = new JMenu("Help");
		aboutItem = new JMenuItem("About...");
		helpMenu.add(aboutItem);
		mainMenuBar.add(helpMenu);

		mainFrame.setJMenuBar(mainMenuBar);

		contentPane	= new JPanel();
		contentPane.setLayout(new BorderLayout());
		//contentPane.add(toolbar, BorderLayout.NORTH);

		hsplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		imageList = new ImageFileList();
		imageList.addListSelectionListener(this);
		imageView = new ImageViewArea();

		vsplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
		statusArea = new StatusMessageArea();
		statusArea.printMessage( "Image Processor - v"+VERSION );
		statusArea.printCommentMessage( "program initialized "+ (new SimpleDateFormat("MMMM dd, yyyy - H:mm:ss")).format(new Date()) );

		hsplit.setLeftComponent(imageList);
		hsplit.setRightComponent(imageView);
		vsplit.setTopComponent(hsplit);
		vsplit.setBottomComponent(statusArea);

		contentPane.add(vsplit, BorderLayout.CENTER);
		mainFrame.setContentPane(contentPane);

		mainFrame.pack();
		mainFrame.show();

		fileChooser = new JFileChooser(new File(System.getProperty("user.dir","")));
		logFileChooser = new JFileChooser(new File(System.getProperty("user.dir","")));

		denoiseDeblur = new DenoiseDeblurSlowOp( mainFrame.getContentPane() );
		denoiseDeblur.addPropertyChangeListener( this );
		denoiseDeblurDialog = new VariableOptionDialog( mainFrame, "Denoise-Deblur Parameters");
		denoiseDeblurDialog.addVariable("Iterations", String.valueOf(DenoiseDeblurSlowOp.DEFAULT_ITERATIONS) );
		denoiseDeblurDialog.addVariable("Alpha", String.valueOf(DenoiseDeblurSlowOp.DEFAULT_ALPHA) );
		denoiseDeblurDialog.addVariable("Mesh Step Size", String.valueOf(DenoiseDeblurSlowOp.DEFAULT_MESH_STEP_SIZE) );
		denoiseDeblurDialog.addVariable("Blur Radius", String.valueOf(DenoiseDeblurSlowOp.DEFAULT_BLUR_RADIUS) );
		denoiseDeblurDialog.addVariable("Max Value Before Error", String.valueOf(DenoiseDeblurSlowOp.DEFAULT_MAX_VALUE_BEFORE_ERROR) );
		denoiseDeblurDialog.addVariable("Min Value Before Error", String.valueOf(DenoiseDeblurSlowOp.DEFAULT_MIN_VALUE_BEFORE_ERROR) );
		denoiseDeblurDialog.addOption("Auto Find Alpha", false);
		denoiseDeblurDialog.addWindowListener( new WindowAdapter (){
			public void windowDeactivated(WindowEvent e){
				if( !denoiseDeblurDialog.isCanceled() ) {
					String iterationString = denoiseDeblurDialog.getValue("Iterations");
					String alphaString = denoiseDeblurDialog.getValue("Alpha");
					String meshString = denoiseDeblurDialog.getValue("Mesh Step Size");
					String blurRadiusString = denoiseDeblurDialog.getValue("Blur Radius");
					String maxString = denoiseDeblurDialog.getValue("Max Value Before Error");
					String minString = denoiseDeblurDialog.getValue("Min Value Before Error");
					try{
						denoiseDeblur.setIterations( 			Integer.parseInt(iterationString) 							);
						denoiseDeblur.setAlpha( 				Double.parseDouble(alphaString) 							);
						denoiseDeblur.setMeshStepSize( 			Integer.parseInt(meshString) 								);
						denoiseDeblur.setBlurRadius(		 	Integer.parseInt(blurRadiusString)						 	);
						denoiseDeblur.setMaxBeforeError( 		Integer.parseInt(maxString) 								);
						denoiseDeblur.setMinBeforeError( 		Integer.parseInt(minString) 	 							);
						denoiseDeblur.setAutoFindAlpha( 		denoiseDeblurDialog.isOptionSelected("Auto Find Alpha") 	);
					} catch (NumberFormatException nfe){
						statusArea.printErrorMessage("Parameters could not be parsed.");
						denoiseDeblurDialog.setVisible(true);
					}
				}
			}
		});
		denoiseDeblurDialog.pack();
		denoiseDeblurDialog.centerDialog();

		edgeFind = new EdgeFindSlowOp( mainFrame.getContentPane() );
		edgeFind.addPropertyChangeListener( this );
		edgeFindDialog = new VariableOptionDialog(mainFrame, "Edge-Find Parameters");
		edgeFindDialog.addVariable("Iterations", String.valueOf(EdgeFindSlowOp.DEFAULT_ITERATIONS) );
		edgeFindDialog.addVariable("Alpha", String.valueOf(EdgeFindSlowOp.DEFAULT_ALPHA) );
		edgeFindDialog.addVariable("Rho", String.valueOf(EdgeFindSlowOp.DEFAULT_RHO) );
		edgeFindDialog.addVariable("Mesh Step Size", String.valueOf(EdgeFindSlowOp.DEFAULT_MESH_STEP_SIZE) );
		edgeFindDialog.addOption("Use Two Point Formula", EdgeFindSlowOp.DEFAULT_TWOPOINT_FORMULA);
		edgeFindDialog.addOption("Display Diffused Image", EdgeFindSlowOp.DEFAULT_DISPLAY_DIFFUSED);
		edgeFindDialog.addOption("Display Edges", EdgeFindSlowOp.DEFAULT_DISPLAY_EDGE);
		edgeFindDialog.addWindowListener( new WindowAdapter (){
			public void windowDeactivated(WindowEvent e){
				if( !edgeFindDialog.isCanceled() ) {
					String iterationString = edgeFindDialog.getValue("Iterations");
					String alphaString = edgeFindDialog.getValue("Alpha");
					String rhoString = edgeFindDialog.getValue("Rho");
					String meshString = edgeFindDialog.getValue("Mesh Step Size");
					try{
						edgeFind.setIterations( 			Integer.parseInt(iterationString) 							);
						edgeFind.setAlpha( 					Double.parseDouble(alphaString) 							);
						edgeFind.setRho( 					Double.parseDouble(rhoString) 								);
						edgeFind.setMeshStepSize( 			Integer.parseInt(meshString) 								);
						edgeFind.setUseTwoPointFormula( 	edgeFindDialog.isOptionSelected("Use Two Point Formula") 	);
						edgeFind.setDisplayDiffusedImage( 	edgeFindDialog.isOptionSelected("Display Diffused Image")	);
						edgeFind.setDisplayEdgeOverlay( 	edgeFindDialog.isOptionSelected("Display Edges") 			);
					} catch (NumberFormatException nfe){
						statusArea.printErrorMessage("Parameters could not be parsed.");
						edgeFindDialog.setVisible(true);
					}
				}
			}
		});
		edgeFindDialog.pack();
		edgeFindDialog.centerDialog();

		colorQuantization = new ColorQuantizationSlowOp( mainFrame.getContentPane() );
		colorQuantization.addPropertyChangeListener( this );
		colorQuantizationDialog = new VariableOptionDialog(mainFrame, "Color-Quantization Parameters");
		colorQuantizationDialog.addVariable("Colors", String.valueOf(ColorQuantizationSlowOp.DEFAULT_NUMBER_OF_COLORS) );
		colorQuantizationDialog.addWindowListener( new WindowAdapter (){
			public void windowDeactivated(WindowEvent e){
				if( !colorQuantizationDialog.isCanceled() ) {
					String value = colorQuantizationDialog.getValue("Colors");
					try{
						colorQuantization.setNumberOfColors( Integer.parseInt(value) );
					} catch (NumberFormatException nfe){
						statusArea.printErrorMessage("Parameters could not be parsed.");
						colorQuantizationDialog.setVisible(true);
					}
				}
			}
		});
		colorQuantizationDialog.pack();
		colorQuantizationDialog.centerDialog();

		backgroundSubtract = new BackgroundSubtractFastOp();
		backgroundSubtractionDialog = new VariableOptionDialog( mainFrame, "Background Subtraction Parameters");
		backgroundSubtractionDialog.addVariable("Ratio", String.valueOf(0.5) );
		backgroundSubtractionDialog.addOption("Bounded", false);
		backgroundSubtractionDialog.addWindowListener( new WindowAdapter (){
			public void windowDeactivated(WindowEvent e){
				if( !backgroundSubtractionDialog.isCanceled() ) {
					String value = backgroundSubtractionDialog.getValue("Ratio");
					try{
						backgroundSubtract.setRatio( Double.parseDouble(value) );
						backgroundSubtract.setBounded( backgroundSubtractionDialog.isOptionSelected("Bounded") );
					} catch (NumberFormatException nfe){
						statusArea.printErrorMessage("Parameters could not be parsed.");
						backgroundSubtractionDialog.setVisible(true);
					}
				}
			}
		});
		backgroundSubtractionDialog.pack();
		backgroundSubtractionDialog.centerDialog();

		rescaleOp = new RescaleFastOp();
		totalVariationOp = new TotalVariationFastOp();

		bicOp = new MultiOp("BAM Image Converter v1.0", mainFrame.getContentPane());
		bicOpDialog = new VariableOptionDialog( mainFrame, "BAM Image Converter Parameters");
		bicOpDialog.addVariable("Saved file postfix", MultiOp.DEFAULT_FILE_POSTFIX);
		bicOpDialog.addWindowListener( new WindowAdapter(){
			public void windowDeactivated(WindowEvent e){
				if( !bicOpDialog.isCanceled() ) {
					String value = bicOpDialog.getValue("Saved file postfix");
					bicOp.setFilePostfix( value );
				}
			}
		});
		bicOpDialog.pack();
		bicOpDialog.centerDialog();
		bicOp.addDataOp( rescaleOp );
		bicOp.addDataOp( backgroundSubtract );
		bicOp.addDataOp( denoiseDeblur );
		bicOp.addDataOp( edgeFind );
		bicOp.addDataOp( colorQuantization );
		bicOp.addPropertyChangeListener( this );
		denoiseDeblur.addPropertyChangeListener(bicOp);
		edgeFind.addPropertyChangeListener(bicOp);
		colorQuantization.addPropertyChangeListener(bicOp);
	}

	public void valueChanged(ListSelectionEvent lse) {
		if (!lse.getValueIsAdjusting()) {
			if ( imageView.getImageCount()==1 || isSaved || JOptionPane.YES_OPTION==JOptionPane.showConfirmDialog(mainFrame,"Select new image without saving modifications?", "Modified Image", JOptionPane.YES_NO_OPTION) ) {
				imageView.clear();
				setWorkingImage( ImageFileList.removeAcceptableFileTypePostfix(imageList.getSelectedImageName()), imageList.getSelectedImage() );
				isSaved=true;
				if( imageList.isMultiSelection() ){
					statusArea.printMessage("Multiple images selected.");
				} else {
					statusArea.printMessage(getWorkingImageName()+ " selected.");
				}
 			}
 		}
 	}

	public void propertyChange(PropertyChangeEvent e){

		String propertyName = e.getPropertyName();
		Object source = e.getSource();

		if (source instanceof AbstractDataOp){
			AbstractDataOp sourceOp = (AbstractDataOp)source;
			if ( !(sourceOp instanceof MultiOp) ) statusArea.addIndent();
			if ( propertyName.equals("processingMessage") ){
				statusArea.printCommentMessage( (String)e.getNewValue() );
			}
			if ( propertyName.equals("processedImage") ){
				if ( !sourceOp.isError() ){
					setWorkingImage( getWorkingImageName()+"_"+sourceOp.opShortName, (BufferedImage)e.getNewValue() );
					statusArea.printActionMessage( sourceOp.opLongName+": " +sourceOp.getMessage() );
				} else {
					statusArea.printErrorMessage( sourceOp.opLongName+" Error: "+sourceOp.getMessage() );
				}
				if( source instanceof MultiOp ) {
					statusArea.removeIndent();
					statusArea.printMessage( sourceOp.opLongName+" complete "+ (new SimpleDateFormat("MMMM dd, yyyy - H:mm:ss")).format(new Date()) +".");
					doingMultiOp = false;
				}
				if( !doingMultiOp ) setBusy(false);
			}
			if( propertyName.equals("processingNewImage") ){
				imageView.clear();
				statusArea.printActionMessage( sourceOp.opLongName+" has selected "+(String)e.getNewValue() );
				setWorkingImage( ImageFileList.removeAcceptableFileTypePostfix((String)e.getNewValue()), (BufferedImage)e.getOldValue() );
			}
			if ( !(sourceOp instanceof MultiOp) ) statusArea.removeIndent();
		}

	}

	public void actionPerformed(ActionEvent ae){
		String temp = ae.getActionCommand();

		if (temp.equals("Set Image Directory...")) {
			imageList.populateList();
			mainFrame.repaint();
		}

		if (temp.equals("Save...")){
			if (isSaved || imageView.getImageCount()==1 ) {
				statusArea.printErrorMessage("Image is already saved or has not been modifed.");
				return;
			}
			int	returnval = fileChooser.showSaveDialog(mainFrame);
			if ( returnval==JFileChooser.APPROVE_OPTION ){
				File saveFile = fileChooser.getSelectedFile();
				try {
					FileOutputStream saveStream = new FileOutputStream(saveFile);
					GifEncoder encoder = new GifEncoder( getWorkingImage(), saveStream );
					encoder.encode();
					saveStream.close();
					isSaved = true;
					statusArea.printActionMessage( "Saved modified "+getWorkingImageName()+" as " +fileChooser.getSelectedFile().getPath() + ".");
				} catch (IOException e) {
					statusArea.printErrorMessage( "Unable to write "+getWorkingImageName()+".");
				}
			}
		}

		if (temp.equals("Save Log File...")){
			int	returnval = logFileChooser.showSaveDialog(mainFrame);
			if ( returnval==JFileChooser.APPROVE_OPTION ){
				File saveFile = logFileChooser.getSelectedFile();
				try {

					PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(saveFile)));
					writer.println( statusArea.getText() );
					writer.close();
					isSaved = true;
					statusArea.printActionMessage( "Saved log file "+saveFile.getName()+".");
				} catch (IOException e) {
					statusArea.printErrorMessage( "Unable to write log file "+saveFile.getName()+".");
				}
			}
		}

		if (temp.equals("Exit")){
			System.exit(0);
		}

		if (temp.equals("Clear Log")){
			statusArea.clear();
			statusArea.printActionMessage( "Log cleared " + (new SimpleDateFormat("MMMM dd, yyyy - H:mm:ss")).format(new Date()) );
			statusArea.printMessage( "Image Processor - v"+VERSION );
		}

		if (temp.equals("Image information")){
			BufferedImage image = getWorkingImage();
			if (image!=null){
				int[] minmax = ImageUtilities.getMinMax( image );
				statusArea.printCommentMessage( "("+image.getWidth()+"x"+image.getHeight()+"), minGray="+minmax[0]+", maxGray="+minmax[1]  );
			}
		}

		if (temp.equals("Rescale between 0 & 255")){
			BufferedImage result = rescaleOp.filter( getWorkingImage() );
			if (result==null || rescaleOp.isError()){
				statusArea.printErrorMessage( rescaleOp.opLongName+" Error: " +rescaleOp.getMessage());
			} else {
				setWorkingImage( getWorkingImageName()+"_"+rescaleOp.opShortName, result );
				statusArea.printActionMessage( rescaleOp.opLongName+" " +rescaleOp.getMessage());
			}
		}

		if (temp.equals("Calculate Total Variation")){
			BufferedImage result = totalVariationOp.filter( getWorkingImage() );
			if (result==null || totalVariationOp.isError()){
				statusArea.printErrorMessage( totalVariationOp.opLongName+" Error: " +totalVariationOp.getMessage());
			} else {
				setWorkingImage( getWorkingImageName()+"_"+totalVariationOp.opShortName, result );
				statusArea.printActionMessage( totalVariationOp.opLongName+" " +totalVariationOp.getMessage());
			}
		}

		if (temp.equals("Background-Subtract")){
			BufferedImage result = backgroundSubtract.filter( getWorkingImage() );
			if (result==null || backgroundSubtract.isError()){
				statusArea.printErrorMessage( backgroundSubtract.opLongName+" Error: " +backgroundSubtract.getMessage());
			} else {
				setWorkingImage( getWorkingImageName()+"_"+backgroundSubtract.opShortName, result );
				statusArea.printActionMessage( backgroundSubtract.opLongName+" " +backgroundSubtract.getMessage());
			}
		}

		if (temp.equals("Background-Subtract...")) {
			BufferedImage image = getWorkingImage();
			if (image!=null){
				backgroundSubtract.setBackgroundImage( image );
				statusArea.printActionMessage("Background image set.");
				backgroundSubtractionDialog.setVisible(true);
			} else {
				statusArea.printErrorMessage("Background image not set.  Image is null.");
			}
		}

		if (temp.equals("Denoise-Deblur")){
			denoiseDeblur.setImage( getWorkingImage() );
			setBusy(true);
			denoiseDeblur.start();
		}

		if (temp.equals("Denoise-Deblur...")){
			denoiseDeblurDialog.setVisible(true);
		}

		if (temp.equals("Edge-Find")){
			edgeFind.setImage( getWorkingImage() );
			setBusy(true);
			edgeFind.start();
		}

		if (temp.equals("Edge-Find...")){
			edgeFindDialog.setVisible(true);
		}

		if (temp.equals("Color-Quantization")){
			colorQuantization.setImage( getWorkingImage() );
			setBusy(true);
			colorQuantization.start();
		}

		if (temp.equals("Color-Quantization...")){
			colorQuantizationDialog.setVisible(true);
		}

		if (temp.equals("BAM Image Converter v1.0")){
			bicOp.clearImageFiles();
			bicOp.setImageFiles( imageList.getSelectedImageFiles() );
			doingMultiOp = true;
			setBusy(true);
			statusArea.printMessage(bicOp.opLongName+" starting.");
			bicOp.start();
		}

		if (temp.equals("BAM Image Converter v1.0...")){
			bicOpDialog.setVisible(true);
		}

	}

	protected void setBusy(boolean busy){
		if(busy){
			imageList.setEnabled(false);
			fileMenu.setEnabled(false);
			setMenu.setEnabled(false);
			processMenu.setEnabled(false);
			batchProcessMenu.setEnabled(false);
		} else {
			imageList.setEnabled(true);
			fileMenu.setEnabled(true);
			setMenu.setEnabled(true);
			processMenu.setEnabled(true);
			batchProcessMenu.setEnabled(true);
		}
	}

	public BufferedImage getWorkingImage(){
		if ( imageView.getImageCount()==0 ) return null;
		BufferedImage temp = ImageUtilities.createBufferedImage( imageView.getLastImage() );
		return temp;
	}

	public String getWorkingImageName(){
		if ( imageView.getImageCount()==0 ) return "";
		return imageView.getLastImageName();
	}

	public void setWorkingImage(String name, Image image){
		if( image==null ) return;
		isSaved = false;
		imageView.addImage( name, image );
		vsplit.validate();
	}

	public void print(String temp){
		statusArea.print(temp);
	}

	public void println(String temp){
		statusArea.println( temp );
	}

	public static void main(String[] args){
		new IP();
	}

}



