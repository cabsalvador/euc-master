import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class eucCalculations{
    private static MathContext mc = new MathContext(4);

    /*
    public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException{
        ObjectMapper mapper = new ObjectMapper();

        DailyElectricityUsage elecUsage;
        DailyElectricityUsage elecUsage1;
        DailyElectricityUsage NON_SUMMER = DailyElectricityUsage.createNewDaily(12,25, 5);
        DailyElectricityUsage SUMMER = DailyElectricityUsage.createNewDaily(6,28, 5);
        NON_SUMMER.setALLreads(new BigDecimal("1"));
		SUMMER.setALLreads(new BigDecimal("1"));

        try {
        	elecUsage = mapper.readValue(new File("Files/dailyElectricityUsage_2020_02_28.json"), DailyElectricityUsage.class);
        }catch(FileNotFoundException e) {
            throw new FileNotFoundException("File not imported");
        }
        try {
        	elecUsage1 = mapper.readValue(new File("Files/dailyusagebyME.json"), DailyElectricityUsage.class);
        }catch(FileNotFoundException e) {
            throw new FileNotFoundException("File not imported");
        }

        System.out.println("total usage from the example JSON file is: " + getTotal(elecUsage));
        System.out.println("total cost of the example JSON file is: " + getTotalCost(elecUsage));

        System.out.println("\ntotal usage from the file i made is: " + getTotal(elecUsage1));
        System.out.println("total cost from the file i made is: " + getTotalCost(elecUsage1));

        System.out.println("\nthis should compute an example object with NON-SUMMER rate: here is the total cost: " + getTotalCost(NON_SUMMER));
        System.out.println("Aaaand the day to day breakdown: " + getDailyCostString(NON_SUMMER));
        System.out.println("this should compute an example object with SUMMER rate: here is the total cost: " + getTotalCost(SUMMER));
        System.out.println("Aaaand the day to day breakdown: " + getDailyCostString(SUMMER));

        //lets test appending!
        SUMMER.appendReads(NON_SUMMER.getReads());
        System.out.println("\ntotal: " + getTotalCost(SUMMER)+ "\nbreakdown by day: " + getDailyCostString(SUMMER));

        //append single EucRates
        ZonedDateTime now = setTime(2020, 6, 26, 18);
        ZonedDateTime nextNow = now.plusHours(1);
        SUMMER.appendReads(new EucRates(
	        		now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
	        		nextNow.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
	        		new BigDecimal("1")
        			)
        		);
        System.out.println("\ntotal: " + getTotalCost(SUMMER)+ "\nbreakdown by day: " + getDailyALLString(SUMMER));
    }
    */


    public static BigDecimal getTotalCost(DailyElectricityUsage obj) {
    	if(obj.getReads() == null) {
    		return new BigDecimal("0");
    	}

    	BigDecimal sumTotal = new BigDecimal("0");
    	EucRates[] storeUsage = obj.getReads();
    	RatePlan[] storeRate = obj.getRatePlans();
    	RatePlan[] SUMMER = createSUMMERRatePlan();
    	RatePlan[] NON_SUMMER = createDefaultRatePlan();
        if(storeRate != null) {
        	for(int i=0;i<storeUsage.length;i++) {
        		int hour = storeUsage[i].getHour();
        		int j = 0;
        		while(hour != storeRate[j].getHour()) {
        			j++;
        		}

        		sumTotal = sumTotal.add((storeRate[j].getValue().multiply(storeUsage[i].getValue())), mc);
        	}
        }else {
	    	for(int i=0;i<storeUsage.length;i++) {
	        	//Summer is Jun 1 - Oct 1, else it's non summer
	    		if(5 < storeUsage[i].getMonth() && storeUsage[i].getMonth() < 10){
	                storeRate = SUMMER;
	            } else {
	                storeRate = NON_SUMMER;
	            }

	    		int hour = storeUsage[i].getHour();
	    		int j = 0;
	    		while(hour != storeRate[j].getHour()) {
	    			j++;
	    		}

	    		sumTotal = sumTotal.add((storeRate[j].getValue().multiply(storeUsage[i].getValue())), mc);
	    	}
        }
    	return sumTotal;
    }

    /*a double number that gives the total cost (usage*usage rate) ($/KWH*KWH)
    public static double getTotalCost(DailyElectricityUsage obj) {
    	BigDecimal sumTotal = new BigDecimal("0");
    	EucRates[] storeUsage = obj.getReads();
    	//EucRates[] storeDailyUsage = new EucRates[24];
    	ArrayList<EucRates> storeDailyUsage = new ArrayList<EucRates>();
    	for(int i=0;i<storeUsage.length;i++) {
    		storeDailyUsage.add(storeUsage[i]);
    		if(storeDailyUsage.size() % 24 == 0 && !storeDailyUsage.isEmpty()) {
    			EucRates[] temp = storeDailyUsage.toArray(new EucRates[storeDailyUsage.size()]);
    			//daily.append("\n" + storeUsage[i].getMonthString() +" " + storeUsage[i].getDay() + " Cost: " + getDailyCost(obj.withReads(temp)));
    			sumTotal = sumTotal.add(getDailyCost(obj.withReads(temp)));
    			storeDailyUsage = new ArrayList<EucRates>();
    		}
    		if(i+1==storeUsage.length && !storeDailyUsage.isEmpty()) {
    			EucRates[] temp = storeDailyUsage.toArray(new EucRates[storeDailyUsage.size()]);
    			//daily.append("\n" + storeUsage[i].getMonthString() +" " + storeUsage[i].getDay() + " Cost: " + getDailyCost(obj.withReads(temp)));
    			sumTotal = sumTotal.add(getDailyCost(obj.withReads(temp)));
    			storeDailyUsage = new ArrayList<EucRates>();
    		}
    	}
    	return sumTotal.doubleValue();
    }
    */

    /*
     * if you have an array of electricity readings and want the total per in 24 hour segments this string will give it to you
    public static String getTotalCostString(DailyElectricityUsage obj) {
    	StringBuilder daily = new StringBuilder();

    	//BigDecimal sumTotal = new BigDecimal("0");
    	EucRates[] storeUsage = obj.getReads();
    	//EucRates[] storeDailyUsage = new EucRates[24];
    	ArrayList<EucRates> storeDailyUsage = new ArrayList<EucRates>();
    	for(int i=0;i<storeUsage.length;i++) {
    		storeDailyUsage.add(storeUsage[i]);
    		if(storeDailyUsage.size() % 24 == 0 && !storeDailyUsage.isEmpty()) {
    			EucRates[] temp = storeDailyUsage.toArray(new EucRates[storeDailyUsage.size()]);
    			daily.append("\n" + storeUsage[i].getMonthString() +" " + storeUsage[i].getDay() + " Cost: " + getTotalCost(obj.withReads(temp)));
    			//sumTotal = sumTotal.add(getDailyCost(obj.withReads(temp)));
    			storeDailyUsage = new ArrayList<EucRates>();
    		}
    		if(i+1==storeUsage.length && !storeDailyUsage.isEmpty()) {
    			EucRates[] temp = storeDailyUsage.toArray(new EucRates[storeDailyUsage.size()]);
    			daily.append("\n" + storeUsage[i].getMonthString() +" " + storeUsage[i].getDay() + " Cost: " + getTotalCost(obj.withReads(temp)));
    			//sumTotal = sumTotal.add(getDailyCost(obj.withReads(temp)));
    			storeDailyUsage = new ArrayList<EucRates>();
    		}
    	}
    	return daily.toString();
    }
    */

    //if you have an array of electricity readings and want the total per day this string will give it to you
    public static String getDailyCostString(DailyElectricityUsage obj) {
    	StringBuilder daily = new StringBuilder();

    	if (obj.getReads() != null) {
			EucRates[] storeUsage = obj.getReads();
			HashMap<LocalDate, ArrayList<EucRates>> whew = new HashMap<>();
			for (int i = 0; i < storeUsage.length; i++) {
				ArrayList<EucRates> storeDailyUsage;
				if (whew.get(storeUsage[i].getLocalDate()) == null) {
					storeDailyUsage = new ArrayList<EucRates>();
				} else {
					storeDailyUsage = whew.get(storeUsage[i].getLocalDate());
				}
				storeDailyUsage.add(storeUsage[i]);
				whew.put(storeUsage[i].getLocalDate(), storeDailyUsage);
			}
			//sort hashmap by LocalDate maybe
			TreeMap<LocalDate, ArrayList<EucRates>> sort = new TreeMap<>();
			sort.putAll(whew);
			Iterator whewIterator = sort.entrySet().iterator();
			while (whewIterator.hasNext()) {
				Map.Entry<LocalDate, ArrayList<EucRates>> mapElement = (Map.Entry<LocalDate, ArrayList<EucRates>>) whewIterator
						.next();

				EucRates[] temp = mapElement.getValue().toArray(new EucRates[mapElement.getValue().size()]);
				daily.append("\n" + temp[0].getMonthString() + " " + temp[0].getDay() + " Cost: $"
						+ getTotalCost(obj.withReads(temp)));
				//daily.append("\n" + mapElement.getKey() + " Cost: " + getDailyCost(obj.withReads(temp)));
			}
			daily.append("\nTotal Usage Cost: $" + getTotalCost(obj));
		}else {
			daily.append("empty: NO USAGES STORED");
		}
		return daily.toString();
    }

    //method returns total usage of DailyElectricityUsage object 
    public static double getTotal(DailyElectricityUsage obj) {
    	if(obj.getReads() == null) {
    		return 0;
    	}
    	BigDecimal sumTotal = new BigDecimal("0");
    	EucRates[] storeUsage = obj.getReads();

    	for(int i=0;i<storeUsage.length;i++) {
    		sumTotal = sumTotal.add(storeUsage[i].getValue(), mc);
    	}
    	return sumTotal.doubleValue();
    }

    //returns string with days and  total usages for that day
    public static String getDailyTotalString(DailyElectricityUsage obj) {
    	StringBuilder daily = new StringBuilder();

    	if (obj.getReads() != null) {
			EucRates[] storeUsage = obj.getReads();
			HashMap<LocalDate, ArrayList<EucRates>> whew = new HashMap<>();
			for (int i = 0; i < storeUsage.length; i++) {
				ArrayList<EucRates> storeDailyUsage;
				if (whew.get(storeUsage[i].getLocalDate()) == null) {
					storeDailyUsage = new ArrayList<EucRates>();
				} else {
					storeDailyUsage = whew.get(storeUsage[i].getLocalDate());
				}
				storeDailyUsage.add(storeUsage[i]);
				whew.put(storeUsage[i].getLocalDate(), storeDailyUsage);
			}
			//sort hashmap by LocalDate maybe
			TreeMap<LocalDate, ArrayList<EucRates>> sort = new TreeMap<>();
			sort.putAll(whew);
			Iterator whewIterator = sort.entrySet().iterator();
			while (whewIterator.hasNext()) {
				Map.Entry<LocalDate, ArrayList<EucRates>> mapElement = (Map.Entry<LocalDate, ArrayList<EucRates>>) whewIterator
						.next();

				EucRates[] temp = mapElement.getValue().toArray(new EucRates[mapElement.getValue().size()]);
				daily.append("\n" + temp[0].getMonthString() + " " + temp[0].getDay() + " Daily Usage: "
						+ getTotal(obj.withReads(temp)) + "KWH");
				//daily.append("\n" + mapElement.getKey() + " Cost: " + getDailyCost(obj.withReads(temp)));
			}
			daily.append("\nTotal Usages: " + getTotal(obj) + "KWH");
		}else {
			daily.append("empty: NO USAGES STORED");
		}
		return daily.toString();
    }

    //returns string with daily total usages and daily cost
    public static String getDailyALLString(DailyElectricityUsage obj) {
    	StringBuilder daily = new StringBuilder();

    	if (obj.getReads() != null) {
			EucRates[] storeUsage = obj.getReads();
			HashMap<LocalDate, ArrayList<EucRates>> whew = new HashMap<>();
			for (int i = 0; i < storeUsage.length; i++) {
				ArrayList<EucRates> storeDailyUsage;
				if (whew.get(storeUsage[i].getLocalDate()) == null) {
					storeDailyUsage = new ArrayList<EucRates>();
				} else {
					storeDailyUsage = whew.get(storeUsage[i].getLocalDate());
				}
				storeDailyUsage.add(storeUsage[i]);
				whew.put(storeUsage[i].getLocalDate(), storeDailyUsage);
			}
			//sort hashmap by LocalDate maybe
			TreeMap<LocalDate, ArrayList<EucRates>> sort = new TreeMap<>();
			sort.putAll(whew);
			Iterator whewIterator = sort.entrySet().iterator();
			while (whewIterator.hasNext()) {
				Map.Entry<LocalDate, ArrayList<EucRates>> mapElement = (Map.Entry<LocalDate, ArrayList<EucRates>>) whewIterator
						.next();

				EucRates[] temp = mapElement.getValue().toArray(new EucRates[mapElement.getValue().size()]);
				daily.append("\n" + temp[0].getMonthString() + " " + temp[0].getDay() + "\nDaily Usage: "
						+ getTotal(obj.withReads(temp)) + "KWH"
                        + "\nCost: $" + getTotalCost(obj.withReads(temp)));
				//daily.append("\n" + mapElement.getKey() + " Cost: " + getDailyCost(obj.withReads(temp)));
			}
			daily.append("\nTotal Usage Cost: $" + getTotalCost(obj)
            + "\nTotal Usages: " + getTotal(obj)+ "KWH");
		}else {
			daily.append("empty: NO USAGES STORED");
		}
		return daily.toString();
    }

    public static RatePlan[] createSUMMERRatePlan(){
        //THIS CREATES SUMMER RATE PLAN
        RatePlan[] defaultRatePlan = new RatePlan[24];
    	int year = 0;
    	int month = 0;
    	int dayOfMonth = 0;
    	String[] dayPartition = {"Off-peak", "Peak", "Mid-Peak"};
    			/*
    			 * dayPartition[0] = "Off-Peak"
    			 * dayPartition[1] = "Peak"
    			 * dayPartition[2] = "Mid-Peak"
    			*/
    	String[] season = {"Summer", "non-Summer"};
    			/*
    			 * season[0] = "Summer"
    			 * season[1] = "non-Summer"
    			 */

    	//off-peak Midnight - 12 p.m.(noon)
    	for(int i=0;i<12;i++) {
    		ZonedDateTime iterate = setTime(year, month, dayOfMonth, i);
    		String startTime = iterate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    		ZonedDateTime iterate1 = iterate.plusHours(1);
    		String endTime = iterate1.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    		defaultRatePlan[i] = new RatePlan(startTime, endTime, new BigDecimal(".1209"));
    		defaultRatePlan[i].setSeason(season[0]);
    		defaultRatePlan[i].setRateTime(dayPartition[0]);

    	}
    	//Mid-peak 12 p.m. - 5 p.m.
    	for(int i=12;i<17;i++) {
    		ZonedDateTime iterate = setTime(year, month, dayOfMonth, i);
    		String startTime = iterate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    		ZonedDateTime iterate1 = iterate.plusHours(1);
    		String endTime = iterate1.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    		defaultRatePlan[i] = new RatePlan(startTime, endTime, new BigDecimal(".1671"));
    		defaultRatePlan[i].setSeason(season[0]);
    		defaultRatePlan[i].setRateTime(dayPartition[2]);

    	}
        //peak 5 p.m. - 8 p.m.
    	for(int i=17;i<20;i++) {
    		ZonedDateTime iterate = setTime(year, month, dayOfMonth, i);
    		String startTime = iterate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    		ZonedDateTime iterate1 = iterate.plusHours(1);
    		String endTime = iterate1.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    		defaultRatePlan[i] = new RatePlan(startTime, endTime, new BigDecimal(".2941"));
    		defaultRatePlan[i].setSeason(season[0]);
    		defaultRatePlan[i].setRateTime(dayPartition[1]);

    	}
    	//Mid-peak 8 p.m. - Midnight
    	for(int i=20;i<defaultRatePlan.length;i++) {
    		ZonedDateTime iterate = setTime(year, month, dayOfMonth, i);
    		String startTime = iterate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    		ZonedDateTime iterate1 = iterate.plusHours(1);
    		String endTime = iterate1.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    		defaultRatePlan[i] = new RatePlan(startTime, endTime, new BigDecimal(".1671"));
    		defaultRatePlan[i].setSeason(season[0]);
    		defaultRatePlan[i].setRateTime(dayPartition[2]);

    	}

    	return defaultRatePlan;
    }

    public static RatePlan[] createDefaultRatePlan(){
        //THIS CREATES NON-SUMMER RATE PLAN
        RatePlan[] defaultRatePlan = new RatePlan[24];
    	int year = 0;
    	int month = 0;
    	int dayOfMonth = 0;
    	String[] dayPartition = {"Off-peak", "Peak", "Mid-Peak"};
    			/*
    			 * dayPartition[0] = "Off-Peak"
    			 * dayPartition[1] = "Peak"
    			 * dayPartition[2] = "Mid-Peak"
    			*/
    	String[] season = {"Summer", "non-Summer"};
    			/*
    			 * season[0] = "Summer"
    			 * season[1] = "non-Summer"
    			 */

    	//off-peak Midnight - 5 p.m.
    	for(int i=0;i<17;i++) {
    		ZonedDateTime iterate = setTime(year, month, dayOfMonth, i);
    		String startTime = iterate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    		ZonedDateTime iterate1 = iterate.plusHours(1);
    		String endTime = iterate1.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    		defaultRatePlan[i] = new RatePlan(startTime, endTime, new BigDecimal(".1006"));
    		defaultRatePlan[i].setSeason(season[1]);
    		defaultRatePlan[i].setRateTime(dayPartition[0]);

    	}
    	//peak 5 p.m. - 8 p.m.
    	for(int i=17;i<20;i++) {
    		ZonedDateTime iterate = setTime(year, month, dayOfMonth, i);
    		String startTime = iterate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    		ZonedDateTime iterate1 = iterate.plusHours(1);
    		String endTime = iterate1.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    		defaultRatePlan[i] = new RatePlan(startTime, endTime, new BigDecimal(".1388"));
    		defaultRatePlan[i].setSeason(season[1]);
    		defaultRatePlan[i].setRateTime(dayPartition[0]);

    	}
    	//off-peak 8 p.m. - Midnight
    	for(int i=20;i<defaultRatePlan.length;i++) {
    		ZonedDateTime iterate = setTime(year, month, dayOfMonth, i);
    		String startTime = iterate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    		ZonedDateTime iterate1 = iterate.plusHours(1);
    		String endTime = iterate1.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    		defaultRatePlan[i] = new RatePlan(startTime, endTime, new BigDecimal(".1006"));
    		defaultRatePlan[i].setSeason(season[1]);
    		defaultRatePlan[i].setRateTime(dayPartition[0]);

    	}

    	return defaultRatePlan;
    }

    protected static ZonedDateTime setTime(int year, int month, int dayOfMonth, int hour) {
    	ZonedDateTime date6;
    	if(year == 0 && month == 0 && dayOfMonth == 0) {
    		ZonedDateTime date = ZonedDateTime.now();
        	ZonedDateTime date0 = date.withNano(0);         //set 0
            ZonedDateTime date1 = date0.withHour(hour);        //manipulate for project
            ZonedDateTime date2 =date1.withMinute(0);       //set 0
            date6 =date2.withSecond(0);       //set 0
    	} else {
	    	ZonedDateTime date = ZonedDateTime.now();
	    	ZonedDateTime date0 = date.withNano(0);         //set 0
	        ZonedDateTime date1 = date0.withHour(hour);        //manipulate for project
	        ZonedDateTime date2 =date1.withMinute(0);       //set 0
	        ZonedDateTime date3 =date2.withMonth(month);        //manipulate for project
	        ZonedDateTime date4 =date3.withSecond(0);       //set 0
	        if(year != 0) {
	        	ZonedDateTime date5 =date4.withYear(year);      //manipulate for project
	        	date6 =date5.withDayOfMonth(dayOfMonth);   //manipulate for project
	        } else {
	        	date6 = date4.withDayOfMonth(dayOfMonth); //manipulate for project
	        }
    	}
		return date6;
    }
}
