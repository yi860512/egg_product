package yi.yi_860512.egg_project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import yi.yi_860512.egg_project.R;

public class Main2Activity extends BaseActivity {

    @Override
    protected void initParam() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initParam();
    }

    public void SET(View view) {
        Intent intent = new Intent(this, SetActivity.class);
        startActivity(intent);
    }

    public void SetProduct(View view) {
        InputPassword(this, ProductActivity.class);
    }

    public void SetCust(View view) {
        InputPassword(this, CustTeamActivity.class);
    }

    public void send(View view) {
        InputPassword(this, SendActivity.class);
    }
}