package com.dongyimai.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dongyimai.entity.Goods;
import com.dongyimai.entity.PageResult;
import com.dongyimai.mapper.*;
import com.dongyimai.pojo.*;
import com.dongyimai.pojo.TbGoodsExample.Criteria;
import com.dongyimai.sellergoods.service.GoodsService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sun.tracing.dtrace.Attributes;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {


	@Autowired
	private TbGoodsMapper goodsMapper;

	@Autowired
	private TbGoodsDescMapper tbGoodsDescMapper;

	@Autowired
	private TbItemCatMapper tbItemCatMapper;

	@Autowired
	private TbBrandMapper tbBrandMapper;
	@Autowired
	private TbSellerMapper tbSellerMapper;

	@Autowired
	private TbItemMapper tbItemMapper;

	@Override
	public List<TbItem> findItemByGoodsIdsAndStatus(Long[] ids, String status) {
		TbItemExample tbItemExample = new TbItemExample();
		TbItemExample.Criteria criteria = tbItemExample.createCriteria();
		criteria.andGoodsIdIn(Arrays.asList(ids));
		criteria.andStatusEqualTo(status);
		List<TbItem> tbItems = tbItemMapper.selectByExample(tbItemExample);
		return tbItems;
	}

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		/*设置商品添加后默认的状态未审核：0*/
		goods.getTbGoods().setAuditStatus("0");
		goodsMapper.insert(goods.getTbGoods());
		Long tbGoodsId = goods.getTbGoods().getId();
		goods.getTbGoodsDesc().setGoodsId(tbGoodsId);
		tbGoodsDescMapper.insert(goods.getTbGoodsDesc());
		/*判断是否启用规则，*/
		if("1".equals(goods.getTbGoods().getIsEnableSpec())){
			/*循环获取*/
			for (TbItem item:goods.getTbItemList()) {
				/*拼接title*/
				String title = goods.getTbGoods().getGoodsName();
				Map<String,String> specmap = JSON.parseObject(item.getSpec(),Map.class);
				for (String key: specmap.keySet()) {
				title+=" "+key+" "+specmap.get(key);
				}
				item.setTitle(title);

				/*获取添加的第一张图片信息*/
				List<Map> imageList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(),Map.class);
				if(imageList != null && imageList.size()>0){
					item.setImage((String) imageList.get(0).get("path"));
				}
				/*插入三级分类id*/
				item.setCategoryid(goods.getTbGoods().getCategory3Id());
				/*设置创建时间和修改时间*/
				item.setCreateTime(new Date());
				item.setUpdateTime(new Date());
				item.setGoodsId(goods.getTbGoods().getId());
				item.setSellerId(goods.getTbGoods().getSellerId());
				/*设置三级分类名*/
				TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
				item.setCategory(tbItemCat.getName());

				/*设置品牌名*/
				TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
				item.setBrand(tbBrand.getName());
				TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
				item.setSeller(tbSeller.getName());
				tbItemMapper.insert(item);
			}
		}else {
			/*如果不启用规格*/
			TbItem tbItem = new TbItem();
			tbItem.setPrice(goods.getTbGoods().getPrice());
			tbItem.setStatus("1");
			tbItem.setNum(10000);
			tbItem.setIsDefault("0");
			String title = goods.getTbGoods().getGoodsName();
			tbItem.setTitle(title);
			List<Map> imageList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(),Map.class);
			if(imageList != null && imageList.size()>0){
				tbItem.setImage((String) imageList.get(0).get("path"));
			}
			/*插入三级分类id*/
			tbItem.setCategoryid(goods.getTbGoods().getCategory3Id());
			/*设置创建时间和修改时间*/
			tbItem.setCreateTime(new Date());
			tbItem.setUpdateTime(new Date());
			tbItem.setGoodsId(goods.getTbGoods().getId());
			tbItem.setSellerId(goods.getTbGoods().getSellerId());
			/*设置三级分类名*/
			TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
			tbItem.setCategory(tbItemCat.getName());

			/*设置品牌名*/
			TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
			tbItem.setBrand(tbBrand.getName());
			TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
			tbItem.setSeller(tbSeller.getName());
			tbItemMapper.insert(tbItem);
		}

	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		goods.getTbGoods().setAuditStatus("0");
		goodsMapper.updateByPrimaryKey(goods.getTbGoods());

		tbGoodsDescMapper.updateByPrimaryKey(goods.getTbGoodsDesc());

		TbItemExample tbItemExample = new TbItemExample();
		TbItemExample.Criteria criteria = tbItemExample.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
		tbItemMapper.deleteByExample(tbItemExample);

		Long TbGoodsId = goods.getTbGoods().getId();
		if("1".equals(goods.getTbGoods().getIsEnableSpec())){
			/*循环获取*/
			for (TbItem item:goods.getTbItemList()) {
				/*拼接title*/
				String title = goods.getTbGoods().getGoodsName();
				Map<String,String> specmap = JSON.parseObject(item.getSpec(),Map.class);
				for (String key: specmap.keySet()) {
					title+=" "+key+" "+specmap.get(key);
				}
				item.setTitle(title);

				/*获取添加的第一张图片信息*/
				List<Map> imageList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(),Map.class);
				if(imageList != null && imageList.size()>0){
					item.setImage((String) imageList.get(0).get("path"));
				}
				/*插入三级分类id*/
				item.setCategoryid(goods.getTbGoods().getCategory3Id());
				/*设置创建时间和修改时间*/
				item.setCreateTime(new Date());
				item.setUpdateTime(new Date());
				item.setGoodsId(goods.getTbGoods().getId());
				item.setSellerId(goods.getTbGoods().getSellerId());
				/*设置三级分类名*/
				TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
				item.setCategory(tbItemCat.getName());

				/*设置品牌名*/
				TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
				item.setBrand(tbBrand.getName());

				TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
				item.setSeller(tbSeller.getName());
				tbItemMapper.insert(item);
			}
		}else {
			/*如果不启用规格*/
			TbItem tbItem = new TbItem();
			tbItem.setPrice(goods.getTbGoods().getPrice());
			tbItem.setStatus("1");
			tbItem.setNum(10000);
			tbItem.setIsDefault("0");
			String title = goods.getTbGoods().getGoodsName();
			tbItem.setTitle(title);
			List<Map> imageList = JSON.parseArray(goods.getTbGoodsDesc().getItemImages(),Map.class);
			if(imageList != null && imageList.size()>0){
				tbItem.setImage((String) imageList.get(0).get("path"));
			}
			/*插入三级分类id*/
			tbItem.setCategoryid(goods.getTbGoods().getCategory3Id());
			/*设置创建时间和修改时间*/
			tbItem.setCreateTime(new Date());
			tbItem.setUpdateTime(new Date());
			tbItem.setGoodsId(goods.getTbGoods().getId());
			tbItem.setSellerId(goods.getTbGoods().getSellerId());
			/*设置三级分类名*/
			TbItemCat tbItemCat = tbItemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
			tbItem.setCategory(tbItemCat.getName());

			/*设置品牌名*/
			TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
			tbItem.setBrand(tbBrand.getName());
			TbSeller tbSeller = tbSellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
			tbItem.setSeller(tbSeller.getName());
			tbItemMapper.insert(tbItem);
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(id);
		TbItemExample tbItemExample = new TbItemExample();
		TbItemExample.Criteria criteria = tbItemExample.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = tbItemMapper.selectByExample(tbItemExample);
		return new Goods(tbGoods,tbGoodsDesc,tbItems);

	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
			System.out.println(goods.getAuditStatus());
		PageHelper.startPage(pageNum, pageSize);
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(Long[] ids,String  status) {
		for (Long id: ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);
			goodsMapper.updateByPrimaryKey(tbGoods);


			TbItemExample tbItemExample = new TbItemExample();
			TbItemExample.Criteria criteria = tbItemExample.createCriteria();
			criteria.andGoodsIdEqualTo(id);
			List<TbItem> tbItems = tbItemMapper.selectByExample(tbItemExample);
			for (TbItem item: tbItems) {
				item.setStatus(status);
				tbItemMapper.updateByPrimaryKey(item);
			}


		}
	}
}
