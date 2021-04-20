package com.dongyimai.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dongyimai.entity.PageResult;
import com.dongyimai.mapper.TbItemCatMapper;
import com.dongyimai.pojo.TbItemCat;
import com.dongyimai.pojo.TbItemCatExample;
import com.dongyimai.pojo.TbItemCatExample.Criteria;
import com.dongyimai.sellergoods.service.ItemCatService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品类目服务实现层
 * @author Administrator
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private RedisTemplate redisTemplate;


	@Override
	public void storeRedis() {
		List<TbItemCat> all = findAll();
		Map map =new HashMap();
		for (TbItemCat cat : all) {
			map.put(cat.getName(),cat.getTypeId());
		}
		redisTemplate.boundHashOps("itemcat").putAll(map);
	}

	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbItemCat> page=   (Page<TbItemCat>) itemCatMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);
		storeRedis();
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKey(itemCat);
		storeRedis();
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		TbItemCatExample tbItemCatExample = new TbItemCatExample();
		Criteria criteria = tbItemCatExample.createCriteria();
		for(Long id:ids){
			Criteria criteria1 = criteria.andParentIdEqualTo(id);
			List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(tbItemCatExample);
			itemCatMapper.deleteByPrimaryKey(id);
			storeRedis();
			if (tbItemCats.size()!=0){
				itemCatMapper.deleteByExample(tbItemCatExample);
			}
		}
	}
	
	
		@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		
		if(itemCat!=null){			
						if(itemCat.getName()!=null && itemCat.getName().length()>0){
				criteria.andNameLike("%"+itemCat.getName()+"%");
			}	
		}
		
		Page<TbItemCat> page= (Page<TbItemCat>)itemCatMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public PageResult findByParId(int pageNum, int pageSize,Long parentId) {
		PageHelper.startPage(pageNum, pageSize);
		TbItemCatExample tbItemCatExample = new TbItemCatExample();
		Criteria criteria = tbItemCatExample.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		Page<TbItemCat> page= (Page<TbItemCat>) itemCatMapper.selectByExample(tbItemCatExample);
		return  new PageResult(page.getTotal(), page.getResult());
/*		TbItemCatExample tbItemCatExample = new TbItemCatExample();
		Criteria criteria = tbItemCatExample.createCriteria();
		criteria.andParentIdEqualTo(parentId);

		return itemCatMapper.selectByExample(tbItemCatExample);*/
	}

	@Override
	public List selectItemCatList1(Long parentId) {
		/*根据条件查询*/
		TbItemCatExample tbItemCatExample = new TbItemCatExample();
		Criteria criteria = tbItemCatExample.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		return itemCatMapper.selectByExample(tbItemCatExample);
	}
}
