package com.dummas.calendar.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dummas.calendar.dao.WikiDao;
import com.dummas.calendar.model.RecordInfo;
import com.dummas.calendar.service.WikiService;

@Service("wikiService")
public class WikiServiceImpl implements WikiService {
	
	@Autowired
	WikiDao wikiDao;

	@Override
	public void addRecord(RecordInfo record) {
		wikiDao.addRecord(record);
	}

	@Override
	public List<RecordInfo> getRecordByYear(String year) {
		return wikiDao.getRecordByYear(year);
	}

	@Override
	public List<RecordInfo> getRecordByDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String month = Integer.toString(cal.get(Calendar.MONTH)+1);
		String day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
		return wikiDao.getRecordByDay(month, day);
	}

}
