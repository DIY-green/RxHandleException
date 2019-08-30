package com.cheng.rxhandle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Solution1 mSolution1;
    private Solution2 mSolution2;
    private Solution3 mSolution3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mSolution1 = new Solution1();
        mSolution2 = new Solution2();
        mSolution3 = new Solution3();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_s1_pit1:
                testS1Pit1();
                break;
            case R.id.btn_s1_pit2:
                testS1Pit2();
                break;
            case R.id.btn_s1_pit3:
                testS1Pit3();
                break;
            case R.id.btn_solution1:
                testSolution1();
                break;
            case R.id.btn_s2_pit1:
                testS2Pit1();
                break;
            case R.id.btn_s2_pit2:
                testS2Pit2();
                break;
            case R.id.btn_s2_pit3:
                testS2Pit3();
                break;
            case R.id.btn_solution2:
                testSolution2();
                break;
            case R.id.btn_s3_pit1:
                testS3Pit1();
                break;
            case R.id.btn_s3_pit2:
                testS3Pit2();
                break;
            case R.id.btn_s3_pit3:
                testS3Pit3();
                break;
            case R.id.btn_solution3:
                testSolution3();
                break;
            default:break;
        }
    }

    private void testS1Pit1() {
        mSolution1.testPit1(new IntegerObserver(this, mSolution1.getOrder()));
    }

    private void testS1Pit2() {
        mSolution1.testPit2(new IntegerObserver(this, mSolution1.getOrder()));
    }

    private void testS1Pit3() {
        mSolution1.testPit3(new IntegerObserver(this, mSolution1.getOrder()));
    }

    private void testSolution1() {
        mSolution1.fillPit(new IntegerObserver(this, mSolution1.getOrder()));
    }

    private void testS2Pit1() {
        mSolution2.testPit1(new IntegerObserver(this, mSolution1.getOrder()));
    }

    private void testS2Pit2() {
        mSolution2.testPit2(new IntegerObserver(this, mSolution2.getOrder()));
    }

    private void testS2Pit3() {
        mSolution2.testPit3(new ResultObserver<Result<Integer>>(this, mSolution2.getOrder()));
    }

    private void testSolution2() {
        mSolution2.fillPit(new IntegerObserver(this, mSolution2.getOrder()));
    }

    private void testS3Pit1() {
        mSolution3.testPit1(new IntegerObserver(this, mSolution3.getOrder()));
    }

    private void testS3Pit2() {
        mSolution3.testPit2(new IntegerObserver(this, mSolution3.getOrder()));
    }

    private void testS3Pit3() {
        mSolution3.testPit3(new IntegerObserver(this, mSolution3.getOrder()));
    }

    private void testSolution3() {
        mSolution3.fillPit(new IntegerObserver(this, mSolution3.getOrder()));
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
