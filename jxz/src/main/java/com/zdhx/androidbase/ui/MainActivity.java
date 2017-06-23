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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
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
import com.zdhx.androidbase.SystemConst;
import com.zdhx.androidbase.entity.WorkSpaceDatasBean;
import com.zdhx.androidbase.ui.account.HomeFragment;
import com.zdhx.androidbase.ui.account.LoginActivity;
import com.zdhx.androidbase.ui.account.MeFragment;
import com.zdhx.androidbase.ui.account.ScroFragment;
import com.zdhx.androidbase.ui.account.ScroGridAdapter;
import com.zdhx.androidbase.ui.account.WorkSpaceFragment;
import com.zdhx.androidbase.ui.base.BaseActivity;
import com.zdhx.androidbase.ui.introducetreads.IntroduceTreadsActivity;
import com.zdhx.androidbase.ui.plugin.FileUtils;
import com.zdhx.androidbase.ui.quantity.PrePassActivity;
import com.zdhx.androidbase.ui.scrosearch.SelectScroActivity;
import com.zdhx.androidbase.ui.treadssearch.SearchTreadsActivity;
import com.zdhx.androidbase.ui.treadssearch.SearchWorkActivity;
import com.zdhx.androidbase.ui.treadssearch.UpFileActivity;
import com.zdhx.androidbase.ui.treelistview.bean.TreeBean;
import com.zdhx.androidbase.ui.xlistview.XListView;
import com.zdhx.androidbase.ui.ykt.BlackBoradAdapter;
import com.zdhx.androidbase.ui.ykt.SearchHandWriteActivity;
import com.zdhx.androidbase.ui.ykt.YKTFragment;
import com.zdhx.androidbase.util.LogUtil;
import com.zdhx.androidbase.util.ProgressUtil;
import com.zdhx.androidbase.util.StringUtil;
import com.zdhx.androidbase.util.ZddcUtil;
import com.zdhx.androidbase.util.permissionUtil.MPermission;
import com.zdhx.androidbase.view.dialog.ECListDialog;

import java.util.ArrayList;
import java.util.HashMap;

import static com.zdhx.androidbase.ui.account.WorkSpaceFragment.isSelectPosition;

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
	//云课堂
	private YKTFragment yktFragment;
	//积分
	private ScroFragment scroFragment;

	public static MainActivity act;


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
	//跳转到搜索手写笔记
	private final int SEARCHHANDWRITECODE = 5;

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
		//批量审核取消点击事件
		if (SELECTMENUINDEX == 1){
			WorkSpaceFragment.isBatchSelect = false;
			HashMap<WorkSpaceDatasBean.DataListBean, String> batchSelectMap = workSpaceFragment.getBatchSelectMap();
			for (WorkSpaceDatasBean.DataListBean in : batchSelectMap.keySet()) {
				in.setSelect(false);
			}
			workSpaceFragment.getBatchSelectMap().clear();
			workSpaceFragment.notifyForSelect();
		}
		//云课堂板书记录取消点击事件
		if (SELECTMENUINDEX == 2){
			BlackBoradAdapter.isMuchSelect = false;
			BlackBoradAdapter.isMuchSelectMap.clear();
			yktFragment.notifyForMuchSelect();
			setAllSelectText(false);
		}
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
			if (SELECTMENUINDEX == 1){
				workSpaceFragment.selectAll(true);
			}
			if (SELECTMENUINDEX == 2){
				yktFragment.selectAll(true);
			}
		}else{
			allSelect.setText("全  选");
			if (SELECTMENUINDEX == 1){
				workSpaceFragment.selectAll(false);
			}
			if (SELECTMENUINDEX == 2){
				yktFragment.selectAll(false);
			}
		}
	}

	/**
	 * 进行审核/发布云课堂板书记录
	 * @param view
	 */
	private final int PREPASSCODE = 111;
	public void onSelectTureClick(View view){

		if (SELECTMENUINDEX == 1){
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

		if (SELECTMENUINDEX == 2){
			yktFragment.onMuchSelectOK();
		}
	}

	/**
	 * 显示确定和取消批量选择
	 * @param isShow
	 */
	public static boolean selectBatchLinearIsShowing;
	public static void showSelectBatchLinear(boolean isShow){

		if (SELECTMENUINDEX == 1){
			selectBatchLinearIsShowing = isShow;
		}
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
		act = this;
		requestBasicPermission();
		getTopBarView().setVisibility(View.GONE);
		//只有二毛学校调用统计接口
		if (SystemConst.doAccess){
			getHideWebView().loadUrl(ZddcUtil.doAccess(ECApplication.getInstance().getLoginUrlMap()));
		}
		initLauncherUIView();



		animation = new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,
				0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		animation.setDuration(1000);//设置动画持续时间
		//常用方法
		animation.setRepeatCount(1000);//设置重复次数
		animation.setFillAfter(false);//动画执行完后是否停留在执行完的状态
		animation.setStartOffset(0);//执行前的等待时间
		animation.setInterpolator(new LinearInterpolator());
		iv = (ImageView) findViewById(R.id.iv);

	}
	private ImageView iv;
	private RotateAnimation animation;
	public void resetAnimImg(boolean b){

		if (iv == null ||animation == null){
			return ;
		}
		if (b){
			iv.setVisibility(View.VISIBLE);
			iv.startAnimation(animation);
		}else{
			animation.cancel();
			iv.clearAnimation();
			iv.setVisibility(View.INVISIBLE);
		}
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
						Manifest.permission.ACCESS_FINE_LOCATION,
						Manifest.permission.MANAGE_DOCUMENTS,
						Manifest.permission.WRITE_SETTINGS
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
		yktFragment = new YKTFragment();
		fragments = new Fragment[] { homeFragment, workSpaceFragment,yktFragment,scroFragment, meFragment };
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, homeFragment)
				.add(R.id.fragment_container, workSpaceFragment)
				.add(R.id.fragment_container, yktFragment)
				.add(R.id.fragment_container, scroFragment).add(R.id.fragment_container,meFragment)
				.hide(workSpaceFragment).hide(yktFragment).hide(scroFragment).hide(meFragment)
				.show(homeFragment).commitAllowingStateLoss();
		mTabs = new Button[5];
		mTabs[0] = (Button) findViewById(R.id.btn_conversation);
		mTabs[1] = (Button) findViewById(R.id.btn_address_list);
		if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
			mTabs[1].setText("工作平台");
		}else{
			mTabs[1].setText("自主学习");
		}
		mTabs[2] = (Button) findViewById(R.id.btn_ykt);
		mTabs[3] = (Button) findViewById(R.id.btn_circle);
		mTabs[4] = (Button) findViewById(R.id.btn_app);
		mTabContainers = new RelativeLayout[5];
		mTabContainers[0] = (RelativeLayout) findViewById(R.id.btn_container_conversation);
		mTabContainers[1] = (RelativeLayout) findViewById(R.id.btn_container_brand);
		mTabContainers[2] = (RelativeLayout) findViewById(R.id.btn_container_ykt);
		mTabContainers[3] = (RelativeLayout) findViewById(R.id.btn_container_expense);
		mTabContainers[4] = (RelativeLayout) findViewById(R.id.btn_container_me);

		if (ECApplication.getInstance().getUserForYKT() == null){
			mTabContainers[2].setVisibility(View.GONE);
		}else{
			mTabContainers[2].setVisibility(View.VISIBLE);
		}

		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);
		mTabContainers[0].setSelected(true);
		typeMenu = (LinearLayout) findViewById(R.id.home_menu);
		menuSelectedTV = (TextView) findViewById(R.id.menuSelectedTV);
		homeMenuDatas = new ArrayList<>();

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
				d.setTitle("请选择");
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
							if (workSpaceFragment.selectIndexTag != position){
								for (int i = 0; i < 9; i++) {
									if (i == WorkSpaceFragment.isSelectPosition){
										workSpaceFragment.setDataChanged(i,false);
									}else{
										workSpaceFragment.setDataChanged(i,true);
									}
								}
							}
							workSpaceFragment.selectIndexTag = position;
							if (position == 0){
								workSpaceFragment.dialog.show();
								workSpaceFragment. workSpaceReFreshDatas(WorkSpaceFragment.isSelectPosition,1);
								if (ECApplication.getInstance().getCurrentUser().getType().equals("0")){
									titleImgThirdLinear.setVisibility(View.GONE);
								}
								onSelectCancel();
							}
							if (position == 1){
								workSpaceFragment.dialog.show();
								selectBatchLinearIsShowing = true;
								workSpaceFragment. workSpaceReFreshDatas(isSelectPosition,1);
								if (ECApplication.getInstance().getCurrentUser().getType().equals("0")){
									titleImgThirdLinear.setVisibility(View.GONE);
								}
							}
							if (position == 2){
								workSpaceFragment.dialog.show();
								selectBatchLinearIsShowing = true;
								workSpaceFragment. workSpaceReFreshDatas(isSelectPosition,1);
								if (ECApplication.getInstance().getCurrentUser().getType().equals("0")){
									titleImgThirdLinear.setVisibility(View.VISIBLE);
									titleImgThird.setVisibility(View.VISIBLE);
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
			case 2:
				onSelectCancel();
				startActivityForResult(new Intent(context,SearchHandWriteActivity.class),SEARCHHANDWRITECODE);
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
			case 3:
				startActivityForResult(new Intent(this, SelectScroActivity.class),SELECTSCROACTIVITYCODE);
				break;
		}
	}

	private void btnConversation(){
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
	}

	private void btnAddressList(){
		titleImgSecond.setVisibility(View.VISIBLE);
		typeMenu.setVisibility(View.VISIBLE);
		titleImgSecond.setImageResource(R.drawable.nav_search);
		if (ECApplication.getInstance().getCurrentUser().getType().equals("2")){
			titleImgThirdLinear.setVisibility(View.VISIBLE);
			titleImgThird.setVisibility(View.VISIBLE);
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
				titleImgThird.setVisibility(View.VISIBLE);
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
	}

	private void btnCircle(){
		fragTitle.setText("积分排名");
		titleImgThirdLinear.setVisibility(View.VISIBLE);
		titleImgSecond.setVisibility(View.INVISIBLE);
		titleImgThird.setVisibility(View.VISIBLE);
		titleImgThird.setImageResource(R.drawable.time);
		typeMenu.setVisibility(View.INVISIBLE);
		SELECTMENUINDEX = 3;
		index = 3;
	}

	private void btnApp(){
		titleImgSecond.setVisibility(View.INVISIBLE);
		titleImgThird.setVisibility(View.INVISIBLE);
		titleImgThirdLinear.setVisibility(View.INVISIBLE);
		typeMenu.setVisibility(View.INVISIBLE);
		fragTitle.setText("我的");
		SELECTMENUINDEX = 4;
		index = 4;
	}

	private void btnYKT(){
		titleImgSecond.setVisibility(View.VISIBLE);
		titleImgThird.setVisibility(View.GONE);
		titleImgThirdLinear.setVisibility(View.GONE);
		typeMenu.setVisibility(View.INVISIBLE);
		fragTitle.setText("云课堂");
		SELECTMENUINDEX = 2;
		index = 2;
	}
	/**
	 * button点击事件
	 * 标题栏文字转换处理及图片显示处理
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
			case R.id.btn_conversation:
				btnConversation();
				break;
			case R.id.btn_address_list:
				btnAddressList();
				break;
			case R.id.btn_circle:
				btnCircle();
				break;
			case R.id.btn_app:
				btnApp();
				break;
			case R.id.btn_ykt:
				btnYKT();
				break;
		}
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


	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (ECApplication.getInstance().hasMorContant()){
			return super.onKeyDown(keyCode, event);
		}
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
					homeFragment.initDatas(startDate,endDate,name,eclassId, HomeFragment.isSelectIndex);
					homeFragment.initXListViewDatas(new XListView(this),HomeFragment.isSelectIndex);
				}
			}
		}
		if (requestCode == INTRODUCETREADSCODE){//发布互动交流
			String isSuccess = (String) MainActivity.map.get("IntroduceTreadsIsTrue");
			if (isSuccess != null&&isSuccess.equals("true")){
				homeFragment.setDatasToNull();
				homeFragment.refurseTreadsAdapter();
				MainActivity.map.remove("IntroduceTreadsIsTrue");
			}
		}

		if (requestCode == SEARCHWORKCODE){//工作平台搜索
			String name = (String) map.get("name");
			String clickId = (String) map.get("clickId");
			String type = (String) map.get("type");
			String eclassIds = (String) map.get("eclassIds");
			String status = (String) map.get("status");
			String highQuality = (String) map.get("highQuality");
			if (clickId != null|eclassIds != null){
				workSpaceFragment.onActReFresh(name,clickId,type,eclassIds,status,highQuality);
				map.clear();
				for (int i = 0; i < 9; i++) {
					if (i == WorkSpaceFragment.isSelectPosition){
						workSpaceFragment.setDataChanged(i,false);
					}else{
						workSpaceFragment.setDataChanged(i,true);
					}
				}
			}else{
				if (name != null){
					workSpaceFragment.onActReFresh(name,clickId,type,eclassIds,status,highQuality);
					map.clear();
					for (int i = 0; i < 9; i++) {
						if (i == WorkSpaceFragment.isSelectPosition){
							workSpaceFragment.setDataChanged(i,false);
						}else{
							workSpaceFragment.setDataChanged(i,true);
						}
					}
				}
			}

			onSelectCancel();
		}
		if (requestCode == UPFILEACTIVITYCODE){//上传资源
			String tag = (String) MainActivity.map.get("UpFileActivityTag");
			if (tag != null&&tag.equals("true")){
				doToast("上传成功！");
				MainActivity.map.remove("UpFileActivityTag");
				workSpaceFragment.showDialogForPrePassAct();
				workSpaceFragment.workSpaceReFreshDatas(WorkSpaceFragment.isSelectPosition,1);
				for (int i = 0; i < 9; i++) {
					if (i == WorkSpaceFragment.isSelectPosition){
						workSpaceFragment.setDataChanged(i,false);
					}else{
						workSpaceFragment.setDataChanged(i,true);
					}
				}
			}
			onSelectCancel();
		}
		//单张网络图片查看，判断是否下载了图片
		if (requestCode == 111){
			LogUtil.w("mainActivity回来");
		}

		if (requestCode == SEARCHHANDWRITECODE){//搜索手写笔记
			String isOK = (String) MainActivity.map.get("searchHandWriteOK");
			if (isOK != null && isOK.equals("OK")){
				MainActivity.map.remove("searchHandWriteOK");
				if (ECApplication.getInstance().getUserForYKT().getType().equals("2")){
					//无课程教师点击搜索无效
					if (SearchHandWriteActivity.selectWeekMap.get("true") == null||SearchHandWriteActivity.selectEclassMap.get("true") == null){
						return;
					}
					//教学周搜索
					int positionForWeek = SearchHandWriteActivity.selectWeekMap.get("true");
					int positionForEclass = SearchHandWriteActivity.selectEclassMap.get("true");

					yktFragment.setStartDate(YKTFragment.tweekVos.get(positionForWeek).getStartDate());
					yktFragment.setEndDate(YKTFragment.tweekVos.get(positionForWeek).getEndDate());
					//班级搜索
					yktFragment.setEclassId(YKTFragment.courses.get(positionForEclass).getId());
					yktFragment.getHandWriteForTeacher(yktFragment.YKTPOSITION);
				}
				if (ECApplication.getInstance().getUserForYKT().getType().equals("0")){
//					//教学周搜索
					int positionForWeek = SearchHandWriteActivity.selectWeekMap.get("true");
					yktFragment.setStartDate(YKTFragment.tweekVosForStudent.get(positionForWeek).getStartDate());
					yktFragment.setEndDate(YKTFragment.tweekVosForStudent.get(positionForWeek).getEndDate());
					//班级搜索
					int positionForCourses = SearchHandWriteActivity.selectBooksMap.get("true");
					yktFragment.setEclassId(YKTFragment.coursesForStudent.getEclass().getId());
					yktFragment.setCourseId(YKTFragment.coursesForStudent.getCourses().get(positionForCourses).getId());
					yktFragment.getHandWriteForStudent(yktFragment.YKTPOSITION);
				}
				yktFragment.showDialog(true,"正在获取数据...");
			}
		}

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		FileUtils.delFilesFromDir(ECApplication.getInstance().getDownloadJxzDir()+"/share");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_right:
				startActivity(new Intent(context, LoginActivity.class));
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		FileUtils.delFilesFromDir(ECApplication.getInstance().getDownloadJxzDir()+"/share");
	}

	/**
	 * listView 无数据时的点击事件
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
			case R.id.emptyimp:
				homeFragment.onEmptyClick(5);
				break;
		}
	}
}
