## GitHub
- [GitHub地址      https://github.com/chendongMarch/QuickRv](https://github.com/chendongMarch/QuickRv)

## Gradle
- `compile 'com.march.quickrvlibs:quickrvlibs:2.0.9'`


## 配置图片加载
```java
RvQuick.init(new  QuickLoad() {
            @Override
            public void load(Context context, String url, ImageView view) {
                Glide.with(context).load(url).centerCrop().crossFade().into(view);
            }

            @Override
            public void loadSizeImg(Context context, String url, ImageView view, int w, int h, int placeHolder) {
                Glide.with(context).load(url).centerCrop().crossFade().into(view);
            }
        });

```

## RvViewHolder的使用
- 为了简化数据的载入,使用RvViewHolder作为统一的ViewHolder,并提供了简单的方法


```java
//RvViewHolder已经提供部分简化的方法,可以使用连式编程快速显示数据而且有足够的扩展性

//获取控件
public <T extends View> T getView(int resId)
public <T extends View> T getView(String resId)
//设置可见
public RvViewHolder setVisibility(int resId, int v)
//设置背景
public RvViewHolder setBg(int resId, int bgRes)
//文字
public RvViewHolder setText(int resId, String txt)
//图片
public RvViewHolder setImg(int resId, int imgResId)
public RvViewHolder setImg(int resId, Bitmap bit)
public RvViewHolder setImg(Context context, int resId, String url)
public RvViewHolder setImg(Context context, int resId, String url,int w,int h,int placeHolder)
//监听
public RvViewHolder setClickLis(int resId, View.OnClickListener listener)
```


## 快速构建适配器
### 单类型
```java
//一个简单的实现,实体类不需要再去实现RvQuickInterface接口
SimpleRvAdapter simpleAdapter =
new SimpleRvAdapter<Demo>(self, demos, R.layout.rvquick_item_a) {
            @Override
            public void bindData4View(RvViewHolder holder, Demo data, int pos) {
                holder.setText(R.id.item_a_tv, data.title);
            }
        };
```
### 多类型
```java
//调用addType()方法配置每种类型的layout资源
//实体类需要实现RvQuickInterface接口
TypeRvAdapter<Demo> typeAdapter =
new TypeRvAdapter<Demo>(context, data) {
            @Override
            public void bindData4View(RvViewHolder holder, Demo data, int pos, int type) {
                //根据类型绑定数据
                switch (type) {
                    case Demo.CODE_DETAIL:
                        holder.setText(R.id.item_quickadapter_type_title, data.getmDemoTitle()).setText(R.id.item_quickadapter_desc, data.getmDescStr());
                        break;
                    case Demo.JUST_TEST:
                        holder.setText(R.id.item_quickadapter_title, data.getmDemoTitle());
                        break;
                }
            }
            @Override
            public void bindListener4View(RvViewHolder holder, int type) {
                 //不使用可不实现
            }
        };
typeAdapter.addType(Demo.CODE_DETAIL, R.layout.item_quickadapter_type)
                .addType(Demo.JUST_TEST, R.layout.item_quickadapter);
```



## RvConverter 转换器
1. 解决Java内置对象(String,Integer这些对象是没有办法实现固定接口的)的数据转换问题，当需要对Java内置对象实现分类适配时使用。

2. 将Integer,String类型的list转换为List<RvQuickModel>
`public static <T> List<RvQuickModel> convert(List<T> list)`

3. 将Integer,String类型的数组转换为List<RvQuickModel>
`public static <T> List<RvQuickModel> convert(T[] list)`

4. 使用转换器将把指定对象包装成RvQuickModel,可以使用`public <T> T get()`获取包装的数据,这样对象就不需要实现固定接口了


```java
String[] strs = new String[]{"a","a","a","a","a","a","a","a","a","a"};
TypeRvAdapter<RvQuickModel> adapter =
      new TypeRvAdapter<RvQuickModel>(this, RvConverter.convert(strs)) {
      @Override
      public void bindData4View(RvViewHolder holder, RvQuickModel data, int pos, int type) {
                String s = data.<String>get();
                ...
      }
};
```


## 两种监听事件
- 单击事件 和 长按事件

```java
public void setOnItemClickListener(OnItemClickListener<RvViewHolder> mClickLis)

public void setOnItemLongClickListener(OnItemLongClickListener<RvViewHolder> mLongClickLis)
```


## 添加Header和Footer
- 资源Id设置(推荐使用这种方式,header 和 footer的数据配置可以在抽象方法中操作)

```java
quickAdapter.addHeaderOrFooter(R.layout.header,R.layout.footer,recyclerView);
```

- 使用加载好的View设置

```java
View headerView = getLayoutInflater().inflate(R.layout.rvquick_header, recyclerView,false)
View footerView = getLayoutInflater().inflate(R.layout.rvquick_footer, recyclerView,false)
quickAdapter.addHeaderOrFooter(header,footer);
```
- 抽象方法实现Header,Footer显示

```java
quickAdapter = new TypeRvAdapter<Demo>(self, demos) {
            @Override
            public void bindHeader(RvViewHolder header) {
               //给Header绑定数据和事件,不需要可以不实现
            }

            @Override
            public void bindFooter(RvViewHolder footer) {
               //给footer绑定数据和事件,不需要可以不实现
            }
        };
```
- 相关API

```java
// 添加header和footer
public void addHeaderOrFooter(int mHeaderRes, int mFooterRes,RecyclerView rv)
public void addHeaderOrFooter(View mHeaderView, View mFooterView)

// 隐藏和显示header和footer
public void setFooterEnable(boolean footerEnable)
public void setHeaderEnable(boolean headerEnable)

// 获取header数目,添加header之后数据和listview一样,数据会错位
public int getHeaderCount()
```

## 预加载

- 添加LoadMoreModule实现加载更多，当接近数据底部时会出发加载更多

- preLoadNum,表示提前多少个Item触发预加载，未到达底部时,距离底部preLoadNum个Item开始加载

- 每当到达底部时会触发加载，为防止多次加载，一次加载未完成时会禁止第二次加载，当加载结束之后调用finishLoad()，保证第二次加载可以进行。

```java
//方法
public void addLoadMoreModule(int preLoadNum,LoadMoreModule.OnLoadMoreListener loadMoreListener)
//当数据加载完毕时调用,才能使下次加载有效,防止重复加载
public void finishLoad()

eg:
quickAdapter.addLoadMoreModule(2, new LoadMoreModule.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("test","4秒后加载新的数据");
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i=0;i<10;i++){
                            demos.add(new Demo(i,"new "+i));
                        }
                        quickAdapter.notifyDataSetChanged();
                        quickAdapter.finishLoad();
                    }
                },4000);
            }
        });
```


## 使用adapterId
- 由于可以使用匿名内部类的形式快速实现,就无法通过类的instantOf方法区分,此时可以使用adapterId区分

```java
public int getAdapterId();

public void setAdapterId(int adapterId);
```


## 举个例子
```java
//内部类实现
quickAdapter = new TypeRvAdapter<Demo>(self, demos) {
            @Override
            public void bindData4View(RvViewHolder holder, Demo data, int pos, int type) {
               // 给控件绑定数据,必须实现
            }

            @Override
            public void bindListener4View(RvViewHolder holder, in super.bindListener4View(holder, type);
                //给控件绑定监听事件,不需要可以不实现
            }

            @Override
            public void bindHeader(RvViewHolder header) {
               //给Header绑定数据和事件,不需要可以不实现
            }

            @Override
            public void bindFooter(RvViewHolder footer) {
               //给footer绑定数据和事件,不需要可以不实现
            }
        };

//继承实现
public class MyAdapter extends TypeRvAdapter<Demo> {

    public MyAdapter(Context context, List<Demo> data) {
        super(context, data);
    }

    @Override
    public void bindData4View(RvViewHolder holder, Demo data, int pos, int type) {
       // 给控件绑定数据,必须实现
    }

    @Override
    public void bindListener4View(RvViewHolder holder, in super.bindListener4View(holder, type);
        //给控件绑定监听事件,不需要可以不实现
    }

    @Override
    public void bindHeader(RvViewHolder header) {
       //给Header绑定数据和事件,不需要可以不实现
    }

    @Override
    public void bindFooter(RvViewHolder footer) {
       //给footer绑定数据和事件,不需要可以不实现
    }
}
```