package com.dongyimai.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dongyimai.entity.PageResult;
import com.dongyimai.entity.Result;
import com.dongyimai.pojo.TbUser;
import com.dongyimai.user.service.UserService;
import com.offcn.utils.PhoneFormatCheckUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户表controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Reference
	private UserService userService;
	
	/**
	 * 返回全部列表
	 * @return
	 */

	/*获取用户名*/
	@RequestMapping("getUsername")
	public Map getUsername(){
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		HashMap<String, String> map = new HashMap<>();
		map.put("username",name);
		return map;
	}

	@RequestMapping("/adduser")
	public Result add(@RequestBody TbUser user, String code){
		System.out.println("方法执行"+user.getPhone()+"=="+user.getEmail());
		boolean b = userService.checkCode(user.getPhone(), code);
		if (!b){
			return new Result(false,"请输入正确验证码");
		}
		System.out.println(b);
		System.out.println(new Date());
		user.setCreated(new Date());
		user.setUpdated(new Date());
		user.setUserLevel(1);
		String password = DigestUtils.md5Hex(user.getPassword());
		user.setPassword(password);
		try {
			userService.add(user,code);
			return new Result(true,"添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"添加失败");
		}
	}

	/*获取验证码*/
	@RequestMapping("sendCode")
	public Result sendCode(String phone){
		if (phone== null){
			return  new Result(false,"手机号不能为空");
		}
		if (!PhoneFormatCheckUtils.isChinaPhoneLegal(phone)){
			return new Result(false,"请输入正确的手机号");
		}
		try {
			userService.sendCode(phone);
			return new Result(true,"验证发发送成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"验证码发送失败");
		}
	}
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return userService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
/*	@RequestMapping("/add")
	public Result add(@RequestBody TbUser user){
		try {
			userService.add(user);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}*/
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
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
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			userService.delete(ids);
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
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);		
	}
	
}
