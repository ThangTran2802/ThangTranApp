public class User extends Person {

    public User(String name, String phone, String username, String password) {
        super(name, phone, username, password);
    }

    // Polymorphism: Override getRole() from Person
    @Override
    public String getRole() {
        return "Customer";
    }

    // Polymorphism: Override getInfo() from Person
    @Override
    public String getInfo() {
        return "Customer: " + getName() + " | Phone: " + getPhone();
    }

    public void bookingVehicle() {
        if (this.getPhone().isEmpty()) { 
            System.out.print("Need phone number to book! ");
        } else {
            System.out.print("Booking successful!");
        }
    }
}