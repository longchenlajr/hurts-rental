import java.util.Scanner;
import java.io.*;
import java.sql.*;


public class Main {
    public static void main(String[] args) throws SQLException, IOException, java.lang.ClassNotFoundException{
        Scanner in = new Scanner(System.in);
        Connection conn = connect(in);
        while (conn == null) {
            conn = connect(in);
        }
        boolean inLoop = true;
        while (inLoop) {
            char q = mainMenu(in);
            switch (q) {
                case 'c':
                    Customer c = new Customer(conn, in);
                    c.customerInterface();
                    break;
                case 's':
                    Staff s = new Staff(conn, in);
                    s.staffInterface();
                    break;
                case 'm':
                    Manager m = new Manager(conn, in);
                    m.managerInterface();
                    break;
                case 'q':
                    System.out.println("[Exit] System exiting, disconnecting from database.");
                    in.close();
                    conn.close();
                    System.exit(0);
            }
        }
        in.close();
        conn.close();
    }

    public static Connection connect(Scanner in) throws SQLException {
        Connection conn;
                                
        //Testing with uname/pword prompt
        System.out.print("[Prompt] Input your Oracle username: ");
        String username = in.nextLine();
        System.out.print("[Prompt] Input your Oracle password: ");
        Console cons = System.console();
        String pass = new String(cons.readPassword());
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", username, pass);
        }
        catch (SQLException e) {
            System.out.println("[Error] Invalid credentials, please try again.");
            in.close();
            return null;
        }
        System.out.println("[Success] " + username + " connected to Oracle at edgar1.cse.lehigh.edu");
        return conn;
    }

    public static char mainMenu(Scanner in) {
        char ret = ' ';
        System.out.print("\n<----->\n\n");
        System.out.println("Please select your user type: ");
        System.out.println("[c] Customer\n" +
                            "[s] Staff\n" + 
                            "[m] Management\n" +
                            "[q] Exit and quit");
        System.out.print(":> ");
        while (true) {
            String type = in.next();
            if (type.equalsIgnoreCase("c")) {ret = 'c'; break;}
            else if (type.equalsIgnoreCase("s")) {ret = 's'; break;}
            else if (type.equalsIgnoreCase("m")) {ret = 'm'; break;}
            else if (type.equalsIgnoreCase("q")) {ret = 'q'; break;}
            else System.out.print("[Error] Please input a valid user type [c/s/m/q]: ");
        }
        return ret;
    }
}
