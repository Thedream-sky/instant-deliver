package com.example.instant_deliver.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.instant_deliver.R;
import com.example.instant_deliver.beans.Order;
import com.example.instant_deliver.tools.loadImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 2018/5/19.
 */

public class orderManageAdapter extends BaseAdapter implements View.OnClickListener{
    private List<Order> orderList = new ArrayList<>();
    private Context context;
    private CallBack callBack;
    private String flag;

    public orderManageAdapter(Context context, List<Order> orderList, CallBack callBack,String flag){
        this.context = context;
        this.orderList = orderList;
        this.callBack = callBack;
        this.flag = flag;
     }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int i) {
        return orderList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();
        Order order = orderList.get(i);
        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.order_manager_item, null);
            viewHolder.idcode = (TextView) view.findViewById(R.id.order_id_code);
            viewHolder.orderImg = (ImageView) view.findViewById(R.id.order_img);
            viewHolder.state = (TextView) view.findViewById(R.id.order_state);
            viewHolder.orderInfodetail = (TextView) view.findViewById(R.id.order_info_detail);
            viewHolder.cancelOrder = (TextView) view.findViewById(R.id.cancel_order);
            viewHolder.deleteOrder = (TextView) view.findViewById(R.id.delete_order);
            //viewHolder.finishedOrder = (TextView) view.findViewById(R.id.finished_order);
            viewHolder.orderPay = (TextView) view.findViewById(R.id.order_pay);
            viewHolder.orderPayDetail = (TextView) view.findViewById(R.id.order_pay_detail);
            viewHolder.buttonGroup = (LinearLayout) view.findViewById(R.id.order_button_group);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.idcode.setText("订单编号："+"SI_"+order.getObjectId());
        loadImage.loadImageGlideRound(context,order.getLauncherhead(),viewHolder.orderImg);
        viewHolder.orderInfodetail.setText(order.getInfo());
        viewHolder.orderPay.setText("￥"+order.getAward());
        viewHolder.orderPayDetail.setText("合计：￥"+order.getAward());
        viewHolder.deleteOrder.setOnClickListener(this);
        //viewHolder.finishedOrder.setOnClickListener(this);
        viewHolder.cancelOrder.setOnClickListener(this);
        String orderState = ""+order.getOrderState();
        //Log.i("xk",exchangeState(orderType));
        viewHolder.state.setText(exchangeState(orderState));

        if(flag.equals("1")){
            if(orderState .equals("2")){
                viewHolder.buttonGroup.setVisibility(View.GONE);
            }else if(orderState .equals("3")){
                viewHolder.cancelOrder.setVisibility(View.GONE);
                //viewHolder.finishedOrder.setVisibility(View.GONE);
            }else if(orderState.equals("4")){
                viewHolder.cancelOrder.setVisibility(View.GONE);
                //viewHolder.finishedOrder.setVisibility(View.GONE);
            }
        }else {
            viewHolder.cancelOrder.setVisibility(View.GONE);
            if(orderState.equals("2")){
                viewHolder.deleteOrder.setVisibility(View.GONE);
            }if(orderState.equals("3")){
                viewHolder.cancelOrder.setVisibility(View.GONE);
               // viewHolder.finishedOrder.setVisibility(View.GONE);
            }
        }
        return view;
    }

    private String exchangeState(String orderType){
        String state = "9999";
        if(orderType.equals("1")){
            state = "未接单";
        }else if(orderType.equals("2")){
            state = "进行中";
        }else if(orderType.equals("3")){
            state = "已完成";
        }else if(orderType.equals("4")) {
            state = "取消";
        }
        return state;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.delete_order:
                callBack.deleteOrder(view);
                break;
            case R.id.cancel_order:
                callBack.cancelOrder(view);
                break;
        }
    }

    public interface CallBack {
        void deleteOrder(View view);
        void cancelOrder(View view);
        //void finishOrder(View view);
    }

    //内部类（内容提供者）
    class ViewHolder {
        //订单编号
        private TextView idcode;
        //订单状态
        private TextView state;
        //订单头像
        private ImageView orderImg;
        //订单信息
        private TextView orderInfodetail;
        //订单费用
        private TextView orderPay;
        //订单费用详情
        private TextView orderPayDetail;
        //删除订单
        private TextView deleteOrder;
        //完成订单
        //private TextView finishedOrder;
        //取消订单
        private TextView cancelOrder;
        //
        private LinearLayout buttonGroup;
    }
}
