import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties
public class RatePlan extends EucRates{
    @JsonIgnore
    private String rateTime = "peak";

    @JsonIgnore
    private String season = "Summer";

    @JsonCreator
    public RatePlan(@JsonProperty("startTime") String start, @JsonProperty("endTime")String end, @JsonProperty("value") BigDecimal costOrKWH ) {
    	super(start, end, costOrKWH);
    }


    public void setRateTime(String rate){
        rateTime = rate;
    }

    public void setSeason(String summerOrNah){
        season = summerOrNah;
    }

    @Override
    public String toString(){
        return "rate time: " + this.rateTime + "startTime: " + this.startTime + "\n"
        + "endTime: " + this.endTime + "\n"
        + "value: " + this.value + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (! super.equals(obj))
            return false;
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        RatePlan other = (RatePlan) obj;
        if (rateTime == null) {
            if (other.rateTime != null)
                return false;
        } else if (!rateTime.equals(other.rateTime))
            return false;
        if (season == null){
            if(other.season != null)
                return false;
        } else if (!season.equals(other.season))
            return false;

        return true;
    }
}
