package com.citi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * CumRetCalculator is concrete class to calculate cumulative return for given
 * base and asof date. This class also is included performance test for big data
 * input.
 * 
 * @author Navid Vaziri
 *
 */
public class CumRetCalculator {
	private Map<Date, Double> treeMap;

	/**
	 * Main method to create sample map, base Date and asof Date for testing. I
	 * defined 174 years difference (2289-2015) between base and asof date to
	 * check performance. Look at {@link CumRetCalculator#createMap()
	 * createMap}. I measured performance by calculating compute time of class
	 * instance creation and findCumReturn method. <br>
	 * The below is performance result in my system:</br>
	 * <br>
	 * Last Date of Map: Mon Mar 25 00:00:00 EDT 2289</br>
	 * CumReturn: -0.017491018813087567 ComputeTime(MiliSec): 74 MapSize:100005
	 * 
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/M/dd");
		Date base = sf.parse("2015/2/1");
		Date asof = sf.parse("2089/5/8");
		Map<Date, Double> sampleMap = createMap();

		long l1 = System.currentTimeMillis();

		CumRetCalculator c = new CumRetCalculator(sampleMap);
		double cumReturn = c.findCumReturn(asof, base);

		long l2 = System.currentTimeMillis();

		System.out.println(
				"CumReturn: " + cumReturn + "   ComputeTime(MiliSec): " + (l2 - l1) + "   MapSize:" + sampleMap.size());
	}

	/**
	 * Use treeMap to sort map by Date. If we use HashMap, the iteration is
	 * unpredictable. We cannot use Singleton design pattern here because
	 * singleton, by definition, is an object you want to be instantiated no
	 * more than once. While we are trying to feed parameters to the
	 * constructor, so a singleton with constructor which has parameters is not
	 * a singleton.
	 * 
	 * @param dailyReturns
	 */
	private CumRetCalculator(Map<Date, Double> dailyReturns) {
		treeMap = new TreeMap<>(dailyReturns);
	}

	/**
	 * Using Map key to iterate over date and retrieve the desirable value to
	 * compute CumReturn. Date value should be bigger than base value and equal
	 * or less than asof value.
	 * 
	 * @param asof
	 * @param base
	 * @return If asof date is equal or less than base date or we don't have any
	 *         date between them in Map, the return value is 0.
	 */
	double findCumReturn(Date asof, Date base) {
		Set<Date> setKey = treeMap.keySet();
		Iterator<Date> iterator = setKey.iterator();
		double d = 1;
		Date dateC = null;
		while (iterator.hasNext()) {
			dateC = iterator.next();
			if (base.compareTo(dateC) > 0)
				continue;
			if (asof.compareTo(dateC) < 0)
				break;
			d *= 1 + treeMap.get(dateC);
		}
		return d - 1;
	}

	/**
	 * Create sample Map as document example, but put date into map unordered to
	 * test TreeMap functionality. In second part to check performance, I add
	 * more 100000 date by loop which made it incremental one day by one day.
	 * Thus the last date is 2289/3/25.
	 * 
	 * @return
	 * @throws ParseException
	 */
	public static Map<Date, Double> createMap() throws ParseException {
		Map<Date, Double> dailyReturns = new HashMap<>();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/M/dd");
		dailyReturns.put(sf.parse("2015/6/10"), -0.12);
		dailyReturns.put(sf.parse("2015/4/15"), -0.10);
		dailyReturns.put(sf.parse("2015/4/10"), 0.15);
		dailyReturns.put(sf.parse("2015/2/10"), 0.05);
		dailyReturns.put(sf.parse("2015/1/10"), 0.10);

		Calendar c = Calendar.getInstance();
		c.setTime(sf.parse("2015/6/10"));
		for (int i = 0; i < 100000; i++) {
			c.add(Calendar.DAY_OF_YEAR, 1);
			dailyReturns.put(c.getTime(), 0.000001);
		}
		System.out.println("Last Date of Map: " + c.getTime());

		return dailyReturns;
	}
}
