import java.util.ArrayList;
import java.util.Calendar;

public class Bank {
	private ArrayList<Account> account;
	private static double totalAmountInSavingsAccts; 
	private static double totalAmountInCheckingAccts; 
	private static double totalAmountInCDAccts;
	private static double totalAmountInAllAccts;
	
	public Bank() {
		account = new ArrayList<Account>();
	}
	
	// Method to open a new account
	public TransactionReceipt openNewAcct(Account a) {    
		TransactionTicket t = new TransactionTicket(a.getAccountNumber(), "New Account", 
         		0, a.getMaturityDateString());
	    int accNum = a.getAccountNumber();
	    int index = findAcct(accNum);
	    
	    if (index == -1) {
	    	account.add(a);
	        totalAmountInAllAccts += a.getBalance();
	        if (a.getAccountType().equals("Savings")) {
	            addToTotalAmountInSavingsAccts(a.getBalance());
	        } else if (a.getAccountType().equals("Checking")) {
	            addToTotalAmountInCheckingAccts(a.getBalance());
	        } else if (a.getAccountType().equals("CD")) {
	            addToTotalAmountInCDAccts(a.getBalance());
	        }
	        TransactionReceipt receipt = new TransactionReceipt(t, true, "", a.getAccountType(), 
	        		account.get(account.size()-1).getBalance(), account.get(account.size()-1).getBalance(),  
	        		account.get(account.size()-1).getMaturityDate()); //account.size()-1
	        a.addTransaction(receipt);
	        return receipt;
	    } else {
	        TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account already exists!", a.getAccountType(), 
	                a.getBalance(), a.getBalance(),  account.get(index).getMaturityDate());
	        a.addTransaction(receipt);
	        return receipt;
	    }
	}
	// Method to get the balance of an account
	public TransactionReceipt getBalance(TransactionTicket t) { 
		int accNum = t.getAcctNum();
		int index = findAcct(accNum);
		if (index != -1) {
			return account.get(index).getBalance(t);
		}
		TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account number " + t.getAcctNum() + 
				" does not exist!", "", 0, 0, null);
		return receipt;
		
	}
	// Method to make a deposit
	public TransactionReceipt makeDeposit(TransactionTicket t) {  
		int accNum = t.getAcctNum();
		int index = findAcct(accNum);
		if(index != -1) {
			TransactionReceipt receipt = account.get(index).makeDeposit(t);
			if (receipt.getTransactionSuccessIndicatorFlag()) {
	            double depositAmount = t.getTransactionAmount();
	            if (account.get(index).getAccountType().equals("Savings")) {
	                addToTotalAmountInSavingsAccts(depositAmount);
	            } else if (account.get(index).getAccountType().equals("Checking")) {
	                addToTotalAmountInCheckingAccts(depositAmount);
	            } else if (account.get(index).getAccountType().equals("CD")) {
	                addToTotalAmountInCDAccts(depositAmount);
	            }
	            totalAmountInAllAccts += depositAmount;
	            System.out.println("Total in all accounts: $" + totalAmountInAllAccts);
	        }
			return receipt;
		}
		return new TransactionReceipt(t, false, "Error: Account number " + t.getAcctNum() + 
				" does not exist!", "", 0, 0, null);
	}
	// Method to make a withdrawal
	public TransactionReceipt makeWithdrawal(TransactionTicket t) { 
		int accNum = t.getAcctNum();
		int index = findAcct(accNum);
		if(index != -1) {
			TransactionReceipt receipt = account.get(index).makeWithdrawal(t);
			if (receipt.getTransactionSuccessIndicatorFlag()) {
	            double withdrawalAmount = t.getTransactionAmount();
	            if (account.get(index).getAccountType().equals("Savings")) {
	                subtractFromTotalAmountInSavingsAccts(withdrawalAmount);
	            } else if (account.get(index).getAccountType().equals("Checking")) {
	                subtractFromTotalAmountInCheckingAccts(withdrawalAmount);
	            } else if (account.get(index).getAccountType().equals("CD")) {
	                subtractFromTotalAmountInCDAccts(withdrawalAmount);
	            }
	            totalAmountInAllAccts -= withdrawalAmount;
	            System.out.println("Total in all accounts: $" + totalAmountInAllAccts);
	        }
			return receipt;
		}
		return new TransactionReceipt(t, false, "Error: Account number " + t.getAcctNum() + 
				" does not exist!", "", 0, 0, null);
	}
	// Method to clear a check
	public TransactionReceipt clearCheck(Check c) {
		int accNum = c.getAccountNumber();
		int index = findAcct(accNum);
		if(index != -1) {
			TransactionReceipt receipt = account.get(index).clearCheck(c);
			 if (receipt.getTransactionSuccessIndicatorFlag()) {
		        double checkAmount = c.getCheckAmount();
		        if (account.get(index).getAccountType().equals("Checking")) {
		             subtractFromTotalAmountInCheckingAccts(checkAmount);
		          }
		        totalAmountInAllAccts -= checkAmount;
		        System.out.println("Total in all accounts: $" + totalAmountInAllAccts);
		     }
			return receipt;
		}
		return null;
	}
	// Method to delete an account
	public TransactionReceipt deleteAcct(TransactionTicket t) {
	    int accNum = t.getAcctNum();
	    int index = findAcct(accNum);
	    
	    if (index != -1) {
	        TransactionReceipt a = account.get(index).getBalance(t);
	        if (a!= null && a.getPostTransactionBalance() != 0) {
	            TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account balance not cleared!",
	            		account.get(index).getAccountType(), a.getPreTransactionBalance(), 
	                    a.getPostTransactionBalance(), account.get(index).getMaturityDate());
	            account.get(index).addTransaction(receipt);
				return receipt;
	        } else {
	            String acctType = account.get(index).getAccountType(); //gets old acctType before deletion
	            account.remove(index);
	            TransactionReceipt receipt = new TransactionReceipt(t, true, "", acctType,
	                       a.getPreTransactionBalance(), a.getPostTransactionBalance(), account.get(index).getMaturityDate());
	            account.get(index).addTransaction(receipt);
	    		return receipt;
	            
	        }
	    }
	    
	    TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account not found!", "", 0, 0, null);
		return receipt;
	}
	public TransactionReceipt closeAcct(TransactionTicket t) {
		int index = findAcct(t.getAcctNum());
		if(index != -1) {
			return account.get(index).closeAcct(t);
		}
		return new TransactionReceipt(t, false, "Error: Account does not exist!", "", 0,0,null);
	}
	public TransactionReceipt reopenAcct(TransactionTicket t) {
		int index = findAcct(t.getAcctNum());
		if(index != -1) {
			return account.get(index).reopenAcct(t);
		}
		return new TransactionReceipt(t, false, "Error: Account does not exist!", "", 0,0,null);
	}

	// Method to find an account by account number
	private int findAcct(int reqNum) {
	    for (int index = 0; index < account.size(); index++) {
	        Account currentAccount = account.get(index);
	        if (currentAccount != null && currentAccount.getAccountNumber() == reqNum) {
	            return index;
	        }
	    }
	    return -1;
	}
	// Method to get an account object by account num
	public Account getAcct(int accNumber) {
		int index = findAcct(accNumber);

        if (index != -1) {
            return new Account(account.get(index)); // Returns new account copy
        } else {
        	return null;
        }
	}
	public ArrayList<Account> getAccount() {
	    ArrayList<Account> cloneCopy = new ArrayList<>();
	    for (Account acc : account) {
	        cloneCopy.add(new Account(acc));
	    }
	    return cloneCopy; // Returns clone copy of account objects
	}
	public String getCurrentDate() {
	    Calendar currentDate = Calendar.getInstance();
	    return String.format("%02d/%02d/%4d",
	            currentDate.get(Calendar.MONTH) + 1,
	            currentDate.get(Calendar.DAY_OF_MONTH),
	            currentDate.get(Calendar.YEAR));
	}
	public Account getAcctWithSSN(String ssn) {
	    Account cloneCopy = null; 
	    for (int index = 0; index < account.size(); index++) {
	        String current = account.get(index).getDepositor().getSSN();
	        if (current != null && current.equalsIgnoreCase(ssn)) {
	            cloneCopy = new Account(account.get(index)); // Create a new Account object using the copy constructor
	            break; 
	        }
	    }
	    return cloneCopy; 
	}
	private static void addToTotalAmountInSavingsAccts(double amount) {
        totalAmountInSavingsAccts += amount;
    }
    private static void subtractFromTotalAmountInSavingsAccts(double amount) {
        totalAmountInSavingsAccts -= amount;
    }
    public static double getTotalAmountInSavingsAccts() {
        return totalAmountInSavingsAccts;
    }
    private static void addToTotalAmountInCheckingAccts(double amount) {
        totalAmountInCheckingAccts += amount;
    }
    private static void subtractFromTotalAmountInCheckingAccts(double amount) {
        totalAmountInCheckingAccts -= amount;
    }
    public static double getTotalAmountInCheckingAccts() {
        return totalAmountInCheckingAccts;
    }
    private static void addToTotalAmountInCDAccts(double amount) {
        totalAmountInCDAccts += amount;
    }
    private static void subtractFromTotalAmountInCDAccts(double amount) {
        totalAmountInCDAccts -= amount;
    }
    public static double getTotalAmountInCDAccts() {
        return totalAmountInCDAccts;
    }
    public static double getTotalAmountInAllAccts() {
        return totalAmountInAllAccts;
    }
    public static String printAccountDatabase(ArrayList<Account> accounts) {
        String str = "";
        for (Account acc : accounts) {
            str += acc.toString() + "\n";
        }
        str += "Total Amount in Savings Accounts: $" + totalAmountInSavingsAccts + 
                "\nTotal Amount in Checking Accounts: $" + totalAmountInCheckingAccts + 
                "\nTotal Amount in CD Accounts: $" + totalAmountInCDAccts +
                "\nTotal Amount in All Accounts: $" + totalAmountInAllAccts;
        return str;
    }
	
}
