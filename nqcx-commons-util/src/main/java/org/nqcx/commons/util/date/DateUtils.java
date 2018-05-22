/* 
 * Copyright 2014 nqcx.org All right reserved. This software is the 
 * confidential and proprietary information of nqcx.org ("Confidential 
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with nqcx.org.
 */

package org.nqcx.commons.util.date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author naqichuan 2014年8月14日 上午11:48:35
 */
public class DateUtils extends org.apache.commons.lang.time.DateUtils {

	private final static Logger logger = LoggerFactory.getLogger(DateUtils.class);

	/**
	 * <p>
	 * Returns current date
	 * </p>
	 * 
	 * @return java Date
	 */
	public static Date date() {
		return new Date();
	}


	/**
	 * 字符串转日期
	 *
	 * @param date
	 * @return
	 */
	public static Date parseDate(String date) {
		return parseDate(date, DateFormatUtils.DATE_FORMAT);
	}

	/**
	 * 字符串转日期指定格式日期
	 *
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date parseDate(String date, String pattern) {
		try {
			return parseDate(date, new String[]{pattern});
		} catch (ParseException e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 当前月份最后一天
	 * 
	 * @return
	 */
	public static String lastDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date());
		return lastDayOfMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));

	}

	/**
	 * 取得月份最后一天
	 * 
	 * @param year
	 * @param month
	 * @return MM-dd
	 */
	public static String lastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		return lastDayOfMonth(cal);
	}

	/**
	 * 取得月份最后一天
	 * 
	 * @param calendar
	 * @return MM-dd
	 */
	private static String lastDayOfMonth(Calendar calendar) {
		String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
		String day = String.valueOf(calendar
				.getActualMaximum(Calendar.DAY_OF_MONTH));
		return (month.length() == 1 ? "0" + month : month) + "-"
				+ (day.length() == 1 ? "0" + day : day);
	}

	/**
	 * 获取月份最后一天 (YYYY-MM-dd)
	 * @return
     */
	public static String lastDayAndYearOfMonth(){
		Calendar cal = Calendar.getInstance();
		String year =  String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		String day = String.valueOf(cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		return (year + "-" + (month.length() == 1 ? "0" + month : month) + "-"
				+ (day.length() == 1 ? "0" + day : day));
	}

	/**
	 * 获取当前月份第一天(YYYY-MM-dd)
	 * @return
     */
	public static String firstDayAndYearOfMonth(){
		Calendar cal = Calendar.getInstance();
		String year =  String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		String day = String.valueOf(cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		return (year + "-" + (month.length() == 1 ? "0" + month : month) + "-"
				+ (day.length() == 1 ? "0" + day : day));
	}

	/**
	 * 取得当前月份第一天
	 * 
	 * @return
	 */
	public static String firstDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date());
		return firstDayOfMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
	}

	/**
	 * 取得月份第一天
	 * 
	 * @param year
	 * @param month
	 * @return MM-dd
	 */
	public static String firstDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		return firstDayOfMonth(cal);
	}

	/**
	 * 取得月份第一天
	 * 
	 * @param calendar
	 * @return MM-dd
	 */
	private static String firstDayOfMonth(Calendar calendar) {
		String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
		String day = String.valueOf(calendar
				.getActualMinimum(Calendar.DAY_OF_MONTH));
		return (month.length() == 1 ? "0" + month : month) + "-"
				+ (day.length() == 1 ? "0" + day : day);
	}

	/**
	 * 取得日期所在周数
	 * 
	 * @param date
	 * @return 格式 "2011-52"
	 */
	public static String weekOfYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONTH);
		cal.setMinimalDaysInFirstWeek(7);
		cal.setTime(date);
		String week = String.valueOf(cal.get(Calendar.WEEK_OF_YEAR));

		return String.valueOf(cal.get(Calendar.YEAR)) + "-"
				+ (week.length() == 1 ? "0" + week : week);
	}

	/**
	 * 取得指定周的第一天
	 * 
	 * @param yearweek
	 * @return
	 */
	public static Date firstDayOfWeek(String yearweek) {
		String[] yws = yearweek.split("-");
		int year = Integer.parseInt(yws[0]);
		int week = Integer.parseInt(yws[1]);

		return firstDayOfWeek(year, week);
	}

	private static Date firstDayOfWeek(int year, int week) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DATE, 1);

		Calendar cal1 = (Calendar) cal.clone();
		cal1.add(Calendar.DATE, week * 7);

		return firstDayOfWeek(cal1.getTime());
	}

	/**
	 * 取得指定日期所在周的第一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date firstDayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setTime(date);
		c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
		return c.getTime();
	}

	/**
	 * 取得指定周的最后一天
	 * 
	 * @param yearweek
	 * @return
	 */
	public static Date lastDayOfWeek(String yearweek) {
		return addDays(firstDayOfWeek(yearweek), 6);
	}

	/**
	 * 取得指定周的最后一天
	 * 
	 * @param year
	 * @param week
	 * @return
	 */
	public static Date lastDayOfWeek(int year, int week) {
		return addDays(firstDayOfWeek(year, week), 6);
	}

	/**
	 * 取得指定日期所在周的最后一天
	 * 
	 * @param date
	 * @return
	 */
	public static Date lastDayOfWeek(Date date) {
		return addDays(firstDayOfWeek(date), 6);
	}

	public static void main(String[] args) {
		System.out.println(firstDayOfWeek("2012-10"));
		System.out.println(lastDayOfWeek("2012-10"));

		System.out.println(firstDayOfMonth(2012, 2));
		System.out.println(lastDayOfMonth(2012, 2));
		System.out.print(lastDayAndYearOfMonth());
		System.out.print(firstDayAndYearOfMonth());

	}

}
