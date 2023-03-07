package com.example.demo.model;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

//Filemodel
public class FileModel {

	public String name;
	public byte[] content;
	public String format;
	public String created;

	public FileModel() {

	}

	public FileModel(String name, byte[] bs, String format) {
		this.name = name;
		this.content = bs;
		this.format = format;
		this.created = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm:ss", Locale.forLanguageTag("nl-NL"))).toString();
	}

	//Should handle multiple formats but this is fine for now.
	//Example : application/pdf = split('/')[1]
	//Doesnt work for other formats like Docx, need better solution.
	public String getRawFormat() {
		return "pdf";
	}

}
