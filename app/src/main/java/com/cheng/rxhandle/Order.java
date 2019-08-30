package com.cheng.rxhandle;

/**
 * @author liwangcheng
 * @date 2019-08-30 06:44.
 */
public class Order {
    public int orderVersion;
    public String orderId;

    public Order() {
    }

    public Order(int orderVersion, String orderId) {
        this.orderVersion = orderVersion;
        this.orderId = orderId;
    }
}
