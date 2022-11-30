public class PS {    
    
    public static String getAll() {
        return "SELECT * FROM all_tables WHERE OWNER = 'CHL223';";
    }

    public static String getLogin() {
        return "SELECT * FROM customer WHERE customer_id = ? AND l_name = ?";
    }

    public static String getMembership() {
        return "SELECT membership_id FROM membership WHERE m_num = ?";
    }

    public static String addCustomer() {
        return "INSERT INTO customer VALUES(DEFAULT, ?, ?, ?, ?, ?)";
    }

    public static String getLoginID() {
        return "SELECT * FROM customer WHERE DLN = ? AND l_name = ?";
    }

    public static String getRentalHistory() {
        return "SELECT * FROM rental NATURAL JOIN vehicle WHERE customer_id = ?";
    }

    public static String getAvailableRentals() {
        return "SELECT * FROM vehicle WHERE reserved = 0 AND instock = 1";
    }

    public static String addPayment() {
        return "INSERT INTO payment VALUES(DEFAULT, ?, ?, ?, ?)";
    }

    public static String getCar()  {
        return "SELECT * FROM vehicle WHERE reserved = 0 AND instock = 1 AND vehicle_id = ?";
    }

    public static String updateRes() {
        return "UPDATE vehicle SET RESERVED = 1. INSTOCK = 0 WHERE vehicle_id = ?";   
    }

    public static String getRate() {
        return "SELECT base_rate FROM rental NATURAL JOIN vehicle WHERE vehicle_id = ?";
    }

    public static String addRental() {
        return "INSERT INTO RENTAL values(DEFAULT, ?, ?, ?, ?, ?)";
    }

    public static String getReservations() {
        return "SELECT * FROM rental NATURAL JOIN vehicle WHERE customer_id = ? AND reserved = 1";
    }

    public static String removeRes() {
        return "UPDATE vehicle SET RESERVED = 0 WHERE vehicle_id = ?";
    }

    public static String getInventory() {
        return "SELECT location_id, city, state FROM location";
    }

    public static String getInventoryByID() {
        return "SELECT vehicle_id, city, state, make, model, plate, odometer, reserved FROM vehicle NATURAL JOIN location WHERE instock = 1 AND location_id = ?";
    }

    public static String getInventoryAll() {
        return "SELECT vehicle_id, city, state, make, model, plate, odometer, reserved FROM vehicle NATURAL JOIN location WHERE instock = 1";
    }

    public static String addMisc() {
        return "INSERT INTO misc VALUES(DEFAULT, ?, ?, ?)";
    }

    public static String addDropoff() {
        return "INSERT INTO dropoff VALUES(DEFAULT, ?)";
    }

    public static String getPayments() {
        return "SELECT * FROM payment WHERE customer_id = ?";
    }

    public static String getInsuranceRate() {
        return "SELECT insurance_id, rate FROM insurance WHERE type = ? AND periodical = ?";
    }

    public static String getOpenRentals() {
        return "SELECT * FROM rental NATURAL JOIN vehicle NATURAL JOIN location WHERE customer_id = ? AND instock = 0";
    }

    public static String updateFuel() {
        return "UPDATE fuel SET remaining_fuel = ? WHERE fuel_id = ?";
    }

    public static String insertCharges() {
        return "INSERT INTO charges VALUES(DEFAULT, ?, ?, ?, ?, ?, ?)";
    }

    public static String getInvoice() {
        return "SELECT f_name, l_name, address, type, card_number, address, charges, m_name, percent_discount, base_rate FROM payment NATURAL JOIN customer NATURAL JOIN charges NATURAL JOIN membership NATURAL JOIN discount NATURAL JOIN rental WHERE charges_id = ?";
    }

    public static String getOdo() {
        return "SELECT odometer FROM vehicle WHERE fuel_id = ?";
    }

    public static String setOdoRes() {
        return "UPDATE vehicle SET instock = 1, reserved = 0, odometer = ? WHERE fuel_id = ?";
    }

    public static String updateStock() {
        return "UPDATE vehicle SET instock = 0 WHERE vehicle_id = ?";
    }

    public static String insertFuel() {
        return "INSERT INTO fuel VALUES(DEFAULT, ?)";
    }

    public static String insertVehicle() {
        return "INSERT INTO vehicle VALUES(DEFAULT, ?, ?, ?, ?, ?, 0, 0, ?, ?, ?)";
    }

    public static String getUnprocessedCharges() {
        return "select * from charges natural join payment natural join customer natural join rental where charges = 0";
    }

    public static String getIDs() {
        return "SELECT * FROM charges where charges_id = ?";
    }

    public static String getCharge(String table, String id) {
        return "SELECT * FROM " + table + " WHERE " + id + " = ?";
    }

    public static String updateCharge() {
        return "UPDATE charges SET charges = ? WHERE charges_id = ?";
    }

    public static String updateLocation() {
        return "UPDATE vehicle SET location_id = ? WHERE vehicle_id = ?";
    }

    public static String updateRate() {
        return "UPDATE rental SET base_rate = ? WHERE vehicle_id = ?";
    }

}

