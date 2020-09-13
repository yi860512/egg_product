package yi.yi_860512.egg_project.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import yi.yi_860512.egg_project.R;
import yi.yi_860512.egg_project.library.Variable;

public class MainActivity extends BaseActivity {

    private TextView showDate;

    @Override
    protected void initParam() {
        showDate = findViewById(R.id.showDate);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initParam();
        Calendar mCal = Calendar.getInstance();
        CharSequence s = DateFormat.format("yyyy-MM-dd", mCal.getTime());
        Variable.Date_today = s.toString();
        showDate.setText(Variable.Date_today);
    }

    public void Enter(View view) {
        Intent intent = new Intent(this,Main2Activity.class);
        startActivity(intent);
    }
}
