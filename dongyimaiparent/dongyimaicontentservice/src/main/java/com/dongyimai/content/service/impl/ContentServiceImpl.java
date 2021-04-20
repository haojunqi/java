package com.dongyimai.content.service.impl;
import java.util.List;

import com.dongyimai.contet.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.dongyimai.mapper.TbContentMapper;
import com.dongyimai.pojo.TbContent;
import com.dongyimai.pojo.TbContentExample;
import com.dongyimai.pojo.TbContentExample.Criteria;


import com.dongyimai.entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;

	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public List<TbContent> findByCatrgoryId(Long categoryId) {
		/*在redis中取数据*/
		List<TbContent> tbContents=(List<TbContent>)redisTemplate.boundHashOps("content").get(categoryId);
		if (tbContents == null){
			/*如果redis中没有，就从数据库中获取*/
			TbContentExample tbContentExample = new TbContentExample();
			Criteria criteria = tbContentExample.createCriteria();
			criteria.andCategoryIdEqualTo(categoryId);
			criteria.andStatusEqualTo("1");
			tbContentExample.setOrderByClause("sort_order");
			List<TbContent> tbContent = contentMapper.selectByExample(tbContentExample);
			redisTemplate.boundHashOps("content").put(categoryId,tbContent);
			return  tbContent;
		}else {
			return tbContents;
		}


	}

	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		/*将对应的广告在redis中删除*/
		redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		contentMapper.insert(content);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		/*获取修改之前的分类id*/
		TbContent tbContent = contentMapper.selectByPrimaryKey(content.getCategoryId());
		Long categoryId = tbContent.getCategoryId();
		redisTemplate.boundHashOps("content").delete(categoryId);
		if (categoryId == tbContent.getCategoryId()){
			redisTemplate.boundHashOps("content").delete(content.getCategoryId());
		}
		contentMapper.updateByPrimaryKey(content);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
