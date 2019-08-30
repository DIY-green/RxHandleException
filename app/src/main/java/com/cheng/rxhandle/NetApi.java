package com.cheng.rxhandle;

import android.os.SystemClock;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * @author liwangcheng
 * @date 2019-08-30 06:43.
 */
public class NetApi {

    public static int sOrderVersion = 0;

    public Observable<Integer> verify(final Order order) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observer) throws Exception {
                if (checkOrderVersion(observer, order)) {
                    return;
                }
                int orderVersion = order.orderVersion + 1;
                sOrderVersion = orderVersion;
                SystemClock.sleep(1000);
                observer.onNext(orderVersion);
            }
        });
    }

    public Integer syncVerify(Order order) throws ApiException {
        checkOrderVersion(order);
        int orderVersion = order.orderVersion + 1;
        sOrderVersion = orderVersion;
        SystemClock.sleep(1000);
        return orderVersion;
    }

    public Observable<Integer> prePay(final Order order) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observer) throws Exception {
                if (checkOrderVersion(observer, order)) {
                    return;
                }
                int orderVersion = order.orderVersion + 1;
                sOrderVersion = orderVersion;
                SystemClock.sleep(1000);
                observer.onNext(orderVersion);
            }
        });
    }

    public Integer syncPrePay(Order order) throws ApiException {
        checkOrderVersion(order);
        int orderVersion = order.orderVersion + 1;
        sOrderVersion = orderVersion;
        SystemClock.sleep(1000);
        return orderVersion;
    }

    public Observable<Integer> pay(final Order order) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observer) throws Exception {
                if (checkOrderVersion(observer, order)) {
                    return;
                }
                int orderVersion = order.orderVersion + 1;
                sOrderVersion = orderVersion;
                SystemClock.sleep(1000);
                observer.onError(new ApiException(100, "权限异常"));
            }
        });
    }

    public Integer syncPay(Order order) throws ApiException {
        checkOrderVersion(order);
        int orderVersion = order.orderVersion + 1;
        sOrderVersion = orderVersion;
        SystemClock.sleep(1000);
        throw new ApiException(100, "权限异常");
    }

    public Observable<Order> queryOrder(final String orderId) {
        return Observable.create(new ObservableOnSubscribe<Order>() {
            @Override
            public void subscribe(ObservableEmitter<Order> observer) throws Exception {
                Order order = syncQueryOrder(orderId);
                observer.onNext(order);
            }
        });
    }

    private Order syncQueryOrder(String orderId) throws ApiException {
        Order order = new Order();
        order.orderVersion = sOrderVersion == 0 ? 100 : sOrderVersion;
        order.orderId = orderId;
        SystemClock.sleep(1000);
        return order;
    }

    private boolean checkOrderVersion(ObservableEmitter<Integer> observer, Order order) {
        if (order.orderVersion != sOrderVersion) {
            observer.onError(new ApiException(101, "订单版本已变更"));
            return true;
        }
        return false;
    }

    private void checkOrderVersion(Order order) throws ApiException {
        if (order.orderVersion != sOrderVersion) {
            throw new ApiException(101, "订单版本已变更");
        }
    }

}
