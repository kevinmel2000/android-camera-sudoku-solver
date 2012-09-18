/**
 * @author xiefei
 * appName: SudokuHelper
 * version: 1
 * function: This application is to used to help player to solve sudoku puzzle ,which can be input in two methods,
 * one is hand input, others input method is get sudoku from picture.Of course picture can be got by camera.
 */

package com.sudokuhelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.sudokuhelper.db.SudokuDatabase;
import com.sudokuhelper.game.CellCollection;
import com.sudokuhelper.game.SudokuGame;
import com.sudokuhelper.inputmethod.IMControlPanel;
import com.sudokuhelper.inputmethod.IMControlPanelStatePersister;
import com.sudokuhelper.inputmethod.IMNumpad;
import com.sudokuhelper.inputmethod.IMPopup;
import com.sudokuhelper.inputmethod.IMSingleNumber;
@SuppressWarnings("unused")
public class HandInputActivity extends Activity{
	private SudokuBoardView mSudokuBoard;
	private SudokuGame mSudokuGame;
	private String level;
	private SolveSudoku solveSudoku;
	private RelativeLayout rootLayout;
	private IMControlPanel mIMControlPanel;
	private IMControlPanelStatePersister mIMControlPanelStatePersister;
	private IMPopup mIMPopup;
	private IMSingleNumber mIMSingleNumber;
	private IMNumpad mIMNumpad;
	private SudokuDatabase mDatabase;
	private HintsQueue mHintsQueue;
	private boolean isFirst;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hand_input);
		isFirst = true;
		mSudokuBoard = (SudokuBoardView)findViewById(R.id.sudoku_board);
		rootLayout = (RelativeLayout)findViewById(R.id.root_layout);
		String data;
		String null_data = "000000000000000000000000000000000000000000000000000000000000000000000000000000000";
		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null) {
			String recognizedStr = bundle.getString("recognizedStr");
			data = recognizedStr;
			rootLayout.setBackgroundResource(R.drawable.sudokuplayer_bk);
		}else{
			data = null_data;
		}
		mSudokuGame = new SudokuGame();
		mSudokuGame.setId(0);
		mSudokuGame.setCells(CellCollection.deserialize(data));
		mSudokuGame.setState(0);
		mSudokuGame.setNote("");
		mSudokuGame.setTime(0);
		level = mSudokuGame.getCells().getDataFromCellCollection();
		for(int i = 0; i < level.length(); i++)
		{
			int row = i / 9 ;
		    int column = i % 9 ;
			mSudokuGame.getCells().getCell(row, column).setEditable(true);
		}
		mSudokuBoard.setGame(mSudokuGame);
		mHintsQueue = new HintsQueue(this);
		mHintsQueue.showOneTimeHint("welcome", R.string.welcome, R.string.first_run_hint);		
		mIMControlPanel = (IMControlPanel)findViewById(R.id.input_methods);
		mIMControlPanel.initialize(mSudokuBoard, mSudokuGame, mHintsQueue);
		//mIMControlPanel.activateFirstInputMethod();//激活第一种输入方法，默认为popup
		mIMControlPanel.activateInputMethod(1);//激活singlenum输入方法
		mIMControlPanelStatePersister = new IMControlPanelStatePersister(this);
        mIMPopup = mIMControlPanel.getInputMethod(IMControlPanel.INPUT_METHOD_POPUP);
        mIMSingleNumber = mIMControlPanel.getInputMethod(IMControlPanel.INPUT_METHOD_SINGLE_NUMBER);
        mIMNumpad = mIMControlPanel.getInputMethod(IMControlPanel.INPUT_METHOD_NUMPAD);
        mDatabase = new SudokuDatabase(getApplicationContext());
        solveSudoku = new SolveSudoku();
		//注册解题按钮监听器
		Button solveButton = (Button)findViewById(R.id.solve);
		solveButton.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						AlertDialog.Builder builder = new Builder(HandInputActivity.this);
						builder.setMessage("确认解题吗？");
						builder.setTitle("提示");
						builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								level = mSudokuBoard.getCells().getDataFromCellCollection();
								CellCollection cc = mSudokuBoard.getCells();
								System.out.println(level);
								if(!cc.isEmpty()) {
									if(cc.validate()) {
										if(!cc.isCompleted()) {
											String answer = solveSudoku.solve(level);
											Log.d("answer: ", answer);
											if(answer.equals(SolveSudoku.LOAD_ERROR)) {
												mHintsQueue.showHint(R.string.load_error, R.string.load_error_info);
											} else if(answer.equals(SolveSudoku.SOLVE__ERROR)) {
												mHintsQueue.showHint(R.string.solve_error, R.string.solve_error_info);
											} else if(answer.equals(SolveSudoku.COMMON_ERROR)){
												mHintsQueue.showHint(R.string.common_error, R.string.common_error_info);
											} else {
												mSudokuGame.setCells(CellCollection.deserialize(answer));
												for(int i = 0; i < level.length(); i++)
												{
													if(level.charAt(i) == '0') {
														int row = i / 9 ;
													    int column = i % 9 ;
														mSudokuGame.getCells().getCell(row, column).setEditable(true);
													}
												}
												mSudokuBoard.setGame(mSudokuGame);
												mHintsQueue.showHint(R.string.complete_sudoku, R.string.complete_sudoku_info1);
												//mSudokuBoard.setReadOnly(true);
												System.out.println(answer);
											}
										} else {
											mHintsQueue.showHint(R.string.complete_sudoku, R.string.complete_sudoku_info);
										}
										
									} else {
										mHintsQueue.showHint(R.string.wrong_sudoku, R.string.wrong_sudoku_info);
									}
								} else {
									mHintsQueue.showHint(R.string.empty_sudoku, R.string.empty_sudoku_info);
								}
							}
							
						});
						builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
							
						});
						builder.create().show();
					}
				});
		//注册保存按钮监听器
		Button saveButton = (Button)findViewById(R.id.save);
		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(isFirst) {
					AlertDialog.Builder builder = new Builder(HandInputActivity.this);
					builder.setMessage("确认保存吗？");
					builder.setTitle("提示");
					builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
							level = mSudokuBoard.getCells().getDataFromCellCollection();
							CellCollection cc = mSudokuBoard.getCells();
							//boolean validate = true;
							System.out.println(level);
							if(!cc.isEmpty()) {
								if(cc.validate()) {
									int count = 0;
									for(int i = 0; i < level.length(); i++) 
										if(level.charAt(i) != '0') count++;
									System.out.println(count);
									if(count < 17) {
										mHintsQueue.showHint(R.string.load_error, R.string.load_error_info);
									} else {
										if(mDatabase.insertSudoku(mSudokuGame) != -1) {
											mHintsQueue.showHint(R.string.success, R.string.save_success_info);
											isFirst = false;
										} else {
											mHintsQueue.showHint(R.string.error, R.string.save_error_info);
										}
									}
									
								} else {
									mHintsQueue.showHint(R.string.wrong_sudoku, R.string.wrong_sudoku_info);
								}
							} else {
								mHintsQueue.showHint(R.string.empty_sudoku, R.string.empty_sudoku_info);
							}
							
						}
					});
					builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
						}
						
					});
					builder.create().show();
				} else {
					mHintsQueue.showHint(R.string.over_save, R.string.over_save_info);
				}
				
			}
		});
		
	}	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mDatabase.close();
	}
}
