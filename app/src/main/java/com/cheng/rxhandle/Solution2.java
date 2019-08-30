package com.cheng.rxhandle;

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
public class Solution2 {

    private NetApi mNetApi = new NetApi();
    private Order mOrder;
    private ApiException mException;

    public Solution2() {
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
                .onExceptionResumeNext(new ObservableSource<Integer>() {
                    @Override
                    public void subscribe(Observer<? super Integer> observer) {
                        int orderVersion = getLatestOrderVersion();
                        observer.onError(new UpdateVersionException(orderVersion, "订单结账异常"));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void testPit3(final Observer<Result<Integer>> observer) {
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
                .map(new Function<Integer, Result<Integer>>() {
                    @Override
                    public Result<Integer> apply(Integer orderVersion) throws Exception {
                        updateOrderVersion(orderVersion);
                        return Result.success(orderVersion);
                    }
                })
                .onExceptionResumeNext(new ObservableSource<Result<Integer>>() {
                    @Override
                    public void subscribe(Observer<? super Result<Integer>> observer) {
                        int orderVersion = getLatestOrderVersion();
                        observer.onNext(Result.failure(orderVersion));
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
                    public void accept(Throwable throwable) throws Exception {
                        int orderVersion = getLatestOrderVersion();
                        mException = new UpdateVersionException(orderVersion, throwable);
                    }
                })
                .onExceptionResumeNext(new ObservableSource<Integer>() {
                    @Override
                    public void subscribe(Observer<? super Integer> observer) {
                        observer.onError(mException);
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
