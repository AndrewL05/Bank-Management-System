import java.util.Calendar;

public class CheckingAccount extends Account{
	
	public CheckingAccount() {
		super();
	}
	public CheckingAccount(Depositor depositor, int requestedAccount, double balance, String status, String accountType) {
		super(depositor, requestedAccount, balance, status, accountType);
	}
	public CheckingAccount(Depositor depositor, int requestedAccount, double balance, String accountType) {
		super(depositor, requestedAccount, balance, accountType);
		System.out.println("LKSLAKMLML THIIS IS CHECKINGS");
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
		        	System.out.println("This is the Checking CLASS");
		        	balance += amount;
		        	postBal = balance;
		        	TransactionReceipt receipt =  new TransactionReceipt(t, true, "", accountType, preBal, postBal, maturityDate);
		        	addTransaction(receipt);
					return receipt;
		        }
		    }else { 
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
				return receipt;			
	        }if(t.getTransactionAmount()>0 && !(t.getTransactionAmount()>balance) && !accountType.equals("CD")) {
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
}
