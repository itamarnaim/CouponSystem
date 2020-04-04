package facade;

import dao.*;
import entites.Company;
import entites.Coupon;
import entites.Customer;
import exception.ExistException;
import exception.NotExistException;
import exception.NotLoginException;

import java.util.List;

public class AdminFacade implements ClientFacade{

    private CompanyDao companyDao;
    private CustomerDao customerDao;
    private CouponsDao couponsDao;
    private boolean isLogin;

    public AdminFacade(){
        this.couponsDao = new CouponDBDAO();
        this.companyDao = new CompanyDBDAO();
        this.customerDao = new CustomerDBDAO();
        this.isLogin = false;
    }


    @Override
    public boolean login(String email, String password) {
        isLogin =false;
        String adminEmail = "admin@admin.com";
        String adminPassword = "admin";
        if (adminEmail.equals(email) && adminPassword.equals(password)){
            isLogin = true;
        }else {
            isLogin = false;
            System.exit(0);
        }

        return isLogin;
    }

    public Company createCompany(Company company) throws NotLoginException, ExistException {
        if (isLogin = false) {
            throw new NotLoginException("Must to login in order to create company");
        }
        if (company == null) {
            return null;
        }

        Company byName = companyDao.getByName(company.getName());
        Company byEmail = companyDao.getByEmail(company.getEmail());

        if (byName != null || byEmail != null) {
            throw new ExistException("Company already exists");
        }
        return companyDao.create(company);
    }

    public Company updateCompany (Company company) throws NotLoginException{
        if (isLogin = false) {
            throw new NotLoginException("Must to login in order to create company");
        }
        if (company == null) {
            return null;
        }

        Company name = null;
        try {
            name = companyDao.getById(company.getId());
        } catch (NotExistException e) {
            e.printStackTrace();
        }

        if (name.getName().equals(company.getName())){
            try {
                companyDao.update(company);
            } catch (NotExistException e) {
                e.printStackTrace();
            }
            return company;

        }else {
            Company newCompanyName = new Company(company.getId(),name.getName(),company.getPassword(),company.getEmail());
            try {
                companyDao.update(newCompanyName);
            } catch (NotExistException e) {
                e.printStackTrace();
            }
            return newCompanyName;

        }

    }

    public Company adminDeleteCompany(long companyId ) throws NotExistException {
        Coupon couponToDelete = couponsDao.getBycompanyId(companyId);
        Company toDelete = companyDao.getById(companyId);



        try {
            couponsDao.deleted(companyId);
        } catch (NotExistException e) {
            e.printStackTrace();
        }

        try {
            toDelete = companyDao.deleted(companyId);

        } catch (NotExistException e) {
            e.printStackTrace();
        }


        return toDelete;
    }

    public Company deleteCompany (long companyId) throws NotLoginException{
        Company toDelete = null;
        for (Coupon companyCoupon:companyDao.getCompanyCoupons(companyId)) {
            couponsDao.deleteCouponPurchaseByCouponId(companyCoupon.getId());
            try {
                couponsDao.deleted(companyCoupon.getId());
            } catch (NotExistException e) {
                e.printStackTrace();
            }
            try {
               toDelete = companyDao.deleted(companyId);
            } catch (NotExistException e) {
                e.printStackTrace();
            }
        }
        return toDelete;

    }


    public List allCompaney (){
        return companyDao.getAll();
    }

    public Company byID(long companyId) throws NotExistException{
        Company byId = null;
        try {
           byId = companyDao.getById(companyId);
        } catch (NotExistException e) {
            e.printStackTrace();
        }
        return byId;
    }

    public Customer createCustomer (Customer customer) throws ExistException{

        Customer email = customerDao.getByEmail(customer.getEmail());

        if (email == null) {
            return customerDao.create(customer);
        }
        else {
            throw new ExistException("Customer is exsit");
        }
    }

    public Customer updateCustoer(Customer customer) throws NotExistException{
        if (customerDao.getById(customer.getId()) == null){
            throw new NotExistException("Customer is not exist");
        }
        return customerDao.update(customer);
    }

    public List allCustomer(){
        return customerDao.getAll();
    }

    public Customer customerById(long customerId) throws NotExistException{
        Customer id = null;

        try {
             id = customerDao.getById(customerId);
        } catch (NotExistException e) {
            e.printStackTrace();
        }
        return id;
    }

    public Customer deleteCustomer (long customerId) throws NotExistException{
        Customer toDelete = null;
        toDelete = customerDao.deleted(customerId);
        return toDelete;
    }



}
