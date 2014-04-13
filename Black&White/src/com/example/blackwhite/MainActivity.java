package com.example.blackwhite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.NativeCameraView;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.*;
import org.opencv.video.Video;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
//import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import org.opencv.objdetect.CascadeClassifier;
import com.example.blackwhite.MainActivity;

import com.example.blackwhite.DetectionBasedTracker;
import com.example.blackwhite.R;
import com.example.blackwhite.MainActivity;
import com.example.blackwhite.LabActivity;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;





//@TargetApi(Build.VERSION_CODES.FROYO)
@SuppressLint("NewApi")
public class MainActivity extends Activity implements CvCameraViewListener2, OnTouchListener {
	
	
	
		// A key for storing the index of the active camera.
		private static final String STATE_CAMERA_INDEX = "cameraIndex";
		// The index of the active camera.
		private int mCameraIndex;
		// Whether the active camera is front-facing.
		// If so, the camera view should be mirrored.
		private boolean mIsCameraFrontFacing;
		// The number of cameras on the device.
		private CameraBridgeViewBase mCameraView;
		private int mNumCameras;
		// Whether the next camera frame should be saved as a photo.
		private boolean mIsPhotoPending;
		//Whether an asynchronous menu action is in progress.
		//If so, menu interaction should be disabled.
		private boolean mIsMenuLocked;
	//Image used for saving in gallery
		private Mat mBgr;
	
	
    private static final String TAG = "OCVSample::Activity";
    private Mat mRgba;
    private Mat mGray;
    private CameraBridgeViewBase mOpenCvCameraView;

    private MenuItem             mItemPreviewRGBA;
    private MenuItem             mItemPreviewHist;
    private MenuItem             mItemPreviewCanny;
    private MenuItem             mItemPreviewSepia;
    private MenuItem             mItemPreviewSobel;
    private MenuItem             mItemPreviewZoom;
    private MenuItem             mItemPreviewPixelize;
    private MenuItem             mItemPreviewPosterize;
    private MenuItem			mItemPreviewGrey;
    private MenuItem  			mItemPreviewGaussian;
    private MenuItem  			mItemPreviewDialate;
    private MenuItem			mItemPreviewNegative;
    private MenuItem 			mItemPreviewNext;
    private MenuItem			mItemPreviewTake;
    public static final int      VIEW_MODE_RGBA      = 0;
   // public static final int      VIEW_MODE_HIST      = 1;
    public static final int      VIEW_MODE_CANNY     = 2;
    public static final int      VIEW_MODE_SEPIA     = 3;
    public static final int      VIEW_MODE_SOBEL     = 4;
    public static final int      VIEW_MODE_ZOOM      = 5;
    public static final int      VIEW_MODE_PIXELIZE  = 6;
    public static final int      VIEW_MODE_POSTERIZE = 7;
    public static final int 	VIEW_MODE_GREY=8;
    public static final int 	VIEW_MODE_GAUSSIAN=9;
    public static final int 	VIEW_MODE_DIALATE=10;
    public static final int 	VIEW_MODE_NEGATIVE=11;
    public static final int 	VIEW_MODE_NEXT=12;
    public static final int 	VIEW_MODE_TAKE=1;
    public static int           viewMode = VIEW_MODE_RGBA;
    private Mat 				mTake;
    private Mat                  mIntermediateMat;
    private Mat                  mHist;
    private Mat                  mMat0;
    private MatOfInt             mChannels[];
    private MatOfInt             mHistSize;
    private int                  mHistSizeNum;
    private MatOfFloat           mRanges;
    private Scalar               mColorsRGB[];
    private Scalar               mColorsHue[];
    private Scalar               mWhilte;
    private Point                mP1;
    private Point                mP2;
    private float                mBuff[];
    private Mat                  mRgbaInnerWindow;
    private Mat                  mGrayInnerWindow;
    private Mat                  mZoomWindow;
    private Mat                  mZoomCorner;
    private Mat                  mSepiaKernel;
    private Size                 mSize0;
    private Size                 mSizeRgba;
    private Size                 mSizeRgbaInner;
    
    //farneback
    private Mat					prev;
    private Mat					next;
    private Mat 				flow;
    int 						flag=1;
    int 						flag1=0;
    
    
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    
                    
                    mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                } break;
                default:
                {
                	
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    
    public MainActivity() {
      //  Log.i(TAG, "Instantiated new " + this.getClass());
    }
 
   // @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint({ "NewApi", "InlinedApi" })
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        final Window window =getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (savedInstanceState != null) {
        mCameraIndex = savedInstanceState.getInt(STATE_CAMERA_INDEX, 0);
        } else {
        mCameraIndex = 0;
        }
      //  mOpenCvCameraView.setCameraIndex(1);
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.GINGERBREAD) {
        CameraInfo cameraInfo = new CameraInfo();
        Camera.getCameraInfo(mCameraIndex, cameraInfo);
        mIsCameraFrontFacing = (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT);
        mNumCameras = Camera.getNumberOfCameras();
        } else { // pre-Gingerbread
        // Assume there is only 1 camera and it is rear-facing.
        mIsCameraFrontFacing = false;
        mNumCameras = 1;
        }
        
        setContentView(R.layout.tutorial1_surface_view);
       
        ImageButton button = (ImageButton)findViewById(R.id.button_click);
        
        button.setOnClickListener(
          	new Button.OnClickListener() {
          		public void onClick(View v) {
          			 takePhoto(mTake);
          			
                  	 
                       
          		}
          	}
          );
        
        
        mOpenCvCameraView = new NativeCameraView(this, mCameraIndex);
        
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_native_surface_view);
 
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
 
        mOpenCvCameraView.setCvCameraViewListener(this);
        
      
       
        
    }
 
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
 
    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }
 
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }
    
 
     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
   
        mItemPreviewRGBA =menu.add("Preview RGBA");
        mItemPreviewTake=menu.add("Take");
       // mItemPreviewHist  = menu.add("Histograms");
        mItemPreviewCanny = menu.add("Canny");
        mItemPreviewSepia = menu.add("Sepia");
        mItemPreviewSobel = menu.add("Sobel");
        mItemPreviewZoom  = menu.add("Zoom");
        mItemPreviewPixelize  = menu.add("Pixelize");
        mItemPreviewPosterize = menu.add("Posterize");
        mItemPreviewGrey=menu.add("Grey");
        mItemPreviewGaussian=menu.add("Gaussian");
        mItemPreviewDialate=menu.add("DialateE");
        mItemPreviewNegative=menu.add("Negative");
        mItemPreviewNext=menu.add("Next");
       
       // mItemPreviewTake.setIcon(new ImageIcon("/click.png"));
      //  getMenuInflater().inflate(R.menu.main, menu);

        if (mNumCameras < 2) {
        // Remove the option to switch cameras, since there is
        // only 1.
        	menu.removeItem(R.id.menu_next_camera);
        	Log.i(TAG, "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" );
        }

        return true;
        
    }
    
    
    @SuppressLint("NewApi")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        /*
        switch (item.getItemId()) 
        {
        case R.id.menu_next_camera:
        mIsMenuLocked = true;
        // With another camera index, recreate the activity.
        mCameraIndex++;
        if (mCameraIndex == mNumCameras) {
        mCameraIndex = 0;
        }
        recreate();
        break;
        
        case R.id.menu_take_photo:
        mIsMenuLocked = true;
        // Next frame, take the photo.
        mIsPhotoPending = true;
        return true;
        default:
        return super.onOptionsItemSelected(item);
        }
        */
        

        
        
        
        if (item == mItemPreviewRGBA)
            viewMode = VIEW_MODE_RGBA;
  //      if (item == mItemPreviewHist)
   //         viewMode = VIEW_MODE_HIST;
        else if (item == mItemPreviewCanny)
            viewMode = VIEW_MODE_CANNY;
        else if (item == mItemPreviewSepia)
            viewMode = VIEW_MODE_SEPIA;
        else if (item == mItemPreviewSobel)
            viewMode = VIEW_MODE_SOBEL;
        else if (item == mItemPreviewZoom)
            viewMode = VIEW_MODE_ZOOM;
        else if (item == mItemPreviewPixelize)
            viewMode = VIEW_MODE_PIXELIZE;
        else if (item == mItemPreviewPosterize)
            viewMode = VIEW_MODE_POSTERIZE;
        else if(item==mItemPreviewGrey)
        	viewMode=VIEW_MODE_GREY;
        else if(item==mItemPreviewGaussian)
        	viewMode=VIEW_MODE_GAUSSIAN;
        else if(item==mItemPreviewDialate)
        	viewMode=VIEW_MODE_DIALATE;
        else if(item==mItemPreviewNegative)
        	viewMode=VIEW_MODE_NEGATIVE;
        else if(item==mItemPreviewNext)
        {
        	viewMode=VIEW_MODE_NEXT;
        	 mIsMenuLocked = true;
             // With another camera index, recreate the activity.
             mCameraIndex=mCameraIndex+1;
             if (mCameraIndex == mNumCameras) {
             mCameraIndex = 0;
        }
            
             recreate();
             return true;
        }
        else if(item==mItemPreviewTake)
        	{viewMode=VIEW_MODE_TAKE;
        	mIsMenuLocked = true;
            // Next frame, take the photo.
            mIsPhotoPending = true;
            return true;
        	}
        		
       
        return true;
    }

 
    public void onCameraViewStarted(int width, int height) {
    	mTake=new Mat();
    	mGray = new Mat();
        
        mRgba = new Mat();
        mGray = new Mat();
        mRgba = new Mat();
        mIntermediateMat = new Mat();
        mSize0 = new Size();
        mHist = new Mat();
        mChannels = new MatOfInt[] { new MatOfInt(0), new MatOfInt(1), new MatOfInt(2) };
        mHistSizeNum = 25;
        mBuff = new float[mHistSizeNum];
        mHistSize = new MatOfInt(mHistSizeNum);
        mRanges = new MatOfFloat(0f, 256f);
        mMat0  = new Mat();
        mColorsRGB = new Scalar[] { new Scalar(200, 0, 0, 255), new Scalar(0, 200, 0, 255), new Scalar(0, 0, 200, 255) };
        mColorsHue = new Scalar[] {
                new Scalar(255, 0, 0, 255),   new Scalar(255, 60, 0, 255),  new Scalar(255, 120, 0, 255), new Scalar(255, 180, 0, 255), new Scalar(255, 240, 0, 255),
                new Scalar(215, 213, 0, 255), new Scalar(150, 255, 0, 255), new Scalar(85, 255, 0, 255),  new Scalar(20, 255, 0, 255),  new Scalar(0, 255, 30, 255),
                new Scalar(0, 255, 85, 255),  new Scalar(0, 255, 150, 255), new Scalar(0, 255, 215, 255), new Scalar(0, 234, 255, 255), new Scalar(0, 170, 255, 255),
                new Scalar(0, 120, 255, 255), new Scalar(0, 60, 255, 255),  new Scalar(0, 0, 255, 255),   new Scalar(64, 0, 255, 255),  new Scalar(120, 0, 255, 255),
                new Scalar(180, 0, 255, 255), new Scalar(255, 0, 255, 255), new Scalar(255, 0, 215, 255), new Scalar(255, 0, 85, 255),  new Scalar(255, 0, 0, 255)
        };
        mWhilte = Scalar.all(255);
        mP1 = new Point();
        mP2 = new Point();

        // Fill sepia kernel
        mSepiaKernel = new Mat(4, 4, CvType.CV_32F);
        mSepiaKernel.put(0, 0, /* R */0.189f, 0.769f, 0.393f, 0f);
        mSepiaKernel.put(1, 0, /* G */0.168f, 0.686f, 0.349f, 0f);
        mSepiaKernel.put(2, 0, /* B */0.131f, 0.534f, 0.272f, 0f);
        mSepiaKernel.put(3, 0, /* A */0.000f, 0.000f, 0.000f, 1f);
        
    }
    private void CreateAuxiliaryMats() {
        if (mRgba.empty())
            return;

        mSizeRgba = mRgba.size();

        int rows = (int) mSizeRgba.height;
        int cols = (int) mSizeRgba.width;

        int left = cols/8 ;// cols/8
        int top = rows/8;// rows/8

        int width = cols * 3/4;//3/4
        int height = rows * 3/4 ;//3/4

        if (mRgbaInnerWindow == null)
            mRgbaInnerWindow = mRgba.submat(top, top + height, left, left + width);
        mSizeRgbaInner = mRgbaInnerWindow.size();

        if (mGrayInnerWindow == null && !mGray.empty())
            mGrayInnerWindow = mGray.submat(top, top + height, left, left + width);

        if (mZoomCorner == null)
            mZoomCorner = mRgba.submat(0, rows / 2 - rows / 10, 0, cols / 2 - cols / 10);

        if (mZoomWindow == null)
            mZoomWindow = mRgba.submat(rows / 2 - 9 * rows / 100, rows / 2 + 9 * rows / 100, cols / 2 - 9 * cols / 100, cols / 2 + 9 * cols / 100);
    }
    public void onCameraViewStopped() {
    }
 
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mTake=mRgba;
        //Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_BGRA2GRAY);
        mGray=inputFrame.gray();
      /*  
        if (mIsPhotoPending==true) {
        	mIsPhotoPending = false;
        	takePhoto(mRgba);
        	return mRgba;
        	}
        	if (mIsCameraFrontFacing) {
        	// Mirror (horizontally flip) the preview.
        	Core.flip(mRgba, mRgba, 1);
        	return mRgba;
        	}
        */

        
        switch (MainActivity.viewMode) {
        
        
        case MainActivity.VIEW_MODE_RGBA:
        			flag1=1;
        		break;

        

        case  (MainActivity.VIEW_MODE_CANNY):
        	
        	
         //    if ((mRgbaInnerWindow == null) || (mGrayInnerWindow == null) || (mRgba.cols() != mSizeRgba.width) || (mRgba.height() != mSizeRgba.height))
          //      CreateAuxiliaryMats();
           // Imgproc.Canny(mRgbaInnerWindow, mIntermediateMat, 80, 90);
            //Imgproc.cvtColor(mIntermediateMat, mRgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);
            Imgproc.Canny(mRgba, mRgba, 80, 90);
            flag1=2;
           
             break;

        case  MainActivity.VIEW_MODE_SOBEL:
        	mGray = inputFrame.gray();

            if ((mRgbaInnerWindow == null) || (mGrayInnerWindow == null) || (mRgba.cols() != mSizeRgba.width) || (mRgba.height() != mSizeRgba.height))
                CreateAuxiliaryMats();

            Imgproc.Sobel(mGrayInnerWindow, mIntermediateMat, CvType.CV_8U, 1, 1);
           Core.convertScaleAbs(mIntermediateMat, mIntermediateMat, 10, 0);
            Imgproc.cvtColor(mIntermediateMat, mRgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);
            
            flag1=3;
           break;

        case  MainActivity.VIEW_MODE_SEPIA:
            if ((mRgbaInnerWindow == null) || (mRgba.cols() != mSizeRgba.width) || (mRgba.height() != mSizeRgba.height))
                CreateAuxiliaryMats();
            Core.transform(mRgba, mTake, mSepiaKernel);
            
            flag1=4;
            
            break;

        case  MainActivity.VIEW_MODE_ZOOM:
            if ((mZoomCorner == null) || (mZoomWindow == null) || (mRgba.cols() != mSizeRgba.width) || (mRgba.height() != mSizeRgba.height))
                CreateAuxiliaryMats();
            Imgproc.resize(mZoomWindow, mZoomCorner, mZoomCorner.size());

            Size wsize = mZoomWindow.size();
            Core.rectangle(mZoomWindow, new Point(1, 1), new Point(wsize.width - 2, wsize.height - 2), new Scalar(255, 0, 0, 255), 2);
            flag1=5;
            mTake=mRgba;
            break;

        case  MainActivity.VIEW_MODE_PIXELIZE:
            if ((mRgbaInnerWindow == null) || (mRgba.cols() != mSizeRgba.width) || (mRgba.height() != mSizeRgba.height))
                CreateAuxiliaryMats();
            Imgproc.resize(mRgba, mRgba, mSize0, 0.1, 0.1, Imgproc.INTER_NEAREST);
            Imgproc.resize(mRgba, mRgba, mSize0, 0., 0., Imgproc.INTER_NEAREST);
            flag1=6;
            mTake=mRgba;
            break;

        case  MainActivity.VIEW_MODE_POSTERIZE:
            if ((mRgbaInnerWindow == null) || (mRgba.cols() != mSizeRgba.width) || (mRgba.height() != mSizeRgba.height))
                CreateAuxiliaryMats();
            /*
            Imgproc.cvtColor(mRgbaInnerWindow, mIntermediateMat, Imgproc.COLOR_RGBA2RGB);
            Imgproc.pyrMeanShiftFiltering(mIntermediateMat, mIntermediateMat, 5, 50);
            Imgproc.cvtColor(mIntermediateMat, mRgbaInnerWindow, Imgproc.COLOR_RGB2RGBA);
            */

            Imgproc.Canny(mRgbaInnerWindow, mIntermediateMat, 80, 90);
            mRgbaInnerWindow.setTo(new Scalar(0, 0, 0, 255), mIntermediateMat);
            Core.convertScaleAbs(mRgba, mRgba, 1./16, 0);
            Core.convertScaleAbs(mRgba, mRgba, 16, 0);
            flag1=7;
            mTake=mRgba;
            break;
            
        case MainActivity.VIEW_MODE_GREY:
        	mRgba=mGray;
        	flag1=8;
        	break;
        case MainActivity.VIEW_MODE_GAUSSIAN:
        	 Mat sourceImage = new Mat();
             Mat destImage = mRgba.clone();
             for(int i=0;i<10;i++){
                 sourceImage = destImage.clone();
                 Imgproc.blur(sourceImage, destImage, new Size(3.0, 3.0));
             }
             mRgba=destImage;
             mTake=mRgba;
             flag1=9;
             break;
             
        case MainActivity.VIEW_MODE_DIALATE:
        
        //	Imgproc.erode(mGray, mGray, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2)));  
        	Imgproc.dilate(mGray, mGray, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2))); 
        	flag1=10;
        	mTake=mGray;
        	//return mGray;
        	
        	 break;
        case MainActivity.VIEW_MODE_NEGATIVE:
        	
        	Mat A=mGray;
        	Mat C = A.clone();
        	Size sizeA = A.size();
        	for (int i = 0; i < sizeA.height; i++)
        	    for (int j = 0; j < sizeA.width; j++) {
        	        double[] data = A.get(i, j);
        	        data[0] = 255-data[0] ;
        	       
        	        C.put(i, j, data);
        	    }
        	flag1=11;
        	mTake=C;
        	break;
        	//return C;
        
        	
        /*	 int rows = (int) mSizeRgba.height;
             int cols = (int) mSizeRgba.width;
             Scalar debugColor;
             debugColor = new Scalar(255);
        	 Mat M =  new Mat(rows,cols, CvType.CV_16UC1,debugColor);
        	 Mat C=null;
        	Core.absdiff(M, mRgba,  C);
        	 return C;
        	*/ 
        	
     case MainActivity.VIEW_MODE_TAKE:
        	if (mIsPhotoPending==true) {
            	mIsPhotoPending = false;
            if(flag1==1)
            {
            	takePhoto(mTake);
            	return(mTake);
            }
            else if(flag1==2)
            {
            	 if ((mRgbaInnerWindow == null) || (mGrayInnerWindow == null) || (mRgba.cols() != mSizeRgba.width) || (mRgba.height() != mSizeRgba.height))
                     CreateAuxiliaryMats();
                // Imgproc.Canny(mRgbaInnerWindow, mIntermediateMat, 80, 90);
                 //Imgproc.cvtColor(mIntermediateMat, mRgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);
                 Imgproc.Canny(mRgba, mTake, 80, 90);
                 flag1=0;
                 takePhoto(mTake);
             	return (mTake);
            	
            }
            else if(flag1==3)
            { if ((mRgbaInnerWindow == null) || (mGrayInnerWindow == null) || (mRgba.cols() != mSizeRgba.width) || (mRgba.height() != mSizeRgba.height))
                CreateAuxiliaryMats();

            Imgproc.Sobel(mGrayInnerWindow, mIntermediateMat, CvType.CV_8U, 1, 1);
            Core.convertScaleAbs(mIntermediateMat, mIntermediateMat, 10, 0);
            Imgproc.cvtColor(mIntermediateMat, mRgbaInnerWindow, Imgproc.COLOR_GRAY2BGRA, 4);
            flag1=0;
            mTake=mRgba;
            takePhoto(mTake);
        	return mTake;
        	}
            else if(flag1==4)
            {
            	 if ((mRgbaInnerWindow == null) || (mRgba.cols() != mSizeRgba.width) || (mRgba.height() != mSizeRgba.height))
                     CreateAuxiliaryMats();
                 Core.transform(mRgba, mTake, mSepiaKernel);;
                 flag1=0;
                 takePhoto(mTake);
             	return mTake;
            }
            else if(flag1==5)
            {
            	 if ((mZoomCorner == null) || (mZoomWindow == null) || (mRgba.cols() != mSizeRgba.width) || (mRgba.height() != mSizeRgba.height))
                     CreateAuxiliaryMats();
                 Imgproc.resize(mZoomWindow, mZoomCorner, mZoomCorner.size());

                 Size wsize1 = mZoomWindow.size();
                 Core.rectangle(mZoomWindow, new Point(1, 1), new Point(wsize1.width - 2, wsize1.height - 2), new Scalar(255, 0, 0, 255), 2);
                 flag1=0;
                 mTake=mRgba;
                 takePhoto(mTake);
             	return mTake;
            }
            else if(flag1==6)
            {
            	 if ((mRgbaInnerWindow == null) || (mRgba.cols() != mSizeRgba.width) || (mRgba.height() != mSizeRgba.height))
                     CreateAuxiliaryMats();
                 Imgproc.resize(mRgbaInnerWindow, mIntermediateMat, mSize0, 0.1, 0.1, Imgproc.INTER_NEAREST);
                 Imgproc.resize(mIntermediateMat, mRgbaInnerWindow, mSizeRgbaInner, 0., 0., Imgproc.INTER_NEAREST);
                 flag1=0;
                 takePhoto(mRgba);
             	return mRgba;
            }
            else if(flag1==7)
            {
            	 if ((mRgbaInnerWindow == null) || (mRgba.cols() != mSizeRgba.width) || (mRgba.height() != mSizeRgba.height))
                     CreateAuxiliaryMats();
                 /*
                 Imgproc.cvtColor(mRgbaInnerWindow, mIntermediateMat, Imgproc.COLOR_RGBA2RGB);
                 Imgproc.pyrMeanShiftFiltering(mIntermediateMat, mIntermediateMat, 5, 50);
                 Imgproc.cvtColor(mIntermediateMat, mRgbaInnerWindow, Imgproc.COLOR_RGB2RGBA);
                 */

            	 Imgproc.Canny(mRgbaInnerWindow, mIntermediateMat, 80, 90);
                 mRgbaInnerWindow.setTo(new Scalar(0, 0, 0, 255), mIntermediateMat);
                 Core.convertScaleAbs(mRgba, mRgba, 1./16, 0);
                 Core.convertScaleAbs(mRgba, mRgba, 16, 0);
                 flag1=0;
                 mTake=mRgba;
                 takePhoto(mTake);
             	return mTake;
            }
            else if(flag1==8)
            {
            	mRgba=mGray;
            	flag1=0;
            	mTake=mRgba;
            	takePhoto(mTake);
            	return mTake;
            }
            else if(flag==9)
            {
            	 Mat sourceImage1 = new Mat();
                 Mat destImage1 = mRgba.clone();
                 for(int i=0;i<10;i++){
                     sourceImage1 = destImage1.clone();
                     Imgproc.blur(sourceImage1, destImage1, new Size(3.0, 3.0));
                 }
                 mRgba=destImage1;
                 flag1=0;
                 takePhoto(mRgba);
             	return mRgba;
            }
            else if(flag1==10)
            {
            	Imgproc.dilate(mGray, mGray, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2))); 
            	flag1=0;
            	mRgba=mGray;
            	takePhoto(mRgba);
            	return mRgba;
            }
            else if(flag1==11)
            {
            	Mat A1=mGray;
            	Mat C1 = A1.clone();
            	Size sizeA1 = A1.size();
            	for (int i = 0; i < sizeA1.height; i++)
            	    for (int j = 0; j < sizeA1.width; j++) {
            	        double[] data = A1.get(i, j);
            	        data[0] = 255-data[0] ;
            	       
            	        C1.put(i, j, data);
            	    }
            	flag1=0;
            	mRgba=C1;
            	takePhoto(mRgba);
            	return mRgba;
            	/*
            	int rows1 = (int) mSizeRgba.height;
                int cols1 = (int) mSizeRgba.width;
                Scalar debugColor1;
                debugColor1 = new Scalar(255);
           	 Mat M1 =  new Mat(rows1,cols1, CvType.CV_16UC1,debugColor1);
           	 Mat C1=null;
           	Core.absdiff(M1, mRgba,  C1);
           	flag1=0;
           	takePhoto(C1);
           	 return C1;*/
            }
            /*	takePhoto(mRgba);
            	return mRgba;
            */	
            	}
        	break;
     case MainActivity.VIEW_MODE_NEXT:
    	 if (mIsCameraFrontFacing) {
         	// Mirror (horizontally flip) the preview.
    	
         	Core.flip(mRgba, mRgba, 1);
         	return mRgba;
    	 }
    	 	
           }
      
        return mRgba;
    }
    @SuppressLint("NewApi")
	private void takePhoto(final Mat mRgba) {
    	// Determine the path and metadata for the photo.
    	Log.d(TAG, "INSIDE TAKE PHOTO");
    	
    	final long currentTimeMillis = System.currentTimeMillis();
    	//final String appName = getString(R.string.app_name);
    	final String appName="BlackWhite";
    	Log.d(TAG, "INSIDE TAKE PHOTO1");
    	final String galleryPath =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
    	Log.d(TAG, "INSIDE TAKE PHOTO2");
    	final String albumPath = galleryPath + "/" + appName;
    	final String photoPath = albumPath + "/" +currentTimeMillis + ".png";
    	final ContentValues values = new ContentValues();
    	Log.d(TAG, "INSIDE TAKE PHOTO3");
    	values.put(MediaStore.MediaColumns.DATA, photoPath);
    	values.put(Images.Media.MIME_TYPE,LabActivity.PHOTO_MIME_TYPE);
    	values.put(Images.Media.TITLE, appName);
    	values.put(Images.Media.DESCRIPTION, appName);
    	values.put(Images.Media.DATE_TAKEN, currentTimeMillis);
    	// Ensure that the album directory exists.
    	File album = new File(albumPath);
    	Log.d(TAG, "INSIDE TAKE PHOTO4");
    	if (!album.isDirectory() && !album.mkdirs()) {
    	Log.e(TAG, "Failed to create album directory at " +albumPath);
    	onTakePhotoFailed();
    	return;
    	}
    	// Try to create the photo.
    	Log.d(TAG, "INSIDE TAKE PHOTO4.1(debug)");
    	//Imgproc.cvtColor(mRgba, mBgr, Imgproc.COLOR_RGBA2BGR, 3);
    	Log.d(TAG, "INSIDE TAKE PHOTO5");
    	if (!Highgui.imwrite(photoPath, mRgba)) {
    	Log.e(TAG, "Failed to save photo to " + photoPath);
    	onTakePhotoFailed();
    	}
    	Log.d(TAG, "Photo saved successfully to " + photoPath);
    	// Try to insert the photo into the MediaStore.
    	Uri uri;
    	try {
    		uri = getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
    		} catch (final Exception e) {
    		Log.e(TAG, "Failed to insert photo into MediaStore");
    		e.printStackTrace();
    		// Since the insertion failed, delete the photo.
    		File photo = new File(photoPath);
    		if (!photo.delete()) {
    		Log.e(TAG, "Failed to delete non-inserted photo");
    		}
    		onTakePhotoFailed();
    		return;
    		}
    		// Open the photo in LabActivity.
    	Log.d(TAG, "INSIDE TAKE PHOTO6");
    		final Intent intent = new Intent(this, LabActivity.class);
    		intent.putExtra(LabActivity.EXTRA_PHOTO_URI, uri);
    		intent.putExtra(LabActivity.EXTRA_PHOTO_DATA_PATH,photoPath);
    		startActivity(intent);
    		}
    		private void onTakePhotoFailed() {
    		mIsMenuLocked = false;
    		// Show an error message.
    		final String errorMessage =
    		getString(R.string.photo_error_message);
    		runOnUiThread(new Runnable() {
    		@Override
    		public void run() {
    		Toast.makeText(MainActivity.this, errorMessage,Toast.LENGTH_SHORT).show();
    		}
    		});
    		}
    

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
        
}
