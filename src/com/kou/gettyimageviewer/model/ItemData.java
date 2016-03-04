package com.kou.gettyimageviewer.model;

// Example: http://www.gettyimagesgallery.com/Images/Thumbnails/1343/134342.jpg

public class ItemData {
	private final String mainURL = "http://www.gettyimagesgallery.com";
	private String title;
	private String imageUrl;

	public ItemData(String title, String imageUrl) {

		this.title = title;
		this.imageUrl = imageUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return mainURL + imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}