import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Calendar;

public class Bank {
	private RandomAccessFile accounts;
	private int numOfActiveAccounts;
	private static double totalAmountInSavingsAccts; 
	private static double totalAmountInCheckingAccts; 
	private static double totalAmountInCDAccts;
	private static double totalAmountInAllAccts;
	
	public Bank() throws FileNotFoundException {
		accounts = new RandomAccessFile("BankAccounts.dat", "rw");
		numOfActiveAccounts = 0;
	}
	public Account readAcctAt(int index) throws IOException {
		Account account;
		accounts.seek((index)*(114*2));
		char[] charArr = new char[114];
		String ref = "", last = "", first = "", ssn = "", accType = "", accStatus = "", maturityD = "";
		int accNum = 0;
		double bal = 0.0;
		
				for(int i=0; i<114;i++) {
					charArr[i] = accounts.readChar();
				}
				ref = new String(charArr);
				last = ref.substring(0, 15).trim();
				first = ref.substring(15,30).trim();
				ssn = ref.substring(30,45).trim();
				accNum = Integer.parseInt(ref.substring(45,60).trim());
				accStatus = ref.substring(60,75).trim();
				accType = ref.substring(75,90).trim();
				bal = Double.parseDouble(ref.substring(90,97).trim());
				maturityD = ref.substring(97,114).trim();

		switch(accType) {
		case "CD":
			Depositor dep = new Depositor(new Name(first, last), ssn);
			account = new CDAccount(dep, accNum, bal, accType, accStatus, maturityD);
			break;
		case "Savings":
			Depositor d = new Depositor(new Name(first, last), ssn);
			account = new SavingsAccount(d, accNum, bal, accType, accStatus);
			break;
		default:
			Depositor dd = new Depositor(new Name(first, last), ssn);
			account = new CheckingAccount(dd, accNum, bal, accStatus, accType);
			break;
		}
		return account;
	}
	public int findAcctAt(int index) throws IOException {
		accounts.seek(index*(114*2));
		return readAcctAt(index).getAccountNumber();
	}
	public Account getAccount(int acctNum) throws IOException {
        String acctType = readAcctAt(findAcct(acctNum)).getAccountType();

        switch (acctType) {
            case "CD":
                return getCDAccount(acctNum);
            case "Checking":
                return getCheckingAccount(acctNum);
            case "Savings":
                return getSavingsAccount(acctNum);
            default:
                throw new IllegalArgumentException("Unknown account type: " + acctType);
        }
    }
	
    public SavingsAccount getSavingsAccount(int acctNum) throws IOException {
        return (SavingsAccount) readAcctAt(findAcct(acctNum));
    }

    public CheckingAccount getCheckingAccount(int acctNum) throws IOException {
        return (CheckingAccount) readAcctAt(findAcct(acctNum));
    }

    public CDAccount getCDAccount(int acctNum) throws IOException {
        return (CDAccount) readAcctAt(findAcct(acctNum));
    }
	//method to open a new account
	public TransactionReceipt openNewAcct(Account a) throws IOException {    
		TransactionTicket t = new TransactionTicket(a.getAccountNumber(), "New Account", 
         		0, a.getMaturityDateString());
	    int accNum = a.getAccountNumber();
	    int index = findAcct(accNum);
		    if (index != -1) {
		    	TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account already exists!", a.getAccountType(), 
		                a.getBalance(), a.getBalance(),  a.getMaturityDate());
		        a.addTransaction(receipt);
		        return receipt;
		        }
		    accounts.setLength((numOfActiveAccounts + 1) * (114 * 2));
	        accounts.seek(accounts.length() - (114 * 2));
	        for (int i = 0; i < 114; i+= 114) {
	            accounts.writeChars(a.toString());
	        }
	        numOfActiveAccounts++;
	        totalAmountInAllAccts += a.getBalance();
	        if (a.getAccountType().equals("Savings")) {
	            addToTotalAmountInSavingsAccts(a.getBalance());
	        } else if (a.getAccountType().equals("Checking")) {
	            addToTotalAmountInCheckingAccts(a.getBalance());
	        } else if (a.getAccountType().equals("CD")) {
	            addToTotalAmountInCDAccts(a.getBalance());
	        }
	        TransactionReceipt receipt = new TransactionReceipt(t, true, "", a.getAccountType(), 
	        		a.getBalance(), a.getBalance(),  
	        		a.getMaturityDate()); 
	        a.addTransaction(receipt);
	        return receipt;
	    } 
	
	//method to get the balance of an account
	public TransactionReceipt getBalance(TransactionTicket t) throws IOException {
		TransactionReceipt receipt;
		int index = findAcct(t.getAcctNum());
		Account account;
		try {
			if (index == -1) { //invalid account
				 throw new InvalidAccountException();
			}
			else {	//valid account
				account = readAcctAt(index);
				receipt = account.getBalance(t);
				accounts.seek(index * (114 * 2));
				for (int i = 0; i < 114; i += 107) {
					accounts.writeChars(account.toString());
				}
			}
		}
		catch (InvalidAccountException e) {
			account = readAcctAt(index);
			receipt = new TransactionReceipt(t, false, e.getMessage(),  account.getAccountType(), account.getBalance(), account.getBalance(), account.getMaturityDate());
		}
		return new TransactionReceipt(receipt);
	}
  
	//method to make a deposit
	public TransactionReceipt makeDeposit(TransactionTicket t) throws IOException, AccountClosedException, InvalidAmountException, CDMaturityDateException {  
		TransactionReceipt receipt;
		int index = findAcct(t.getAcctNum());
		Account account;
		try {
			if (index == -1) {	//invalid account
				throw new InvalidAccountException(t.getAcctNum());
			}
			else {	//valid account
				account = readAcctAt(index);
				receipt = account.makeDeposit(t);
				accounts.seek(index * (174 * 2));
				for (int i = 0; i < 174; i += 174) {
					accounts.writeChars(account.toString());
				}
			}
		}
		catch (InvalidAccountException e) {
			if (e.getMessage().equals("Error: Account number " + t.getAcctNum() + " does not exist.")) {
				receipt = new TransactionReceipt(t, false, e.getMessage());
			}
			else {
				account = readAcctAt(index);
				receipt = new TransactionReceipt(t, true, e.getMessage(), account.getAccountType(), account.getBalance(), account.getBalance(),
				account.getMaturityDate());
				//account.addTransaction(new TransactionReceipt(receipt));
				accounts.seek(index * (174 * 2));
				for (int i = 0; i < 174; i += 174) {
					accounts.writeChars(account.toString());
				}
			}
			
		}
		return new TransactionReceipt(receipt);
	}
	// Method to make a withdrawal
	public TransactionReceipt makeWithdrawal(TransactionTicket t) throws IOException, AccountClosedException, InvalidAmountException, InsufficientFundsException, CDMaturityDateException {
		TransactionReceipt receipt;
		int index = findAcct(t.getAcctNum());
		Account account;
		try {
			if (index == -1) {	//invalid account
				throw new InvalidAccountException(t.getAcctNum());
			}
			else {	//valid account
				account = readAcctAt(index);
				receipt = account.makeWithdrawal(new TransactionTicket(t));
				accounts.seek(index * (174 * 2));
				for (int i = 0; i < 174; i += 174) {
					accounts.writeChars(account.toString());
				}
			}
		}
		catch (InvalidAccountException e) {
			if (e.getMessage().equals("Error: Account number " + t.getAcctNum() + " does not exist.")) {
				receipt = new TransactionReceipt(t, false, e.getMessage());
			}
			else {
				account = readAcctAt(index);
				receipt = new TransactionReceipt(t, true, e.getMessage(), account.getAccountType(), account.getBalance(), account.getBalance(),
						account.getMaturityDate());
				accounts.seek(index * (174 * 2));
				for (int i = 0; i < 174; i += 174) {
					accounts.writeChars(account.toString());
				}
			}
		}
		return new TransactionReceipt(receipt);
	}
	//method to clear a check
	public TransactionReceipt clearCheck(Check check, TransactionTicket ticket) throws IOException {
		TransactionReceipt receipt;
		int index = findAcct(ticket.getAcctNum());
		Account account;
		try {
			if (index == -1) {	//invalid account
				throw new InvalidAccountException(ticket.getAcctNum());
			}
			else {	//valid account
				account = readAcctAt(index);
				receipt = account.clearCheck(check);
				accounts.seek(index * (174 * 2));
				for (int i = 0; i < 174; i += 174) {
					accounts.writeChars(account.toString());
				}
			}
		}
		catch (InvalidAccountException | AccountClosedException | InvalidAmountException
		| CheckTooOldException | PostDatedCheckException e) {
			if (e.getMessage().equals("Error: Account number " + ticket.getAcctNum() + " does not exist.")) {
				receipt = new TransactionReceipt(ticket, false, e.getMessage());
			}
			else {
				account = readAcctAt(index);
				receipt = new TransactionReceipt(t, true, e.getMessage(), account.getAccountType(), account.getBalance(), account.getBalance(),
						account.getMaturityDate());
				accounts.seek(index * (174 * 2));
				for (int i = 0; i < 174; i += 174) {
					accounts.writeChars(account.toString());
				}
			}
		}
		catch (InsufficientFundsException e) {
			account = readAcctAt(index);
			Account checking = new CheckingAccount(account.getDepositor(), account.getBalance(),
			account.getBalance() - 2.50, account.getTransactionHistory());
			receipt = new TransactionReceipt(ticket, e.getMessage(), checking.getAcctBalance() + 2.50, checking.getAcctBalance());
			checking.addTransaction(new TransactionReceipt(receipt));
			Bank.subtractFromAllTotal(2.50);
			Bank.subtractFromCheckingTotal(2.50);
			accounts.seek(index * (174 * 2));
			for (int i = 0; i < 174; i += 174) {
				accounts.writeChars(checking.getAcctString());
			}
		}
		return new TransactionReceipt(receipt);
	}
	//method to delete an account
	public TransactionReceipt deleteAcct(TransactionTicket t) {
	    int accNum = t.getAcctNum();
	    int index = findAcct(accNum);
	    
	    if (index != -1) {
	        TransactionReceipt a = account[index].getBalance(t);
	        if (a!= null && a.getPostTransactionBalance() != 0) {
	            TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account balance not cleared!",
	            		account[index].getAccountType(), a.getPreTransactionBalance(), 
	                    a.getPostTransactionBalance(), account[index].getMaturityDate());
	            account[index].addTransaction(receipt);
				return receipt;
	        } else {
	        	int gap = index;
	            String acctType = account[index].getAccountType(); //gets old acctType before deletion
	            if (gap >= 0 && gap < numOfActiveAccounts) {
	                //swap the last element with the element at the gap
	                Account temp = account[numOfActiveAccounts - 1];
	                account[numOfActiveAccounts - 1] = account[gap];
	                account[gap] = temp;
	                //set the last element to null and decrement the count
	                account[numOfActiveAccounts - 1] = null;
	                numOfActiveAccounts--;
	            }
	            TransactionReceipt receipt = new TransactionReceipt(t, true, "", acctType,
	                       a.getPreTransactionBalance(), a.getPostTransactionBalance(), account[index].getMaturityDate());
	            account[index].addTransaction(receipt);
	    		return receipt;
	            
	        }
	    }
	    
	    TransactionReceipt receipt = new TransactionReceipt(t, false, "Error: Account not found!", "", 0, 0, null);
		return receipt;
	    
	}
  //method to close account
	public TransactionReceipt closeAcct(TransactionTicket t) {
		int index = findAcct(t.getAcctNum());
		if(index != -1) {
			return account[index].closeAcct(t);
		}
		return new TransactionReceipt(t, false, "Error: Account does not exist!", "", 0,0,null);
	}
	public TransactionReceipt reopenAcct(TransactionTicket t) {
		int index = findAcct(t.getAcctNum());
		if(index != -1) {
			return account[index].reopenAcct(t);
		}
		return new TransactionReceipt(t, false, "Error: Account does not exist!", "", 0,0,null);
	}

	//method to find an account by account number
	private int findAcct(int reqNum) throws IOException {
		int index = -1;
		for (int i = 0; i < numOfActiveAccounts; i++) {
			if (readAcctAt(i).getAccountNumber() == reqNum) {
				index = i;
				break;
			}
		}
		return index;
	}
	//method to get an account object by account num
	public Account getAcct(int accNumber) {
		int index = findAcct(accNumber);

        if (index != -1) {
            return new Account(account[index]); // Returns new account copy
        } else {
        	return null;
        }
	}
	public Account[] getAccounts() {
	    Account[] cloneCopy = new Account[account.length];
	    for (int i = 0; i < numOfActiveAccounts; i++) {
	        cloneCopy[i] = new Account(account[i]);
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
	    for (int index = 0; index < numOfActiveAccounts; index++) {
	        String current = account[index].getDepositor().getSSN();
	        if (current != null && current.equalsIgnoreCase(ssn)) {
	            cloneCopy = new Account(account[index]); // Create a new Account object using the copy constructor
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
    public int getNumOfActiveAccount() {
    	return numOfActiveAccounts;
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
