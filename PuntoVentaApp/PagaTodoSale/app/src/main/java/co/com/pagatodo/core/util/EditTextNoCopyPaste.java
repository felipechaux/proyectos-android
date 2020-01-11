package co.com.pagatodo.core.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

@SuppressLint("AppCompatCustomView")
public class EditTextNoCopyPaste extends EditText {

    public EditTextNoCopyPaste(Context context) {
        super(context);
        init();
    }

    public EditTextNoCopyPaste(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextNoCopyPaste(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setCustomSelectionActionModeCallback(new BlockedActionModeCallback());
        this.setLongClickable(false);
    }

    @Override
    public boolean isSuggestionsEnabled() {
        return false;
    }

    private class BlockedActionModeCallback implements ActionMode.Callback {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        public void onDestroyActionMode(ActionMode mode) {
            // not implemented
        }
    }
}