package ellis.image.processing;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.beans.*;
import Acme.JPM.Encoders.GifEncoder;

import java.util.*;
import java.io.*;

public class MultiOp extends AbstractSlowOp implements PropertyChangeListener{

	public static String				DEFAULT_FILE_POSTFIX			= "_multi";

	private Vector						dataOps 				= new Vector();
	private AbstractDataOp				currentOp				= null;
	private boolean						readyForNextOp 			= true;
	private File[]						imageFiles				= null;
	private String						filePostfix				= DEFAULT_FILE_POSTFIX;

	public MultiOp(String name, Component parentComponent){
		super(parentComponent);
		opLongName = name;
		opShortName = "multi";
		minProgress = 0;
		maxProgress = 20;
	}

	public void run(){

		if (dataOps.size()==0 || imageFiles.length==0){
			setError(true);
			setMessage("Invalid Parameters.");
		} else {
			//Display which operations are in use.
			propertyChangeSupport.firePropertyChange( "processingMessage", "", opLongName+" is using the following operations..." );
			Iterator opIt;
			opIt = dataOps.listIterator();
			while (opIt.hasNext()){
				propertyChangeSupport.firePropertyChange( "processingMessage", "", ((AbstractDataOp)opIt.next()).toString() );
			}
			//Begin Processing files
			for(int i=0; i<imageFiles.length && !isCanceled() && !isError(); i++){
				setStatus(imageFiles[i].getName());
				progress++;
				updateProgress();

				originalImage = ImageUtilities.createBufferedImage( Toolkit.getDefaultToolkit().getImage(imageFiles[i].getPath()) );
				BufferedImage result;

					propertyChangeSupport.firePropertyChange( "processingNewImage", originalImage, imageFiles[i].getName() );
					double[][] source = ImageUtilities.getDataFromBufferedImage( originalImage );
					ImageUtilities.rescaleData( source, 0, 255 );
					processedImage = ImageUtilities.getBufferedImageFromData( source );

					result = null;
					opIt = dataOps.listIterator();
					while (opIt.hasNext() && !isCanceled() && !isError()){
						currentOp = (AbstractDataOp)opIt.next();
						currentOp.setImage( processedImage );
						try{
							if(currentOp instanceof AbstractFastOp){
								result = ((AbstractFastOp)currentOp).filter( processedImage );
								//propertyChangeSupport.firePropertyChange( "processedImage", result, imageFiles[i].getName() );
							} else{
								readyForNextOp = false;
								((AbstractSlowOp)currentOp).start();
									while( !readyForNextOp && !isCanceled() ){
										synchronized(this){
											try{
												wait();
											} catch(InterruptedException e){
												setError(true);
												setMessage("Interruped by unknown source." );
											}
										}
									}
								result = currentOp.getProcessedImage();
							}
						} catch (ArithmeticException e){
							propertyChangeSupport.firePropertyChange( "processingMessage", "", "An Exception occured.  "+e );
							break;
						} catch (IllegalArgumentException e){
							propertyChangeSupport.firePropertyChange( "processingMessage", "", "An Exception occured.  "+e );
							break;
						}
						processedImage = result;
						if (processedImage==null || currentOp.isError()){
							propertyChangeSupport.firePropertyChange( "processingMessage", "", "Operation encountered a problem with "+currentOp.opLongName+".  Skipping remaining operations on this image." );
							break;
						}
					}

					if( !isError() ) setMessage("Complete.");
					//Save each image as a gif file
					if( processedImage!=null && !isCanceled() && !isError() ) {

						File saveFile = new File( ImageFileList.removeAcceptableFileTypePostfix(imageFiles[i].getPath())+filePostfix+".gif");
						try {
							FileOutputStream saveStream = new FileOutputStream(saveFile);
							GifEncoder encoder = new GifEncoder( processedImage, saveStream );
							encoder.encode();
							saveStream.close();
						} catch (IOException e) {
							setError(true);
							setMessage("Unable to save file "+saveFile.getName());
						}
					}
					//Change the message if it was really cancelled
					if (isCanceled()) {
						setError(true);
						setMessage("Operation cancelled.");
					}
			}

		}

		setProcessedImageOnFinish( processedImage );
	}

	public void propertyChange(PropertyChangeEvent e){
		readyForNextOp = true;
		synchronized(this){
			notify();
		}
	}

	public void setFilePostfix(String filePostfix){
		this.filePostfix = filePostfix;
	}

	public void addDataOp(AbstractDataOp dataOp){
		if( thread!=null ) throw new IllegalStateException(opLongName+" is currently running.");
		dataOps.add(dataOps.size(), dataOp);
		currentOp = dataOp;
	}

	public void clearDataOps(){
		if( thread!=null ) throw new IllegalStateException(opLongName+" is currently running.");
		dataOps.clear();
		currentOp = null;
	}

	public void setImageFiles(File[] files){
		this.imageFiles = files;
		maxProgress = imageFiles.length;
	}

	public void clearImageFiles(){
		this.imageFiles = null;
	}

}