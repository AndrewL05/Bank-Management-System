import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.io.File;

public class Main {

	public static void main(String[] args) throws IOException
	{
		
		//Account[] account = new Account[MAX_NUM];
	    //int numAccts = bank.getNumOfActiveAccount();	//number of accounts
		int numAccts;                
		Bank bank = new Bank(); 
	    char choice;									//menu item selected
	    boolean notDone = true;						//loop control flag

	    // open input test cases file
	    File testFile = new File("myTestCases.txt");
	
	    //create Scanner object
	    Scanner kybd = new Scanner(testFile);
	    //Scanner kybd = new Scanner(System.in);

	    // open the output file
	    PrintWriter outFile = new PrintWriter("pgmOutput.txt");
	    //PrintWriter outFile = new PrintWriter(System.out);

	    /* first part */
	    /* fill and print initial database */
	    numAccts = readAccts(bank);
	    //Bank bank = new Bank(account,numAccts);
        printAccts(bank, numAccts, outFile); 
	    
	    /* second part */
	    /* prompts for a transaction and then */
	    /* call functions to process the requested transaction */
	    do {
	        menu();
	        choice = kybd.next().charAt(0);
	        switch(choice)
	        {
	            case 'q':
	            case 'Q':
	                notDone = false;
	                printAccts(bank,numAccts,outFile);
	                break;
	            case 'b':
	            case 'B':
	                balance(bank,numAccts,outFile,kybd);
	                break;
	            case 'd':
	            case 'D':
	                deposit(bank,numAccts,outFile,kybd);
	                break;
	            case 'w':
	            case 'W':
	                withdrawal(bank,numAccts,outFile,kybd);
	                break;
	            case 'n':
	            case 'N':
	                numAccts = newAcct(bank,numAccts,outFile,kybd);
	                break;
	            case 'I':
	            case 'i':
	            	accountInfo(bank, numAccts, outFile, kybd);
	            	break;
	            case 'x':
	            case 'X':
	                numAccts = deleteAcct(bank,numAccts,outFile,kybd);
	                break;
	            case 'C':
	            case 'c':
	            	clearCheck(bank, numAccts, outFile, kybd);
	            	break;
	            case 'H':
	            case 'h':
	            	accountInfoWithHistory(bank, numAccts, outFile, kybd);
	            	break;
	            case 'S':
	            case 's':
	            	closeAccount(bank, numAccts, outFile, kybd);
	            	break;
	            case 'R':
	            case 'r':
	            	reopenAccount(bank, numAccts, outFile, kybd);
	            	break;
	            default:
	                outFile.println("Error: " + choice + " is an invalid selection -  try again");
	                outFile.println();
	                outFile.flush();
	                break;
	        }
	        // give user a chance to look at output before printing menu
	        //pause(kybd);
	    } while (notDone);
	    
	    //close the output file
	    outFile.close();
	    
	    //close the test cases input file
	    kybd.close();
	    
	    System.out.println();
	    System.out.println("The program is terminating");
	}
	
	/* Method readAccts()
	 * Input:
	 *  bankAcc - reference to array of BankAccount objects
	 * Process:
	 *  Reads the initial database of first name, last name, SSN, accountNum, accountType and balance
	 * Output:
	 *  Fills in the bankAcc object arrays and returns the number of active accounts
	 */
	public static int readAccts(Bank bank) throws IOException {
	    // open database input file
	    // create File object
	    File dbFile = new File("C:\\Users\\Andrew\\Desktop\\CISC 3115 Workspace\\PrgmHW7\\src\\initAccounts.txt");
	    // create Scanner object
	    Scanner sc = new Scanner(dbFile);
	    String maturityDate;
	    int count = 0;  
	    //int maxAccts = bank.getAccount().size();  // maximum number of active accounts allowed
	    while (sc.hasNext()) {
	        String line = sc.nextLine();
	        String[] token = line.split(" ");
	        
	        String first = token[0];
	        String last = token[1];
	        String setSSN = token[2];
	        int setAccNum = Integer.parseInt(token[3]);
	        String setAccType = token[4];
	        double setBalance = Double.parseDouble(token[5]);

	        Name myName = new Name(first, last);
	        Depositor myDepositor = new Depositor(myName, setSSN);

	        // Check account type and create the corresponding account
	        Account myAccount;
	        if (setAccType.equals("CD")) {
	            // For CD accounts
	            maturityDate = token[6];
	            myAccount = new CDAccount(myDepositor, setAccNum, setBalance, setAccType, maturityDate);
	        } else if(setAccType.equals("Checking")) {
	            myAccount = new CheckingAccount(myDepositor, setAccNum, setBalance, setAccType);
	        } else {
	        	myAccount = new SavingsAccount(myDepositor, setAccNum, setBalance, setAccType);
	        }

	        // Add the created account to the array
	        bank.openNewAcct(myAccount); 
	        count++; 
	    }

	    // close the input file
	    sc.close();

	    // return the account number count
	    return count;
	}
  
	/* Method printAccts:
	 * Input:
	 *  bank - bank objects
	 *  numAccts - number of active accounts
	 *  outFile - reference to the output file
	 * Process:
	 *  Prints the database of bank account info (name, ssn, accNum, AccType, balance, maturity date (for CD accounts))
	 * Output:
	 *  Prints the database of accounts and balances for each account 
	*/
	public static void printAccts(Bank bank, int numAccts, PrintWriter outFile) {
	    outFile.println();
	    outFile.printf("%-15s %-15s %-12s %-15s %-15s %-15s %8s %17s%n",
	            "Last Name", "First Name", "SSN", "Acct Num", "Acct Type", "Account Status", "Balance", "Maturity Date");

	    // Print account information for every active account
	    for (int i = 0; i < bank.getAccount().size(); i++) {
	        Account currentAccount = bank.getAccount().get(i);
	        if (currentAccount != null && currentAccount.getDepositor() != null) {
	            outFile.println(currentAccount.toString());
	        }
	    }
	    outFile.println("*********** Total amount in each/all Account ***********");
        outFile.println("Total Amount In Checking Accounts: $" + Bank.getTotalAmountInCheckingAccts());
        outFile.println("Total Amount In Savings Accounts: $" + Bank.getTotalAmountInSavingsAccts());
        outFile.println("Total Amount In CD Accounts: $" + Bank.getTotalAmountInCDAccts());
        outFile.println("Total Amount In ALL Accounts: $" + Bank.getTotalAmountInAllAccts());
	    outFile.println();
	    // Flush the output file
	    outFile.flush();
	}
	
	/* Method menu()
	 * Input:
	 *  none
	 * Process:
	 *  Prints the menu of transaction choices
	 * Output:
	 *  Prints the menu of transaction choices
	 */
	public static void menu() //changed menu 
	{
	    System.out.println();
	    System.out.println("Select one of the following transactions:");
	    System.out.println("\t****************************");
	    System.out.println("\t    List of Choices         ");
	    System.out.println("\t****************************");
	    System.out.println("\t     W -- Withdrawal");
	    System.out.println("\t     D -- Deposit");
	    System.out.println("\t     N -- New Account");
	    System.out.println("\t     B -- Balance Inquiry");
	    System.out.println("\t     C -- Clear Check");
	    System.out.println("\t     I -- Account Info"); 
	    System.out.println("\t     H -- Account Info plus Account Transaction History"); 
	    System.out.println("\t     S -- Close Account"); 
	    System.out.println("\t     R -- Reopen a Closed Account"); 
	    System.out.println("\t     X -- Delete Account");
	    System.out.println("\t     Q -- Quit");
	    System.out.println();
	    System.out.print("\tEnter your selection: ");
	}
	
	
	/* Method balance:
	 * Input:
	 *  bankAcc - array of bank account objects
	 *  numAccts - number of active accounts
	 *  outFile - reference to output file	
	 *  kybd - reference to the "test cases" input file
	 * Process:
	 *  Prompts for the requested account
	 *  Calls findAcct() to see if the account exists
	 *  If the account exists, the balance & account type is printed
	 *  Otherwise, an error message is printed
	 * Output:
	 *  If the account exists, the balance & account type is printed
	 *  Otherwise, an error message is printed
	 */
	public static void balance(Bank bank, int numAccts, 
			PrintWriter outFile, Scanner kybd) //done
	{
	    int requestedAccount;

	    System.out.println();
	    System.out.print("Enter the account number: ");			//prompt for the account number
	    requestedAccount = kybd.nextInt();						//read-in the account number
	    int termOfCD = 0;
	    TransactionReceipt tr;
	    TransactionTicket t;
	    if (bank.getAcct(requestedAccount) != null && bank.getAcct(requestedAccount).getMaturityDate() != null &&
	            bank.getAcct(requestedAccount).getAccountType().equals("CD")) {
	        Calendar matDate = bank.getAcct(requestedAccount).getMaturityDate();
	        termOfCD = matDate.get(Calendar.MONTH) - Calendar.getInstance().get(Calendar.MONTH);
	    }
	    if(bank.getAcct(requestedAccount) != null && bank.getAcct(requestedAccount).getMaturityDate() != null) {
	    	 t = new TransactionTicket(requestedAccount, "", "Balance Inquiry", 0.0, termOfCD, 
	    			 bank.getAcct(requestedAccount).getMaturityDate());
	    }else {
	    	 t = new TransactionTicket(requestedAccount, "", "Balance Inquiry", 0.0, termOfCD);
	    }
	    tr = bank.getBalance(t);
	    
	    if (!tr.getTransactionSuccessIndicatorFlag())                                        
	    	//invalid account
	    {
	    	outFile.println(tr.toString());
	       
	    }
	    else                                                    //valid account
	    {
	        outFile.println(tr.toString());
	    }
	    outFile.println();

	    outFile.flush();				//flush the output buffer
	}

	/* Method deposit:
	 * Input:
	 *  bank - bank object
	 *  numAccts - number of active accounts
	 *  outFile - reference to the output file
	 *  kybd - reference to the "test cases" input file
	* Process:
	 *  Prompts for the requested account
	 *  Calls makeDeposit() to see if the account exists and deposit
	 *  If the account exists, prompts for the amount to deposit
	 *  If Account is CD then it prompts user for new term and update maturity date
	 *  If the amount is valid, it makes the deposit and prints the new balance & account type
	 *  Otherwise, an error message is printed
	 * Output:
	 *  For a valid deposit, the deposit transaction is printed
	 *  Otherwise, an error message is printed
	 */
	public static void deposit(Bank bank, int numAccts, PrintWriter outFile, Scanner kybd) {
		int requestedAccount;
	    double amountToDeposit;
	    TransactionReceipt tr;
	    System.out.println();
	    System.out.println("Enter the account number: "); // prompt for the account number
	    requestedAccount = kybd.nextInt(); // read-in the account number

	    // Check if the account exists
	    if (bank.getAcct(requestedAccount) != null) {
	        System.out.println("Enter amount to deposit: "); // prompt for amount to deposit
	        amountToDeposit = kybd.nextDouble(); // read-in the amount to deposit
	        int termOfCD = 0;

	        if (bank.getAcct(requestedAccount).getMaturityDate() != null &&
	                bank.getAcct(requestedAccount).getAccountType().equals("CD")) {
	            Calendar matDate = bank.getAcct(requestedAccount).getMaturityDate();
	            System.out.println("Enter new term/date (6, 12, 18, or 24)");
	            termOfCD = kybd.nextInt();
	            //preMatDate = matDate.get(Calendar.MONTH) - termOfCD;
	        }
	        TransactionTicket t = new TransactionTicket(requestedAccount, bank.getAcct(requestedAccount).getCurrentDate(),
	                "Deposit", amountToDeposit, termOfCD, bank.getAcct(requestedAccount).getMaturityDate()); 
	        tr = bank.makeDeposit(t);
	        if (!tr.getTransactionSuccessIndicatorFlag()) // invalid account
	        {
	        	outFile.println(tr.toString());
	        } else { 
	        	// valid accounts
	            outFile.println(tr.toString());
	       }
	        
	    } else {
	    	TransactionTicket t = new TransactionTicket(requestedAccount, "",
	                "Deposit", 0, 0); 
	        tr = bank.makeDeposit(t);
	        outFile.println(tr.toString());
	    }

	    outFile.println();
	    outFile.flush(); // flush the output buffer
	}



	/*
	 * Method Withdrawal:
	 * Input: 
	 * 	bank - bank object
	 *  numAccts - number of active accounts
	 *  outFile - reference to the output file
	 *  kybd - reference to the "test cases" input file
	 * Process:
	 * 	Prompts user for requested Account (reqAccount)
	 * 	Calls makeWithdrawal() to see if the account exists and withdraw
	 *  If account exists then it asks user for amount of the withdrawal and executes the action
	 *  If Account is CD then it prompts user for new term and update maturity date
	 *  Or else print error account exists
	 * Output:
	 * 	For a valid withdrawal, the withdrawal transaction is printed
	 *  Otherwise, an error message is printed
	 */
	public static void withdrawal(Bank bank, int numAccts, PrintWriter outFile, Scanner kybd) {
		TransactionReceipt tr;
	    int reqAccount;
	    double amountToWithdraw;

	    System.out.println("Enter your account number: ");

	    // Validate account number input
	    while (!kybd.hasNextInt()) {
	        System.out.println("Invalid input. Please enter a valid account number: ");
	        kybd.next(); // Consume invalid input
	    }

	    reqAccount = kybd.nextInt();

	    if (bank.getAcct(reqAccount) != null) {
	        System.out.println("Enter amount to withdraw: ");

	        amountToWithdraw = kybd.nextDouble();
	        int termOfCD = 0;

	        if (bank.getAcct(reqAccount).getMaturityDate() != null &&
	                bank.getAcct(reqAccount).getAccountType().equals("CD")) {
	            System.out.println("Enter new term/date (6, 12, 18, or 24)");
	            termOfCD = kybd.nextInt();
	            System.out.println(termOfCD);
	        }

	        TransactionTicket t = new TransactionTicket(reqAccount, bank.getAcct(reqAccount).getCurrentDate(),
	                "Withdrawal", amountToWithdraw, termOfCD, bank.getAcct(reqAccount).getMaturityDate());
	        tr = bank.makeWithdrawal(t);

	        if (!tr.getTransactionSuccessIndicatorFlag()) {
	        	outFile.println(tr.toString());
	        } else {
	        	outFile.println(tr.toString());
	        }
	    } else {
	        // Handle non-existent account
	    	TransactionTicket t = new TransactionTicket(reqAccount, "",
	                "Withdrawal", 0, 0); 
	        tr = bank.makeWithdrawal(t);
	        outFile.println(tr.toString());
	    }
	    outFile.println();
        outFile.flush();
	}

	/*
	 * Method New Account:
	 * Input: 
	 * 	bank - bank object
	 *  numAccts - number of active accounts
	 *  outFile - reference to the output file
	 *  kybd - reference to the "test cases" input file
	 * Process:
	 * 	Prompts user for the new account number (newAccNum)
	 * 	Prompts user for info and inputs into account
	 * 	Calls openNewAcct() to execute the creation of new account
	 *  If account already exists then print error account already exists
	 *  If account is CD type then it prompts user for a term for maturity date
	 *  Otherwise it makes the new account with all of the depositor's info and desired initial opening balance
	 * Output:
	 * 	For a successful creation, it prints the new account number, balance, and account type
	 * 	Otherwise error is printed
	 */
	public static int newAcct(Bank bank, int numAccts,
            PrintWriter outFile, Scanner kybd) {
		Account account;
		int matDate = 0;
	    int newAccNum = 0;
	    System.out.println("Enter your new account number: ");
	    newAccNum = kybd.nextInt();
	    System.out.println("newAccNum: " + newAccNum); 
	  
	    System.out.println("Enter new depositor First name: ");
	    //kybd.next();
	    String first = kybd.next();
	    System.out.println("first: " + first); 

	    System.out.println("Enter last name: ");
	    String last = kybd.next();
	    System.out.println("last: " + last); 

	    System.out.println("Enter your SSN: ");
	    String SSN = kybd.next();
	    System.out.println("SSN: " + SSN); 

	    System.out.println("Enter account type: ");
	    String accType = kybd.next();
	    System.out.println("accType: " + accType);

	    System.out.println("Enter your initial opening balance: ");
	    double openingBalance = kybd.nextDouble();
	    System.out.println("openingBalance: " + openingBalance); 

	    if (accType.equals("CD")) {
	        System.out.println("Enter term of CD: ");
	        matDate = kybd.nextInt();
	        System.out.println("matDate: " + matDate); 
	        Name n = new Name(first, last);
	        Depositor d = new Depositor(n, SSN);
	        account = new Account(d, newAccNum, openingBalance, accType, matDate);
	    }else {
		    Name n = new Name(first, last);
	        Depositor d = new Depositor(n, SSN);
	        account = new Account(d, newAccNum, openingBalance, accType);
		    }
	    TransactionReceipt tr;
	    tr = bank.openNewAcct(account);
	    if (tr.getTransactionSuccessIndicatorFlag()) { 
	        outFile.println(tr.toString());
	    } else {
	        outFile.println(tr.toString());
	    }
	
	    outFile.println();
	    outFile.flush();
	    return numAccts; 
	}

	/*
	 * Method Delete Account:
	 * Input: 
	 * 	bank -  bank object
	 *  numAccts - number of active accounts
	 *  outFile - reference to the output file
	 *  kybd - reference to the "test cases" input file
	 * Process:
	 * 	Prompts user for the account number to be deleted (accNum)
	 * 	Calls .deleteAcct() to check if account num exists to delete
	 *  If a account exists (else print error does not exist) and has a balance of non-zero then it prints error
	 *  Otherwise if the account exists with a cleared balance, it shifts the element after the 
	 *  deleted index in the account array to fill the gap of deletion.
	 * Output:
	 * 	For a successful deletion, it prints the deletion confirmation 
	 * 	Otherwise error is printed due to non-existent account number
	 */
	public static int deleteAcct(Bank bank, int numAccts,
			PrintWriter outFile, Scanner kybd) 
	{
		int accNum = 0;
		System.out.println("Enter the account number: ");
		accNum = kybd.nextInt();
		TransactionReceipt tr;
		TransactionTicket t = new TransactionTicket(accNum, bank.getCurrentDate(), "Delete Account", 0);
		tr = bank.deleteAcct(t);
		
		
		if(tr.getTransactionSuccessIndicatorFlag()) {
	        outFile.println(tr.toString());
	    } else {
	    	//No such account num is found so print error
	        outFile.println(tr.toString());
	    }
		outFile.println();
	    outFile.flush();

		return numAccts;
	}
	/*
	 * Method Account Info:
	 * Input: 
	 * 	bank - bank object
	 *  numAccts - number of active accounts
	 *  outFile - reference to the output file
	 *  kybd - reference to the "test cases" input file
	 * Process:
	 * 	Prompts user for a SSN 
	 * 	Checks if the requested SSN exists in the database (If yes: increment # of acc found with such SSN & print info)
	 * 	Otherwise it prints that 0 account was found under the SSN
	 * Output:
	 * 	For a successful find of SSN in the database, it prints a table with all the info under Depositor's SSN
	 * 	Otherwise error is printed & 0 accts were found
	 */
	public static void accountInfo(Bank bank, int numAccts, PrintWriter outFile, Scanner kybd) {
	    String reqSSN;
	    System.out.println("Enter your SSN: ");
	    reqSSN = kybd.nextLine();
	    int accFound = 0; //number of accounts found with requested SSN  

	    //header 
	    outFile.println("Transaction Requested: Account Info");
	    outFile.println("SSN: " + reqSSN);
	    outFile.printf("%-15s %-15s %-11s %-15s %-15s %-15s %8s %17s\n",
	            "Last Name", "First Name", "SSN", "Acct Num", "Acct Type", "Account Status", "Balance", "Maturity Date");

	    for (int i = 0; i < numAccts; i++) {
	        Account currentAccount = bank.getAccount().get(i);
	        if (currentAccount != null && currentAccount.getDepositor() != null &&
	                currentAccount.getDepositor().getSSN().trim().equalsIgnoreCase(reqSSN.trim())) { //checks if ssn exists
	            accFound++; //increment the # of accounts found with such SSN
	            
	            //prints bank info 
	            outFile.println(currentAccount.toString());
	            
	        }
	    }
	    // Prints the number of accounts found at the end
	    if (accFound > 0) {
	        outFile.println(accFound + " accounts were found");
	    } else {
	        outFile.println("Error: NO account exists for this SSN!"); //no account with such SSN exists
	        outFile.println(accFound + " accounts were found");
	    }
	    outFile.println();
	    outFile.flush();
	}


	/*
	 * Method Clear Check:
	 * Input: 
	 * 	bank - bank object
	 *  numAccts - number of active accounts
	 *  outFile - reference to the output file
	 *  kybd - reference to the "test cases" input file
	 * Process:
	 * 	Prompts user for account number, check amount, and check date
	 * 	Calls .clearCheck() to execute the action 
	 * 	Checks if the account exists and is a checking account otherwise print error
	 * 	Checks if account has sufficient funds otherwise charge bounce fee of $2.50
	 * Output:
	 * 	For a successful clear it prints success and clears check amount 
	 * 	Otherwise error is printed/fee charged
	 */
	public static void clearCheck(Bank bank, int numAccts, PrintWriter outFile, Scanner kybd) {
		double checkAmount = 0;
		String date = "";
		
		System.out.println("Enter Account Number: ");
		int reqAccNum = kybd.nextInt();
		System.out.println(reqAccNum);
		
		System.out.println("Enter Check Amount: ");
		checkAmount = kybd.nextDouble();
		System.out.println(checkAmount);
		
		System.out.println("Enter Check Date: ");
		date = kybd.next();
		System.out.println(date);
		
		TransactionReceipt tr;
		Check c = new Check(reqAccNum, checkAmount, date);
		tr = bank.clearCheck(c);
		if(tr.getTransactionSuccessIndicatorFlag()) {
			outFile.println(tr.toString());
		}else {
			outFile.println(tr.toString());
		}
		outFile.println();
		outFile.flush();
	}
	/*
	 * Method Account Info w History:
	 * Input: 
	 * 	bank - bank object
	 *  numAccts - number of active accounts
	 *  outFile - reference to the output file
	 *  kybd - reference to the "test cases" input file
	 * Process:
	 * 	Prompts user for a SSN 
	 * 	Checks if the requested SSN exists in the database (If yes: increment # of acc found with such SSN & print info)
	 * 	Otherwise it prints that 0 account was found under the SSN
	 * 	Then finds the transaction receipts for those accounts with the SSN
	 * Output:
	 * 	For a successful find of SSN in the database, it prints a table with all the info under Depositor's SSN and history
	 * 	Otherwise error is printed & 0 accts were found
	 */
	public static void accountInfoWithHistory(Bank bank, int numAccts, PrintWriter outFile, Scanner kybd) {
	    String reqSSN;
	    System.out.println("Enter your SSN: ");
	    reqSSN = kybd.nextLine();
	    int accFound = 0; // number of accounts found with requested SSN

	    // header
	    outFile.println("Transaction Requested: Account Info With Transaction History");
	    outFile.println("SSN: " + reqSSN);
	    outFile.println();
	    
	    for (int i = 0; i < bank.getAccount().size(); i++) {
	        if (bank.getAccount().get(i) != null && bank.getAccount().get(i).getDepositor() != null &&
	                bank.getAccount().get(i).getDepositor().getSSN().trim().equals(reqSSN.trim())) { // checks if ssn exists
	            accFound++; // increment the # of accounts found with such SSN
	            outFile.printf("%-15s %-15s %-12s %-15s %-15s %-15s %8s %17s%n",
	    	            "Last Name", "First Name", "SSN", "Acct Num", "Acct Type", "Account Status", "Balance", "Maturity Date");
	            // prints bank info
	            outFile.println(bank.getAccount().get(i).toString());
    
	            outFile.println("***** Account Transactions *****");
	            outFile.printf("%-15s %-15s %8s %10s %10s %25s\n",
	                    "Date", "Transaction", "Amount", "Status", "Balance", "Reason For Failure");
	            
        		for(int j=0;j<bank.getAccount().size();j++){
	        		TransactionTicket t = new TransactionTicket(bank.getAccount().get(j).getAccountNumber(), 
	        				bank.getAccount().get(j).getDate(), "Acc Info W/ History", 0);
		            ArrayList<TransactionReceipt> trHistory = bank.getAccount().get(i).getTransactionHistory(t);
		            if (trHistory != null) {
		            	for (TransactionReceipt receipt : trHistory) {
		            		outFile.printf("%-15s %-15s $%7.2f %9s $%10.2f %25s\n",
		            		        receipt.getTransactionTicket().getDateOfTransactionString(),
		            		        receipt.getTransactionTicket().getTransactionType(),
		            		        receipt.getTransactionTicket().getTransactionAmount(),
		            		        receipt.getStatus(),
		            		        receipt.getPostTransactionBalance(),
		            		        receipt.getTransactionFailureReason());
		            	}
		            	
		            }
        		}
        		outFile.println();
	        }
	        
	    }
	    // Prints the number of accounts found at the end
	    if (accFound > 0) {
	        outFile.println(accFound + " accounts were found");
	    } else {
	        outFile.println("Error: NO account exists for this SSN!"); // no account with such SSN exists
	        outFile.println(accFound + " accounts were found");
	    }
	    
	    outFile.println();
	    outFile.flush();
	}
	
	/*
	 * Method Close Account:
	 * Input: 
	 * 	bank - bank object
	 *  numAccts - number of active accounts
	 *  outFile - reference to the output file
	 *  kybd - reference to the "test cases" input file
	 * Process:
	 * 	Prompts user for a account number
	 * 	Creates a ticket and sends it to the closeAcct method 
	 * 	If account is open, it closes and prevents it from transactions other than balance inquiries
	 * 	Otherwise print error
	 * Output:
	 * 	For a successful transaction, the account is open and not already closed or does not exist 
	 */
	public static void closeAccount(Bank bank, int numAccts, PrintWriter outFile, Scanner kybd) {
		int accNum;
		System.out.println("Enter the account number: ");
		accNum = kybd.nextInt();
		TransactionTicket t = new TransactionTicket(accNum, bank.getCurrentDate(), "Close Account", 0);
		TransactionReceipt tr = bank.closeAcct(t);
		if(!tr.getTransactionSuccessIndicatorFlag()) {
			outFile.println(tr.toString());
		}else {
			outFile.println(tr.toString());
		}
		outFile.println();
		outFile.flush();
	}
	/*
	 * Method reopen Account:
	 * Input:
	 * 	bank - bank object
	 *  numAccts - number of active accounts
	 *  outFile - reference to the output file
	 *  kybd - reference to the "test cases" input file
	 * Process:
	 * 	Prompts user for a account number
	 * 	Creates a ticket and sends to bank reopenAcct method
	 * 	If account is closed, it will be reopen 
	 * 	Otherwise print error
	 * Output:
	 * 	For a successful transaction, the account is closed and reopned
	 * 	but the account is not already opened or does not exist
	 */
	public static void reopenAccount(Bank bank, int numAccts, PrintWriter outFile, Scanner kybd) {
		int accNum;
		System.out.println("Enter the account number: ");
		accNum = kybd.nextInt();
		TransactionTicket t = new TransactionTicket(accNum, bank.getCurrentDate(), "reopen Account", 0);
		TransactionReceipt tr = bank.reopenAcct(t);
		if(!tr.getTransactionSuccessIndicatorFlag()) {
			outFile.println(tr.toString());
		}else {
			outFile.println(tr.toString());
		}
		outFile.println();
		outFile.flush();
	}
	
	/* Method pause() */ 
	public static void pause(Scanner keyboard)
	{
		String tempstr;
		System.out.println();
		System.out.print("press ENTER to continue");
		tempstr = keyboard.nextLine();		//flush previous ENTER
		tempstr = keyboard.nextLine();		//wait for ENTER
	}

}
