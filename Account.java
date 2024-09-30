import java.util.ArrayList;
import java.util.Calendar;

public class Account{
	protected Depositor dd;
	protected int accNum;
	protected double balance;
	protected String accountType;
	protected String accountStatus;
	private ArrayList<TransactionReceipt> tr;
	protected Calendar maturityDate;
	protected Calendar newMaturityDate;
	
	//Depositor depositor, int requestedAccount, double balance, String accountType, String matDate
	public Account() {
		tr = new ArrayList<>();
		maturityDate = Calendar.getInstance();
		maturityDate.clear();
		dd = new Depositor();
		accNum=0;
		balance=0;
		accountType="";
		accountStatus = "Open";
		newMaturityDate = (Calendar) maturityDate.clone();
	}
	public Account(Account a) { // Copy Constructor
		dd = new Depositor(a.dd);
		accNum = a.accNum;
		balance = a.balance;
		accountType = a.accountType;
		accountStatus = a.accountStatus;
		tr = a.tr;
		maturityDate = a.maturityDate;
		newMaturityDate = a.newMaturityDate;
	}
	public Account(Depositor d, int accountNum, double bal, String accType, String accStatus, String matDate) {
		tr = new ArrayList<>();
		dd=d;
		accNum=accountNum;
		balance=bal;
		accountType=accType;
		accountStatus = accStatus;
		maturityDate=Calendar.getInstance();
		maturityDate.clear();
		if(matDate.indexOf("/")==-1) {
			matDate = "";
		}
		String[] dateArr = matDate.split("/");
		if(matDate.isEmpty()) {
			
		}else {
			maturityDate.set(Integer.parseInt(dateArr[2]),
					Integer.parseInt(dateArr[0]) - 1,
					Integer.parseInt(dateArr[1]));
			newMaturityDate = (Calendar) maturityDate.clone();
		}
	}
	public Account(Depositor d, int accountNum, double bal, String accType, String matDate){
		tr = new ArrayList<>();
		dd=d;
		accNum=accountNum;
		balance=bal;
		accountType=accType;
		accountStatus = "Open";
		maturityDate=Calendar.getInstance();
		maturityDate.clear();
		if(matDate.indexOf("/")==-1) {
			matDate = "";
		}
		String[] dateArr = matDate.split("/");
		if(matDate.isEmpty()) {
			
		}else {
			maturityDate.set(Integer.parseInt(dateArr[2]),
					Integer.parseInt(dateArr[0]) - 1,
					Integer.parseInt(dateArr[1]));
			newMaturityDate = (Calendar) maturityDate.clone();
		}
	}
	public Account(Depositor d, int accountNum, double bal, String accType, int term){
		tr = new ArrayList<>();
		dd=d;
		accNum=accountNum;
		balance=bal;
		accountType=accType;
		accountStatus = "Open";
		// Set maturityDate to the current date
	    maturityDate = Calendar.getInstance();
	    // Sets to current
	    maturityDate.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
	    // Add the specified term to the maturityDate
	    maturityDate.add(Calendar.MONTH, term);
	    newMaturityDate = (Calendar) maturityDate.clone();
	}
	public Account(Depositor d, int accountNum, double bal, String accType)  {
		tr = new ArrayList<>();
		dd=d;
		accNum=accountNum;
		balance=bal;
		accountType=accType;
		accountStatus = "Open";
	}
	public Account(Depositor d, int accountNum, double bal, String accType, int term, Calendar matDate) {
		tr = new ArrayList<>();
		dd=d;
		accNum=accountNum;
		balance=bal;
		accountType=accType;
		accountStatus = "Open";
	    maturityDate = matDate;
	    //maturityDate.add(Calendar.MONTH, term);
	    newMaturityDate = (Calendar) maturityDate.clone();
	    newMaturityDate.add(Calendar.MONTH, term);
	}
	public Account(Depositor d, int accountNum, double bal, String accType, Calendar matDate) {
		dd=d;
		accNum = accountNum;
		balance = bal;
		accountType = accType;
		accountStatus = "Open";
	    maturityDate = matDate;
	    newMaturityDate = (Calendar) maturityDate.clone();
	}
	
	public int getAccountNumber() {
		return accNum;
	}
	public TransactionReceipt getBalance(TransactionTicket t){
		if(accNum == t.getAcctNum()) {
			double preBal = balance;
			double postBal = balance;
			TransactionReceipt receipt = new TransactionReceipt(t, true, "", accountType, preBal, postBal, maturityDate);
			addTransaction(receipt);
			return receipt;
		}else {
			String fail = "Error: Account not found!";
			TransactionReceipt receipt = new TransactionReceipt(t, false, fail, accountType, 0, 0, maturityDate);
			addTransaction(receipt);
			return receipt;
		}
		
	}
	
	public TransactionReceipt makeDeposit(TransactionTicket t) {
	    if (accNum == t.getAcctNum()) {
	        double amount = t.getTransactionAmount();
	        double preBal = balance;
	        double postBal;
	        if(accountStatus.equals("Closed")){
				TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account is closed", accountType, balance, balance,
						maturityDate);
				addTransaction(receipt);
				return receipt;
			}
	        if(t.getTransactionAmount()>0 && !accountType.equals("CD")) {
	        	balance += amount;
	        	postBal = balance;
	        	TransactionReceipt receipt =  new TransactionReceipt(t, true, "", accountType, preBal, postBal, maturityDate);
	        	addTransaction(receipt);
				return receipt;
	        }else if(t.getTransactionAmount()<=0) { //handles negative deposits 
	        	TransactionReceipt receipt =  new TransactionReceipt(t, false, String.format("Error: $%.2f is an invalid amount", amount), accountType,
	        		    balance, balance, maturityDate);
	        	addTransaction(receipt);
				return receipt;
	        }
	        if (accountType.equals("CD") && (maturityDate.before(Calendar.getInstance()) 
	        		|| maturityDate.equals(Calendar.getInstance()))) {
	        	balance += amount;
	        	postBal = balance;
	            setMaturityDate(t.getTermOfCD()); // Update maturity date based on the selected term
	            TransactionReceipt receipt = new TransactionReceipt(t, true, "", accountType, preBal, postBal, newMaturityDate);
	            addTransaction(receipt);
				return receipt;
	        }else {
	        	postBal = balance;
	        	TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Maturity Date not reached -- " + 
	        			getMaturityDateString(), accountType, preBal, postBal, maturityDate);
	        	addTransaction(receipt);
				return receipt;
	        }
	    } else {
	        String fail = "Error: Account does not exist!";
	        TransactionReceipt receipt = new TransactionReceipt(t, false, fail, accountType, 0, 0, 
	        		null); //Failed: displays original matDate
	        addTransaction(receipt);
			return receipt;
	    }
	}

	public TransactionReceipt makeWithdrawal(TransactionTicket t) {
	    if (accNum == t.getAcctNum()) {
	        double withdrawalAmount = t.getTransactionAmount();
	        double preBal = balance;
	        double postBal;
	        if(accountStatus.equals("Closed")){
	        	TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account is closed", accountType, balance, balance,
						maturityDate);
	        	addTransaction(receipt);
				return receipt;			}
	        if(t.getTransactionAmount()>0 && !(t.getTransactionAmount()>balance) && !accountType.equals("CD")) {
	        	balance -= withdrawalAmount;
	        	postBal = balance;
	        	TransactionReceipt receipt = new TransactionReceipt(t, true, "", accountType, preBal, postBal, maturityDate);
	        	addTransaction(receipt);
				return receipt;	
	        }else if(t.getTransactionAmount()<=0){ 
	        	TransactionReceipt receipt = new TransactionReceipt(t, false, String.format(("Error: $%.2f is an invalid amount"), withdrawalAmount), 
	        			accountType, balance, balance, maturityDate);
	        	addTransaction(receipt);
				return receipt;	
	        }else if(t.getTransactionAmount()>balance) {
	        	 TransactionReceipt receipt = new TransactionReceipt(t, false, String.format("Error: Insufficient funds - Transaction voided", 
							t.getTransactionAmount()), getAccountType(), getBalance(), 
							getBalance(),getMaturityDate());
	        	 addTransaction(receipt);
	        	 return receipt;
	        }
	        //Deposits and Withdrawals will be allowed only on or after the maturity date. 
	        if (accountType.equals("CD") && (maturityDate.before(Calendar.getInstance()) 
	        		|| maturityDate.equals(Calendar.getInstance()))) {
	        	balance -= withdrawalAmount;
	        	postBal = balance;
	            setMaturityDate(t.getTermOfCD()); // Update maturity date based on the selected term
	            TransactionReceipt receipt = new TransactionReceipt(t, true, "", accountType, preBal, postBal, newMaturityDate);
	            addTransaction(receipt);
				return receipt;
	        }else {
	        	postBal = balance;
	        	TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Maturity Date not reached -- " + getMaturityDateString(), accountType, preBal, postBal, 
	        			maturityDate);
	        	addTransaction(receipt);
	    		return receipt;
	        }
	    } else {
	        String fail = "Error: Account does not exist!";
	        TransactionReceipt receipt = new TransactionReceipt(t, false, fail, accountType, 0, 0, maturityDate); //Failed: displays original matDate
	        addTransaction(receipt);
			return receipt;
	    }
	}
	public TransactionReceipt clearCheck(Check c) {
	    double preAmount = balance;
	    double fee = 2.50;
	    TransactionTicket t = new TransactionTicket(accNum, "Clear Check", 
         		c.getCheckAmount(), c.getDateOfCheckStr());
	    if(accountStatus.equals("Closed")){
			TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account is closed", accountType, 
					balance, balance, maturityDate);
			addTransaction(receipt);
		    return receipt;
		}
	    if (accNum == c.getAccountNumber() && accountType.equals("Checking")) {
	        Calendar today = Calendar.getInstance();
	        
	        if(c.getCheckAmount()<=0) {
	        	TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Invalid Check Amount!", accountType, 
	        			balance, balance, maturityDate);
	        	addTransaction(receipt);
        		return receipt;
	        }
	        //Calculate the difference in months and years
	        int yearDiff = today.get(Calendar.YEAR) - c.getDateOfCheck().get(Calendar.YEAR);
	        int monthDiff = today.get(Calendar.MONTH) - c.getDateOfCheck().get(Calendar.MONTH);

	        //Checks if the check date is within the six-month limit
	        if (yearDiff < 1 || (yearDiff == 1 && monthDiff <= -6)) {
	        	if(yearDiff < 0 || (yearDiff == 0 && monthDiff >= 0)){
	        		if (preAmount >= c.getCheckAmount()) {
		                balance -= c.getCheckAmount();
		                System.out.println("PreAmount: " + preAmount);
		                System.out.println("Post balance: " + balance);
		                TransactionReceipt receipt = new TransactionReceipt(t, true, "", accountType, preAmount, balance, 
		                		maturityDate);
		                addTransaction(receipt);
		        		return receipt;
		            } else{
		                balance -= fee;
		                TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account lacks sufficient funds! $2.50 bounce fee charged.",
		                        accountType, preAmount, balance, maturityDate);
		                addTransaction(receipt);
				        return receipt;
		            }
	        	}else {
	        		TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Check has a future date" + " - " + c.getDateOfCheckStr(), 
	        				accountType, preAmount, balance, maturityDate);
	        		addTransaction(receipt);
			        return receipt;
	        	}
	            
	        } else {
	            // Check is past six months 
	            TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Check is more than six months old" + " - " + c.getDateOfCheckStr(), accountType,
	                    preAmount, balance, maturityDate);
	            addTransaction(receipt);
		        return receipt;
	        }
	    }
	    TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Not a Checking Account!", accountType,
	            preAmount, balance, maturityDate);
	    addTransaction(receipt);
        return receipt;
	}
	public TransactionReceipt closeAcct(TransactionTicket t) {
		if (accNum == t.getAcctNum()) {
			if(accountStatus.equals("Open")){
				//If still open, then set to "closed"
				setAccStatus("Closed");
				TransactionReceipt receipt = new TransactionReceipt(t, true, "", accountType,
						balance, balance, maturityDate);
				addTransaction(receipt);
		        return receipt;
			}else {
				//Checks if Account is already closed
				TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account is already closed!", "",
						0, 0, null);
				addTransaction(receipt);
		        return receipt;
			}
		}
			TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account does not exist!", "",
					0, 0, null);
			addTransaction(receipt);
	        return receipt;
		
	}
	public TransactionReceipt reopenAcct(TransactionTicket t) {
		if(accNum == t.getAcctNum()) {
			if(accountStatus.equals("Closed")) {
				setAccStatus("Open");
				TransactionReceipt receipt = new TransactionReceipt(t, true, "", accountType,
						balance, balance, maturityDate);
				addTransaction(receipt);
		        return receipt;
			}else {
				TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account is already opened", accountType,
						balance, balance, maturityDate);
				addTransaction(receipt);
		        return receipt;
			}
		}
		TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account not found!", "",
				0, 0, null);
		addTransaction(receipt);
        return receipt;
	}
	public void addTransaction(TransactionReceipt r) 
	{
		tr.add(r);
	}
	public ArrayList<TransactionReceipt> getTransactionHistory(TransactionTicket t) {
	    ArrayList<TransactionReceipt> trHistory = new ArrayList<TransactionReceipt>();

	    for (TransactionReceipt receipt : tr) {
	        if (receipt != null && receipt.getTransactionTicket() != null && 
	                receipt.getTransactionTicket().getAcctNum() == t.getAcctNum()) {
	            trHistory.add(receipt);
	        }
	    }

	    return trHistory;
	}
	public double getBalance() {
		return balance;
	}
	public String getAccountType() {
		return accountType;
	}
	public Depositor getDepositor() { // returns copy 
		Depositor copy = new Depositor(dd);
		return copy;
	}
	public Calendar getMaturityDate() {
		return maturityDate;
	}
	public String getMaturityDateString() {
		String tempStr;
		if(maturityDate!=null) {
			tempStr = String.format("%02d/%02d/%4d",
					maturityDate.get(Calendar.MONTH) + 1,
					maturityDate.get(Calendar.DAY_OF_MONTH),
					maturityDate.get(Calendar.YEAR));
			return tempStr;
		}
		return "";
	}
	public String getNewMaturityDateStr() {
		String tempStr;
		if(newMaturityDate!=null) {
			tempStr = String.format("%02d/%02d/%4d",
					newMaturityDate.get(Calendar.MONTH) + 1,
					newMaturityDate.get(Calendar.DAY_OF_MONTH),
					newMaturityDate.get(Calendar.YEAR));
			return tempStr;
		}
		return "";
	}
	
	public String getDate() {
		Calendar date = Calendar.getInstance();
		date.clear();
		String tempStr;
		tempStr = String.format("%02d/%02d/%4d",
				date.get(Calendar.MONTH) + 1,
				date.get(Calendar.DAY_OF_MONTH),
				date.get(Calendar.YEAR));
		return tempStr;
	}
	public String getCurrentDate() {
	    Calendar currentDate = Calendar.getInstance();
	    return String.format("%02d/%02d/%4d",
	            currentDate.get(Calendar.MONTH) + 1,
	            currentDate.get(Calendar.DAY_OF_MONTH),
	            currentDate.get(Calendar.YEAR));
	}
	protected void setMaturityDate(int termOfCD) {
        if (termOfCD == 6 || termOfCD == 12 || termOfCD == 18 || termOfCD == 24) {
        	newMaturityDate.add(Calendar.MONTH, termOfCD); 
        }
	}
	public String getAccStatus() {
		return accountStatus;
	}
	protected void setAccStatus(String s) {
		accountStatus = s;
	}
	public String toString() { //use to print dataBase and accountInfo receipts
		 String str = "";
		            str += String.format("%15s", getDepositor().getName().getLastName());
		            str += String.format("%15s", getDepositor().getName().getFirstName());
		            str += String.format("%15s", getDepositor().getSSN());
		            str += String.format("%15s", accNum);
		            str += String.format("%15s", accountType);
		            str += String.format("%15s", accountStatus);
		            str +=  String.format("%7.2f", getBalance());
		            
		            
		            if (accountType.equals("CD")) {
		                str += String.format("%17s",getNewMaturityDateStr());
		            } else {
		                str += "          ";
		            }
		        
		    return str;
		}
	public String InString() {
		String str = String.format("%-15s%-15s%-15s%-15s%-15s%-15s$%7.2f", getDepositor().getName().getLastName(), getDepositor().getName().getFirstName(),
				getDepositor().getSSN(), accNum, accountType, accountStatus, balance);
		return str;
	}
	public boolean equals(Account a) {
		if(getDepositor().getName().getFirstName().equals(a.getDepositor().getName().getFirstName()) && 
				getDepositor().getName().getLastName().equals(a.getDepositor().getName().getLastName())) {
			return true;
		}
		return false;
	}
}
