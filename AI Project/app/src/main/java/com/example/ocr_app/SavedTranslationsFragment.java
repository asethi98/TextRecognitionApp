package com.example.ocr_app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class SavedTranslationsFragment extends Fragment {
	private ListView savedTranslationsListView;
	private SavedTranslationsListAdapter savedTranslationsListAdapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_saved_translations, container, false);
		return root;
	}

	public void onViewCreated(View view, Bundle savedInstanceState) {
		savedTranslationsListView = (ListView) view.findViewById(R.id.savedTranslations_list);
		savedTranslationsListAdapter = new SavedTranslationsListAdapter(getContext(), TranslationUtils.savedTranslationsList);
		savedTranslationsListAdapter.notifyDataSetChanged();
		savedTranslationsListView.setAdapter(savedTranslationsListAdapter);
	}
}