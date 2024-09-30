import java.util.Calendar;

public class Check {
	private int accNum;
	private double checkAmount;
	private String dateOfCheck;
	private Calendar date;
	
	public Check() {
		date = Calendar.getInstance();
		date.clear();
		accNum = 0;
		checkAmount = 0;
		dateOfCheck = "";
	}
	public Check(int a, double c, String dateStr) {
		date = Calendar.getInstance();
		date.clear();
		accNum = a;
		checkAmount = c;
		dateOfCheck = dateStr;
		String[] dateArr = dateOfCheck.split("/");
		date.set(Integer.parseInt(dateArr[2]),
				Integer.parseInt(dateArr[0]) - 1,
				Integer.parseInt(dateArr[1]));
	}
	public Check(Check c) { // Copy Constructor
		accNum = c.accNum;
		checkAmount = c.checkAmount;
		dateOfCheck = c.dateOfCheck;
		date = c.date;
	}
	private void setDateOfCheck(String s) {
		date.clear();
		String[] dateArr = s.split("/");
		date.set(Integer.parseInt(dateArr[2]),
				Integer.parseInt(dateArr[0]) - 1,
				Integer.parseInt(dateArr[1]));
		
	}
	public int getAccountNumber() {
		return accNum;
	}
	public double getCheckAmount() {
		return checkAmount;
	}
	public Calendar getDate() {
		return date;
	}
	public String getDateOfCheckStr() {
		String tempStr;
		tempStr = String.format("%02d/%02d/%4d",
				date.get(Calendar.MONTH) + 1,
				date.get(Calendar.DAY_OF_MONTH),
				date.get(Calendar.YEAR)
				);
		return tempStr;
	}
	public Calendar getDateOfCheck() {
		return date;
	}
	public String toString() {
		return "Account Number: " + accNum + "\nCheck Amount: $" + checkAmount + "\nDate of Check: " + dateOfCheck;
	}
}
