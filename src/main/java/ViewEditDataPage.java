import javax.swing.*; 
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowEvent;
/* 
This displays everything in the "View/Edit Data" page 
 */
public class ViewEditDataPage extends JPanel
{
	//declare an ViewEditData JFrame variable
	private final ViewEditData viewEditData; 

	
	//declare jlist
	private JScrollPane scrollOfList;
	
	//array that will store the reads from storage
	EucRates[] userData;
	
	public ViewEditDataPage(final ViewEditData viewEditData)
	{
		this.viewEditData = viewEditData;
		this.setLayout(new BorderLayout());
		
		ElectricityUsageCalculator.modelOfListOfData = new DefaultListModel();
		ElectricityUsageCalculator.listOfData = new JList();
		ElectricityUsageCalculator.listOfData.setModel(ElectricityUsageCalculator.modelOfListOfData);
		ElectricityUsageCalculator.listOfData.setLayoutOrientation(JList.VERTICAL);
		scrollOfList = new JScrollPane(ElectricityUsageCalculator.listOfData);
		
		this.add(scrollOfList);
		
		if(ElectricityUsageCalculator.storage.getReads() != null)
		{
			ElectricityUsageCalculator.modelOfListOfData.removeAllElements();
			userData = ElectricityUsageCalculator.storage.getReads();
			
			ElectricityUsageCalculator.modelOfListOfData.addElement("User Id: " + ElectricityUsageCalculator.storage.getUserId());
			
			ElectricityUsageCalculator.modelOfListOfData.addElement("Unit: " +ElectricityUsageCalculator.storage.getUnit());
			
			if (ElectricityUsageCalculator.storage.getSiteTimeZoneId() == null) {
				ElectricityUsageCalculator.storage.setSiteTimeZoneId(ElectricityUsageCalculator.storage.getReads()[0].getTimeZone());
			}
			ElectricityUsageCalculator.modelOfListOfData.addElement("Time Zone: " + ElectricityUsageCalculator.storage.getSiteTimeZoneId());
			
			ElectricityUsageCalculator.modelOfListOfData.addElement(ElectricityUsageCalculator.labelsForList);
			for(int i = 0; i < userData.length; i++)
			{
				ElectricityUsageCalculator.modelOfListOfData.addElement(" " + userData[i].getStartTime() + "         " + userData[i].getEndTime() + "         " + userData[i].getValue());
			}
			
			if (ElectricityUsageCalculator.storage.getRatePlans() != null) {
				//user rate plan
				RatePlan[] userRatePlan = ElectricityUsageCalculator.storage.getRatePlans();
				ElectricityUsageCalculator.modelOfListOfData.addElement(ElectricityUsageCalculator.labelsForRateList);
				for (int i = 0; i < userRatePlan.length; i++) {
					ElectricityUsageCalculator.modelOfListOfData.addElement(" " + userRatePlan[i].getStartTime()
							+ "         " + userRatePlan[i].getEndTime() + "         " + userRatePlan[i].getValue());
				} 
			}
		}
		
	}
}
