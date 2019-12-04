package com.example.ocr_app;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SavedTranslationsListAdapter extends ArrayAdapter<SavedTranslations> {

	private Context mContext;
	private List<SavedTranslations> savedTranslationsList = new ArrayList<>();

	public SavedTranslationsListAdapter(@NonNull Context context, @LayoutRes ArrayList<SavedTranslations> list) {
		super(context, 0, list);
		mContext = context;
		savedTranslationsList = list;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		View listItem = convertView;
		if(listItem == null)
			listItem = LayoutInflater.from(mContext).inflate(R.layout.translationlist_item,parent,false);

		SavedTranslations currentTranslation = savedTranslationsList.get(position);

		TextView translationDate = (TextView) listItem.findViewById(R.id.translationDateTv);
		translationDate.setText(currentTranslation.getTranslationDate());

		TextView detectedText = (TextView) listItem.findViewById(R.id.detectedTextTv);
		detectedText.setText(currentTranslation.getDetectedText());

		TextView translatedText = (TextView) listItem.findViewById(R.id.translatedTextTv);
		translatedText.setText(currentTranslation.getTranslatedText());

		return listItem;
	}

}
