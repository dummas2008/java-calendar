package com.dummas.calendar.controller;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dummas.calendar.json.RespInfo;

import us.codecraft.xsoup.Xsoup;


@Controller
public class CalendarController {

	@RequestMapping(value = "/CrawlWiki")
	@ResponseBody
	public RespInfo GetVisitor(@RequestBody Map<String, Object> map) throws IOException
	{
		String year =(String) map.get("year");
		Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/"+year).get();
		
		String result = Xsoup.compile("//*[@id=\"mw-content-text\"]/ul[3]/li").evaluate(doc).get();
		result = result.replaceAll("\\<[^>]*>","");
		
		Elements events = doc.select("#Events");
		Elements content = doc.select("#mw-content-text");
		
		for(Element el:content)
		{
			String tag = el.text();
		}
		
		Element elem = content.first();
		
		while(elem != null)
		{
			elem = elem.nextElementSibling();
			if(elem == events.first())
			{
				String firstRec = elem.nextElementSibling().data();
			}
		}
		return new RespInfo(0, "success", result);
	}
}
