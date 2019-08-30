package com.cheng.rxhandle;

/**
 * @author liwangcheng
 * @date 2019-08-30 15:37.
 */
public interface Contract {

    interface View {
        void showToast(String msg);
        void hideLoading();
    }

}
