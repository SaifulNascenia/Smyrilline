package com.mcp.smyrilline.listener;

import com.onyxbeacon.rest.model.content.Coupon;
import java.util.ArrayList;

/**
 * Created by raqib on 8/4/17.
 */

public interface CouponsListener {

    void onCouponReceived(Coupon coupon);

    void onDeliveredCouponsReceived(ArrayList<Coupon> coupons);
}
