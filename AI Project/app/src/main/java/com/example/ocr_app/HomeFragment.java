package com.example.ocr_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

public class HomeFragment extends Fragment {
	private RelativeLayout scanAndTranslateLayout;
	private RelativeLayout scanToPDFLayout;
	private RelativeLayout selectFromAlbumLayout;
	private RelativeLayout translationsLayout;
	private Button captureTextToScanBtn;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_home, container,false);
		return root;
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		scanAndTranslateLayout = (RelativeLayout) view.findViewById(R.id.scanAndTranslateLayout);
		scanAndTranslateLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CaptureTextToTranslateFragment nextFrag= new CaptureTextToTranslateFragment();
				getActivity().getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_container, nextFrag)
						.addToBackStack(null)
						.commit();
			}
		});

		scanToPDFLayout = (RelativeLayout) view.findViewById(R.id.scanToPDFLayout);
		scanToPDFLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CaptureTextToScanFragment nextFrag= new CaptureTextToScanFragment();
				getActivity().getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_container, nextFrag)
						.addToBackStack(null)
						.commit();
			}
		});

		translationsLayout = (RelativeLayout) view.findViewById(R.id.translationsLayout);
		translationsLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SavedTranslationsFragment nextFrag= new SavedTranslationsFragment();
				getActivity().getSupportFragmentManager().beginTransaction()
						.replace(R.id.fragment_container, nextFrag)
						.addToBackStack(null)
						.commit();
			}
		});
	}

}
