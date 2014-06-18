package bean;

import java.util.ArrayList;
import java.util.List;

public class QunsEntity extends Entity{

	public int icon;
	public String id ;
	public String label;
	public String title;
	public String display;
	public String sort;
	public String logo;
	public List<String> descriptions = new ArrayList<String>();
	public List<RequireEntity> requires = new ArrayList<RequireEntity>();
	
	public QunsEntity() {
		
	}
	
	public QunsEntity(int icon, String id, String label) {
		this.icon = icon;
		this.id = id;
		this.label = label;
	}
}
