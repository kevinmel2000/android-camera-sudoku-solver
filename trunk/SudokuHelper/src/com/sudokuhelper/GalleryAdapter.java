/**
 * @author xiefei
 * appName: SudokuHelper
 * version: 1
 * function: This application is to used to help player to solve sudoku puzzle ,which can be input in two methods,
 * one is hand input, others input method is get sudoku from picture.Of course picture can be got by camera.
 */

package com.sudokuhelper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GalleryAdapter extends BaseAdapter{

	// ����Context
	private Context		mContext;
	// ������������ ��ͼƬԴ
	private Integer[]	mImageIds	= 
	{ 
			R.drawable.sudoku_template1, 
			R.drawable.sudoku_template2,
	};
	public GalleryAdapter(Context c)
	{
		mContext = c;
	}
	// ��ȡͼƬ�ĸ���
	public int getCount()
	{
		return mImageIds.length;
	}
	// ��ȡͼƬ�ڿ��е�λ��
	public Object getItem(int position)
	{
		return position;
	}
	// ��ȡͼƬID
	public long getItemId(int position)
	{
		return position;
	}
	// ��ȡͼƬ��Ӧ��position
	public Integer getcheckedImageIDPosition(int position) {
		return mImageIds[position];
	}
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ImageView imageView;
		if (convertView == null)
		{
			// ��ImageView������Դ
			imageView = new ImageView(mContext);
			// ���ò��� ͼƬ200 * 200 ��ʾ
			imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
			// ������ʾ��������
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		}
		else
		{
			imageView = (ImageView) convertView;
		}
		imageView.setImageResource(mImageIds[position]);
		return imageView;
	}
}
