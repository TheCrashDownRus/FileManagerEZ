package com.example.crashdown.filemanagerez;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private RecyclerView recyclerView2;


    private File currentDir;
    private File currentDir1;
    private File[] files;
    private File[] files1;
    private List<FileObject> strings = new ArrayList<FileObject>();
    private List<FileObject> strings1 = new ArrayList<FileObject>();
    private boolean InMainDirectory = false;
    private boolean InMainDirectory2 = false;

    private boolean inSelectMode = false;
    private boolean inSelectMode1 = false;
    private List<String> selected = new ArrayList<String>();
    private List<String> selected1 = new ArrayList<String>();

    public static FileObject STORAGE_LOCATION;
    public static FileObject SDCARD_LOCATION;
    public static final String TURN_BACK_BUTTON = "../turn_back/..";

    private static int lastRecyclerAction = 0;
    private static long lastCloseClicked = 0;


    final ListAdapter listAdapter = new ListAdapter(this, strings, currentDir, selected);
    final ListAdapter listAdapter2 = new ListAdapter(this, strings1, currentDir1, selected1);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        STORAGE_LOCATION = new FileObject(Environment.getExternalStorageDirectory(), "Storage");
        SDCARD_LOCATION = new FileObject(new File(System.getenv("SECONDARY_STORAGE")), "SD-Card");

        currentDir = STORAGE_LOCATION.getFile();
        strings.add(STORAGE_LOCATION);
        if (currentDir.isDirectory())
        {
            files = currentDir.listFiles();
            for (int i = 0; i < currentDir.list().length; i++)
                Log.d("EPTA", currentDir.list()[i]);
            for (int i = 0; i < files.length; i++)
            {
                    strings.add(new FileObject(files[i]));
            }
        }


        currentDir1 = SDCARD_LOCATION.getFile();
        strings1.add(SDCARD_LOCATION);
        if (currentDir1.isDirectory())
        {
            files1 = currentDir1.listFiles();
            for (int i = 0; i < files1.length; i++)
            {
                strings1.add(new FileObject(files1[i]));
            }
        }



        recyclerView = (RecyclerView) findViewById(R.id.list1);


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(listAdapter);
        recyclerView.addItemDecoration(new MyDecorator(3));
        /////
        /////
        /////
        /////
        recyclerView2 = (RecyclerView) findViewById(R.id.list2);


        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView2.setAdapter(listAdapter2);
        recyclerView2.addItemDecoration(new MyDecorator(3));




        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Log.d("EPTAhui", "click------------------");
                        if(!inSelectMode)
                        {
                            lastRecyclerAction=1;
                            if (strings.get(position).getName() == TURN_BACK_BUTTON)
                            {
                                MoverArgument result = MoveToParent(strings, currentDir);
                                strings = result.getStrings();
                                currentDir = result.getCurrentDir();
                                listAdapter.notifyDataSetChanged();
                            } else if ((strings.get(position).getFile().getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory) ||
                                    (strings.get(position).getFile().getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory))
                            {
                                for (int i = 0; i < strings.size(); ) strings.remove(0);
                                strings.add(STORAGE_LOCATION);
                                strings.add(SDCARD_LOCATION);
                                InMainDirectory = true;
                                listAdapter.notifyDataSetChanged();
                            } else if (InMainDirectory) {
                                if (position == 0) {
                                    currentDir = STORAGE_LOCATION.getFile();
                                    for (int i = 0; i < strings.size(); ) strings.remove(0);
                                    strings.add(STORAGE_LOCATION);
                                    if (currentDir.isDirectory()) {
                                        files = currentDir.listFiles();
                                        for (int i = 0; i < files.length; i++) {
                                            strings.add(new FileObject(files[i]));
                                        }
                                    }
                                }
                                if (position == 1)
                                {
                                    currentDir = SDCARD_LOCATION.getFile();
                                    for (int i = 0; i < strings.size(); ) strings.remove(0);
                                    strings.add(SDCARD_LOCATION);
                                    if (currentDir.isDirectory()) {
                                        files = currentDir.listFiles();
                                        for (int i = 0; i < files.length; i++) {
                                            strings.add(new FileObject(files[i]));
                                        }
                                    }
                                }
                                InMainDirectory = false;
                                listAdapter.notifyDataSetChanged();
                            } else {
                                MoverArgument result = MoveToChild(strings, currentDir, position);
                                strings = result.getStrings();
                                currentDir = result.getCurrentDir();
                                listAdapter.notifyDataSetChanged();
                            }

                    }
                    else if (position != 0 && !InMainDirectory)
                        {
                            if(!selected.contains(strings.get(position).getName())) selected.add(strings.get(position).getName());
                            else selected.remove(strings.get(position).getName());
                            if(selected.size()==0) inSelectMode = false;
                            Log.d("EPTAhui", "----" + strings.get(position).getName());
                            Log.d("EPTAhui", "----" + selected);
                            listAdapter.notifyDataSetChanged();
                        }
                }

                    @Override
                    public void onItemLongClick(View view, int position)
                    {
                        lastRecyclerAction=1;
                        Log.d("EPTAhui", "longlonglongclick------------------");
                        Toast.makeText(getApplicationContext(),strings.get(position).getName(), Toast.LENGTH_SHORT).show();
                        if(!inSelectMode && position!=0 && !InMainDirectory)
                        {
                            inSelectMode = !inSelectMode;
                            selected.add(strings.get(position).getName());
                        }
                        else if(inSelectMode)
                        {
                            inSelectMode = !inSelectMode;
                            while(selected.size() > 0) selected.remove(0);
                        }

                        listAdapter.notifyDataSetChanged();

                    }

                })
        );




        recyclerView2.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView2, new RecyclerItemClickListener.OnItemClickListener() {


                    @Override
                    public void onItemClick(View view, int position)
                    {
                        if (!inSelectMode1)
                        {
                            lastRecyclerAction=2;
                            if (strings1.get(position).getName() == TURN_BACK_BUTTON) {
                                MoverArgument result = MoveToParent(strings1, currentDir1);
                                strings1 = result.getStrings();
                                currentDir1 = result.getCurrentDir();
                                listAdapter2.notifyDataSetChanged();
                            } else if ((strings1.get(position).getFile().getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory2) ||
                                    (strings1.get(position).getFile().getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory2)) {
                                for (int i = 0; i < strings1.size(); ) strings1.remove(0);
                                strings1.add(STORAGE_LOCATION);
                                strings1.add(SDCARD_LOCATION);
                                InMainDirectory2 = true;
                                listAdapter2.notifyDataSetChanged();
                            } else if (InMainDirectory2) {
                                if (position == 0)
                                {
                                    currentDir1 = STORAGE_LOCATION.getFile();
                                    for (int i = 0; i < strings1.size(); ) strings1.remove(0);
                                    strings1.add(STORAGE_LOCATION);
                                    if (currentDir1.isDirectory()) {
                                        files1 = currentDir1.listFiles();
                                        for (int i = 0; i < files1.length; i++) {
                                            strings1.add(new FileObject(files1[i]));
                                        }
                                    }
                                }
                                if (position == 1)
                                {
                                    currentDir1 = SDCARD_LOCATION.getFile();
                                    for (int i = 0; i < strings1.size(); ) strings1.remove(0);
                                    strings1.add(SDCARD_LOCATION);
                                    if (currentDir1.isDirectory()) {
                                        files1 = currentDir1.listFiles();
                                        for (int i = 0; i < files1.length; i++) {
                                            strings1.add(new FileObject(files1[i]));
                                        }
                                    }
                                }
                                InMainDirectory2 = false;
                                listAdapter2.notifyDataSetChanged();
                            } else {
                                MoverArgument result = MoveToChild(strings1, currentDir1, position);
                                strings1 = result.getStrings();
                                currentDir1 = result.getCurrentDir();
                                listAdapter2.notifyDataSetChanged();
                            }
                        } else if (position != 0 && !InMainDirectory2)
                            {
                                if(!selected1.contains(strings1.get(position).getName())) selected1.add(strings1.get(position).getName());
                                else selected1.remove(strings1.get(position).getName());
                                if(selected1.size()==0) inSelectMode1 = false;
                                Log.d("EPTAhui",  "----" + strings1.get(position).getName());
                                Log.d("EPTAhui", "----" + selected1);
                                listAdapter2.notifyDataSetChanged();
                            }


                    }

                    @Override
                    public void onItemLongClick(View view, int position)
                    {
                        lastRecyclerAction=2;
                        Log.d("EPTAhui", "longlonglongclick------------------");
                        Toast.makeText(getApplicationContext(),strings1.get(position).getName(), Toast.LENGTH_SHORT).show();
                        if(!inSelectMode1 && position != 0 && !InMainDirectory2)
                        {
                            inSelectMode1 = !inSelectMode1;
                            selected1.add(strings1.get(position).getName());
                        }
                        else if (inSelectMode1)
                        {
                            inSelectMode1 = !inSelectMode1;
                            while(selected1.size() > 0) selected1.remove(0);
                        }

                        listAdapter2.notifyDataSetChanged();
                    }
                })
        );




    }



    @Override
    public void onBackPressed() {
        if(!inSelectMode && !inSelectMode1)
        {
            if(lastRecyclerAction==0 || (lastRecyclerAction==1&&InMainDirectory) || (lastRecyclerAction==2&&InMainDirectory2))
            {
                if(System.currentTimeMillis()-lastCloseClicked < 2000) super.onBackPressed();
                else
                {
                    lastCloseClicked = System.currentTimeMillis();
                    Toast.makeText(getApplicationContext(),"Press once more to close app", Toast.LENGTH_SHORT).show();
                }

                //Toast.makeText(getApplicationContext(),"//APP_CLOSED", Toast.LENGTH_LONG).show();
            }

            else if (lastRecyclerAction==1)
            {
                if (strings.get(0).getName() == TURN_BACK_BUTTON)
                {
                    MoverArgument result = MoveToParent(strings, currentDir);
                    strings = result.getStrings();
                    currentDir = result.getCurrentDir();
                    listAdapter.notifyDataSetChanged();
                } else if ((strings.get(0).getFile().getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory) ||
                        (strings.get(0).getFile().getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory))
                {
                    for (int i = 0; i < strings.size(); ) strings.remove(0);
                    strings.add(STORAGE_LOCATION);
                    strings.add(SDCARD_LOCATION);
                    InMainDirectory = true;
                    listAdapter.notifyDataSetChanged();
                }
            }
            else if (lastRecyclerAction==2)
            {
                if (strings1.get(0).getName() == TURN_BACK_BUTTON) {
                    MoverArgument result = MoveToParent(strings1, currentDir1);
                    lastRecyclerAction = 2;
                    strings1 = result.getStrings();
                    currentDir1 = result.getCurrentDir();
                    listAdapter2.notifyDataSetChanged();
                } else if ((strings1.get(0).getFile().getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory2) ||
                        (strings1.get(0).getFile().getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath()) && !InMainDirectory2)) {
                    for (int i = 0; i < strings1.size(); ) strings1.remove(0);
                    strings1.add(STORAGE_LOCATION);
                    strings1.add(SDCARD_LOCATION);
                    InMainDirectory2 = true;
                    listAdapter2.notifyDataSetChanged();
                }
            }
        }
        else
        {
            if (inSelectMode)
            {
                inSelectMode = !inSelectMode;
                while (selected.size() > 0) selected.remove(0);
                listAdapter.notifyDataSetChanged();
            }
            if (inSelectMode1)
            {
                inSelectMode1 = !inSelectMode1;
                while (selected1.size() > 0) selected1.remove(0);
                listAdapter2.notifyDataSetChanged();
            }
        }

    }

    public MoverArgument MoveToParent(List<FileObject> strings, File currentDir)
    {
        File[] files;
        currentDir = currentDir.getParentFile();
        for(int i = 0; i < strings.size();)
        {
            strings.remove(0);
        }
        files = currentDir.listFiles();
        if (currentDir.getAbsolutePath().equals(STORAGE_LOCATION.getFile().getAbsolutePath()))
        {
            strings.add(STORAGE_LOCATION);
        }
        else if (currentDir.getAbsolutePath().equals(SDCARD_LOCATION.getFile().getAbsolutePath()))
        {
            strings.add(SDCARD_LOCATION);
        }
        else strings.add(new FileObject(TURN_BACK_BUTTON));
        for (int i = 0; i < files.length; i++)
        {
            strings.add(new FileObject(files[i]));
        }
        MoverArgument result = new MoverArgument();
        result.setStrings(strings);
        result.setCurrentDir(currentDir);
        return result;
    }
    public MoverArgument MoveToChild (List<FileObject> strings, File currentDir, int position)
    {
        File[] files;
        currentDir = new File(currentDir.getAbsolutePath() + "/" + strings.get(position).getName());
        if(currentDir.isDirectory())
        {
            for(int i = 0; i < strings.size();)
            {
                strings.remove(0);
            }
            files = currentDir.listFiles();
            strings.add(new FileObject(TURN_BACK_BUTTON));
            for (int i = 0; i < files.length; i++)
            {
                strings.add(new FileObject(files[i]));
            }
        }
        else currentDir = currentDir.getParentFile();
        MoverArgument result = new MoverArgument();
        result.setStrings(strings);
        result.setCurrentDir(currentDir);
        return result;
    }



}