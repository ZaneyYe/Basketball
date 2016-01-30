package com.crawler.pojo;

import com.crawler.service.ApiService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yzy mia5121@163.com on 2016/1/27.
 * 爬虫实体
 */
@Component
public class Crawler {

	@Autowired
	private ApiService apiService;

	private static final Logger LOGGER = LoggerFactory.getLogger(Crawler.class);

	private static final String START_URL = "http://www.755qq.com/html/part/9_{page}.html";

	public void begin(){
		try {
			//获取第一页信息
			String html = this.apiService.doGet("http://www.755qq.com/html/part/9.html");
			//获取总页数
			Document document = Jsoup.parse(html);
			String pagestr = document.select(".pages strong").text();
			String[] strs = pagestr.split("/");
			Integer pages = Integer.parseInt(strs[1]); //得到总页码
//			System.out.println(pages);
			//第一页
//			List<String> urlList = new ArrayList<>();
//			Elements elements = document.select(".textList li a");
//			for(int i = 2;i < elements.size();i++){
//				String href = elements.get(i).attr("href");
//				String url = "http://www.711gg.com"+href;
//				urlList.add(url);
//			}
//			System.out.println(urlList.toString());
			//从第二页开始
			List<String> imgURLList = new ArrayList<>();
			for(int i = 2;i <=  pages/10; i++){
				String pageUrl  = StringUtils.replace(START_URL,"{page}",i+"");
				String innerHtml = this.apiService.doGet(pageUrl);
				Document document2 = Jsoup.parse(innerHtml);
				List<String> urlList2 = new ArrayList<>();
				Elements elements2 = document2.select(".textList li a");
				for(int j = 2;j < elements2.size();j++){
					String href = elements2.get(j).attr("href");
					String url = "http://www.755qq.com"+href;
					urlList2.add(url);
				}
				//遍历请求图片网页
				for(String url : urlList2){
					String picHtml = this.apiService.doGet(url);
					Document picDoc = Jsoup.parse(picHtml);
					Elements imgEles = picDoc.select(".picContent img");
					for(Element e : imgEles){
						String src = e.attr("src");
						imgURLList.add(src);
					}
				}
			}
			for(String imgURL : imgURLList){
				downImage(imgURL);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载图片
	 * @param imageUrl 下载路径
	 * @throws Exception
	 */
	public void downImage(String imageUrl){
		try {
			URL url = new URL(imageUrl);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			InputStream is = conn.getInputStream();
			int num = new Random().nextInt();
			File imageFile = new File("F:\\picture\\hu\\img_"+num+".jpg");
			if(!imageFile.exists()){
				imageFile.createNewFile();
				OutputStream out = new FileOutputStream(imageFile);
				byte[] buf = new byte[1024];
				int len = -1;
				while( (len = is.read(buf)) > -1 ){
					out.write(buf, 0, len);
				}
				out.close();
			} else {
				System.out.println(Thread.currentThread() + "已经存在img");
			}
			is.close();
		} catch (Exception e) {
			System.err.println("########下载失败###########################################");
			e.printStackTrace();
		}

	}
}
