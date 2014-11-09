
package in.mings.littledog;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class DeviceListActivity extends Activity {

    private ListView devicelist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_device_list);

        devicelist = (ListView) findViewById(R.id.listView);

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();


            if ((i & 1) == 0) {
                map.put("ItemImage", R.drawable.mypet_small);//图像资源的ID
            } else {
                map.put("ItemImage", R.drawable.mybag_small);//图像资源的ID
            }
            map.put("ItemTitle", "Level " + i);
            map.put("ItemText", "Finished in 1 Min 54 Secs, 70 Moves! ");
            listItem.add(map);
        }

        //生成适配器的Item和动态数组对应的元素
        SimpleAdapter listItemAdapter = new SimpleAdapter(this, listItem,//数据源
                R.layout.device_item,//ListItem的XML实现
                //动态数组与ImageItem对应的子项
                new String[]{"ItemImage", "ItemTitle", "ItemText"},
                //ImageItem的XML文件里面的一个ImageView,两个TextView ID
                new int[]{R.id.imageView, R.id.textView, R.id.textView2}
        );

        devicelist.setAdapter(listItemAdapter);
    }


}
