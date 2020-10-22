import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowEvent;
/*Displays the window for "Import Data" */

public class ImportData extends JFrame
{
	private final ElectricityUsageCalculator electricityUsageCalculator;
	private JPanel spacePanel;
	private JLabel titleLabel;
	private FileChooserButton fileChooserButton;

	private static String FRAME_TITLE = "Import Data";

	public ImportData(final ElectricityUsageCalculator electricityUsageCalculator)
	{
		this.electricityUsageCalculator = electricityUsageCalculator;
		this.setSize(new Dimension(700,400));
		this.setLayout(new GridLayout(3,3));
		//create labels
		titleLabel = new JLabel(FRAME_TITLE, SwingConstants.CENTER);
		//create buttons
		fileChooserButton = new FileChooserButton(electricityUsageCalculator);

		SpacePanel();this.add(titleLabel); SpacePanel(); //row1
		SpacePanel();this.add(fileChooserButton);SpacePanel(); //row2
		SpacePanel();SpacePanel();SpacePanel();//row3
	}
	private void SpacePanel()
	{
		spacePanel = new JPanel();
		this.add(spacePanel);
	}
}
