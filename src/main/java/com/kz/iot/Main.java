package com.kz.iot;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Main {
	
	/**
     * 读入TXT文件
     */
    public String readFile(String absPath) {
    	StringBuilder content = new StringBuilder();
        String pathname = absPath; // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {
            String line;
            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return content.toString();
    }

    public String sign(String paramsStr) {
    	Gson gsonApp = new GsonBuilder().enableComplexMapKeySerialization().create();
		Type appType = new TypeToken<Map<String, String>>() {}.getType();
		Map<String, String> paramsMap = gsonApp.fromJson(paramsStr, appType);
		String secret = "2097b85f76d6a28e9fb2e8674b361466";
		String ret = ApiUtils.sign(paramsMap, secret);
		return ret;
    }
    
    public String decrypt(String encStr) {
    	String key = "2097b85f76d6a28e9fb2e867";
    	String ret = DesUtils.decrypt(encStr, key);
    	return ret;
    }

	public static void main(String[] args) {
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		options.addOption("h", false, "Lists short help");
		options.addOption("S", false, "Sign");
		options.addOption("D", false, "Encryption and Decryption");
		options.addOption( "f", "file", true, "The file that contains parameters to sign." );
		
		HelpFormatter hf = new HelpFormatter();
        hf.setWidth(110);

		try {
			CommandLine line = parser.parse(options, args);
			if(line.hasOption("h")) { 
				// 这里显示简短的帮助信息
				hf.printHelp("java -jar sign.jar [D|S] -f <arg>", options);
			}
			
			if(line.hasOption("S") && line.hasOption("f")) {
				String value = line.getOptionValue("file");
				Main mapp = new Main();
				String paramsStr = mapp.readFile(value);
				System.out.println(paramsStr);
				String ret = mapp.sign(paramsStr);
				System.out.println(ret);
			}
			else if(line.hasOption("D") && line.hasOption("f")) {
				String value = line.getOptionValue("file");
				Main mapp = new Main();
				String encryptedStr = mapp.readFile(value);
				String ret = mapp.decrypt(encryptedStr);
				System.out.println(ret);
			}
			else {
				System.out.println("Use -h to see the help.");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}