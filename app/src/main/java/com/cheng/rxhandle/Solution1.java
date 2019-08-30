package com.cheng.rxhandle;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author liwangcheng
 * @date 2019-08-30 06:42.
 */
public class Solution1 {

    private NetApi mNetApi = new NetApi();
    private Order mOrder;

    public Solution1() {
        mOrder = new Order(1, "A001");
        NetApi.sOrderVersion = mOrder.orderVersion;
    }

    public void testPit1(Observer<Integer> observer) {
        mNetApi.verify(mOrder)
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer orderVersion) throws Exception {
                        updateOrderVersion(orderVersion);
                        return mNetApi.prePay(mOrder);
                    }
                })
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer orderVersion) throws Exception {
                        updateOrderVersion(orderVersion);
                        return mNetApi.pay(mOrder);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void testPit2(Observer<Integer> observer) {
        mNetApi.verify(mOrder)
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer orderVersion) throws Exception {
                        updateOrderVersion(orderVersion);
                        return mNetApi.prePay(mOrder);
                    }
                })
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer orderVersion) throws Exception {
                        updateOrderVersion(orderVersion);
                        return mNetApi.pay(mOrder);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        int orderVersion = getLatestOrderVersion();
                        throw new UpdateVersionException(orderVersion, throwable);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void testPit3(final Observer<Integer> observer) {
        mNetApi.verify(mOrder)
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer orderVersion) throws Exception {
                        updateOrderVersion(orderVersion);
                        return mNetApi.prePay(mOrder);
                    }
                })
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer orderVersion) throws Exception {
                        updateOrderVersion(orderVersion);
                        return mNetApi.pay(mOrder);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        int orderVersion = getLatestOrderVersion();
                        observer.onError(new UpdateVersionException(orderVersion, throwable));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void fillPit(final Observer<Integer> observer) {
        mNetApi.verify(mOrder)
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer orderVersion) throws Exception {
                        updateOrderVersion(orderVersion);
                        return mNetApi.prePay(mOrder);
                    }
                })
                .flatMap(new Function<Integer, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(Integer orderVersion) throws Exception {
                        updateOrderVersion(orderVersion);
                        return mNetApi.pay(mOrder);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(final Throwable throwable) throws Exception {
                        final int orderVersion = getLatestOrderVersion();
                        Observable.just(1).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                                observer.onError(new UpdateVersionException(orderVersion, throwable));
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
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
