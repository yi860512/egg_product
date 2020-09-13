package yi.yi_860512.egg_project.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import yi.yi_860512.egg_project.Model.Cust;
import yi.yi_860512.egg_project.Model.Product;
import yi.yi_860512.egg_project.R;

public class CustActivity extends BaseActivity {
    private ListView mListView;
    private MyAdapter mAdapter;
    private LinkedList<Cust> list;
    private LinkedList<String> CustIdList;
    private LinkedList<String> CustNameList;
    private LinkedList<String> CustGmailList;
    private LinkedList<String> CustPhoneList;
    private LinkedList<String> CustAddressList;
    private String teamId = "";

    private class MyAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public MyAdapter() {
            //下面兩種2則1都可
            //inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {//資料有幾筆
            return list.size();
        }

        @Override
        public Object getItem(int position) {//取得物件
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {//唯一識別
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = inflater.inflate(R.layout.item_list_cust, parent, false);//版面,群組為parent參數,最後一個都用false
            TextView tvName = v.findViewById(R.id.custName);
            TextView tvPhone = v.findViewById(R.id.custPhone);
            TextView tvGmail = v.findViewById(R.id.custGmail);
            TextView tvAddress = v.findViewById(R.id.custAddress);
            tvName.setText(list.get(position).getName());
            tvPhone.setText(list.get(position).getPhone());
            tvGmail.setText(list.get(position).getGmail());
            tvAddress.setText(list.get(position).getAddress());
            return v;
        }
    }


    @Override
    protected void initParam() {
        Intent intent = getIntent();
        teamId = intent.getStringExtra("team");
        CustNameList = new LinkedList<>();
        CustIdList = new LinkedList<>();
        CustGmailList = new LinkedList<>();
        CustPhoneList = new LinkedList<>();
        CustAddressList = new LinkedList<>();
        list = new LinkedList<>();
        mListView = findViewById(R.id.list);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        custInit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust);
        initParam();
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(CustActivity.this)
                        .setTitle("刪除?")
                        .setMessage("確定要刪除" + list.get(position).getName() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);
                                //刪除
                                firebaseFirestore.collection("cust")
                                        .document(CustIdList.get(position))
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                                custInit();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return false;
            }
        });
    }

    public void CreateCust(View view) {
        LayoutInflater mInflater = LayoutInflater.from(this);
        View v = mInflater.inflate(R.layout.item_create_cust, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setView(v);
        final EditText editCustName = v.findViewById(R.id.custName);
        final EditText editCustGmail = v.findViewById(R.id.custGmail);
        final EditText editCustPhone = v.findViewById(R.id.custPhone);
        final EditText editCustAddress = v.findViewById(R.id.custAddress);

        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String CustName = editCustName.getText().toString();
                String CustGmail = editCustGmail.getText().toString();
                String CustPhone = editCustPhone.getText().toString();
                String CustAddress = editCustAddress.getText().toString();
                if (CustName.equals("")) {
                    Toast.makeText(CustActivity.this, "名稱必填", Toast.LENGTH_SHORT).show();
                } else {
                    custInit();
                    Toast.makeText(CustActivity.this, "新增成功", Toast.LENGTH_SHORT).show();
                    CustSaveToCloud(CustName, CustGmail, CustPhone, CustAddress, teamId);
                }
            }
        });
        dialog.show();
    }

    private void custInit() {
        list.clear();
        CustNameList.clear();
        CustIdList.clear();
        CustGmailList.clear();
        CustPhoneList.clear();
        CustAddressList.clear();
        firebaseFirestore.collection("cust")
                .whereEqualTo("teamId", teamId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String n = document.get("name").toString();
                                String g = document.getData().get("gmail").toString();
                                String ph = document.getData().get("phone").toString();
                                String a = document.getData().get("address").toString();
                                Cust cust = new Cust(n, g, ph, a);
                                list.add(cust);
                                CustNameList.add(n);
                                CustIdList.add(document.getId());
                                CustGmailList.add(g);
                                CustPhoneList.add(ph);
                                CustAddressList.add(a);
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            custInit();
                            Log.v("show", "Error getting documents: ", task.getException());
                        }
                    }

                });
    }

    public void ChangeInfo(View view) {
        final int[] ChosePosition = {0};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final View v = getLayoutInflater().inflate(R.layout.item_change_cust, null);
        dialog.setCancelable(false);
        dialog.setTitle("資訊變更");
        dialog.setMessage("請選擇客戶");
        final Spinner spinner = v.findViewById(R.id.spinner);
        final EditText CGm = v.findViewById(R.id.ChangeGmail);
        final EditText CPh = v.findViewById(R.id.ChangePhone);
        final EditText CAd = v.findViewById(R.id.ChangeAddress);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.item_spinners, CustNameList);
        spinner.setAdapter(listAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ChosePosition[0] = position;
                CGm.setText(CustGmailList.get(position));
                CPh.setText(CustPhoneList.get(position));
                CAd.setText(CustAddressList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialog.setCancelable(false);
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ChangeGmail = CGm.getText().toString();
                String ChangePhone = CPh.getText().toString();
                String ChangeAddress = CAd.getText().toString();
                if (ChangeGmail.equals("") || ChangePhone.equals("") || ChangeAddress.equals("")) {
                    dialog.cancel();
                    Toast.makeText(CustActivity.this, "有空白未輸入", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> cust = new HashMap<>();
                    cust.put("name", spinner.getSelectedItem().toString());
                    cust.put("gmail", ChangeGmail);
                    cust.put("phone", ChangePhone);
                    cust.put("address", ChangeAddress);
                    cust.put("teamId", teamId);
                    firebaseFirestore.collection("cust").document(CustIdList.get(ChosePosition[0])).set(cust)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(CustActivity.this, "完成", Toast.LENGTH_SHORT).show();
                                    custInit();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CustActivity.this, "失敗", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        dialog.setView(v);
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
}