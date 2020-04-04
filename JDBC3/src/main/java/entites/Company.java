package entites;

import java.util.ArrayList;

public class Company {

    private long id;
    private String name;
    private String email;
    private String password;
    private ArrayList<Coupon> coupons;



    public Company(long id, String name, String password,String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        coupons = new ArrayList();
    }

    public Company(String name, String password,String email) {
        this.name = name;
        this.email = email;
        this.password = password;
        coupons = new ArrayList();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Coupon> getCoupons() {
        return coupons;
    }

    public void setCoupons(ArrayList<Coupon> coupons) {
        this.coupons = coupons;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", coupons=" + coupons +
                '}';
    }
}
