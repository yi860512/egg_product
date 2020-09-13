package yi.yi_860512.egg_project.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import yi.yi_860512.egg_project.Model.Product;
import yi.yi_860512.egg_project.R;

public class ProductActivity extends BaseActivity {

    private ListView mListView;
    private MyAdapter mAdapter;
    private LinkedList<Product> list;
    private LinkedList<String> proIdList;
    private LinkedList<String> proNameList;

    @Override
    protected void initParam() {
        proNameList = new LinkedList<>();
        proIdList = new LinkedList<>();
        list = new LinkedList<>();
        mListView = findViewById(R.id.list);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        productInit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        initParam();
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(ProductActivity.this)
                        .setTitle("刪除?")
                        .setMessage("確定要刪除" + list.get(position).getName() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);
                                //刪除
                                firebaseFirestore.collection("product").document(proIdList.get(position))
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
                                productInit();
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

    public void CreateProduct(View view) {
        LayoutInflater mInflater = LayoutInflater.from(this);
        View v = mInflater.inflate(R.layout.item_create_product, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setView(v);
        dialog.setCancelable(false);
        final EditText editProN = v.findViewById(R.id.productName);
        final EditText editProP = v.findViewById(R.id.productPrice);
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String EditProName = editProN.getText().toString();
                if (EditProName.equals("")) {
                    dialog.cancel();
                    Toast.makeText(ProductActivity.this, "未輸入產品", Toast.LENGTH_SHORT).show();
                } else {
                    productInit();
                    Toast.makeText(ProductActivity.this, "新增成功", Toast.LENGTH_SHORT).show();
                    ProductSaveToCloud(EditProName, editProP.getText().toString());
                }
            }
        });
        dialog.show();
    }


    private void productInit() {
        proNameList.clear();
        proIdList.clear();
        list.clear();
        //去資料庫撈資料載入
        firebaseFirestore.collection("product")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = new Product(document.getData().get("name").toString(),
                                        document.getData().get("price").toString());
                                list.add(product);
                                proNameList.add(document.get("name").toString());
                                proIdList.add(document.getId());
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            productInit();
                            Log.v("show", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void ChangePrice(View view) {
        final int[] ChosePosition = {0};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        final View v = getLayoutInflater().inflate(R.layout.item_change_product, null);
        dialog.setCancelable(false);
        dialog.setTitle("價格變更");
        dialog.setMessage("請選擇產品");
        final Spinner spinner = v.findViewById(R.id.spinner);
        final EditText editText = v.findViewById(R.id.ChangePrice);
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.item_spinners, proNameList);
        spinner.setAdapter(listAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ChosePosition[0] = position;
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
                String ChangePrice = editText.getText().toString();
                if (ChangePrice.equals("")) {
                    dialog.cancel();
                    Toast.makeText(ProductActivity.this, "未輸入價格", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> pro = new HashMap<>();
                    pro.put("name", spinner.getSelectedItem().toString());
                    pro.put("price", ChangePrice);
                    //add不會重複
                    firebaseFirestore.collection("product").document(proIdList.get(ChosePosition[0]))
                            .set(pro)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    productInit();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                }
            }
        });
        dialog.setView(v);
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

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
            View v = inflater.inflate(R.layout.item_list_product, parent, false);//版面,群組為parent參數,最後一個都用false
            TextView tvName = v.findViewById(R.id.textName);
            TextView tvPrice = v.findViewById(R.id.textPrice);
            tvName.setText(list.get(position).getName());
            tvPrice.setText(list.get(position).getPrice());
            return v;
        }
    }
}