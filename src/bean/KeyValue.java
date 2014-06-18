package bean;

import java.util.ArrayList;
import java.util.List;

public class KeyValue extends Entity{
	public String key;
	public String value;
	public List<RelationshipEntity> relations = new ArrayList<RelationshipEntity>();
	
	public KeyValue() {
	}
	
	public KeyValue(String key, String value) {
		this.key = key;
		this.value = value;
	}
}
