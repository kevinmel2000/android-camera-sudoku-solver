/**
 * @author xiefei
 * appName: SudokuHelper
 * version: 1
 * function: This application is to used to help player to solve sudoku puzzle ,which can be input in two methods,
 * one is hand input, others input method is get sudoku from picture.Of course picture can be got by camera.
 */

package com.sudokuhelper;
import static com.sudokuhelper.util.CommonUtilities.streamCopy;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.ml.CvKNearest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private Button camInputButton;
	private Button handInputButton;
	private Button puzzleLibraryButton;
	private Button aboutButton;
	private Button galleryInputButton;
	private static final int DIALOG_ABOUT = 0;
	public static CvKNearest model;
    private static final String TAG = "MainActivity";
    private ProgressDialog progressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("程序正在努力的加载中...");
		progressDialog.setTitle("请稍候...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
        TrainDataThread mythread = new TrainDataThread();
        mythread.start();//开始启动训练数据线程
        //注册galleryInputButton按钮点击监听器
        galleryInputButton = (Button)findViewById(R.id.galleryInput);
        galleryInputButton.setOnClickListener(
        		new OnClickListener() {
					public void onClick(View v) {
						//galleryInputButton.setBackgroundResource(R.drawable.galleryinput_hover);
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, GalleryInputActivity.class);
						MainActivity.this.startActivity(intent);
					}
				});
        //注册puzzle按钮点击监听器
        puzzleLibraryButton = (Button)findViewById(R.id.puzzle_library);
        puzzleLibraryButton.setOnClickListener(
        		new OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(MainActivity.this, SudokuListActivity.class);
						MainActivity.this.startActivity(intent);
					}
				});
        //注册camInputButton按钮监听器
        camInputButton = (Button)findViewById(R.id.camInput);
        camInputButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, CamInputActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
        //注册handInputButton按钮监听器
        handInputButton = (Button)findViewById(R.id.handInput);
        handInputButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, HandInputActivity.class);
				
				MainActivity.this.startActivity(intent);
			}
		});
        //注册aboutButton按钮监听器
        aboutButton = (Button)findViewById(R.id.about);
        aboutButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_ABOUT);
			}
		});
    }
    /**
     * 主程序菜单响应
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    */
	@Override
	protected Dialog onCreateDialog(int id) {
		LayoutInflater factory = LayoutInflater.from(this);
		switch(id) {
			case DIALOG_ABOUT :
				final View aboutView = factory.inflate(R.layout.about, null);
	            return new AlertDialog.Builder(this)
	                .setIcon(R.drawable.ic_launcher)
	                .setTitle(R.string.app_name)
	                .setView(aboutView)
	                .setPositiveButton("OK", null)
	                .create();
		}
		return super.onCreateDialog(id);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	/**
	 * 响应退出键(BACK)，提示确认退出对话框
	 * 
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setMessage("确认退出吗？");
			builder.setTitle("提示");
			builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int which) {
					dialog.dismiss();
					MainActivity.this.finish();
				}
			});
			builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int which) {
					dialog.dismiss();
				}
				
			});
			builder.create().show();
			return false;
		}
		return false;
	}
	/**
	 * 加载训练数据和响应数据，按double类型解析，返回训练需要的Mat
	 * @param fileName 文件路径(如/mnt/sdcard/sudokuhelper/generalsamples.data)
	 * @return Mat 数据矩阵
	 */
  	public Mat loadData(String fileName) {
  		int row = 0;
  		int col = 0;
  		BufferedReader in;
  		try {
  			in = new BufferedReader(new FileReader(fileName));
  			ArrayList<String> listStr = new ArrayList<String>();
  			String s;
  			try {
  				while(( s = in.readLine()) != null) {
  					listStr.add(s);
  				}
  				in.close();
  			} catch (IOException e) {
  				e.printStackTrace();
  			}
  			
  			row = listStr.size();
  			col = listStr.get(0).split(" ").length;
  			Mat m = new Mat(row, col,CvType.CV_32F);
  			if(listStr != null) {
  				for(int i = 0; i < listStr.size(); i++) {
  					String []str = listStr.get(i).split(" ");
  					
  					for(int j = 0; j < str.length ; j++) {
  						double val = Double.parseDouble(str[j]);
  						m.put(i, j, val);
  					}
  				}
  				return m;
  			} 
  		} catch (FileNotFoundException e) {
  			e.printStackTrace();
  		}
  		return null;
  	}
  	/**
  	 * 训练数据
  	 * 初始化Opencv jni本地库,拷贝训练数据至SdCard中
  	 */
    public void trainData() {
    	//初始化opencv本地库
    	if(!OpenCVLoader.initDebug()) {
			Log.e(TAG, "initDebugError!");
		}
    	//将训练数据拷贝至SdCard中
        model = new CvKNearest();
		String recPath = "/mnt/sdcard/sudokuHelper/";
		String samplesName = "generalsamples.data";
		String responseName = "generalresponses.data";
		File recDataDir = new File(recPath, "train_data");
		if (!recDataDir.exists()) {//判断文件夹train_data是否存在，当存在默认认为数据已经存在该文件夹下不执行拷贝
			recDataDir.mkdirs();
			// copy assets/generalsamples.data to /mnt/sdcard/sudokuHelper/data/
			try {
				InputStream is = getAssets().open(samplesName);
				File samplesDataFile = new File(recDataDir, samplesName);
				try {
					OutputStream os = new FileOutputStream(samplesDataFile);
					streamCopy(is, os);
					os.close();
					Log.d(TAG, "copying file generalsamples.data success!");
				} catch (IOException e) {
					e.printStackTrace();
				}
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// copy assets/generalresponses.data to /mnt/sdcard/sudokuHelper/data/
			try {
				InputStream is = getAssets().open(responseName);
				File responsesDataFile = new File(recDataDir, responseName);
				try {
					OutputStream os = new FileOutputStream(responsesDataFile);
					streamCopy(is, os);
					os.close();
					Log.d(TAG, "copying file generalresponses.data success!");
				} catch (IOException e) {
					e.printStackTrace();
				}
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Mat samples = loadData(recPath + "train_data/" + samplesName);
		Mat response = loadData(recPath + "train_data/" + responseName);
		model.train(samples, response);//训练数据
    }
    /**
     * 复制模板数据
     * 拷贝模板文件至SdCard中
     */
    public void initTemplate() {
    	String templatePath = "/mnt/sdcard/sudokuHelper/";
    	File templateDataDir = new File(templatePath, "sudoku_template");
    	String []templateArr = {"sudoku_template1.png","sudoku_template2.png"};
    	Integer []templateId = {R.drawable.sudoku_template1, R.drawable.sudoku_template2};
    	if (!templateDataDir.exists()) {
    		templateDataDir.mkdirs();
			// copy assets/generalsamples.data to /mnt/sdcard/sudokuHelper/data/
    		for(int i = 0; i < 2; i ++) {
    			try {
					InputStream is = getResources().openRawResource(templateId[i]);
					File templateDataFile = new File(templateDataDir, templateArr[i]);
					try {
						OutputStream os = new FileOutputStream(templateDataFile);
						streamCopy(is, os);
						os.close();
						Log.d(TAG, "copying file " + templateArr[i]+ " success!");
					} catch (IOException e) {
						e.printStackTrace();
					}
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}	
    	}
    }
    /**
     * 接受训练数据线程返回的消息，并决定将响应线程(showButton/finishActivity)
     * post到线程队列
     */
    public Handler myHandler = new Handler();
    /**
     *  显示按钮线程
     *  默认主程序上面的5个按钮是隐藏,加载完训练数据集和拷贝完模板后才显示
     */
    Runnable showButton = new Runnable() {
        public void run() {
        	try {
        		camInputButton.setVisibility(View.VISIBLE);
            	handInputButton.setVisibility(View.VISIBLE);
            	puzzleLibraryButton.setVisibility(View.VISIBLE);
            	aboutButton.setVisibility(View.VISIBLE);
            	galleryInputButton.setVisibility(View.VISIBLE);
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
        	
        }
    };
    /**
     * 结束MainActivity线程
     * 当存储不可访问时，弹出提示信息框，结束程序
     */
    Runnable finishActivity = new Runnable() {
    	public void run() {
    		AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setMessage("读取存储出现问题,请检查是否具有存储卡或者存储卡是否插好！");
			builder.setTitle("提示");
			builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int which) {
					dialog.dismiss();
					MainActivity.this.finish();
				}
			});
			builder.create().show();
    	}
    };
    /**
     * 训练数据线程
     * 当存储卡不可访问，弹出提示信息退出
     * 否则训练数据，初始化模板（拷贝），结束进度条对话框，更新UI
     */
  	class TrainDataThread extends Thread {
  		public void run() {
  			Looper.prepare();
  			if(existSDcard()) {
  				trainData();
  	  			initTemplate();
  	  			progressDialog.dismiss();
  	  			myHandler.post(showButton);
  			} else {
  				myHandler.post(finishActivity);
  			}
  			Looper.loop();
  		}
      }
    /**
     * 判断存储卡是否可以访问
     * @return true   存储卡可以访问
     * 		   false  存储卡不可访问
     */
    public static boolean existSDcard()
    {
        if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState()))
            return true;
        else
            return false;
    }
}
