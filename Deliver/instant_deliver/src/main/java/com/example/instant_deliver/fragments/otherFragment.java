package com.example.instant_deliver.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.instant_deliver.R;
import com.example.instant_deliver.beans.Order;
import com.example.instant_deliver.beans._User;
import com.example.instant_deliver.tools.ListviewForScrollView;
import com.example.instant_deliver.tools.getConnState;
import com.example.instant_deliver.adapters.orderAdapter;
import com.example.instant_deliver.tools.orderListTool;
import com.example.instant_deliver.tools.orderTool;
import com.example.instant_deliver.tools.usersManager;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class otherFragment extends Fragment {

    //可见的item
    private int start_index, end_index;
    //每页数据条数的最大值
    private Integer LIMIT = 5;
    //当前页
    private Integer currentPage = 0;
    //状态码（默认刷新）
    private Integer STATECODE = 0;
    private orderAdapter adapter;
    private List<Order> totallist;
    private PullToRefreshScrollView scrollView;
    private ListviewForScrollView mylistView;
    private TextView warn;
    private View view;
    private Order order = new Order();
    private int pos;
    //回调接口
    private orderAdapter.CallBack callBack;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //接单
            if (msg.what == 2) {
                if (getConnState.isConn(getActivity())) {
                    Order order = (Order) msg.obj;
                    int pos = msg.arg1;
                    updateOrder();
                } else {
                    Toast.makeText(getActivity().getApplication(), "当前网络不可用", Toast.LENGTH_SHORT).show();
                }
            }

            if(msg.what==3){
                if(!(msg.obj.toString()==null)){
                    orderTool.opengaode(msg.obj.toString(),getActivity());
                }
            }

            if (msg.what == 1) {
                _User users = BmobUser.getCurrentUser(_User.class);
                BmobQuery<Order> bmobQuery = new BmobQuery<>();
                //设置每页最多为5条数据
                bmobQuery.setLimit(LIMIT);
                bmobQuery.addWhereEqualTo("school", users.getUniversity());
                bmobQuery.addWhereEqualTo("orderState", 1);
                bmobQuery.addWhereEqualTo("orderType", "其他");
                //删选超时的订单
                //bmobQuery.addWhereGreaterThanOrEqualTo("endDate", new BmobDate(new Date()));
                //最新发布与奖励最多的
                bmobQuery.order("-award,-createdAt");

                //刷新状态
                if (STATECODE == 0) {
                    //当前页归零
                    currentPage = 0;
                    bmobQuery.setSkip(0);
                    bmobQuery.findObjects(new FindListener<Order>() {
                        @Override
                        public void done(List<Order> list, BmobException e) {
                            scrollView.onRefreshComplete();
                            if (list == null || list.size() == 0) {
                                warn.setVisibility(View.VISIBLE);
                                totallist =new ArrayList<Order>();
                            } else {
                                warn.setVisibility(View.GONE);
                                totallist = list;
                            }
                            adapter = new orderAdapter(totallist, getActivity().getApplicationContext(), callBack);
                            adapter.notifyDataSetChanged();
                            mylistView.setAdapter(adapter);
                        }
                    });
                }

                if (STATECODE == 1) {
                    bmobQuery.setSkip((currentPage + 1) * LIMIT);
                    bmobQuery.findObjects(new FindListener<Order>() {
                        @Override
                        public void done(List<Order> list, BmobException e) {
                            scrollView.onRefreshComplete();
                            if (totallist == null || totallist.size() == 0) {
                                warn.setVisibility(View.VISIBLE);
                            } else {
                                if (list.size() < 5 && currentPage > 0) {
                                    //改变提示语
                                    warn.setText("没有更多数据了，童鞋");
                                }

                                warn.setVisibility(View.GONE);
                                //添加到
                                totallist.addAll(list);
                                //listview适配
                                adapter = new orderAdapter(totallist, getActivity().getApplicationContext(), callBack);
                                adapter.notifyDataSetChanged();
                                mylistView.setAdapter(adapter);
                            }
                        }
                    });

                    currentPage++;
                }
            }
        }
    };

    //修改订单状态
    private void updateOrder() {
        final _User reciver = BmobUser.getCurrentUser(_User.class);
        Order order1 = new Order();
        if (order.getLauncher()
                .equals(reciver.getObjectId())) {
            Toast.makeText(getActivity().getApplication(), "不能接自己发出的单", Toast.LENGTH_LONG).show();
        }else if (order.getOrderState() == 1) {
            //状态设置为接单中
            order1.setOrderState(2);
            order1.setReciver(reciver.getObjectId());
            order1.setReceviername(reciver.getUsername());
            //更新订单信息
            order1.update(order.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        //添加好友
                        usersManager.addFriend(reciver.getObjectId(),order.getLauncher());
                        //保存订单好友关系表
                        orderListTool.saveState(getActivity(),reciver.getObjectId(),order.getLauncher(),order.getObjectId());
                        totallist.remove(pos);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getActivity().getApplication(), "抢单成功！！！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity().getApplication(), "抢单失败" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            //已被抢单，则被清除
            totallist.remove(pos);
            adapter.notifyDataSetChanged();
            Toast.makeText(getActivity().getApplication(), "抢单失败，已经被抢单", Toast.LENGTH_SHORT).show();
        }

    }

    public otherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_other, container, false);
        }

        //初始化
        init();
        //网络可用才加载
        if (getConnState.isConn(getActivity())) {
            STATECODE = 0;
            //获取数据请求
            handler.sendEmptyMessageDelayed(1, 500);
        } else {
            Toast.makeText(getActivity().getApplication(), "当前网络不可用", Toast.LENGTH_SHORT).show();
        }
        //监听事件
        listenr();
        return view;
    }


    //防止fragment被销毁
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    //初始化控件
    private void init() {
        scrollView = (PullToRefreshScrollView) view.findViewById(R.id.other_scrollView);
        mylistView = (ListviewForScrollView) view.findViewById(R.id.other_listView);
        //手动把ScrollView滚动至最顶端
        scrollView.smoothScrollTo(0, 0);
        warn = (TextView) view.findViewById(R.id.other_warn);
        totallist = new ArrayList<>();
        callBack = new orderAdapter.CallBack() {
            @Override
            public void Buttonclick(View view) {
                //当前位置
                pos = Integer.parseInt("" + view.getTag());
                //每次点击都要实时的获取当前状态
                BmobQuery<Order> query =new BmobQuery<>();
                query.getObject(totallist.get(pos).getObjectId(), new QueryListener<Order>() {
                    @Override
                    public void done(Order myorder, BmobException e) {
                        order = myorder;
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("确定抢此单!");
                builder.setTitle("提示");
                builder.setIcon(R.drawable.logo);

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //提示框消失
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        dialog.dismiss();
                        handler.sendEmptyMessageDelayed(2, 500);
                    }
                });

                //显示dialog
                final AlertDialog alertDialog = builder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FFA500"));
                    }
                });
                alertDialog.show();
            }

            @Override
            public void AddressClick(View view) {
                Message message =new Message();
                message.what = 3;
                message.obj = view.getTag();
                handler.sendMessage(message);
            }
        };
    }

    //监听事件
    private void listenr() {
        //设置刷新模式：上推下拉都可以刷新
        scrollView.setMode(PullToRefreshBase.Mode.BOTH);

        scrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            //下拉
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (getConnState.isConn(getActivity())) {
                    //下拉为刷新
                    STATECODE = 0;
                    //获取数据请求
                    handler.sendEmptyMessageDelayed(1, 1000);
                } else {
                    scrollView.onRefreshComplete();
                    Toast.makeText(getActivity().getApplication(), "当前网络不可用", Toast.LENGTH_SHORT).show();
                }
                scrollView.getRefreshableView();

            }

            //上推
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                ILoadingLayout pullup = scrollView.getLoadingLayoutProxy(false, true);
                pullup.setPullLabel("加载更多");
                pullup.setRefreshingLabel("正在加载");
                pullup.setReleaseLabel("加载更多");
                if (getConnState.isConn(getActivity())) {
                    //上推为加载更多
                    STATECODE = 1;
                    //获取数据请求
                    handler.sendEmptyMessageDelayed(1, 1000);
                } else {
                    scrollView.onRefreshComplete();
                    Toast.makeText(getActivity().getApplication(), "当前网络不可用", Toast.LENGTH_SHORT).show();
                }
                scrollView.getRefreshableView();
            }
        });
    }
}
