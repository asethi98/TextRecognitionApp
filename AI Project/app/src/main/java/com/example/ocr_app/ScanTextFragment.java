package com.example.ocr_app;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class ScanTextFragment extends Fragment {

	private EditText _detectedTextContent;
	private ImageView _voiceDetectedTextBtn;
	private String _detectedText;
	private Button _exportToPDFBtn;
	private TextToSpeech _textToSpeech;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_scan_text, container, false);
		return root;
	}

	@RequiresApi(api = Build.VERSION_CODES.KITKAT)
	public void onViewCreated(View view, Bundle savedInstanceState) {
		_detectedTextContent = (EditText) view.findViewById(R.id.detectedTextContent);
		_voiceDetectedTextBtn = (ImageView) view.findViewById(R.id.voiceDetectedTextBtn);
		_exportToPDFBtn = (Button) view.findViewById(R.id.exportToPDFBtn);

		// Retrieve the detected text from the CaptureTextToScanFragment
		Bundle bundle = getArguments();
		if (bundle != null) {
			_detectedText = bundle.getString("detectedString");
			_detectedTextContent.setText(_detectedText);
		}

		// Request read and write permissions for create the .pdf file
		requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

		_textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				int ttsLanguage;
				if (status == TextToSpeech.SUCCESS) {
					ttsLanguage = _textToSpeech.setLanguage(Locale.US);

					if (ttsLanguage == TextToSpeech.LANG_MISSING_DATA
							|| ttsLanguage == TextToSpeech.LANG_NOT_SUPPORTED) {
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
		_voiceDetectedTextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int speechStatus;
				speechStatus = _textToSpeech.speak(_detectedTextContent.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
				if (speechStatus == TextToSpeech.ERROR) {
					Log.e("TTS", "Error converting text to speech");
				}
			}
		});

		_exportToPDFBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createAndViewPDF(_detectedTextContent.getText().toString());
			}
		});
	}

	// Method for creating, saving, and opening a .pdf file given a string
	public void createAndViewPDF(String text) {
		Document document = new Document();
		try {
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Dir";

			File directory = new File(path);
			if (!directory.exists())
				directory.mkdirs();

			File file = new File(directory, "detectedText.pdf");
			FileOutputStream fOut = new FileOutputStream(file);

			PdfWriter.getInstance(document, fOut);
			//open the document to write to it
			document.open();

			Paragraph paragraph1 = new Paragraph(text);
			Font paragraphFont= new Font(Font.FontFamily.HELVETICA);
			paragraph1.setAlignment(Paragraph.ALIGN_CENTER);
			paragraph1.setFont(paragraphFont);

			//add the paragraph to document
			document.add(paragraph1);

		} catch (DocumentException de) {
			Log.e("PDFCreator", "DocumentException:" + de);

		} catch (IOException e) {
			Log.e("PDFCreator", "IOException:" + e);

		} finally {
			document.close();
		}

		viewPdf("detectedText.pdf", "Dir");
	}

	// Method for opening a pdf file
	private void viewPdf(String file, String directory) {
		File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
		Uri uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", pdfFile);

		// Setting the intent for pdf reader
		Intent pdfIntent = new Intent(Intent.ACTION_VIEW);

		pdfIntent.setDataAndType(uri, "application/pdf");
		pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		try {
			startActivity(pdfIntent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getContext(), "Unable to read the PDF file", Toast.LENGTH_SHORT).show();
		}
	}
}
