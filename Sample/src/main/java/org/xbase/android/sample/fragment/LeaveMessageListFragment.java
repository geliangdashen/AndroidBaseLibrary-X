package org.xbase.android.sample.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import org.xbase.android.BaseFragment;
import org.xbase.android.adapter.JsonArrayAdapter;
import org.xbase.android.http.AjaxCallBack;
import org.xbase.android.http.XHttp;
import org.xbase.android.sample.R;

public class LeaveMessageListFragment extends BaseFragment {

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.listview_ptr, null);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(android.R.id.list);
        XHttp.get("http://shuiguorili.com:8080/leavemessage", new AjaxCallBack() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                listView.setAdapter(new JsonArrayAdapter(view.getContext(),t,R.layout.item_listview_t_i, new String[]{"context"}, new int[] {R.id.tv_name}));
            }
        });

    }

}
