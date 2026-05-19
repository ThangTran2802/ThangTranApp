import java.sql.*;
import java.util.ArrayList;

public class Database {
    private Connection conn;

    public Database() {
        try {
            String url = "jdbc:mysql://localhost:3306/booking_app";
            String user = "root";
            String password = "Thang@2802";

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected successfully.");

            seedData();
            syncDriverAvailability();
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(null,
                "Cannot connect to MySQL!\nError: " + e.getMessage(),
                "Database Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    // Seed default driver accounts if they don't exist
    private void seedData() {
        String sql = "INSERT IGNORE INTO drivers (name, phone, username, password, license_plate) VALUES " +
                     "('Cristiano Ronaldo', '0987654321', 'driver1', '123456', '29A-11111'), " +
                     "('Lionel Messi', '0912345678', 'driver2', '234567', '29A-22222')";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Seed data error: " + e.getMessage());
        }

    }

    // Auto-fix: Reset drivers marked as busy with no active "In Progress" rides
    private void syncDriverAvailability() {
        String sql = "UPDATE drivers SET is_available = TRUE " +
                     "WHERE is_available = FALSE AND name NOT IN " +
                     "(SELECT driver_name FROM rides WHERE status = 'In Progress')";
        try (Statement stmt = conn.createStatement()) {
            int fixed = stmt.executeUpdate(sql);
            if (fixed > 0) {
                System.out.println("Auto-fix: Reset " + fixed + " driver(s) with stuck busy state.");
            }
        } catch (SQLException e) {
            System.out.println("Sync driver error: " + e.getMessage());
        }
    }



    // --- ADD RIDE (with phone number) ---
    public void addRideWithPhone(String customerName, String phone, String driverName, String pickup, String dropoff, float distance, float fare, String status) {
        String sql = "INSERT INTO rides (customer_name, customer_phone, driver_name, pickup, dropoff, distance, fare, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, customerName);
            pstmt.setString(2, phone);
            pstmt.setString(3, driverName);
            pstmt.setString(4, pickup);
            pstmt.setString(5, dropoff);
            pstmt.setFloat(6, distance);
            pstmt.setFloat(7, fare);
            pstmt.setString(8, status);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Add ride error: " + e.getMessage());
        }
    }

    // --- ADD NEW DRIVER ---
    public void addDriver(Driver driver) {
        String sql = "INSERT INTO drivers (name, phone, username, password, license_plate) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, driver.getName());
            pstmt.setString(2, driver.getPhone());
            pstmt.setString(3, driver.getUsername());
            pstmt.setString(4, driver.getPassword());
            pstmt.setString(5, driver.getVehicle());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Add driver error: " + e.getMessage());
        }
    }

    // --- ADD RIDE (from Ride object) ---
    public void addRide(Ride ride) {
        String sql = "INSERT INTO rides (customer_name, customer_phone, driver_name, pickup, dropoff, distance, fare, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ride.getUserId());
            pstmt.setString(2, ride.getPhone());
            pstmt.setString(3, ride.getDriverId());
            pstmt.setString(4, ride.getPickupLocation());
            pstmt.setString(5, ride.getDropoffLocation());
            pstmt.setFloat(6, ride.getDistance());
            pstmt.setDouble(7, ride.getFare());
            pstmt.setString(8, ride.getStatus());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Add ride error: " + e.getMessage());
        }
    }

    // --- GET ALL RIDES ---
    public ArrayList<Ride> getRides() {
        ArrayList<Ride> list = new ArrayList<>();
        String sql = "SELECT r.*, d.license_plate FROM rides r LEFT JOIN drivers d ON r.driver_name = d.name";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) { 
            while (rs.next()) {
                Ride r = new Ride(
                    rs.getString("customer_name"),
                    rs.getString("customer_phone"),
                    rs.getString("driver_name"),
                    rs.getString("license_plate") != null ? rs.getString("license_plate") : "--",
                    rs.getString("pickup"),
                    rs.getString("dropoff"),
                    rs.getFloat("distance"),
                    rs.getFloat("fare"),
                    rs.getString("status")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Get rides error: " + e.getMessage());
        }
        return list;
    }

    // --- GET RIDES BY PHONE ---
    public ArrayList<Ride> getRidesByPhone(String phone) {
        ArrayList<Ride> list = new ArrayList<>();
        String sql = "SELECT r.*, d.license_plate FROM rides r LEFT JOIN drivers d ON r.driver_name = d.name WHERE r.customer_phone = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Ride(
                        rs.getString("customer_name"),
                        rs.getString("customer_phone"),
                        rs.getString("driver_name"),
                        rs.getString("license_plate") != null ? rs.getString("license_plate") : "--",
                        rs.getString("pickup"),
                        rs.getString("dropoff"),
                        rs.getFloat("distance"),
                        rs.getFloat("fare"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Get rides by phone error: " + e.getMessage());
        }
        return list;
    }

    // --- FIND DRIVER (login authentication) ---
    public Driver findDriver(String username, String password) {
        String sql = "SELECT * FROM drivers WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Driver(
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("license_plate")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Find driver error: " + e.getMessage());
        }
        return null;
    }

    // --- FIND AVAILABLE DRIVER ---
    public Driver findAvailableDriver() {
        String sql = "SELECT * FROM drivers WHERE is_available = TRUE LIMIT 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return new Driver(
                    rs.getString("name"),
                    rs.getString("phone"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("license_plate")
                );
            }
        } catch (SQLException e) {
            System.out.println("Find available driver error: " + e.getMessage());
        }
        return null;
    }

    // --- UPDATE DRIVER AVAILABILITY ---
    public void updateDriverAvailability(String username, boolean available) {
        String sql = "UPDATE drivers SET is_available = ? WHERE username = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, available);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Update availability error: " + e.getMessage());
        }
    }

    // --- COMPLETE CURRENT RIDE ---
    public void completeCurrentRide(String driverName) {
        String sql = "UPDATE rides SET status = 'Completed' WHERE driver_name = ? AND status = 'In Progress' ORDER BY id DESC LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, driverName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Complete ride error: " + e.getMessage());
        }
    }

    // --- GET CURRENT RIDE FOR DRIVER ---
    public Ride getCurrentRideForDriver(String driverName) {
        String sql = "SELECT r.*, d.license_plate FROM rides r LEFT JOIN drivers d ON r.driver_name = d.name WHERE r.driver_name = ? AND r.status = 'In Progress' ORDER BY r.id DESC LIMIT 1";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, driverName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Ride(
                        rs.getString("customer_name"),
                        rs.getString("customer_phone"),
                        rs.getString("driver_name"),
                        rs.getString("license_plate") != null ? rs.getString("license_plate") : "--",
                        rs.getString("pickup"),
                        rs.getString("dropoff"),
                        rs.getFloat("distance"),
                        rs.getFloat("fare"),
                        rs.getString("status")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Get current ride error: " + e.getMessage());
        }
        return null;
    }

    // --- COUNT COMPLETED RIDES ---
    public int getCompletedRideCount(String driverName) {
        String sql = "SELECT COUNT(*) FROM rides WHERE driver_name = ? AND status = 'Completed'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, driverName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Count rides error: " + e.getMessage());
        }
        return 0;
    }

    // --- GET TOTAL EARNINGS ---
    public double getTotalEarnings(String driverName) {
        String sql = "SELECT COALESCE(SUM(fare), 0) FROM rides WHERE driver_name = ? AND status = 'Completed'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, driverName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("Get earnings error: " + e.getMessage());
        }
        return 0;
    }

    // --- TEST HELPERS ---
    public void resetForTest() {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM rides");
            stmt.executeUpdate("UPDATE drivers SET is_available = TRUE");
            System.out.println("Reset OK: rides cleared, drivers available.");
        } catch (SQLException e) { System.out.println("Reset error: " + e.getMessage()); }
    }

    public void setAllDriversBusy() {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE drivers SET is_available = FALSE");
        } catch (SQLException e) { System.out.println("Set busy error: " + e.getMessage()); }
    }

    public void resetAllDrivers() {
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("UPDATE drivers SET is_available = TRUE");
        } catch (SQLException e) { System.out.println("Reset drivers error: " + e.getMessage()); }
    }

    // Close database connection when done
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Close connection error: " + e.getMessage());
        }
    }
}