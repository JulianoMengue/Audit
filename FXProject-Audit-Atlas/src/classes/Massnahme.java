package classes;

public class Massnahme {
	private String id;
	private String standart;
	private String kapitel;
	private String massnahmenart;
	private String verantwortlich;
	private String kurzbeschreibung;
	private String unsetzung;

	public Massnahme(String id, String standart, String kapitel, String massnahmenart, String verantwortlich,
			String kurzbeschreibung, String unsetzung) {
		super();
		this.id = id;
		this.standart = standart;
		this.kapitel = kapitel;
		this.massnahmenart = massnahmenart;
		this.verantwortlich = verantwortlich;
		this.kurzbeschreibung = kurzbeschreibung;
		this.unsetzung = unsetzung;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStandart() {
		return standart;
	}

	public void setStandart(String standart) {
		this.standart = standart;
	}

	public String getKapitel() {
		return kapitel;
	}

	public void setKapitel(String kapitel) {
		this.kapitel = kapitel;
	}

	public String getMassnahmenart() {
		return massnahmenart;
	}

	public void setMassnahmenart(String massnahmenart) {
		this.massnahmenart = massnahmenart;
	}

	public String getVerantwortlich() {
		return verantwortlich;
	}

	public void setVerantwortlich(String verantwortlich) {
		this.verantwortlich = verantwortlich;
	}

	public String getKurzbeschreibung() {
		return kurzbeschreibung;
	}

	public void setKurzbeschreibung(String kurzbeschreibung) {
		this.kurzbeschreibung = kurzbeschreibung;
	}

	public String getUnsetzung() {
		return unsetzung;
	}

	public void setUnsetzung(String unsetzung) {
		this.unsetzung = unsetzung;
	}

	@Override
	public String toString() {
		return "Massnahme [id=" + id + ", standart=" + standart + ", kapitel=" + kapitel + ", massnahmenart="
				+ massnahmenart + ", verantwortlich=" + verantwortlich + ", kurzbeschreibung=" + kurzbeschreibung
				+ ", unsetzung=" + unsetzung + "]";
	}

}
