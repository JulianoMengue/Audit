package classes;

public class CheckList {
	private String id;
	private String standartNormKapitel;
	private String normForderung;
	private String fragen;
	
	public CheckList(String id, String standartNormKapitel, String normForderung, String fragen) {
		super();
		this.id = id;
		this.standartNormKapitel = standartNormKapitel;
		this.normForderung = normForderung;
		this.fragen = fragen;
	}

	public String getStandartNormKapitel() {
		return standartNormKapitel;
	}

	public void setStandartNormKapitel(String standartNormKapitel) {
		this.standartNormKapitel = standartNormKapitel;
	}

	public String getNormForderung() {
		return normForderung;
	}

	public void setNormForderung(String normForderung) {
		this.normForderung = normForderung;
	}

	public String getFragen() {
		return fragen;
	}

	public void setFragen(String fragen) {
		this.fragen = fragen;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "CheckList [id=" + id + ", standartNormKapitel=" + standartNormKapitel + ", normForderung="
				+ normForderung + ", fragen=" + fragen + "]";
	}
	
}
