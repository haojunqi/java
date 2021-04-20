package com.dongyimai.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.dongyimai.entity.Goods;
import com.dongyimai.entity.PageResult;
import com.dongyimai.entity.Result;
import com.dongyimai.pojo.TbGoods;
import com.dongyimai.pojo.TbItem;
import com.dongyimai.sellergoods.service.GoodsService;
import dongyimai.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	@Reference
	private ItemPageService itemPageService;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Destination queue;

	@Autowired
	private Destination topic;

/*	*//*静态页面生成*//*
	@RequestMapping("/creat")
	public void cerateItemPage(Long goodsId){
		System.out.println(goodsId);
		itemPageService.cerateItemPage(goodsId);
	}*/
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		/*获取登录商家名称*/
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println(name+"----------");
		goods.getTbGoods().setSellerId(name);
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			/*itemSearchService.deleteByIds(Arrays.asList(ids));*/
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		goods.setAuditStatus("0");
		return goodsService.findPage(goods, page, rows);		
	}

	@RequestMapping("/login")
	public Map<String,String> login(){
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		Map<String,String> map = new HashMap<>();
		map.put("userName",name);
		return map;
	}

	/*商品审核*/
	@RequestMapping("updateStatus")
	public Result updateStatus(final Long[] ids, String status){

		try {
			goodsService.updateStatus(ids,status);
			if (status.equals("1")){
				List<TbItem> item = goodsService.findItemByGoodsIdsAndStatus(ids, status);
				if (item != null && item.size()>0){
					/*itemSearchService.ItemToSolr(item);*/
					final String item_str = JSON.toJSONString(item);
					jmsTemplate.send(queue, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(item_str);
						}
					});
				}
			/*	for (Long goodsId :ids ){
					itemPageService.cerateItemPage(goodsId);
				}*/
				/*id数组发送到消息队列*/
				jmsTemplate.send(topic, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(ids);
					}
				});
			}
			return new Result(true,"修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"修改失败");
		}
	}
	
}
