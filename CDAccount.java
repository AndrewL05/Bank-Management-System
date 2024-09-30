import java.util.Calendar;

public class CDAccount extends SavingsAccount{
	private Calendar maturityDate;
	
	public CDAccount(){
		super();
	}
	public CDAccount(Depositor depositor, int requestedAccount, double balance, String accountType, String status, String matDate) {
		super(depositor, requestedAccount, balance, accountType, status, matDate);
		maturityDate = super.getMaturityDate();
	}
	public CDAccount(Depositor depositor, int requestedAccount, double balance, String accountType, String matDate) {
	    super(depositor, requestedAccount, balance, accountType, matDate);
	}

	public CDAccount(Depositor d, int accountNum, double bal, String accType, int term, Calendar matDate) {
		super(d, accountNum, bal, accType, term, matDate);
		maturityDate = matDate;
		
	}
	public CDAccount(Depositor d, int accountNum, double bal, String accType, Calendar matDate) {
		super(d, accountNum, bal, accType, matDate);
		maturityDate = matDate;
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
	        if(t.getTransactionAmount()<=0) { //handles negative deposits 
	        	TransactionReceipt receipt =  new TransactionReceipt(t, false, String.format("Error: $%.2f is an invalid amount", amount), accountType,
	        		    balance, balance, maturityDate);
	        	addTransaction(receipt);
				return receipt;
	        }
	        if (accountType.equals("CD") && this.maturityDate != null && (this.maturityDate.before(Calendar.getInstance()) 
	        		|| this.maturityDate.equals(Calendar.getInstance()))) {
	        	System.out.println("CD account entered");
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
	        if(t.getTransactionAmount()<=0){ 
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
	        if (accountType.equals("CD") && maturityDate != null && (maturityDate.before(Calendar.getInstance()) 
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

	
}
