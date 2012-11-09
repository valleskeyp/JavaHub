package com.valleskeyp.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.util.Log;

public class FileStuff {
	@SuppressWarnings("resource")
	public static boolean storeObjectFile(Context context, String filename, Object content, Boolean external) {
		try {
			File file;
			FileOutputStream fos;
			ObjectOutputStream oos;
			if (external) {
				file = new File(context.getExternalFilesDir(null), filename);
				fos = new FileOutputStream(file);
			} else {
				fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			}
			oos = new ObjectOutputStream(fos);
			oos.writeObject(content);
			oos.close();
			fos.close();
		} catch (IOException e) {
			Log.e("WRITE ERROR", filename);
		}
		return true;
	}
	
	@SuppressWarnings("resource")
	public static Object ReadObjectFile(Context context, String filename, Boolean external) {
		Object content = new Object();
		try {
			File file;
			FileInputStream fin;
			if (external) {
				file = new File(context.getExternalFilesDir(null), filename);
				fin = new FileInputStream(file);
			} else {
				file = new File(filename);
				fin = context.openFileInput(filename);
			}
			
			ObjectInputStream ois = new ObjectInputStream(fin);
			
			try {
				content = (Object) ois.readObject();
			} catch (ClassNotFoundException e) {
				Log.e("READ ERROR", "INVALID OBJECT FILE");
			} 
			ois.close();
			fin.close();
		} catch (FileNotFoundException e) {
			Log.e("READ ERROR", "FILE NOT FOUND");
			return null;
		} catch (IOException e) {
			Log.e("READ ERROR", "I/O ERROR");
		}
			return content;
	}
}
