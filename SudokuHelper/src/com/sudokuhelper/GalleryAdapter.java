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

	// 定义Context
	private Context		mContext;
	// 定义整型数组 即图片源
	private Integer[]	mImageIds	= 
	{ 
			R.drawable.sudoku_template1, 
			R.drawable.sudoku_template2,
	};
	public GalleryAdapter(Context c)
	{
		mContext = c;
	}
	// 获取图片的个数
	public int getCount()
	{
		return mImageIds.length;
	}
	// 获取图片在库中的位置
	public Object getItem(int position)
	{
		return position;
	}
	// 获取图片ID
	public long getItemId(int position)
	{
		return position;
	}
	// 获取图片对应的position
	public Integer getcheckedImageIDPosition(int position) {
		return mImageIds[position];
	}
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ImageView imageView;
		if (convertView == null)
		{
			// 给ImageView设置资源
			imageView = new ImageView(mContext);
			// 设置布局 图片200 * 200 显示
			imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
			// 设置显示比例类型
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
