package com.valleskeyp.tubehub;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class EditActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_edit, menu);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode)
        {
        case 999:
            setResult(999);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
