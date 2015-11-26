package toxz.me.whizz.additional;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.unique.whizzdo.R;
import com.unique.whizzdo.application.SettingsHelper;
import com.unique.whizzdo.data.DatabaseHelper;
import com.unique.whizzdo.data.Note;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by carlos on 6/5/14.
 */
public class QuickSnapActivity extends Activity {

    final int MAX_FOCUS_COUNT = 3;
    int mFocusCount = 0;
    File mRootFile = null;
    SurfaceView mSurfaceView = null;
    SurfaceHolder mSurfaceHolder = null;
    //    TextView mTextView = null;
    Camera mCamera = null;
    boolean isOpenCamera = false;
    int mHeight, mWidth;

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            e.printStackTrace();
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private void unlock() {
//        getWindowManager().updateViewLayout(this.getCurrentFocus(), new WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON));
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager.inKeyguardRestrictedInputMode()) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            Log.i("unlock()", "unlock to show");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("onCreate()", "QuickSnapActivity is created !");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_snap);
        unlock();
        init();
    }

    @SuppressWarnings("deprecation")
    boolean init() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = getWindowManager();
        wm.getDefaultDisplay().getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            this.finish();
            return false;
        }
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mRootFile = new File(root, "Whizz");
        if (!mRootFile.exists()) {
            mRootFile.mkdir();
        }
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder = mSurfaceView.getHolder();
//        mTextView = (TextView) findViewById(R.id.text);
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
            }

            public void surfaceCreated(SurfaceHolder holder) {
                Log.i("camera", "->[surfaceCreated] ");
                initCamera();
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }
        });
        return true;
    }

    @SuppressLint("NewApi")
    void initCamera() {
        mCamera = getCameraInstance();
        if (mCamera != null) {
            mCamera.setDisplayOrientation(90);
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPictureSize(parameters.getSupportedPreviewSizes().get(0).width, parameters.getSupportedPreviewSizes().get(0).height);
//                parameters.setPreviewFpsRange(4, 10);
                parameters.setPictureFormat(ImageFormat.JPEG);
                parameters.set("jpeg-quality", 25);
////                parameters.setPictureSize(mWidth, mHeight);
                parameters.setFlashMode(SettingsHelper.isSnapFlash(this) ? Camera.Parameters.FLASH_MODE_ON : Camera.Parameters.FLASH_MODE_OFF);
                mCamera.enableShutterSound(true);
                mCamera.setParameters(parameters);
//                Log.d("Process Report!", parameters.getFlashMode() + "");
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
                Log.i("camera", "->[startPreview] ");
            } catch (Exception e) {
                e.printStackTrace();
            }
            isOpenCamera = true;
            Camera.AutoFocusCallback mAutoFocusCallback = new MyAutoFocusCallback();

            mCamera.autoFocus(mAutoFocusCallback);
        } else {
            Log.d("Process Report !", "Camera is null !");
            QuickSnapActivity.this.finish();
        }
    }

    private class MyAutoFocusCallback implements Camera.AutoFocusCallback {
        public void onAutoFocus(boolean success, Camera camera) {

            Log.d("Process Report!", "onAutoFocus is called.");
            mFocusCount++;
            if (success || mFocusCount >= MAX_FOCUS_COUNT) {
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    public void onPictureTaken(byte[] data, Camera camera) {
                        if (data != null) {

                            final Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                            String time = new SimpleDateFormat("yyMMddHHmmss").format(new Date(System.currentTimeMillis()));
                            File file = new File(mRootFile, time + ".jpg");
                            FileOutputStream outStream = null;
                            try {
                                Log.i("camera", "->[file]" + file.getPath());
                                outStream = new FileOutputStream(file);
                                Log.i("camera", "->[file]" + file.getPath());
                                bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                                outStream.close();

                                ArrayList<Uri> uris = new ArrayList<Uri>();
                                uris.add(galleryAddPic(file.getAbsolutePath()));
                                new Note.Builder().setImageUris(uris).setCreatedTime(System.currentTimeMillis()).create().commit(DatabaseHelper.getDatabaseHelper(QuickSnapActivity.this));
                                Toast.makeText(QuickSnapActivity.this, "图片已保存", Toast.LENGTH_SHORT).show();
                                QuickSnapActivity.this.finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        QuickSnapActivity.this.finish();
                    }
                });
            } else {
                mCamera.autoFocus(this);
                Log.d("Process report !", "AutoFocus failed!Try again!");
            }

        }
    }

    private Uri galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        return contentUri;
    }
}
