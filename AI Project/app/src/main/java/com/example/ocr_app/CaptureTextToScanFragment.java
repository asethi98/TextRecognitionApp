package com.example.ocr_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class CaptureTextToScanFragment extends Fragment {
	private TextView detectedTextTv;
	private CameraSource mCameraSource;
	private static final int requestPermissionID = 101;
	private SurfaceView mCameraView;
	private String detectedText;
	private Button captureBtn;


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_capture_to_scan, container,false);
		return root;
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		detectedTextTv = (TextView) view.findViewById(R.id.detectedTextTv);
		mCameraView = (SurfaceView) view.findViewById(R.id.surfaceView);
		captureBtn = (Button) view.findViewById(R.id.captureBtn);
		captureBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ScanTextFragment scanTextFragment = new ScanTextFragment();
				Bundle bundle = new Bundle();
				FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
				// Pass the string containing the detectedText to the TranslateTextFragment
				bundle.putString("detectedString",detectedText);
				scanTextFragment.setArguments(bundle);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.replace(R.id.fragment_container, scanTextFragment);
				fragmentTransaction.commit();
			}
		});
		startCameraSource();

	}

	private void startCameraSource() {
		//Create the TextRecognizer
		final TextRecognizer textRecognizer = new TextRecognizer.Builder(getContext()).build();

		if (!textRecognizer.isOperational()) {
			Log.w("CaptureTextToScanF", "Text detector dependencies have not yet been loaded");
		} else {

			// Initialize camerasource to use high resolution and set autofocus to on.
			mCameraSource = new CameraSource.Builder(getContext(), textRecognizer)
					.setFacing(CameraSource.CAMERA_FACING_BACK)
					.setRequestedPreviewSize(1280, 1024)
					.setAutoFocusEnabled(true)
					.setRequestedFps(2.0f)
					.build();

			/**
			 * Add call back to SurfaceView and check if camera permission is granted.
			 * If permission is granted we can start our cameraSource and pass it to surfaceView
			 */
			mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
				@Override
				public void surfaceCreated(SurfaceHolder holder) {
					try {

						if (ActivityCompat.checkSelfPermission(getContext(),
								Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
							requestPermissions(new String[]{Manifest.permission.CAMERA},
									requestPermissionID);
							return;
						}
						mCameraSource.start(mCameraView.getHolder());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				}

				/**
				 * Release resources for cameraSource
				 */
				@Override
				public void surfaceDestroyed(SurfaceHolder holder) {
					mCameraSource.stop();
				}
			});

			// Set the TextRecognizer's Processor
			textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
				@Override
				public void release() {
				}

				/**
				 * Detect all the text from camera using TextBlock and the values into a stringBuilder
				 * which will then be set to the textView.
				 * */
				@Override
				public void receiveDetections(Detector.Detections<TextBlock> detections) {
					final SparseArray<TextBlock> items = detections.getDetectedItems();
					if (items.size() != 0 ){

						detectedTextTv.post(new Runnable() {
							@Override
							public void run() {
								StringBuilder stringBuilder = new StringBuilder();
								for(int i=0;i<items.size();i++){
									TextBlock item = items.valueAt(i);
									stringBuilder.append(item.getValue());
									stringBuilder.append("\n");
								}
								detectedText = stringBuilder.toString();
								detectedTextTv.setText("Text Detected!");
							}
						});
					}else {
						detectedTextTv.setText("");
					}
				}
			});

		}
	}
}