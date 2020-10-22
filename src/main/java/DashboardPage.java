import javax.swing.*;
import javax.swing.text.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.awt.*;
//import java.awt.event.ActionEvent;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
/*
This displays everything in the "Dashboard" page
*/
public class DashboardPage extends JPanel
{
	private final ElectricityUsageCalculator electricityUsageCalculator;
//Buttons
	private JButton viewEditData_button = new JButton("View Data");
	private JButton addElectricityUsage_button = new JButton("Add Usage Manually");
	private JButton changeElectricityRate_button = new JButton("Change Electricity Rate");
	private JButton importData_button = new JButton("Import Data");
	private JButton viewResults_button = new JButton("View Graph Results");
	private JButton exit_button = new JButton("Save and Exit");
	private JButton close_button = new JButton("Close");
	private JButton clearData_button = new JButton("Clear Data");
	//private JButton clearALL_button = new JButton("Clear All Data");
	//private JButton info_button = new JButton("Info");
//Panels
	private JPanel spacePanel;
	private JPanel centerPanel;
	private JPanel centerRowPanel;
	private JPanel buttonsPanel1, buttonsPanel2;
//Labels
	private JLabel titleLabel;
	private JLabel usageLabel;

//declare jlist
	private JScrollPane scrollOfList;

	//array that will store the reads from storage
	EucRates[] userData;

//Strings
	private static String TITLE_LABEL = "Electricity Usage Calculator";
	private static String USAGE_LABEL = "Enter Electricity Usage:";

//Font
	private Font titleFont = new Font("New Courier", Font.PLAIN, 30);

	public DashboardPage(final ElectricityUsageCalculator electricityUsageCalculator)
	{
		this.electricityUsageCalculator = electricityUsageCalculator;

		this.setLayout(new BorderLayout());
		titleLabel = new JLabel(TITLE_LABEL,SwingConstants.CENTER);
		usageLabel = new JLabel(USAGE_LABEL,SwingConstants.LEADING);
		titleLabel.setFont(titleFont);

		//declares Jlist in dashboard window
		ElectricityUsageCalculator.modelOfListOfTotals = new DefaultListModel();
		ElectricityUsageCalculator.listOfTotals = new JList();
		ElectricityUsageCalculator.listOfTotals.setModel(ElectricityUsageCalculator.modelOfListOfTotals);
		ElectricityUsageCalculator.listOfTotals.setLayoutOrientation(JList.VERTICAL);
		scrollOfList = new JScrollPane(ElectricityUsageCalculator.listOfTotals);

		//add components to window
		this.add(titleLabel,BorderLayout.NORTH);
		JPanel southPanel = new JPanel(new FlowLayout());
		southPanel.add(exit_button);
		southPanel.add(close_button);
		southPanel.add(clearData_button);
		this.add(southPanel, BorderLayout.SOUTH);
		CenterPanel();
		this.add(centerPanel, BorderLayout.CENTER);

		//declares buttons
		changeElectricityRate_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//AddElectricityRate addElectricityRate = new AddElectricityRate();
				//addElectricityRate.setVisible(true);
				AddUsageRate addUsageRate = new AddUsageRate();
				addUsageRate.setVisible(true);

			}
		});
		viewEditData_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ViewEditData viewEditData = new ViewEditData();
				viewEditData.setVisible(true);
			}
		});
		addElectricityUsage_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				AddElectricityUsage addElectricityUsage = new AddElectricityUsage();
				addElectricityUsage.setVisible(true);
			}
		});
		importData_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ImportData importData = new ImportData(electricityUsageCalculator);
				importData.setVisible(true);
			}
		});
		viewResults_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ViewResults viewResults = new ViewResults();
				//viewResults.setVisible(true);
			}
		});

		
		clearData_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				int userChoice = JOptionPane.showConfirmDialog(electricityUsageCalculator, "Are You Sure? Cannot be Undone");
				if(userChoice == 0) {
					electricityUsageCalculator.storage = new DailyElectricityUsage();
					electricityUsageCalculator.storage.setUnit("KWH");
					ElectricityUsageCalculator.modelOfListOfTotals.removeAllElements();
				}
				electricityUsageCalculator.updateJList();
				ElectricityUsageCalculator.modelOfListOfTotals.removeAllElements();
			}
		});
		
		close_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				//JFrame topFrame = (JFrame) SwingUtilities.getRoot(getRootPane());
				//topFrame.dispose();
				electricityUsageCalculator.exit();
			}
		});

		exit_button.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ObjectMapper mapper = new ObjectMapper();
				try {

		            // Java objects to JSON file
		            //making directories in java, hopefully works on both *nix and windows
		            File file = new File("Files");
		            file.mkdir();

		            DailyElectricityUsage newDay;
					if (ElectricityUsageCalculator.storage.getReads() != null) {
						if(ElectricityUsageCalculator.storage.getSiteTimeZoneId() == null) {
							ElectricityUsageCalculator.storage.setSiteTimeZoneId(
									ElectricityUsageCalculator.storage.getReads()[0].getTimeZone()
									);
						}

						newDay = ElectricityUsageCalculator.storage;
						//write current working
			            mapper.writeValue(new File("Files/dailyusage" +newDay.getReads()[0].getLocalDate() + ".json"),newDay);
					}


		        } catch (IOException emptyFile) {
		        	emptyFile.printStackTrace();
		        }
				//JFrame topFrame = (JFrame) SwingUtilities.getRoot(getRootPane());
				//topFrame.dispose();
				electricityUsageCalculator.exit();
			}
		});
	}


	private void CenterPanel()
	{
		centerPanel = new JPanel(new GridLayout(2,1));
		centerPanel.add(scrollOfList);
		centerRowPanel();
	}
	private void SpacePanel()
	{
		spacePanel = new JPanel();
		centerPanel.add(spacePanel);
	}
	private void centerRowPanel()
	{
		centerRowPanel = new JPanel(new GridLayout(2,1));

		buttonsPanel1();
		buttonsPanel2();

		centerPanel.add(centerRowPanel);
	}

	private void buttonsPanel1()
	{
		buttonsPanel1 = new JPanel();

		buttonsPanel1.add(usageLabel);
		buttonsPanel1.add(importData_button);
		buttonsPanel1.add(addElectricityUsage_button);

		centerRowPanel.add(buttonsPanel1);
	}
	private void buttonsPanel2()
	{
		buttonsPanel2 = new JPanel();

		buttonsPanel2.add(changeElectricityRate_button);
		buttonsPanel2.add(viewEditData_button);
		buttonsPanel2.add(viewResults_button);
		buttonsPanel2.add(clearData_button);
		centerRowPanel.add(buttonsPanel2);
	}
}
