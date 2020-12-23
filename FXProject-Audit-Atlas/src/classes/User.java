package classes;

public class User {
	private String email;
	private String password;
	private String rolle;

	public User(String email, String password, String rolle) {
		super();
		this.email = email;
		this.password = password;
		this.rolle = rolle;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRolle() {
		return rolle;
	}

	public void setRolle(String rolle) {
		this.rolle = rolle;
	}

	@Override
	public String toString() {
		return "User [email=" + email + ", password=" + password + ", rolle=" + rolle + "]";
	}
}
