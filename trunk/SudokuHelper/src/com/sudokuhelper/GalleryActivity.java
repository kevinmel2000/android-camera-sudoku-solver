/**
 * @author xiefei
 * appName: SudokuHelper
 * version: 1
 * function: This application is to used to help player to solve sudoku puzzle ,which can be input in two methods,
 * one is hand input, others input method is get sudoku from picture.Of course picture can be got by camera.
 */

package com.sudokuhelper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class GalleryActivity extends Activity {
	/** Called when the activity is first created. */
	private GalleryAdapter imageAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sudoku_gallery);

		//ȡ��GridView����
		GridView gridview = (GridView) findViewById(R.id.gridview);
		//���Ԫ�ظ�gridview
		imageAdapter = new GalleryAdapter(this);
		gridview.setAdapter(imageAdapter);

		// ����Gallery�ı���
		//gridview.setBackgroundResource(R.drawable.bg0);

		//�¼�����
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id)
			{
				Toast.makeText(GalleryActivity.this, "��ѡ����" + (position + 1) + " ��ͼƬ", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(GalleryActivity.this, GalleryInputActivity.class);
				Integer drawablePosition = imageAdapter.getcheckedImageIDPosition(position);
				Resources resource = getResources();
				Uri uri =  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
					    + resource.getResourcePackageName(drawablePosition) + "/"
					    + resource.getResourceTypeName(drawablePosition) + "/"
					    + resource.getResourceEntryName(drawablePosition));
				intent.setDataAndType(uri, "image/**");
				GalleryActivity.this.setResult(RESULT_OK, intent);
				GalleryActivity.this.finish();
			}
		});
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
    
	
}
