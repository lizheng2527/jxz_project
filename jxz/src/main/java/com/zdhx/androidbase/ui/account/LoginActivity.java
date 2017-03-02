package com.zdhx.androidbase.ui.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.SystemConst;
import com.zdhx.androidbase.entity.ParameterValue;
import com.zdhx.androidbase.entity.User;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.ProgressThreadWrap;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.RunnableWrap;
import com.zdhx.androidbase.util.StringUtil;
import com.zdhx.androidbase.util.ToastUtil;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.view.dialog.ECAlertDialog;
import com.zdhx.androidbase.view.dialog.ECProgressDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.zdhx.androidbase.R.id.btn_left;

/**
 * @Title: LoginActivity.java
 * @Package com.xinyulong.seagood.ui.account
 * @Description: TODO(登陆页面)
 * @date 2016-5-3 下午4:12:54
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

    private Activity context;

    private EditText etxt_pwd;
    private AutoCompleteTextView etxt_username;

    private TextView txt_regist, txt_re_pwd;

    private ECProgressDialog dialog;

    private ImageView ip;

    private ECAlertDialog buildAlert;

    private List<String> userNameList;

    @Override
    protected int getLayoutId() {

        return R.layout.login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        getTopBarView().setVisibility(View.GONE);
        //检查更新
        checkVerson();
        updata();
        dialog = new ECProgressDialog(context);
        getTopBarView().setTopBarToStatus(1, R.drawable.topbar_back_bt, -1, "登录", this);
        ImageButton btnqq = (ImageButton) findViewById(R.id.ibtn_login_qq);
        ImageButton btnWeiChat = (ImageButton) findViewById(R.id.ibtn_login_weichat);
        ImageButton btnSina = (ImageButton) findViewById(R.id.ibtn_login_sina);
        Button btn_login = (Button) findViewById(R.id.btn_login);
        btnqq.setOnClickListener(this);
        btnWeiChat.setOnClickListener(this);
        btnSina.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        etxt_pwd = (EditText) findViewById(R.id.etxt_pwd);
        txt_regist = (TextView) findViewById(R.id.txt_regist);
        txt_regist.setOnClickListener(this);
        txt_re_pwd = (TextView) findViewById(R.id.txt_re_pwd);
        txt_re_pwd.setOnClickListener(this);
        etxt_username = (AutoCompleteTextView) findViewById(R.id.etxt_username);

        User user = ECApplication.getInstance().getCurrentUser();
        if(user != null){
            etxt_username.setText(ECApplication.getInstance().getCurrentUser().getLoginName());
            etxt_pwd.setText(ECApplication.getInstance().getCurrentUser().getPassWord());
        }
        userNameList = new ArrayList<>();
        if (ECApplication.getInstance().getAllUser().size()>0){
            for (int i = 0; i < ECApplication.getInstance().getAllUser().size(); i++) {
                if (!userNameList.contains(ECApplication.getInstance().getAllUser().get(i).getLoginName())){
                    userNameList.add(ECApplication.getInstance().getAllUser().get(i).getLoginName());
                }
            }
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.item_username,userNameList);
        etxt_username.setAdapter(adapter);

        etxt_username.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String key = etxt_username.getText().toString();
//                etxt_pwd.setText(ECApplication.getInstance().getUserValue(key).getPassWord());
                etxt_pwd.setText("");
            }
        });

        //配置IP地址“test”为测试地址，不输入为正式地址
        ip = (ImageView) findViewById(R.id.ip);
        ip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                buildAlert = ECAlertDialog.buildAlert(context,R.string.title, null, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String name = ((EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText))).getText().toString();
                        SystemConst.setTextIp(name);
                    }
                });
                buildAlert.setTitle("修改地址");
                buildAlert.setCanceledOnTouchOutside(false);
                buildAlert.setContentView(R.layout.config_dcaddress_dialog);
                String server = "";
                final EditText editText = (EditText) (buildAlert.getWindow().findViewById(R.id.dcAddressText));
                TextView delectTV = (TextView) buildAlert.getWindow().findViewById(R.id.delectTV);
                delectTV.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        editText.setText("");
                    }
                });
                if (!server.equals("")) {
                    editText.setText(server);
                }
                if(!buildAlert.isShowing()){
                    buildAlert.show();
                    editText.setSelection(editText.getText().length());
                    editText.setSelectAllOnFocus(true);
                }
                return true;
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case btn_left:
                finish();
                break;
            case R.id.txt_regist:
                break;
            case R.id.txt_re_pwd:
                break;
            case R.id.btn_login:
                login();
                break;
            default:
                break;
        }
    }
    private HashMap<String, ParameterValue> loginMap;
    private HashMap<String, ParameterValue> userInfoMap = new HashMap<>();
    private String loginJson;
    private User userInfos;
    private Handler handler = new Handler();

    private void login() {

        ProgressUtil.show(context,"正在登陆..");

        if (StringUtil.isBlank(getStringByUI(etxt_username))) {
            doToast("用户名不能为空  ");
            return;
        }
        if (StringUtil.isBlank(getStringByUI(etxt_pwd))) {
            doToast("密码不能为空");
            return;
        }
        loginMap = new HashMap<String, ParameterValue>();
        loginMap.put("loginName", new ParameterValue(getStringByUI(etxt_username)));
        loginMap.put("password", new ParameterValue(getStringByUI(etxt_pwd)));
        userInfoMap.put("sys_auto_authenticate", new ParameterValue("true"));
        userInfoMap.put("sys_username", new ParameterValue(getStringByUI(etxt_username)));
        userInfoMap.put("sys_password", new ParameterValue(getStringByUI(etxt_pwd)));
        new ProgressThreadWrap(context, new RunnableWrap() {
            @Override
            public void run() {
                try {
                    loginJson = ZddcUtil.checkLogin(loginMap);
                    if (loginJson.contains("true")) {
                        String userInfoJson = ZddcUtil.getUserInfo(userInfoMap);
                        if (userInfoJson.length() != 0) {
                            userInfos = new Gson().fromJson(userInfoJson, User.class);
                            userInfos.setLoginName(loginMap.get("loginName").getValues().get(0));
                            userInfos.setPassWord(loginMap.get("password").getValues().get(0));
                            LogUtil.e(userInfos.toString());
                            ECApplication.getInstance().saveUser(userInfos);
                            ECApplication.getInstance().saveUsers(userInfos);
                            ECApplication.getInstance().setCurrentUserMap(userInfoMap);
                            ECApplication.getInstance().setLoginUrlMap("sys_auto_authenticate", new ParameterValue("true"));
                            ECApplication.getInstance().setLoginUrlMap("sys_username", new ParameterValue(getStringByUI(etxt_username)));
                            ECApplication.getInstance().setLoginUrlMap("sys_password", new ParameterValue(getStringByUI(etxt_pwd)));
                        }
                    }
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            ProgressUtil.hide();
                            if (loginJson.contains("true")) {
                                startActivity(new Intent(context, MainActivity.class));
                                new ProgressThreadWrap(context, new RunnableWrap() {
                                    @Override
                                    public void run() {
                                        String userAuth = null;
                                        try {
                                            userAuth = ZddcUtil.getUserAuth(ECApplication.getInstance().getLoginUrlMap());
                                            if (userAuth.length() != 0){
                                                if (userAuth.contains("yes")){
                                                    ECApplication.getInstance().setUserAuth("yes");
                                                }
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                finish();
                            } else {
                                ToastUtil.showMessage("账号或密码错误");
                            }
                        }
                    }, 5);
                } catch (IOException e) {
                    e.printStackTrace();
                    ProgressUtil.hide();
                    ToastUtil.showMessage("连接服务器失败");
                }
            }
        }).start();
    }

    public void checkVerson() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int currentVersion = info.versionCode;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int lastVersion = prefs.getInt("VERSION_KEY", 0);
        LogUtil.e("lastVersion:"+lastVersion+"---currentVersion:"+currentVersion);
        if (currentVersion > lastVersion) {
            //如果当前版本大于上次版本，该版本属于第一次启动
            //将当前版本写入preference中，则下次启动的时候，据此判断，不再为首次启动
            prefs.edit().putInt("VERSION_KEY",currentVersion).commit();
        }
    }

    public void updata() {
//		ToastUtil.showMessage("正在检查版本更新，请稍等");
        PgyUpdateManager.register(LoginActivity.this,
                new UpdateManagerListener() {

                    @Override
                    public void onUpdateAvailable(final String result) {

                        // 将新版本信息封装到AppBean中
                        final AppBean appBean = getAppBeanFromString(result);

                        final AlertDialog alertDialog = new AlertDialog.Builder(
                                LoginActivity.this).create();
                        alertDialog.show();
                        Window window = alertDialog.getWindow();
                        window.setContentView(R.layout.umeng_update_dialog);
                        TextView umeng_update_content = (TextView) window
                                .findViewById(R.id.umeng_update_content);
                        umeng_update_content.setText("v" + appBean.getVersionName() + "版本更新日志：\n" + (StringUtil.isBlank(appBean.getReleaseNote()) ? "无"
                                : appBean.getReleaseNote()));
                        Button umeng_update_id_ok = (Button) window
                                .findViewById(R.id.umeng_update_id_ok);
                        umeng_update_id_ok
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        startDownloadTask(LoginActivity.this,
                                                appBean.getDownloadURL());
                                        alertDialog.dismiss();
                                    }
                                });
                        Button umeng_update_id_cancel = (Button) window
                                .findViewById(R.id.umeng_update_id_cancel);
                        umeng_update_id_cancel
                                .setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        alertDialog.dismiss();
                                    }
                                });
                    }

                    @Override
                    public void onNoUpdateAvailable() {
//						ToastUtil.showMessage("已是最新版本");
                    }
                });
    }

//    class UserNameAdapter extends BaseAdapter implements Filterable {
//
//        public UserNameAdapter() {
//        }
//
//        @Override
//        public int getCount() {
//            return userNameList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return userNameList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View view = View.inflate(context,R.layout.item_username,null);
////            TextView tv = (TextView) view.findViewById(R.id.usernameTV);
////            tv.setText(userNameList.get(position));
//            return view;
//        }
//
//        @Override
//        public Filter getFilter() {
//            return null;
//        }
//    }
}
