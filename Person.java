public class Person {
    private String name;
    private String phone;
    private String username;
    private String password;

    public Person(String name, String phone, String username, String password) {
        this.name = name;
        this.phone = phone;
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return this.name;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    // Polymorphism: subclass overrides to return specific role
    public String getRole() {
        return "Person";
    }

    // Polymorphism: subclass overrides to return specific info
    public String getInfo() {
        return "Name: " + name + " | Phone: " + phone;
    }

    @Override
    public String toString() {
        return getRole() + " [" + name + ", " + phone + "]";
    }
}
