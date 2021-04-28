package com.hfy.dinner.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.hfy.dinner.dao.CategoryDao;
import com.hfy.dinner.dao.FoodDao;
import com.hfy.dinner.repository.pojo.Category;
import com.hfy.dinner.repository.pojo.Food;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author hufayong
 * @date 2021/3/6 22:34
 */
@Service
public class CategoryService {
    @Resource
    private CategoryDao categoryDao;

    @Resource
    private FoodDao foodDao;

    public PageInfo<?> getCategoryById(Integer familyId) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("family_id", familyId);
        wrapper.orderByAsc("sort");
        List<Category> lists = categoryDao.selectList(wrapper);
        QueryWrapper<Food> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("family_id", familyId);
        if (!CollectionUtils.isEmpty(lists)) {
            for (Category category : lists) {
                wrapper1.eq("category_id", category.getId());
                category.setFoods(foodDao.selectList(wrapper1));
            }
        }
        return new PageInfo<>(lists);
    }
}