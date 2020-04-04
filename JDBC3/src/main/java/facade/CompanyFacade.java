package facade;

import dao.*;
import entites.Company;
import entites.Coupon;
import exception.ExistException;
import exception.NotExistException;
import exception.NotLoginException;
import pool.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompanyFacade implements ClientFacade {

    private Company company;
    private CompanyDao companyDao;
    private CustomerDao customerDao;
    private CouponsDao couponsDao;
    private Connection connection;
    private ConnectionPool pool = ConnectionPool.getInstance();
    private boolean isLogin;

    public CompanyFacade(){
        this.companyDao = new CompanyDBDAO();
        this.couponsDao = new CouponDBDAO();
        this.customerDao = new CustomerDBDAO();
        this.isLogin = false;

    }


    private boolean isCompanyCoupon(Coupon coupon){ return coupon.getCompanyId() == company.getId(); }
//    private boolean isTitleNotExist(Coupon coupon){
//        for (Coupon companyCoupon:  ) {
//
//            if (companyCoupon.getTitle().equals(coupon.getTitle())){ return false; }
//        }
//        return true;
//    }




    @Override
    public boolean login(String email, String password) {
        boolean isLogin =false;
        String dataBaseEmail = "";
        String dataBasePassword = "";
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM COMPANIES WHERE EMAIL = ? AND PASSWORD = ?";

        try (PreparedStatement pstms = connection.prepareStatement(sql)){
            pstms.setString(1,email);
            pstms.setString(2,password);
            ResultSet resultSet = pstms.executeQuery();

            while (resultSet.next()){
               dataBaseEmail= resultSet.getString(3);
               dataBasePassword = resultSet.getString(4);
            }

            if (email.equals(dataBaseEmail) && password.equals(dataBasePassword)){
                System.out.println("Successful Login!");
                isLogin = true;

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

 public Coupon addCoupon (Coupon coupon) throws   NotLoginException {
     if (isLogin = false) {
         throw new NotLoginException("Must to login in order to create Coupon");
     }
     if (coupon == null) {
         return null;
     }
    List<Coupon> titleCoupon = couponsDao.getAll();

     if (titleCoupon.get(3).getTitle().equals(coupon.getTitle())){
         return null;
     }
     try {
         couponsDao.create(coupon);
     } catch (ExistException e) {
         e.printStackTrace();
     }finally {
         pool.returnConnection(connection);
     }
     return coupon;

 }

 public Coupon companyUpdateCoupon (Coupon coupon) throws NotLoginException{
     if (isLogin = false) {
         throw new NotLoginException("Must to login in order to update Coupon");
     }
     try {
         couponsDao.update(coupon);
     } catch (NotExistException e) {
         e.printStackTrace();
     }
     return coupon;
 }

 public void deleteCoupon (long couponId) throws NotLoginException{
     try {
         if(isCompanyCoupon(couponsDao.getById(couponId))){
             couponsDao.deleteCouponPurchaseByCouponId(couponId);
             couponsDao.deleted(couponId);
         }
       } catch (NotExistException e1) {
         e1.printStackTrace();
     }

     }


     public List getAllCompanyCoupon(long companyId) throws NotLoginException {
        if (isLogin = false) {
            throw new NotLoginException("Must to login in order to display company Coupon");
        }
        List<Coupon> companyCoupon = new ArrayList<>();
        try {
            companyCoupon = Collections.singletonList(couponsDao.getBycompanyId(companyId));
            System.out.println(companyCoupon);

        } catch (NotExistException e) {
            e.printStackTrace();
        }
        return companyCoupon;
    }

    public List getAllCompanyCouponByCategory (long companyId , long categoryId) throws NotLoginException{
        if (isLogin = false) {
            throw new NotLoginException("Must to login in order to display company Coupon by category");
        }
        List<Coupon> companyAndCategory =  new ArrayList<>();

        try {
            companyAndCategory = Collections.singletonList(couponsDao.getBycompanyIdAndCategortId(companyId, categoryId));
            System.out.println(companyAndCategory);
        } catch (NotExistException e) {
            e.printStackTrace();
        }

        return companyAndCategory;
    }

    public Coupon deleteCoupomByCompany (long couponID) throws NotLoginException, NotExistException{
        if (isLogin = false) {
            throw new NotLoginException("Must to login in order to delete company Coupon by category");
        }
        Coupon couponToDelete = couponsDao.getById(couponID);

        if (couponToDelete == null){
            throw  new NotExistException("Coupon is not exist");
        }

        couponsDao.deleted(couponToDelete.getId());
        couponsDao.deleteCouponPurchaseByCouponId(couponToDelete.getCompanyId());

        System.out.println(couponToDelete);

        return couponToDelete;

    }

    public Company companyInfo(long companyId) throws NotExistException ,NotLoginException{
        if (isLogin = false) {
            throw new NotLoginException("Must to login in order to view company information");
        }
        Company companyInfo = companyDao.getById(companyId);
        System.out.println(companyInfo);

        return companyInfo;

    }

    public List<Coupon> getCompanyCoupon (double maxPrice , long companyId) throws NotLoginException{
        if (isLogin = false) {
            throw new NotLoginException("Must to login in order to view company coupons");

        }

       List<Coupon> coupons = couponsDao.getCouponsPrice(maxPrice,companyId);

        return  coupons;
    }


}
