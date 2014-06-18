package bean;

public class FunsPhotoEntity extends Entity {
	public String filePath;
	public String fileUrl;
	public FunsPhotoEntity(String filePath, String fileUrl) {
		super();
		this.filePath = filePath;
		this.fileUrl = fileUrl;
	}
	
}
