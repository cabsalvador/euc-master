import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

class MethodTest {
	private final eucCalculations uut = new eucCalculations();
	private final DailyElectricityUsage NON_SUMMER = DailyElectricityUsage.createNewDaily(12,25);
    private final DailyElectricityUsage SUMMER = DailyElectricityUsage.createNewDaily(6,28,5);
    private final DailyElectricityUsage TODAY = DailyElectricityUsage.createNewDaily();
    private final DailyElectricityUsage EMPTY = new DailyElectricityUsage();

	
	@Test
	void testgetTotalCost_1() {
		NON_SUMMER.setALLreads(new BigDecimal("1"));
		SUMMER.setALLreads(new BigDecimal("1"));
		TODAY.setALLreads(new BigDecimal("1"));
		
		
		BigDecimal index = uut.getTotalCost(NON_SUMMER);
		assertEquals(new BigDecimal("2.534"), index);
		
		NON_SUMMER.setRatePlans(uut.createSUMMERRatePlan());
		BigDecimal index1 = uut.getTotalCost(NON_SUMMER);
		assertEquals(new BigDecimal("3.836"), index1);
		
		BigDecimal index2 = uut.getTotalCost(SUMMER);
		assertEquals(new BigDecimal("19.19"), index2);
		
		BigDecimal index3 = uut.getTotalCost(TODAY);
		assertEquals(new BigDecimal("2.534"), index3);
		
        ZonedDateTime now = uut.setTime(2020, 6, 26, 18);
        ZonedDateTime nextNow = now.plusHours(1);
        TODAY.appendReads(new EucRates(
	        		now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
	        		nextNow.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
	        		new BigDecimal("1")
        			)
        		);
		BigDecimal index4 = uut.getTotalCost(TODAY);
		assertEquals(new BigDecimal("2.828"), index4);
		
		double index5 = uut.getTotal(TODAY);
		assertEquals(25, index5);
		
		String index6 = uut.getDailyCostString(TODAY);
		assertEquals("\n" + ZonedDateTime.now().getMonth() + " " + ZonedDateTime.now().getDayOfMonth()
				+ " Cost: $2.534"
				+ "\nJUNE 26 Cost: $0.2941"
				+ "\nTotal Usage Cost: $2.828", index6);
		
		BigDecimal index7 = uut.getTotalCost(EMPTY);
		assertEquals(new BigDecimal("0"), index7);
		
		double index8 = uut.getTotal(EMPTY);
		assertEquals(0.0, index8);
		
		String index9 = uut.getDailyCostString(EMPTY);
		assertEquals("empty: NO USAGES STORED", index9);
		
		String index9a = uut.getDailyALLString(EMPTY);
		assertEquals("empty: NO USAGES STORED", index9a);
		
		String index9b = uut.getDailyTotalString(EMPTY);
		assertEquals("empty: NO USAGES STORED", index9b);
		
		String index10 = uut.getDailyTotalString(TODAY);
		assertEquals("\n" + ZonedDateTime.now().getMonth() + " " + ZonedDateTime.now().getDayOfMonth()
				+ " Daily Usage: 24.0KWH"
				+ "\nJUNE 26 Daily Usage: 1.0KWH"
				+ "\nTotal Usages: 25.0KWH", index10);
		
		String index11 = uut.getDailyALLString(TODAY);
		assertEquals("\n" + ZonedDateTime.now().getMonth() + " " + ZonedDateTime.now().getDayOfMonth()
				+ "\nDaily Usage: 24.0KWH"
				+ "\nCost: $2.534"
				+ "\nJUNE 26"
				+ "\nDaily Usage: 1.0KWH"
				+ "\nCost: $0.2941"
				+ "\nTotal Usage Cost: $2.828"
				+ "\nTotal Usages: 25.0KWH", index11);
	}
}
