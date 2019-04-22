package com.kz.iot;

import java.io.File;
import java.net.URL;

public class FileUtils {
	public static String getCurrentPath() {
		URL url = FileUtils.class.getProtectionDomain().getCodeSource()
				.getLocation();

		String filePath = null;
		try {
			filePath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (filePath.endsWith(".jar")) {
			filePath = filePath.substring(0, filePath.lastIndexOf("/"));
		}

		File file = new File(filePath);
		filePath = file.getAbsolutePath() + File.separator;

		return filePath;
	}
}
