package yi.yi_860512.egg_project.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import yi.yi_860512.egg_project.R;
import yi.yi_860512.egg_project.library.Config;

public abstract class BaseActivity extends AppCompatActivity {
    private ConnectivityManager CM;
    private MyReceiver myReceiver;
    private ProgressDialog progressDialog;
    private Config config;
    public FirebaseFirestore firebaseFirestore;

    protected abstract void initParam();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        config = Config.getInstance(this);
        CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myReceiver, filter);
    }

    //網路是否連線
    private boolean isConnected() {
        NetworkInfo info = CM.getActiveNetworkInfo();
        boolean isConnected = info != null && info.isConnectedOrConnecting();
        return isConnected;
    }

    //網路斷線檢測
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("show", isConnected() + "");
            if (!isConnected()) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle("網路斷線");
                progressDialog.setMessage("重新連線中...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            } else {
                if (progressDialog != null) {
                    //將Product資料上傳到FireBase
                    String tmpProName = config.getProNameLocalTmp();
                    if (!tmpProName.equals("")) {
                        returnPushPro(tmpProName, config.getProPriceLocalTmp());
                    }
                    String tmpCustName = config.getCustNameLocalTmp();
                    if (!tmpCustName.equals("")) {
                        returnPushCust(tmpCustName,
                                config.getCustGmailLocalTmp(), config.getCustPhoneLocalTmp(),
                                config.getCustAddressLocalTmp(), config.getTeamIdLocalTmp());
                    }
                    String tmpTeamName = config.getTeamCustLocalTmp();
                    if (!tmpTeamName.equals("")) {
                        returnPushTeam(tmpTeamName);
                    }
                    progressDialog.dismiss();
                    progressDialog = null;
                }
            }
        }
    }

    //重新上傳產品資訊
    private void returnPushPro(String tmpName, String tmpPrice) {
        ProductSaveToCloud(tmpName, tmpPrice);
    }

    //重新上傳產品資訊
    private void returnPushCust(String tmpName, String tmpGmail,
                                String tmpPhone, String address, String teamid) {
        CustSaveToCloud(tmpName, tmpGmail, tmpPhone, address, teamid);
    }

    //重新上傳產品資訊
    private void returnPushTeam(String tmpName) {
        TeamSaveToCloud(tmpName);
    }

    //上傳產品到firebase
    public void ProductSaveToCloud(final String EdProName, final String EdProPrice) {
        ProductSaveToLocal(EdProName, EdProPrice);
        Map<String, Object> pro = new HashMap<>();
        pro.put("name", EdProName);
        pro.put("price", EdProPrice);
        firebaseFirestore.collection("product")
                .add(pro)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        ProductDeleteLocal();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        returnPushPro(EdProName, EdProPrice);
                    }
                });
    }

    //上傳客戶到firebase
    public void CustSaveToCloud(final String CustName, final String CustGmail, final String CustPhone,
                                final String CustAddress, final String teamId) {
        CustSaveToLocal(CustName, CustGmail, CustPhone, CustAddress, teamId);
        Map<String, Object> cust = new HashMap<>();
        cust.put("name", CustName);
        cust.put("gmail", CustGmail);
        cust.put("phone", CustPhone);
        cust.put("address", CustAddress);
        cust.put("teamId", teamId);
        firebaseFirestore.collection("cust")
                .add(cust)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        CustDeleteLocal();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        returnPushCust(CustName, CustGmail, CustPhone, CustAddress, teamId);
                    }
                });
    }

    //上傳CustTeam到firebase
    public void TeamSaveToCloud(final String TeamCustName) {
        TeamSaveToLocal(TeamCustName);
        Map<String, Object> team = new HashMap<>();
        team.put("name", TeamCustName);
        firebaseFirestore.collection("team")
                .add(team)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        TeamDeleteLocal();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        returnPushTeam(TeamCustName);
                    }
                });
    }

    //跳出輸入密碼視窗
    public void InputPassword(final Context context, final Class goToClass) {
        LayoutInflater mInflater = LayoutInflater.from(context);
        View v = mInflater.inflate(R.layout.activity_input_password, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(false);
        dialog.setTitle("密碼");
        dialog.setMessage("請輸入密碼");
        dialog.setView(v);
        dialog.setCancelable(false);
        final EditText editText = v.findViewById(R.id.password);
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String EditPassword;
                if (editText.getText().toString().equals("")) {
                    dialog.cancel();
                    Toast.makeText(context, "未輸入密碼", Toast.LENGTH_SHORT).show();
                } else {
                    EditPassword = editText.getText().toString();
                    if (EditPassword.equals(config.getPassword())) {
                        //intent
                        Toast.makeText(context, "密碼正確", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context, goToClass);
                        startActivity(intent);
                    } else {
                        dialog.cancel();
                        Toast.makeText(context, "密碼錯誤", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dialog.show();
    }

    //新增config產品資訊
    public void ProductSaveToLocal(String proName, String proPrice) {
        config.setProLocalTmp(proName, proPrice);
    }

    //刪除config的產品暫存
    public void ProductDeleteLocal() {
        config.setProLocalTmp("", "");
    }

    //新增config產品資訊
    public void CustSaveToLocal(String custName, String gmail, String phone, String address, String teamid) {
        config.setCustLocalTmp(custName, gmail, phone, address, teamid);
    }

    //刪除config的產品暫存
    public void CustDeleteLocal() {
        config.setCustLocalTmp("", "", "", "", "");
    }

    //新增config CustTeam資訊
    public void TeamSaveToLocal(String teamName) {
        config.setTeamCustLocalTmp(teamName);
    }

    //刪除config CustTeam資訊
    public void TeamDeleteLocal() {
        config.setTeamCustLocalTmp("");
    }
}