package com.offcn.user.service;

import com.offcn.user.except.UserExisException;
import com.offcn.user.mapper.TMemberAddressMapper;
import com.offcn.user.mapper.TMemberMapper;
import com.offcn.user.pojo.TMember;
import com.offcn.user.pojo.TMemberAddress;
import com.offcn.user.pojo.TMemberAddressExample;
import com.offcn.user.pojo.TMemberExample;
import org.bouncycastle.crypto.generators.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServceImpl implements UserService{
    @Autowired(required = false)
    private TMemberMapper tMemberMapper;

    @Autowired
    private TMemberAddressMapper tMemberAddressMapper;


    @Override
    public void registerUser(TMember member) {
        TMemberExample tMemberExample = new TMemberExample();
        TMemberExample.Criteria criteria = tMemberExample.createCriteria();
        criteria.andLoginacctEqualTo(member.getLoginacct());
        List<TMember> tMembers = tMemberMapper.selectByExample(tMemberExample);
        if (tMembers!=null && tMembers.size()>0){
            throw new UserExisException();
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encode = encoder.encode(member.getUserpswd());
        member.setUserpswd(encode);
        member.setAuthstatus("0");
        member.setUsertype("0");
        member.setAccttype("2");
        tMemberMapper.insert(member);
    }

    @Override
    public TMember login(String loginacct, String userpswd) {
        /*对密码进行解码*/
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        /*根据手机号获取用户的信息*/
        TMemberExample tMemberExample = new TMemberExample();
        TMemberExample.Criteria criteria = tMemberExample.createCriteria();
        criteria.andLoginacctEqualTo(loginacct);
        List<TMember> tMembers = tMemberMapper.selectByExample(tMemberExample);
        /*根据手机号查询只会有一个值*/
        if (tMembers != null && tMembers.size()==1){
            TMember tMember = tMembers.get(0);
            /*对密码进行解码*/                   //没有加密的字符串    加密以后的字符串
            boolean matches = encoder.matches(userpswd, tMember.getUserpswd());
            return matches?tMember:null;
        }
        return null;
    }

    @Override
    public TMember findById(Integer id) {
        TMember member = tMemberMapper.selectByPrimaryKey(id);
        return member;
    }

    /*获取用户收件地址*/
    @Override
    public List<TMemberAddress> getAddress(Integer memberId) {
        TMemberAddressExample tMemberAddressExample = new TMemberAddressExample();
        TMemberAddressExample.Criteria criteria = tMemberAddressExample.createCriteria();
        criteria.andMemberidEqualTo(memberId);
        return tMemberAddressMapper.selectByExample(tMemberAddressExample);
    }
}
