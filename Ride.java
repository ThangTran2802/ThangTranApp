public class Ride {
    private String userId;
    private String phone;
    private String driverId;
    private String licensePlate;
    private String pickupLocation;
    private String dropoffLocation;
    private float distance;
    private float fare;
    private String status;

    public Ride(String userId, String phone, String driverId, String licensePlate, String pickupLocation, String dropoffLocation, float distance, float fare, String status) {
        this.userId = userId;
        this.phone = phone;
        this.driverId = driverId;
        this.licensePlate = licensePlate;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.distance = distance;
        this.fare = fare;
        this.status = status;
    }

    public String getPhone() { return this.phone; }
    public String getLicensePlate() { return this.licensePlate; }

    public String getUserId() {
        return this.userId;
    }

    public String getDriverId() {
        return this.driverId;
    }

    public String getPickupLocation() {
        return this.pickupLocation;
    }

    public String getDropoffLocation() {
        return this.dropoffLocation;
    }

    public float getDistance() {
        return this.distance;
    }

     public float getFare() {
        return this.fare;
    }

     public String getStatus() {
        return this.status;
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("Ride status: " + newStatus);
    }

}