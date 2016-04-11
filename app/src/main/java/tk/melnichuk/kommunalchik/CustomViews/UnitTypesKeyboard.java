package tk.melnichuk.kommunalchik.CustomViews;

import android.app.Activity;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import tk.melnichuk.kommunalchik.MainActivity;
import tk.melnichuk.kommunalchik.R;

/**
 * When an activity hosts a keyboardView, this class allows several EditText's to register for it.
 *
 * @author Maarten Pennings
 * @date   2012 December 23
 */
public class UnitTypesKeyboard {

    /** A link to the KeyboardView that is used to render this CustomKeyboard. */
    private KeyboardView mKeyboardView;
    /** A link to the activity that hosts the {@link #mKeyboardView}. */
    private Activity     mHostActivity;

    public List<Keyboard.Key> mKeyboardKeys;
    /** The key (code) handler. */

    public final static int CODE_DELETE   = -5;
    public final static int CODE_DECIMAL  = 55000;
    public final static int CODE_PERCENT  = 55001;
    public final static int CODE_FRACTION = 55002;
    public final static int CODE_NEXT     = 10;
    public final static int CODE_DOT      = 46;
    public final static int CODE_DIVIDE   = 47;
    public final static int CODE_ZERO     = 48;

    private  int index_decimal = -1;
    private  int index_percent = -1;
    private  int index_fraction = -1;

    private OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {

        @Override public void onKey(int primaryCode, int[] keyCodes) {
            // NOTE We can say '<Key android:codes="49,50" ... >' in the xml file; all codes come in keyCodes, the first in this list in primaryCode
            // Get the EditText and its Editable
            View focusCurrent = mHostActivity.getWindow().getCurrentFocus();
            if( focusCurrent==null || focusCurrent.getClass()!=UnitTypesEditText.class ) return;
            UnitTypesEditText edittext = (UnitTypesEditText) focusCurrent;
            ViewGroup parent  = (ViewGroup) edittext.getParent();
            TextView textView = (TextView) parent.findViewById(R.id.format);
            Editable editable = edittext.getText();
            int start = edittext.getSelectionStart();
            // Apply the key to the edittext
            switch(primaryCode) {
                case CODE_DELETE:
                    if (editable != null && start > 0) {
                        editable.delete(start - 1, start);
                    }
                    break;
                //TODO:reformat editable on type change
                //TODO:strip zeros
                case CODE_FRACTION:
                    updateKeyStatus(false,false,true);
                    textView.setText(R.string.format_fraction);
                    edittext.setFraction();
                    break;

                case CODE_PERCENT:
                    updateKeyStatus(false,true,false);
                    textView.setText(R.string.format_percent);
                    edittext.setPercent();
                    break;

                case CODE_DECIMAL:
                    updateKeyStatus(true,false,false);
                    textView.setText(R.string.format_decimal);
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
                        Toast.makeText(edittext.getContext(),R.string.err_fraction_decimal,Toast.LENGTH_LONG).show();
                    else if(editable.toString().contains("."))
                        Toast.makeText(edittext.getContext(),R.string.err_decimal_count,Toast.LENGTH_LONG).show();
                    else editable.insert(start, Character.toString((char) primaryCode) );
                    break;

                case CODE_DIVIDE:
                    if(!edittext.isFraction())
                        Toast.makeText(edittext.getContext(),R.string.err_fraction_format,Toast.LENGTH_LONG).show();
                    else if(editable.toString().contains("/"))
                        Toast.makeText(edittext.getContext(), R.string.err_fraction_count, Toast.LENGTH_LONG).show();
                    else editable.insert(start, Character.toString((char) primaryCode) );
                    break;
                case CODE_ZERO:
                    start = edittext.getSelectionStart();
                    editable.insert(start, Character.toString((char) primaryCode) );
                    edittext.setText(trimZeros(editable.toString() ));

                    edittext.setSelection( start+1<=editable.length() ? start+1 : editable.length() );
                    break;
                default:
                    editable.insert(start, Character.toString((char) primaryCode) );
                    break;
            }
        }

        @Override public void onPress(int arg0) {
        }

        @Override public void onRelease(int primaryCode) {
        }

        @Override public void onText(CharSequence text) {
        }

        @Override public void swipeDown() {
        }

        @Override public void swipeLeft() {
        }

        @Override public void swipeRight() {
        }

        @Override public void swipeUp() {
        }
    };

    /**
     * Create a custom keyboard, that uses the KeyboardView (with resource id <var>viewid</var>) of the <var>host</var> activity,
     * and load the keyboard layout from xml file <var>layoutid</var> (see {@link Keyboard} for description).
     * Note that the <var>host</var> activity must have a <var>KeyboardView</var> in its layout (typically aligned with the bottom of the activity).
     * Note that the keyboard layout xml file may include key codes for navigation; see the constants in this class for their values.
     * Note that to enable EditText's to use this custom keyboard, call the {@link #registerEditText(UnitTypesEditText)}.
     *
     * @param host The hosting activity.
     * @param viewid The id of the KeyboardView.
     * @param layoutid The id of the xml file containing the keyboard layout.
     */
    public UnitTypesKeyboard(Activity host, int viewid, int layoutid) {
        mHostActivity= host;
        mKeyboardView= (KeyboardView)mHostActivity.findViewById(viewid);
        mKeyboardView.setKeyboard(new Keyboard(mHostActivity, layoutid));
        mKeyboardView.setPreviewEnabled(false); // NOTE Do not show the preview balloons
        mKeyboardView.setOnKeyboardActionListener(mOnKeyboardActionListener);
        // Hide the standard keyboard initially
        mHostActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
       setupKeyStatus();
    }

    /** Returns whether the CustomKeyboard is visible. */
    public boolean isCustomKeyboardVisible() {
        return mKeyboardView.getVisibility() == View.VISIBLE;
    }

    /** Make the CustomKeyboard visible, and hide the system keyboard for view v. */
    public void showCustomKeyboard( View v ) {
        mKeyboardView.setVisibility(View.VISIBLE);
        mKeyboardView.setEnabled(true);
        if( v!=null ) ((InputMethodManager)mHostActivity.getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /** Make the CustomKeyboard invisible. */
    public void hideCustomKeyboard() {
        mKeyboardView.setVisibility(View.GONE);
        mKeyboardView.setEnabled(false);
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


    /**
     * Register <var>EditText<var> with resource id <var>resid</var> (on the hosting activity) for using this custom keyboard.
     *
     * @param edittext The resource view of the EditText that registers to the custom keyboard.
     */
    public void registerEditText(final UnitTypesEditText edittext) {

        if(edittext == null) {
            return;
        }

        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
            // NOTE By setting the on focus listener, we can show the custom keyboard when the edit box gets focus, but also hide it when the edit box loses focus
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showCustomKeyboard(v);
                else {
                    if(edittext.isFraction()) {
                        String val = edittext.getText().toString();
                        if (val.contains("/")) {
                            String[] arr = val.split("/");
                            if(arr[1].matches("(^0+$)")) {
                                edittext.setText("0");
                            }
                        }
                    }
                    hideCustomKeyboard();
                }
            }
        });
        edittext.setOnClickListener(new OnClickListener() {
            // NOTE By setting the on click listener, we can show the custom keyboard again, by tapping on an edit box that already had focus (but that had the keyboard hidden).
            @Override
            public void onClick(View v) {
                showCustomKeyboard(v);
            }
        });
        // Disable standard keyboard hard way
        // NOTE There is also an easy way: 'edittext.setInputType(InputType.TYPE_NULL)' (but you will not have a cursor, and no 'edittext.setCursorVisible(true)' doesn't work )
        edittext.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                UnitTypesEditText edittext = (UnitTypesEditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                return true; // Consume touch event
            }
        });
        // Disable spell check (hex strings look like words to Android)
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }

    private void setModeIndexes(){

    }

    public void updateKeyStatus(boolean isDecimal, boolean isPercent, boolean isFraction){

        mKeyboardKeys.get(index_decimal).on = isDecimal;
        mKeyboardKeys.get(index_percent).on = isPercent;
        mKeyboardKeys.get(index_fraction).on = isFraction;

        mKeyboardView.invalidateAllKeys();
    }

    private String trimZeros(String s){
        s = s.replaceFirst("^0+(?!$)", "");
        if(s.charAt(0) == '.') s = "0" + s;
        return s;
    }

}