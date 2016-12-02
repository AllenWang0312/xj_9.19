package measurement.color.com.xj_919.and.Utils.io.FileOpener;

public class FileInfo{
	
	private String name;
	private String dirPath;
	private String secondInfo;
	public FileInfo(String name, String dirPath,String secondInfo) {
		super();
		this.name = name;
		this.dirPath = dirPath;
		this.secondInfo = secondInfo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDirPath() {
		return dirPath;
	}
	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}
	public String getSecondInfo() {
		return secondInfo;
	}
	public void setSecondInfo(String secondInfo) {
		this.secondInfo = secondInfo;
	}
}
