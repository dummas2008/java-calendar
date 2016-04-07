package com.dummas.calendar.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.dummas.calendar.model.RecordInfo;

@Repository("wikiDao")
public interface WikiDao {

	int addRecord(RecordInfo record);

	List<RecordInfo> getRecordByYear(String year);

}
