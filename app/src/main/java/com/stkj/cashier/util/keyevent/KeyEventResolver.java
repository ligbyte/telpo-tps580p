package com.stkj.cashier.util.keyevent;

import android.os.Handler;
import android.text.TextUtils;
import android.view.InputDevice;
import android.view.KeyEvent;

import com.stkj.cashier.util.util.LogUtils;


/**
 * 扫码枪事件解析类 by chen
 */
public class KeyEventResolver {

    private final static long MESSAGE_DELAY = 50;//延迟50ms，判断扫码是否完成。
    private final StringBuffer mStringBufferResult;//扫码内容
    private final Handler mHandler;
    private final Runnable mScanningFishedRunnable;
    boolean hasShift = false;
    private boolean mCaps;//大小写区分
    private OnScanSuccessListener mOnScanSuccessListener;

    public KeyEventResolver() {
        mStringBufferResult = new StringBuffer();
        mHandler = new Handler();
        mScanningFishedRunnable = new Runnable() {
            @Override
            public void run() {
                performScanSuccess();
            }
        };
    }

    public KeyEventResolver(OnScanSuccessListener onScanSuccessListener) {
        mOnScanSuccessListener = onScanSuccessListener;
        mStringBufferResult = new StringBuffer();
        mHandler = new Handler();
        mScanningFishedRunnable = new Runnable() {
            @Override
            public void run() {
                performScanSuccess();
            }
        };
    }

    /**
     * 返回扫码成功后的结果
     */
    private void performScanSuccess() {
        String barcode = mStringBufferResult.toString();
        if (mOnScanSuccessListener != null) {
            mOnScanSuccessListener.onScanSuccess(barcode);
        }
        mStringBufferResult.setLength(0);
    }

    /**
     * 扫码枪事件解析
     */
    public void analysisKeyEvent(android.view.KeyEvent event) {
        int keyCode = event.getKeyCode();
        //字母大小写判断
        checkLetterStatus(event);
        if (event.getAction() == android.view.KeyEvent.ACTION_DOWN) {
            //char aChar = getInputCode(event);
            LogUtils.e("键盘" + getInputCode(event));
            char aChar = ' ';
            if (KeyDownUtil.isLeftShift(event.getKeyCode())) {
                hasShift = true;
                return;
            } else {
                if (hasShift) {
                    aChar = KeyDownUtil.getCharFromDictionary(KeyDownUtil.KEYCODE_SHIFT_LEFT, event.getKeyCode());
                } else {
                    aChar = KeyDownUtil.getCharFromDictionary(event.getKeyCode());
                }
                hasShift = false;
            }
            if (aChar != 0) {
                mStringBufferResult.append(aChar);
            }
            LogUtils.e("键盘" + mStringBufferResult.toString());
            if (keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
                //若为回车键，直接返回
                mHandler.removeCallbacks(mScanningFishedRunnable);
                mHandler.post(mScanningFishedRunnable);
            }
            //else {
            //    //延迟post，若50ms内，有其他事件
            //    mHandler.removeCallbacks(mScanningFishedRunnable);
            //    mHandler.postDelayed(mScanningFishedRunnable, MESSAGE_DELAY);
            //}
        }
    }

    //检查shift键
    private void checkLetterStatus(android.view.KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == android.view.KeyEvent.KEYCODE_SHIFT_RIGHT || keyCode == android.view.KeyEvent.KEYCODE_SHIFT_LEFT) {
            //按着shift键，表示大写,松开shift键，表示小写
            mCaps = event.getAction() == android.view.KeyEvent.ACTION_DOWN;
        }
    }


    //获取扫描内容
    public String getInputCode(android.view.KeyEvent event) {
        int keyCode = event.getKeyCode();
        char aChar;
        if (keyCode >= android.view.KeyEvent.KEYCODE_A && keyCode <= android.view.KeyEvent.KEYCODE_Z) {
            //字母
            aChar = (char) ((mCaps ? 'A' : 'a') + keyCode - android.view.KeyEvent.KEYCODE_A);
        } else if (keyCode >= android.view.KeyEvent.KEYCODE_0 && keyCode <= android.view.KeyEvent.KEYCODE_9) {
            //数字
            aChar = (char) ('0' + keyCode - android.view.KeyEvent.KEYCODE_0);
        } else {
            //其他符号
            switch (keyCode) {
                case android.view.KeyEvent.KEYCODE_PERIOD:
                case android.view.KeyEvent.KEYCODE_FORWARD_DEL:
                case android.view.KeyEvent.KEYCODE_NUMPAD_DOT:
                    aChar = '.';
                    break;
                case android.view.KeyEvent.KEYCODE_MINUS:
                    aChar = mCaps ? '_' : '-';
                    break;
                case android.view.KeyEvent.KEYCODE_SLASH:
                    aChar = '/';
                    break;
                case android.view.KeyEvent.KEYCODE_DEL:
                    //
                    aChar = '-';
                    return "删除";
                //break;
                case android.view.KeyEvent.KEYCODE_NUMPAD_ADD:
                    aChar = '+';
                    break;
                case android.view.KeyEvent.KEYCODE_ENTER:
                case android.view.KeyEvent.KEYCODE_NUMPAD_ENTER:
                    //确认
                    aChar = '=';
                    return "确认";
                //break;
                case android.view.KeyEvent.KEYCODE_DPAD_UP:
                    //向上
                    aChar = "向上".toCharArray()[0];
                    // break;
                    return "向上";
                case android.view.KeyEvent.KEYCODE_DPAD_DOWN:
                    //向下
                    aChar = "向下".toCharArray()[0];
                    // break;
                    return "向下";
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    //向上
                    aChar = "向左".toCharArray()[0];
                    // break;
                    return "向左";
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    //向下
                    aChar = "向右".toCharArray()[0];
                    // break;
                    return "向右";
                case android.view.KeyEvent.KEYCODE_F1:
                case KeyEvent.KEYCODE_CALCULATOR:
                    // 设置
                    return "设置";
                case android.view.KeyEvent.KEYCODE_ESCAPE:
                case android.view.KeyEvent.KEYCODE_SEARCH:
                    //功能
                    aChar = "功能".toCharArray()[0];
                    // break;
                    return "功能";
                case android.view.KeyEvent.KEYCODE_BACK:
                    //取消
                    aChar = "取消".toCharArray()[0];
                    // break;
                    return "取消";
                case android.view.KeyEvent.KEYCODE_F2:
                    //统计
                    aChar = "统计".toCharArray()[0];
                    return "统计";


                case android.view.KeyEvent.KEYCODE_BACKSLASH:
                    aChar = mCaps ? '|' : '\\';
                    break;
                case KeyEvent.KEYCODE_NUMPAD_0:
                    return "0";
                case KeyEvent.KEYCODE_NUMPAD_1:
                    return "1";
                case KeyEvent.KEYCODE_NUMPAD_2:
                    //未锁定NumLock 不处理按键事件
                    if (isAigoKeyboard(event.getDevice()) && !event.isNumLockOn()) {
                        return "";
                    }
                    return "2";
                case KeyEvent.KEYCODE_NUMPAD_3:
                    return "3";
                case KeyEvent.KEYCODE_NUMPAD_4:
                    //未锁定NumLock 不处理按键事件
                    if (isAigoKeyboard(event.getDevice()) && !event.isNumLockOn()) {
                        return "";
                    }
                    return "4";
                case KeyEvent.KEYCODE_NUMPAD_5:
                    return "5";
                case KeyEvent.KEYCODE_NUMPAD_6:
                    //未锁定NumLock 不处理按键事件
                    if (isAigoKeyboard(event.getDevice()) && !event.isNumLockOn()) {
                        return "";
                    }
                    return "6";
                case KeyEvent.KEYCODE_NUMPAD_7:
                    return "7";
                case KeyEvent.KEYCODE_NUMPAD_8:
                    //未锁定NumLock 不处理按键事件
                    if (isAigoKeyboard(event.getDevice()) && !event.isNumLockOn()) {
                        return "";
                    }
                    return "8";
                case KeyEvent.KEYCODE_NUMPAD_9:
                    return "9";
                default:
                    aChar = 0;
                    break;
            }
        }
        return String.valueOf(aChar);
    }

    private boolean isAigoKeyboard(InputDevice inputDevice) {
        if (inputDevice == null) {
            return false;
        }
        //A18 BT5.0 Keyboard |
        //Compx 2.4G Wireless Receiver  | mVendorId 9639 mProductId: 64112
        return inputDevice.getName().contains("A18") || inputDevice.getName().contains("Compx 2.4G");
    }

    public void onDestroy() {
        mHandler.removeCallbacks(mScanningFishedRunnable);
        mOnScanSuccessListener = null;
    }


    public interface OnScanSuccessListener {
        void onScanSuccess(String barcode);
    }
}





