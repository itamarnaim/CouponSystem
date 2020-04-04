package dao;

import entites.Company;
import entites.Coupon;
import exception.ExistException;
import exception.NotExistException;
import exception.ValidException;
import pool.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDBDAO implements CompanyDao {

    private ConnectionPool pool = ConnectionPool.getInstance();
    private CouponDBDAO coupons = new CouponDBDAO();

    private Connection connection;

    public CompanyDBDAO() {
    }
//    ------------ Method build Company ---------------(extra)
    private Company buildCompany (ResultSet resultSet) throws SQLException{
        long id = resultSet.getLong(1);
        String name = resultSet.getString(2);
        String email = resultSet.getString(3);
        String password = resultSet.getString(4);
        return new Company(id,name,email,password);
    }
    @Override
    public Company getByName (String name) {
        Company company = null;
        String sql = "SELECT * FROM COMPANIES WHERE NAME = ?";
        Connection connection = pool.getConnection();

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1,name);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()){
//                long id = resultSet.getLong(1);
//                String password = resultSet.getString(3);
//                String email = resultSet.getString(4);
//                company = new Company(id, name,password,email);
                company = buildCompany(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);
        }
        return company;

    }
    @Override
    public Company getByEmail(String email){
        Company company = null;
        String sql = "SELECT * FROM COMPANIES WHERE EMAIL = ?";
        Connection connection = pool.getConnection();

        try(PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1,email);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()){
                company = buildCompany(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);
        }
        return company;

    }

    @Override
    public Company getById(long companyId) throws NotExistException {
        Company company = null;
        String sql = "SELECT * FROM COMPANIES WHERE ID = ?";
        Connection connection = pool.getConnection();

        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setLong(1,companyId);
            ResultSet resultSet =pstmt.executeQuery();

            while (resultSet.next()){

//                String name = resultSet.getString(2);
//                String password = resultSet.getString(4);
//                String email = resultSet.getString(3);
//                company = new Company(companyId,name,password,email);
                company = buildCompany(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }finally {
            pool.returnConnection(connection);
        } if (company == null){
            throw new NotExistException("Company whit id" + companyId + "not exist");
        }
        return company;
    }

    @Override
    public Company create(Company company) throws ExistException {
        if (getByName(company.getName()) != null) {
            throw new ExistException("Company exist");
        }
            Connection connection = pool.getConnection();
        String sql2 = "INSERT INTO COMPANIES (NAME,PASSWORD,EMAIL) VALUES (? ,?,?)";
        try ( PreparedStatement pstms =connection.prepareStatement(sql2,Statement.RETURN_GENERATED_KEYS);){
            pstms.setString(1,company.getName());
            pstms.setString(2,company.getEmail());
            pstms.setString(3,company.getPassword());
            pstms.executeUpdate();
            ResultSet resultSet = pstms.getGeneratedKeys();

            if (resultSet.next()){
                company.setId(resultSet.getLong(1));
            }


        } catch (SQLException e) {
            e.printStackTrace();

        }finally {
            pool.returnConnection(connection);
        }

        return company;
    }


    @Override
    public Company update(Company company) throws NotExistException {
        if (getById(company.getId()) == null){
            throw new NotExistException("Company is not exist");
        }
//        Company company1 = null;
        Connection connection = pool.getConnection();
        String sql3 = "UPDATE COMPANIES SET NAME = ? , EMAIL = ? , PASSWORD = ? WHERE ID = ?";
        try(PreparedStatement pstms = connection.prepareStatement(sql3)) {

            pstms.setString(1,company.getName());
            pstms.setString(2,company.getEmail());
            pstms.setString(3,company.getPassword());
            pstms.setLong(4,company.getId());
            pstms.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);
        }
        return company;
    }


    @Override
    public Company deleted(long companyId) throws NotExistException {
        Company company = getById(companyId);
        if (company == null) { return  null; }

        String sql = "DELETE FROM COMPANIES WHERE ID = ?";
        Connection connection = pool.getConnection();

        try (PreparedStatement pstms = connection.prepareStatement(sql)){
            pstms.setLong(1,companyId);
            pstms.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);
        }
        return company;
    }

    @Override
    public List<Company> getAll() {
            List<Company> companyList = new ArrayList<>();
            Connection connection = pool.getConnection();
            String sql = "SELECT * FROM COMPANIES ";
            try (PreparedStatement pstms = connection.prepareStatement(sql)){
                ResultSet resultSet = pstms.executeQuery();

                while (resultSet.next()){
//                    long id = resultSet.getLong(1);
//                    String name = resultSet.getString(2);
//                    String email = resultSet.getString(3);
//                    String password = resultSet.getString(4);
//                    companyList.add(new Company(id,name,email,password));
                    companyList.add(buildCompany(resultSet));
                }
                for (Company company: companyList) {
                    System.out.println(companyList);

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                pool.returnConnection(connection);
            }
        return companyList;
    }

    @Override
    public boolean isCompanyExist(String email, String password) throws NotExistException, ValidException {
        boolean isCompanyExist = false;
        Connection connection = pool.getConnection();
        String sql = "SELECT * FROM COMPANIES WHERE EMAIL = ? AND PASSWORD = ?";

        try (PreparedStatement pstms = connection.prepareStatement(sql)){
            pstms.setString(1,email);
            pstms.setString(2,password);
            ResultSet resultSet = pstms.executeQuery();

            while (resultSet.next()){
                Company company = buildCompany(resultSet);
                isCompanyExist = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            pool.returnConnection(connection);
        }
        return isCompanyExist;
    }

    @Override
    public ArrayList<Coupon> getCompanyCoupons(long companyId){
        ArrayList<Coupon> companyCoupons = new ArrayList<>();

        String sql = "SELECT * FROM COUPONS WHERE COMPANY_ID = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setLong(1,companyId);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()){
                companyCoupons.add(coupons.buildCoupon(resultSet));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return companyCoupons;
    }




}
