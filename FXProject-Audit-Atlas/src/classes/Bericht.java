package classes;

public class Bericht {
	private String id;
	private String eingesehene;
	private String erfuellt;
	private String bemerkungen;

	public Bericht(String id, String eingesehene, String erfuellt, String bemerkungen) {
		this.id = id;
		this.eingesehene = eingesehene;
		this.erfuellt = erfuellt;
		this.bemerkungen = bemerkungen;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEingesehene() {
		return eingesehene;
	}

	public void setEingesehene(String eingesehene) {
		this.eingesehene = eingesehene;
	}

	public String getErfuellt() {
		return erfuellt;
	}

	public void setErfuellt(String erfuellt) {
		this.erfuellt = erfuellt;
	}

	public String getBemerkungen() {
		return bemerkungen;
	}

	public void setBemerkungen(String bemerkungen) {
		this.bemerkungen = bemerkungen;
	}

	@Override
	public String toString() {
		return "Bericht [id=" + id + ", eingesehene=" + eingesehene + ", erfuellt=" + erfuellt + ", bemerkungen="
				+ bemerkungen + "]";
	}

}
