import javax.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/*

*This is the main Electricity Usage Calculator.

When initialized, it will create all the "pages" which are JPanel, and then add them to CardLayout.

*/
public class ElectricityUsageCalculator extends JPanel{

/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected static DailyElectricityUsage storage;
	protected static JList listOfTotals;
	protected static JList listOfData;
	protected static DefaultListModel modelOfListOfTotals;
	protected static DefaultListModel modelOfListOfData;
	protected static String labelsForList = String.format(" %-59s%-59s%-30s", "startTime: ","endTime:","usage:");
	protected static String labelsForRateList = String.format(" %-59s%-59s%-30s", "startTime: ","endTime:","$Cost/KWH:");

	//	All the cards that will be used in the program.
	private static String FRAME_TITLE = "Electricity Usage Calculator";
	private static String DASHBOARD_PAGE_CARD = "Dashboard";

	private JPanel cards;
	private DashboardPage dashboardPage;

	ElectricityUsageCalculator(){
		super(FRAME_TITLE);
		cards = new JPanel();
		cards.setLayout(new CardLayout());

		dashboardPage = new DashboardPage(this);
		cards.add(DASHBOARD_PAGE_CARD, dashboardPage);

		this.setSize(new Dimension(900, 600));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.getContentPane().add(cards);
		showDashboardPage();
		this.setVisible(true);
		this.setResizable(false);
	}

	public void showDashboardPage()
	{
		((CardLayout)cards.getLayout()).show(cards, DASHBOARD_PAGE_CARD);
	}
	public void exit(){
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}



	/*public void exit(){
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}*/
	
	protected void updateJList() {
		//updates the JList in the dashboard that shows the total usage and cost
		DailyElectricityUsage obj = ElectricityUsageCalculator.storage;
		if (obj.getReads() != null) {
			ElectricityUsageCalculator.modelOfListOfTotals.removeAllElements();
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
				ElectricityUsageCalculator.modelOfListOfTotals.addElement(temp[0].getMonthString() + " " + temp[0].getDay()); 
				ElectricityUsageCalculator.modelOfListOfTotals.addElement("Daily Usage: " + eucCalculations.getTotal(obj.withReads(temp)) + "KWH");
				ElectricityUsageCalculator.modelOfListOfTotals.addElement("Cost: $" + eucCalculations.getTotalCost(obj.withReads(temp)));
				//daily.append("\n" + mapElement.getKey() + " Cost: " + getDailyCost(obj.withReads(temp)));
			}
			ElectricityUsageCalculator.modelOfListOfTotals.addElement("Total Usage Cost: $" + eucCalculations.getTotalCost(obj));
			ElectricityUsageCalculator.modelOfListOfTotals.addElement("Total Usages: " + eucCalculations.getTotal(obj)+ "KWH");
		}else {
			ElectricityUsageCalculator.modelOfListOfTotals.addElement("empty: NO USAGES STORED");
		}
		ElectricityUsageCalculator.listOfTotals.revalidate();
	}

	public static void main(String[] args) {
		storage = new DailyElectricityUsage();
		storage.setUnit("KWH");

		ElectricityUsageCalculator electricityUsageCalculator = new ElectricityUsageCalculator();
	}


}
