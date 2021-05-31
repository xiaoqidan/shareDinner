package com.hfy.dinner.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hfy.dinner.dao.FamilyDao;
import com.hfy.dinner.dao.OrderDao;
import com.hfy.dinner.dao.UserDao;
import com.hfy.dinner.repository.dto.OrderQueryDto;
import com.hfy.dinner.repository.pojo.Family;
import com.hfy.dinner.repository.pojo.Order;
import com.hfy.dinner.repository.pojo.User;
import com.hfy.dinner.repository.vo.OrderVo;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author hfy
 */
@Service
public class OrderService {
    @Resource
    private OrderDao orderDao;
    @Resource
    private UserDao userDao;
    @Resource
    private FamilyDao familyDao;


    public PageInfo<OrderVo> getOrderList(OrderQueryDto queryDto) {
        int page=queryDto.getOffset() / queryDto.getLimit()+1;
        PageHelper.startPage(page, queryDto.getLimit());
        List<Order> orders = orderDao.selectByDto(queryDto);
        PageInfo orderPageInfo = new PageInfo<>(orders);
        orderPageInfo.setList(convertVo(orders));
        return orderPageInfo;
    }

    public void insert(Order order) {
        order.setCreateTime(new Date());
        order.setStatus(1);
        orderDao.insert(order);
    }

    public PageInfo<OrderVo> getOrderByUserId(Integer userId) {
        QueryWrapper<Order> query = new QueryWrapper<>();
        query.eq("user_id", userId);
        List<Order> orders = orderDao.selectList(query);
        return new PageInfo<>(convertVo(orders));
    }

    private List<OrderVo> convertVo(List<Order> orders) {
        List<OrderVo> lists = new ArrayList<>();
        for (Order order : orders) {
            OrderVo orderVo = new OrderVo();
            BeanUtils.copyProperties(order, orderVo);
            User user = userDao.selectById(order.getUserId());
            Family family = familyDao.selectById(order.getFamilyId());
            orderVo.setFamilyName(family.getName());
            orderVo.setUserName(user.getName());
            orderVo.setPicture(family.getImgLocation());
            orderVo.setLocation(family.getLocation());
            lists.add(orderVo);
        }
        return lists;
    }

    public Order getOrderByUserIdAndFamilyId(Integer userId, Integer familyId) {
        List<Order> orders = orderDao.selectToday(userId, familyId);
        if (!CollectionUtils.isEmpty(orders)) {
            return orders.get(0);
        }
        return null;
    }

    public boolean update(Order order) {
        if (order.getUserId() == null) {
            System.out.println("订单 用户id 不能为空");
            return false;
        }
        if (order.getFamilyId() == null) {
            System.out.println("订单 店铺id 不能为空");
            return false;
        }
        orderDao.updateByOrder(order.getUserId(), order.getFamilyId());
        return true;
    }

    public List<Order> getSevenDay() {
        return orderDao.getSevenDay();
    }

    public List<Integer> getIndexData() {
        List<Integer> lists = new ArrayList<>();
        lists.add(userDao.selectCountUserDay(0));
        lists.add(userDao.selectCountUserDay(1));
        lists.add(orderDao.selectCountDay());
        lists.add(userDao.selectCountALL(0));
        lists.add(userDao.selectCountALL(1));
        lists.add(orderDao.selectCountAll());
        return lists;
    }
}
