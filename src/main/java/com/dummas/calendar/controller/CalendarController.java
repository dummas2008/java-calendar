package com.dummas.calendar.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dummas.calendar.json.RespInfo;
import com.dummas.calendar.model.RecordInfo;
import com.dummas.calendar.service.WikiService;

import us.codecraft.xsoup.Xsoup;


@Controller
public class CalendarController {
	
	@Autowired
	private WikiService wikiService;

	@RequestMapping(value = "/CrawlWiki")
	@ResponseBody
	public RespInfo CrawlWiki(@RequestBody Map<String, Object> map) throws IOException
	{
		int result = -1;
		String year = (String) map.get("year");
		if (year.compareTo("all") == 0) {
			for (int i = -700; i < 2017; i++) {
				if (i == 0)
					continue;
				result = crawlWiki(Integer.toString(i));
				if(result !=0)
					break;
			}
		} else
			result = crawlWiki(year);
		if (result == 0)
			return new RespInfo(0, "success");
		else
			return new RespInfo(1, "failure");

	}
	
	public int crawlWiki(String year) 
	{
		String wikiUrl = "https://en.wikipedia.org/wiki/";
		int y = Integer.parseInt(year);
		if(y < 0)
			wikiUrl += (Math.abs(y) + "_BC");
		else
			wikiUrl += year;
		Document doc = null;
		int retryCount = 0;
		boolean isSuccess = false;
		while(true)
		{
			try {
				doc = Jsoup.connect(wikiUrl).get();
				isSuccess = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			retryCount++;
			if(retryCount > 3 || isSuccess)
				break;
		}
		
		if(doc == null)
			return -1;
/*		String result = Xsoup.compile("//*[@id=\"mw-content-text\"]/ul[3]/li").evaluate(doc).get();
		result = result.replaceAll("\\<[^>]*>","");*/
		
		Elements events = Xsoup.compile("//*[@id=\"mw-content-text\"]/h2[1]").evaluate(doc).getElements();
		String test = events.first().cssSelector();
		String id = events.first().child(0).id();
		if(id.compareToIgnoreCase("Events") != 0)
			events = Xsoup.compile("//*[@id=\"mw-content-text\"]/h2[2]").evaluate(doc).getElements();
		
		Element elem = events.first();
		
		String month = null;
		String day = null;
		String genre = null;
		String type = null, topic = null;
		while(elem != null)
		{
			if(elem.tagName().compareToIgnoreCase("h2") == 0)
			{
				if(elem.child(0).id().compareToIgnoreCase("Events") == 0)
					genre = "Events";
				else if(elem.child(0).id().compareToIgnoreCase("Births") == 0)
				{
					type = null;
					topic = null;
					genre = "Births";
				}
				else if(elem.child(0).id().compareToIgnoreCase("Deaths") == 0)
				{
					type = null;
					topic = null;
					genre = "Deaths";
				}
				else if(elem.child(0).id().compareToIgnoreCase("References") == 0)
					break;
				else
					break;
			}
			if(elem.tagName().compareToIgnoreCase("h3") == 0)
			{
				if(elem.child(0).id().compareToIgnoreCase("By_place") == 0)
					type = "By_place";
				else if(elem.child(0).id().compareToIgnoreCase("By_topic") == 0)
					type = "By_topic";
			}
			else if(elem.tagName().compareToIgnoreCase("h4") == 0)
			{
				topic = elem.child(0).text();
			}
			else if(elem.tagName().compareToIgnoreCase("ul") == 0)
			{
				for(Element child:elem.children())
				{
					String data = child.outerHtml();
					Element rec = child.children().first();
					if(rec != null)
						rec = rec.nextElementSibling();
					if(rec == null || rec.tagName().compareToIgnoreCase("ul") != 0)
					{
						if(child.childNodeSize() == 0)
							continue;
						String dateTBD = child.childNode(0).outerHtml().replaceAll("\\<[^>]*>","");
						Date date = parseDate(dateTBD);
						if(date != null)
						{
							Calendar cal = Calendar.getInstance();
							cal.setTime(date);
							month = Integer.toString(cal.get(Calendar.MONTH)+1);
							day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
						}
						RecordInfo record = new RecordInfo();
						record.setYear(year);
						record.setMonth(month);
						record.setDay(day);
						record.setGenre(genre);
						record.setCountry(topic);
						record.setLanguage("en");
						record.setContent_html(data);
						record.setContent(data.replaceAll("\\<[^>]*>",""));
						wikiService.addRecord(record);
					}
					else if(rec.tagName().compareToIgnoreCase("ul") == 0)
					{
						String dateTBD = data.replaceAll("\\<[^>]*>","");
						Date date = parseDate(dateTBD);
						if(date != null)
						{
							Calendar cal = Calendar.getInstance();
							cal.setTime(date);
							month = Integer.toString(cal.get(Calendar.MONTH)+1);
							day = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
						}
						
						for(Element subchild:rec.children())
						{
							data = subchild.outerHtml();				
							RecordInfo record = new RecordInfo();
							record.setYear(year);
							record.setMonth(month);
							record.setDay(day);
							record.setGenre(genre);
							record.setCountry(topic);
							record.setLanguage("en");
							record.setContent_html(data);
							record.setContent(data.replaceAll("\\<[^>]*>",""));
							wikiService.addRecord(record);
						}
					}
				}
			}
			elem = elem.nextElementSibling();
		}
		return 0;
	}
	
	
	public Date parseDate(String dateStr)
	{
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("MMMMMMMM d",java.util.Locale.US);
			Date d = sdf.parse(dateStr);
			return d;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value = "/GetWiki")
	@ResponseBody
	public RespInfo GetWiki(@RequestBody Map<String, Object> map) 
	{
		String year =(String) map.get("year");
		return new RespInfo(0, "success", wikiService.getRecordByYear(year));
	}
	
	@RequestMapping(value = "/Wiki")
	@ResponseBody
	public RespInfo Wiki() 
	{		
		return new RespInfo(0, "success", wikiService.getRecordByDay(new Date()));
	}
}
