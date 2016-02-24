package com.thinksky.Face;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.thinksky.tox.R;

public class FaceView extends LinearLayout {

	Context context;
	ViewPager vp_content; //
	LinearLayout ll_navigate;

	// 表情对应的文字
	static String[] all_faces_to_string = {"[aoman]","[baiyan]","[bishi]","[bizui]","[cahan]","[caidao]","[chajin]","[cheer]","[chong]","[ciya]","[da]","[dabian]","[dabing]","[dajiao]",
            "[daku]","[dangao]","[danu]","[dao]","[deyi]","[diaoxie]","[e]","[fadai]","[fadou]","[fan]","[fanu]","[feiwen]","[fendou]","[gangga]","[geili]","[gouyin]","[guzhang]","[haha]",
            "[haixiu]","[haqian]","[hua]","[huaixiao]","[hufen]","[huishou]","[huitou]","[jidong]","[jingkong]","[jingya]","[kafei]","[keai]","[kelian]","[ketou]","[kiss]","[ku]",
            "[kuaikule]","[kulou]","[kun]","[lanqiu]","[lenghan]","[liuhan]","[liulei]","[liwu]","[love]","[ma]","[meng]","[nanguo]","[no]","[ok]","[peifu]","[pijiu]","[pingpang]",
            "[pizui]","[qiang]","[qinqin]","[qioudale]","[qiu]","[quantou]","[ruo]","[se]","[shandian]","[shengli]","[shenma]","[shuai]","[shuijiao]","[taiyang]","[tiao]","[tiaopi]",
            "[tiaosheng]","[tiaowu]","[touxiao]","[tu]","[tuzi]","[wabi]","[weiqu]","[weixiao]","[wen]","[woshou]","[xia]","[xianwen]","[xigua]","[xinsui]","[xu]","[yinxian]","[yongbao]",
            "[youhengheng]","[youtaiji]","[yueliang]","[yun]","[zaijian]","[zhadan]","[zhemo]","[zhuakuang]","[zhuanquan]","[zhutou]","[zuohengheng]","[zuotaiji]","[zuqiu]"
    };
	// 表情总数
	static int[] all_faces = {R.drawable.aoman,R.drawable.baiyan,R.drawable.bishi,R.drawable.bizui,R.drawable.cahan,R.drawable.caidao,R.drawable.chajin,R.drawable.cheer,R.drawable.chong,
            R.drawable.ciya,R.drawable.da,R.drawable.dabian,R.drawable.dabing,R.drawable.dajiao,R.drawable.daku,R.drawable.dangao,R.drawable.danu,R.drawable.dao,R.drawable.deyi,
            R.drawable.diaoxie,R.drawable.e,R.drawable.fadai,R.drawable.fadou,R.drawable.fan,R.drawable.fanu,R.drawable.feiwen,R.drawable.fendou,R.drawable.gangga,R.drawable.geili,
            R.drawable.gouyin,R.drawable.guzhang,R.drawable.haha,R.drawable.haixiu,R.drawable.haqian,R.drawable.hua,R.drawable.huaixiao,R.drawable.hufen,R.drawable.huishou,
            R.drawable.huitou,R.drawable.jidong,R.drawable.jingkong,R.drawable.jingya,R.drawable.kafei,R.drawable.keai,R.drawable.kelian,R.drawable.ketou,R.drawable.kiss,R.drawable.ku,
            R.drawable.kuaikule,R.drawable.kulou,R.drawable.kun,R.drawable.lanqiu,R.drawable.lenghan,R.drawable.liuhan,R.drawable.liulei,R.drawable.liwu,R.drawable.love,R.drawable.ma,
            R.drawable.meng,R.drawable.nanguo,R.drawable.no,R.drawable.ok,R.drawable.peifu,R.drawable.pijiu,R.drawable.pingpang,R.drawable.pizui,R.drawable.qiang,R.drawable.qinqin,
            R.drawable.qioudale,R.drawable.qiu,R.drawable.quantou,R.drawable.ruo,R.drawable.se,R.drawable.shandian,R.drawable.shengli,R.drawable.shenma,R.drawable.shuai,R.drawable.shuijiao,
            R.drawable.taiyang,R.drawable.tiao,R.drawable.tiaopi,R.drawable.tiaosheng,R.drawable.tiaowu,R.drawable.touxiao,R.drawable.tu,R.drawable.tuzi,R.drawable.wabi,R.drawable.weiqu,
            R.drawable.weixiao,R.drawable.wen,R.drawable.woshou,R.drawable.xia,R.drawable.xianwen,R.drawable.xigua,R.drawable.xinsui,R.drawable.xu,R.drawable.yinxian,R.drawable.yongbao,
            R.drawable.youhengheng,R.drawable.youtaiji,R.drawable.yueliang,R.drawable.yun,R.drawable.zaijian,R.drawable.zhadan,R.drawable.zhemo,R.drawable.zhuakuang,R.drawable.zhuanquan,
            R.drawable.zhutou,R.drawable.zuohengheng,R.drawable.zuotaiji,R.drawable.zuqiu
    };
	// 一页表情数（固定）
	static int one_faces_sum = 21;
	// 页数（表情总数除一页表情数）
	int index_sum = all_faces.length / one_faces_sum + 1;

	int[] one_faces; // 单页表情

	ArrayList<GridView> gridViews; //
	Work work;
	
	public FaceView(Context context, AttributeSet attrs,Work work) {
//        super(context);
		super(context, attrs);
		this.context = context;
		this.work = work;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.layout_faceview, this);
		vp_content = (ViewPager) findViewById(R.id.vp_content);
		ll_navigate = (LinearLayout) findViewById(R.id.ll_navigate);

		initViewPager();
	}

	private void initViewPager() {

		LayoutInflater inflater = LayoutInflater.from(context);

		gridViews = new ArrayList<GridView>();
		GridView gView;

		// 生成表情
		for (int i = 0; i < index_sum; i++) {
			gView = (GridView) inflater.inflate(R.layout.layout_faceview_gridview, null);

			List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
			final List<String> listItems_str = new ArrayList<String>();
			final List<Integer> listItems_id = new ArrayList<Integer>();

			// 生成一页表情
			for (int j = i * one_faces_sum; j < (i + 1) * one_faces_sum; j++) {
				if (j >= all_faces.length) { // 超出表情总数时，终止
					break;
				}
				System.out.println("添加第" + j + "个表情");
				Map<String, Object> listItem = new HashMap<String, Object>();
				listItem.put("image", all_faces[j]);
				listItems.add(listItem);

				listItems_str.add(all_faces_to_string[j]);
				listItems_id.add(all_faces[j]);
			}
			SimpleAdapter simpleAdapter = new SimpleAdapter(context, listItems,
					R.layout.item_faceview_gridview, new String[] { "image" },
					new int[] { R.id.iv_face });
			gView.setAdapter(simpleAdapter);
			gView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					work.onClick(listItems_id.get(arg2),listItems_str.get(arg2));
				}
			});

			gridViews.add(gView);

			// 每产生一页表情，增加一个导航点
			ImageView view = new ImageView(context);
			view.setBackgroundResource(R.drawable.insoft_point_selector);
			view.setEnabled(false);
			ll_navigate.addView(view);
			if (i == 0) { // 第一个为选中
				view.setEnabled(true);
			}
		}

		// 填充ViewPager的数据适配器
		PagerAdapter pagerAdapter = new PagerAdapter() {
			@Override
			public int getCount() {
				return gridViews.size();
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView((View) object);
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View view = gridViews.get(position);
				container.addView(view);
				return view;
			}
		};

		vp_content.setAdapter(pagerAdapter);
		vp_content
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int arg0) {
						// TODO Auto-generated method stub
						for (int i = 0; i < ll_navigate.getChildCount(); i++) {
							ImageView view = (ImageView) ll_navigate
									.getChildAt(i);
							if (i == arg0) {
								view.setEnabled(true);
								continue;
							}
							view.setEnabled(false);
						}
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
						// TODO Auto-generated method stub

					}
				});
	}
	
	public interface Work {
		public void onClick(int id, String item_str);
	};
}
