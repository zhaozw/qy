package bean;

public class FunsPhotoEntity extends Entity {
	public String filePath;
	public String fileUrl;
	public int type;
	public FunsPhotoEntity(String filePath, String fileUrl) {
		super();
		this.filePath = filePath;
		this.fileUrl = fileUrl;
	}
	
	public FunsPhotoEntity(String filePath, String fileUrl, int type) {
		super();
		this.filePath = filePath;
		this.fileUrl = fileUrl;
		this.type = type;
	}
}
