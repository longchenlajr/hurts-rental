Chenla Long, Jr (chl223)  
CSE 241  

# Hurts Rent-A-Lemon  
### "Rent a painfully cheap, bad car"  

** Getting Started **  
    1. Starting in the directory chl223, the program can be run with the help of the Makefile. 'make all' will clean .class files, compile .java, and run the program (in that order). If you need to perform these actions individually: 'make clean', 'make build', 'make test' removes class files, compiles, and runs, respectively.  
    2. To log in to the database, you will be prompted to sign in with your Oracle username and password.  
    3. Each interface provides the user with the ability to interact with the Hurts database in various ways. Below are explanations of what to expect from each option you may choose. 

** Interfaces **  
    1. Customer  
        - If you would like to test with an existing account, logins with substantial data would be (75, Clevely) (57, Favel).
        a.  Login to existing account   
            - Prompts for login using the user's customer ID and last name. 
            i.)     View rental history  
                    - Displays a list of all existing rentals (past and current) of the logged in user.  
            ii.)    Make a reservation  
                    - Displays a list of unreserved, in-stock vehicles that are available for reservation. If reserved, a rental will be placed in advance.   
            iii.)   Remove a reservation  
                    - Drops a reservation for the logged in user if any are found.  
            iv.)    Rent a vehicle  
                    - Prompts for necessary information in order to create a rental.   
            v.)     Add payment method      
                    - Prompts for card type, number, and cvv.   
            vi.)    Logout  
                    - Exits to login screen.  
        b.  Create a new account  
            - Walks through the process to create a Hurts rental account. A valid Driver's License number and address is required for registration, and if a membership is present then you also have the option to link that company with your account.  
        c.  Quit to interface selection  
            - Exits.  
    2. Staff  
        a.  View inventory  
            - Displays a list of locations to check the inventory of (Selecting 0 will display a list of every vehicle in stock at any Hurts location).  
        b.  Create rental  
            - Prompts for necessary information in order to create a rental. If a customer account is not yet created, you will be asked to make one.   
        c.  Check-in rental  
            - Prompts for necessary information in order to check-in an active rental. In order to compute a final invoice, information regarding miscellaneous charges, insurance, dropoff, and fuel consumption will be requested. If a payment is not found, you will be given the option to add a new payment method before the invoice can be created.  
        d.  Create a customer account  
            - Walks through the process to create a Hurts rental account. A valid Driver's License number and address is required for registration, and if a membership is present then you also have the option to link that company with your account. 
        e.  Quit to interface selection.  
            - Exits.  
    3. Management  
        a.  Add a vehicle.  
            - Adds a vehicle to the Hurts database by collecting necessary information about the vehicle itself.
        b.  Move a vehicle.
            - Changes the location that the vehicle is stored at.
        c.  View unprocessed charges.  
            - Displays all the charges in the database that have not been paid for/accounted for. 
        d.  Process charges.  
            - Displays all the unaccounted for charges and processes them and prints an invoice.  
        e.  Quit to interface selection.
            - Exits.

** File Heirarchy ** 
chl223long {  
    - chl223 {  
        - Customer.java   
            - Java file for the Customer interface.  
        - Main.java  
            - Java file for the main driver of the application that calls each individual interface.  
        - Makefile  
            - File used to compile, run, and clean java class files.   
        - Manager.java  
            - Java file for the Manager interface.  
        - Manifest.txt  
            - Text file to specify class path for oracle driver.  
        - ojdbc8.jar  
            - Jar dependency allowing JDBC access.  
        - PS.java  
            - Java file containing functions to return SQL queries used in PreparedStatements.  
        - Staff.java  
            - Java file for the Staff interface.  
    }  
    - data_gen {  
        - datagen.ods  
            - Excel file to create queries based off of cells with generated data.   
        - QueryBuilding.txt  
            - Text file used to store all queries in order so that tables can be dropped, recreated, and populated when updates to the database are required.   
    }  
}  