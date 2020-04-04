package facade;

import dao.*;
import entites.Coupon;
import entites.Customer;
import exception.ExistException;
import exception.LimitException;
import exception.NotExistException;
import pool.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class CustomerFacade implements ClientFacade {

    private Customer customer;
    private CompanyDao companyDao;
    private CustomerDao customerDao;
    private CouponsDao couponsDao;
    private Connection connection;
    private ConnectionPool pool = ConnectionPool.getInstance();
    private boolean isLogin;
    private Long id ;


    public CustomerFacade(){
        this.companyDao = new CompanyDBDAO();
        this.couponsDao = new CouponDBDAO();
        this.customerDao = new CustomerDBDAO();
        this.isLogin = false;


    }


    @Override
    public boolean login(String email, String password) {


        boolean isLogin =false;
        String dataBaseEmail = "";
        String dataBasePassword = "";
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM CUSTOMERS WHERE EMAIL = ? AND PASSWORD = ? ";

        try (PreparedStatement pstms = connection.prepareStatement(sql)){
            pstms.setString(1,email);
            pstms.setString(2,password);
            ResultSet resultSet = pstms.executeQuery();

            while (resultSet.next()){
                dataBaseEmail= resultSet.getString(4);
                dataBasePassword = resultSet.getString(5);
            }

            if (email.equals(dataBaseEmail) && password.equals(dataBasePassword)){
                System.out.println("Successful Login!");
                isLogin = true;
                id = customerDao.getByEmail(email).getId();

            }else {
                System.out.println("Incorrect Password");
                isLogin = false;
                System.exit(0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);
        }
        return isLogin;

    }

    private boolean isValidPurchase(Coupon coupon){
        for (Coupon customerCoupon:customer.getCoupons() ) {
            if (coupon.getId() == customerCoupon.getId()) return false;

        }
        if (coupon.getAmount() == 0) return false;
        if (coupon.getEndDate().isBefore(LocalDate.now())) return false;

        return true;
    }

    public void purchaseCoupon(Coupon coupon) throws ExistException, LimitException {
        if (isValidPurchase(coupon)){
            couponsDao.addCouponPurchase(customer.getId(),coupon.getId());
            coupon.setAmount(coupon.getAmount() - 1) ;
            try {
                couponsDao.update(coupon);
            } catch (NotExistException e) {
                e.printStackTrace();
            }
            customer.getCoupons().add(coupon);
        }else {
            throw new LimitException("Invalid Purchase");
        }



    }

    public ArrayList<Coupon> getCustomerCoupon ()  {
        return customer.getCoupons();
    }

      public ArrayList<Coupon> getCustomerCouponAndCatrgory(long categoryId) {
          ArrayList<Coupon> coupons = new ArrayList<>();
          for (Coupon coupon : getCustomerCoupon()) {
              if (coupon.getCategoryId() == categoryId) {
                  coupons.add(coupon);
              }

          }
          return coupons;
      }


       public ArrayList<Coupon> getCustomerCouponPrice (double maxPrice) {
           ArrayList<Coupon> coupons = new ArrayList<>();
           for (Coupon coupon : getCustomerCoupon()) {
               if (coupon.getPrice() < maxPrice) {
                   coupons.add(coupon);
               }


           }
           return coupons;
       }


       public Customer getCustomerDetails(long id){

           try {
               Customer customer =customerDao.getById(id);
           } catch (NotExistException e) {
               e.printStackTrace();
           }
            return customer;

       }





}
