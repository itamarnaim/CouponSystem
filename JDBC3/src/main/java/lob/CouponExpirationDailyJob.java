package lob;

import dao.CouponDBDAO;
import entites.Coupon;
import exception.NotExistException;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class CouponExpirationDailyJob implements Runnable {

    private CouponDBDAO couponDBDAO;
    private boolean stop = false;

    public CouponExpirationDailyJob() {
        this.couponDBDAO = new CouponDBDAO();
    }



    @Override
    public void run() {
        while (!stop){
            try {
                for (Coupon coupon: couponDBDAO.getAll()) {
                    if (coupon.getEndDate().isBefore(LocalDate.now())){
                        try {
                            couponDBDAO.deleted(coupon.getId());
                        } catch (NotExistException e) {
                            e.printStackTrace();
                        }
                        couponDBDAO.deleteCouponPurchaseByCouponId(coupon.getId());
                    }

                }
                TimeUnit.DAYS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

        }

    }

    public void stop(){
        stop =true;
    }
}
