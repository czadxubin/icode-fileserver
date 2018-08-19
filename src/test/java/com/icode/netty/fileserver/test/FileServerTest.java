package com.icode.netty.fileserver.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;

public class FileServerTest {
	public static void testUploadFile() throws Exception {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost("http://192.168.1.102:8080/upload/jdk-7u40-windows-x64.exe");
		String pathname = "D:\\2017工作\\SoftPackage\\jdk\\jdk-7u40-windows-x64.exe";
		File file = new File(pathname);
		assert file.exists();
		HttpEntity entity = new FileEntity(file);
		httpPost.setEntity(entity);
		CloseableHttpResponse response2 = httpclient.execute(httpPost);
		try {
		    System.out.println(response2.getStatusLine());
		    HttpEntity entity2 = response2.getEntity();
		    // do something useful with the response body
		    // and ensure it is fully consumed
		    InputStream content = entity2.getContent();
		    Header contentEncoding = entity.getContentEncoding();
		    String encoding = "utf-8";
		    if(contentEncoding!=null&&contentEncoding.getValue()!=null) {
		    	encoding = contentEncoding.getValue();
		    }
			BufferedReader br = new BufferedReader(new InputStreamReader(content, encoding));
			String oneLine = null;
			while((oneLine = br.readLine())!=null) {
				System.out.println(oneLine);
			}
		    System.out.println("-------------------------------------------------");
		} finally {
		    response2.close();
		}
	}
	@Test
	public void testAvgUpload() throws Exception {
		Long startTime = System.currentTimeMillis();
		int times = 10;
		for(int i=0;i<times;i++) {
			Long start = System.currentTimeMillis();
			testUploadFile();
			Long end = System.currentTimeMillis();
			System.out.println("第"+(i+1)+"次上传耗时："+(end-start)+"ms");
		}
		Long endTime = System.currentTimeMillis();
		System.out.println("平均时长："+(endTime-startTime)/times+"ms");
	}

	@Test
	public void testConcurrentUpload() throws IOException {
		ExecutorService executor = Executors.newFixedThreadPool(10);
		int times = 1;
		String dirPath = "D:\\2017工作\\SoftPackage\\jdk\\";
		String fileName = "jdk-7u40-windows-x64.exe";
		for(int i=0;i<times;i++) {
//			String newFileName = "jdk-7u40-windows-x64_"+i+".exe";
			String newFileName = "jdk-7u40-windows-x64.exe";
			Future<Long> submit = executor.submit(new ConcurrentUpload(dirPath, fileName,newFileName));
		}
		System.in.read();
	}
	
	static class ConcurrentUpload implements Callable<Long>{
		public String dirPath;
		public String fileName;
		public String newFileName;
		ConcurrentUpload(String dirPath,String fileName,String newFileName){
			this.dirPath = dirPath;
			this.fileName = fileName;
			this.newFileName = newFileName;
		}
		@Override
		public Long call() throws Exception {
			Long startTime = System.currentTimeMillis();
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost("http://192.168.1.102:8080/upload/"+newFileName);
			File file = new File(dirPath,fileName);
			assert file.exists();
			HttpEntity entity = new FileEntity(file);
			httpPost.setEntity(entity);
			CloseableHttpResponse response2 = null;
			try {
				response2 = httpclient.execute(httpPost);
			    System.out.println(response2.getStatusLine());
			    HttpEntity entity2 = response2.getEntity();
			    // do something useful with the response body
			    // and ensure it is fully consumed
			    InputStream content = entity2.getContent();
			    Header contentEncoding = entity.getContentEncoding();
			    String encoding = "utf-8";
			    if(contentEncoding!=null&&contentEncoding.getValue()!=null) {
			    	encoding = contentEncoding.getValue();
			    }
				BufferedReader br = new BufferedReader(new InputStreamReader(content, encoding));
				String oneLine = null;
				while((oneLine = br.readLine())!=null) {
					System.out.println(oneLine);
				}
			    System.out.println("-------------------------------------------------");
			}catch (Exception e) {
				e.printStackTrace();
			} finally {
			    try {
					response2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Long endTime = System.currentTimeMillis();
			System.out.println("本次耗时："+(endTime-startTime)+"ms");
			return (endTime-startTime);
		}
		
	}
}
