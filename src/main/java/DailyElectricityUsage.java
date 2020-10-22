import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * stores all attributes for eucCalculations project
 * Example taken from https://mkyong.com/java/jackson-how-to-parse-json/
 *
 */
public class DailyElectricityUsage {
	private String userId;
    private String unit;
    private String siteTimeZoneId;
    private EucRates[] reads;
    private String seriesComponents;
    private RatePlan[] ratePlans;
    
    public DailyElectricityUsage(){
    	
    }
    
    public RatePlan[] getRatePlans(){
        RatePlan[] temp = ratePlans;
        return temp;
    }

    public void setRatePlans(RatePlan[] ratePlan){
        this.ratePlans = ratePlan;
    }

    public EucRates[] getReads(){
        EucRates[] reading = reads;
    	return reading;
    }
    
    //returns true if there is a repeat
    public boolean checkRepeat(EucRates obj) {
    	boolean check = false;
    	if(reads == null) {
    		return check;
    	}
    	EucRates[] storeUsage = reads;
    	HashMap<LocalDateTime, EucRates> whew = new HashMap<>();
    	for(int i=0;i<storeUsage.length;i++) {
    		whew.put(storeUsage[i].getLocalDateTime(), storeUsage[i]);
    	}
    	if(whew.get(obj.getLocalDateTime()) != null) {
    		check = true; 
    	}
    	
    	return check;
    }
    
	//sorts usage by localDateTime using treeMap
    public void sortDefault() {
    	if(reads != null) {
	    	ArrayList<EucRates> temp = new ArrayList<>();
	    	EucRates[] storeUsage = reads;
	    	HashMap<LocalDateTime, EucRates> whew = new HashMap<>();
	    	for(int i=0;i<storeUsage.length;i++) {
	    		whew.put(storeUsage[i].getLocalDateTime(), storeUsage[i]);
	    	}
	    	
	    	TreeMap<LocalDateTime, EucRates> sort = new TreeMap<>();
	    	sort.putAll(whew);
	    	
	    	Iterator whewIterator = sort.entrySet().iterator();
	    	while(whewIterator.hasNext()) {
	    		Map.Entry<LocalDateTime, EucRates> mapElement = (Map.Entry<LocalDateTime, EucRates>)whewIterator.next();
	    		
	    		temp.add(mapElement.getValue());
	    	}
	    	this.reads = temp.toArray(new EucRates[temp.size()]);
    	}
    }

    //instead of setting the reads value, adds an array to the end of reads
	public void appendReads(EucRates[] reading){
		ArrayList<EucRates> temp;
		if(reads != null) {
			temp = new ArrayList<EucRates>(Arrays.asList(reads));
		}else {
			temp = new ArrayList<EucRates>();
		}
		temp.addAll(Arrays.asList(reading));

		this.reads = temp.toArray(new EucRates[temp.size()]);
	}

    //instead of setting the reads value, 
	//adds a single EucRates object to the end of reads
	public void appendReads(EucRates reading){
		ArrayList<EucRates> temp;
		if(reads != null) {
			temp = new ArrayList<EucRates>(Arrays.asList(reads));
		}else {
			temp = new ArrayList<EucRates>();
		}
		temp.add(reading);

		this.reads = temp.toArray(new EucRates[temp.size()]);
	}

    public void setReads(EucRates[] reading){
        this.reads = reading;
    }
    
    //sets ALL values in reads array to given BigDecimal value
    public void setALLreads(BigDecimal value)
    {
    	for(int i = 0; i<reads.length; i++) {
    		this.reads[i].setValue(value);
    	}
    }

    @JsonIgnore
    public DailyElectricityUsage withReads(EucRates[] reading) {
    	DailyElectricityUsage newTemp = new DailyElectricityUsage();
    	newTemp.setRatePlans(ratePlans);
    	newTemp.setSeriesComponents(seriesComponents);
    	
    	ZoneId z = null;
    	if(siteTimeZoneId != null) {
    		z = ZoneId.of(siteTimeZoneId);
    	}
    	newTemp.setSiteTimeZoneId(z);
    	
    	newTemp.setUnit(unit);
    	newTemp.setuserId(userId);
    	newTemp.setReads(reading);
    	return newTemp;
    }


    public String getSeriesComponents(){
        return seriesComponents;
    }

    public void setSeriesComponents(String seriesComponents){
        this.seriesComponents = seriesComponents;
    }

	public String getUserId() {
		return userId;
	}

	public void setuserId(String userId) {
		this.userId = userId;
	}

    public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

    public String getSiteTimeZoneId() {
        return siteTimeZoneId;
    }

    
    public void setSiteTimeZoneId(String siteTimeZoneId) {
        if(siteTimeZoneId == null) {
        	this.siteTimeZoneId = null;
        } else {
	    	ZoneId z = ZoneId.of(siteTimeZoneId);
	    	this.siteTimeZoneId = z.toString();
        }
    }
    

    public void setSiteTimeZoneId(ZoneId z) {
    	if (z != null) {
			this.siteTimeZoneId = z.toString();
		}
    }


	@Override
	public String toString() {
		return "DailyElectricityUsage "
				+ "\n[userId=" + userId 
				+ "\nunit=" + unit 
				+ "\nsiteTimeZoneId=" + siteTimeZoneId 
				+ "\nreads=" + Arrays.toString(reads)
				+ "\nseriesComponents=" + seriesComponents 
				+ "\nratePlans=" + Arrays.toString(ratePlans) 
				+ "\n]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		DailyElectricityUsage other = (DailyElectricityUsage) obj;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
        if (unit == null){
            if(other.unit != null)
                return false;
        } else if (!unit.equals(other.unit))
            return false;
        if (siteTimeZoneId == null){
            if(other.siteTimeZoneId != null)
                return false;
        } else if (!siteTimeZoneId.equals(other.siteTimeZoneId))
            return false;
        if (seriesComponents == null){
            if(other.seriesComponents != null)
                return false;
        } else if (!seriesComponents.equals(other.seriesComponents))
            return false;
		if (!Arrays.equals(reads, other.reads))
			return false;
		if (!Arrays.equals(ratePlans, other.ratePlans))
				return false;



		return true;
	}
    
	//gets the current date and creates object with usage values of 1 every hour
    protected static DailyElectricityUsage createNewDaily() {
        //create electricity usage values)
    	int year = 0;
    	int month = 0;
    	int dayOfMonth = 0;
    	int hour = 0;
    	EucRates[] usageImport = new EucRates[24];
    	BigDecimal triangleSum = new BigDecimal("0");
    	ZonedDateTime iterate = eucCalculations.setTime(year, month, dayOfMonth, hour);

    	for(int i = 0; i<usageImport.length;i++)
    	{
    		ZonedDateTime startTime = iterate;
    		ZonedDateTime endTime = startTime.plusHours(1);

    		usageImport[i] = new EucRates(
    				startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    				endTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    				triangleSum
    				);
    		iterate = iterate.plusHours(1);
    	}

        DailyElectricityUsage newDaily = new DailyElectricityUsage();
        newDaily.setuserId("0000");
        newDaily.setUnit("KWH");
        newDaily.setSiteTimeZoneId(ZonedDateTime.now().getZone());
        newDaily.setReads(usageImport);

        return newDaily;
    }
    
    //gets the current date and creates object with usage values of 1 every hour
    //parameter is amount of days to create
    protected static DailyElectricityUsage createNewDaily(int days) {
        //create electricity usage values)
    	int year = 0;
    	int month = 0;
    	int dayOfMonth = 0;
    	int hour = 0;
    	EucRates[] usageImport = new EucRates[24*days];
    	BigDecimal triangleSum = new BigDecimal("0");
    	ZonedDateTime iterate = eucCalculations.setTime(year, month, dayOfMonth, hour);

    	for(int i = 0; i<usageImport.length;i++)
    	{
    		ZonedDateTime startTime = iterate;
    		ZonedDateTime endTime = startTime.plusHours(1);

    		usageImport[i] = new EucRates(
    				startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    				endTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    				triangleSum
    				);
    		iterate = iterate.plusHours(1);
    	}

        DailyElectricityUsage newDaily = new DailyElectricityUsage();
        newDaily.setuserId("0000");
        newDaily.setUnit("KWH");
        newDaily.setSiteTimeZoneId(ZonedDateTime.now().getZone());
        newDaily.setReads(usageImport);

        return newDaily;
    }

    //Creates DailyElectricityUsage object with specific month and day all hour usage values are 1
    protected static DailyElectricityUsage createNewDaily(int USERmonth, int USERdayOfMonth) {
        //create electricity usage values)
    	int year = 0;
    	int month = USERmonth;
    	int dayOfMonth = USERdayOfMonth;
    	int hour = 0;
    	EucRates[] usageImport = new EucRates[24];
    	BigDecimal triangleSum = new BigDecimal("0");
    	ZonedDateTime iterate = eucCalculations.setTime(year, month, dayOfMonth, hour);

    	for(int i = 0; i<usageImport.length;i++)
    	{
    		ZonedDateTime startTime = iterate;
    		ZonedDateTime endTime = startTime.plusHours(1);

    		usageImport[i] = new EucRates(
    				startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    				endTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    				triangleSum
    				);
    		iterate = iterate.plusHours(1);
    	}

        DailyElectricityUsage newDaily = new DailyElectricityUsage();
        newDaily.setuserId("0000");
        newDaily.setUnit("KWH");
        newDaily.setSiteTimeZoneId(ZonedDateTime.now().getZone());
        newDaily.setReads(usageImport);

        return newDaily;
    }

    //Creates DailyElectricityUsage object with specific month and day 
    //all hour usage values are 1, third parameter creates multiple days
    protected static DailyElectricityUsage createNewDaily(int USERmonth, int USERdayOfMonth, int numberOfDays) {
        //create electricity usage values)
    	int year = 0;
    	int month = USERmonth;
    	int dayOfMonth = USERdayOfMonth;
    	int hour = 0;
    	EucRates[] usageImport = new EucRates[24*numberOfDays];
    	BigDecimal triangleSum = new BigDecimal("0");
    	ZonedDateTime iterate = eucCalculations.setTime(year, month, dayOfMonth, hour);

    	for(int i = 0; i<usageImport.length;i++)
    	{
    		ZonedDateTime startTime = iterate;
    		ZonedDateTime endTime = startTime.plusHours(1);

    		usageImport[i] = new EucRates(
    				startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    				endTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
    				triangleSum
    				);
    		iterate = iterate.plusHours(1);
    	}

        DailyElectricityUsage newDaily = new DailyElectricityUsage();
        newDaily.setuserId("0000");
        newDaily.setUnit("KWH");
        newDaily.setSiteTimeZoneId(ZonedDateTime.now().getZone());
        newDaily.setReads(usageImport);

        return newDaily;
    }

	//finds usage value down to the year, month, day, hour and replaces 
	public void replaceReading(EucRates addedUsage) {
		ArrayList<EucRates> temp = new ArrayList<>();
		if(reads == null) {
    		reads = new EucRates[1];
    		reads[0] = addedUsage;
    	}else {
	    	EucRates[] storeUsage = reads;
	    	HashMap<LocalDateTime, EucRates> whew = new HashMap<>();
	    	for(int i=0;i<storeUsage.length;i++) {
	    		whew.put(storeUsage[i].getLocalDateTime(), storeUsage[i]);
	    	}
	    	if(whew.get(addedUsage.getLocalDateTime()) != null) {
	    		whew.replace(addedUsage.getLocalDateTime(), addedUsage);
	    	}
	    	
	    	TreeMap<LocalDateTime, EucRates> sort = new TreeMap<>();
	    	sort.putAll(whew);
	    	
	    	Iterator whewIterator = sort.entrySet().iterator();
	    	while(whewIterator.hasNext()) {
	    		Map.Entry<LocalDateTime, EucRates> mapElement = (Map.Entry<LocalDateTime, EucRates>)whewIterator.next();
	    		
	    		temp.add(mapElement.getValue());
	    	}
	    	this.reads = temp.toArray(new EucRates[temp.size()]);
    	}
    	
	}
}
