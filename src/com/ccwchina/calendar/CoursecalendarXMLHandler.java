package com.ccwchina.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ccwchina.bean.Course;
import com.ccwchina.bean.CourseCalendar;

public class CoursecalendarXMLHandler extends DefaultHandler {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	Map<String, List<CourseCalendar>> dataSource = null;
	CourseCalendar coursecalendar = null;
	List<Course> courseList = null;
	Course course = null;
	String tagName = null;
	String parentTagName = null;
	
	public CoursecalendarXMLHandler(Map<String, List<CourseCalendar>> dataSource) {
		super();
		this.dataSource = dataSource;
	}
	
	public Map<String, List<CourseCalendar>> getDataSource() {
		return dataSource;
	}
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		tagName = localName;
		if (tagName.equals("courseCalendar")) {
			coursecalendar = new CourseCalendar();
		}else if(tagName.equals("courses")) {
			courseList = new ArrayList<Course>();
		}else if(tagName.equals("course")) {
			course = new Course();
			parentTagName = localName;
		}else if(tagName.equals("classTime")) {
			parentTagName = localName;
		}else if(tagName.equals("courseLocation")) {
			parentTagName = localName;
		}else if(tagName.equals("courseTrunkType")) {
			parentTagName = localName;
		}else if(tagName.equals("courseBranchType")) {
			parentTagName = localName;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		String value = new String(ch, start, length);
		if(tagName.equals("courseCalendarId")) {
			coursecalendar.setCourseCalendarId(value);
		}else if(tagName.equals("classDate")) {
			try {
				coursecalendar.setClassDate(sdf.parse(value));
			} catch (ParseException e) {}
		}else if(tagName.equals("id")) {
			if(parentTagName.equals("classTime")) {
				coursecalendar.setClassTimeId(Integer.valueOf(value));
			}else if(parentTagName.equals("courseLocation")) {
				coursecalendar.setCourseLocationId(Integer.valueOf(value));
			}else if(parentTagName.equals("courseTrunkType")) {
				coursecalendar.setCourseTrunkTypeId(Integer.valueOf(value));
			}else if(parentTagName.equals("courseBranchType")) {
				coursecalendar.setCourseBranchTypeId(Integer.valueOf(value));
			}else if(parentTagName.equals("course")) {
				course.setCourseId(value);
			}
		}else if(tagName.equals("name")) {
			if(parentTagName.equals("classTime")) {
				coursecalendar.setClassTimeName(value);
			}else if(parentTagName.equals("courseLocation")) {
				coursecalendar.setCourseLocationName(value);
			}else if(parentTagName.equals("courseTrunkType")) {
				coursecalendar.setCourseTrunkTypeName(value);
			}else if(parentTagName.equals("courseBranchType")) {
				coursecalendar.setCourseBranchTypeName(value);
			}
		}else if(tagName.equals("pricePerPerson")) {
			coursecalendar.setPricePerPerson(Integer.valueOf(value));
		}else if(tagName.equals("seatLeft")) {
			coursecalendar.setSeatLeft(Integer.valueOf(value));
		}else if(tagName.equals("fontColor")) {
			coursecalendar.setFontColor(value);
		}else if(tagName.equals("backgroundColor")) {
			coursecalendar.setBackgroundColor(value);
		}else if(tagName.equals("courseNameEn")) {
			course.setCourseNameEn(value);
		}else if(tagName.equals("pictureOne")) {
			course.setPictureOne(value);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		tagName = localName;
		if (tagName.equals("courseCalendar")) {
			if(coursecalendar.getClassDate() != null) {
				String key = sdf.format(coursecalendar.getClassDate());
				if(dataSource.get(key) == null) {
	        		List<CourseCalendar> ccList = new ArrayList<CourseCalendar>();
	        		ccList.add(coursecalendar);
	        		dataSource.put(key, ccList);
	        	}else {
	        		dataSource.get(key).add(coursecalendar);
	        	}
			}
		}else if(tagName.equals("courses")) {
			coursecalendar.setCourseList(courseList);
		}else if(tagName.equals("course")) {
			courseList.add(course);
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
}
