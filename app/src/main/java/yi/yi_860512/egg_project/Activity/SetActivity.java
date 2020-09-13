package yi.yi_860512.egg_project.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import yi.yi_860512.egg_project.R;
import yi.yi_860512.egg_project.library.Config;

public class SetActivity extends BaseActivity {
    private Config config;

    @Override
    protected void initParam() {
        config = Config.getInstance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        initParam();
    }

    public void changePassword(View view) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View alert_view = inflater.inflate(R.layout.item_set_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新增").setView(alert_view);
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setDimAmount(0);
        dialog.show();
        final EditText ET_originalPassword = alert_view.findViewById(R.id.originalPassword);
        final EditText ET_newPassword = alert_view.findViewById(R.id.newPassword);
        Button btn = alert_view.findViewById(R.id.changePassword);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (config.getPassword().equals(ET_originalPassword.getText().toString())) {
                    config.setPassword(ET_newPassword.getText().toString());
                    Toast.makeText(SetActivity.this, "變更完成", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                } else {
                    Toast.makeText(SetActivity.this, "密碼錯誤!!", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            }
        });
    }

    public void Back(View view) {
        Intent intent = new Intent(this,Main2Activity.class);
        startActivity(intent);
        finish();
    }
}