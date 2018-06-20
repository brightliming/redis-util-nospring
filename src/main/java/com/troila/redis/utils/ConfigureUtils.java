package com.troila.redis.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class ConfigureUtils {
	
	private static Properties configure = new Properties();

	public static String getConfig(String key) {
		return (String)configure.get(key);
	}
	
	public static Properties getConfigs() {
		return configure;
	}

	public void initSettings(File f) {
		InputStream in = null;
		try {
			in = new FileInputStream(f);
			configure.load(in);
		} catch (IOException e) {
			e.printStackTrace();

			if (in != null)
				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	public void init() {
		try {
			String workdir = System.getProperty("user.dir");
			File f = new File(workdir + "/conf");
			if (!f.exists()) {
				System.out.println("getClass().getResource");
				URI uri = getClass().getResource("/").toURI();
				f = new File(uri);
			} else {
				System.out.println(workdir + "/conf");
			}
			if ((f.exists()) && (f.isDirectory())) {
				File[] files = f.listFiles();
				if (files != null)
					for (int i = 0; i < files.length; i++)
						if(files[i].getName().indexOf("properties") != -1) {
							initSettings(files[i]);
						}				
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
