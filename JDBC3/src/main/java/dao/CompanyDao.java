package dao;

import entites.Company;
import entites.Coupon;
import exception.ExistException;
import exception.NotExistException;
import exception.ValidException;

import java.util.ArrayList;
import java.util.List;

public interface CompanyDao {

     Company getById (long companyId) throws NotExistException;

     Company create(Company company) throws ExistException;

     Company update(Company company) throws NotExistException;

     Company deleted (long companyId) throws NotExistException;

     List<Company> getAll ();

     boolean isCompanyExist (String email, String password) throws NotExistException, ValidException;

    Company getByName (String name);

    Company getByEmail(String email);

    ArrayList<Coupon> getCompanyCoupons(long companyId);

}
