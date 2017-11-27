package com.ro.sapientia.ms.jimysty.sapiadvertiser.Activity;

import android.os.Bundle;
import android.widget.EditText;

import com.ro.sapientia.ms.jimysty.sapiadvertiser.BasicActivity;
import com.ro.sapientia.ms.jimysty.sapiadvertiser.R;

/**
 * Created by barth on 11/13/2017.
 */

public class Profile extends BasicActivity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.profile);
    }

    private void Edit(){
        EditText etFirst_Name = findViewById(R.id.pr_first);
        EditText etLast_Name = findViewById(R.id.pr_last);
        EditText etEmail = findViewById(R.id.pr_email);
        EditText etPhone_Number = findViewById(R.id.pr_phone);
        EditText etPassword = findViewById(R.id.pr_password);
        EditText etCondirm_Password = findViewById(R.id.pr_confirm_password);

       // etFirst_Name.setBackground();
    }
}
