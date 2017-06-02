package com.zdhx.androidbase.ui.paint.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.zdhx.androidbase.R;
import com.zdhx.androidbase.ui.MainActivity;
import com.zdhx.androidbase.ui.paint.interfaces.EditTextDialogListener;
import com.zdhx.androidbase.ui.paint.interfaces.OnClickOkListener;
import com.zdhx.androidbase.ui.paint.interfaces.PaintViewCallBack;
import com.zdhx.androidbase.ui.paint.utils.BitMapUtils;
import com.zdhx.androidbase.ui.paint.utils.ImageButtonTools;
import com.zdhx.androidbase.ui.paint.utils.PaintConstants;
import com.zdhx.androidbase.ui.paint.view.ColorPickerDialog;
import com.zdhx.androidbase.ui.paint.view.ColorView;
import com.zdhx.androidbase.ui.paint.view.OkCancleDialog;
import com.zdhx.androidbase.ui.paint.view.OkDialog;
import com.zdhx.androidbase.ui.paint.view.PaintView;
import com.zdhx.androidbase.ui.paint.view.SaveDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.zdhx.androidbase.ui.paint.utils.PaintConstants.COLOR1;
import static com.zdhx.androidbase.ui.paint.utils.PaintConstants.COLOR2;
import static com.zdhx.androidbase.ui.paint.utils.PaintConstants.COLOR3;
import static com.zdhx.androidbase.ui.paint.utils.PaintConstants.COLOR4;
import static com.zdhx.androidbase.ui.paint.utils.PaintConstants.COLOR5;
import static com.zdhx.androidbase.ui.paint.utils.PaintConstants.COLOR6;
import static com.zdhx.androidbase.ui.paint.utils.PaintConstants.COLOR7;
import static com.zdhx.androidbase.ui.paint.utils.PaintConstants.COLOR8;
import static com.zdhx.androidbase.ui.paint.utils.PaintConstants.COLOR_VIEW_SIZE;
import static com.zdhx.androidbase.ui.paint.utils.PaintConstants.LOAD_ACTIVITY;


/**
 * PaintActivity Activityʵ������������Activity��Ҫ����PaintView��������Э��
 *
 * @author tf
 *
 */
public class PaintActivity extends Activity implements OnClickListener {
	// PaintView
	private PaintView mPaintView = null;
	// button �����ϵĸ�����ť
	private ImageButton saveButton = null;
	private ImageButton loadButton = null;
	private ImageButton clearButton = null;
	private ImageButton eraserButton = null;
	private ImageButton colorSelectButton = null;
	private ImageButton penSizeButton = null;
	private ImageButton undoButton = null;
	private ImageButton redoButton = null;
	private ImageButton toButtonLayoutButton = null;
	private ImageButton toColorLayoutButton = null;
	private ImageButton toolButton = null;

	// ������߰�ť���������˵��ϵİ�ť
	private Button backGroundColorButton = null;
	private Button plainPaintButton = null;
	private Button blurPaintButton = null;
	private Button embossButton = null;

	// ���Menu�����Ĺ��ܲ˵�
	private ImageButton exitButton = null;
	private ImageButton aboutButton = null;
	private ImageButton helpButton = null;

	// ����PopWindow
	private PopupWindow mPopupWindow = null;
	private PopupWindow toolsPopupWindow = null;

	// һ��8��ColorView
	private ColorView colorView1 = null;
	private ColorView colorView2 = null;
	private ColorView colorView3 = null;
	private ColorView colorView4 = null;
	private ColorView colorView5 = null;
	private ColorView colorView6 = null;
	private ColorView colorView7 = null;
	private ColorView colorView8 = null;

	// ͨ������Layout������ĳЩ�仯
	private LinearLayout colorLayout = null;
	private LinearLayout buttonLayout = null;
	private RelativeLayout paintViewLayout = null;
	private LinearLayout eraserSizeLayout = null;
	private LinearLayout penSizeLayout = null;
	private LinearLayout shapLayout = null;
	private LinearLayout shapLayoutf = null;

	// һЩRadioGroup����Ҫ��(Ҳ����Ĭ�ϵ�)RadioButton
	private RadioGroup colorRadioGroup = null;
	private RadioGroup eraserSizeRadioGroup = null;
	private RadioButton eraserSizeRadio01 = null;
	private RadioGroup penSizeRadioGroup = null;
	private RadioButton penSizeRadio1 = null;
	private RadioGroup shapRadioGroup = null;
	private RadioGroup shapRadioGroupf = null;
	private RadioButton curvRadioButton = null;

	// ��������SizeRadioGroup��һЩ����
	private boolean clearCheckf = false;
	private boolean clearCheck = false;

	private List<ColorView> mColorViewList = null;

	// ʹ��PenType��ʱ�洢ѡ��ı�����������ʱ�ٴ���PaintView
	private int mPenType = PaintConstants.PEN_TYPE.PLAIN_PEN;

	private Context mContext;

	private ImageView img;

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_paint);
		img = (ImageView) findViewById(R.id.img);
		mContext = this;
		init();
	}

	private void init() {
		initLayout();
		initButtons();
		initColorViews();
		initPaintView();
		initPopUpWindow();
		initCallBack();
		initShapRadioGroups();

	}

	private void initFile(){
		File file = (File) MainActivity.map.get("fileForPaint");
		if(file != null){
//			MainActivity.map.remove("fileForPaint");
//			Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/primary%3ADownload%2Fjxz%2F-1884952496.jpg");
//			Uri uri = Uri.fromFile(file);
//			initActRes(uri);
			Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
			img.setImageBitmap(b);
//			Log.w("initFile",uri.toString());
			/*Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			mPaintView.setForeBitMap(bitmap);
			mPaintView.resetState();
			upDateUndoRedo();
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}*/
			/*ImageView imageView = new ImageView(mContext);
			DisplayMetrics metric = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metric);
			int width = metric.widthPixels;     // 屏幕宽度（像素）
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			int x = bitmap.getWidth();
			int y = bitmap.getHeight();

			float height = (float)width*(Float.parseFloat(y+"")/Float.parseFloat(x+""));
			LayoutParams margin9 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			margin9.setMargins(10, 30, 10, 0);//在左边距10像素，顶边距10像素的位置显示图片
			RelativeLayout.LayoutParams layoutParams9 = new RelativeLayout.LayoutParams(margin9);
			layoutParams9.height = (int)height;//设置图片的高度
			layoutParams9.width = width; //设置图片的宽度

			imageView.setLayoutParams(layoutParams9);
			imageView.setImageURI(Uri.fromFile(new File(ECApplication.getInstance().getDownloadJxzDir(),file.getName())));
			paintViewLayout.addView(imageView);
			mPaintView.setLayoutParams(layoutParams9);
			mPaintView.setForeBitMap(bitmap);
			mPaintView.resetState();
			upDateUndoRedo();*/
		}
	}

	/**
	 * ��ʼ�����е�RadioGroup
	 */
	private void initShapRadioGroups() {
		shapRadioGroup = (RadioGroup) findViewById(R.id.shapRadioGroup);
		curvRadioButton = (RadioButton) findViewById(R.id.RadioButtonShapCurv);
		shapRadioGroupf = (RadioGroup) findViewById(R.id.shapRadioGroupf);
		initEraseSizeGroup();
		initPenSizeGroup();
		initShapRadioGroup();
		initShapRadioGroupf();
	}

	private void initShapRadioGroupf() {
		shapRadioGroupf
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// �����ǰû��ѡ��
						if (checkedId == -1) {
							return;
						}
						if (clearCheck == false) {
							clearCheckf = true;
							shapRadioGroup.clearCheck();
						}
						mPaintView.setPenStyle(Paint.Style.FILL);
						switch (checkedId) {
							case R.id.RadioButtonShapLine:
								mPaintView.setCurrentShapType(PaintConstants.SHAP.LINE);
								mPaintView.setPenStyle(Paint.Style.STROKE);
								break;
							case R.id.RadioButtonShapRectf:
								mPaintView.setCurrentShapType(PaintConstants.SHAP.RECT);
								break;
							case R.id.RadioButtonShapCirclef:
								mPaintView.setCurrentShapType(PaintConstants.SHAP.CIRCLE);
								break;
							case R.id.RadioButtonShapOvalf:
								mPaintView.setCurrentShapType(PaintConstants.SHAP.OVAL);
								break;
							case R.id.RadioButtonShapSquaref:
								mPaintView.setCurrentShapType(PaintConstants.SHAP.SQUARE);
								break;
							default:
								break;
						}
						clearCheckf = false;
					}
				});
	}

	/**
	 * ��ʼ����һ��ShapRadioGroup
	 */
	private void initShapRadioGroup() {
		curvRadioButton.setChecked(true);
		shapRadioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// �����ǰû��ѡ��
						if (checkedId == -1) {
							return;
						}
						/**
						 * ����Ҫÿ�ζ�����
						 */
						if (clearCheckf == false) {
							clearCheck = true;
							shapRadioGroupf.clearCheck();
						}
						mPaintView.setPenStyle(Paint.Style.STROKE);
						switch (checkedId) {
							case R.id.RadioButtonShapCurv:
								mPaintView.setCurrentShapType(PaintConstants.SHAP.CURV);
								break;
							case R.id.RadioButtonShapRect:
								mPaintView.setCurrentShapType(PaintConstants.SHAP.RECT);
								break;
							case R.id.RadioButtonShapCircle:
								mPaintView.setCurrentShapType(PaintConstants.SHAP.CIRCLE);
								break;
							case R.id.RadioButtonShapOval:
								mPaintView.setCurrentShapType(PaintConstants.SHAP.OVAL);
								break;
							case R.id.RadioButtonShapSquare:
								mPaintView.setCurrentShapType(PaintConstants.SHAP.SQUARE);
								break;
							default:
								break;
						}
						clearCheck = false;
					}
				});
	}

	/**
	 * ��ʼ������ȷ��Pensize��radioGroup
	 */
	private void initPenSizeGroup() {
		penSizeRadioGroup = (RadioGroup) findViewById(R.id.penRaidoGroup);
		penSizeRadio1 = (RadioButton) findViewById(R.id.RadioButtonPen01);
		penSizeRadio1.setChecked(true);
		penSizeRadioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
							case R.id.RadioButtonPen01:
								mPaintView.setPenSize(PaintConstants.PEN_SIZE.SIZE_1);
								break;
							case R.id.RadioButtonPen02:
								mPaintView.setPenSize(PaintConstants.PEN_SIZE.SIZE_2);
								break;
							case R.id.RadioButtonPen03:
								mPaintView.setPenSize(PaintConstants.PEN_SIZE.SIZE_3);
								break;
							case R.id.RadioButtonPen04:
								mPaintView.setPenSize(PaintConstants.PEN_SIZE.SIZE_4);
								break;
							case R.id.RadioButtonPen05:
								mPaintView.setPenSize(PaintConstants.PEN_SIZE.SIZE_5);
								break;
							default:
								break;
						}
					}
				});
	}

	/**
	 * ��ʼ��EraserSizeѡ�񲼾�
	 */
	private void initEraseSizeGroup() {
		eraserSizeRadioGroup = (RadioGroup) findViewById(R.id.eraseRaidoGroup);
		eraserSizeRadio01 = (RadioButton) findViewById(R.id.RadioButtonEraser01);
		eraserSizeRadio01.setChecked(true);
		eraserSizeRadioGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
							case R.id.RadioButtonEraser01:
								mPaintView.setEraserSize(PaintConstants.ERASER_SIZE.SIZE_1);
								break;
							case R.id.RadioButtonEraser02:
								mPaintView.setEraserSize(PaintConstants.ERASER_SIZE.SIZE_2);
								break;
							case R.id.RadioButtonEraser03:
								mPaintView.setEraserSize(PaintConstants.ERASER_SIZE.SIZE_3);
								break;
							case R.id.RadioButtonEraser04:
								mPaintView.setEraserSize(PaintConstants.ERASER_SIZE.SIZE_4);
								break;
							case R.id.RadioButtonEraser05:
								mPaintView.setEraserSize(PaintConstants.ERASER_SIZE.SIZE_5);
								break;
							default:
								break;
						}
					}
				});
	}

	/**
	 * ��ʼ��paintView�Ļص�����
	 */
	private void initCallBack() {

		mPaintView.setCallBack(new PaintViewCallBack() {
			// ������֮���Button���и���
			@Override
			public void onHasDraw() {
				enableUndoButton();
				disableRedoButton();
			}

			// �����֮���ø��������Ĵ��ڶ���ʧ
			@Override
			public void onTouchDown() {
				setAllLayoutInvisable();
			}
		});
	}

	/**
	 * ��ʼ��popUpWindow
	 */
	private void initPopUpWindow() {
		initMenuPopup();
		initToolPopup();
	}

	/**
	 * ��ʼ��Tool��PopupWidow
	 */
	private void initToolPopup() {
		View toolsPopup = initToolPopWindowLayout();
		initToolPopButtons(toolsPopup);
	}

	/**
	 * ��ʼ��ToolsPopupWindow�Ĳ���
	 */
	private View initToolPopWindowLayout() {
		LayoutInflater mLayoutInflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View toolsPopup = mLayoutInflater.inflate(R.layout.tools, null);
		toolsPopupWindow = new PopupWindow(toolsPopup,
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
		toolsPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		return toolsPopup;
	}

	/**
	 * ��ʼ��ToolsPopupWindows�ϵ�Buttons
	 */
	private void initToolPopButtons(View toolsPopup) {
		plainPaintButton = (Button) toolsPopup
				.findViewById(R.id.buttonPlainPen);
		setToolButton(plainPaintButton);

		blurPaintButton = (Button) toolsPopup.findViewById(R.id.buttonBlurPen);
		setToolButton(blurPaintButton);

		backGroundColorButton = (Button) toolsPopup
				.findViewById(R.id.buttonSelectBackGroundColor);
		setToolButton(backGroundColorButton);

		embossButton = (Button) toolsPopup.findViewById(R.id.buttonEmboss);
		setToolButton(embossButton);
	}

	/**
	 * ��ʼ��Menu��popupWindow
	 */
	private void initMenuPopup() {
		View menuPopup = initPopLayout();
		initMenuPopButtons(menuPopup);
	}

	/**
	 * ��ʼ��MenuPopupWindow�Ĳ���
	 */
	private View initPopLayout() {
		LayoutInflater mLayoutInflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View menuPopup = mLayoutInflater.inflate(R.layout.translucent_button,
				null);
		mPopupWindow = new PopupWindow(menuPopup, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		return menuPopup;
	}

	/**
	 * ��ʼ��MenuPopupWindows�ϵ�Buttons
	 */
	private void initMenuPopButtons(View menuPopup) {
		exitButton = (ImageButton) menuPopup.findViewById(R.id.exitButton);
		exitButton
				.setImageDrawable(getResources().getDrawable(R.drawable.quit));
		exitButton.setOnClickListener(this);

		aboutButton = (ImageButton) menuPopup.findViewById(R.id.aboutButton);
		aboutButton.setImageDrawable(getResources().getDrawable(
				R.drawable.about));
		aboutButton.setOnClickListener(this);

		helpButton = (ImageButton) menuPopup.findViewById(R.id.helpButton);
		helpButton
				.setImageDrawable(getResources().getDrawable(R.drawable.help));
		helpButton.setOnClickListener(this);
	}

	/**
	 * ����popupWindow���õ��İ�ť�ĸ�ʽ
	 */
	private void setToolButton(Button button) {
		button.setOnClickListener(this);
		button.setTextColor(Color.WHITE);
	}

	/**
	 * ��ʼ���������õ�paintView
	 */
	private void initPaintView() {
		mPaintView = new PaintView(this);
//		File file = (File) MainActivity.map.get("fileForPaint");
//		Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//		mPaintView.setForeBitMap(bitmap);
		/*if(file != null){
			MainActivity.map.remove("fileForPaint");
			ImageView imageView = new ImageView(mContext);
			DisplayMetrics metric = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metric);
			int width = metric.widthPixels;     // 屏幕宽度（像素）
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			int x = bitmap.getWidth();
			int y = bitmap.getHeight();

			float height = (float)width*(Float.parseFloat(y+"")/Float.parseFloat(x+""));
			LayoutParams margin9 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			margin9.setMargins(10, 30, 10, 0);//在左边距10像素，顶边距10像素的位置显示图片
			RelativeLayout.LayoutParams layoutParams9 = new RelativeLayout.LayoutParams(margin9);
			layoutParams9.height = (int)height;//设置图片的高度
			layoutParams9.width = width; //设置图片的宽度

			imageView.setLayoutParams(layoutParams9);
//			imageView.setImageURI(Uri.fromFile(new File(ECApplication.getInstance().getDownloadJxzDir(),file.getName())));
//			paintViewLayout.addView(imageView);
//			mPaintView.setLayoutParams(layoutParams9);
			mPaintView.setForeBitMap(bitmap);

//			mPaintView.resetState();
//			upDateUndoRedo();
		}*/
		paintViewLayout.addView(mPaintView);
	}

	/**
	 * ��ʼ�����õ���Layout
	 */
	private void initLayout() {
		colorLayout = (LinearLayout) findViewById(R.id.LinearLayoutColor);
		buttonLayout = (LinearLayout) findViewById(R.id.buttonLayout);
		colorRadioGroup = (RadioGroup) findViewById(R.id.radioGroupColor);
		paintViewLayout = (RelativeLayout) findViewById(R.id.paintViewLayout);
		eraserSizeLayout = (LinearLayout) findViewById(R.id.sizeSelectLayout);
		penSizeLayout = (LinearLayout) findViewById(R.id.sizeSelectLayoutPen);
		eraserSizeLayout.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.popbackground4));
		penSizeLayout.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.popbackground4));
		shapLayout = (LinearLayout) findViewById(R.id.shapSelectLayout1);
		shapLayoutf = (LinearLayout) findViewById(R.id.shapSelectLayout2);
	}

	/**
	 * ��ʼ����ɫѡ���RadioGroup
	 */
	private void initColorRadioGroup() {
		mColorViewList = new ArrayList<ColorView>();
		mColorViewList.add(colorView1);
		mColorViewList.add(colorView2);
		mColorViewList.add(colorView3);
		mColorViewList.add(colorView4);
		mColorViewList.add(colorView5);
		mColorViewList.add(colorView6);
		mColorViewList.add(colorView7);
		mColorViewList.add(colorView8);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				COLOR_VIEW_SIZE, COLOR_VIEW_SIZE);
		params.setMargins(10, 5, 10, 5);

		for (ColorView colorView : mColorViewList) {
			colorRadioGroup.addView(colorView, params);
			colorView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
											 boolean isChecked) {
					for (ColorView colorView : mColorViewList) {
						if (buttonView.equals(colorView)
								&& buttonView.isChecked()) {
							setToLastPenTeype();
							mPaintView.setPenColor(colorView.getColor());
						}
					}
				}
			});
		}
	}

	/**
	 * �����Eraser��Set����һ��PenType
	 */
	private void setToLastPenTeype() {
		if (mPaintView.getCurrentPainter() == PaintConstants.PEN_TYPE.ERASER) {
			mPaintView.setCurrentPainterType(mPenType);
		}
	}

	/**
	 * ��ʼ����ɫѡ���View
	 */
	private void initColorViews() {
		// ��ȡpreference
		SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);

		// ��������ļ������ڣ���ʹ��Ĭ��ֵ
		colorView1 = new ColorView(this, settings.getInt("color1", COLOR1));
		colorView2 = new ColorView(this, settings.getInt("color2", COLOR2));
		colorView3 = new ColorView(this, settings.getInt("color3", COLOR3));
		colorView4 = new ColorView(this, settings.getInt("color4", COLOR4));
		colorView5 = new ColorView(this, settings.getInt("color5", COLOR5));
		colorView6 = new ColorView(this, settings.getInt("color6", COLOR6));
		colorView7 = new ColorView(this, settings.getInt("color7", COLOR7));
		colorView8 = new ColorView(this, settings.getInt("color8", COLOR8));
		initColorRadioGroup();
	}

	/**
	 * ��ʼ�����е�Button
	 */
	private void initButtons() {
		findButtonById();
		setBackGroundDrawable();
		List<ImageButton> list = initButtonList();
		for (ImageButton imageButton : list) {
			ImageButtonTools.setButtonFocusChanged(imageButton);
			imageButton.setOnClickListener(this);
		}
	}

	/**
	 * ����Ҫ�����ImageButton���뵽List��
	 */
	private List<ImageButton> initButtonList() {
		List<ImageButton> list = new ArrayList<ImageButton>();
		list.add(loadButton);
		list.add(clearButton);
		list.add(eraserButton);
		list.add(saveButton);
		list.add(penSizeButton);
		list.add(colorSelectButton);
		list.add(undoButton);
		list.add(redoButton);
		list.add(toButtonLayoutButton);
		list.add(toColorLayoutButton);
		list.add(toolButton);
		return list;
	}

	/**
	 * �ҵ����е�ͨ�����е�button
	 */
	private void findButtonById() {
		saveButton = (ImageButton) findViewById(R.id.imageButtonSave);
		loadButton = (ImageButton) findViewById(R.id.imageButtonLoadPicture);
		clearButton = (ImageButton) findViewById(R.id.imageButtonClear);
		eraserButton = (ImageButton) findViewById(R.id.imageButtonEraser);
		penSizeButton = (ImageButton) findViewById(R.id.imageButtonPen);
		colorSelectButton = (ImageButton) findViewById(R.id.imageButtonColorSelect);

		undoButton = (ImageButton) findViewById(R.id.imageButtonUndo);
		redoButton = (ImageButton) findViewById(R.id.imageButtonRedo);

		toolButton = (ImageButton) findViewById(R.id.imageButtonTools);

		toButtonLayoutButton = (ImageButton) findViewById(R.id.imageButtonToButtonLayout);
		toColorLayoutButton = (ImageButton) findViewById(R.id.imageButtonToColorLayout);
	}

	/**
	 * ��ʼ������Button��Drawable
	 */
	private void setBackGroundDrawable() {
		clearButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.newfile));
		eraserButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.eraser));
		loadButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.loadpicture));
		saveButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.save));
		penSizeButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.pen_default));
		colorSelectButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.colorselect));
		redoButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.cantredo));
		undoButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.cantundo));
		toButtonLayoutButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.changetobuttonlayout));
		toColorLayoutButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.changetocolorlayout));
		toolButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.tools));
	}

	/**
	 * onClick����
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.imageButtonSave:
				onClickButtonSave();
				break;

			case R.id.imageButtonLoadPicture:
				onClickButtonLoad();
				break;

			case R.id.imageButtonClear:
				onClickButtonClear();
				break;

			case R.id.imageButtonEraser:
				onClickButtonEraser();
				break;

			case R.id.imageButtonPen:
				onCLickButtonPen();
				break;

			case R.id.imageButtonColorSelect:
				onClickButtonColorSelect();
				break;

			case R.id.imageButtonUndo:
				onClickButtonUndo();
				break;

			case R.id.imageButtonRedo:
				onClickButtonRedo();
				break;

			case R.id.imageButtonToColorLayout:
				onClickButtonToColorLayout();
				break;

			case R.id.imageButtonToButtonLayout:
				onClickButtonToButtonLayout();
				break;

			case R.id.buttonSelectBackGroundColor:
				onClickButtonBackGround();
				break;

			case R.id.imageButtonTools:
				onClickButtonTools();
				break;

			case R.id.buttonBlurPen:
				onClickButtonBlurPen();
				break;

			case R.id.buttonPlainPen:
				onClickButtonPlainPen();
				break;

			case R.id.exitButton:
				onClickButtonExit();
				break;

			case R.id.buttonEmboss:
				onClickButtonEmboss();
				break;

			case R.id.aboutButton:
				onClickButtonAbout();
				break;

			case R.id.helpButton:
				onClickButtonHelp();
				break;
			default:
				break;
		}
	}

	/**
	 * ���ڵ�Activity
	 */
	void onClickButtonHelp() {
		Intent intent = new Intent(this, Help.class);
		startActivity(intent);
	}

	/**
	 * ���ڵ�Activity
	 */
	private void onClickButtonAbout() {
		Intent intent = new Intent(this, About.class);
		startActivity(intent);
	}

	/**
	 * ��������ŵ����ù�������
	 */
	private void setToolTyle(int type) {
		mPaintView.setCurrentPainterType(type);
		mPenType = type;
		toolsPopupWindow.dismiss();
	}

	/**
	 * ����Ч�����ڵ��֮��dissmiss
	 */
	private void onClickButtonEmboss() {
		setToolTyle(PaintConstants.PEN_TYPE.EMBOSS);
	}

	/**
	 * �˳�����
	 */
	private void onClickButtonExit() {
		this.finish();
	}

	/**
	 * ���Ǧ�ʹ���
	 */
	private void onClickButtonPlainPen() {
		setToolTyle(PaintConstants.PEN_TYPE.PLAIN_PEN);
	}

	/**
	 * blurPen
	 */
	private void onClickButtonBlurPen() {
		setToolTyle(PaintConstants.PEN_TYPE.BLUR);
	}

	/**
	 * ������ߣ���������ѡ��
	 */
	private void onClickButtonTools() {
		setAllLayoutInvisable();
		toolsPopupWindow.showAtLocation(findViewById(R.id.mainLayout),
				Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
	}

	/**
	 * ����ColorViews
	 */
	@Override
	protected void onPause() {
		SharedPreferences currentState = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = currentState.edit();
		editor.putInt("color1", colorView1.getColor());
		editor.putInt("color2", colorView2.getColor());
		editor.putInt("color3", colorView3.getColor());
		editor.putInt("color4", colorView4.getColor());
		editor.putInt("color5", colorView5.getColor());
		editor.putInt("color6", colorView6.getColor());
		editor.putInt("color7", colorView7.getColor());
		editor.putInt("color8", colorView8.getColor());
		editor.commit();
		super.onPause();
	}

	/**
	 * �����menu��ʱ��popupwindowαװ��menu��ʾ
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// ���menu
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			setAllLayoutInvisable();
			mPopupWindow.showAtLocation(findViewById(R.id.mainLayout),
					Gravity.RIGHT | Gravity.BOTTOM, 0, 0);
		}
		// �������
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			OkCancleDialog returnDialog = new OkCancleDialog(this,
					new OnClickOkListener() {
						@Override
						public void onClickOk() {
							finish();
						}
					});
			returnDialog.show();
			returnDialog.setMessage("确定要退出么？");
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	/**
	 * �ı䱳����ɫ
	 */
	private void onClickButtonBackGround() {
		new ColorPickerDialog(this, new ColorPickerDialog.OnColorChangedListener() {
			@Override
			public void colorChanged(int color) {
				mPaintView.setBackGroundColor(color);
				toolsPopupWindow.dismiss();
			}
		}, mPaintView.getBackGroundColor()).show();
		Log.e("aaa", ""+mPaintView.getBackGroundColor());
	}

	/**
	 * ȥButtonLayout�������棩
	 */
	private void onClickButtonToButtonLayout() {
		buttonLayout.setVisibility(View.VISIBLE);
		colorLayout.setVisibility(View.GONE);
	}

	/**
	 * ȥ��ɫѡ�����
	 */
	private void onClickButtonToColorLayout() {
		setAllLayoutInvisable();
		setToLastPenTeype();
		buttonLayout.setVisibility(View.INVISIBLE);
		colorLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * redo
	 */
	private void onClickButtonRedo() {
		setAllLayoutInvisable();
		mPaintView.redo();
		upDateUndoRedo();
	}

	/**
	 * undo
	 */
	private void onClickButtonUndo() {
		setAllLayoutInvisable();
		mPaintView.undo();
		upDateUndoRedo();
	}

	/**
	 * ����UndoRedo Button
	 */
	private void upDateUndoRedo() {
		if (mPaintView.canUndo()) {
			enableUndoButton();
		} else {
			disableUndoButton();
		}
		if (mPaintView.canRedo()) {
			enableRedoButton();
		} else {
			disableRedoButton();
		}
	}

	private void enableRedoButton() {
		redoButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.redo));
	}

	private void disableUndoButton() {
		undoButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.cantundo));
	}

	/**
	 * colorѡ����� �����Eraser���Ϊǰһ������
	 */
	private void onClickButtonColorSelect() {
		new ColorPickerDialog(this, new ColorPickerDialog.OnColorChangedListener() {
			@Override
			public void colorChanged(int color) {
				mPaintView.setPenColor(color);
				for (ColorView colorView : mColorViewList) {
					if (colorView.isChecked()) {
						setToLastPenTeype();
						colorView.setColor(color);
						Log.e("aaa", ""+color);

					}
				}
			}
		}, mPaintView.getPenColor()).show();
	}

	/**
	 * �ı仭�ʵĴ�С
	 */
	private void onCLickButtonPen() {
		mPaintView.setCurrentPainterType(mPenType);
		if (penSizeLayout.isShown()) {
			setAllLayoutInvisable();
		} else {
			penSizeLayout.setVisibility(View.VISIBLE);
			shapLayout.setVisibility(View.VISIBLE);
			shapLayoutf.setVisibility(View.VISIBLE);
			eraserSizeLayout.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * �����еĲ���ȫ������
	 */
	private void setAllLayoutInvisable() {
		shapLayout.setVisibility(View.INVISIBLE);
		shapLayoutf.setVisibility(View.INVISIBLE);
		penSizeLayout.setVisibility(View.INVISIBLE);
		eraserSizeLayout.setVisibility(View.INVISIBLE);
	}

	/**
	 * ��Ƥ
	 */
	private void onClickButtonEraser() {
		if (eraserSizeLayout.isShown()) {
			setAllLayoutInvisable();
		} else {
			eraserSizeLayout.setVisibility(View.VISIBLE);
			penSizeLayout.setVisibility(View.INVISIBLE);
			shapLayout.setVisibility(View.INVISIBLE);
			shapLayoutf.setVisibility(View.INVISIBLE);
		}
		mPaintView.setCurrentPainterType(PaintConstants.PEN_TYPE.ERASER);
	}

	/**
	 * ���
	 */
	private void onClickButtonClear() {
		setAllLayoutInvisable();
		clearDialog();
	}

	/**
	 * ��նԻ���
	 */
	private void clearDialog() {
		OkCancleDialog dialog = new OkCancleDialog(this,
				new OnClickOkListener() {
					@Override
					public void onClickOk() {
						mPaintView.clearAll();
						mPaintView.resetState();
						upDateUndoRedo();
						upDateColorView();
						resetSizeView();
					}
				});
		dialog.show();
		dialog.setCanceledOnTouchOutside(true);
		dialog.setMessage("确定要清空当前图画么？\n您未保存的修改将丢失");
	}

	/**
	 * ��ColorView��Check���
	 */
	private void upDateColorView() {
		colorRadioGroup.clearCheck();
	}

	/**
	 * ����sizeView
	 */
	private void resetSizeView() {
		penSizeRadio1.setChecked(true);
		eraserSizeRadio01.setChecked(true);
		curvRadioButton.setChecked(true);
	}

	/**
	 * ����ͼƬ
	 */
	private void onClickButtonLoad() {
		setAllLayoutInvisable();
		sendUpdateBroadCast();
		startLoadActivity();
	}

	/**
	 * ���͹㲥������sd���е����ݿ�
	 */
	private void sendUpdateBroadCast() {
//		IntentFilter intentFilter = new IntentFilter(
//				Intent.ACTION_MEDIA_SCANNER_STARTED);
//		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
//		intentFilter.addDataScheme("file");
////		new SingleMediaScanner(mContext, file);
//
//		sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//				Uri.parse("file://"
//						+ Environment.getExternalStorageDirectory()
//								.getAbsolutePath())));
	}

	/**
	 * ����
	 */
	private void onClickButtonSave() {
		setAllLayoutInvisable();
		boolean sdCardIsMounted = android.os.Environment
				.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED);
		if (!sdCardIsMounted) {
			OkDialog okDialog = new OkDialog(this, new OnClickOkListener() {
				@Override
				public void onClickOk() {
				}
			});
			okDialog.show();
			okDialog.setMessage("请插入存储卡");
		} else {
			SaveDialog dialog = new SaveDialog(this,
					new EditTextDialogListener() {
						// �����ȷ����ʱ���Զ����� getDialogText�ӿ�
						@Override
						public void getDialogText(String string) {
							String sdDir = getDirPath();
							String file = sdDir + string + ".png";
							Bitmap bitmap = mPaintView.getSnapShoot();
							BitMapUtils.saveToSdCard(file, bitmap);
							sendUpdateBroadCast();
						}
					});
			dialog.show();
		}
	}

	/**
	 * ��ʼ����activiy, ����imageIntent��Ѱ�Һ��ʵĳ�������
	 */
	private void startLoadActivity() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, LOAD_ACTIVITY);
	}

	private void initActRes(Uri uri){
//		Uri uri = data.getData();
		ContentResolver cr = this.getContentResolver();
		try {
			Bitmap bitmap;
			BitmapFactory.Options op = new BitmapFactory.Options();
			op.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(cr.openInputStream(uri), null,
					op);
			int wRatio = (int) Math.ceil(op.outWidth
					/ (float) mPaintView.getWidth());
			int hRatio = (int) Math.ceil(op.outHeight
					/ (float) mPaintView.getHeight());
			// 如果超出指定大小，则缩小相应的比例
			if (wRatio > 1 && hRatio > 1) {
				if (wRatio > hRatio) {
					op.inSampleSize = wRatio;
				} else {
					op.inSampleSize = hRatio;
				}
			}
			op.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeStream(
					cr.openInputStream(uri), null, op);
			bitmap = BitmapFactory
					.decodeStream(cr.openInputStream(uri));
			mPaintView.setForeBitMap(bitmap);
			mPaintView.resetState();
			upDateUndoRedo();
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����֮��õ�·��
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case LOAD_ACTIVITY:
				if (data != null) {
					Uri uri = data.getData();
					Log.w("onActResult",uri.toString());
					initActRes(uri);
					/*ContentResolver cr = this.getContentResolver();
					try {
						Bitmap bitmap;
						BitmapFactory.Options op = new BitmapFactory.Options();
						op.inJustDecodeBounds = true;
						BitmapFactory.decodeStream(cr.openInputStream(uri), null,
								op);
						int wRatio = (int) Math.ceil(op.outWidth
								/ (float) mPaintView.getWidth());
						int hRatio = (int) Math.ceil(op.outHeight
								/ (float) mPaintView.getHeight());
						// 如果超出指定大小，则缩小相应的比例
						if (wRatio > 1 && hRatio > 1) {
							if (wRatio > hRatio) {
								op.inSampleSize = wRatio;
							} else {
								op.inSampleSize = hRatio;
							}
						}
						op.inJustDecodeBounds = false;
						bitmap = BitmapFactory.decodeStream(
								cr.openInputStream(uri), null, op);
						bitmap = BitmapFactory
								.decodeStream(cr.openInputStream(uri));
						mPaintView.setForeBitMap(bitmap);
						mPaintView.resetState();
						upDateUndoRedo();
						if (bitmap != null && !bitmap.isRecycled()) {
							bitmap.recycle();
							bitmap = null;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}*/
				}
				break;
			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private String getDirPath() {
		File sdcarddir = android.os.Environment.getExternalStorageDirectory();
		String dirString = sdcarddir.getPath() + "/paintPad/";
		File filePath = new File(dirString);
		if (!filePath.exists()) {
			if (!filePath.mkdirs()) {
				OkDialog dialog = new OkDialog(this, new OnClickOkListener() {
					@Override
					public void onClickOk() {

					}
				});
				dialog.show();
				dialog.setMessage("无法在sd卡中创建目录/paintPad, \n请检查SDCard");
			}
		}
		return dirString;
	}

	private void enableUndoButton() {
		undoButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.undo));
	}

	private void disableRedoButton() {
		redoButton.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.cantredo));
	}
}