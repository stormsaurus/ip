class GIPSingle extends AbstractAction{

	GIP		gip;

	public GIPSingle(GIP arg){
		super( "",new ImageIcon("singleprocess.gif") );
		gip = arg;
	}

	public void actionPerformed(ActionEvent ae) {
		gip.edgeProcess();
	}

}

class GIPBatch extends AbstractAction{

	GIP		gip;

	public GIPBatch(GIP arg){
		super( "",new ImageIcon("batchprocess.gif") );
		gip = arg;
	}

	public void actionPerformed(ActionEvent ae) {
	}

}

class GIPHistSum extends AbstractAction{

	GIP		gip;

	public GIPHistSum(GIP arg){
		super( "",new ImageIcon("histsumprocess.gif") );
		gip = arg;
	}

	public void actionPerformed(ActionEvent ae) {
	}

}

class GIPEdgeSum extends AbstractAction{

	GIP		gip;

	public GIPEdgeSum(GIP arg){
		super( "",new ImageIcon("edgesumprocess.gif") );
		gip = arg;
	}

	public void actionPerformed(ActionEvent ae) {
	}

}

class GIPEdgeBox extends AbstractAction{

	GIP		gip;

	public GIPEdgeBox(GIP arg){
		super( "",new ImageIcon("edgeboxprocess.gif") );
		gip = arg;
	}

	public void actionPerformed(ActionEvent ae) {
	}

}