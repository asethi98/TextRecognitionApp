package com.example.ocr_app;

public class SavedTranslations {
	private String translationDate;
	private String detectedText;
	private String translatedText;

	public SavedTranslations(String translationDate, String detectedText, String translatedText) {
		this.translationDate = translationDate;
		this.detectedText = detectedText;
		this.translatedText = translatedText;
	}

	public String getTranslationDate(){
		return translationDate;
	}

	public String getDetectedText() {
		return detectedText;
	}

	public String getTranslatedText() {
		return translatedText;
	}

	public void setTranslationDate(String translationDate) {
		this.translationDate = translationDate;
	}

	public void setDetectedText(String detectedText) {
		this.detectedText = detectedText;
	}

	public void setTranslatedText(String translatedText) {
		this.translatedText = translatedText;
	}

}
