package com.yahoo.melnichuk.a.kommunalchik.CustomViews;

import android.app.Activity;
import android.content.res.Configuration;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

/**
 * Created by xxxalxxx on 13.02.2015.
 */
public class FragmentKeyboard {

    public static KeyboardView mKeyboardView;
    private Activity mHostActivity;
    private boolean portrait = true;
    private ScrollFragment mFragment;
    public static List<Keyboard.Key> mKeyboardKeys;
    private TextView mTextView;
    private View mContainerView;

    public final static int CODE_DELETE = -5;
    public final static int CODE_DECIMAL = 55000;
    public final static int CODE_PERCENT = 55001;
    public final static int CODE_FRACTION = 55002;
    public final static int CODE_NEXT = 10;
    public final static int CODE_DOT = 46;
    public final static int CODE_DIVIDE = 47;
    public final static int CODE_ZERO = 48;

    private static int index_decimal = -1;
    private static int index_percent = -1;
    private static int index_fraction = -1;

    /** The key (code) handler. */
    private KeyboardView.OnKeyboardActionListener mOnKeyboardActionListener = new KeyboardView.OnKeyboardActionListener() {

        @Override public void onKey(int primaryCode, int[] keyCodes) {

            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
            if( focusCurrent==null || focusCurrent.getClass()!= NumberFormatEditText.class ) return;

            mTextView =(TextView) ((ViewGroup)focusCurrent.getParent()).getChildAt(2);
            NumberFormatEditText edittext = (NumberFormatEditText) focusCurrent;
            Editable editable = edittext.getText();
            int start = edittext.getSelectionStart();

            switch(primaryCode) {
                case CODE_DELETE:
                    if (editable != null && start > 0) {
                        editable.delete(start - 1, start);
                    }
                    break;
                //TODO:reformat editable on type change
                //TODO:strip zeros
                case CODE_FRACTION:
                    updateKeyStatus(CODE_FRACTION);
                    mTextView.setText(R.string.format_fraction);
                    edittext.setFraction();
                    break;

                case CODE_PERCENT:
                    updateKeyStatus(CODE_PERCENT);
                    mTextView.setText(R.string.format_percent);
                    edittext.setPercent();
                    break;

                case CODE_DECIMAL:
                    updateKeyStatus(CODE_DECIMAL);
                    mTextView.setText(R.string.format_decimal);
                    edittext.setDecimal();
                    break;

                case CODE_NEXT:
                    View focusNew = edittext.focusSearch(View.FOCUS_FORWARD);
                    if (focusNew != null)
                        focusNew.requestFocus();
                    break;

                case CODE_DOT:
                    //TODO: hande two dots in fraction

                    if(edittext.isFraction())
                        Toast.makeText(mFragment.v.getContext(),R.string.err_fraction_decimal,Toast.LENGTH_LONG).show();
                    else if(editable.toString().contains("."))
                        Toast.makeText(mFragment.v.getContext(),R.string.err_decimal_count,Toast.LENGTH_LONG).show();
                    else editable.insert(start, Character.toString((char) primaryCode) );
                    break;

                case CODE_DIVIDE:
                    if(!edittext.isFraction())
                        Toast.makeText(mFragment.v.getContext(),R.string.err_fraction_format,Toast.LENGTH_LONG).show();
                    else if(editable.toString().contains("/"))
                        Toast.makeText(mFragment.v.getContext(),R.string.err_fraction_count,Toast.LENGTH_LONG).show();
                    else editable.insert(start, Character.toString((char) primaryCode) );
                    break;
                case CODE_ZERO:
                    start = edittext.getSelectionStart();
                    editable.insert(start, Character.toString((char) primaryCode) );
                    edittext.setText(trimZeros(editable.toString() ));
                    edittext.setSelection( start+1<=editable.length()?start+1 : editable.length() );
                    break;
                default:
                    editable.insert(start, Character.toString((char) primaryCode) );
                    break;
            }

        }

        @Override public void onPress(int arg0) {}
        @Override public void onRelease(int primaryCode) {}
        @Override public void onText(CharSequence text) {}
        @Override public void swipeDown() {}
        @Override public void swipeLeft() {}
        @Override public void swipeRight() {}
        @Override public void swipeUp() {}
    };


    public FragmentKeyboard(ScrollFragment f) {

        mFragment = f;
        mHostActivity=  mFragment.getActivity();
        mKeyboardView = (KeyboardView) mHostActivity.findViewById(R.id.keyboardview);
        if(f.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            portrait = true;
            mKeyboardView.setKeyboard(new Keyboard(mHostActivity, R.xml.keyboard_portrait));
        } else {
            portrait = false;
            mKeyboardView.setKeyboard(new Keyboard(mHostActivity,R.xml.keyboard_landscape));
        }
        setupKeyStatus();
        mContainerView = mHostActivity.findViewById(R.id.container);
        mKeyboardView.setPreviewEnabled(false); // NOTE Do not show the preview balloons
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        // Hide the standard keyboard_portrait initially
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /** Returns whether the CustomKeyboard is visible. */
    public static boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /** Make the CustomKeyboard visible, and hide the system keyboard_portrait for view v. */
    public void showCustomKeyboard( View v, boolean withDelay ) {

        if (v != null){
            ((InputMethodManager) mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
            NumberFormatEditText view = (NumberFormatEditText)v;
            if(view.isFraction()) updateKeyStatus(CODE_FRACTION);
            else if(view.isPercent()) updateKeyStatus(CODE_PERCENT);
            else updateKeyStatus(CODE_DECIMAL);
            if(withDelay) showKeyboardWithDelay();
            else mKeyboardView.setVisibility(View.VISIBLE);
            mKeyboardView.setEnabled(true);
        }
    }

    /** Make the CustomKeyboard invisible. */
    public static void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
    }

    /**
     * Register <var>EditText<var> with resource id <var>resid</var> (on the hosting activity) for using this custom keyboard_portrait.
     *
     * @param resid The resource id of the EditText that registers to the custom keyboard_portrait.
     */

    public void registerEditText(int resid,ViewGroup vg) {
        NumberFormatEditText edittext= (NumberFormatEditText) vg.findViewById(resid);
        // Make the custom keyboard_portrait appear
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom keyboard_portrait when the edit box gets focus, but also hide it when the edit box loses focus
            @Override public void onFocusChange(View v, boolean hasFocus) {
                int heightDiff = mContainerView.getRootView().getHeight() - mContainerView.getHeight();
                if(hasFocus)
                    if(heightDiff>100) showCustomKeyboard(v,true);
                    else showCustomKeyboard(v,false);
                else hideCustomKeyboard();
            }
        });


        // Disable standard keyboard_portrait hard way
        // NOTE There is also an easy way: 'edittext.setInputType(InputType.TYPE_NULL)' (but you will not have a cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
        edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                NumberFormatEditText edittext = (NumberFormatEditText) v;

                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard_portrait
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                float x = event.getX();
                float y = event.getY();
                edittext.setSelection(getOffsetForPosition(edittext,x,y));
                // edittext.requestFocus();

                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }


    public void registerEditText(int resid,View v) {
        NumberFormatEditText edittext= (NumberFormatEditText) v.findViewById(resid);
        // Make the custom keyboard_portrait appear
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom keyboard_portrait when the edit box gets focus, but also hide it when the edit box loses focus
            @Override public void onFocusChange(View v, boolean hasFocus) {
                int heightDiff = mContainerView.getRootView().getHeight() - mContainerView.getHeight();
                if(hasFocus)
                    if(heightDiff>100) showCustomKeyboard(v,true);
                    else showCustomKeyboard(v,false);
                else hideCustomKeyboard();
            }
        });

        edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                NumberFormatEditText edittext = (NumberFormatEditText) v;

                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard_portrait
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                float x = event.getX();
                float y = event.getY();
                // edittext.setSelection(getOffsetForPosition(edittext,x,y));
                //  edittext.requestFocus();

                return true; // Consume touch event
            }
        });

        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    String trimZeros(String s){
        while(1<s.length() && s.substring(0, 1).equals("0") && 1!= s.indexOf(".") )
            s = s.substring(1, s.length());
        return s;
    }


    public void setupKeyStatus(){
        new Thread(new Runnable(){
            @Override
            public void run() {
                mKeyboardKeys = mKeyboardView.getKeyboard().getKeys();
                for(int i=0;i<mKeyboardKeys.size();i++){
                    if(mKeyboardKeys.get(i).codes[0]==CODE_DECIMAL)
                        index_decimal = i;
                    if(mKeyboardKeys.get(i).codes[0]==CODE_FRACTION)
                        index_fraction = i;
                    if(mKeyboardKeys.get(i).codes[0]==CODE_PERCENT)
                        index_percent = i;
                }
                mKeyboardKeys.get(index_decimal).on=true;
                mKeyboardView.post(new Runnable() {
                    public void run() {
                        mKeyboardView.invalidateAllKeys();
                    }
                });
            }
        }).run();
    }

    public static void updateKeyStatus(int key){
        if(key == CODE_FRACTION) {
            mKeyboardKeys.get(index_fraction).on=true;
            mKeyboardKeys.get(index_decimal).on=false;
            mKeyboardKeys.get(index_percent).on=false;
        }
        if(key == CODE_DECIMAL) {
            mKeyboardKeys.get(index_decimal).on = true;
            mKeyboardKeys.get(index_fraction).on = false;
            mKeyboardKeys.get(index_percent).on = false;
        }
        if(key == CODE_PERCENT) {
            mKeyboardKeys.get(index_percent).on = true;
            mKeyboardKeys.get(index_decimal).on = false;
            mKeyboardKeys.get(index_fraction).on = false;
        }
        mKeyboardView.invalidateAllKeys();
    }

    public int getOffsetForPosition(EditText et, float x, float y) {
        if (et.getLayout() == null)
            return -1;

        final int line = getLineAtCoordinate(et, y);
        final int offset = getOffsetAtCoordinate(et, line, x);
        return offset;
    }

    private int getOffsetAtCoordinate(EditText et, int line, float x) {
        x = convertToLocalHorizontalCoordinate(et, x);
        return et.getLayout().getOffsetForHorizontal(line, x);
    }

    private float convertToLocalHorizontalCoordinate(EditText et, float x) {
        x -= et.getTotalPaddingLeft();
        // Clamp the position to inside of the view.
        x = Math.max(0.0f, x);
        x = Math.min(et.getWidth() - et.getTotalPaddingRight() - 1, x);
        x += et.getScrollX();
        return x;
    }

    private int getLineAtCoordinate(EditText et, float y) {
        y -= et.getTotalPaddingTop();
        // Clamp the position to inside of the view.
        y = Math.max(0.0f, y);
        y = Math.min(et.getHeight() - et.getTotalPaddingBottom() - 1, y);
        y += et.getScrollY();
        return et.getLayout().getLineForVertical((int) y);
    }

    void showKeyboardWithDelay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    mKeyboardView.post(new Runnable() {
                        public void run() {
                            mKeyboardView.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (Exception e) {
                    mKeyboardView.post(new Runnable() {
                        public void run() {
                            mKeyboardView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        }).start();
    }

}