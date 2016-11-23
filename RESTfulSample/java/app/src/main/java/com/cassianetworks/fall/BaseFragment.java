package com.cassianetworks.fall;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.x;

/**
 * Created by Cassia on 2015/7/3./
 */
public abstract class BaseFragment extends Fragment {

    //    private HttpAction mHttpAction;
    public BaseActivity parent;
    public DisplayMetrics dm;
    protected View view;
    protected FragmentManager fm;
    protected LayoutInflater inflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parent = ((BaseActivity) getActivity());
        fm = getFragmentManager();
        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        view = x.view().inject(this, inflater, container);
        init();
//        setListener();
        setTitle();
        return view;
    }


//    protected void setContentView(int layoutID) {
//        view = View.inflate(getActivity(), layoutID, null);
//    }


    protected View findViewById(int id) {
        return view.findViewById(id);
    }
//    public View getView(){
//        return view;
//    }

    /**
     * 初始化UI
     */
    protected abstract void init();

    /**
     * 设置监听器
     */
//    protected abstract void setListener();

    /**
     * 设置标题
     */
    protected void setTitle() {
    }

    public boolean onKeyBackDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && onKeyBackDown();
    }

    public boolean onKeyBackDown() {
        return true;
    }


    public void showTips(final String message) {
        if (isAdded())
            parent.showTips(message);
    }

    public void switchFragment(Fragment from, Fragment to, Bundle bundle) {
        if (bundle != null) {
            Bundle arguments = to.getArguments();
            if (arguments == null) {
                to.setArguments(bundle);
            } else {
                arguments.putAll(bundle);
            }

        }
        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                .beginTransaction().setCustomAnimations(
                        R.anim.push_right_in, R.anim.push_left_out);
        if (!to.isAdded()) {    // 先判断是否被add过
            transaction.hide(from).add(R.id.container, to).addToBackStack(null).commit(); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            transaction.hide(from).addToBackStack(null).show(to).commit(); // 隐藏当前的fragment，显示下一个
        }

    }

    /**
     * 通过Class跳转界面
     */
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 含有Bundle通过Class跳转界面
     */
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(getContext(), cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }
}
