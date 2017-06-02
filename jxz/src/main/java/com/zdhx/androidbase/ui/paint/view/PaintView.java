package com.zdhx.androidbase.ui.paint.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zdhx.androidbase.ui.paint.interfaces.PaintViewCallBack;
import com.zdhx.androidbase.ui.paint.interfaces.Shapable;
import com.zdhx.androidbase.ui.paint.interfaces.ShapesInterface;
import com.zdhx.androidbase.ui.paint.interfaces.ToolInterface;
import com.zdhx.androidbase.ui.paint.interfaces.UndoCommand;
import com.zdhx.androidbase.ui.paint.painttools.BlurPen;
import com.zdhx.androidbase.ui.paint.painttools.EmbossPen;
import com.zdhx.androidbase.ui.paint.painttools.Eraser;
import com.zdhx.androidbase.ui.paint.painttools.PlainPen;
import com.zdhx.androidbase.ui.paint.shapes.Circle;
import com.zdhx.androidbase.ui.paint.shapes.Curv;
import com.zdhx.androidbase.ui.paint.shapes.Line;
import com.zdhx.androidbase.ui.paint.shapes.Oval;
import com.zdhx.androidbase.ui.paint.shapes.Rectangle;
import com.zdhx.androidbase.ui.paint.shapes.Square;
import com.zdhx.androidbase.ui.paint.utils.BitMapUtils;
import com.zdhx.androidbase.ui.paint.utils.PaintConstants;

import java.util.ArrayList;

import static com.zdhx.androidbase.ui.paint.utils.PaintConstants.UNDO_STACK_SIZE;


public class PaintView extends View implements UndoCommand {

	boolean canvasIsCreated = false;

	private Canvas mCanvas = null;
	private ToolInterface mCurrentPainter = null;

	/* Bitmap ������ */
	private Bitmap mBitmap = null;
	private Bitmap mOrgBitMap = null;

	private int mBitmapWidth = 0;
	private int mBitmapHeight = 0;

	private int mBackGroundColor = PaintConstants.DEFAULT.BACKGROUND_COLOR;
	/* paint ������ */
	private Paint mBitmapPaint = null;

	private paintPadUndoStack mUndoStack = null;

	private int mPenColor = PaintConstants.DEFAULT.PEN_COLOR;;
	private int mPenSize = PaintConstants.PEN_SIZE.SIZE_1 ;

	private int mEraserSize = PaintConstants.ERASER_SIZE.SIZE_1;

	int mPaintType = PaintConstants.PEN_TYPE.PLAIN_PEN;

	private PaintViewCallBack mCallBack = null;
	private int mCurrentShapeType = 0;
	private ShapesInterface mCurrentShape = null;
	private Paint.Style mStyle = Paint.Style.STROKE;

	/* ���� ������ */
	private boolean isTouchUp = false;
	private int mStackedSize = UNDO_STACK_SIZE;

	public PaintView(Context context) {
		this(context, null);
	}

	public PaintView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PaintView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * ��ʼ��
	 */
	private void init() {
		mCanvas = new Canvas();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mUndoStack = new paintPadUndoStack(this, mStackedSize);
		mPaintType = PaintConstants.PEN_TYPE.PLAIN_PEN;
		mCurrentShapeType = PaintConstants.SHAP.CURV;
		creatNewPen();
	}

	/**
	 * �ص���������onHasDraw����
	 */
	public void setCallBack(PaintViewCallBack callBack) {
		mCallBack = callBack;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		isTouchUp = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mCanvas.setBitmap(mBitmap);
			creatNewPen();
			mCurrentPainter.touchDown(x, y);
			mUndoStack.clearRedo();
			mCallBack.onTouchDown();
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			mCurrentPainter.touchMove(x, y);
			if (mPaintType == PaintConstants.PEN_TYPE.ERASER) {
				mCurrentPainter.draw(mCanvas);
			}
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			if (mCurrentPainter.hasDraw()) {
				mUndoStack.push(mCurrentPainter);
				if (mCallBack != null) {
					// ����undo\redo����ʵ
					mCallBack.onHasDraw();
				}
			}
			mCurrentPainter.touchUp(x, y);
			// ֻ����up��ʱ�����bitmap�ϻ�ͼ��������ʾ��view��
			mCurrentPainter.draw(mCanvas);
			invalidate();
			isTouchUp = true;
			break;
		}
		return true;
	}

	/**
	 * ���þ�����״����Ҫע����ǹ��캯���е�Painter���������ʳ�¯��
	 */
	private void setShape() {
		if (mCurrentPainter instanceof Shapable) {
			switch (mCurrentShapeType) {
			case PaintConstants.SHAP.CURV:
				mCurrentShape = new Curv((Shapable) mCurrentPainter);
				break;
			case PaintConstants.SHAP.LINE:
				mCurrentShape = new Line((Shapable) mCurrentPainter);
				break;
			case PaintConstants.SHAP.SQUARE:
				mCurrentShape = new Square((Shapable) mCurrentPainter);
				break;
			case PaintConstants.SHAP.RECT:
				mCurrentShape = new Rectangle((Shapable) mCurrentPainter);
				break;
			case PaintConstants.SHAP.CIRCLE:
				mCurrentShape = new Circle((Shapable) mCurrentPainter);
				break;
			case PaintConstants.SHAP.OVAL:
				mCurrentShape = new Oval((Shapable) mCurrentPainter);
				break;
			default:
				break;
			}
			((Shapable) mCurrentPainter).setShap(mCurrentShape);
		}
	}

	@Override
	public void onDraw(Canvas cv) {
		cv.drawColor(mBackGroundColor);
		// ���ⲿ���Ƶķ���ֻ��һ�֣���������bitmap�ϻ��ƣ�Ȼ����ص�cv
		cv.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		// TouchUpʹ��BitMap��canvas���л��ƣ�Ҳ�Ͳ�����View�ϻ�����
		if (!isTouchUp) {
			// ƽʱ��ֻ��view��cv����ʱ����
			// earaser������cv�ϻ��ƣ���Ҫֱ�ӻ�����bitmap��
			if (mPaintType != PaintConstants.PEN_TYPE.ERASER) {
				mCurrentPainter.draw(cv);
			}
		}
	}

	/**
	 * ����һ���µĻ���
	 */
	void creatNewPen() {
		ToolInterface tool = null;
		switch (mPaintType) {
		case PaintConstants.PEN_TYPE.PLAIN_PEN:
			tool = new PlainPen(mPenSize, mPenColor, mStyle);
			break;
		case PaintConstants.PEN_TYPE.ERASER:
			tool = new Eraser(mEraserSize);
			break;
		case PaintConstants.PEN_TYPE.BLUR:
			tool = new BlurPen(mPenSize, mPenColor, mStyle);
			break;
		case PaintConstants.PEN_TYPE.EMBOSS:
			tool = new EmbossPen(mPenSize, mPenColor, mStyle);
			break;
		default:
			break;
		}
		mCurrentPainter = tool;
		setShape();
	}

	/**
	 * �����¼�����ʱ������Bitmap��setCanvas
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (!canvasIsCreated) {
			mBitmapWidth = w;
			mBitmapHeight = h;
			creatCanvasBitmap(w, h);
			canvasIsCreated = true;
		}
	}

	/**
	 * ���������ϵı���ͼƬ��ͬʱ���б���
	 */
	public void setForeBitMap(Bitmap bitmap) {
		if (bitmap != null) {
			recycleMBitmap();
			recycleOrgBitmap();
		}else{
			return;
		}
		mBitmap = BitMapUtils.duplicateBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight());
		Log.e("mBitmap",bitmap.getWidth()+"width");
		Log.e("mBitmap",bitmap.getHeight()+"getHeight");
		mOrgBitMap = BitMapUtils.duplicateBitmap(mBitmap);
		Log.e("mOrgBitMap",mOrgBitMap.getWidth()+"width");
		Log.e("mOrgBitMap",mOrgBitMap.getHeight()+"getHeight");
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		invalidate();
	}

	/**
	 * ����ԭʼͼƬ
	 */
	private void recycleOrgBitmap() {
		if (mOrgBitMap != null && !mOrgBitMap.isRecycled()) {
			mOrgBitMap.recycle();
			mOrgBitMap = null;
		}
	}

	/**
	 * ����ͼƬ
	 */
	private void recycleMBitmap() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
			mBitmap = null;
		}
	}

	/**
	 * �õ���ǰview�Ľ�ͼ
	 */
	public Bitmap getSnapShoot() {
		// ��õ�ǰ��view��ͼƬ
		setDrawingCacheEnabled(true);
		buildDrawingCache(true);
		Bitmap bitmap = getDrawingCache(true);
		Bitmap bmp = BitMapUtils.duplicateBitmap(bitmap);
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
		// �����������
		setDrawingCacheEnabled(false);
		return bmp;
	}

	/**
	 * �����Ļ
	 */
	public void clearAll() {
		recycleMBitmap();
		recycleOrgBitmap();
		mUndoStack.clearAll();
		creatCanvasBitmap(mBitmapWidth, mBitmapHeight);
		invalidate();
	}

	/**
	 * ����bitMapͬʱ�����canvas
	 */
	private void creatCanvasBitmap(int w, int h) {
		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas.setBitmap(mBitmap);
	}

	/**
	 * �ı䵱ǰ���ʵ�����
	 */
	public void setCurrentPainterType(int type) {
		switch (type) {
		case PaintConstants.PEN_TYPE.BLUR:
		case PaintConstants.PEN_TYPE.PLAIN_PEN:
		case PaintConstants.PEN_TYPE.EMBOSS:
		case PaintConstants.PEN_TYPE.ERASER:
			mPaintType = type;
			break;
		default:
			mPaintType = PaintConstants.PEN_TYPE.PLAIN_PEN;
			break;
		}
	}

	/**
	 * �ı䵱ǰ��Shap
	 */
	public void setCurrentShapType(int type) {
		switch (type) {
		case PaintConstants.SHAP.CURV:
		case PaintConstants.SHAP.LINE:
		case PaintConstants.SHAP.RECT:
		case PaintConstants.SHAP.CIRCLE:
		case PaintConstants.SHAP.OVAL:
		case PaintConstants.SHAP.SQUARE:
			mCurrentShapeType = type;
			break;
		default:
			mCurrentShapeType =  PaintConstants.SHAP.CURV;
			break;
		}
	}

	/**
	 * �õ���ǰ���ʵ�����
	 */
	public int getCurrentPainter() {
		return mPaintType;
	}

	/**
	 * �ı䵱ǰ���ʵĴ�С
	 */
	public void setPenSize(int size) {
		mPenSize = size;
	}

	/**
	 * �ı䵱ǰEraser�Ĵ�С
	 */
	public void setEraserSize(int size) {
		mEraserSize = size;
	}

	/**
	 * �õ���ǰ���ʵĴ�С
	 */
	public int getPenSize() {
		return mPenSize;
	}

	/**
	 * ����״̬
	 */
	public void resetState() {
		setCurrentPainterType(PaintConstants.PEN_TYPE.PLAIN_PEN);
		setPenColor(PaintConstants.DEFAULT.PEN_COLOR);
		setBackGroundColor(PaintConstants.DEFAULT.BACKGROUND_COLOR);
		mUndoStack.clearAll();
	}

	/**
	 * ���ı�����ɫ
	 */
	public void setBackGroundColor(int color) {
		mBackGroundColor = color;
		invalidate();
	}

	/**
	 * �õ�������ɫ
	 */
	public int getBackGroundColor() {
		return mBackGroundColor;
	}

	/**
	 * �ı仭�ʵ���ɫ���ڴ����±ʵ�ʱ�����ʹ����
	 */
	public void setPenColor(int color) {
		mPenColor = color;
	}

	/**
	 * �õ�penColor
	 */
	public int getPenColor() {
		return mPenColor;
	}

	/**
	 * ������ʱ������û�б���
	 */
	protected void setTempForeBitmap(Bitmap tempForeBitmap) {
		if (null != tempForeBitmap) {
			recycleMBitmap();
			mBitmap = BitMapUtils.duplicateBitmap(tempForeBitmap);
			if (null != mBitmap && null != mCanvas) {
				mCanvas.setBitmap(mBitmap);
				invalidate();
			}
		}
	}

	public void setPenStyle(Style style) {
		mStyle = style;
	}

	public byte[] getBitmapArry() {
		return BitMapUtils.bitampToByteArray(mBitmap);
	}

	@Override
	public void undo() {
		if (null != mUndoStack) {
			mUndoStack.undo();
		}
	}

	@Override
	public void redo() {
		if (null != mUndoStack) {
			mUndoStack.redo();
		}
	}

	@Override
	public boolean canUndo() {
		return mUndoStack.canUndo();
	}

	@Override
	public boolean canRedo() {
		return mUndoStack.canRedo();
	}

	@Override
	public String toString() {
		return "mPaint" + mCurrentPainter + mUndoStack;
	}

	/*
	 * ===================================�ڲ��࿪ʼ=================================
	 * �ڲ��࣬����undo��redo
	 */
	public class paintPadUndoStack {
		private int m_stackSize = 0;
		private PaintView mPaintView = null;
		private ArrayList<ToolInterface> mUndoStack = new ArrayList<ToolInterface>();
		private ArrayList<ToolInterface> mRedoStack = new ArrayList<ToolInterface>();
		private ArrayList<ToolInterface> mOldActionStack = new ArrayList<ToolInterface>();

		public paintPadUndoStack(PaintView paintView, int stackSize) {
			mPaintView = paintView;
			m_stackSize = stackSize;
		}

		/**
		 * ��painter����ջ��
		 */
		public void push(ToolInterface penTool) {
			if (null != penTool) {
				// ���undo�Ѿ�����
				if (mUndoStack.size() == m_stackSize && m_stackSize > 0) {
					// �õ���Զ�Ļ���
					ToolInterface removedTool = mUndoStack.get(0);
					// ���еıʼ�����
					mOldActionStack.add(removedTool);
					mUndoStack.remove(0);
				}

				mUndoStack.add(penTool);
			}
		}

		/**
		 * �������
		 */
		public void clearAll() {
			mRedoStack.clear();
			mUndoStack.clear();
			mOldActionStack.clear();
		}

		/**
		 * undo
		 */
		public void undo() {
			if (canUndo() && null != mPaintView) {
				ToolInterface removedTool = mUndoStack
						.get(mUndoStack.size() - 1);
				mRedoStack.add(removedTool);
				mUndoStack.remove(mUndoStack.size() - 1);

				if (null != mOrgBitMap) {
					// Set the temporary fore bitmap to canvas.
					// �������ļ�ʱ������һ��,����Ҫ���»��Ƴ���
					mPaintView.setTempForeBitmap(mPaintView.mOrgBitMap);
				} else {
					// ������������ڣ������´���һ�ݱ���
					mPaintView.creatCanvasBitmap(mPaintView.mBitmapWidth,
							mPaintView.mBitmapHeight);
				}

				Canvas canvas = mPaintView.mCanvas;

				// First draw the removed tools from undo stack.
				for (ToolInterface paintTool : mOldActionStack) {
					paintTool.draw(canvas);
				}

				for (ToolInterface paintTool : mUndoStack) {
					paintTool.draw(canvas);
				}

				mPaintView.invalidate();
			}
		}

		/**
		 * redo
		 */
		public void redo() {
			if (canRedo() && null != mPaintView) {
				ToolInterface removedTool = mRedoStack
						.get(mRedoStack.size() - 1);
				mUndoStack.add(removedTool);
				mRedoStack.remove(mRedoStack.size() - 1);

				if (null != mOrgBitMap) {
					// Set the temporary fore bitmap to canvas.
					mPaintView.setTempForeBitmap(mPaintView.mOrgBitMap);
				} else {
					// Create a new bitmap and set to canvas.
					mPaintView.creatCanvasBitmap(mPaintView.mBitmapWidth,
							mPaintView.mBitmapHeight);
				}

				Canvas canvas = mPaintView.mCanvas;
				// ������ǰ�ıʼ��������removedStack��
				// First draw the removed tools from undo stack.
				for (ToolInterface sketchPadTool : mOldActionStack) {
					sketchPadTool.draw(canvas);
				}
				// �����������Ǵӳ���������ƣ�����ֻ����ʱ�Ĵ洢
				for (ToolInterface sketchPadTool : mUndoStack) {
					sketchPadTool.draw(canvas);
				}

				mPaintView.invalidate();
			}
		}

		public boolean canUndo() {
			return (mUndoStack.size() > 0);
		}

		public boolean canRedo() {
			return (mRedoStack.size() > 0);
		}

		public void clearRedo() {
			mRedoStack.clear();
		}

		@Override
		public String toString() {
			return "canUndo" + canUndo();
		}
	}
	/* ==================================�ڲ������ ================================= */

}
