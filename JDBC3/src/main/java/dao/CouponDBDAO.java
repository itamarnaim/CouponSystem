package dao;

import entites.Coupon;
import exception.ExistException;
import exception.LimitException;
import exception.NotExistException;
import pool.ConnectionPool;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CouponDBDAO implements CouponsDao {

    private ConnectionPool pool = ConnectionPool.getInstance();


    public CouponDBDAO() {
    }

    public Coupon buildCoupon(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong(1);
        long companyId = resultSet.getLong(2);
        long categoryId = resultSet.getLong(3);
        String title = resultSet.getString(4);
        String description = resultSet.getString(5);
        LocalDate startDate = resultSet.getDate(6).toLocalDate();
        LocalDate endDate = resultSet.getDate(7).toLocalDate();
        int amount = resultSet.getInt(8);
        double price = resultSet.getDouble(9);
        String image = resultSet.getString(10);
//        Category category = Category.valueOf(resultSet.getString(11));
        return new Coupon(id, companyId, categoryId, title, description, startDate, endDate, amount, price, image);

    }


    @Override
    public Coupon getById(long couponId) throws NotExistException {
        Coupon coupon = null;
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM COUPONS WHERE COMPANY_ID IN (SELECT ID FROM COMPANIES) AND ID = ? ";

        try (PreparedStatement pstms = connection.prepareStatement(sql)) {
            pstms.setLong(1, couponId);
            ResultSet resultSet = pstms.executeQuery();
            while (resultSet.next()) {
                coupon = buildCoupon(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (coupon == null) {
                throw new NotExistException("Coupon whit id: " + " " + couponId + " " + " is not exist");
            }
        } finally {
            pool.returnConnection(connection);
        }

        return coupon;
    }

    @Override
    public Coupon create(Coupon coupon) throws ExistException {
        Connection connection = pool.getConnection();
        String sql = "INSERT INTO COUPONS (COMPANY_ID , CATEGORY_ID, TITLE,DESCRIPTION,START_DATE,END_DATE,AMOUNT,PRICE,IMAGE)" +
                "VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstms = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstms.setLong(1, coupon.getCompanyId());
            pstms.setLong(2, coupon.getCategoryId());
            pstms.setString(3, coupon.getTitle());
            pstms.setString(4, coupon.getDescription());
            pstms.setDate(5, Date.valueOf(coupon.getStartDate()));
            pstms.setDate(6, Date.valueOf(coupon.getEndDate()));
            pstms.setInt(7, coupon.getAmount());
            pstms.setDouble(8, coupon.getPrice());
            pstms.setString(9, coupon.getImage());
            pstms.executeUpdate();

            ResultSet resultSet = pstms.getGeneratedKeys();
            if (resultSet.next()) {
                coupon.setId(resultSet.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.returnConnection(connection);
        }
        return coupon;
    }

    @Override
    public Coupon update(Coupon coupon) throws NotExistException {
        Coupon coupon1 = null;
//        if (getById(coupon.getId()) == null){
//            throw new NotExistException("Coupon whit id: " + " " + coupon.getId() + " " + "is not exist ");
//        }
        Connection connection = pool.getConnection();
        String sql = "UPDATE COUPONS SET TITLE = ? , DESCRIPTION = ? , END_DATE = ? , AMOUNT = ? , PRICE = ? , IMAGE = ? WHERE COMPANY_ID IN (SELECT ID FROM COMPANIES WHERE ID = ?) AND ID = ?";

        try (PreparedStatement patma = connection.prepareStatement(sql)) {
            patma.setString(1, coupon.getTitle());
            patma.setString(2, coupon.getDescription());
            patma.setDate(3, Date.valueOf(coupon.getEndDate()));
            patma.setInt(4, coupon.getAmount());
            patma.setDouble(5, coupon.getPrice());
            patma.setString(6, coupon.getImage());
            patma.setLong(7, coupon.getCompanyId());
            patma.setLong(8,coupon.getId());
            patma.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.returnConnection(connection);
        }
        return coupon1;
    }

    @Override
    public Coupon addCouponAmount(long couponId) throws NotExistException {
        Coupon coupon = null;
        if (getById(couponId) == null) {
            throw new NotExistException("Coupon whit id: " + " " + couponId + " " + "is not exist");
        }
        Connection connection = pool.getConnection();
        String sql = "UPDATE COUPONS SET AMOUNT = AMOUNT + 1 WHERE ID = ?";
        try (PreparedStatement pstms = connection.prepareStatement(sql)) {

            pstms.setLong(1, couponId);
            pstms.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.returnConnection(connection);
        }
        return coupon;

    }
    @Override
    public Coupon minusCouponAmount(long couponId) throws NotExistException {
        Coupon coupon = null;
        if (getById(couponId) == null) {
            throw new NotExistException("Coupon whit id: " + " " + couponId + " " + "is not exist");
        }
        Connection connection = pool.getConnection();
        String sql = "UPDATE COUPONS SET AMOUNT = AMOUNT - 1 WHERE ID = ?";
        try (PreparedStatement pstms = connection.prepareStatement(sql)) {
            pstms.setLong(1, couponId);
            pstms.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.returnConnection(connection);
        }
        return coupon;

    }

    @Override
    public Coupon updateAmount(long couponId, long companyId ,int amount) throws NotExistException {
        Coupon coupon = null;
        if (getById(couponId) == null) {
            throw new NotExistException("Coupon whit id: " + " " + couponId + " " + "is not exist ");
        }
        Connection connection = pool.getConnection();
        String sql = "UPDATE COUPONS SET AMOUNT = ? WHERE COMPANY_ID IN (SELECT ID FROM COMPANIES WHERE ID = ?) AND ID = ?";
        try (PreparedStatement pstms = connection.prepareStatement(sql)) {
//            pstms.setInt(1,coupon.getAmount());
            pstms.setInt(1, amount);
            pstms.setLong(2, companyId);
            pstms.setLong(3,couponId);
            pstms.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.returnConnection(connection);
        }
        return coupon;
    }
    @Override
    public Coupon updateEndDate(long couponId,long companyId ,LocalDate endDate) throws NotExistException {
        Coupon coupon = null;
        if (getById(couponId) == null) {
            throw new NotExistException("Coupon whit id: " + " " + couponId + " " + "is not exist ");
        }
        Connection connection = pool.getConnection();
        String sql = "UPDATE COUPONS SET END_DATE = ? WHERE COMPANY_ID IN (SELECT ID FROM COMPANIES WHERE ID = ?) AND ID = ?";
        try (PreparedStatement pstms = connection.prepareStatement(sql)) {
            pstms.setDate(1, Date.valueOf(endDate));
            pstms.setLong(2, companyId);
            pstms.setLong(3,couponId);
            pstms.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.returnConnection(connection);
        }
        return coupon;
    }

    @Override
    public Coupon deleted(long couponId) throws NotExistException {
       Coupon coupon = getById(couponId);
       if (coupon == null) {return null;}
        Connection connection = pool.getConnection();
        String sql = "DELETE FROM COUPONS WHERE COMPANY_ID IN (SELECT ID FROM COMPANIES WHERE ID = ?) AND ID = ?";
        try (PreparedStatement pstms = connection.prepareStatement(sql)) {
            pstms.setLong(1, coupon.getCompanyId());
            pstms.setLong(2,couponId);
            pstms.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.returnConnection(connection);
        }
        return coupon;
    }

    @Override
    public List<Coupon> getAll() {
        List<Coupon> couponList = new ArrayList<>();
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM COUPONS";
        try (PreparedStatement pstms = connection.prepareStatement(sql)) {
            ResultSet resultSet = pstms.executeQuery();

            while (resultSet.next()) {
//                long id = resultSet.getLong(1);
//                long companyId = resultSet.getLong(2);
//                long categoryId = resultSet.getLong(3);
//                String title = resultSet.getString(4);
//                String description = resultSet.getString(5);
//                LocalDate startDate = resultSet.getDate(6).toLocalDate();
//                LocalDate endDate = resultSet.getDate(7).toLocalDate();
//                int amount = resultSet.getInt(8);
//                double price = resultSet.getDouble(9);
//                String image = resultSet.getString(10);
                couponList.add(buildCoupon(resultSet));
            }
            for (Coupon coupon:couponList) {
                System.out.println(couponList);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.returnConnection(connection);
        }
        return couponList;
    }


    @Override
    public Coupon addCouponPurchase(long customerID, long couponID) throws LimitException {
        Coupon coupon = null;
        try {
            getById(couponID);
        } catch (NotExistException e) {
            e.printStackTrace();
        }
        Connection connection = pool.getConnection();
        String insert = "INSERT INTO CUSTOMERS_VS_COUPONS (CUSTOMER_ID ,COUPON_ID) VALUES (? , ?)";
        String select = "SELECT * FROM COUPONS WHERE ID = ?";
        try (PreparedStatement selectPstmt = connection.prepareStatement(select);
             PreparedStatement insertPstmt = connection.prepareStatement(insert)) {

            selectPstmt.setLong(1, couponID);
            ResultSet resultSet = selectPstmt.executeQuery();
            while (resultSet.next()) {
                coupon = buildCoupon(resultSet);
            }
            if (coupon == null) return  null;

            insertPstmt.setLong(1, customerID);
            insertPstmt.setLong(2, couponID);

            insertPstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.returnConnection(connection);
        }
        return coupon;
    }

    @Override
    public Coupon deleteCouponPurchase(long customerID, long couponID) {
        Coupon coupon = null;
        try {
            coupon = getById(couponID);
        } catch (NotExistException e) {
            e.printStackTrace();
        }
        Connection connection = pool.getConnection();
        String sql = "DELETE FROM CUSTOMERS_VS_COUPONS WHERE CUSTOMER_ID = ? AND COUPON_ID = ?";
        try (PreparedStatement pstms = connection.prepareStatement(sql)) {
            pstms.setLong(1, customerID);
            pstms.setLong(2, couponID);
            pstms.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.returnConnection(connection);
        }
        return coupon;
    }
//    public List getCouponTitle (long companyId){
//        List<String> couponsTitle = new ArrayList<>();
//        Connection connection = pool.getConnection();
//        String sql = "SELECT TITLE FROM COUPONS WHERE COMPANY_ID = ?";
//        try (PreparedStatement pstms = connection.prepareStatement(sql)){
//            pstms.setLong(1,companyId);
//            ResultSet resultSet = pstms.executeQuery();
//
//            int i = 0 ;
//            while (resultSet.next()) {
//                couponsTitle.set(i, resultSet.getString(4));
//                System.out.println(couponsTitle.get(i));
//                i ++;
//
//
//            }
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }finally {
//            pool.returnConnection(connection);
//        }
//        return couponsTitle;
//    }

    @Override
    public Coupon deleteCouponPurchaseByCouponId(long couponID){
        Coupon coupon = null;
        try {
            coupon = getById(couponID);
        } catch (NotExistException e) {
            e.printStackTrace();
        }
        Connection connection = pool.getConnection();
        String sql = "DELETE FROM CUSTOMERS_VS_COUPONS WHERE COUPON_ID = ?";
        try (PreparedStatement pstms = connection.prepareStatement(sql)) {
            pstms.setLong(1, couponID);
            pstms.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.returnConnection(connection);
        }
        return coupon;

    }

    @Override
    public Coupon getBycompanyId(long companyId) throws NotExistException {
        Coupon coupon = null;
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM COUPONS WHERE COMPANY_ID = ? ";

        try (PreparedStatement pstms = connection.prepareStatement(sql)) {
            pstms.setLong(1, companyId);
            ResultSet resultSet = pstms.executeQuery();
            while (resultSet.next()) {
                coupon = buildCoupon(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (coupon == null) {
                throw new NotExistException("Coupon whit company id: " + " " + companyId + " " + " is not exist");
            }
        } finally {
            pool.returnConnection(connection);
        }

        return coupon;
    }

    @Override
    public Coupon getBycompanyIdAndCategortId(long companyId , long categortId) throws NotExistException {
        Coupon coupon = null;
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM COUPONS WHERE COMPANY_ID = ? AND CATEGORY_ID = ?";

        try (PreparedStatement pstms = connection.prepareStatement(sql)) {
            pstms.setLong(1, companyId);
            pstms.setLong(2,categortId);
            ResultSet resultSet = pstms.executeQuery();
            while (resultSet.next()) {
                coupon = buildCoupon(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (coupon == null) {
                throw new NotExistException("Coupon whit company id: " + " " + companyId + " " + " and category id" + " " + categortId + " " + "is not exist");
            }
        } finally {
            pool.returnConnection(connection);
        }

        return coupon;
    }
    @Override
    public List<Coupon> getCouponsPrice (double maxPrice , long companyId){
        List<Coupon> couponList = new ArrayList<>();
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM COUPONS WHERE PRICE >= ? AND COMPANY_ID IN (SELECT ID FROM COMPANIES WHERE ID = ?) ";
        try (PreparedStatement patms = connection.prepareStatement(sql)){
            patms.setDouble(1,maxPrice);
            patms.setLong(2,companyId);
            ResultSet resultSet = patms.executeQuery();

            while (resultSet.next()){
                couponList.add(buildCoupon(resultSet));
            }
            for (Coupon coupon: couponList) {
                System.out.println(couponList);

            }



        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);
        }

        return couponList;

    }
}
