package com.dummas.calendar.controller;

import java.io.IOException;
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
		String year =(String) map.get("year");
		Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/"+year).get();
		
		String result = Xsoup.compile("//*[@id=\"mw-content-text\"]/ul[3]/li").evaluate(doc).get();
		result = result.replaceAll("\\<[^>]*>","");
		
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
			elem = elem.nextElementSibling();
		}
		return new RespInfo(0, "success", result);
	}
	
	@RequestMapping(value = "/GetWiki")
	@ResponseBody
	public RespInfo GetWiki(@RequestBody Map<String, Object> map) 
	{
		String year =(String) map.get("year");
		return new RespInfo(0, "success", wikiService.getRecordByYear(year));
	}
}
