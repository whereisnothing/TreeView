package com.chenxu.treeview;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RelativeLayout root = (RelativeLayout)findViewById(R.id.root);
        List<TreeView.TreeNode> nodeList=new ArrayList<>();
        TreeView treeView = new TreeView(this, nodeList, new TreeView.TreeViewListener() {
            @Override
            public void treeViewDidClick(TreeView.TreeNode node, String title) {
                Snackbar.make(root, title, Snackbar.LENGTH_SHORT).show();
            }
        });
        TreeView.TreeNode hefei = treeView.new TreeNode("合肥",1,false,false,null);
        TreeView.TreeNode bengbu = treeView.new TreeNode("蚌埠",1,false,false,null);
        List<TreeView.TreeNode> children = new ArrayList<>();
        children.add(hefei);
        children.add(bengbu);
        TreeView.TreeNode anhui = treeView.new TreeNode("安徽",0,false,true,children);
        nodeList.add(anhui);
        TreeView.TreeNode yangpu = treeView.new TreeNode("杨浦",1,false,false,null);
        TreeView.TreeNode xuhui = treeView.new TreeNode("徐汇",1,false,false,null);
        List<TreeView.TreeNode> children2 = new ArrayList<>();
        children2.add(yangpu);
        children2.add(xuhui);
        TreeView.TreeNode shanghai = treeView.new TreeNode("上海",0,false,true,children2);
        nodeList.add(shanghai);
        RelativeLayout.LayoutParams treeViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        treeView.setLayoutParams(treeViewParams);
        root.addView(treeView);
        treeView.setNodeList(nodeList);

//       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
