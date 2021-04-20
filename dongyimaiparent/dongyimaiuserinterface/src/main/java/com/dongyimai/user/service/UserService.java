package com.dongyimai.user.service;

import com.dongyimai.entity.PageResult;
import com.dongyimai.pojo.TbUser;

import java.util.List;

/**
 * 用户表服务层接口
 * @author Administrator
 *
 */
public interface UserService {

	public void add(TbUser user ,String code);
	public void sendCode(String phone);
	public boolean checkCode(String phone,String code);

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbUser> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	

	
	
	/**
	 * 修改
	 */
	public void update(TbUser user);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbUser findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbUser user, int pageNum, int pageSize);
	
}
