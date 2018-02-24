package com.march.lightadapter.annotation;

import com.march.lightadapter.LightAdapter;
import com.march.lightadapter.helper.LightLogger;
import com.march.lightadapter.module.FullSpanModule;
import com.march.lightadapter.module.HFModule;
import com.march.lightadapter.module.LoadMoreModule;
import com.march.lightadapter.module.TopLoadMoreModule;

import java.lang.reflect.Field;

/**
 * CreateAt : 2018/2/24
 * Describe :
 *
 * @author chendong
 */
public class AnnotationParser {

    public static final String TAG = AnnotationParser.class.getSimpleName();

    private static Field getAdapterField(Object target) {
        Field[] fields = target.getClass().getDeclaredFields();
        // find field
        Field adapterField = null;
        for (Field field : fields) {
            if (field.getType().equals(LightAdapter.class)) {
                adapterField = field;
                break;
            }
        }
        if (adapterField == null) {
            throw new IllegalArgumentException("at least need item layout id");
        }
        return adapterField;
    }

    public static void parse2(Object target, LightAdapter adapter) {
        Field adapterField = getAdapterField(target);
        //////////////////////////////  -- LoadMore --  //////////////////////////////
        LoadMore loadMoreAnno = adapterField.getAnnotation(LoadMore.class);
        if (loadMoreAnno != null) {
            int bottomPreLoadNum = loadMoreAnno.bottom();
            int topPreLoadNum = loadMoreAnno.top();
            if (bottomPreLoadNum >= 0) {
                adapter.addModule(new LoadMoreModule(bottomPreLoadNum));
            }
            if (topPreLoadNum >= 0) {
                adapter.addModule(new TopLoadMoreModule(topPreLoadNum));
            }
        }
        //////////////////////////////  -- Header Footer --  //////////////////////////////
        HeaderFooter hfAnno = adapterField.getAnnotation(HeaderFooter.class);
        if (hfAnno != null) {
            int headerLayoutId = hfAnno.header();
            int footerLayoutId = hfAnno.footer();
            if (headerLayoutId > 0 || footerLayoutId > 0) {
                adapter.addModule(new HFModule(adapter.getContext(), headerLayoutId, footerLayoutId));
            }
        }

        AdapterConfig itemTypeConfigAnno = adapterField.getAnnotation(AdapterConfig.class);
        if (itemTypeConfigAnno != null) {
            //////////////////////////////  -- full span --  //////////////////////////////
            FullSpanModule fullSpanModule = new FullSpanModule();
            int[] fullSpanTypes = itemTypeConfigAnno.fullSpanTypes();
            fullSpanModule.addFullSpanType(fullSpanTypes);
            adapter.addModule(fullSpanModule);
            //////////////////////////////  -- item type --  //////////////////////////////
            int defItemLayoutId = itemTypeConfigAnno.itemLayoutId();
            int[] itemTypes = itemTypeConfigAnno.itemTypes();
            int[] itemLayoutIds = itemTypeConfigAnno.itemLayoutIds();
            if (defItemLayoutId > 0) {
                adapter.addType(LightAdapter.TYPE_DEFAULT, defItemLayoutId);
            } else if (itemTypes.length != 0 && itemTypes.length == itemLayoutIds.length) {
                for (int i = 0; i < itemTypes.length; i++) {
                    adapter.addType(itemTypes[i], itemLayoutIds[i]);
                }
            } else {
                LightLogger.e(TAG, "itemTypes.length = " + itemTypes.length
                        + " ,itemLayoutIds.length = " + itemLayoutIds.length);
            }
        }
    }
}
