
public class Name {
	private String first;
	private String last;
	
	public Name() {
		last = "";
		first = "";
	}
	public Name(String F, String L) {
		first=F;
		last=L;
	}
	public Name(Name n) { // Copy Constructor
		first = n.first;
		last = n.last;
	}
	private void setLast(String s) {
		last = s;
	}
	private void setFirst(String s) {
		first = s;
	}
	public String getLastName() {
		return last;
	}
	public String getFirstName() {
		return first;
	}
	public String toString() {
	    return String.format("%-15s %-15s", last, first);
	}
	public boolean equals(Name n) {
		if(first.equals(n.getFirstName()) && last.equals(n.getLastName())) {
			return true;
		}
		return false;
	}
}
