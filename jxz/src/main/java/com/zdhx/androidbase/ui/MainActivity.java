package com.zdhx.androidbase.ui;
/**
 *                             _ooOoo_
 *                            o8888888o
 *                            88" . "88
 *                            (| @_@ |)
 *                            O\  =  /O
 *                         ____/`---'\____
 *                       .'  \\|     |//  `.
 *                      /  \\|||  :  |||//  \
 *                     /  _||||| -:- |||||-  \
 *                     |   | \\\  -  /// |   |
 *                     | \_|  ''\---/''  |   |
 *                     \  .-\__  `-`  ___/-. /
 *                   ___`. .'  /--.--\  `. . __
 *                ."" '<  `.___\_<|>_/___.'  >'"".
 *               | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *               \  \ `-.   \_ __\ /__ _/   .-` /  /
 *          ======`-.____`-.___\_____/___.-`____.-'======
 *                             `=---='
 *          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *                     佛祖保佑        永无BUG
 *            佛曰:
 *                   写字楼里写字间，写字间里程序员；
 *                   程序人员写程序，又拿程序换酒钱。
 *                   酒醒只在网上坐，酒醉还来网下眠；
 *                   酒醉酒醒日复日，网上网下年复年。
 *                   但愿老死电脑间，不愿鞠躬老板前；
 *                   奔驰宝马贵者趣，公交自行程序员。
 *                   别人笑我忒疯癫，我笑自己命太贱；
 *                   不见满街漂亮妹，哪个归得程序员？
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;
import com.zdhx.androidbase.ECApplication;
import com.zdhx.androidbase.R;
import com.zdhx.androidbase.entity.WorkSpaceDatasBean;
import com.zdhx.androidbase.ui.account.HomeFragment;
import com.zdhx.androidbase.ui.account.LoginActivity;
import com.zdhx.androidbase.ui.account.MeFragment;
import com.zdhx.androidbase.ui.account.ScroFragment;
import com.zdhx.androidbase.ui.account.ScroGridAdapter;
import com.zdhx.androidbase.ui.account.WorkSpaceFragment;
import com.zdhx.androidbase.ui.account.WorkSpaceGridAdapter;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.ui.introducetreads.IntroduceTreadsActivity;
import com.zdhx.androidbase.ui.quantity.PrePassActivity;
import com.zdhx.androidbase.ui.scrosearch.SelectScroActivity;
import com.zdhx.androidbase.ui.treadssearch.SearchTreadsActivity;
import com.zdhx.androidbase.ui.treadssearch.SearchWorkActivity;
import com.zdhx.androidbase.ui.treadssearch.UpFileActivity;
import com.zdhx.androidbase.ui.treelistview.bean.TreeBean;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.StringUtil;
import com.zdhx.androidbase.util.permissionUtil.MPermission;
import com.zdhx.androidbase.view.dialog.ECListDialog;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends BaseActivity implements OnClickListener {
	//全局调用的集合
	public static HashMap<String,Object> map = new HashMap<String, Object>();
	private Activity context;
	//动态主页
	private HomeFragment homeFragment;
	//工作平台
	private WorkSpaceFragment workSpaceFragment;
	//我的
	private MeFragment meFragment;
	//积分
	private ScroFragment scroFragment;


	private Fragment[] fragments;

	private int index;
	// 当前fragment的index
	private int currentTabIndex;

	private Button[] mTabs;

	private RelativeLayout[] mTabContainers;

	public static LinearLayout typeMenu;
	private static String menuTitleForFirst = "班级";
	private static String menuTitleForSecond = "教师备课资源";
	//主页显示的菜单选项
	public static ArrayList<String> homeMenuDatas;
	public static ArrayList<String> homeMenuDatasForService = new ArrayList<>();
	//选择菜单的计数器（用来统计显示的条目）
	public static int menuPosition;
	//工作平台菜单计数器（用来统计显示的条目）
	public static int menuPositionForWork;
	//选择菜单显示的文字
	private TextView menuSelectedTV;

	public static int SELECTMENUINDEX = 0;
	//功能fragment标题
	private TextView fragTitle;
	//标题栏左边第一个图标
	private ImageView titleImgFirst;
	//标题栏左边第二个图标
	private ImageView titleImgSecond;
	private LinearLayout titleImgSecondLinear;
	//标题栏左边第三个图标
	private ImageView titleImgThird;
	private LinearLayout titleImgThirdLinear;
	//跳转到选择时间及班级的请求码
	private final  int SELECTSCROACTIVITYCODE = 0;
	//跳转到发布活动页面的请求码
	private final  int INTRODUCETREADSCODE = 1;
	//跳转到搜索动态请求码
	private final int SEARCHTREADSCODE = 2;
	//跳转到搜索工作平台资源请求码
	private final int SEARCHWORKCODE = 3;
	//跳转到搜索工作平台资源请求码
	private final int UPFILEACTIVITYCODE = 4;

	//用来承接互动交流和工作平台菜单显示的过度变量
	private int indexForMenu;

	private static TextView allSelect;

	private static LinearLayout selectBatchLinear;
	private TextView selectTure,selectCancel;



	/**
	 * 缓存三个TabView
	 */
	private final HashMap<Integer, Fragment> mTabViewCache = new HashMap<Integer, Fragment>();

	/**
	 * 取消批量审核，清空选择内容
	 * @param view
	 */
	public void onSelectCancelClick(View view){
		onSelectCancel();
	}

	public void onSelectCancel(){
		showSelectBatchLinear(false);
		allSelect.setText("全  选");
		WorkSpaceFragment.isBatchSelect = false;
		HashMap<WorkSpaceDatasBean.DataListBean, String> batchSelectMap = workSpaceFragment.getBatchSelectMap();
		for (WorkSpaceDatasBean.DataListBean in : batchSelectMap.keySet()) {
			in.setSelect(false);
		}
		workSpaceFragment.getBatchSelectMap().clear();
		workSpaceFragment.notifyForSelect();
	}

	public static void setAllSelectText(boolean b){
		if (b){
			allSelect.setText("取消全选");
		}else{
			allSelect.setText("全  选");
		}
	}

	/**
	 * 全选
	 * @param view
	 */
	public void onAllSelectClick(View view){
		if (!allSelect.getText().toString().contains("取消")){
			allSelect.setText("取消全选");
			workSpaceFragment.selectAll(true);
		}else{
			allSelect.setText("全  选");
			workSpaceFragment.selectAll(false);
		}
	}

	/**
	 * 进行审核
	 * @param view
	 */
	private final int PREPASSCODE = 111;
	public void onSelectTureClick(View view){
		//TODO 执行批量审核
		allSelect.setText("全  选");
		HashMap<WorkSpaceDatasBean.DataListBean, String> batchSelectMap = workSpaceFragment.getBatchSelectMap();
		int count = 0;
		if (batchSelectMap.size() == 0){
			doToast("请选择审批的数据");
			return;
		}
		StringBuffer sb = new StringBuffer();
		for (WorkSpaceDatasBean.DataListBean in : batchSelectMap.keySet()) {
			count = count +1;
			if (count == batchSelectMap.size()){
				sb.append(batchSelectMap.get(in));
			}else{
				sb.append(batchSelectMap.get(in)+",");

			}
		}
		startActivityForResult(new Intent(context, PrePassActivity.class),PREPASSCODE);
		MainActivity.map.put("ids",sb.toString());
		MainActivity.map.put("WorkSpaceFragment",workSpaceFragment);
	}

	/**
	 * 显示确定和取消批量选择
	 * @param isShow
	 */
	public static boolean selectBatchLinearIsShowing;
	public static void showSelectBatchLinear(boolean isShow){
		selectBatchLinearIsShowing = isShow;
		if (isShow){
			selectBatchLinear.setVisibility(View.VISIBLE);
		}else{
			selectBatchLinear.setVisibility(View.GONE);
		}
	}

	@Override
	protected int getLayoutId() {return R.layout.activity_main;}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		requestBasicPermission();
		checkVerson();
		getTopBarView().setVisibility(View.GONE);
		initLauncherUIView();
		updata();
	}

	/**
	 * 基本权限管理
	 */
	private void requestBasicPermission() {
		MPermission.with(MainActivity.this)
				.addRequestCode(100)
				.permissions( Manifest.permission.WRITE_EXTERNAL_STORAGE,
						Manifest.permission.READ_EXTERNAL_STORAGE,
						Manifest.permission.CAMERA,
						Manifest.permission.READ_PHONE_STATE,
						Manifest.permission.RECORD_AUDIO,
						Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.ACCESS_FINE_LOCATION
				)
				.request();
	}

	/**
	 * 初始化主界面UI视图
	 */
	private void initLauncherUIView() {
		if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
			menuTitleForSecond = "教师备课资源";
		}else{
			menuTitleForSecond = "教师引导";
		}
		selectBatchLinear = (LinearLayout) findViewById(R.id.fragment_selectbatch);
		selectTure = (TextView) findViewById(R.id.selectTure);
		selectCancel = (TextView) findViewById(R.id.selectCancel);
		allSelect = (TextView) findViewById(R.id.allselect);
		homeFragment =  new HomeFragment();
		workSpaceFragment = new WorkSpaceFragment();
		meFragment = new MeFragment();
		scroFragment = new ScroFragment();
		fragments = new Fragment[] { homeFragment, workSpaceFragment,scroFragment, meFragment };
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, homeFragment)
				.add(R.id.fragment_container, workSpaceFragment)
				.add(R.id.fragment_container, scroFragment).add(R.id.fragment_container,meFragment)
				.hide(workSpaceFragment).hide(scroFragment).hide(meFragment)
				.show(homeFragment).commitAllowingStateLoss();
		mTabs = new Button[4];
		mTabs[0] = (Button) findViewById(R.id.btn_conversation);
		mTabs[1] = (Button) findViewById(R.id.btn_address_list);
		if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
			mTabs[1].setText("工作平台");
		}else{
			mTabs[1].setText("自主学习");
		}
		mTabs[2] = (Button) findViewById(R.id.btn_circle);
		mTabs[3] = (Button) findViewById(R.id.btn_app);
		mTabContainers = new RelativeLayout[4];
		mTabContainers[0] = (RelativeLayout) findViewById(R.id.btn_container_conversation);
		mTabContainers[1] = (RelativeLayout) findViewById(R.id.btn_container_brand);
		mTabContainers[2] = (RelativeLayout) findViewById(R.id.btn_container_expense);
		mTabContainers[3] = (RelativeLayout) findViewById(R.id.btn_container_me);

		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);
		mTabContainers[0].setSelected(true);
		typeMenu = (LinearLayout) findViewById(R.id.home_menu);
		menuSelectedTV = (TextView) findViewById(R.id.menuSelectedTV);
		homeMenuDatas = new ArrayList<String>();

		if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
			homeMenuDatas.add("班级");
			homeMenuDatas.add("年级");
			homeMenuDatas.add("学校");
			homeMenuDatas.add("教师");
			homeMenuDatasForService.add("teacher");
			homeMenuDatasForService.add("student");
		}else{
			homeMenuDatas.add("班级");
			homeMenuDatas.add("年级");
			homeMenuDatas.add("学校");
			homeMenuDatasForService.add("teacherGuid");
			homeMenuDatasForService.add("studentGuid");
			homeMenuDatasForService.add("studyResult");
		}
		typeMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {

				switch (SELECTMENUINDEX){
					case 0:
						indexForMenu = menuPosition;
						break;
					case 1:
						indexForMenu = menuPositionForWork;
						break;
				}
				ECListDialog d = new ECListDialog(MainActivity.this,homeMenuDatas,indexForMenu);
				d.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
					@Override
					public void onDialogItemClick(Dialog d, int position) {
						if (SELECTMENUINDEX == 0){
							menuPosition = position;
							menuSelectedTV.setText(homeMenuDatas.get(position));
							menuTitleForFirst = homeMenuDatas.get(position);
							homeFragment.setDatasToNull();
							homeFragment.initDatasForMain();
							homeFragment.getSwitch();
						}

						if (SELECTMENUINDEX == 1){
							menuPositionForWork = position;
							workSpaceFragment.selectIndexTag = position;
							if (position == 0){
								workSpaceFragment.dialog.show();
								workSpaceFragment. workSpaceReFreshDatas(WorkSpaceGridAdapter.index,1);
								if (ECApplication.getInstance().getCurrentUser().getType().equals("0")){
									titleImgThirdLinear.setVisibility(View.GONE);
								}
								onSelectCancel();
							}
							if (position == 1){
								workSpaceFragment.dialog.show();
								selectBatchLinearIsShowing = true;
								workSpaceFragment. workSpaceReFreshDatas(WorkSpaceGridAdapter.index,1);
								if (ECApplication.getInstance().getCurrentUser().getType().equals("0")){
									titleImgThirdLinear.setVisibility(View.GONE);
								}
							}
							if (position == 2){
								workSpaceFragment.dialog.show();
								selectBatchLinearIsShowing = true;
								workSpaceFragment. workSpaceReFreshDatas(WorkSpaceGridAdapter.index,1);
								if (ECApplication.getInstance().getCurrentUser().getType().equals("0")){
									titleImgThirdLinear.setVisibility(View.VISIBLE);
									titleImgThird.setImageResource(R.drawable.upload);
								}
							}
							menuSelectedTV.setText(homeMenuDatas.get(position));
							menuTitleForSecond = homeMenuDatas.get(position);
						}
					}
				});
				d.show();
			}
		});
		fragTitle = (TextView) findViewById(R.id.main_title);
		titleImgFirst = (ImageView) findViewById(R.id.main_img_left_first);
		titleImgSecond = (ImageView) findViewById(R.id.main_img_left_second);
		titleImgSecondLinear = (LinearLayout) findViewById(R.id.main_img_left_second_linear);
		titleImgThirdLinear = (LinearLayout) findViewById(R.id.main_img_left_third_linear);
		titleImgSecondLinear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onImgSecondClick();
			}
		});
		titleImgThird = (ImageView) findViewById(R.id.main_img_left_third);
		titleImgThird.setImageResource(R.drawable.nav_edit);
		titleImgThirdLinear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onImgThirdClick();
			}
		});
	}

	/**
	 * 跳转到搜索条件的Activity
	 */
	private void onImgSecondClick(){
		switch(SELECTMENUINDEX){
			case 0:
				startActivityForResult(new Intent(context,SearchTreadsActivity.class),SEARCHTREADSCODE);
				break;
			case 1:
				startActivityForResult(new Intent(context,SearchWorkActivity.class),SEARCHWORKCODE);
				break;
		}
	}

	/**
	 * 上传资源及动态
	 */
	private void onImgThirdClick() {
		switch (SELECTMENUINDEX){
			case 0:
				startActivityForResult(new Intent(MainActivity.this,IntroduceTreadsActivity.class),INTRODUCETREADSCODE);
				break;
			case 1:
				startActivityForResult(new Intent(MainActivity.this,UpFileActivity.class),UPFILEACTIVITYCODE);
				break;
			case 2:
				startActivityForResult(new Intent(this, SelectScroActivity.class),SELECTSCROACTIVITYCODE);
				break;
		}
	}


	/**
	 * button点击事件
	 * 标题栏文字转换处理及图片显示处理
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
			case R.id.btn_conversation:
				typeMenu.setVisibility(View.VISIBLE);
				titleImgSecond.setVisibility(View.VISIBLE);
				titleImgThird.setVisibility(View.VISIBLE);
				titleImgSecond.setImageResource(R.drawable.nav_search);
				titleImgThirdLinear.setVisibility(View.VISIBLE);
				titleImgThird.setImageResource(R.drawable.nav_edit);
				fragTitle.setText("互动交流");
				menuSelectedTV.setText(menuTitleForFirst);
				if (homeMenuDatas != null){
					homeMenuDatas.clear();

				}else{
					homeMenuDatas = new ArrayList<String>();
				}
				if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
					homeMenuDatas.add("班级");
					homeMenuDatas.add("年级");
					homeMenuDatas.add("学校");
					homeMenuDatas.add("教师");
				}else{
					homeMenuDatas.add("班级");
					homeMenuDatas.add("年级");
					homeMenuDatas.add("学校");
				}
				index = 0;
				SELECTMENUINDEX = 0;
				break;
			case R.id.btn_address_list:
				titleImgSecond.setVisibility(View.VISIBLE);
				typeMenu.setVisibility(View.VISIBLE);
				titleImgSecond.setImageResource(R.drawable.nav_search);
				if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
					titleImgThirdLinear.setVisibility(View.VISIBLE);
					titleImgThird.setImageResource(R.drawable.upload);
				}else{
					titleImgThirdLinear.setVisibility(View.GONE);
				}
				if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
					fragTitle.setText("工作平台");
				}else{
					fragTitle.setText("自主学习");
				}
				menuSelectedTV.setText(menuTitleForSecond);
				if (ECApplication.getInstance().getCurrentUser().getType().equals("0")){
					if (menuTitleForSecond.equals("学习成果")){
						titleImgThirdLinear.setVisibility(View.VISIBLE);
						titleImgThird.setImageResource(R.drawable.upload);
					}else{
						titleImgThirdLinear.setVisibility(View.GONE);
					}
				}
				if (homeMenuDatas != null){
					homeMenuDatas.clear();
				}else{
					homeMenuDatas = new ArrayList<String>();

				}
				if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
					homeMenuDatas.add("教师备课资源");
					homeMenuDatas.add("学生生成资源");
				}else{
					homeMenuDatas.add("教师引导");
					homeMenuDatas.add("同伴互助");
					homeMenuDatas.add("学习成果");
				}
				index = 1;
				SELECTMENUINDEX = 1;
				break;
			case R.id.btn_circle:
				fragTitle.setText("积分排名");
				titleImgThirdLinear.setVisibility(View.VISIBLE);
				titleImgSecond.setVisibility(View.INVISIBLE);
				titleImgThird.setVisibility(View.VISIBLE);
				titleImgThird.setImageResource(R.drawable.time);
				typeMenu.setVisibility(View.INVISIBLE);
				SELECTMENUINDEX = 2;
				index = 2;
				break;
			case R.id.btn_app:
				titleImgSecond.setVisibility(View.INVISIBLE);
				titleImgThird.setVisibility(View.INVISIBLE);
				typeMenu.setVisibility(View.INVISIBLE);
				fragTitle.setText("我的");
				SELECTMENUINDEX = 3;
				index = 3;
				break;
		}
//		menuPosition = 0;
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager()
					.beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		mTabContainers[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		mTabContainers[index].setSelected(true);
		for (int i = 0; i < 4; i++) {
			if (index == i){
				mTabs[i].setTextColor(Color.parseColor("#4cbbda"));
			}else{
				mTabs[i].setTextColor(Color.parseColor("#000000"));
			}
		}
		currentTabIndex = index;
	}

	public void onUpdateApp (View v) {
		updata();
	}

	public void updata() {
//		ToastUtil.showMessage("正在检查版本更新，请稍等");
		PgyUpdateManager.register(MainActivity.this,
				new UpdateManagerListener() {

					@Override
					public void onUpdateAvailable(final String result) {

						// 将新版本信息封装到AppBean中
						final AppBean appBean = getAppBeanFromString(result);

						final AlertDialog alertDialog = new AlertDialog.Builder(
								MainActivity.this).create();
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
										startDownloadTask(MainActivity.this,
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

	public void onFeedBack (View v) {
		startActivity(new Intent(context, LoginActivity.class));
	}
	@Override
	protected void onResume() {
		super.onResume();
	}


	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			if((System.currentTimeMillis()-exitTime) > 2000){
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	public static boolean ScroSearchTag = false;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//工作平台资源审核返回时执行
		if (requestCode == PREPASSCODE){
			setAllSelectText(workSpaceFragment.isSelectAll());
		}

		if (requestCode == SELECTSCROACTIVITYCODE){//搜索积分
			if (map !=null){
				if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
					String startDate = (String) map.get("startDate");
					String endDate = (String) map.get("endDate");
					String clickId = null;
					TreeBean bean = (TreeBean) map.get("ScroTreeBean");
					if (startDate == null&&endDate == null&&bean == null){
						return;
					}
					ProgressUtil.show(context,"正在刷新");
					if (bean != null){
						clickId = bean.getId();
						scroFragment.initMap(ScroGridAdapter.index,startDate,endDate,clickId);
						scroFragment.refrushAdapter(-1);
					}else{
						scroFragment.initMap(ScroGridAdapter.index,startDate,endDate,clickId);
						scroFragment.refrushAdapter(0);
					}
					ScroSearchTag = true;
				}else{
					String startDate = (String) map.get("startDate");
					String endDate = (String) map.get("endDate");
					if (startDate != null&&endDate != null) {
						ProgressUtil.show(context, "正在刷新");
						scroFragment.initMap(1, startDate, endDate,null);
						scroFragment.refrushAdapter(0);
						ScroSearchTag = true;
					}
				}
			}
		}
		if (requestCode == SEARCHTREADSCODE){//查询互动交流
			if (map !=null){
				String name = (String) map.get("name");
				String startDate = (String) map.get("startDate");
				String endDate = (String) map.get("endDate");
				String eclassId = (String) map.get("eclassId");
				map.clear();
				if (name != null&&!name.equals("")||startDate != null||endDate != null||eclassId != null){
					ProgressUtil.show(this,"正在查询..");
					homeFragment.setDatasToNull();
//					homeFragment.setGridCurrent(0);
					homeFragment.initDatas(startDate,endDate,name,eclassId);
					homeFragment.initXListViewDatas(null);
				}
			}
		}
		if (requestCode == INTRODUCETREADSCODE){//发布互动交流
			String isSuccess = (String) MainActivity.map.get("IntroduceTreadsIsTrue");
			if (isSuccess != null&&isSuccess.equals("true")){
				homeFragment.setDatasToNull();
				homeFragment.refurseTreadsAdapter();
			}
		}

		if (requestCode == SEARCHWORKCODE){//工作平台搜索
			String name = (String) map.get("name");
			String clickId = (String) map.get("clickId");
			String type = (String) map.get("type");
			String eclassIds = (String) map.get("eclassIds");
			String status = (String) map.get("status");
			if (clickId != null|eclassIds != null){
				workSpaceFragment.onActReFresh(name,clickId,type,eclassIds,status);
				map.clear();
			}else{
				if (name != null){
					workSpaceFragment.onActReFresh(name,clickId,type,eclassIds,status);
					map.clear();
				}
			}
			onSelectCancel();
		}
		if (requestCode == UPFILEACTIVITYCODE){
			String tag = (String) MainActivity.map.get("UpFileActivityTag");
			if (tag != null&&tag.equals("true")){
				doToast("上传成功！");
				MainActivity.map.remove("UpFileActivityTag");
				workSpaceFragment.workSpaceReFreshDatas(WorkSpaceGridAdapter.index,1);
			}
			onSelectCancel();
		}

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_right:
				startActivity(new Intent(context, LoginActivity.class));
				break;
			default:
				break;
		}
	}

	public void checkVerson() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
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


	/**
	 * listView 无数据是的点击事件
	 * @param view
	 */
	public void onEmptyClick(View view){
		switch (view.getId()){
			case R.id.emptyall:
				homeFragment.onEmptyClick(0);
				break;
			case R.id.emptyinter:
				homeFragment.onEmptyClick(1);
				break;
			case R.id.emptymy:
				homeFragment.onEmptyClick(2);
				break;
			case R.id.emptyres:
				homeFragment.onEmptyClick(3);
				break;
			case R.id.emptyatt:
				homeFragment.onEmptyClick(4);
				break;
		}
	}
}
