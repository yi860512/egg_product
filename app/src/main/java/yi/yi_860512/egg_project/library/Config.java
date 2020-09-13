package yi.yi_860512.egg_project.library;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {
    private static Context mContext;
    private static Config instance = null;
    private SharedPreferences sp;
    private String PASSWORD = "password";
    private String ProNameLocalTmp = "proNameLocalTmp";
    private String ProPriceLocalTmp = "proPriceLocalTmp";
    private String CustNameLocalTmp = "custNameLocalTmp";
    private String CustGmailLocalTmp = "custGmailLocalTmp";
    private String CustPhoneLocalTmp = "custPhoneLocalTmp";
    private String CustAddressLocalTmp = "custAddressLocalTmp";
    private String TeamId = "teamid";//分類客戶是哪個team
    private String TeamNameLocalTmp = "teamNameLocalTmp";

    public synchronized static Config getInstance(Context context) {
        if (instance == null) {
            instance = new Config(context);
        }
        return instance;
    }

    private Config(Context context) {
        this.mContext = context;
        sp = mContext.getSharedPreferences("Set", Context.MODE_PRIVATE);
    }

    public void setProLocalTmp(String proName, String proPrice) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(ProNameLocalTmp, proName);
        edit.putString(ProPriceLocalTmp, proPrice);
        edit.commit();
    }

    public String getProNameLocalTmp() {
        return sp.getString(ProNameLocalTmp, "");
    }

    public String getProPriceLocalTmp() {
        return sp.getString(ProPriceLocalTmp, "");
    }

    public void setPassword(String password) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(PASSWORD, password);
        edit.commit();
    }

    public String getPassword() {
        return sp.getString(PASSWORD, "123456789");
    }

    public void setCustLocalTmp(String custName, String gmail, String phone,String address,String teamId) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(CustNameLocalTmp, custName);
        edit.putString(CustGmailLocalTmp, gmail);
        edit.putString(CustPhoneLocalTmp, phone);
        edit.putString(CustAddressLocalTmp, address);
        edit.putString(TeamId, teamId);
        edit.commit();
    }
    public void setTeamCustLocalTmp(String custName) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(CustNameLocalTmp, custName);
        edit.commit();
    }
    public String getTeamCustLocalTmp() {
        return sp.getString(TeamNameLocalTmp, "");
    }
    public String getCustNameLocalTmp() {
        return sp.getString(CustNameLocalTmp, "");
    }

    public String getCustAddressLocalTmp() {
        return sp.getString(CustAddressLocalTmp, "");
    }
    public String getCustGmailLocalTmp() {
        return sp.getString(CustGmailLocalTmp, "");
    }
    public String getCustPhoneLocalTmp() {
        return sp.getString(CustPhoneLocalTmp, "");
    }
    public String getTeamIdLocalTmp() {
        return sp.getString(TeamId, "");
    }
}
