package gov.nasa.pds.citool.diff;

import java.util.List;

public class DiffRecord {
	private String info;
	private List<String> fromSource;
	private List<String> fromTarget;
	
	public DiffRecord(String info, List<String> fromSource, List<String> fromTarget) {
		this.info = info;
		this.fromSource = fromSource;
		this.fromTarget = fromTarget;
	}
	
	public String getInfo() {return info;}
	
	public List<String> getFromSource() {return fromSource;}
	
	public List<String> getFromTarget() {return fromTarget;}
}

