import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JComboBox;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.math.BigDecimal;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.text.*;
import static javax.swing.JOptionPane.showMessageDialog;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.lang.*;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import static javax.swing.ScrollPaneConstants.*;

/*
This displays everything in the "Add Electricity Usage" page
except for the textboxes the user types in
 */
public class AddElectricityUsagePage extends JPanel
{
	//declare an AddElectricityUsage JFrame variable
	private final AddElectricityUsage addElectricityUsage;

	//Buttons
	private JButton cancel_button;
	private JButton enter_button;
	private JButton addUsage_button;

	//Panels
	private JPanel spacePanel, spacePanel1;
	private JPanel eastPanel, westPanel,southPanel;
	private JPanel datePanel, hourPanel, usagePanel;

	private JTabbedPane centerPanel;
	private JPanel card1, card2;
	private JPanel createListPanel;


	//Labels
	private JLabel titleLabel;
	private JLabel dateLabel, hourLabel, usageLabel;
	private JLabel monthLabel, dayLabel, yearLabel;

	//JCombo Boxes
	private JComboBox timeComboBox, toComboBox;
	private JComboBox monthComboBox, dayComboBox;

	//Scroll for Data Jlist
	private JScrollPane paneOfData;

	//TextFields
	private JTextField yearField;
	private JTextField usageField;

	//Strings for labels, combo boxes, list
	private static String TITLE_LABEL = "Add Electricity Usage";

	private static String DATE_LABEL = "                                          Date of Usage:    ";
	private static String HOUR_LABEL = "                                          Hour of Usage:    ";
	private static String USAGE_LABEL = "         Amount of Electricity Usage (kW):    ";

	private static String MONTH_LABEL = "   month:  ";
	private static String DAY_LABEL = "    day:  ";
	private static String YEAR_LABEL = "    year:";

	private static String[] monthStrings = {"01", "02","03","04","05", "06","07","08","09", "10","11","12"};
	private static String[] dayStrings = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
			   								"13", "14", "15", "16", "17","18", "19", "20", "21", "22", "23", "24",
			   								"25", "26", "26", "27", "28", "29", "30", "31"};
	private static String[] timeStrings = { "00:00-01:00", "01:00-02:00", "02:00-03:00", "03:00-04:00",
											 "04:00-05:00", "05:00-06:00","06:00-07:00", "07:00-08:00",
											 "08:00-09:00", "09:00-10:00","10:00-11:00", "11:00-12:00",
											 "12:00-13:00", "13:00-14:00","14:00-15:00", "15:00-16:00",
											 "16:00-17:00", "17:00-18:00","18:00-19:00", "19:00-20:00",
											 "20:00-21:00", "21:00-22:00","22:00-23:00", "23:00-00:00"};

	String user_month_string , user_day_string, user_year_string, user_time_string, user_usage_string;
	int user_month_int, user_day_int, user_year_int, user_from_int,user_to_int;

	EucRates addedUsage;
	EucRates[] userReads;

	ZonedDateTime start_time, end_time;

	public AddElectricityUsagePage(final AddElectricityUsage addElectricityUsage)
	{
		this.addElectricityUsage = addElectricityUsage;
		this.setLayout(new BorderLayout(0, 0));

		//create labels
		titleLabel = new JLabel(TITLE_LABEL,SwingConstants.CENTER);

		dateLabel = new JLabel(DATE_LABEL,SwingConstants.TRAILING);
		hourLabel = new JLabel(HOUR_LABEL,SwingConstants.TRAILING);
		usageLabel = new JLabel(USAGE_LABEL,SwingConstants.TRAILING);

		monthLabel = new JLabel(MONTH_LABEL);
		dayLabel = new JLabel(DAY_LABEL);
		yearLabel = new JLabel(YEAR_LABEL);

		//create textfields
		yearField = new JTextField();
		usageField = new JTextField();
		usageField.setText("0.0");

		//the user can only type numbers and decimals in the usage textfield
		usageField.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e) { }
			public void keyReleased(KeyEvent e) { }
			public void keyTyped(KeyEvent e)
			{
				char c = e.getKeyChar();
				if(c != KeyEvent.VK_DELETE && c != KeyEvent.VK_BACK_SPACE && c != '.')
				{
					if(!(c >= '0' && c <= '9'))
					{
						e.consume();
					}
				}
			}
		});
		timeComboBox = new JComboBox(timeStrings);
		monthComboBox = new JComboBox(monthStrings);
		dayComboBox = new JComboBox(dayStrings);

		timeComboBox.setSelectedItem(null);
		monthComboBox.setSelectedItem(null);
		dayComboBox.setSelectedItem(null);

		//create buttons
		cancel_button = new JButton("Cancel");
		enter_button = new JButton("Finish");
		addUsage_button = new JButton("Add Electricity Usage");

		//what happens when cancel button is pressed
		cancel_button.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
		       addElectricityUsage.dispose();
		    }
		});

		//what happens when enter button is pressed
		enter_button.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
		    	user_month_string = (String)monthComboBox.getSelectedItem();
		    	user_day_string = (String)dayComboBox.getSelectedItem();
		    	user_year_string = yearField.getText();
		    	user_time_string = (String)timeComboBox.getSelectedItem();
		    	user_usage_string = usageField.getText();

		    	String userDate = user_month_string + user_day_string + user_year_string;

		    	if(ValidDate(userDate,user_year_string) && ValidTime(user_time_string) && ValidUsage(user_usage_string))
		    	{
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
		    		
		    		addElectricityUsage.dispose();
		    	}
		    }
		});

		addUsage_button.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
		    	user_month_string = (String)monthComboBox.getSelectedItem();
		    	user_day_string = (String)dayComboBox.getSelectedItem();
		    	user_year_string = yearField.getText();
		    	user_time_string = (String)timeComboBox.getSelectedItem();
		    	user_usage_string = usageField.getText();

		    	String userDate = user_month_string + user_day_string + user_year_string;

		    	if(ValidDate(userDate,user_year_string) && ValidTime(user_time_string) && ValidUsage(user_usage_string))
		    	{
		    		user_month_int = Integer.parseInt((String)monthComboBox.getSelectedItem());
			    	user_day_int = Integer.parseInt((String)dayComboBox.getSelectedItem());
			    	user_year_int = Integer.parseInt(yearField.getText());
			    	user_from_int = Integer.parseInt(user_time_string.substring(0, 2));
			    	user_to_int = Integer.parseInt(user_time_string.substring(6, 8));

		    		start_time = ZonedDateTime.of(user_year_int, user_month_int, user_day_int, user_from_int, 00, 00, 000, ZoneOffset.ofHours(-8));
		    		if(user_from_int == 23)
		    		{
		    			end_time = start_time.plusHours(1);
		    		}
		    		else
		    		{
		    			end_time = eucCalculations.setTime(user_year_int, user_month_int, user_day_int, user_to_int);
		    		}
			    	BigDecimal usage = new BigDecimal(user_usage_string);

		    		//for EucRates
		    		String user_startTime = start_time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
		    		String user_endTime = end_time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

		    		addedUsage = new EucRates(user_startTime, user_endTime,usage);
				
				if(ElectricityUsageCalculator.storage == null) 
		    		{
		    			ElectricityUsageCalculator.storage = new DailyElectricityUsage();
		    		}
		    		try
		    		{
		    			//if the usage the user is adding is for an hour than already has usage, throw exception
		    			if(ElectricityUsageCalculator.storage.checkRepeat(addedUsage)) 
		    			{
		    				throw new Exception();
		    			} 
		    			
		    			//no repeats, so append the usage to storage
		    			else
		    			{
			    			ElectricityUsageCalculator.storage.appendReads(addedUsage);
						
			    			//removes all elements in JList and displays all the user's reads in storage
			    			userReads = ElectricityUsageCalculator.storage.getReads();
			    			ElectricityUsageCalculator.modelOfListOfData.removeAllElements();
			    			ElectricityUsageCalculator.modelOfListOfData.addElement(ElectricityUsageCalculator.labelsForList);
			    			for(int i = 0; i < userReads.length; i++)
			    			{
			    				ElectricityUsageCalculator.modelOfListOfData.addElement(" " + userReads[i].getStartTime() + "         " + userReads[i].getEndTime() + "         " + userReads[i].getValue());
			    			}
			    			
		    			}
		    		}
		    		catch(Exception repeat)
		    		{
		    			int options = JOptionPane.showConfirmDialog(addElectricityUsage, "Repeat usage, replace?");
		    			//// 0=yes, 1=no, 2=cancel
		    			if(options == 0) 
		    			{
		    				ElectricityUsageCalculator.storage.replaceReading(addedUsage);

		    				//removes all elements in JList and displays all the user's reads in storage
		    				userReads = ElectricityUsageCalculator.storage.getReads();
		    				ElectricityUsageCalculator.modelOfListOfData.removeAllElements();
		    				ElectricityUsageCalculator.modelOfListOfData.addElement(ElectricityUsageCalculator.labelsForList);
			    			for(int i = 0; i < userReads.length; i++)
			    			{
			    				ElectricityUsageCalculator.modelOfListOfData.addElement(" " + userReads[i].getStartTime() + "         " + userReads[i].getEndTime() + "         " + userReads[i].getValue());
			    			}
		    			}
		    		}
		    	}
		    }
		});



		//NORTH BorderLayout
		this.add(titleLabel, BorderLayout.NORTH);

		//EAST BorderLayout
		eastPanel = new JPanel();
		this.add(eastPanel, BorderLayout.EAST);

		//WEST BorderLayout
		westPanel = new JPanel();
		westPanel.setLayout(new GridLayout(6,1));
		spacePanel = new JPanel();
		westPanel.add(spacePanel);
		westPanel.add(dateLabel);
		westPanel.add(hourLabel);
		westPanel.add(usageLabel);
		this.add(westPanel, BorderLayout.WEST);

		//SOUTH BorderLayout
		southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
		southPanel.add(cancel_button);
		southPanel.add(Box.createHorizontalGlue());
		southPanel.add(addUsage_button);
		southPanel.add(Box.createHorizontalGlue());
		southPanel.add(enter_button);
		this.add(southPanel, BorderLayout.SOUTH);

		//CENTER BorderLayout
		centerPanel = new JTabbedPane();
		card1 = new JPanel()
		{
			public Dimension getPreferredSize()
			{
				Dimension size = super.getPreferredSize();
				size.width += 100;
				return size;
			}
		};

		card1.setLayout(new GridLayout(11,1));
		//row1 in center
		CreateSpacePanel();
		//row2 in center
		CreateSpacePanel();
		//row3 in center
		CreateDatePanel();
		//row4 in center
		CreateSpacePanel();
		//row5 in center
		CreateHourPanel();
		//row6 in center
		CreateSpacePanel();
		//row7 in center
		CreateUsagePanel();

		card2 = new JPanel();
		CreateList();

		centerPanel.add("Add Electricity Usage",card1);
		centerPanel.add("View Data Entered",card2);

		this.add(centerPanel, BorderLayout.CENTER);
	}

/*An empty space Panel*/
	private void CreateSpacePanel()
	{
		spacePanel1 = new JPanel();
		card1.add(spacePanel1);
	}
/*Panel that contains date*/
	private void CreateDatePanel()
	{
		datePanel = new JPanel();
		datePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		datePanel.add(dateLabel);

		datePanel.add(monthLabel);
		datePanel.add(monthComboBox);

		datePanel.add(dayLabel);
		datePanel.add(dayComboBox);

		datePanel.add(yearLabel);
		datePanel.add(yearField);
		yearField.setColumns(5);

		card1.add(datePanel);
	}
/*Panel that contains hour of usage combo boxes*/
	private void CreateHourPanel()
	{
		hourPanel = new JPanel();
		hourPanel.setLayout(new FlowLayout(FlowLayout.LEADING,10,0));
		hourPanel.add(hourLabel);
		hourPanel.add(timeComboBox);
		card1.add(hourPanel);
	}

/*Panel that contains textbox for electricity usage*/
	private void CreateUsagePanel()
	{
		usagePanel = new JPanel();
		usagePanel.setLayout(new BoxLayout(usagePanel, BoxLayout.X_AXIS));
		usagePanel.add(usageLabel);
		usagePanel.add(usageField);
		usageField.setColumns(10);
		card1.add(usagePanel);
	}
/*Method that creates the JList*/

	private void CreateList()
	{
		createListPanel = new JPanel(new BorderLayout());
		ElectricityUsageCalculator.modelOfListOfData = new DefaultListModel();
		ElectricityUsageCalculator.listOfData = new JList();

		ElectricityUsageCalculator.listOfData.setModel(ElectricityUsageCalculator.modelOfListOfData);
		ElectricityUsageCalculator.listOfData.setLayoutOrientation(JList.VERTICAL);

		paneOfData = new JScrollPane(ElectricityUsageCalculator.listOfData);
		paneOfData.setPreferredSize(new Dimension(820,465));
		paneOfData.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
		createListPanel.add(paneOfData);
		card2.add(createListPanel);
	}

/*Checks if date user inputed is valid */
	private boolean ValidDate(String date,String year)
	{
		DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
		Date date1 = null;
		dateFormat.setLenient(false);
		//checks if year is only length 4
		if(year.length() != 4)
		{
			JOptionPane.showMessageDialog(this, "Invalid Date", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		//checks if date exists
		try
		{
			date1 = dateFormat.parse(date);
			return true;
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, "Invalid Date", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

/*Checks if user inputed a valid hour of usage*/
	private boolean ValidTime(String time)
	{
		/*the value of start time should always be bigger than end time
		 *with the exception of a start time of 23:00 that has an end time of 00:00
		*/
		if(time == null)
		{
			//JOptionPane.showMessageDialog(this, "Invalid Hour of Usage: Hour(s) not inputted.");
			JOptionPane.showMessageDialog(this, "Hour(s) of Usage not inputted.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		/*
		//substrings of times that just contain the hour
		String startHour = time.substring(0, 2);
    		String endHour = time.substring(6,8);

    		//accept if start is 23:00 and end is 00:00
		if (start == "23:00" && end =="00:00")
		{
			return true;
		}
		//don't accept if start and end time are same, or if start is greater than end time
		else if(start.compareTo(end) == 0 || start.compareTo(end) > 0)
		{
			JOptionPane.showMessageDialog(this, "Invalid Hour of Usage.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		//don't accept if start and end time are more than an hour apart
		else if(Integer.valueOf(endHour) - Integer.valueOf(startHour) > 1)
		{
			JOptionPane.showMessageDialog(this, "Start and end time should be an hour apart.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		*/
		else
		{
			return true;
		}
	}

/*Checks if the usage the user entered is valid.
 *Checks if user left textbox blank or has more than one decimal.
 */
	private boolean ValidUsage(String usage)
	{
		int count = 0;
		//user left textfield blank
		if (usage.length() == 0)
		{
			JOptionPane.showMessageDialog(this, "Invalid Electricity Usage.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		//counts how many decimals are in the string
		for(int i = 0; i < usage.length(); i++)
		{
			if(usage.charAt(i) == '.')
			{
				count++;
			}
		}
		//if there's more than one decimal, output error message
		if(count > 1)
		{
			JOptionPane.showMessageDialog(this, "Invalid Electricity Usage.", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else
		{
			return true;
		}
	}
}
