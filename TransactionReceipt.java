import java.util.Calendar;

public class TransactionReceipt {
	private TransactionTicket t;
	private boolean successIndicatorFlag;
	private String reasonForFailure;
	private String accountType;
	private double preTransactionBalance;
	private double postTransactionBalance;
	private Calendar postTransactionMaturityDate;
	
	public TransactionReceipt() {
		t = null;
		postTransactionMaturityDate = Calendar.getInstance();
		postTransactionMaturityDate.clear();
		successIndicatorFlag = false;
		reasonForFailure = "";
		accountType = "";
		preTransactionBalance = 0.00;
		postTransactionBalance = 0.00;
	}
	public TransactionReceipt(TransactionReceipt receipt) { // Copy Constructor
		this.t = new TransactionTicket(t);
		successIndicatorFlag = receipt.successIndicatorFlag;
		reasonForFailure = receipt.reasonForFailure;
		accountType = receipt.accountType;
		preTransactionBalance = receipt.preTransactionBalance;
		postTransactionBalance = receipt.postTransactionBalance;
		postTransactionMaturityDate = receipt.postTransactionMaturityDate;
	}
	public TransactionReceipt(TransactionTicket ti, boolean successIndicator, String reasonForFail, 
			String acctType, double preAmount, double postAmount, Calendar maturityDate) {
		t = ti;
		successIndicatorFlag = successIndicator;
		reasonForFailure = reasonForFail;
		accountType = acctType;
		preTransactionBalance = preAmount;
		postTransactionBalance = postAmount;
		postTransactionMaturityDate = Calendar.getInstance();
		postTransactionMaturityDate.clear();
		postTransactionMaturityDate = maturityDate;
	}
	public TransactionReceipt(TransactionTicket ticket, double pre, double post, int termOfCD) {
		
	}
	public TransactionReceipt(TransactionTicket ticket, boolean successIndi, String fail) {
			t = ticket;
			successIndicatorFlag = successIndi;
			reasonForFailure = fail;
			preTransactionBalance = 0.00;
			postTransactionBalance = 0.00;
			postTransactionMaturityDate = null;
	}
	public TransactionTicket getTransactionTicket() {
		TransactionTicket copy = new TransactionTicket(t);
		return copy;
	}
	public boolean getTransactionSuccessIndicatorFlag() {
		return successIndicatorFlag;
	}
	public String getTransactionFailureReason() {
		return reasonForFailure; 
	}
	public double getPreTransactionBalance() {
		return preTransactionBalance;
	}
	public double getPostTransactionBalance() {
		return postTransactionBalance;
	}
	public Calendar getPostTransactionMaturityDate() {
		return postTransactionMaturityDate; 
	}
	public String getAcctType() {
		return accountType;
	}
	public Calendar getDateOfTransaction() {
		return t.getDateOfTransaction();
	}
	public String getStatus() {
		if(successIndicatorFlag) {
			return "Done";
		}
		return "Failed";
	}
	public String getPostTransactionMaturityDateStr() {
		String tempStr;
		if(postTransactionMaturityDate!=null) {
			tempStr = String.format("%02d/%02d/%4d",
					postTransactionMaturityDate.get(Calendar.MONTH) + 1,
					postTransactionMaturityDate.get(Calendar.DAY_OF_MONTH),
					postTransactionMaturityDate.get(Calendar.YEAR));
			return tempStr;
		}
		return "N/A";
	}
	public String toString() {
		String str;
		if(successIndicatorFlag) {
			if(t.getTransactionType().equals("Balance Inquiry")) { 
				// For balance inquiries
				str = t.toString() +  "\nCurrent Balance: $" + preTransactionBalance + "\nAccount Type: " + accountType;
				return str; 
			}else if(t.getTransactionType().equals("New Account")) {
				// For new account
				if(accountType.equals("CD")) {
					str = t.toString() + "\nMaturity Date: " + t.getDates() + "\nOpening Balance: $" + 
							preTransactionBalance + "\nAccount Type: " + accountType + "\nAccount Successfully Opened!"; 
					return str;
				}
				str = t.toString() + "\nOpening Balance: $" + preTransactionBalance + "\nAccount Type: " + accountType +
						"\nAccount Successfully Opened!"; 
				return str;
			}else if(t.getTransactionType().equals("Delete Account")) {
				// For delete acct
				str = t.toString() + "\nCurrent Balance: $" + preTransactionBalance + "\nAccount Type: " + accountType +
						"\nAccount Deleted Successfully!"; 
				return str;
			}else if(t.getTransactionType().equals("Clear Check")) {
				// For Clear Check 
				str = t.toString() + "\nCurrent Balance: $" + preTransactionBalance + "\nNew Balance: $" + postTransactionBalance +
						"\nAccount Type: " + accountType + "\nCheck Successfully Cleared!";
				return str;
			}else if(t.getTransactionType().equals("Close Account")){
				str = t.toString() + "\nCurrent Balance: $" + preTransactionBalance + "\nAccount Type: " + accountType +
						"\nAccount Successfully Closed!"; 
				return str;
			} else if(t.getTransactionType().equals("reopen Account")) {
				str = t.toString() + "\nCurrent Balance: $" + preTransactionBalance + "\nAccount Type: " + accountType +
						"\nAccount Successfully Re-opened!"; 
				return str;
			}else{
				// For every other transaction
				str = t.toString() + "\nCurrent Balance: $" + preTransactionBalance + "\nAccount Type: " + accountType +
						"\nNew Balance: $" + postTransactionBalance; 
				return str;
			}
		}
		if(accountType.length()>0) {
			return t.toString() + "\nCurrent Balance: $" + preTransactionBalance + "\nAccount Type: " + accountType +
					"\n" + reasonForFailure;
		}
		return t.toString() + "\n" + reasonForFailure; //String for failure
	}
	
}


