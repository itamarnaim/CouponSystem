package dao;


import entites.Customer;
import exception.ExistException;
import exception.NotExistException;
import exception.ValidException;

import java.util.List;

public interface CustomerDao {

    Customer getById (long companyId) throws NotExistException;

    Customer create(Customer customer) throws ExistException;

    Customer update(Customer customer) throws NotExistException;

    Customer deleted (long cusromerId) throws NotExistException;

    List<Customer> getAll ();

    boolean isCustomerExist (String name, String password) throws NotExistException, ValidException;

    Customer getByEmail (String email);

    List customerCoupon (long customerId, long couponId);

    List allCustomerCoupon(long customerId);

    List category (long id);


}
