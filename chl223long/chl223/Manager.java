import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;
import java.sql.*;

public class Manager {
    Connection conn;
    Scanner in;

    public Manager(Connection conn, Scanner in) {
        this.conn = conn;
        this.in = in;
    }

    public void managerInterface(){ 
        mainMenu();
    }

    public void mainMenu() {
        System.out.print("\n<----->\n\n");
        System.out.print("Please select an operation:");
        while (true) {
            System.out.print( "\n[+] Add a vehicle.\n" + 
                                "[#] Move a vehicle.\n" +
                                "[*] View unprocessed charges.\n" +
                                "[%] Process charges.\n" +
                                "[q] Quit to interface selection.\n:> ");
        
            String input = in.next();
            if (input.equals("+")) addVehicle();
            else if (input.equals("#")) moveVehicle();
            else if (input.equals("*")) viewCharges();
            else if (input.equals("%")) processCharges();
            else if (input.equals("q")) break;
            else System.out.print("[Error] Please input a valid operation.\n");
        }
    }
    
    public void addVehicle() {
        List<Integer> locs = new ArrayList<Integer>();
        String make = "";
        String model = "";
        String plate = "";
        String type = "";
        int odometer = -1;
        int location = -1;
        String vin = "";

        System.out.print("\n[Prompt] Specify vehicle make.\n:> ");
        in.nextLine();
        make = in.next();
        System.out.print("\n[Prompt] Specify vehicle model.\n:> ");
        in.nextLine();
        model = in.next();
        System.out.print("\n[Prompt] What is the license plate value? [xxxx-xxx]\n:> ");
        while (true) {
            plate = in.next();
            if (plate.matches("[A-Za-z0-9]{4}-[A-Za-z0-9]{3}")) {
                break;
            } else {
                System.out.print("[Error] License plate must be in format [xxxx-xxx].\n:> ");
            }
        }
        System.out.print("\n[Prompt] Specify vehicle type.[SUV/Truck/Sedan/Coupe]\n:> ");
        while (true) {
            type = in.next();
            if (type.equalsIgnoreCase("SUV") || type.equalsIgnoreCase("Truck") || type.equalsIgnoreCase("Sedan") || type.equalsIgnoreCase("Coupe")) {
                break;
            } else {
                System.out.print("[Error] Must choose from the vehicle types [SUV/Truck/Sedan/Coupe].\n:> ");
            }
        }
        System.out.print("\n[Prompt] What is the odometer reading?\n:> ");
        while (true) {
            try {
                odometer = in.nextInt();
                break;
            } catch (InputMismatchException e) {
                System.out.print("[Error] Odometer input must be an integer!\n:> ");
                in.next();
            }
        }
        System.out.print("\n[Prompt] Select the location that the vehicle is stored at by location ID.");
        try {
            PreparedStatement locations = conn.prepareStatement(PS.getInventory());
            ResultSet allLocations = locations.executeQuery();
            if (!allLocations.next()) {
                System.out.print("[Error] Locations not available.\n");
            } else {
                System.out.printf("\n%-15s%-25s%-15s\n", "[Location ID]", "[City]", "[State]");
                do {
                    locs.add(allLocations.getInt(1));
                    System.out.printf("%-15s%-25s%-15s\n", allLocations.getInt(1), allLocations.getString(2), allLocations.getString(3));
                } while (allLocations.next());
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not fetch locations.\n");
        }
        System.out.print(":> ");
        while (true) {
            try {
                location = in.nextInt();
                if (locs.contains(location)) {
                    break;
                } else {
                    System.out.print("[Error] Location not found, input a valid location ID.\n:> ");
                }
            } catch (InputMismatchException e) {
                System.out.print("[Error] Location ID must be an integer type.\n:> ");
                in.next();
            }
        }
        System.out.print("\n[Prompt] Input the vehicle's VIN.\n:> ");

        while (true) {
            vin = in.next();
            if (vin.matches("[A-za-z0-9]{17}")) {
                break;
            } else {
                System.out.print("[Error] VIN must be a 17 digit alphanumeric value.\n:> ");
            }
        }
        try {
            PreparedStatement addFuel = conn.prepareStatement(PS.insertFuel(), new String[]{"fuel_id"});
            addFuel.setInt(1, 15);
            int res = addFuel.executeUpdate();
            if (res < 1) {
                System.out.print("[Error] Could not create fuel row.\n");
            } else {
                ResultSet fid = addFuel.getGeneratedKeys();
                fid.next();
                int fuelID = fid.getInt(1);
                try {
                    PreparedStatement addVehicle = conn.prepareStatement(PS.insertVehicle());
                    addVehicle.setString(1, make);
                    addVehicle.setString(2, model);
                    addVehicle.setString(3, plate);
                    addVehicle.setString(4, type);
                    addVehicle.setInt(5, odometer);
                    addVehicle.setInt(6, fuelID);
                    addVehicle.setInt(7, location);
                    addVehicle.setString(8, vin);
                    int added = addVehicle.executeUpdate();
                    if (added < 1) {
                        System.out.print("[Error] Could not create vehicle.\n");
                    } else {
                        System.out.print("\n[Notice] Vehicle successfully added!\n");
                    }
                } catch (SQLException e) {
                    System.out.print("[] Could not insert vehicle.\n");
                }
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not insert fuel.\n");
        }
    }

    public void moveVehicle() {
        int lid = -1;
        int vid = -1;
        List<Integer> locationid = new ArrayList<Integer>();
        System.out.print("\n[Prompt] Select the location of the car you would like to move by ID.");
        try {
            PreparedStatement getLocation = conn.prepareStatement(PS.getInventory());
            ResultSet locations = getLocation.executeQuery();
            if (!locations.next()) {
                System.out.print("[Error] Locations not available.\n");
            } else {
                System.out.printf("\n%-15s%-25s%-15s\n", "[Location ID]", "[City]", "[State]");
                do {
                    locationid.add(locations.getInt(1));
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
                if (locationid.contains(lid)) break;
                else System.out.print("[Error] Invalid location ID.\n:> ");
            } catch (InputMismatchException e) {
                System.out.print("[Error] Location ID must be numeric.\n:> ");
                in.next();
            }
        }
        List<Integer> vehicleid = new ArrayList<Integer>();
        System.out.print("\n[Prompt] Select a vehicle by ID.");
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
                    vehicleid.add(localInventory.getInt(1));
                    System.out.printf("%-15s%-25s%-25s%-15s%-15s%-15s\n", localInventory.getInt(1), localInventory.getString(4), localInventory.getString(5), localInventory.getString(6), localInventory.getInt(7), localInventory.getInt(8));
                } while (localInventory.next());
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not fetch local inventory.\n");
        }
        System.out.print(":> ");
        while (true) {
            try {
                vid = in.nextInt();
                if (vehicleid.contains(vid)) break;
                else System.out.print("[Error] Invalid vehicle ID.\n:> ");
            } catch (InputMismatchException e) {
                System.out.print("[Error] Vehicle ID must be numeric.\n:> ");
                in.next();
            }
        }
        System.out.print("[Prompt] Where would you like to move the vehicle to?\n:> ");
        while (true) {
            try {
                lid = in.nextInt();
                if (locationid.contains(lid)) break;
                else System.out.print("[Error] Invalid location ID.\n:> ");
            } catch (InputMismatchException e) {
                System.out.print("[Error] Vehicle ID must be numeric.\n:> ");
                in.next();
            }
        }
        try {
            PreparedStatement updateLocation = conn.prepareStatement(PS.updateLocation());
            updateLocation.setInt(1, lid);
            updateLocation.setInt(2, vid);
            int ret = updateLocation.executeUpdate();
            if (ret < 1) {
                System.out.print("[Error] Could not update vehicle location.\n");
            } else {
                System.out.print("\n[Success] Vehicle moved!\n");
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not update vehicle location.\n");
        } 



        
    }

    public int getBalance(int cid) {
        int bal = 0;
        int mid = -1;
        int iid = -1;
        int did = -1;
        int fid = -1;
        try {
            PreparedStatement getIDs = conn.prepareStatement(PS.getIDs());
            getIDs.setInt(1, cid);
            ResultSet IDs = getIDs.executeQuery();
            if (!IDs.next()) {
                System.out.print("[Error] No IDs available for this charge ID.\n");
            } else {
                mid = IDs.getInt(3);
                try {
                    PreparedStatement getCharge = conn.prepareStatement(PS.getCharge("misc", "miscellaneous_id"));
                    getCharge.setInt(1, mid);
                    ResultSet mCharge = getCharge.executeQuery();
                    if (mCharge.next()) {
                        if (mCharge.getInt(2) == 1) {
                            bal += 40;
                        }
                        if (mCharge.getInt(3) == 1) {
                            bal += 150;
                        }
                        if (mCharge.getInt(4) == 1) {
                            bal += 50;
                        }
                    }
                } catch (SQLException e) {}
                iid = IDs.getInt(4);
                try {
                    PreparedStatement getCharge = conn.prepareStatement(PS.getCharge("insurance", "insurance_id"));
                    getCharge.setInt(1, iid);
                    ResultSet iCharge = getCharge.executeQuery();
                    if (iCharge.next()) {
                        bal += iCharge.getInt(2);
                    }
                } catch (SQLException e) {}
                did = IDs.getInt(5);
                try {
                    PreparedStatement getCharge = conn.prepareStatement(PS.getCharge("dropoff", "dropoff_id"));
                    getCharge.setInt(1, did);
                    ResultSet dCharge = getCharge.executeQuery();
                    if (dCharge.next()) {
                        bal += dCharge.getInt(2);
                    }
                } catch (SQLException e) {}
                fid = IDs.getInt(6);
                try {
                    PreparedStatement getCharge = conn.prepareStatement(PS.getCharge("fuel", "fuel_id"));
                    getCharge.setInt(1, fid);
                    ResultSet fCharge = getCharge.executeQuery();
                    if (fCharge.next()) {
                        bal += (15 - fCharge.getInt(2)) * 12;
                    }
                } catch (SQLException e) {}
                return bal;
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not retrive IDs.\n");
        }
        return bal;
    }

    public HashMap<Integer, Integer> viewCharges() {
        HashMap<Integer, Integer> ret = new HashMap<>();
        try {
            PreparedStatement viewCharges = conn.prepareStatement(PS.getUnprocessedCharges());
            ResultSet unpaid = viewCharges.executeQuery();
            if (!unpaid.next()) {
                System.out.print("[Error] No unpaid charges found.\n");
            } else {
                System.out.printf("\n%-20s%-20s%-20s%-20s%-20s%-20s\n", "[Charge ID]", "[First Name]", "[Last Name]", "[Rental Start]", "[Rental End]", "[Unpaid balance]");
                do {
                    int bal = getBalance(unpaid.getInt(3));
                    ret.put(unpaid.getInt(3), bal);
                    System.out.printf("%-20s%-20s%-20s%-20s%-20s%-20s\n", unpaid.getInt(3), unpaid.getString(12), unpaid.getString(13), unpaid.getDate(18), unpaid.getDate(19), bal);
                } while (unpaid.next());
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not retrieve charges");
        }
        return ret;
    }

    public void processCharges() {
        int i1 = -1;
        System.out.print("\n[Prompt] Select a charge to process (by charge ID).");
        HashMap<Integer, Integer> cid = viewCharges();
        System.out.print(":> ");
        while (true) {
            try {
                i1 = in.nextInt();
                if (cid.containsKey(i1)) {
                    updateCharges(i1, cid.get(i1)); 
                    break;
                }
                else System.out.print("[Error] Charge ID not found.\n:> ");
            } catch (InputMismatchException e) {
                System.out.print("[Error] Charge ID must be numeric.\n:> ");
                in.next();
            }
        }
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
                            "\nBILL TO: \n\t" 
                            + res.getString(1) + " " + res.getString(2) + "\n\t" +
                            res.getString(8) + "\n\t" + 
                            res.getString(3) + 
                            "\n-------------------------------------------------------------------------" + "\n" + 
                            "\t\tSubtotal: $" + bal + "\n" +
                            "\t\t\tBase Rate: $" + res.getInt(10) + "\n" +
                            "\t\t\tAdditional Charges: $" + res.getInt(7) + "\n" +
                            "\t\tMembership Discount (" + res.getInt(9) + "%): " + "-$" + Math.round(dis/100*100) + "\n" +
                            "\n-------------------------------------------------------------------------" + "\n" + 
                            "\t\t\t\t\t\t\tBalance Due: $" + Math.round((bal-dis)/100 * 100) + "\n");
        } catch (SQLException e) {
            System.out.print("[Error] Could not fetch invoice.\n");
        }
    }

    public int updateCharges(int cid, int charge) {
        try {
            PreparedStatement apply = conn.prepareStatement(PS.updateCharge());
            apply.setInt(1, charge);
            apply.setInt(2, cid);
            int update = apply.executeUpdate();
            if (update < 1) {
                return -1;
            } else {
                invoice(cid);
                return cid;
            }
        } catch (SQLException e) {
            System.out.print("[Error] Could not apply charges.\n");
        }
        return -1;
    }
}
