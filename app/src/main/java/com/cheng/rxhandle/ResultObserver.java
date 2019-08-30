package com.cheng.rxhandle;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author liwangcheng
 * @date 2019-08-30 08:24.
 */
public class ResultObserver<T> implements Observer<T> {

    private static final String TAG = "ResultObserver";

    private Order mOrder;
    private Contract.View mView;
    private Callback<T> mCallback;

    public interface Callback<T> {
        void callback(T t);
    }

    public ResultObserver(Contract.View view, Order order) {
        mView = view;
        mOrder = order;
    }

    public void setCallback(Callback<T> callback) {
        mCallback = callback;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T result) {
        Log.e(TAG, "onNext - Current result = " + result + "，DB orderVersion = " + NetApi.sOrderVersion);
        if (null != mCallback) {
            mCallback.callback(result);
        }
        toast("操作成功，result - " + result);
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
