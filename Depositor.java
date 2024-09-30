
public class Depositor {
	private Name n;
	private String SSN;
	
	public Depositor() {
		n = new Name();
		SSN = "";
	}
	public Depositor(Name n, String ssn) {
		this.n=n;
		SSN=ssn;
	}
	public Depositor(Depositor d) { //copy constructor
		this.n = new Name(d.n);
		this.SSN = d.SSN;
	}
	private void setSSN(String s) {
		SSN = s;
	}
	private void setName(Name n) {
		this.n = n;
	}
	public String getSSN() {
		return SSN;
	}
	public Name getName() {
		Name copy = new Name(n);
		return copy;
	}
	public String toString() {
		return n.toString() + String.format("%-15s", SSN);
	}
	public boolean equals(Depositor d) {
		if(n.equals(d.getName())) {
			return true;
		}
		return false;
	}
}
