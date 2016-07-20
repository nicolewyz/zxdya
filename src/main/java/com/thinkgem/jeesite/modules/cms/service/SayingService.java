/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.cms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.cms.dao.SayingDao;
import com.thinkgem.jeesite.modules.cms.entity.Saying;

/**
 * 文章Service
 * 
 */

@Service
@Transactional(readOnly = true)
public class SayingService extends CrudService<SayingDao, Saying> {

	//private static Logger logger = LoggerFactory.getLogger(SayingService.class);
	
	@Autowired
	private SayingDao sayingDao;
	
	/**
	 * 通过编号获取内容标题
	 * @return new Object[]{栏目Id,文章Id,文章标题}
	 */
	public Saying findByIds(String id) {
		Saying saying = sayingDao.findById(id);
		return saying;
	}
	
}
