import java.math.BigDecimal;
import java.time.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.annotation.*;


//https://www.tutorialspoint.com/java8/java8_datetime_api.htm
//https://www.java67.com/2016/04/how-to-convert-string-to-localdatetime-in-java8-example.html

public class EucRates{
    protected String startTime;
    protected String endTime;
    protected BigDecimal value;

    @JsonIgnore
    private ZonedDateTime startTIME_FORMAT;
    @JsonIgnore
    private ZonedDateTime endTIME_FORMAT;
    @JsonIgnore
    private DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public EucRates(){
        
    }

    @JsonCreator
    public EucRates(@JsonProperty("startTime") String start, @JsonProperty("endTime")String end, @JsonProperty("value") BigDecimal costOrKWH){
        startTime = start;
        endTime = end;
        value = costOrKWH;  //this will be the $cost per KWH in a daily usage rate object,
                            //or KWH spent in a daily electricity rate object

        startTIME_FORMAT = convertTimeString(start);
        endTIME_FORMAT = convertTimeString(end);
    }

    public String getStartTime(){
        return startTime;
    }

    public String getEndTime(){
        return endTime;
    }

    public BigDecimal getValue(){
        return value;
    }

    public void setValue(BigDecimal val){
        value = val;
    }

    public void setStartTime(String start){
        startTime = start;
        startTIME_FORMAT = convertTimeString(start);
    }

    public void setEndTime(String end){
        endTime = end;
        endTIME_FORMAT = convertTimeString(end);
    }
    
    @JsonIgnore
    public int getHour() {
    	return startTIME_FORMAT.getHour();
    }
    
    @JsonIgnore
    public int getMonth() {
    	return startTIME_FORMAT.getMonthValue();
    }
    
    @JsonIgnore
    public String getMonthString() {
    	return startTIME_FORMAT.getMonth().toString();
    }
    
    @JsonIgnore
    public int getDay() {
    	return startTIME_FORMAT.getDayOfMonth();
    }
    
    @JsonIgnore
    public int getDayOfYear() {
    	return startTIME_FORMAT.getDayOfYear();
    }
    
    @JsonIgnore
    public int getEndDay() {
    	return endTIME_FORMAT.getDayOfMonth();
    }
    
    @JsonIgnore
    public LocalDate getLocalDate() {
    	return startTIME_FORMAT.toLocalDate();
    }
    
    @JsonIgnore
    public LocalDateTime getLocalDateTime() {
    	return startTIME_FORMAT.toLocalDateTime();
    }
    

    private ZonedDateTime convertTimeString(String timeString){
    	ZonedDateTime timeParse = ZonedDateTime.parse(timeString, formatter);
    	
    	int year = timeParse.getYear();
    	int month = timeParse.getMonthValue();
    	int dayOfMonth = timeParse.getDayOfMonth();
    	int hour = timeParse.getHour();
    	
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

    @Override
    public String toString(){
        return "\nstartTime: " + startTime 
        + "\nendTime: " + endTime
        + "\nvalue: " + value + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        EucRates other = (EucRates) obj;
        if (startTime == null) {
            if (other.startTime != null)
                return false;
        } else if (!startTime.equals(other.startTime))
            return false;
        if (endTime == null){
            if(other.endTime != null)
                return false;
        } else if (!endTime.equals(other.endTime))
            return false;
        if (value == null){
            if(other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;

        return true;
    }
    @JsonIgnore
	public ZoneId getTimeZone() {
		return startTIME_FORMAT.getZone();
	}
}
