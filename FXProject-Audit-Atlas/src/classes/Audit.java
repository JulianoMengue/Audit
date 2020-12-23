package classes;

public class Audit {
	private String id;
	private String datum;
	private String uhrzeit;
	private String abteilung;
	private String auditart;
	private String hausherr;
	private String auditorEins;
	private String auditorZwei;

	public Audit(String id, String datum, String uhrzeit, String abteilung, String auditart, String hausherr,
			String auditorEins, String auditorZwei) {

		this.id = id;
		this.datum = datum;
		this.uhrzeit = uhrzeit;
		this.abteilung = abteilung;
		this.auditart = auditart;
		this.hausherr = hausherr;
		this.auditorEins = auditorEins;
		this.auditorZwei = auditorZwei;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDatum() {
		return datum;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}

	public String getUhrzeit() {
		return uhrzeit;
	}

	public void setUhrzeit(String uhrzeit) {
		this.uhrzeit = uhrzeit;
	}

	public String getAbteilung() {
		return abteilung;
	}

	public void setAbteilung(String abteilung) {
		this.abteilung = abteilung;
	}

	public String getAuditart() {
		return auditart;
	}

	public void setAuditart(String auditart) {
		this.auditart = auditart;
	}

	public String getHausherr() {
		return hausherr;
	}

	public void setHausherr(String hausherr) {
		this.hausherr = hausherr;
	}

	public String getAuditorEins() {
		return auditorEins;
	}

	public void setAuditorEins(String auditorEins) {
		this.auditorEins = auditorEins;
	}

	public String getAuditorZwei() {
		return auditorZwei;
	}

	public void setAuditorZwei(String auditorZwei) {
		this.auditorZwei = auditorZwei;
	}

	@Override
	public String toString() {
		return "Audit [id=" + id + ", datum=" + datum + ", uhrzeit=" + uhrzeit + ", abteilung=" + abteilung
				+ ", auditart=" + auditart + ", hausherr=" + hausherr + ", auditorEins=" + auditorEins
				+ ", auditorZwei=" + auditorZwei + "]";
	}

}