package dao;

import entites.Coupon;
import exception.ExistException;
import exception.LimitException;
import exception.NotExistException;

import java.time.LocalDate;
import java.util.List;

public interface CouponsDao {

    Coupon getById (long couponId) throws NotExistException;

    Coupon create(Coupon coupon) throws ExistException;

    Coupon update(Coupon coupon) throws NotExistException;

    Coupon addCouponAmount(long couponId) throws NotExistException;

    Coupon minusCouponAmount(long couponId) throws NotExistException;

    Coupon updateAmount(long couponId, long companyId ,int amount) throws NotExistException;

    Coupon updateEndDate(long couponId, long companyId , LocalDate endDate) throws NotExistException;

    Coupon deleted (long couponId) throws NotExistException;

    List<Coupon> getAll ();

    Coupon addCouponPurchase (long customerID , long couponID) throws LimitException;

    Coupon deleteCouponPurchase (long customerID , long couponID);

//     List<Coupon> getCouponTitle (long companyId);
Coupon getBycompanyId(long companyId) throws NotExistException;

    Coupon getBycompanyIdAndCategortId(long companyId , long categortId) throws NotExistException;

    Coupon deleteCouponPurchaseByCouponId(long couponID);

    List<Coupon> getCouponsPrice (double maxPrice , long companyId);
}
