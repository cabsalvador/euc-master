import java.awt.event.ActionEvent;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class FileChooserButton extends EUCButton{
	private static final String BUTTON_NAME = "Open JSON File";
	final JFileChooser fileChooser = new JFileChooser();

	public FileChooserButton(final ElectricityUsageCalculator electricityUsageCalculator){
		super(BUTTON_NAME, electricityUsageCalculator);
		this.addActionListener(this);
	}
    @Override
    public void actionPerformed(ActionEvent e) {
		fileChooser.setDialogTitle("Select a .json file");
		FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .json files", "json");
			  fileChooser.setAcceptAllFileFilterUsed(false);
		  fileChooser.addChoosableFileFilter(restrict);
				int returnVal = fileChooser.showOpenDialog(null);
		
		RatePlan[] userRatePlan = null;
		if(ElectricityUsageCalculator.storage.getRatePlans() != null) {
			userRatePlan = ElectricityUsageCalculator.storage.getRatePlans();
		}

		if (returnVal == JFileChooser.APPROVE_OPTION) {
		      File file = fileChooser.getSelectedFile();
		      // what to do with the json file .
			  ObjectMapper mapper = new ObjectMapper();
			  try {
				  DailyElectricityUsage UserFile = mapper.readValue(file, DailyElectricityUsage.class);
				  Object[] options = {"Replace all Usage", "Append to Usage", "Cancel"}; 
				  int userChoice;
				  
				  if (ElectricityUsageCalculator.storage.getReads() != null) {
					userChoice = JOptionPane.showOptionDialog(getParent(), "What would you like to do with the file?",
							"File Options", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
							options, options[2]);
				}else {
					userChoice = 0;
				}
				//0=Replace 1=Add to Usage 2=cancel
				  if(userChoice==0) {
					  ElectricityUsageCalculator.storage = UserFile;
					  ElectricityUsageCalculator.storage.setRatePlans(userRatePlan);
					  
					  /*updates the JList in the dashboard that shows the total usage and cost
					  if(ElectricityUsageCalculator.storage.getReads() != null)
					  {
						  ElectricityUsageCalculator.modelOfListOfTotals.removeAllElements();
						  ElectricityUsageCalculator.modelOfListOfTotals.addElement(eucCalculations.getDailyALLString(ElectricityUsageCalculator.storage));
						  //ElectricityUsageCalculator.modelOfListOfTotals.removeElementAt(ElectricityUsageCalculator.modelOfListOfTotals.getSize() - 1);
						  ElectricityUsageCalculator.modelOfListOfTotals.addElement("Total Usage: " + eucCalculations.getTotal(ElectricityUsageCalculator.storage) + " kW");
						  //ElectricityUsageCalculator.modelOfListOfTotals.removeElementAt(ElectricityUsageCalculator.modelOfListOfTotals.getSize() - 1);
					  }
					  ElectricityUsageCalculator.listOfTotals.revalidate();
					  */
				  }else if(userChoice ==1) {
					  if(ElectricityUsageCalculator.storage == null) {
						  ElectricityUsageCalculator.storage = UserFile;
						  ElectricityUsageCalculator.storage.setRatePlans(userRatePlan);

					  }else {
						  ElectricityUsageCalculator.storage.appendReads(UserFile.getReads());
					  }
					  /*updates the JList in the dashboard that shows the total usage and cost
					  if(ElectricityUsageCalculator.storage.getReads() != null)
					  {
						  ElectricityUsageCalculator.modelOfListOfTotals.removeAllElements();
						  ElectricityUsageCalculator.modelOfListOfTotals.addElement(eucCalculations.getDailyALLString(ElectricityUsageCalculator.storage));
						  //ElectricityUsageCalculator.modelOfListOfTotals.removeElementAt(ElectricityUsageCalculator.modelOfListOfTotals.getSize() - 1);
						  ElectricityUsageCalculator.modelOfListOfTotals.addElement("Total Usage: " + eucCalculations.getTotal(ElectricityUsageCalculator.storage) + " kW");
						  //ElectricityUsageCalculator.modelOfListOfTotals.removeElementAt(ElectricityUsageCalculator.modelOfListOfTotals.getSize() - 1);
					  }
					  ElectricityUsageCalculator.listOfTotals.revalidate();
					  */
				  }
			  } catch (JsonParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
			  } catch (JsonMappingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
			  } catch (IOException e1) {
					// TODO Auto-generated catch block
						e1.printStackTrace();
			  }

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
		
		
		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		topFrame.dispose();
	}
}
