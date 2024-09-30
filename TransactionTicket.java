import java.util.Calendar;

public class TransactionTicket {
	private Calendar date;
	private Calendar maturityDate;
	private int accNum;
	private String dateOfTransaction;
	private String typeOfTransaction;
	private double amountOfTransaction;
	private int termOfCD;
	private String Dates;
	
	public TransactionTicket() {
		accNum = 0;
		dateOfTransaction = "";
		typeOfTransaction = "";
		amountOfTransaction = 0.00;
		date = Calendar.getInstance();
		date.clear();
		termOfCD = 0;
	}
	public TransactionTicket(TransactionTicket t) { // Copy Constructor
		this.date = t.date;
		this.accNum = t.accNum;
		this.dateOfTransaction = t.dateOfTransaction;
		this.typeOfTransaction = t.typeOfTransaction;
		this.amountOfTransaction = t.amountOfTransaction;
		this.termOfCD = t.termOfCD;
	}
	public TransactionTicket(int accnum, String dateOfTrans, String typeTrans, double amount, int term) {
        accNum = accnum;
        dateOfTransaction = dateOfTrans;
        typeOfTransaction = typeTrans;
        amountOfTransaction = amount;
        termOfCD = term;
        date = Calendar.getInstance();
        date.clear();

        if (!dateOfTransaction.isEmpty()) {
            String[] dateArr = dateOfTransaction.split("/");
            if (dateArr.length == 3) {
                date.set(Integer.parseInt(dateArr[2]),
                        Integer.parseInt(dateArr[0]) - 1,
                        Integer.parseInt(dateArr[1]));
            }
        }
    }
	public TransactionTicket(int accnum, String dateOfTrans, String typeTrans, double amount, int term, Calendar matDate) { //for balance inquiry
        accNum = accnum;
        dateOfTransaction = dateOfTrans;
        typeOfTransaction = typeTrans;
        amountOfTransaction = amount;
        termOfCD = term;
        maturityDate = matDate;
        date = Calendar.getInstance();
        date.clear();

        if (!dateOfTransaction.isEmpty()) {
            String[] dateArr = dateOfTransaction.split("/");
            if (dateArr.length == 3) {
                date.set(Integer.parseInt(dateArr[2]),
                        Integer.parseInt(dateArr[0]) - 1,
                        Integer.parseInt(dateArr[1]));
            }
        }
    }
	public TransactionTicket(int accnum, String dateOfTrans, String typeTrans, double amount) { //for non CD
		accNum = accnum;
		dateOfTransaction = dateOfTrans;
		typeOfTransaction = typeTrans;
		amountOfTransaction = amount;
		date = Calendar.getInstance();
		date.clear();
		String[] dateArr = dateOfTransaction.split("/");
		date.set(Integer.parseInt(dateArr[2]),
				Integer.parseInt(dateArr[0]) - 1,
				Integer.parseInt(dateArr[1]));
	}
	public TransactionTicket(String dateOfTrans, String typeTrans, double amount) { //for account info plus history w/o accNum 
        dateOfTransaction = dateOfTrans;
        typeOfTransaction = typeTrans;
        amountOfTransaction = amount;
        date = Calendar.getInstance();
        date.clear();

        if (!dateOfTransaction.isEmpty()) {
            String[] dateArr = dateOfTransaction.split("/");
            if (dateArr.length == 3) {
                date.set(Integer.parseInt(dateArr[2]),
                        Integer.parseInt(dateArr[0]) - 1,
                        Integer.parseInt(dateArr[1]));
            }
        }
    }
	public TransactionTicket(int accnum, String typeTrans, double amount) { //for newAcc
		accNum = accnum;
		typeOfTransaction = typeTrans;
		amountOfTransaction = amount;
		date = Calendar.getInstance();
	}
	public TransactionTicket(int accnum, String typeTrans, double amount, String checkDate) { //for clear check
		accNum = accnum;
		typeOfTransaction = typeTrans;
		amountOfTransaction = amount;
		Dates = checkDate;
		
	}
	
	public Calendar getDateOfTransaction() {
		return date;
	}
	public String getDateOfTransactionString() {
		date = Calendar.getInstance();
		String tempStr;
		if(date!=null) {
			tempStr = String.format("%02d/%02d/%4d",
					date.get(Calendar.MONTH) + 1,
					date.get(Calendar.DAY_OF_MONTH),
					date.get(Calendar.YEAR));
			return tempStr;
		}
		return "N/A";
	}
	public String getCheckDateStr() {
		String tempStr;
		tempStr = String.format("%02d/%02d/%4d",
				date.get(Calendar.MONTH) + 1,
				date.get(Calendar.DAY_OF_MONTH),
				date.get(Calendar.YEAR)
				);
		return tempStr;
	}
	public int getAcctNum() {
		return accNum;
	}
	public String getTransactionType() {
		return typeOfTransaction;
	}
	public double getTransactionAmount() {
		return amountOfTransaction;
	}
	public int getTermOfCD() {
		return termOfCD;
	}
	public String getMaturityDateStr() {
		String tempStr;
		if(maturityDate!=null) {
			tempStr = String.format("%02d/%02d/%4d",
					maturityDate.get(Calendar.MONTH) + 1,
					maturityDate.get(Calendar.DAY_OF_MONTH),
					maturityDate.get(Calendar.YEAR));
			return tempStr;
		}
		return "N/A";
	}
	public String getDates() {
		return Dates;
	}
	public String toString() {
		if(typeOfTransaction.equals("New Account")) {
			String str = "\nTransaction Requested: " + typeOfTransaction + "\nDate of Transaction: " + getDateOfTransactionString() 
					 + "\nAccount Number: " + accNum;
			return str;
		}
		if(typeOfTransaction.equals("Clear Check")) {
			String str = "\nTransaction Requested: " + typeOfTransaction + "\nDate of Transaction: " + getDateOfTransactionString()
			+ "\nCheck Date: " + Dates + "\nAccount Number: " + accNum + "\nCheck Amount: $" + amountOfTransaction;
			return str;
		}
		String str = "\nTransaction Requested: " + typeOfTransaction + "\nDate of Transaction: " + getDateOfTransactionString() +
				 "\nAccount Number: " + accNum + "\nAmount of Transaction: $" + amountOfTransaction;
		if(termOfCD != 0) {
			str += "\nMaturityDate: " + getMaturityDateStr();
		}
		return str;
	}

}
