package dao;

import entites.Customer;
import exception.ExistException;
import exception.NotExistException;
import exception.ValidException;
import pool.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDBDAO implements CustomerDao {

    private ConnectionPool pool = ConnectionPool.getInstance();

    private Connection connection;

    public CustomerDBDAO() {

    }

//    ------------- Method to build customer--------------
    private Customer buildCustomer (ResultSet resultSet) throws SQLException{
        long id = resultSet.getLong(1);
        String firstName =resultSet.getString(2);
        String lastName =  resultSet.getString(3);
        String email = resultSet.getString(4);
        String password = resultSet.getString(5);
        return new Customer(id,firstName,lastName,email,password);
    }



    @Override
    public Customer getById(long customerId) throws NotExistException {
        Customer customer = null;
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM CUSTOMERS WHERE ID = ?";
        try (PreparedStatement pstms = connection.prepareStatement(sql)){
            pstms.setLong(1,customerId);
            ResultSet resultSet = pstms.executeQuery();
            while (resultSet.next()){
                customer = buildCustomer(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (customer == null){
                throw new NotExistException("Customer whit id" +" " + customerId + "not exist");
            }
        }
        return customer;
    }

    public Customer getByName (String name) throws NotExistException{
        Customer customer = null;
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM CUSTOMERS WHERE FIRST_NAME = ?";

        try (PreparedStatement pstms = connection.prepareStatement(sql)){
            pstms.setString(1,name);
            ResultSet resultSet = pstms.executeQuery();

            while (resultSet.next()){
                customer = buildCustomer(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);

        }
        return customer;
    }

    public Customer getByEmail (String email) {
        Customer customer = null;
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM CUSTOMERS WHERE EMAIL = ?";

        try (PreparedStatement pstms = connection.prepareStatement(sql)){
            pstms.setString(1,email);
            ResultSet resultSet = pstms.executeQuery();

            while (resultSet.next()){
                customer = buildCustomer(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);
        }
        return customer;
    }

    @Override
    public Customer create(Customer customer) throws ExistException {
//        try {
//            if (getByName(customer.getFirstName()) != null){
//                throw new ExistException("Customer exist");
//            }
//        } catch (NotExistException e) {
//            e.printStackTrace();
//        }
        Connection connection = pool.getConnection();
        String sql = "INSERT INTO CUSTOMERS (FIRST_NAME, LAST_NAME, EMAIL, PASSWORD) VALUES (?,?,?,?)";
        try (PreparedStatement pstms = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            pstms.setString(1,customer.getFirstName());
            pstms.setString(2,customer.getLastName());
            pstms.setString(3,customer.getEmail());
            pstms.setString(4,customer.getPassword());
            pstms.executeUpdate();
            ResultSet resultSet =pstms.getGeneratedKeys();

            if (resultSet.next()){
                customer.setId(resultSet.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);
        }
        return customer;
    }

    @Override
    public Customer update(Customer customer) throws NotExistException {
//        if (getById(customer.getId())== null){
//            throw new NotExistException("Customer is not exist");
//        }
        Connection connection = pool.getConnection();
        String sql = "UPDATE CUSTOMERS SET EMAIL = ? , PASSWORD = ? WHERE ID = ? ";
        try (PreparedStatement pstms = connection.prepareStatement(sql)){
            pstms.setString(1,customer.getEmail());
            pstms.setString(2,customer.getPassword());
            pstms.setLong(3,customer.getId());
            pstms.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);
        }
        return customer;
    }

    @Override
    public Customer deleted(long cusromerId) throws NotExistException {
        Customer customer = getById(cusromerId);
        if (customer == null) {return null;}
        Connection connection = pool.getConnection();
        String sql = "DELETE FROM CUSTOMERS WHERE ID = ?";
        try (PreparedStatement pstms = connection.prepareStatement(sql)){
            pstms.setLong(1,cusromerId);
            pstms.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);
        }
        return customer;
    }

    @Override
    public List<Customer> getAll() {
        List<Customer> customerList = new ArrayList<>();
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM CUSTOMERS";

        try (PreparedStatement pstms = connection.prepareStatement(sql)){
            ResultSet resultSet = pstms.executeQuery();

            while (resultSet.next()) {
                customerList.add(buildCustomer(resultSet));
            }
            for (Customer customer: customerList) {
                System.out.println(customerList);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);
        }
        return customerList;
    }

    @Override
    public boolean isCustomerExist(String email, String password) throws NotExistException, ValidException {

       boolean isCustomerExist = false;

       Connection connection = pool.getConnection();
       String sql = "SELECT * FROM CUSTOMERS WHERE EMAIL = ? AND PASSWORD = ?";
       try (PreparedStatement pstms = connection.prepareStatement(sql)){
           pstms.setString(1,email);
           pstms.setString(2,password);
           ResultSet resultSet = pstms.executeQuery();

           while (resultSet.next()){
               long id = resultSet.getLong(1);
               String firstName = resultSet.getString(2);
               String lastName = resultSet.getString(3);
               Customer customer =  buildCustomer(resultSet);
               isCustomerExist = true;
           }

       } catch (SQLException e) {
           e.printStackTrace();
       }finally {
           pool.returnConnection(connection);
       }
        return isCustomerExist;
    }
    @Override
    public List customerCoupon (long customerId, long couponId){
        List customerCoupon = new ArrayList();
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM CUSTOMERS_VS_COUPONS WHERE CUSTOMER_ID = ? AND COUPON_ID = ?";
        try (PreparedStatement pstms = connection.prepareStatement(sql)){
            pstms.setLong(1,customerId);
            pstms.setLong(2,couponId);
            ResultSet resultSet = pstms.executeQuery();

            while (resultSet.next()){
                customerCoupon.add(resultSet.getLong(1));
                customerCoupon.add(resultSet.getLong(2));
                System.out.println("Customer ID : " + " " + customerCoupon.get(0) + " " + "Coupon ID : " + " " + customerCoupon.get(1));
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerCoupon;
    }
    @Override
    public List allCustomerCoupon(long customerId){
        List allCustomerCoupon = new ArrayList();
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM CUSTOMERS_VS_COUPONS WHERE CUSTOMER_ID = ?";

        try (PreparedStatement pstms = connection.prepareStatement(sql)){
            pstms.setLong(1,customerId);
            ResultSet resultSet = pstms.executeQuery();

            while (resultSet.next()){
                allCustomerCoupon.add(resultSet.getLong(1));
                allCustomerCoupon.add(resultSet.getLong(2));
                System.out.println("Customer ID : " + " " + allCustomerCoupon.get(0) + " " + "Coupon ID : " + " " + allCustomerCoupon.get(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);
        }
        return allCustomerCoupon;
    }

    @Override
    public List category (long id) {
        List catego = new ArrayList();
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM CATEGORIES";

        try (PreparedStatement pstms = connection.prepareStatement(sql)){
            ResultSet resultSet = pstms.executeQuery();

            while (resultSet.next()){
                long id1 = resultSet.getLong(1);
                String desc = resultSet.getString(2);
                catego.add(id1 );
                catego.add(desc);
            }
                System.out.println(catego);

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);
        }
        return catego;
    }
}
