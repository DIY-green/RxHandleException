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

    /**
     * 中间某个接口失败，接收到异常了，但是orderVersion更新丢失了
     *
     * @param observer
     */
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
    /**
     * 中间某个接口失败，在doOnError中抛出UpdateVersionException包裹orderVersion，
     * 希望调用方捕获该异常，但是异常被转换了，orderVersion信息丢失
     *
     * @param observer
     */
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

    /**
     * doOnError 受 subscribeOn/observerOn 影响，调用线程不确定
     * 可能导致 observer.onError 在非目标线程调用
     *
     * @param observer
     */
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
                .onExceptionResumeNext(new ObservableSource<Integer>() {
                    @Override
                    public void subscribe(Observer<? super Integer> observer) {
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 在doOnError中主动切换线程保证 observer.onError 在目标线程调用
     *
     * @param observer
     */
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
                        // 指定subscribeOn为目标线程，确保Observer#onError在目标线程中调用
                        Observable.just(0).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(Object o) throws Exception {
                                observer.onError(new UpdateVersionException(orderVersion, throwable));
                            }
                        });
                    }
                })// 防止onError多次调用
                .onExceptionResumeNext(new ObservableSource<Integer>() {
                    @Override
                    public void subscribe(Observer<? super Integer> observer) {

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

    public void resetOrder() {
        mOrder.orderVersion = 1;
    }
}
