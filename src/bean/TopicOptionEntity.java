package bean;

public class TopicOptionEntity extends Entity{

	public int icon;
	public String name;
	public boolean isChosen;
	public int type;
	
	public String category_id;
	public String title;
	public String display;
	public String sort;
	public String thumb;
	
	public TopicOptionEntity() {
		super();
	}
	
	public TopicOptionEntity(int icon, String name, boolean isChosen) {
		super();
		this.icon = icon;
		this.name = name;
		this.isChosen = isChosen;
	}
	
	
}
