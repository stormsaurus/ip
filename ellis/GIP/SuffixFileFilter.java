
import java.io.*;


public class SuffixFileFilter implements java.io.FileFilter{

	private String[]	suffixes;
	
	public SuffixFileFilter(String arg){
		suffixes = new String[1];
		suffixes[0]=arg;	
	}

	public SuffixFileFilter(String[] arg){
		suffixes = arg;
	}

	public boolean accept(File arg){
		for (int i=0; i<suffixes.length; i++){
			if ( arg.getName().endsWith(suffixes[i]) ) {
				return true;
			}
		}
		return false;
	}

}