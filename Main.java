import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Database db = new Database();
        Scanner sc = new Scanner(System.in);

        
        db.addDriver(new Driver("Mo Cuong", "0833282206", "mcuong123", "mcuongdeptrai", "23A-1234"));
        db.addDriver(new Driver("Tran Binh", "0397149526", "binh456", "thaybinhdeptrai", "23A-4567"));
         

        int choice_role = 0;
        while (choice_role != 3) {
            System.out.println("1. Login as Customer");
            System.out.println("2. Login as Driver");
            System.out.println("3. Exit");
            choice_role = sc.nextInt();
            sc.nextLine();

            if (choice_role == 1) {
                int choice = 0;
                while (choice != 3) {
                    System.out.println("1. Book a ride");
                    System.out.println("2. View ride history");
                    System.out.println("3. Exit");
                    choice = sc.nextInt();
                    sc.nextLine();
            
            
                    if (choice == 1) {
                        System.out.println("Enter pickup location: ");
                        String pickup = sc.nextLine();
                        System.out.println("Enter dropoff location: ");
                        String dropoff = sc.nextLine();
                        System.out.println("Enter distance: ");
                        float distance = sc.nextFloat();
                        Driver driver = db.findAvailableDriver();
                        if (driver != null) {
                            Ride ride = new Ride("User1", "0123456789", driver.getName(), driver.getVehicle(), pickup, dropoff, distance, (float)(10000 + (8000 * distance)), "Completed");
                            db.addRide(ride);
                            driver.acceptRide();
                            System.out.println("Ride booked with: " + driver.getName());
                            System.out.println("Fare : " + ride.getFare());
                        } else {
                            System.out.println("No driver available!");
                        }
                    } else if (choice == 2) {
                        System.out.println("Ride history: ");
                        for (Ride i : db.getRides()) {
                            System.out.println(i.getPickupLocation() + " -> " + i.getDropoffLocation() + " | Fare: " + i.getFare() + " | Status: " + i.getStatus());
                        }
                    }
                }
            } else if (choice_role == 2) {
                System.out.println("Username: ");
                String username = sc.nextLine();
                System.out.println("Password: ");
                String password = sc.nextLine();
                Driver driver = db.findDriver(username, password);
                if (driver != null) {
                    System.out.println("Welcome " + driver.getName() + "!");
                    int driverchoice = 0;
                    while (driverchoice != 2) {
                        System.out.println("1. Complete current ride");
                        System.out.println("2. Back");
                        driverchoice = sc.nextInt();
                        sc.nextLine();
                        if (driverchoice == 1) {
                            if (!driver.isAvailable()) {
                                driver.completeRide();
                            } else {
                                System.out.println("You have no active ride!");
                            }
                        }
                    }
                } else {
                    System.out.println("Something went wrong!");
                }
            }
        }
    }
}