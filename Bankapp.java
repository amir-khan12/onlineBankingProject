package com.capgemini.swissbank.ui;

import java.util.List;
import java.util.Scanner;

import com.capgemini.swissbank.bean.AccMasterBean;
import com.capgemini.swissbank.bean.AccountType;
import com.capgemini.swissbank.bean.CustomerBean;
import com.capgemini.swissbank.bean.PayeeBean;
import com.capgemini.swissbank.bean.TransactionBean;
import com.capgemini.swissbank.bean.UserTable;
import com.capgemini.swissbank.exception.BankException;
import com.capgemini.swissbank.service.AdminServiceImpl;
import com.capgemini.swissbank.service.CustomerServiceImpl;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Bankapp {
	public static Scanner in=new Scanner(System.in);
	
	public static Logger logger=Logger.getRootLogger();
	
	public static void main(String[] args) {
		
		PropertyConfigurator.configure("resources//log4j.properties");
		
		int uid;
		String password;
		UserTable validUser = null;
		boolean userCheck = false;
		boolean isOpen=false;
		char uType;
		int count=0;
		CustomerServiceImpl serviceCustomer = new CustomerServiceImpl();

		//Scanner input = new Scanner(System.in);

		System.out.println(
				"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println();
		System.out.println("   MMMMMMMMMMMMMMMMMMMMWOc.,cxXMMMMMMMMMMMMMMMMMMMMMM               CCCCCCCCCCCCC             GGGGGGGGGGGGG    \n"
				         + "   MMMMMMMMMMMMMMMMMWKd;      .ckNMMMMMMMMMMMMMMMMMMM              CCC::::::::::::C          GGG::::::::::::G  \n"
				         + "   MMMMMMMMMMMMMWXkl,.           'cxKNMMMMMMMMMMMMMMM            CC:::::::::::::::C        GG:::::::::::::::G  \n"
				         + "   MMMMMMMMMNKkl;.                  .'cdOXWMMMMMMMMMM           C:::::CCCCCCCC::::C       G:::::GGGGGGGG::::G  \n"
				         + "   MMMMWXko:'.                           .;lx0NMMMMMM          C:::::C       CCCCCC      G:::::G       GGGGGG  \n"
				         + "   MMXx:.                                    .,l0WMMM          C:::::C                   G:::::G               \n"
				         + "   WO,                                          .lXMM          C:::::C                   G:::::G               \n"
				         + "   Nc                                            .kMM          C:::::C                   G:::::G    GGGGGGGGGG \n"
				         + "   M0;.                                         .dNMM          C:::::C                   G:::::G    G::::::::G \n"
				         + "   MMN0o:'..        ..''      .,..         ..;lkXMMMM          C:::::C                   G:::::G    GGGGG::::G \n"
				         + "   MMMMMMNX0OkxxxxkO0KO;      .dKKOOkxxxkk0KNWMMMMMMM          C:::::C                   G:::::G        G::::G \n"
				         + "   MMMMMMMMMMMMMMMMNOl.         ;xXWMMMMMMMMMMMMMMMMM          C:::::C       CCCCCC      G:::::G       G::::G  \n"
				         + "   MMMMMMMMMMMMMNOl;.            .':dKMMMMMMMMMMMMMMM           C:::::CCCCCCCC::::C       G:::::GGGGGGGG::::G  \n"
				         + "   MMMMMMMMMMMMMNOdddddddddddddddddddKMMMMMMMMMMMMMMM            CC:::::::::::::::C        GG:::::::::::::::G  \n"  
				         + "                                                                   CCC::::::::::::C          GGG::::::GGG:::G  \n"
				         + "                                                                      CCCCCCCCCCCCC             GGGGGG   GGGG  \n");                                        
				         
		System.out.println(
				"+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		System.out.println();
		System.out.println("-----------");
		System.out.println("WELCOME");
		System.out.println("-----------");

		do {
			
			System.out.println("Please Enter User ID to login:");
			uid = in.nextInt();
			in.nextLine();			
			
			try {
		
				boolean isValid=serviceCustomer.validateUser(uid);
				if(isValid){
					 isOpen=serviceCustomer.getUserStatus(uid);
					if(isOpen){
						do{
							
							System.out.println("Please Enter your password:  (forgot password ? press Y/y)");
							password = in.nextLine();
							//if user forgets password
									if(password.equalsIgnoreCase("Y"))
									{
										String[] secret=serviceCustomer.getSecretQuestion(uid);
										System.out.println("Please Answer the secret question: \n"+secret[0]);
										String secretAnswer = in.nextLine();
							
										if(secretAnswer.equals(secret[1]))
										{
											serviceCustomer.changePassword(uid, "sbq500#");
											System.out.println("Your password is sbq500#\nUse this to login.\nChange your password on next login");
											
										}
										
										else{
											try {
												serviceCustomer.updateLockStatus(uid);
												System.out.println("Your answer is wrong.\nLooks like an unauthorised access......... Your account has been locked\n Please Contact the Admin for further assistance");
												System.exit(-1);	
											} catch (BankException e) {
												logger.error(e.getMessage());
												e.printStackTrace();
											}
											
										}
										
										}
									else{
								
									validUser=serviceCustomer.validateUser(uid, password);
										if (validUser != null) {
											
											userCheck = true;
										
											}
						
									else {
										
										count++;
							
										if(count<3&&count>0){
											System.out.println("Incorrect password entered "+(3-count)+" tries left ");
										}
										else{
											try {
												serviceCustomer.updateLockStatus(uid);
												System.out.println("Sorry Your account got locked\nContact the Admin for further assistance");
												System.exit(-1);	
											} catch (BankException e) {
												logger.error(e.getMessage());
												e.printStackTrace();
											}
												
											}							
							
										}
						
								}
						}while(count<3&&userCheck==false);
							
						
						
					}
					else{
						System.out.println("Sorry Your account is locked.\nPlease Contact Admin for further assistance");
						System.exit(-1);
					}
				}
				else{
					System.out.println("Invalid User ID");
				}
				
			} catch (BankException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			
		} while (userCheck == false);

		
		if (validUser.getType().equals("A")) {
			openAdminPage(validUser); // opens admin page
		} else {
			openUserPage(validUser); // opens customer page
		}
		

	}

	private static void openAdminPage(UserTable admin) {
int choice = 0;
		
		System.out.println("Welcome  back" + admin.getUserName() );
		System.out.println("What would you like to do today ?");
		
		do {
			System.out.println("1. Create Account");
			System.out.println("2. View Transactions");
			System.out.println("3. Exit");
			choice=in.nextInt();
			in.nextLine();
			switch (choice) {
			case 1:
				createAccount(admin);
				break;

			case 2:
				viewTransactions(admin);				
			case 3:
				System.out.println("Thank you! Have a nice day.");
				break;
			default:
				System.out.println("Wrong option entered please try again");
				break;
			}
		} while (choice !=3 );

	}
	
	private static void openUserPage(UserTable user) {
		int selection = 0;
		                               
		System.out.println("Welcome " + user.getUserName() + " please enter your choice");
		
		//Scanner input1 = new Scanner(System.in);
		do {
			// some cosmetics to be done here
			System.out.println("1. View Bank statement");
			System.out.println("2. Change password");
			System.out.println("3. Change address and/or phone number");
			System.out.println("4. Fund transfer");
			System.out.println("5. Issue new cheque");
			System.out.println("6. Track chequebook/card  delivery status");
			System.out.println("7. Exit");

			selection = in.nextInt();
			in.nextLine();
			switch (selection) {
			case 1:
				//System.out.println("here's your statement");
				showBankStatement(user);
				break;

			case 2:
				//System.out.println("password changed");
				changePassword(user);
				break;

			case 3:
				//System.out.println("address and phone number changed");
				changeDetails(user);
				break;

			case 4:
				//System.out.println("fund transferred to blah");
				fundTransfer(user);
				break;

			case 5:
				//System.out.println("new cheque");
				newChequeBook(user);
				break;

			case 6:
				
				trackStatus(user);
				break;

			case 7:
				System.out.println("Thank you have a nice day :-)");
				break;

			default:
				System.out.println("Invalid option entered. Please try again");
				break;
			}

		} while (selection != 7);

		//input1.close();
	}
	
	

	private static void showBankStatement(UserTable user) {
		int choice = 0;
		//Scanner sc = new Scanner(System.in);

		do {
			System.out.println("1. Mini bank statement");
			System.out.println("2. Detailed statement");

			choice = in.nextInt();

			switch (choice) {
			case 1:
				miniBankStatement(user);
				break;

			case 2:
				detailedBankStatement(user);
				break;

			default:
				System.out.println("Invalid option");
				System.out.println("please try again");
				break;
			}
		} while (choice != 1 && choice != 2);
		//sc.close();

	}
	
	private static void miniBankStatement(UserTable user) {

		CustomerServiceImpl serviceCustomer = new CustomerServiceImpl();

		int accId = user.getAccId();

		List<TransactionBean> list = null;
		try {
			list = serviceCustomer.viewMiniStatement(accId);
		} catch (BankException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		for (TransactionBean transactionBean : list) {
			System.out.println(transactionBean);

		}
	}
	
	private static void detailedBankStatement(UserTable user) {
		
		CustomerServiceImpl serviceCustomer = new CustomerServiceImpl();
		int accId = user.getAccId();
		
		List<TransactionBean> list = null;
		try {
			list = serviceCustomer.viewDetailedStatement(accId);
		} catch (BankException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		for (TransactionBean transactionBean : list) {
			System.out.println(transactionBean);
		}
	}

	private static void changePassword(UserTable user) {
		
		int accId = user.getAccId();
		String checkOldPwd = user.getPassword();
		String checkNewPwd;
		String oldPwd;
		String newPwd;
		boolean changeStatus = false;

		CustomerServiceImpl serviceCustomer = new CustomerServiceImpl();

		//Scanner sc1 = new Scanner(System.in);

		System.out.println("Please enter your current password");
		oldPwd = in.nextLine();

		// compare if he's entered his old pwd right
		// this validation should be performed in the service layer

		Out:
		if (checkOldPwd.equals(oldPwd)) {
			System.out.println("Enter your new password");
			newPwd = in.nextLine();
			
			do {
				System.out.println("Reenter your new password");
				checkNewPwd = in.nextLine();
				
				if(!(newPwd.equals(checkNewPwd))) {
					System.out.println("passwords don't match ! ");
					System.out.println("Please check and try again");
				}
				
			} while (!(newPwd.equals(checkNewPwd)));
			
			System.out.println("Are you sure you want to continue ? (y/n)");
			
			String answer=in.nextLine();
			
			if(answer.equals("Y")||answer.equals("y"))
			{	
				try {
					changeStatus = serviceCustomer.changePassword(accId, oldPwd, newPwd);
				} catch (BankException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
			
				if (changeStatus) {
					System.out.println("Password successfully changed");

				} else {
					System.out.println("Unable to change password");

				}
			}else
				break Out;

		} else {
			System.out.println("Unable to proceed further as wrong password is entered");
		}
		//sc1.close();
	}

	private static void changeDetails(UserTable user) {
		int choice = 0;
		//Scanner sc2 = new Scanner(System.in);

		do {
			System.out.println("1. Change address");
			System.out.println("2. Change phone number");

			choice = in.nextInt();
			in.nextLine();
			switch (choice) {
			case 1:
				changeAddress(user);	

				break;

			case 2:
				changePhoneNumber(user);				
				break;
				
			default:
				System.out.println("Invalid option !");
				System.out.println("please try again");
				break;
			}

		}			
		while (choice != 1 && choice != 2);
		
		//sc2.close();
	}
	
	private static void changeAddress(UserTable user) {
		int accId = user.getAccId();
		String newAddress;
		boolean isChanged = false;
		
		CustomerServiceImpl serviceCustomer = new CustomerServiceImpl();
		
		
		//Scanner sc = new Scanner(System.in);
		System.out.println("Please enter your new address");
		newAddress = in.nextLine();
		
		try {
			isChanged = serviceCustomer.changeAddress(accId, newAddress);
		} catch (BankException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		if(isChanged) {
			System.out.println("New address updated successfully");
		}else {
			System.out.println("Something went wrong. Could not update the address. Please try after sometime");
		}
		
		//sc.close();
	}

	private static void changePhoneNumber(UserTable user) {
		int accId = user.getAccId();
		String newPhoneNumber;
		boolean isChanged = false;
		
		CustomerServiceImpl serviceCustomer = new CustomerServiceImpl();
		
		//Scanner sc = new Scanner(System.in);
		
		System.out.println("Please enter your new Phone number");
		newPhoneNumber = in.nextLine();
		
		try {
			isChanged = serviceCustomer.changePhoneNumber(accId, newPhoneNumber);
		} catch (BankException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
		if(isChanged) {
			System.out.println("New phone number updated successfully");
		}else {
			System.out.println("Something went wrong. Could not update the phone number. Please try after sometime");
		}
		
		//sc.close();		
	}

	private static void fundTransfer(UserTable user) {
		CustomerServiceImpl serviceCustomer = new CustomerServiceImpl();
		System.out.println("Please Choose the type of fund transfer : ");
		System.out.println("1. With in the Bank");
		System.out.println("2. To other Banks");
		int choice=in.nextInt();
		in.nextLine();
		switch (choice) {
		case 1:
			inFundTransfer(user);
			break;
		case 2:
			System.out.println("1.Add Payee");
			System.out.println("2.Select Payee");
			int key=in.nextInt();
			in.nextLine();
			switch (key) {
			case 1:
				System.out.println("Enter Payee Account Id:");
				int payeeAccountId=in.nextInt();
				in.nextLine();
				System.out.println("Enter Payee Nickname:");
				String nickName=in.nextLine();
				try {
					boolean isInserted=serviceCustomer.insertPayee(user.getAccId(), payeeAccountId, nickName);
					if(isInserted)
						System.out.println("Payee added successfully.");
				} catch (BankException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
				break;
			case 2:
				
				System.out.println("The list of payees registered are:");
				try {
					List<PayeeBean> payeeList=serviceCustomer.viewPayee(user.getAccId());
					
					for (PayeeBean payeeBean : payeeList) {
						System.out.println(payeeBean);
					}
					
					System.out.println("Please Enter Transaction password");
					String transactionPassword=in.nextLine();
					if(serviceCustomer.validateTransactionPassword(transactionPassword, user))
					{
						System.out.println("Please Enter payee AccountID");
						int payeeAccountNumber=in.nextInt();
						in.nextLine();
					System.out.println("Please Enter the amount");
					double transactionAmount=in.nextDouble();
					in.nextLine();
					if(serviceCustomer.validateTransactionAmount(transactionAmount)){
					double balance=serviceCustomer.outFundTransfer(user.getAccId(), payeeAccountNumber, transactionPassword, transactionAmount);
					System.out.println("Transaction successful\nyour current balance is "+balance);
					}
					else{
						System.out.println("Can not transact more than Rs. 1000000");
					}
					}
					else{
						System.out.println("Invalid Transaction password");
					}
				} catch (BankException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
				break;
			
			default:
				System.out.println("Invalid option entered. Please try again ");				
				break;
			}
			
			
			break;
		default:
			break;
		}
		
		
	}
	
	private static void newChequeBook(UserTable user)  {
		CustomerServiceImpl serviceCustomer = new CustomerServiceImpl();
		try {
			int requisitionId=serviceCustomer.generateCheque(user.getAccId());
			System.out.println("Your requisition id is"+requisitionId);
		} catch (BankException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}
	private static void trackStatus(UserTable user) {
		CustomerServiceImpl serviceCustomer = new CustomerServiceImpl();
		System.out.println("Please enter the requisition Id");
		int requisitionId=in.nextInt();
		in.nextLine();
		try {
			String status=serviceCustomer.trackStatus(requisitionId);
			System.out.println("Your Service status is "+status);
		} catch (BankException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	private static void inFundTransfer(UserTable user){
		CustomerServiceImpl serviceCustomer = new CustomerServiceImpl();
		System.out.println("Please Enter payee Account Id");
		int accountIdTo=in.nextInt();
		in.nextLine();
		System.out.println("Please Enter Transaction amount");
		double transactionAmount=in.nextDouble();			
		in.nextLine();
		try {
			double balance=serviceCustomer.inFundTransfer(accountIdTo, user.getAccId(), transactionAmount);
			System.out.println("Transaction is successful.\nYour Balance is "+balance);
		} catch (BankException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	
private static void createAccount(UserTable admin){
		
		CustomerBean newCustomer = new CustomerBean() ; 
		AccMasterBean masterSheet = new AccMasterBean();
		AdminServiceImpl serviceAdmin = new AdminServiceImpl();
		UserTable newValidCustomer = new UserTable();
		String addLine1;
		String addLine2;
		String addLine3;
		List<String> errorList = null;
		
		do {
			errorList = null;// check to done here if empty or null
			System.out.println("Please enter the following details to create the account");
			System.out.println("Enter the name of the customer");
			newCustomer.setCustomerName(in.nextLine());
			System.out.println("Enter the address (line 1)"); // sad part is that address is in one line
			addLine1 = in.nextLine();
			System.out.println("Enter the adress (line 2)");
			addLine2 = in.nextLine();
			System.out.println("Enter the address (line 3)");
			addLine3 = in.nextLine();
			newCustomer.setAddress(addLine1 + "\n" + addLine2 + "\n" + addLine3);
			System.out.println("Enter phone number");
			newCustomer.setPhoneNumber(in.nextLine());
			System.out.println("Enter emailId");
			newCustomer.setEmail(in.nextLine());
			System.out.println("Enter the type of account (savings/current)");
			// do cross check this statement later
			masterSheet.setType(AccountType.valueOf(in.nextLine()));
			System.out.println("Enter the opening balance.");
			System.out.println(" Minimum balance should be 3000 Rs");
			masterSheet.setAccBalance(in.nextDouble());
			in.nextLine();
			System.out.println("Enter PAN card number");
			newCustomer.setPanNUm(in.nextLine());
			// pan number details not entered
			errorList = serviceAdmin.isValidNewCustomer(newCustomer, masterSheet);
			if (!errorList.isEmpty()) {
				System.out.println("Customer details entered are not valid");
				System.out.println("Please refer the errors and reenter the details ");
				for (String errors : errorList) {
					System.out.println(errors);
				}
			} 
		} while (!errorList.isEmpty());
		
		try {
			newValidCustomer = serviceAdmin.createUser(masterSheet, newCustomer);
			System.out.println("account created successfully!!!");
			System.out.println("Account number : " + newValidCustomer.getAccId());
			System.out.println("Custome name :" + newCustomer.getCustomerName());
			// why the heck should admin know dummy password?
			// to do generate password of 8 characters write a seperate method
			System.out.println(" dummy password has been sent to " + newCustomer.getPhoneNumber());
		} catch (BankException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		
	}

	private static void viewTransactions(UserTable admin){
		int choice = 0;
		AdminServiceImpl serviceAdmin = new AdminServiceImpl();
		List<TransactionBean> list = null;
		
		do {
			System.out.println("Enter the type of transactions to be viewed");
			System.out.println("1. daily transction");
			System.out.println("2. weekly transaction");
			System.out.println("3. monthly transcations");
			choice = in.nextInt();
			in.nextLine();
			switch (choice) {
			case 1:
				try {
					list = serviceAdmin.getTransactionsDaily();
					if(list==null){
						System.out.println("No transactions performed today");
					}
					else{
					for (TransactionBean transactionBean : list) {
						System.out.println(transactionBean);
					}
					}
				} catch (BankException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
				break;
			case 2:
				try {
					list = serviceAdmin.getTransactionsWeekly();
					if(list==null){
						System.out.println("No transactions performed in this week");
					}
					else{
					for (TransactionBean transactionBean : list) {
						System.out.println(transactionBean);
					}
					}
				} catch (BankException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
				break;
			case 3:
				try {
					list = serviceAdmin.getTransactionsMonthly();
					if(list==null){
						System.out.println("No transactions performed in this month");
					}
					else{
					for (TransactionBean transactionBean : list) {
						System.out.println(transactionBean);
					}
					}
				} catch (BankException e) {
					logger.error(e.getMessage());
					e.printStackTrace();
				}
				break;

			default:
				System.out.println("Wrong option entered, please try again");
				break;
			}
		} while (choice!=1 || choice!=2 || choice!=3);
	}

}
