package com.example.blackurl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ContentHandler;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		ApkController.upgradeRootPermission(getPackageCodePath());

		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		int heapsize = manager.getMemoryClass();
		List<RunningAppProcessInfo> runlist =  manager.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : runlist) {
			Log.d("runprocess", runningAppProcessInfo.processName);
		}
		Log.d("TAG", String.valueOf(heapsize));	
		
		// 网络状态用的ConnectivityManager
		Context context = MainActivity.this;
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo =  connectivityManager.getActiveNetworkInfo();
		if(networkInfo == null){
			Log.d("network", "没有网络连接");
		}
		NetworkInfo wifInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo phoneInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifInfo.isConnected()) {
			Log.d("network", "wifi网络");
			Log.d("wifi", wifInfo.toString());
		}
		if (phoneInfo.isConnected()) {
			Log.d("network", "手机网络");	
		}else {
			Log.d("phoneinfo", phoneInfo.toString());
		}
		
		
		Button blackbutton = (Button) findViewById(R.id.blackurl);
		Button writebutton = (Button) findViewById(R.id.writeurl);
		Button installwrite = (Button) findViewById(R.id.installwrite);
		Button installwritenomal = (Button) findViewById(R.id.installwritenomal);
		Button installblack = (Button) findViewById(R.id.installblack);
		Button installblacknamal = (Button) findViewById(R.id.installblacknomal);
		Button sendsmg = (Button) findViewById(R.id.sendsmg);
		Button sendhttp = (Button) findViewById(R.id.sendhttp);
		final EditText edittext = (EditText) findViewById(R.id.showhttpRes);

		
		//黑白网站库		
		final List<String> blackurlList = new ArrayList<String>();
		final List<String> writeurlList = new ArrayList<String>();
		
		writeurlList.add("http://www.baidu.com");
		writeurlList.add("http://www.360.cn");
		writeurlList.add("http://www.sina.com.cn");
		writeurlList.add("http://www.youku.com");
		
		blackurlList.add("http://600116.dofox.gq/");
		blackurlList.add("http://dbpsj.com/edu/index.html?http://baidu.com/=600116");
	
		//模拟发送http get 请求
		sendhttp.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				List<String> urllist = new ArrayList<String>();
				urllist.add("http://www.baidu.com");
				urllist.add("http://www.so.com");
				urllist.add("http://www.yangyanxing.com");
				urllist.add("http://www.sohu.com");
				urllist.add("http://down.360safe.com/123.cab");
				
				for (final String url : urllist) {
				new Thread(new Runnable() {
					public void run() {
						try {
							final HttpURLConnection urlconn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(url,null,null);
							System.out.println(urlconn.getResponseCode());
//							InputStream in = urlconn.getInputStream();
//							final String rst = HttpRequestUtil.read2String(in);
							v.post(new Runnable() {
								public void run() {
									try {
										edittext.append(url+":"+String.valueOf(urlconn.getResponseCode()));
										edittext.append("\r\n");										
									} catch (IOException e) {										
										e.printStackTrace();
									}								
								}
							});
								
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}).start();
			}
				
			}
		});
		
		
		//点击模拟发送短信
		sendsmg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String message = "cxye";
				SmsManager sms = SmsManager.getDefault();
				sms.sendTextMessage("10086", null, message, null, null);				
			}
		});
		
		
		//点击安装黑应用
		installblack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				installapksilent(v,"apk/black.apk");		
			}
		});
		//普通模式安装黑应用
		installblacknamal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				installapkNormal(v,"apk/black.apk");				
			}
		});
		
		
		//点击安装正常应用		
		installwrite.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				installapksilent(v,"apk/write.apk");				
			}
		});
		
		
		//普通模式安装白应用
		installwritenomal.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				installapkNormal(v,"apk/write.apk");
				
			}
		});
		
	
		//点击黑网址按钮
		blackbutton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				int i = (int) (Math.random()*blackurlList.size());
				String needurl = blackurlList.get(i);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(needurl));
				intent.putExtra("url", needurl);
				Toast.makeText(MainActivity.this, needurl, Toast.LENGTH_LONG).show();
				startActivity(intent);
			}
		});
		
		//点击白网址按钮
		writebutton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				int i = (int) (Math.random()*writeurlList.size());
				String needurl = writeurlList.get(i);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(needurl));
				Toast.makeText(MainActivity.this, needurl, Toast.LENGTH_LONG).show();
				intent.putExtra("url", needurl);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//将流拷贝到storage中
	private void writeStreamToFile(InputStream stream, File file) {
		try
		{
			//
			OutputStream output = null;
			try
			{
				output = new FileOutputStream(file);
			}
			catch (FileNotFoundException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try
			{
				try
				{
					final byte[] buffer = new byte[1024];
					int read;

					while ((read = stream.read(buffer)) != -1)
						output.write(buffer, 0, read);

					output.flush();
				}
				finally
				{
					output.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		finally
		{
			try
			{
				stream.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	private void installapkNormal(final View v, String apkpath) {
		AssetManager am = getAssets();
		try {
			InputStream stream = am.open(apkpath);
			File f = new File(apkpath);
			String filename = f.getName();
			Log.d("filename", filename);
			if (stream==null) {
				Log.v("error", "file not found");
				return;
			}else{
				String folder = Environment.getExternalStorageDirectory().getAbsolutePath();
				final String apkpathsdcard = folder+"/"+filename;
				Log.d("path", apkpathsdcard);
				File file = new File(apkpathsdcard);
				file.createNewFile();
				writeStreamToFile(stream,file);
				Intent intent = new Intent(Intent.ACTION_VIEW);
//				String installString = "file://" + file;
				intent.setDataAndType(Uri.fromFile(new File(apkpathsdcard)), "application/vnd.android.package-archive");
				startActivity(intent);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void installapksilent(final View v,String apkpath) {
		AssetManager am = getAssets();
		try {
			final InputStream stream = am.open(apkpath);
			File f = new File(apkpath);
			String filename = f.getName();
			Log.d("filename", filename);
			if (stream==null) {
				Log.v("error", "file not found");
				return;
			}else{
				String folder = Environment.getExternalStorageDirectory().getAbsolutePath();
				final String apkpathsdcard = folder+"/"+filename;
				Log.d("path", apkpathsdcard);
				final File file = new File(apkpathsdcard);
				file.createNewFile();
				
				new Thread(new Runnable() {
					public void run() {
						writeStreamToFile(stream,file);
						final boolean rst = ApkController.install(apkpathsdcard, getApplicationContext());
						v.post(new Runnable() {
							public void run() {
								if (rst) {
									Log.d("result", "安装成功");
								}else{
									Log.v("rstult", "安装失败");
								}
							}
						});
					}

				}).start();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
