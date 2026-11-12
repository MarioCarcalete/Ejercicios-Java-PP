package Examen1;

import com.google.gson.annotations.SerializedName;
import java.util.List;
public class Lista {
	@SerializedName("ucm")
	
	private List<ucm> ucm2;
	public List<ucm> getUCM(){
		return ucm2;
	}
	
	public void setUCM(List<ucm> c) {
		this.ucm2 = c;
	}

}
