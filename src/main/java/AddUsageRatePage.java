import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddUsageRatePage extends JPanel{
	private final AddUsageRate addUsageRate;
	private Box mainBox = Box.createVerticalBox();


	//Buttons
	private JButton cancelButton, enterButton, newRateButton;

	//Labels
	private JLabel titleLabel;
	private JLabel rateTypeLabel, startLabel, endLabel, rateAmountLabel;
	private JLabel offPeakLabel, midPeakLabel, peakLabel;

	//ComboBoxes
	private JComboBox rate_CB, startTime_CB, endTime_CB;
	private JTextField offPeakCB, midPeakCB, peakCB;

	//Panels
	private JPanel mainPanel, subHeaderPanel, spacePanel;
	private JPanel buttonPanel;
	private JPanel peaksPanel, peakSubpanel1, peakSubpanel2;
	private JPanel subPanel = new JPanel(new FlowLayout());

	//TextFields
	private JFormattedTextField rateField;

	//Scroll Pane
	private JScrollPane scrollPane;


	//Strings for Labels, Buttons, ComboBoxes
	private static String TITLE_LABEL = "Edit Electricity Usage Rates";

	private static String RATE_TYPE_LABEL = "  Rate Type  ";
	private static String START_LABEL = "Start Time";
	private static String END_LABEL = "End Time";
	private static String RATE_AMOUNT_LABEL = "   Rate   ";


	private static String[] rateString = {"Off-Peak", "Mid-Peak", "Peak"};
	private static String[] hourString = {"00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00",
											"11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00",
											"20:00", "21:00", "22:00", "23:00", "24:00"};


	String userRateStartTime, userRateEndTime, userRateType;
	int userRateInt;
	ArrayList<RatePlan> userRateTimes = new ArrayList<RatePlan>();;


	public AddUsageRatePage(final AddUsageRate addUsageRate) {
		this.addUsageRate = addUsageRate;

		declarations();

		//This is the Action Listener for the '+' that Creates and displays a new row of Rates
		newRateButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//userRateTimes = new ArrayList<RatePlan>();
						int peakTime = rate_CB.getSelectedIndex();
						BigDecimal value = new BigDecimal("0");
						//0 = "off-peak" 1= Mid-peak 2=peak
						if(peakTime == 0) {
							try {
								value = new BigDecimal(offPeakCB.getText()) ;
							} catch (NumberFormatException e1) {

								//int options = JOptionPane.showInputDialog((addUsageRate, "Please Select Rate Type"));
				    			//// 0=yes, 1=no, 2=cancel

								e1.printStackTrace();
							}
						}else if(peakTime == 1) {
							value = new BigDecimal(midPeakCB.getText()) ;
						}else {
							value = new BigDecimal(peakCB.getText()) ;

						}
						
						int startHour = Integer.parseInt(startTime_CB.getSelectedItem().toString().substring(0, 2));
						
						for(int i= startTime_CB.getSelectedIndex();i<endTime_CB.getSelectedIndex();i++) {
							ZonedDateTime startTime = eucCalculations.setTime(0, 0, 0, i);
							ZonedDateTime endTime = startTime.plusHours(1);
							RatePlan timePeriod = new RatePlan(
									startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
									endTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
									value
									);
							userRateTimes.add(timePeriod);
						}


						createNewRate(endTime_CB.getSelectedItem());
						mainPanel.revalidate();
						mainPanel.repaint();
						JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
						topFrame.pack();
					}

				});

		//This is the Action Listener for 'Cancel' Button
		cancelButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					addUsageRate.dispose();
				}
			});


		enterButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
					int peakTime = rate_CB.getSelectedIndex();
					BigDecimal value = new BigDecimal("0");
					//0 = "off-peak" 1= Mid-peak 2=peak
					if(peakTime == 0) {
						value = new BigDecimal(offPeakCB.getText()) ;
					}else if(peakTime == 1) {
						value = new BigDecimal(midPeakCB.getText()) ;
					}else {
						value = new BigDecimal(peakCB.getText()) ;

					}
					for(int i= startTime_CB.getSelectedIndex();i<endTime_CB.getSelectedIndex();i++) {
						ZonedDateTime startTime = eucCalculations.setTime(0, 0, 0, i);
						ZonedDateTime endTime = startTime.plusHours(1);
						RatePlan timePeriod = new RatePlan(
								startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
								endTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
								value
								);
						userRateTimes.add(timePeriod);
					}


					//sets user rate plan to entered values
					if(userRateTimes == null) {
						addUsageRate.dispose();
					}else {
						RatePlan[] userRatePlan = (RatePlan[]) userRateTimes.toArray(new RatePlan[userRateTimes.size()]);
						ElectricityUsageCalculator.storage.setRatePlans(userRatePlan);
					}
					
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

					addUsageRate.dispose();
				}
			});


/*		rateField.addKeyListener(new KeyListener()
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
*/
		mainBox.add(titleLabel); //Displays the Title
		spacePanel();
		spacePanel();
		createPeakValues();
		spacePanel();
		createSubHeaders(); //Displays the SubHeaders
		createNewRate(null);
		mainBox.add(mainPanel);
		createButtonPanel(); //Displays
		scrollPane = new JScrollPane(mainBox, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(850, 450));
		this.add(mainBox);
		//this.add(scrollPane);


	}

	//This method does most of the declarations
	private void declarations() {
		titleLabel = new JLabel(TITLE_LABEL);
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); //Aligns the title to the center
		rateTypeLabel = new JLabel(RATE_TYPE_LABEL);
		startLabel = new JLabel(START_LABEL);
		endLabel = new JLabel(END_LABEL);
		rateAmountLabel = new JLabel(RATE_AMOUNT_LABEL);
		offPeakLabel = new JLabel("Off-Peak");
		midPeakLabel = new JLabel("Mid-Peak");
		peakLabel = new JLabel("Peak");
		offPeakCB = new JTextField("0.0");
		midPeakCB = new JTextField("0.0");
		peakCB = new JTextField("0.0");
		offPeakCB.setColumns(7);
		midPeakCB.setColumns(6);
		peakCB.setColumns(7);

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0,1));

		cancelButton = new JButton("Cancel");
		enterButton = new JButton("Finish & Save");
		newRateButton = new JButton("+");
		newRateButton.setAlignmentX(Component.CENTER_ALIGNMENT);

	}

	//Creates and displays the SubHeaders 'Rate Type', 'Start', 'End', and 'Rate Amount'.
	private void createSubHeaders() {
		subHeaderPanel = new JPanel();
		subHeaderPanel.setLayout(new FlowLayout(FlowLayout.CENTER,60, 10));
		subHeaderPanel.add(rateTypeLabel);
		subHeaderPanel.add(startLabel);
		subHeaderPanel.add(endLabel);
//		subHeaderPanel.add(rateAmountLabel);
		mainBox.add(subHeaderPanel);

	}

	//Creates and displays the headers and fields to input the values for each type of Peak.
	private void createPeakValues() {
		peaksPanel = new JPanel();
		peakSubpanel1 = new JPanel();
		peakSubpanel2 = new JPanel();
		peaksPanel.setLayout(new GridLayout(2,1));
		peakSubpanel1.setLayout(new FlowLayout(FlowLayout.CENTER,60,0));
		peakSubpanel2.setLayout(new FlowLayout(FlowLayout.CENTER,50, 0));

		peakSubpanel1.add(offPeakLabel);
		peakSubpanel1.add(midPeakLabel);
		peakSubpanel1.add(peakLabel);
		peakSubpanel2.add(offPeakCB);
		peakSubpanel2.add(midPeakCB);
		peakSubpanel2.add(peakCB);

		peaksPanel.add(peakSubpanel1);
		peaksPanel.add(peakSubpanel2);
		mainBox.add(peaksPanel);

	}

	//Adds the Combo Boxes to the Main Panel and displays them.
	private void createNewRate(Object endTime){
		/* Comboboxes ******************/
		subPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,50,8));
		rate_CB = new JComboBox(rateString);
		startTime_CB = new JComboBox(hourString);
		endTime_CB = new JComboBox(hourString);

		//Sets the default text in ComboBox to 'blank'
		rate_CB.setSelectedItem(null);
		startTime_CB.setSelectedItem(endTime);
		endTime_CB.setSelectedItem(null);

		rateField = new JFormattedTextField("0.0");
		rateField.setColumns(7);
		subPanel.add(rate_CB);
		subPanel.add(startTime_CB);
		subPanel.add(endTime_CB);
//		subPanel.add(rateField);
		mainPanel.add(subPanel);

	}
	//Creates and displays bottom Panel containing '+', 'Cancel', and 'Finish & Save' Buttons
	//Adds '+', 'Cancel', and 'Save' button to the mainBox, and displays it.
	private void createButtonPanel() {
		mainBox.add(newRateButton);
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPanel.add(cancelButton);
		buttonPanel.add(enterButton);
		mainBox.add(buttonPanel);
	}

	//This method creates an empty space panel.
	private void spacePanel() {
		spacePanel = new JPanel();
		JLabel blanklabel = new JLabel(" ");
		spacePanel.add(blanklabel);
		mainBox.add(spacePanel);
	}
}
