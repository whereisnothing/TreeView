package com.chenxu.treeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TreeView extends ListView implements
		AdapterView.OnItemClickListener {
	private List<TreeNode> nodeList;
	private TreeViewListener listener;
    private TreeAdapter adapter;

	public TreeView(Context context, List<TreeNode> nodeList,
                    TreeViewListener listener) {
		super(context);
		this.nodeList = nodeList;
		this.listener = listener;
		setOverScrollMode(View.OVER_SCROLL_ALWAYS);

		setOnItemClickListener(this);
        adapter=new TreeAdapter();
        setAdapter(adapter);
        LogUtil.ii("after set adapter");
	}

	public TreeView(Context context, String dataSourceFileName,
                    TreeViewListener listener) {
		super(context);
		this.listener = listener;
		setOverScrollMode(OVER_SCROLL_ALWAYS);

		setOnItemClickListener(this);

		parseFile(dataSourceFileName);
        adapter=new TreeAdapter();
        setAdapter(adapter);
	}

	private void parseFile(String dataSourceFileName) {
		InputStream in = null;
		try {
			in = getContext().getResources().getAssets()
					.open(dataSourceFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(in, "UTF-8");

			List<TreeNode> rootNodeList=null;
			List<TreeNode> hierarchyNodeList=new ArrayList<TreeNode>();
			int eventType = parser.getEventType();
			int currentlevel=0;
			Stack<TreeNode> stack=null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if (parser.getName().equals("tree")) {
						rootNodeList=new ArrayList<TreeNode>();
					} else if (parser.getName().equals("node")) {
						String title = parser.getAttributeValue(null, "title");
						String level=parser.getAttributeValue(null, "level");
						String isExpand = parser.getAttributeValue(null, "isExpand");
						boolean bIsExpand = "true".equals(isExpand)?true:false;
						int levelInt = Integer.valueOf(level);
						if (levelInt==0) {
							stack=new Stack<TreeNode>();
							TreeNode node = new TreeNode(title, levelInt, bIsExpand, false, null);
							stack.push(node);
						}else {
							TreeNode node=new TreeNode(title, levelInt, bIsExpand, false, null);
							stack.push(node);
						}
						eventType = parser.next();
					} 
					break;
				case XmlPullParser.END_TAG:
					if (parser.getName().equals("tree")) {
						this.nodeList=rootNodeList;
					}else if (parser.getName().equals("node")) {
						if (!stack.isEmpty()) {
							TreeNode top = stack.pop();
							if (top.level==0) {
								rootNodeList.add(top);
							} else {
								if (stack.size()>=1) {
									TreeNode tempNextToTop = stack.peek();
									tempNextToTop.addChild(top);
								}

							}

						}
					}
					break;
				}
				eventType = parser.next();
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public TreeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TreeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

    @Override
    public TreeAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(TreeAdapter adapter) {
        this.adapter = adapter;
        super.setAdapter(adapter);
    }

    public List<TreeNode> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<TreeNode> nodeList) {
        this.nodeList = nodeList;
        adapter.notifyDataSetChanged();
    }

    @Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		TreeNode node = getNodeFromDisplayPosition(position);
		if (node.hasChildren) {
			node.setExpand(!node.isExpand);
		} else {
			if (listener != null) {
				listener.treeViewDidClick(node, node.getTitle());
			}
		}
	}

	public TreeNode getNodeFromRealPosition(int position) {
		int startCount = 0, endCount = 0;
		for (int i = 0; i < nodeList.size(); i++) {
			TreeNode node = nodeList.get(i);
			endCount += node.getRealCount();
			if (position < endCount) {
				return node.getNodeFromRealPosition(position - startCount);
			}
			startCount += node.getRealCount();
		}
		return null;
	}

	public TreeNode getNodeFromDisplayPosition(int position) {
		int startCount = 0, endCount = 0;
		for (int i = 0; i < nodeList.size(); i++) {
			TreeNode node = nodeList.get(i);
			endCount += node.getDisplayCount();
			if (position < endCount) {
				return node.getNodeFromDisplayPosition(position - startCount);
			}
			startCount += node.getDisplayCount();
		}
		return null;
	}

	public class TreeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			int count = 0;
			if (nodeList == null) {
				count = 0;
			} else {
				for (int i = 0; i < nodeList.size(); i++) {
					TreeNode node = nodeList.get(i);
					count += node.getDisplayCount();
				}

			}
            LogUtil.ii("getCount:"+count);
			return count;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return getNodeFromDisplayPosition(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			TreeNode node = getNodeFromDisplayPosition(position);
			LogUtil.i("chenxu", "position:" + position + " node:" + node);
			if (convertView == null) {
				viewHolder = new ViewHolder();
				LinearLayout linearLayout = new LinearLayout(getContext());
				LayoutParams linearLayoutParams = new LayoutParams(
						LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
				linearLayout.setLayoutParams(linearLayoutParams);
				linearLayout.setOrientation(LinearLayout.HORIZONTAL);
				linearLayout.setGravity(Gravity.CENTER_VERTICAL);

				ImageView imageView = new ImageView(getContext());
				LinearLayout.LayoutParams imageViewLayoutParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				imageView.setLayoutParams(imageViewLayoutParams);
				imageView.setScaleType(ScaleType.CENTER_INSIDE);
				viewHolder.foldImageView = imageView;
				linearLayout.addView(imageView);

				TextView textView = new TextView(getContext());
				LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(
						0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
				textView.setLayoutParams(textViewLayoutParams);
				viewHolder.titleTextView = textView;
				linearLayout.addView(textView);

				linearLayout.setTag(viewHolder);
				convertView = linearLayout;
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (node.hasChildren) {
				viewHolder.foldImageView.setVisibility(View.VISIBLE);
				int imageId = 0;
				if (node.isExpand) {
					imageId = R.drawable.minus;
				} else {
					imageId = R.drawable.plus;
				}
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
						imageId);
				viewHolder.foldImageView.setImageBitmap(bitmap);
			} else {
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.plus);
				viewHolder.foldImageView.setImageBitmap(bitmap);
				viewHolder.foldImageView.setVisibility(View.INVISIBLE);
			}
			viewHolder.titleTextView.setText(node.title);
			viewHolder.titleTextView.setTextSize(22);
			viewHolder.titleTextView.setTextColor(Color.BLUE);
			LogUtil.i("chenxu",
					"level:" + node.level + " padding:" + node.getLevel() * 30);
			convertView.setPadding(node.getLevel() * 30, 0, 0, 0);

			return convertView;
		}

		public class ViewHolder {
			public ImageView foldImageView;
			public TextView titleTextView;
		}
	}

	public class TreeNode {
		private String title;
		private int level;
		private boolean isExpand;
		private boolean hasChildren;
		private List<TreeNode> children;

		public TreeNode(String title, int level, boolean isExpand,
				boolean hasChildren, List<TreeNode> children) {
			super();
			this.title = title;
			this.level = level;
			this.isExpand = isExpand;
			this.hasChildren = hasChildren;
			this.children = children;
		}
		public void addChild(TreeNode child) {
			this.hasChildren=true;
			if (children==null) {
				children=new ArrayList<TreeNode>();
			}
			children.add(child);
		}

		public TreeNode getNodeFromRealPosition(int position) {
			int startCount = 0, endCount = 0;
			if (position == 0) {
				return this;
			}
			if (hasChildren) {
				for (int i = 0; i < children.size(); i++) {
					TreeNode node = children.get(i);
					endCount += node.getRealCount();
					if (position <= endCount) {
						return node.getNodeFromRealPosition(position
								- startCount - 1);
					}
					startCount += node.getRealCount();
				}

			} else {
				if (position == 0) {
					return this;
				} else {
					return null;
				}
			}
			return null;
		}

		public TreeNode getNodeFromDisplayPosition(int position) {
			int startCount = 0, endCount = 0;
			if (position == 0) {
				return this;
			}
			if (hasChildren) {
				for (int i = 0; i < children.size(); i++) {
					TreeNode node = children.get(i);
					endCount += node.getDisplayCount();
					if (position <= endCount) {
						return node.getNodeFromDisplayPosition(position
								- startCount - 1);
					}
					startCount += node.getDisplayCount();
				}

			} else {
				if (position == 0) {
					return this;
				} else {
					return null;
				}
			}
			return null;
		}

		public String getTitleFromDisplayPosition(int position) {
			String title = "";
			TreeNode node = getNodeFromDisplayPosition(position);
			if (node != null) {
				title = node.getTitle();
			}
			return title;
		}

		public int getDisplayCount() {
			int count = 1;
			if (isExpand) {
				if (hasChildren) {
					for (int i = 0; i < children.size(); i++) {
						TreeNode child = children.get(i);
						count += child.getDisplayCount();
					}
				} else {

				}
			} else {

			}
			return count;
		}

		public int getRealCount() {
			int count = 1;
			if (hasChildren) {
				for (int i = 0; i < children.size(); i++) {
					TreeNode child = children.get(i);
					count += child.getRealCount();
				}
			} else {

			}
			return count;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public boolean isExpand() {
			return isExpand;
		}

		public void setExpand(boolean isExpand) {
			if (isExpand != this.isExpand) {
				this.isExpand = isExpand;
				((BaseAdapter) getAdapter()).notifyDataSetChanged();
			}
		}

		public boolean isHasChildren() {
			return hasChildren;
		}

		public void setHasChildren(boolean hasChildren) {
			this.hasChildren = hasChildren;
		}

		public List<TreeNode> getChildren() {
			return children;
		}

		public void setChildren(List<TreeNode> children) {
			this.children = children;
		}

	}

	public interface TreeViewListener {
		public void treeViewDidClick(TreeNode node, String title);
	}
}
