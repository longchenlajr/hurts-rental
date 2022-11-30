import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Types;


public class Staff {
    Connection conn;
    Scanner in;

    public Staff(Connection conn, Scanner in) {
        this.conn = conn;
        this.in = in;
    }

    public void staffInterface(){ 
        mainMenu();
    }

    public void mainMenu() {
        System.out.print("\n<----->\n\n");
        System.out.print("Please select an operation:");
        while (true) {
            System.out.print( "\n[#] View inventory.\n" + 
                                "[+] Create rental. \n" +
                                "[-] Check-in rental.\n" +
                                "[%] Create a customer account.\n" +
                                "[q] Quit to interface selection.\n:> ");
        
            String input = in.next();
            if (input.equals("#")) viewInventory();
            else if (input.equals("+")) createRental();
            else if (input.equals("-")) checkIn();
            else if (input.equals("%")) createAccount();
            else if (input.equals("q")) break;
            else System.out.print("[Error] Please input a valid operation.\n");
        }
    }
    public void viewInventory() {
        int lid = -1;
        System.out.print("\n[Prompt] Input the location ID of the inventory you want to check. (0 for entire inventory)");
        try {
            PreparedStatement getLocation = conn.prepareStatement(PS.getInventory());
            ResultSet locations = getLocation.executeQuery();
            if (!locations.next()) {
                System.out.print("[Error] Locations not available.\n");
            } else {
                System.out.printf("\n%-15s%-25s%-15s\n", "[Location ID]", "[City]", "[State]");
                do {
                    System.out.printf("%-15s%-25s%-15s\n", locations.getInt(1), locations.getString(2), locations.getString(3));
                } while (locations.next());
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not fetch location list.\n");
        }
        System.out.print(":> ");
        while (true) {
            try {
                lid = in.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.print("[Error] Location ID must be numeric.\n:> ");
                in.next();
            }
        }
        if (lid == 0) {
            try {
                PreparedStatement getAll = conn.prepareStatement(PS.getInventoryAll());
                ResultSet fullInventory = getAll.executeQuery();
                if (!fullInventory.next()) {
                    System.out.print("[Error] No vehicles in inventory found.\n");
                } else {
                    System.out.printf("\n%-15s%-25s%-15s%-25s%-25s%-15s%-15s%-15s\n", "[Vehicle ID]", "[City]", "[State]", "[Make]", "[Model]", "[Plate]", "[Odometer]", "[Reserved]");
                    do {
                        System.out.printf("%-15s%-25s%-15s%-25s%-25s%-15s%-15s%-15s\n", fullInventory.getInt(1), fullInventory.getString(2), fullInventory.getString(3), fullInventory.getString(4), fullInventory.getString(5), fullInventory.getString(6), fullInventory.getInt(7), fullInventory.getInt(8));
                    } while (fullInventory.next());
                }
            } catch (SQLException e) {
                System.out.print("[Error] Could not fetch total inventory.\n");
            }
        } else {
            try {
                PreparedStatement getLocalInventory = conn.prepareStatement(PS.getInventoryByID());
                getLocalInventory.setInt(1, lid);
                ResultSet localInventory = getLocalInventory.executeQuery();
                if (!localInventory.next()) {
                    System.out.print("[Error] Invalid ID or no inventory found.\n");
                } else {
                    String city = localInventory.getString(2);
                    String state = localInventory.getString(3);
                    System.out.print("\n[Notice] Local inventory for " + city + ", " + state + ".");
                    System.out.printf("\n%-15s%-25s%-25s%-15s%-15s%-15s\n", "[Vehicle ID]", "[Make]", "[Model]", "[Plate]", "[Odometer]", "[Reserved]");
                    do {
                        System.out.printf("%-15s%-25s%-25s%-15s%-15s%-15s\n", localInventory.getInt(1), localInventory.getString(4), localInventory.getString(5), localInventory.getString(6), localInventory.getInt(7), localInventory.getInt(8));
                    } while (localInventory.next());
                }
            } catch (SQLException e) {
                System.out.print("[Error] Could not fetch local inventory.\n");
            }
        }
    }

    public int createAccount() {
        String fname = "";
        String lname = "";
        String addy = "";
        int dln = -1;
        int mid = -1;
        System.out.print("\n[Prompt] Input customer first name.\n:> ");
        while (true) {
            fname = in.next();
            if (fname.matches("[a-zA-Z]+")) {
                break;
            }
            System.out.print("[Error] First name must be alphabetic, try again.\n:> ");
        }
        System.out.print("\n[Prompt] Input customer last name.\n:> ");
        while (true) {
            lname = in.next();
            if (lname.matches("[a-zA-Z]+")) {
                break;
            }
            System.out.print("[Error] Last name must be alphabetic, try again.\n:> ");
        }
        System.out.print("\n[Prompt] Input customer address\n:> ");
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
        System.out.print("\n[Prompt] Does the customer have a membership? [y/n]\n:> ");
        while (true) {
            String input = in.next();
            if (input.equalsIgnoreCase("y")) {
                System.out.print("\n[Prompt] Input membership number.\n:> ");
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
                        in.next();
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

    public void createRental() {
        String sdate = "";
        String edate = "";
        int vid = -1;
        int rate = -1;
        int cid = getCustomerID();
        if (cid < 0) {
            return;
        }
        System.out.print("\n[Prompt] When does the rental period begin? [dd-Mon-yy]\n:> ");
        while (true) {
            if (checkValidDate(sdate = in.next())) {
                break;
            } else {
                System.out.print("[Error] Start date must be in the format of [dd-Mon-yy].\n:> ");
            }
        }
        System.out.print("\n[Prompt] Expected check-out [dd-Mon-yy]\n:> ");
        while (true) {
            if (checkValidDate(edate = in.next())) {
                break;
            } else {
                System.out.print("[Error] End date must be in the format of [dd-Mon-yy].\n:> ");
            }
        }
        System.out.print("\n[Prompt] Select an available rental by ID.\n:> ");
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
                        System.out.print("\n[Notice] The selected vehicle can be rented for $" + rate +"/day. Please confirm with customer if they would like to complete the rental. [y/n]?\n:> ");
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

    public void checkIn() {
        int cid = getCustomerID();
        int[] fid = getFuelID(cid);
        updateOdometerRes(fid[0]);
        int[] mid = getMiscID();
        int[] iid = getInsuranceID();
        int[] did = getDropoffID();
        int pid = getPaymentID(cid);
        if (pid == -1) {
            System.out.print("\n[Prompt] Add a payment method (or quit to selection menu)? [y/n]\n:> ");
            while (true) {
                String i1 = in.next();
                if (i1.equalsIgnoreCase("y")) {
                    pid = createPayment(cid);
                } else if (i1.equalsIgnoreCase("n")) {
                    return;
                } else {
                    System.out.print("[Error] Input must be [y/n].\n");
                }
            }
        }
        int charge = fid[1] + mid[1] + iid[1] + did[1];
        int chargesID = applyCharges(charge, mid[0], iid[0], did[0], fid[0], pid);
        if (chargesID > 0) {
            invoice(chargesID);
        }
    }

    public int[] getFuelID(int cid) {
        List<Integer> ll = new ArrayList<Integer>();
        int[] ret = {0, 0};
        int remFuel = -1;
        System.out.print("\n[Prompt] Select a rental to check-in.\n");
        try {
            PreparedStatement ps = conn.prepareStatement(PS.getOpenRentals());
            ps.setInt(1, cid);
            ResultSet activeRentals = ps.executeQuery();
            if (!activeRentals.next()) {
                System.out.print("[Error] User has no active rentals.\n");
            } else {
                System.out.printf("\n%-20s%-20s%-20s%-20s%-20s%-20s%-20s%-20s%-25s\n", "[Vehicle ID]", "[Rental Start]", "[Rental End]", "[Make]", "[Model]", "[Plate]", "[Odometer Reading]", "[City]", "[State]");
                do {
                    ll.add(activeRentals.getInt(2));
                    System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s%-20s%-20s%-25s\n", activeRentals.getInt(2), activeRentals.getString(4).substring(0, 10), activeRentals.getString(5).substring(0, 10), activeRentals.getString(8), activeRentals.getString(9), activeRentals.getString(10), activeRentals.getInt(12), activeRentals.getString(18), activeRentals.getString(19));
                } while (activeRentals.next());
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not retrieve rentals.\n");
            return ret;
        }
        System.out.print(":> ");
        Boolean boo = true;
        while (boo) {
            try {
                ret[0] = in.nextInt();
                if (ll.contains(ret[0])) {
                    break;
                } else {
                    System.out.print("[Error] Vehicle ID not found.\n:> ");
                }
            } catch (InputMismatchException e) {
                System.out.print("[Error] Vehicle ID must be an integer.\n:> ");
                in.next();
            }
        }
        System.out.print("\n[Prompt] Input vehicle's remaining fuel\n:> ");
        while (true) {
            try {
                remFuel = in.nextInt();
                if (remFuel > 16 || remFuel < 0) {
                    System.out.print("[Error] Input must be between a full tank (15g) and an empty tank (0g).\n:> ");
                } else {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.print("[Error] Input must be an integer.\n:> ");
                in.next();
            }
        }
        ret[1] = (15 - remFuel) * 12;
        try {
            PreparedStatement ff = conn.prepareStatement(PS.updateFuel());
            ff.setInt(1, remFuel);
            ff.setInt(2, ret[0]);
            int res = ff.executeUpdate();
            if (res <= 0) {
                System.out.print("[Error] Fuel was unable to be updated.\n");
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not update remaining fuel.\n");
        }
        return ret;
    }

    public int getCustomerID() {
        int uid = -1;
        String lname = "";
        System.out.print("\n[Prompt] Please enter the customer ID. \n:> ");
        while (true) {
            try {
                uid = in.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.print("[Error] User ID must be numeric. Try again\n:> ");
                in.next();
            }
        }
        System.out.print("\n[Prompt] Please enter the customer's last name.\n:> ");
        lname = in.next();
        String user = PS.getLogin();
        try {
            PreparedStatement selectUser = conn.prepareStatement(user);
            selectUser.setInt(1, uid);
            selectUser.setString(2, lname);
            ResultSet result = selectUser.executeQuery();

            if (!result.next()) {
                System.out.print("[Error] User not found.\n" + 
                                    "\n[Prompt] Create a new account? (or exit to main menu) [y/n]\n:> ");
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
                System.out.print("[Success] Successfully logged in " + name + "!\n");
                return uid;
            }
        } catch (SQLException e) {
            System.out.println("[Error] SQL Error: " + e);
        }
        return -1;
    }

    public int[] getMiscID() {
        int[] ret = {0, 0};
        int cseat = -1;
        int nav = -1;
        int radio = -1;
        System.out.print("\n[Prompt] Was a child seat rented out? [y/n]\n:> ");
        while (true) {
            String input = in.next();
            if (input.equalsIgnoreCase("y")) {
                cseat = 1;
                ret[1] += 40;
                break;
            } else if (input.equalsIgnoreCase("n")) {
                cseat = 0;
                break;
            } else {
                System.out.print("[Error] Input must be [y/n].\n:> ");
            }
        }
        System.out.print("\n[Prompt] Was an external navigator needed? [y/n]\n:> ");
        while (true) {
            String input = in.next();
            if (input.equalsIgnoreCase("y")) {
                nav = 1;
                ret[1] += 150;
                break;
            } else if (input.equalsIgnoreCase("n")) {
                nav = 0;
                break;
            } else {
                System.out.print("[Error] Input must be [y/n].\n:> ");
            }
        }        
        System.out.print("\n[Prompt] Was a radio rented out? [y/n]\n:> ");
        while (true) {
            String input = in.next();
            if (input.equalsIgnoreCase("y")) {
                radio = 1;
                ret[1] += 50;
                break;
            } else if (input.equalsIgnoreCase("n")) {
                radio = 0;
                break;
            } else {
                System.out.print("[Error] Input must be [y/n].\n:> ");
            }
        }
        try {
            PreparedStatement addMisc = conn.prepareStatement(PS.addMisc(), new String[] {"miscellaneous_id"});
            addMisc.setInt(1, cseat);
            addMisc.setInt(2, nav);
            addMisc.setInt(3, radio);
            int add = addMisc.executeUpdate();
            if (add < 0) {
                return ret;
            }
            ResultSet miscAdd = addMisc.getGeneratedKeys();
            miscAdd.next();
            ret[0] = miscAdd.getInt(1);
            return ret;
        } catch (SQLException e) {
            System.out.print("[Error] Could not create misc charges row.\n");
        }
        return null;
    }

    public int[] getInsuranceID() {
        int[] ret = {0, 0};
        String i2 = "";
        String i3 = "";
        System.out.print("\n[Prompt] Was any insurance required? [y/n]\n:> ");
        while (true) {
            String input = in.next();
            if (input.equalsIgnoreCase("y")) {
                System.out.print("\n[Prompt] What type? [collision/theft/accident/personal/q]\n:> ");
                while (true) {
                    i2 = in.next();
                    if (i2.equalsIgnoreCase("collision") || i2.equalsIgnoreCase("theft") || i2.equalsIgnoreCase("accident") || i2.equalsIgnoreCase("personal")) {
                        break;
                    } else if (i2.equalsIgnoreCase("q")) {
                        return ret;
                    } else {
                        System.out.print("[Error] Input must be [collision/theft/accident/personal/q].\n:> ");
                    }
                }
                System.out.print("\n[Prompt] Choose a charge period. [hourly/daily/weekly/q]\n:> ");
                while (true) {
                    i3 = in.next();
                    if (i3.equalsIgnoreCase("hourly") || i3.equalsIgnoreCase("daily") || i3.equalsIgnoreCase("weekly")) {
                        break;
                    } else if (i3.equalsIgnoreCase("q")) {
                        return ret;
                    } else {
                        System.out.print("[Error] Input must be [hourly/daily/weekly/q].\n:> ");
                    }
                }
                try {
                    PreparedStatement getInsurance = conn.prepareStatement(PS.getInsuranceRate());
                    getInsurance.setString(1, i2);
                    getInsurance.setString(2, i3);
                    ResultSet insuranceRate = getInsurance.executeQuery();
                    if (insuranceRate.next()) {
                        ret[0] = insuranceRate.getInt(1);
                        ret[1] = insuranceRate.getInt(2);
                    } else {
                        System.out.print("[Error] Could not fetch insurance rate.\n");
                    }
                    return ret;
                } catch (SQLException e) {
                    System.out.print("[Error] Could not fetch insurance rate.\n");
                    return null;
                }
            } else if (input.equalsIgnoreCase("n")) {
                return ret;
            } else {
                System.out.print("[Error] Input must be [y/n].\n:> ");
            }
        }
    }

    public int[] getDropoffID() {
        int[] ret = {0, 0};
        int miles = -1;
        int mpg = -1;
        double price = -1.0;
        int dropoffCost = -1;
        System.out.print("\n[Prompt] Is the dropoff ocurring at the same location it was rented from? [y/n]\n:> ");
        while (true) {
            String input = in.next();
            if (input.equalsIgnoreCase("n")) {
                System.out.print("\n[Prompt] Input the distance (in miles) from the original location.\n:> ");
                while (true) {
                    try {
                        miles = in.nextInt();
                        if (miles > 0) {
                            break;
                        } else {
                            System.out.print("[Error] Distance must be greater than 0.\n:> ");
                        }
                    } catch (InputMismatchException e) {
                        System.out.print("[Error] Distance must be numeric.\n:> ");
                        in.next();
                    }
                }
                System.out.print("\n[Prompt] How many miles does the vehicle get per gallon?\n:> ");
                while (true) {
                    try {
                        mpg = in.nextInt();
                        if (mpg > 0) {
                            break;
                        } else {
                            System.out.print("[Error] M/g must be greater than 0.\n:> ");
                        }
                    } catch (InputMismatchException e) {
                        System.out.print("[Error] Distance must be numeric.\n:> ");
                        in.next();
                    }
                }
                System.out.print("\n[Prompt] Input current cost per gallon of gasoline.\n:> ");
                while (true) {
                    try {
                        price = in.nextDouble();
                        if (price > 0) {
                            break;
                        } else {
                            System.out.print("[Error] $/g must be greater than 0.\n:> ");
                        }
                    } catch (InputMismatchException e) {
                        System.out.print("[Error] Distance must be numeric.\n:> ");
                        in.next();
                    }
                }
                ret[1] = (int)Math.round(miles / mpg * price);
                break;
            } else if (input.equalsIgnoreCase("y")) {
                ret[1] = 0;
                break;
            } else {
                System.out.print("[Error] Input must be [y/n].\n:> ");
            }
        }
        try {
            PreparedStatement addMisc = conn.prepareStatement(PS.addDropoff(), new String[]{"dropoff_id"});
            addMisc.setInt(1, dropoffCost);
            int upd = addMisc.executeUpdate();
            if (upd < 0) {
                System.out.print("[Error] Could not retrieve dropoff ID.\n");
                return ret;
            }
            ResultSet doAdd = addMisc.getGeneratedKeys();
            doAdd.next();
            ret[0] = doAdd.getInt(1);
            return ret;
        } catch (SQLException e) {
            System.out.print("[Error] Could not create dropoff charges row.\n");
        }
        return ret;
    }

    public int getPaymentID(int cid) {
        int pid = -1;
        List<Integer> ids = getPayments(cid);
        if (ids == null) {
            return -1;
        }
        System.out.print("\n[Prompt] Select the preferred payment method (By payment ID).\n:> ");
        while (true) {
            try {
                pid = in.nextInt();
                if (ids.contains(pid)) {
                    return pid;
                }
                System.out.print("[Error] Invalid payment ID.\n:> ");
            } catch (InputMismatchException e) {
                System.out.print("[Error] Payment ID must be an integer type.\n:> ");
                in.next();
            }
        }
    }

    public List<Integer> getPayments(int cid) {
        List<Integer> ret = new ArrayList<Integer>();
        try {
            PreparedStatement payments = conn.prepareStatement(PS.getPayments());
            payments.setInt(1, cid);
            ResultSet allPayments = payments.executeQuery();
            if (!allPayments.next()) {
                System.out.print("[Error] User has no available payment methods.\n");
                return null;
            }
            System.out.printf("\n%-15s%-15s%-20s\n" , "[Payment ID]", "[Type]", "[Card number]");
            do {
                ret.add(allPayments.getInt(1));
                System.out.printf("%-15s%-15s%-20s\n", allPayments.getInt(1), allPayments.getString(2), ("****-****-****-" + allPayments.getString(3).substring(12, 16)));
            } while (allPayments.next());

        } catch (SQLException e) {
            System.out.print("[Error] Could not fetch payments.\n");
        }
        return ret;
    }

    public int createPayment(int cid) {
        String type = "";
        String cardNum = "";
        int cvv = -1;
        System.out.print("\n[Prompt] What is your payment type?\n:> ");
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
            PreparedStatement addCard = conn.prepareStatement(PS.addPayment(), new String[]{"payment_id"});
            addCard.setString(1, type);
            addCard.setLong(2, Long.parseLong(cardNum));
            addCard.setInt(3, cvv);
            addCard.setInt(4, cid);
            int res = addCard.executeUpdate();
            if (res > 0) {
                String last4 = String.valueOf(cardNum).substring(12, 16);
                System.out.print("[Success] Card ending in " + last4 + " added to wallet.\n");
                ResultSet getPid = addCard.getGeneratedKeys();
                getPid.next();
                return getPid.getInt(1);
            } else {
                System.out.print("[Error] Could not add card to wallet.\n");
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not add card to wallet.\n");
        }
        return -1;
    }

    public int applyCharges(int charge, int mid, int iid, int did, int fid, int pid) {
        try {
            PreparedStatement apply = conn.prepareStatement(PS.insertCharges(), new String[]{"charges_id"});
            apply.setInt(1, charge);
            if (mid == 0) {
                apply.setNull(2, Types.NULL);
            } else {
                apply.setInt(2, mid);
            }
            if (iid == 0) {
                apply.setNull(3, Types.NULL);
            } else {
                apply.setInt(3, iid);
            }
            apply.setInt(4, did);
            apply.setInt(5, fid);
            apply.setInt(6, pid);
            int res = apply.executeUpdate();
            if (res > 0) {
                ResultSet chargesID = apply.getGeneratedKeys();
                chargesID.next();
                return chargesID.getInt(1);
            } else {
                System.out.print("[Error] Could not apply charges.\n");
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not apply charges.\n");
        }
        return -1;
    }

    public void invoice(int cid) {
        try {
            PreparedStatement getInvoice = conn.prepareStatement(PS.getInvoice());
            getInvoice.setInt(1, cid);
            ResultSet res = getInvoice.executeQuery();
            if (!res.next()) {
                System.out.print("[Error] Could not locate charge ID");
                return;
            }
            double dis = (res.getDouble(7) + res.getDouble(10)) * (res.getDouble(9)/100);
            double bal = (res.getDouble(7) + res.getDouble(10));
            System.out.print("\n[Notice] Displaying invoice..."+
                            "\nBILL TO: \n" + res.getString(1) + " " + res.getString(2) + "\n" +
                            res.getString(8) + "\n" + 
                            res.getString(3) + 
                            "\n-------------------------------------------------------------------------" + "\n" + 
                            "\t\tSubtotal:\n" +
                            "\t\t\tBase Rate: $"+ res.getInt(10) + "\n" +
                            "\t\t\tAdditional Charges: $" + res.getInt(7) + "\n" +
                            "\t\tMembership Discount (" + res.getInt(9) + "%): " + "-$" + dis + "\n" +
                            "\t\tBalance Due: $" + (bal-dis) + "\n");

        } catch (SQLException e) {
            System.out.print("[Error] Could not fetch invoice.\n");
        }
    }

    public void updateOdometerRes(int fid) {
        int reading = -1;
        int i1 = -1;
        try {
            PreparedStatement getOdo = conn.prepareStatement(PS.getOdo());
            getOdo.setInt(1, fid);
            ResultSet odometer = getOdo.executeQuery();
            if (odometer.next()) {
                reading = odometer.getInt(1);
            } else {
                System.out.print("[Error] No odometer reading found.\n");
                return;
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not retrieve odometer reading.\n");
        }

        System.out.print("\n[Prompt] What is the updated odometer reading?\n:> ");
        while (true) {
            try {
                i1 = in.nextInt();
                if (i1 == reading) System.out.print("[Error] Updated reading cannot match stored reading.\n:> ");
                else if (i1 < reading) System.out.print("[Error] Updated reading cannot be less than the stored reading.\n:> ");
                else break;
            } catch (InputMismatchException e) {
                System.out.print("[Error] Odometer reading must be an integer.\n:> ");
                in.next();
            }
        }
        try {
            PreparedStatement odr = conn.prepareStatement(PS.setOdoRes());
            odr.setInt(1, i1);
            odr.setInt(2, fid);
            int res = odr.executeUpdate();
            if (res < 1) System.out.print("[Error] Odometer reading failed to update.\n");
        } catch (SQLException e) {
            System.out.print("[Error] Could not update odometer reading.\n");
        }
    }
}