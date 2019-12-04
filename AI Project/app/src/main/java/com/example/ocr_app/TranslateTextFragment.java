package com.example.ocr_app;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TranslateTextFragment extends Fragment implements AdapterView.OnItemSelectedListener {
	private TextView _detectedTextTV;
	private TextView _translatedTextTv;
	private ImageView _voiceDetectedTextButton;
	private ImageView _voiceTranslationButton;
	private Button _saveTranslationButton;
	private int _ttsLanguage;
	private String _targetLanguage;
	private String _translatedText;
	public String _detectedText;
	private TextToSpeech _textToSpeech;
	private Locale _ttsSelectedLanguage;
	Context _mContext;
	Translate _translate;
	private Spinner _spinner;
	private static final String[] languages = {"English", "German", "Hindi", "Spanish", "French"};

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_translate_text, container,false);
		return root;
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public void onViewCreated(View view, Bundle savedInstanceState) {
		_detectedTextTV = (TextView) view.findViewById(R.id.detectedTextStringTv);
		_translatedTextTv = (TextView) view.findViewById(R.id.translatedTextStringTv);
		_voiceDetectedTextButton = (ImageView) view.findViewById(R.id.voiceDetectedTextBtn);
		_voiceTranslationButton = (ImageView) view.findViewById(R.id.voiceTranslationBtn);
		_saveTranslationButton = (Button) view.findViewById(R.id.saveTranslationBtn);
		_mContext = getContext();
		_spinner = (Spinner) view.findViewById(R.id.spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(_mContext, android.R.layout.simple_spinner_item, languages);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_spinner.setAdapter(adapter);
		_spinner.setOnItemSelectedListener(this);

		DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		final String currentDate = sdf.format(date);
		Log.i("TranslateTextFragment", "Current Date: " + currentDate);

		// Retrieve the detected text from the CaptureTextToTranslateFragment
		Bundle bundle = getArguments();
		if(bundle != null) {
			_detectedText = bundle.getString("detectedString");

			if (checkInternetConnection()) {
				getTranslationService();
			}

			Detection detection = _translate.detect(_detectedText);
			Log.i("TranslateTextFragment","Language of Detected Text: " + detection.toString());

			_detectedTextTV.setText("Detected Language: " + detection.getLanguage().toUpperCase() + "\n" + _detectedText);
			_translatedTextTv.setText("");
		}

		_textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					_ttsLanguage = _textToSpeech.setLanguage(Locale.US);

					if (_ttsLanguage == TextToSpeech.LANG_MISSING_DATA
							|| _ttsLanguage == TextToSpeech.LANG_NOT_SUPPORTED) {
						Log.e("TTS", "This language is not supported!");
					} else {
						Log.i("TTS", "Language Supported.");
					}
					Log.i("TTS", "Success Initializing.");

				} else {
					Toast.makeText(getContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Voice the detected text using TTS when the button/speaker image is tapped
		_voiceDetectedTextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int speechStatus;
				_ttsLanguage = _textToSpeech.setLanguage(Locale.US);
				speechStatus = _textToSpeech.speak(_detectedText, TextToSpeech.QUEUE_FLUSH, null);
				if (speechStatus == TextToSpeech.ERROR) {
					Log.e("TTS", "Error converting text to speech");
				}
			}
		});

		// Voice the translated text using TTS when the button/speaker image is tapped
		_voiceTranslationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int speechStatus;
				_ttsLanguage = _textToSpeech.setLanguage(_ttsSelectedLanguage);
				speechStatus = _textToSpeech.speak(_translatedText, TextToSpeech.QUEUE_FLUSH, null);
				if (speechStatus == TextToSpeech.ERROR) {
					Log.e("TTS", "Error converting text to speech");
				}
			}
		});

		_saveTranslationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Save the date, detected text, and translation in the global list
				TranslationUtils.savedTranslationsList.add(new SavedTranslations(currentDate, _detectedText, _translatedText));

				// Navigate to the SavedTranslationsFragment
				SavedTranslationsFragment savedTranslationsFragment = new SavedTranslationsFragment();
				FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.replace(R.id.fragment_container, savedTranslationsFragment);
				fragmentTransaction.commit();
			}
		});
	}

	// Stop the TTS when done
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (_textToSpeech != null) {
			_textToSpeech.stop();
			_textToSpeech.shutdown();
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public void onLanguageSelected() {
		if (checkInternetConnection()) {
			getTranslationService();
			translateDetectedText();
		} else {
			// if there's no internet connection, display the corresponding error message
			_translatedTextTv.setText(getResources().getString(R.string.noInternet));
		}
	}

	// Checks if we are connected to a network
	public boolean checkInternetConnection() {
		ConnectivityManager connectivityManager = (ConnectivityManager) _mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public void getTranslationService() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		try (InputStream inputStream = getResources().openRawResource(R.raw.credentials)) {
			// Get the credentials from the credentials.json file
			final GoogleCredentials myCredentials = GoogleCredentials.fromStream(inputStream);

			// Set the credentials and get the translation service:
			TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
			_translate = translateOptions.getService();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void translateDetectedText() {
		Translation translation = _translate.translate(_detectedText,
				Translate.TranslateOption.targetLanguage(_targetLanguage),
				Translate.TranslateOption.model("base"));
		_translatedText = translation.getTranslatedText();
		_translatedTextTv.setText(_translatedText);

	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (position) {
			case 0:
				_targetLanguage = "en"; //English
				onLanguageSelected();
				_ttsSelectedLanguage = Locale.US;
				break;
			case 1:
				_targetLanguage = "de"; //German
				onLanguageSelected();
				_ttsSelectedLanguage = Locale.GERMAN;
				break;
			case 2:
				_targetLanguage = "hi"; //Hindi
				onLanguageSelected();
				_ttsSelectedLanguage = Locale.forLanguageTag("hin");
				break;
			case 3:
				_targetLanguage = "es"; //Spanish
				onLanguageSelected();
				_ttsSelectedLanguage = Locale.forLanguageTag("es");;
				break;
			case 4:
				_targetLanguage = "fr"; //French
				onLanguageSelected();
				_ttsSelectedLanguage = Locale.FRENCH;
				break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Do nothing
	}
}
