package com.example.jcs.orderassistant.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import com.example.jcs.orderassistant.db.DatabaseHelper;
import com.example.jcs.orderassistant.db.DatabaseSchema;
import com.example.jcs.orderassistant.db.DatabaseSchema.MemberEntry;
import com.example.jcs.orderassistant.R;
import com.example.jcs.orderassistant.app.OrderApplication;

import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGANormalRefreshViewHolder;
import de.timroes.android.listview.EnhancedListView;

public class MainActivity extends Activity implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private BGARefreshLayout mRefreshLayout;

    private EnhancedListView listView = null;
    private List<DealInfo> dealInfoList = new ArrayList<DealInfo>();
    private List<Integer> recordIdList = new  ArrayList<Integer>();
    private List<Integer> delList = new  ArrayList<Integer>();
    private DealInfoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.activity_header);

        TextView header = (TextView) findViewById(R.id.header_text);
        header.setText("订餐小助手");

        initRefreshLayout();

        Button addMButton = (Button) findViewById(R.id.AddMButton);
        addMButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddMemberActivity.class);
                startActivity(intent);
            }
        });

        Button aaButton = (Button) findViewById(R.id.AAButton);
        aaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AACountActivity.class);
                startActivity(intent);
            }
        });

        Button sepButton = (Button) findViewById(R.id.IndButton);
        sepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SepcountActivity.class);
                startActivity(intent);
            }
        });

        Button banlaceButton = (Button) findViewById(R.id.ListOfAccounts);
        banlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BanlaceActivity.class);
                startActivity(intent);
            }
        });

        Button rmButton = (Button) findViewById(R.id.RMButton);
        rmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReceiveMoneyActivity.class);
                startActivity(intent);
            }
        });

        Button adButton = (Button) findViewById(R.id.RecButton);
        adButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdvanceRecordActivity.class);
                startActivity(intent);
            }
        });

        listView = (EnhancedListView) findViewById(R.id.main_lv);
        getDealInfo();
        mAdapter = new DealInfoAdapter(MainActivity.this,R.layout.deal_item,dealInfoList);
        listView.setAdapter(mAdapter);

        // Set the callback that handles dismisses.
        listView.setDismissCallback(new de.timroes.android.listview.EnhancedListView.OnDismissCallback() {
            @Override
            public EnhancedListView.Undoable onDismiss(EnhancedListView listView, final int position) {
                Integer id = recordIdList.get(position);
                delList.clear();
                delList.add(id);

                final DealInfo item = mAdapter.getItem(position);
                mAdapter.remove(position);

                return new EnhancedListView.Undoable() {
                    @Override
                    public void undo() {
                        delList.remove(delList.size() - 1);
                        mAdapter.insert(position, item);
                    }

                    @Override
                    public String getTitle() {
                        return "删除交易记录"; // Plz, use the resource system :)
                    }

                    @Override
                    public void discard() {
                        //恢复成员余额，删除子订单，删除订单
                        //这里需要用一个事务
                        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
                        SQLiteDatabase db = dbHelper.getWritableDatabase();

                        db.beginTransaction();
                        try {
                            for (int i = 0; i < delList.size(); i++) {
                                //1，按订单号查询子订单
                                String id_query = "select * from " + DatabaseSchema.SubOrderEntry.TABLE_NAME
                                        + " where " + DatabaseSchema.SubOrderEntry.COLUMN_ORDERID + " = "
                                        + delList.get(i);
                                Cursor cursor = db.rawQuery(id_query, null);
                                int count = 0;
                                List<Integer> memberIdLst = new ArrayList<Integer>();
                                List<Integer> subOrderIdLst = new ArrayList<Integer>();
                                List<Float> moneyLst = new ArrayList<Float>();
                                while (cursor.moveToNext()) {
                                    subOrderIdLst.add(cursor.getInt(0));
                                    moneyLst.add(cursor.getFloat(2));
                                    memberIdLst.add(cursor.getInt(3));
                                    count++;
                                }

                                for (int j = 0; j < memberIdLst.size(); j++) {
                                    id_query = "select " + DatabaseSchema.MemberEntry.COLUMN_MONEY
                                            + " from " + DatabaseSchema.MemberEntry.TABLE_NAME
                                            + " where " + MemberEntry._ID + " = "
                                            + memberIdLst.get(j);

                                    float money = 0;
                                    cursor = db.rawQuery(id_query, null);
                                    while (cursor.moveToNext()) {
                                        money = cursor.getFloat(0);
                                        break;
                                    }

                                    //2,修改余额
                                    ContentValues values = new ContentValues();
                                    values.clear();
                                    values.put(DatabaseSchema.MemberEntry.COLUMN_MONEY, moneyLst.get(j) + money);
                                    id_query = "" + DatabaseSchema.MemberEntry._ID + "= ?";
                                    String[] arry = new String[1];
                                    arry[0] = Integer.toString(memberIdLst.get(j));
                                    db.update(DatabaseSchema.MemberEntry.TABLE_NAME, values, id_query, arry);
                                }

                                //3,删除子订单
                                for (int j = 0; j < subOrderIdLst.size(); j++) {
                                    String[] args = new String[1];
                                    args[0] = Integer.toString(subOrderIdLst.get(j));
                                    db.delete(DatabaseSchema.SubOrderEntry.TABLE_NAME, DatabaseSchema.SubOrderEntry._ID + " =?",
                                            args);
                                }

                                recordIdList.remove(delList.get(i));

                                //4,删除订单
                                String[] mainId = new String[1];
                                mainId[0] = Integer.toString(delList.get(i));
                                db.delete(DatabaseSchema.OrderEntry.TABLE_NAME, DatabaseSchema.OrderEntry._ID + " =?",
                                        mainId);
                            }
                            db.setTransactionSuccessful();
                            ;
                        } catch (Exception e) {
                            ;
                        } finally {
                            db.endTransaction();
                        }
                        delList.clear();
                    }
                }

                        ;

            }
        });

//        listView.enableSwipeToDismiss();
        listView.disableSwipeToDismiss();
        listView.setUndoStyle(EnhancedListView.UndoStyle.SINGLE_POPUP);
    }

    @Override
    protected void onStop() {
        if(listView != null) {
            listView.discardUndo();
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        if(listView != null) {
            listView.discardUndo();
        }
        super.onPause();
    }

    protected void onRestart() {
        super.onRestart();
        listView = (EnhancedListView) findViewById(R.id.main_lv);
        getDealInfo();
        DealInfoAdapter adapter = new DealInfoAdapter(MainActivity.this,R.layout.deal_item,dealInfoList);
        listView.setAdapter(adapter);
    }

    private void getDealInfo()
    {
        dealInfoList.clear();
        recordIdList.clear();
        DatabaseHelper dbHelper = OrderApplication.getDbHelper();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String query = "select * from " + DatabaseSchema.OrderEntry.TABLE_NAME
                + " order by " + DatabaseSchema.OrderEntry._ID +" desc";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            recordIdList.add(cursor.getInt(0));

            String detail ="";
            float sum = 0;
            int id = cursor.getInt(0);
            long date = cursor.getLong(1);
            String dateStr = UiUtility.GetDateInfo(date);
            int return_money = cursor.getInt(2);
            String dining = cursor.getString(3);
            String sub_query = "select " + " * "
                    +" from " + DatabaseSchema.SubOrderEntry.TABLE_NAME
                    + " where " + DatabaseSchema.SubOrderEntry.COLUMN_ORDERID +" = "
                    + Integer.toString(id);
            Cursor subcursor = db.rawQuery(sub_query,null);
            while (subcursor.moveToNext()) {
                float each = subcursor.getFloat(2);
                sum+=subcursor.getFloat(2);

                String member_query = "select " + DatabaseSchema.MemberEntry.COLUMN_NAME
                        +" from " + DatabaseSchema.MemberEntry.TABLE_NAME
                        + " where " + DatabaseSchema.MemberEntry._ID +" = "
                        + subcursor.getInt(3);
                Cursor member_cursor = db.rawQuery(member_query,null);
                String memberName="";
                while (member_cursor.moveToNext()) {
                    memberName =member_cursor.getString(0);
                }

                detail +=memberName;
                detail += " "+ UiUtility.getMoneyStr(each) + " ";
            }

            DealInfo info = new DealInfo(dining,dateStr,sum,detail);
            dealInfoList.add(info);
        }
    }

    private void initRefreshLayout() {
        mRefreshLayout = (BGARefreshLayout) findViewById(R.id.refreshLayout);

        mRefreshLayout.setPullDownRefreshEnable(false);
        // 为BGARefreshLayout设置代理
        mRefreshLayout.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGARefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(this, false);
        // 设置下拉刷新和上拉加载更多的风格
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);

        // 为了增加下拉刷新头部和加载更多的通用性，提供了以下可选配置选项  -------------START
        // 设置正在加载更多时的文本
        refreshViewHolder.setLoadingMoreText("loading...");
        // 设置整个加载更多控件的背景颜色资源id
        refreshViewHolder.setLoadMoreBackgroundColorRes(R.color.custom_imoocstyle);
        // 设置整个加载更多控件的背景drawable资源id
       // refreshViewHolder.setLoadMoreBackgroundDrawableRes(loadMoreBackgroundDrawableRes);
        // 设置下拉刷新控件的背景颜色资源id
        refreshViewHolder.setRefreshViewBackgroundColorRes(R.color.custom_imoocstyle);
        // 设置下拉刷新控件的背景drawable资源id
        //refreshViewHolder.setRefreshViewBackgroundDrawableRes(refreshViewBackgroundDrawableRes);
        // 设置自定义头部视图（也可以不用设置）     参数1：自定义头部视图（例如广告位）， 参数2：上拉加载更多是否可用
        //mRefreshLayout.setCustomHeaderView(headerView, false);
        // 可选配置  -------------END
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        // 在这里加载最新数据

        ImageView view = (ImageView) findViewById(R.id.img);
        view.setVisibility(View.VISIBLE);

        /*if (mIsNetworkEnabled) {
            // 如果网络可用，则加载网络数据
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(MainActivity.LOADING_DURATION);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    // 加载完毕后在UI线程结束下拉刷新
                    mRefreshLayout.endRefreshing();
                    mDatas.addAll(0, DataEngine.loadNewData());
                    mAdapter.setDatas(mDatas);
                }
            }.execute();
        } else {*/
            // 网络不可用，结束下拉刷新
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            mRefreshLayout.endRefreshing();
        //}
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        // 在这里加载更多数据，或者更具产品需求实现上拉刷新也可以

        ImageView view = (ImageView) findViewById(R.id.img);
        view.setVisibility(View.GONE);

        /*if (mIsNetworkEnabled) {
            // 如果网络可用，则异步加载网络数据，并返回true，显示正在加载更多
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(MainActivity.LOADING_DURATION);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    // 加载完毕后在UI线程结束加载更多
                    mRefreshLayout.endLoadingMore();
                    mAdapter.addDatas(DataEngine.loadMoreData());
                }
            }.execute();

            return true;
        } else {*/
            // 网络不可用，返回false，不显示正在加载更多
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
        return false;
        //}
    }

    // 通过代码方式控制进入正在刷新状态。应用场景：某些应用在activity的onStart方法中调用，自动进入正在刷新状态获取最新数据
    public void beginRefreshing() {
        mRefreshLayout.beginRefreshing();
    }

    // 通过代码方式控制进入加载更多状态
    public void beginLoadingMore() {
        mRefreshLayout.beginLoadingMore();
    }


    @Override
    protected void onResume() {
        super.onResume();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
