import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Customer {
    Connection conn;
    Scanner in;

    public Customer(Connection conn, Scanner in) {
        this.conn = conn;
        this.in = in;
    }
    
    public void customerInterface(){ 
        int customer_id = welcome();
        if (customer_id < 0) {
            return;
        }
        mainMenu(customer_id);
    }

    public int welcome() {
        System.out.print("\n<----->\n\n");
        System.out.print("Welcome to Hurts Rent-A-Lemon!\n" +  
                            "Please select an operation:");
        while (true) {
            System.out.print( "\n[+] Login to existing account.\n" + 
                                "[!] Create a new account. \n" +
                                "[q] Quit to interface selection.\n:> ");
            String input = in.next();
            if (input.equals("+")) return loginAccount();
            else if (input.equals("!")) return createAccount();
            else if (input.equalsIgnoreCase("q")) break;
            else System.out.print("[Error] Please input a valid operation.\n");
        }
        return -1;
    }

    public void mainMenu(int cid) {
            System.out.print("\n<----->\n\n");
            System.out.print("Please select an operation:\n");
            while (true) {
            System.out.print("\n[*] View rental history.\n" + 
                                "[^] Make a reservation. \n" +
                                "[-] Remove a reservation. \n" +
                                "[@] Rent a vehicle.\n" +
                                "[$] Add payment method.\n" +
                                "[q] Logout.\n:> ");
        
            String input = in.next();
            if (input.equals("*")) viewHistory(cid);
            else if (input.equals("^")) makeReservation(cid);
            else if (input.equals("-")) removeReservation(cid);
            else if (input.equals("@")) rentCar(cid);
            else if (input.equals("$")) addPayment(cid);
            else if (input.equalsIgnoreCase("q")) break;
            else System.out.print("[Error] Please input a valid operation.\n");
        }
    }

    public int loginAccount() {
        int uid = -1;
        String lname = "";
        System.out.print("\n[Prompt] Please enter your user ID. \n:> ");
        while (true) {
            try {
                uid = in.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.print("[Error] User ID must be numeric. Try again\n:> ");
                in.next();
            }
        }
        System.out.print("\n[Prompt] Please enter your last name (Capitalization matters!).\n:> ");
        lname = in.next();
        String user = PS.getLogin();
        try {
            PreparedStatement selectUser = conn.prepareStatement(user);
            selectUser.setInt(1, uid);
            selectUser.setString(2, lname);
            ResultSet result = selectUser.executeQuery();
            if (!result.next()) {
                System.out.print("[Error] User not found.\n" + 
                                    "[Prompt] Create a new account? (or exit to main menu) [y/n]\n:> ");
                while (true) {
                    String input = in.next();
                    if (input.equalsIgnoreCase("y")) {
                        return createAccount();
                    } else if (input.equalsIgnoreCase("n")) {
                        return -1;
                    } else {
                        System.out.print("[Error] Select a valid option [y/n]: ");
                    }
                }
            } else {
                String name = result.getString("f_name") + " " + result.getString("l_name");
                System.out.println("[Success] Welcome back, " + name + "!\n");
                return uid;
            }
        } catch (SQLException e) {
            System.out.println("[Error] SQL Error: " + e);
        }
        return -1;
    }

    public int createAccount() {
        String fname = "";
        String lname = "";
        String addy = "";
        int dln = -1;
        int mid = -1;
        System.out.print("\n[Prompt] What is your first name?\n:> ");
        while (true) {
            fname = in.next();
            if (fname.matches("[a-zA-Z]+")) {
                break;
            }
            System.out.print("[Error] First name must be alphabetic, try again.\n:> ");
        }
        System.out.print("\n[Prompt] What is your last name?\n:> ");
        while (true) {
            lname = in.next();
            if (lname.matches("[a-zA-Z]+")) {
                break;
            }
            System.out.print("[Error] Last name must be alphabetic, try again.\n:> ");
        }
        System.out.print("\n[Prompt] Where do you live?\n:> ");
        in.next();
        addy = in.nextLine();
        System.out.print("\n[Prompt] Enter a valid driver's license number.\n:> ");
        while (true) {
            try {
                dln = in.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.print("[Error] DLN must be numeric, try again.\n:> ");
                in.next();
            }
        }
        System.out.print("\n[Prompt] Do you have a company membership? [y/n]\n:> ");
        while (true) {
            String input = in.next();
            if (input.equalsIgnoreCase("y")) {
                System.out.print("\n[Prompt] What is your membership number?\n:> ");
                while (true) {
                    try {
                        mid = in.nextInt();
                        try {
                            PreparedStatement selectMember = conn.prepareStatement(PS.getMembership());
                            selectMember.setInt(1, mid);
                            ResultSet res = selectMember.executeQuery();
                            if (!res.next()) {
                                System.out.print("[Error] Member ID not found, defaulting to no membership.\n");
                                mid = 11;
                                break;
                            } else {
                                mid = res.getInt("membership_id");
                                System.out.print("[Success] Member validated.\n");
                                break;
                            }
                        } catch (SQLException e) {
                            System.out.println("[Error] SQL Error");
                            System.out.print("\n[Prompt] Enter a valid membership number.\n:> ");
                        }
                    } catch (InputMismatchException e) {
                        System.out.print("[Error] Membership number must be numeric, try again.\n:> ");
                    }
                }
                break;
            } else if (input.equalsIgnoreCase("n")) {
                mid = 11;
                break;
            } else {
                System.out.print("[Error] Select a valid option [y/n]\n:> ");
            }
        }
        try {
            PreparedStatement insertCustomer = conn.prepareStatement(PS.addCustomer());
            insertCustomer.setString(1, fname);
            insertCustomer.setString(2, lname);
            insertCustomer.setString(3, addy);
            insertCustomer.setInt(4, dln);
            insertCustomer.setInt(5, mid);
            int res = insertCustomer.executeUpdate();
            if (res > 0) {
                System.out.print("[Success] Account created.\n");
            } else {
                System.out.print("[Error] Create an account failed.\n");
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not create account.\n");
        }
        try {
            PreparedStatement getCustomer = conn.prepareStatement(PS.getLoginID());
            getCustomer.setInt(1, dln);
            getCustomer.setString(2, lname);
            ResultSet customer = getCustomer.executeQuery();
            if (!customer.next()) {
                System.out.println("[Error] Could not fetch customer ID.\n");
            } else {
                return customer.getInt("customer_id");
            }
        } catch (SQLException e) {
            System.out.println("[Error] SQL Error");
        }
        return -1;
    }

    public void viewHistory(int cid) {
        try {
            PreparedStatement history = conn.prepareStatement(PS.getRentalHistory());
            history.setInt(1, cid);
            ResultSet rentalHistory = history.executeQuery();
            if (!rentalHistory.next()) {
                System.out.printf("[Error] No records found.\n");
            } else {
                System.out.printf("\n%-15s%-15s%-15s%-25s%-25s%-15s\n", "[Start date]", "[End date]", "[Base Rate]", "[Make]", "[Model]", "[License Plate]");
                do {
                    System.out.printf("%-15s%-15s%-15s%-25s%-25s%-15s\n", rentalHistory.getDate(3), rentalHistory.getDate(4), rentalHistory.getInt(5), rentalHistory.getString(7), rentalHistory.getString(8), rentalHistory.getString(9));
                } while (rentalHistory.next());
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not locate rental history.\n" + e);
        }
    }

    public void makeReservation(int cid) {
        String sdate = "";
        String edate = "";
        int vid = -1;
        int rate = -1;
        System.out.print("[Prompt] What date would you like your reservation to begin? [dd-Mon-yy]\n:> ");
        while (true) {
            if (checkValidDate(sdate = in.next())) {
                break;
            } else {
                System.out.print("[Error] Start date must be in the format of [dd-Mon-yy].\n:> ");
            }
        }
        System.out.print("[Prompt] What date would you like your reservation to end? [dd-Mon-yy]\n:> ");
        while (true) {
            if (checkValidDate(edate = in.next())) {
                break;
            } else {
                System.out.print("[Error] End date must be in the format of [dd-Mon-yy].\n:> ");
            }
        }
        System.out.print("[Prompt] Please select an available rental by ID.\n ");
        viewAvailableRentals();
        System.out.print("\n:> ");
        while (true) {
            try {
                vid = in.nextInt();
                try {
                    PreparedStatement selectCar = conn.prepareStatement(PS.getCar());
                    selectCar.setInt(1, vid);
                    ResultSet car = selectCar.executeQuery();
                    if (!car.next()) {
                        System.out.print("[Error] Invalid vehicle ID. Try another\n:> ");
                    } else {
                        try {
                            PreparedStatement getRate = conn.prepareStatement(PS.getRate());
                            getRate.setInt(1, vid);
                            ResultSet rr = getRate.executeQuery();
                            if (rr.next()) {
                                rate = rr.getInt(1);
                            } else {
                                rate = 150;
                            }
                        } catch (SQLException e) {
                            System.out.print("[Error] Failed to get rental rate.\n");
                            break;
                        }
                        System.out.print("[Notice] The vehicle you selected can be rented for $" + rate +"/day. Would you like to make your reservation[y/n]?\n:> ");
                        while (true) {
                            String input = in.next();
                            if (input.equalsIgnoreCase("y")) {
                                try {
                                    PreparedStatement createReservation = conn.prepareStatement(PS.addRental());
                                    createReservation.setString(1, sdate);
                                    createReservation.setString(2, edate);
                                    createReservation.setInt(3, rate);
                                    createReservation.setInt(4, cid);
                                    createReservation.setInt(5, vid);
                                    int ret = createReservation.executeUpdate();
                                    if (ret > 0) {
                                        System.out.print("[Success] Reservation placed.\n");
                                    } else {
                                        System.out.print("[Error] Reservation failed.\n");
                                    }
                                } catch (SQLException e) {
                                    System.out.print("[Error] Failed to create reservation.\n");
                                }
                                try {
                                    PreparedStatement updateStock = conn.prepareStatement(PS.updateStock());
                                    updateStock.setInt(1, vid);
                                    int upd = updateStock.executeUpdate();
                                    if (upd < 1) {
                                        System.out.print("[Error] Could not update stock.\n");
                                    }
                                } catch (SQLException e) {
                                    System.out.print("[Error] Could not update stock.\n");
                                }
                                break;
                            } else if (input.equalsIgnoreCase("n")) {
                                break;
                            } else {
                                System.out.print("[Error] Input must be [y/n]\n:> ");
                            }
                        }
                        break;
                    }
                } catch (SQLException e) {
                    System.out.print("[Error] SQL Error\n:>");
                }
            } catch (InputMismatchException e) {
                System.out.print("[Error] ID must be numeric.\n:> ");
                in.next();
            }
        }
    }

    public boolean checkValidDate(String date2) {
        try { 
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
            Date date = sdf.parse(date2);
            if (date2.equals(sdf.format(date))) return true;
        } catch (ParseException e) {return false;}
        return false;
    }

    public void viewAvailableRentals() {
        try {
            PreparedStatement getCars = conn.prepareStatement(PS.getAvailableRentals());
            ResultSet cars = getCars.executeQuery();
            if (!cars.next()) {
                System.out.print("[Error] No vehicles are available at this time.\n");
                return;
            }
            System.out.printf("\n%-15s%-25s%-25s%-15s%-15s\n", "[Vehicle ID]", "[Make]", "[Model]", "[License Plate]", "[Odometer]");
            do {
                System.out.printf("%-15s%-25s%-25s%-15s%-15s\n", cars.getInt(1), cars.getString(2), cars.getString(3), cars.getString(4), cars.getInt(6));
            } while (cars.next());

        } catch (SQLException e) {
            System.out.println("[Error] Could not fetch inventory.\n");
        }
    }

    public void removeReservation(int cid) {
        int rid = -1;
        System.out.print("[Prompt] Select a reservation to remove (By rental ID).\n");
        try {
            PreparedStatement rentals = conn.prepareStatement(PS.getReservations());
            rentals.setInt(1, cid);
            ResultSet rents = rentals.executeQuery();
            if (!rents.next()) {
                System.out.print("[Error] No reservations are available at this time.\n");
                return;
            }
            System.out.printf("\n%-15s%-15s%-15s%-15s%-25s%-25s\n", "[Rental ID]", "[Start Date]", "[End Date]", "[Base rate]", "[Make]", "[Model]");
            do {
                System.out.printf("%-15s%-15s%-15s%-15s%-25s%-25s\n", rents.getInt(2), rents.getDate(3), rents.getDate(4), rents.getInt(5), rents.getString(7), rents.getString(8));
            } while (rents.next());
        } catch (SQLException e) {
            System.out.print("[Error] Could not fetch active reservations.\n");
        }
        System.out.print(":> ");
        while (true) {
            try {
                rid = in.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.print("[Error] Rental ID must be numeric.\n:> ");
                in.next();
            }
        }
        try {
            PreparedStatement removeRental = conn.prepareStatement(PS.removeRes());
            removeRental.setInt(1, rid);
            int res = removeRental.executeUpdate();
            if (res > 0) {
                System.out.print("[Success] Reservation dropped.\n");
            } else {
                System.out.print("[Error] Reservations not found.\n");
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not remove reservation.\n");
        }
    }

    public void rentCar(int cid) {
        String sdate = "";
        String edate = "";
        int vid = -1;
        int rate = -1;
        System.out.print("[Prompt] When would you like to rent your vehicle? [dd-Mon-yy]\n:> ");
        while (true) {
            if (checkValidDate(sdate = in.next())) {
                break;
            } else {
                System.out.print("[Error] Start date must be in the format of [dd-Mon-yy].\n:> ");
            }
        }
        System.out.print("[Prompt] Expected check-out [dd-Mon-yy]\n:> ");
        while (true) {
            if (checkValidDate(edate = in.next())) {
                break;
            } else {
                System.out.print("[Error] End date must be in the format of [dd-Mon-yy].\n:> ");
            }
        }
        System.out.print("[Prompt] Please select an available rental by ID.\n:> ");
        viewAvailableRentals();
        System.out.print("\n:> ");
        while (true) {
            try {
                vid = in.nextInt();
                try {
                    PreparedStatement selectCar = conn.prepareStatement(PS.getCar());
                    selectCar.setInt(1, vid);
                    ResultSet car = selectCar.executeQuery();
                    if (!car.next()) {
                        System.out.print("[Error] Invalid vehicle ID. Try another\n:> ");
                    } else {
                        try { 
                            PreparedStatement getRate = conn.prepareStatement(PS.getRate());
                            getRate.setInt(1, vid);
                            ResultSet rr = getRate.executeQuery();
                            if (rr.next()) {
                                rate = rr.getInt(1);
                            } else {
                                rate = 150;
                            }
                        } catch (SQLException e) {
                            System.out.print("[Error] Failed to get rental rate.\n");
                            break;
                        }
                        System.out.print("[Notice] The vehicle you selected can be rented for $" + rate +"/day. Would you like to check-out [y/n]?\n:> ");
                        while (true) {
                            String input = in.next();
                            if (input.equalsIgnoreCase("y")) {
                                try {
                                    PreparedStatement createReservation = conn.prepareStatement(PS.addRental());
                                    createReservation.setString(1, sdate);
                                    createReservation.setString(2, edate);
                                    createReservation.setInt(3, rate);
                                    createReservation.setInt(4, cid);
                                    createReservation.setInt(5, vid);
                                    int ret = createReservation.executeUpdate();
                                    if (ret > 0) {
                                        System.out.print("[Success] Rental made.\n");
                                    } else {
                                        System.out.print("[Error] Reservation failed.\n");
                                    }
                                } catch (SQLException e) {
                                    System.out.print("[Error] Failed to create rental.\n");;
                                }
                                break;
                            } else if (input.equalsIgnoreCase("n")) {
                                break;
                            } else {
                                System.out.print("[Error] Input must be [y/n]\n:> ");
                            }
                        }
                        break;
                    }
                } catch (SQLException e) {
                    System.out.print("[Error] SQL Error\n:>");
                }
            } catch (InputMismatchException e) {
                System.out.print("[Error] ID must be numeric.\n:> ");
                in.next();
            }
        }
    }
    public void addPayment(int cid) {
        String type = "";
        String cardNum = "";
        int cvv = -1;
        System.out.print("[Prompt] What is your payment type?\n:> ");
        while (true) {
            type = in.next();
            if (type.matches("[a-zA-Z]+")) {
                break;
            } else {
                System.out.print("[Error] Payment type must be alphabetic.\n:> ");
            }
        }
        System.out.print("[Prompt] Input card number.\n:> " );
        while (true) {
            cardNum = in.next();
            if (cardNum.matches("[0-9]{16}")) {
                break;
            } else {
                System.out.print("[Error] Card number must be a sixteen digit numeric value.\n:> ");
            }
        }
        System.out.print("[Prompt] Input CVV.\n:> ");
        while (true) {
            try {
                cvv = in.nextInt();
                if (cvv > 99 && cvv < 1000) {
                    break;
                } else {
                    System.out.print("[Error] CVV must be a three digit numeric value.\n:> ");
                }
            } catch (InputMismatchException e) {
                System.out.print("[Error] CVV must be a three digit numeric value.\n:> ");
                in.next();
            }
        }
        try {
            PreparedStatement addCard = conn.prepareStatement(PS.addPayment());
            addCard.setString(1, type);
            addCard.setLong(2, Long.parseLong(cardNum));
            addCard.setInt(3, cvv);
            addCard.setInt(4, cid);
            int res = addCard.executeUpdate();
            if (res > 0) {
                String last4 = String.valueOf(cardNum).substring(12, 16);
                System.out.print("[Success] Card ending in " + last4 + " added to wallet.\n");
            } else {
                System.out.print("[Error] Could not add card to wallet.\n");
            }
            
        } catch (SQLException e) {
            System.out.print("[Error] Could not add card to wallet.\n");
        }
    }
}