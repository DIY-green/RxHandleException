package com.cheng.rxhandle;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author liwangcheng
 * @date 2019-08-30 07:19.
 */
public class IntegerObserver implements Observer<Integer> {

    private static final String TAG = "IntegerObserver";

    private Order mOrder;
    private Contract.View mView;

    public IntegerObserver(Contract.View view, Order order) {
        mView = view;
        mOrder = order;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(Integer orderVersion) {
        Log.e(TAG, "onNext - Current orderVersion = " + orderVersion + "，DB orderVersion = " + NetApi.sOrderVersion);
        toast("操作成功，更新订单号 - " + orderVersion);
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError - Current orderVersion = " + mOrder.orderVersion + "，DB orderVersion = " + NetApi.sOrderVersion);
        Log.e(TAG, "出现异常了", e);
        if (e instanceof UpdateVersionException) {
            Log.e(TAG, "从UpdateVersionException中取出最新orderVersion - " + Thread.currentThread());
            updateOrderVersion(((UpdateVersionException) e).orderVersion);
            toast(((UpdateVersionException) e).errorMsg);
        } else if (e instanceof ApiException) {
            toast(((ApiException) e).errorMsg);
        } else {
            toast("未知异常");
        }
    }

    @Override
    public void onComplete() {
        Log.e(TAG, "onComplete");
        mView.hideLoading();
    }

    private void toast(String message) {
        boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();
        Log.e(TAG, "show Toast - 是否在主线程中调用:" + isMainThread);
        mView.showToast(message);
        mView.hideLoading();
    }

    private void updateOrderVersion(int orderVersion) {
        mOrder.orderVersion = orderVersion;
    }

}
