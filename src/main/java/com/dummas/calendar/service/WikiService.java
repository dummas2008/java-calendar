package com.dummas.calendar.service;

import java.util.Date;
import java.util.List;

import com.dummas.calendar.model.RecordInfo;

public interface WikiService {

	void addRecord(RecordInfo record);

	List<RecordInfo> getRecordByYear(String year);

	List<RecordInfo> getRecordByDay(Date date);

}
