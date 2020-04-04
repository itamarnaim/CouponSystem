package tests;

import entites.Company;
import entites.Coupon;
import entites.Customer;
import exception.ExistException;
import exception.LimitException;
import exception.NotExistException;
import exception.NotLoginException;
import facade.AdminFacade;
import facade.CompanyFacade;
import facade.CustomerFacade;
import lob.CouponExpirationDailyJob;
import loginManager.LoginManager;
import loginManager.clientType;
import pool.ConnectionPool;

import java.time.LocalDate;

public class Test {

    public static void testAll() {

        //1. Start Daily Job Thread
        CouponExpirationDailyJob couponExpirationDailyJob = new CouponExpirationDailyJob();
        Thread dailyJobThread = new Thread(couponExpirationDailyJob);
        dailyJobThread.start();

        LoginManager loginManager = LoginManager.getInstance();


        //2. Admin Tests
        AdminFacade adminFacade = (AdminFacade) loginManager.login("admin@admin", "admin", clientType.ADMINISTRATOR);

        Company companyTest = new Company("test", "test", "test@test.mail");

        try {
            adminFacade.createCompany(companyTest);
        } catch (NotLoginException e) {
            e.printStackTrace();
        } catch (ExistException e) {
            e.printStackTrace();
        }

        Customer customerTest = new Customer("CustomerTestFN", "CustomerTestLN", "CustomerTest@mail", "Test");

        try {
            adminFacade.createCustomer(customerTest);
        } catch (ExistException e) {
            e.printStackTrace();
        }

        try {
            adminFacade.byID(companyTest.getId());

        } catch (NotExistException e) {
            e.printStackTrace();
        }

        try {
            adminFacade.customerById(customerTest.getId());
        } catch (NotExistException e) {
            e.printStackTrace();
        }

        companyTest.setPassword("password");
        try {
            adminFacade.updateCompany(companyTest);
        } catch (NotLoginException e) {
            e.printStackTrace();
        }

        customerTest.setFirstName("FNchangeTest");

        try {
            adminFacade.updateCustoer(customerTest);
        } catch (NotExistException e) {
            e.printStackTrace();
        }

        System.out.println(adminFacade.allCompaney());
        System.out.println(adminFacade.allCustomer());

        //3. Company Daily Test
        CompanyFacade companyFacade = (CompanyFacade) loginManager.login("Test@email", "test", clientType.COMPANY);

        Coupon couponTest = new Coupon(companyTest.getId(), 3, "tests", "test coupon", LocalDate.now(), LocalDate.now(), 2, 22, "testtttt");

        try {
            companyFacade.addCoupon(couponTest);
        } catch (NotLoginException e) {
            e.printStackTrace();
        }

        couponTest.setImage("https://https://imgur.com/gallery/qG9MGlW");
        try {
            companyFacade.companyUpdateCoupon(couponTest);
        } catch (NotLoginException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(companyFacade.getAllCompanyCoupon(companyTest.getId()));
        } catch (NotLoginException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(companyFacade.getAllCompanyCoupon(300));
        } catch (NotLoginException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(companyFacade.companyInfo(companyTest.getId()));
        } catch (NotExistException e) {
            e.printStackTrace();
        } catch (NotLoginException e) {
            e.printStackTrace();
        }

        //4. Customer Daily Test

        CustomerFacade customerFacade = (CustomerFacade) loginManager.login("Customertest@mail", "testPassword", clientType.CUSTOMER);

        try {
            customerFacade.purchaseCoupon(couponTest);
        } catch (ExistException e) {
            e.printStackTrace();
        } catch (LimitException e) {
            e.printStackTrace();
        }

        System.out.println(customerFacade.getCustomerCoupon());
        System.out.println(customerFacade.getCustomerCouponAndCatrgory(couponTest.getCategoryId()));
        System.out.println(customerFacade.getCustomerDetails(customerTest.getId()));


        //5. Delete All

        try {
            companyFacade.deleteCoupon(couponTest.getId());
        } catch (NotLoginException e) {
            e.printStackTrace();
        }

        try {
            adminFacade.deleteCompany(companyTest.getId());
        } catch (NotLoginException e) {
            e.printStackTrace();
        }

        try {
            adminFacade.deleteCustomer(customerTest.getId());
        } catch (NotExistException e) {
            e.printStackTrace();
        }

        //6. Stop Daily Job
        couponExpirationDailyJob.stop();
        dailyJobThread.interrupt();

        //7. Closing Job
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        connectionPool.closeAllConnections();
    }
}
