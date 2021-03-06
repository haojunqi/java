package com.dongyimai.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.dongyimai.entity.PageResult;
import com.dongyimai.mapper.TbUserMapper;
import com.dongyimai.pojo.TbUser;
import com.dongyimai.pojo.TbUserExample;
import com.dongyimai.pojo.TbUserExample.Criteria;
import com.dongyimai.user.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.security.DigestException;
import java.util.*;

/**
 * 用户表服务实现层
 * @author Administrator
 *
 */
@Service(timeout = 600000)
public class UserServiceImpl implements UserService {
	@Autowired
	private TbUserMapper userMapper;
	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Destination sms_queue;

	@Autowired
	private Destination mail_queue;

	@Autowired
	private RedisTemplate redisTemplate;
	@Override
	public void sendCode(final String phone) {
		/*生成六位数随机数*/
		final String code=(int)(Math.random()*900000)+100000+"";
		System.out.println("验证码"+code);
		redisTemplate.boundHashOps("smsCode").put(phone,code);
		jmsTemplate.send(sms_queue, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("mobile",phone);
				mapMessage.setString("code",code);
				return mapMessage;
			}
		});

	}

	/*验证验证码是否正确*/

	@Override
	public boolean checkCode(String phone, String code) {
		String smsCode = (String) redisTemplate.boundHashOps("smsCode").get(phone);
		if (smsCode==null){
			return false;
		}
		if (code.equals(smsCode)){
			return true;
		}
		return false;
	}

	@Override
	public void add(final TbUser user, String code) {
		int insert = userMapper.insert(user);
		if (insert>0){
			jmsTemplate.send(mail_queue, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					MapMessage mapMessage = session.createMapMessage();
					mapMessage.setString("user",user.getEmail());
					mapMessage.setString("text","今天晚上已经成功了");
					return mapMessage;
				}
			});
		}
	}

	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbUser> page=   (Page<TbUser>) userMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/*
	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user){
		userMapper.updateByPrimaryKey(user);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbUser user, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		if(user!=null){			
						if(user.getUsername()!=null && user.getUsername().length()>0){
				criteria.andUsernameLike("%"+user.getUsername()+"%");
			}			if(user.getPassword()!=null && user.getPassword().length()>0){
				criteria.andPasswordLike("%"+user.getPassword()+"%");
			}			if(user.getPhone()!=null && user.getPhone().length()>0){
				criteria.andPhoneLike("%"+user.getPhone()+"%");
			}			if(user.getEmail()!=null && user.getEmail().length()>0){
				criteria.andEmailLike("%"+user.getEmail()+"%");
			}			if(user.getSourceType()!=null && user.getSourceType().length()>0){
				criteria.andSourceTypeLike("%"+user.getSourceType()+"%");
			}			if(user.getNickName()!=null && user.getNickName().length()>0){
				criteria.andNickNameLike("%"+user.getNickName()+"%");
			}			if(user.getName()!=null && user.getName().length()>0){
				criteria.andNameLike("%"+user.getName()+"%");
			}			if(user.getStatus()!=null && user.getStatus().length()>0){
				criteria.andStatusLike("%"+user.getStatus()+"%");
			}			if(user.getHeadPic()!=null && user.getHeadPic().length()>0){
				criteria.andHeadPicLike("%"+user.getHeadPic()+"%");
			}			if(user.getQq()!=null && user.getQq().length()>0){
				criteria.andQqLike("%"+user.getQq()+"%");
			}			if(user.getIsMobileCheck()!=null && user.getIsMobileCheck().length()>0){
				criteria.andIsMobileCheckLike("%"+user.getIsMobileCheck()+"%");
			}			if(user.getIsEmailCheck()!=null && user.getIsEmailCheck().length()>0){
				criteria.andIsEmailCheckLike("%"+user.getIsEmailCheck()+"%");
			}			if(user.getSex()!=null && user.getSex().length()>0){
				criteria.andSexLike("%"+user.getSex()+"%");
			}	
		}
		
		Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
