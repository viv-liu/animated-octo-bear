package com.ui.module;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList; 
import java.util.Collections;
import java.util.List;
import java.text.DateFormat; 

import com.example.animated_octo_bear.R;
import com.file.process.*;

import android.os.Bundle; 
import android.app.ListActivity;
import android.content.Intent; 
import android.view.View;
import android.widget.ListView; 

public class Import_UI extends ListActivity {

	private File currentDir;
    private FileArrayAdapter adapter;
    private String ext;
    private String folderpath;
    private String verifyext;
    private String verifyext2;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        /*
         * Gets Intent's from title_screen which determines the 
         * folder path, and what extensions to verify.
         */
        
        Intent intent = getIntent();
        folderpath = intent.getStringExtra("folderpath");
        verifyext = intent.getStringExtra("verifyext");
        verifyext2 = intent.getStringExtra("verifyext2");
        
        currentDir = new File(folderpath);
        fill(currentDir); 
    }
    
    private void fill(File f)
    {
    	File[]dirs = f.listFiles(); 
		 this.setTitle("Current Dir: "+f.getName());
		 List<Item>dir = new ArrayList<Item>();
		 List<Item>fls = new ArrayList<Item>();
		 try{
			 for(File ff: dirs)
			 { 
				Date lastModDate = new Date(ff.lastModified()); 
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				ext = get_ext(ff);
				if(ff.isDirectory()){
					
					/*
					 * If the file is a directory
					 * Check how many items are within it.
					 * Add them and see the total # and
					 * add them to the directories data structure
					 */
					
					File[] fbuf = ff.listFiles(); 
					int buf = 0;
					if(fbuf != null){ 
						buf = fbuf.length;
					} 
					else buf = 0; 
					String num_item = String.valueOf(buf);
					if(buf == 0) num_item = num_item + " item";
					else num_item = num_item + " items";
					
					//String formated = lastModDate.toString();
					dir.add(new Item(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"directory_icon")); 
				}
				else if (ext.matches(verifyext)||ext.matches(verifyext2))
				{	
					fls.add(new Item(ff.getName(),ff.length() + " Byte", date_modify, ff.getAbsolutePath(),"file_icon"));
				}
				else
				{
					continue;
				}
			 }
		 }catch(Exception e)
		 {    
			 
		 }
		 Collections.sort(dir);
		 Collections.sort(fls);
		 dir.addAll(fls);
		 if(!f.getName().equalsIgnoreCase("sdcard"))
			 dir.add(0,new Item("..","Parent Directory","",f.getParent(),"directory_up"));
		 adapter = new FileArrayAdapter(Import_UI.this,R.layout.file_view,dir);
		 this.setListAdapter(adapter); 
    }
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Item o = adapter.getItem(position);
		if(o.getImage().equalsIgnoreCase("directory_icon")||o.getImage().equalsIgnoreCase("directory_up")){
				currentDir = new File(o.getPath());
				fill(currentDir);
		}
		else
		{
			onFileClick(o);
		}
	}
    private void onFileClick(Item o)
    {
    	//Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
    	Intent intent = new Intent(Import_UI.this, Import_Process.class);
        intent.putExtra("GetPath",currentDir.toString());
        intent.putExtra("GetFileName",o.getName());
        setResult(RESULT_OK, intent);
        startActivity(intent);
    }
    // Gets the file extension
    private String get_ext(File ff)
    {
    	String filename = ff.getName();
        String filenameArray[] = filename.split("\\.");
        String extension = filenameArray[filenameArray.length-1];
		return extension;
    }
 }
