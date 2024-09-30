import java.util.Calendar;

public class SavingsAccount extends Account {
  
	public SavingsAccount() {
		super();
	}
	public SavingsAccount(Depositor depositor, int requestedAccount, double balance, String accountType) {
		super(depositor, requestedAccount, balance, accountType);
	}
	public SavingsAccount(Depositor d, int accountNum, double bal, String accType, int term, Calendar matDate) {
		super(d, accountNum, bal, accType, term, matDate);
	}
	public SavingsAccount(Depositor d, int accountNum, double bal, String accType, String accStatus, String matDate) {
		super(d, accountNum, bal, accType, accStatus , matDate);
	}
	public SavingsAccount(Depositor d, int accountNum, double bal, String accType, String matDate) {
		super(d, accountNum, bal, accType, matDate);
	}
	public SavingsAccount(Depositor d, int accountNum, double bal, String accType, int term) {
		super(d, accountNum, bal, accType, term);
	}
	public SavingsAccount(Depositor d, int accountNum, double bal, String accType, Calendar matDate) {
		super(d, accountNum, bal, accType, matDate);
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
	        }else {
	        	System.out.println("This is the SAVING CLASS");
	        	System.out.println(balance);
	        	balance += amount;
	        	System.out.println(balance);
	        	postBal = balance;
	        	TransactionReceipt receipt = new TransactionReceipt(t, true, "", accountType, preBal, postBal, maturityDate);
	        	addTransaction(receipt);
				return receipt;
	        }
	    }else {
	    	String fail = "Error: Account does not exist!";
	        TransactionReceipt receipt = new TransactionReceipt(t, false, fail, accountType, 0, 0, 
	        		null); 
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
				return receipt;			
	        }
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
	    } 
	        String fail = "Error: Account does not exist!";
	        TransactionReceipt receipt = new TransactionReceipt(t, false, fail, accountType, 0, 0, maturityDate); //Failed: displays original matDate
	        addTransaction(receipt);
			return receipt;
	}
	
}
