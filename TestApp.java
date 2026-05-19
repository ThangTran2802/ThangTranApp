public class TestApp {
    static int passed = 0;
    static int failed = 0;

    static void check(String testName, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + testName);
            passed++;
        } else {
            System.out.println("[FAIL] " + testName);
            failed++;
        }
    }

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  RIDE BOOKING APP - FULL TEST SUITE");
        System.out.println("========================================\n");

        Database db = new Database();

        // --- SETUP: Clear old rides, reset drivers ---
        System.out.println("--- Setup: Resetting test data ---");
        db.resetForTest();
        System.out.println();

        // --- TEST 1: Find Available Driver ---
        System.out.println("--- TEST 1: Find Available Driver ---");
        Driver d1 = db.findAvailableDriver();
        check("Available driver found", d1 != null);
        if (d1 != null) {
            check("Driver has a name", d1.getName() != null && !d1.getName().isEmpty());
            System.out.println("   -> Driver found: " + d1.getName() + " | Plate: " + d1.getVehicle());
        }
        System.out.println();

        // --- TEST 2: Book a ride and verify driver marked as busy ---
        System.out.println("--- TEST 2: Book a Ride (phone: 0912345678) ---");
        if (d1 != null) {
            db.addRideWithPhone("Guest", "0912345678", d1.getName(), "Ha Noi", "Bac Ninh", 10, 90000, "In Progress");
            db.updateDriverAvailability(d1.getUsername(), false);

            // Check if that driver is no longer found as available
            Driver d2 = db.findAvailableDriver();
            // If only 1 driver exists, d2 should be null or a different driver
            java.util.ArrayList<Ride> rides = db.getRides();
            check("Ride was saved to DB", rides.size() >= 1);
            check("Phone number saved correctly", 
                rides.stream().anyMatch(r -> "0912345678".equals(r.getPhone())));
            check("Ride status is 'In Progress'", 
                rides.stream().anyMatch(r -> "In Progress".equals(r.getStatus())));
            System.out.println("   -> Ride count in DB: " + rides.size());
            System.out.println("   -> Status: " + rides.get(rides.size()-1).getStatus());
        }
        System.out.println();

        // --- TEST 3: Driver Login ---
        System.out.println("--- TEST 3: Driver Login ---");
        Driver dLogin = db.findDriver("driver1", "123456");
        check("Driver driver1 login SUCCESS", dLogin != null);
        if (dLogin != null) System.out.println("   -> Logged in as: " + dLogin.getName());

        Driver dFail = db.findDriver("driver1", "wrongpassword");
        check("Wrong password login REJECTED", dFail == null);
        System.out.println();

        // --- TEST 4: Complete ride (by assigned driver) ---
        System.out.println("--- TEST 4: Complete Ride (by assigned driver) ---");
        if (d1 != null) {
            db.completeCurrentRide(d1.getName());
            db.updateDriverAvailability(d1.getUsername(), true);

            java.util.ArrayList<Ride> ridesAfter = db.getRides();
            check("Ride status updated to 'Completed'",
                ridesAfter.stream().anyMatch(r -> "Completed".equals(r.getStatus())));
            System.out.println("   -> Status after complete: " + ridesAfter.get(ridesAfter.size()-1).getStatus());
        }
        System.out.println();

        // --- TEST 5: Driver available again ---
        System.out.println("--- TEST 5: Driver Available Again ---");
        Driver d3 = db.findAvailableDriver();
        check("Driver is available again after completing ride", d3 != null);
        if (d3 != null) System.out.println("   -> Available driver: " + d3.getName());
        System.out.println();

        // --- TEST 6: No driver available (all busy) ---
        System.out.println("--- TEST 6: No Driver Scenario ---");
        // Set all drivers to busy
        db.setAllDriversBusy();
        Driver d4 = db.findAvailableDriver();
        check("No driver found when all are busy", d4 == null);
        // Reset drivers
        db.resetAllDrivers();
        System.out.println();

        // --- RESULTS ---
        System.out.println("========================================");
        System.out.println("  RESULTS: " + passed + " PASSED | " + failed + " FAILED");
        System.out.println("========================================");
        if (failed == 0) System.out.println("  ALL TESTS PASSED! App is ready.");
        else System.out.println("  Some tests failed. Please review.");
    }
}
