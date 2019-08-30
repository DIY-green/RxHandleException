package com.cheng.rxhandle;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author liwangcheng
 * @date 2019-08-30 08:41.
 */
public class Solution3 {

    private NetApi mNetApi = new NetApi();
    private Order mOrder;

    public Solution3() {
        mOrder = new Order(1, "A001");
        NetApi.sOrderVersion = mOrder.orderVersion;
    }

    public void testPit1(Observer<Integer> observer) {
        asyncCheckout(mOrder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void testPit2(Observer<Integer> observer) {

    }

    public void testPit3(Observer<Integer> observer) {

    }

    public void fillPit(Observer<Integer> observer) {
        asyncCheckout2(mOrder)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private Observable<Integer> asyncCheckout(final Order order) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                try {
                    updateOrderVersion(mNetApi.syncVerify(order));
                    updateOrderVersion(mNetApi.syncPrePay(order));
                    updateOrderVersion(mNetApi.syncPay(order));
                    emitter.onNext(mOrder.orderVersion);
                } catch (Exception ex) {
                    emitter.onError(ex);
                }
            }
        });
    }
    
    private Observable<Integer> asyncCheckout2(final Order order) {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                try {
                    updateOrderVersion(mNetApi.syncVerify(order));
                    updateOrderVersion(mNetApi.syncPrePay(order));
                    updateOrderVersion(mNetApi.syncPay(order));
                    emitter.onNext(mOrder.orderVersion);
                } catch (Exception ex) {
                    int orderVersion = getLatestOrderVersion();
                    emitter.onError(new UpdateVersionException(orderVersion, ex));
                }
            }
        });
    }

    private void updateOrderVersion(int orderVersion) {
        mOrder.orderVersion = orderVersion;
    }

    public Order getOrder() {
        return mOrder;
    }

    private int getLatestOrderVersion() {
        return NetApi.sOrderVersion;
    }

}
