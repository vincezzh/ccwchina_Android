package com.ccwchina.calendar;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.ccwchina.bean.CourseCalendar;
import com.ccwchina.common.CCWChinaConst;

public class CourseCalendarProcessor {
	public static Map<String, List<CourseCalendar>> getAMonthCourseCalendars(Calendar fromDate, Calendar toDate) throws Exception {
		Map<String, List<CourseCalendar>> dataSource = new HashMap<String, List<CourseCalendar>>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fromDateString = sdf.format(fromDate.getTime());
		String toDateString = sdf.format(toDate.getTime());
		
		String httpUrl = CCWChinaConst.WEBSITE_CONTEXT + "/mobile/calendar.htm?fromMonthDate=" + fromDateString + "&toMonthDate=" + toDateString;
        HttpGet request = new HttpGet(httpUrl);
        HttpClient httpClient = new DefaultHttpClient();
//        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + new Date());
        HttpResponse response = httpClient.execute(request);
        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String xml = EntityUtils.toString(response.getEntity());
//            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + new Date());
            parseCourseCalendarXMl(xml, dataSource);
//            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + new Date());
        }
        
		return dataSource;
	}
	
	public static void parseCourseCalendarXMl(String xml, Map<String, List<CourseCalendar>> dataSource) throws Exception {
		SAXParserFactory parserFactory = SAXParserFactory.newInstance();
		XMLReader xmlReader = parserFactory.newSAXParser().getXMLReader();
		xmlReader.setContentHandler(new CoursecalendarXMLHandler(dataSource));
		xmlReader.parse(new InputSource(new StringReader(xml)));
	}
	
//	public static void parseCourseCalendarXMl(String xml, Map<String, List<CourseCalendar>> dataSource) throws Exception {
//		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
//        Document doc = dbBuilder.parse(new ByteArrayInputStream(xml.getBytes()));
//
//        NodeList courseCalendarNodeList = doc.getElementsByTagName("courseCalendar");
//        CourseCalendar cc = null;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        for(int i = 0; i < courseCalendarNodeList.getLength(); i++) {
//        	cc = new CourseCalendar();
//        	Element courseCalendar = (Element)courseCalendarNodeList.item(i);
//        	String key = "";
//        	
//        	cc.setCourseCalendarId(courseCalendar.getElementsByTagName("courseCalendarId").item(0).getTextContent());
//        	key = courseCalendar.getElementsByTagName("classDate").item(0).getTextContent();
//        	cc.setClassDate(sdf.parse(key));
//        	
//        	Element classtime = (Element)courseCalendar.getElementsByTagName("classTime").item(0);
//        	cc.setClassTimeId(Integer.valueOf(classtime.getElementsByTagName("id").item(0).getTextContent()));
//        	cc.setClassTimeName(classtime.getElementsByTagName("name").item(0).getTextContent());
//        	
//        	cc.setPricePerPerson(Integer.valueOf(courseCalendar.getElementsByTagName("pricePerPerson").item(0).getTextContent()));
//        	cc.setSeatLeft(Integer.valueOf(courseCalendar.getElementsByTagName("seatLeft").item(0).getTextContent()));
//        	
//        	Element courseLocation = (Element)courseCalendar.getElementsByTagName("courseLocation").item(0);
//        	cc.setCourseLocationId(Integer.valueOf(courseLocation.getElementsByTagName("id").item(0).getTextContent()));
//        	cc.setCourseLocationName(courseLocation.getElementsByTagName("name").item(0).getTextContent());
//        	
//        	Element courseTrunkType = (Element)courseCalendar.getElementsByTagName("courseTrunkType").item(0);
//        	cc.setCourseTrunkTypeId(Integer.valueOf(courseTrunkType.getElementsByTagName("id").item(0).getTextContent()));
//        	cc.setCourseBranchTypeName(courseTrunkType.getElementsByTagName("name").item(0).getTextContent());
//        	cc.setFontColor(courseTrunkType.getElementsByTagName("fontColor").item(0).getTextContent());
//        	cc.setBackgroundColor(courseTrunkType.getElementsByTagName("backgroundColor").item(0).getTextContent());
//        	Element courseBranchType = (Element)courseTrunkType.getElementsByTagName("courseBranchType").item(0);
//        	cc.setCourseBranchTypeId(Integer.valueOf(courseBranchType.getElementsByTagName("id").item(0).getTextContent()));
//        	cc.setCourseBranchTypeName(courseBranchType.getElementsByTagName("name").item(0).getTextContent());
//        	
//        	Element courses = (Element)courseCalendar.getElementsByTagName("courses").item(0);
//        	List<Course> courseList = new ArrayList<Course>();
//        	NodeList courseNodeList = courses.getElementsByTagName("course");
//        	Course c = null;
//        	for(int j = 0; j < courseNodeList.getLength(); j++) {
//        		c = new Course();
//        		Element course = (Element)courseNodeList.item(j);
//        		
//        		c.setCourseId(course.getElementsByTagName("id").item(0).getTextContent());
//        		c.setCourseNameEn(course.getElementsByTagName("courseNameEn").item(0).getTextContent());
//        		c.setPictureOne(course.getElementsByTagName("pictureOne").item(0).getTextContent());
//        		
//        		courseList.add(c);
//        	}
//        	cc.setCourseList(courseList);
//        	
//        	if(dataSource.get(key) == null) {
//        		List<CourseCalendar> ccList = new ArrayList<CourseCalendar>();
//        		ccList.add(cc);
//        		dataSource.put(key, ccList);
//        	}else {
//        		dataSource.get(key).add(cc);
//        	}
//        }
//	}
}
