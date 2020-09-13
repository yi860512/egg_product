package yi.yi_860512.egg_project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

import yi.yi_860512.egg_project.Model.Cust;
import yi.yi_860512.egg_project.Model.Team;
import yi.yi_860512.egg_project.R;

public class CustTeamActivity extends BaseActivity {
    private LinkedList<Team> list;
    private ListView mlistView;
    private MyAdapter mAdapter;
    private LinkedList<String> teamIdList, teamNameList;

    @Override
    protected void initParam() {
        mlistView = findViewById(R.id.list);
        list = new LinkedList<>();
        teamIdList = new LinkedList<>();
        teamNameList = new LinkedList<>();
        mAdapter = new MyAdapter();
        mlistView.setAdapter(mAdapter);
        teamInit();
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
            View v = inflater.inflate(R.layout.item_list_cust_team, parent, false);//版面,群組為parent參數,最後一個都用false
            TextView tvName = v.findViewById(R.id.custTeamName);
            tvName.setText(list.get(position).getName());
            return v;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_team);
        initParam();
        //mlistView OnClick IntentToCustActivity
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CustTeamActivity.this, CustActivity.class);
                intent.putExtra("team", teamIdList.get(position));
                startActivity(intent);
            }
        });
        //刪除群組
        mlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(CustTeamActivity.this)
                        .setTitle("刪除?")
                        .setMessage("確定要刪除" + list.get(position).getName() + "群組?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);
                                //刪除,表單名稱
                                firebaseFirestore.collection("team")
                                        .document(teamIdList.get(position))
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
                                //未完成，刪除該群組底下的cust/暫定保留不刪除
                                teamInit();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                return true;
            }
        });
    }

    private void teamInit() {
        list.clear();
        teamIdList.clear();
        teamNameList.clear();
        firebaseFirestore.collection("team")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Team team = new Team(document.getData().get("name").toString());
                                list.add(team);
                                teamNameList.add(document.get("name").toString());
                                teamIdList.add(document.getId());
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            teamInit();
                            Log.v("show", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void CreateTeam(View view) {
        LayoutInflater mInflater = LayoutInflater.from(this);
        View v = mInflater.inflate(R.layout.item_create_cust_team, null);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setView(v);
        dialog.setCancelable(false);
        final EditText editTeamName = v.findViewById(R.id.teamName);
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String TeamName = editTeamName.getText().toString();
                if (TeamName.equals("")) {
                    Toast.makeText(CustTeamActivity.this, "名稱必填", Toast.LENGTH_SHORT).show();
                } else {
                    teamInit();
                    Toast.makeText(CustTeamActivity.this, "新增成功", Toast.LENGTH_SHORT).show();
                    TeamSaveToCloud(TeamName);
                }
            }
        });
        dialog.show();
    }
}
