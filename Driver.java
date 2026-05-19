public class Driver extends Person { 
    private String vehicle;
    private boolean isAvailable;

    public Driver(String name, String phone, String username, String password, String vehicle) {
        super(name, phone, username, password);
        this.vehicle = vehicle;
        this.isAvailable = true;
    }

    public String getVehicle() {
        return this.vehicle;
    }

    public boolean isAvailable() {
        return this.isAvailable;
    }

    // Polymorphism: Override getRole() from Person
    @Override
    public String getRole() {
        return "Driver";
    }

    // Polymorphism: Override getInfo() from Person
    @Override
    public String getInfo() {
        return "Driver: " + getName() + " | Plate: " + vehicle + " | Status: " + (isAvailable ? "Available" : "Busy");
    }

    public void acceptRide() {
        if (this.isAvailable) {
            this.isAvailable = false;
            System.out.println("Driver has accepted the ride!");
        } else {
            System.out.println("Driver is busy!");
        }
    }

    public void completeRide() {
        this.isAvailable = true;
        System.out.println("Driver is now available!");
    }
}
