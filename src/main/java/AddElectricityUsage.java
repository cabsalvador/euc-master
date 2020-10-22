import javax.swing.*; 
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/*Displays the window for "Add Electricty Usage" */

public class AddElectricityUsage extends JFrame
{
	private static String FRAME_TITLE = "Add Electricity Usage";
	private static String ADD_ELECTRICITY_USAGE_PAGE_CARD = "AddElectricityUsagePage";
	
	private JPanel cards;
	private AddElectricityUsagePage addElectricityUsagePage;
	
	AddElectricityUsage()
	{
		super(FRAME_TITLE);
		cards = new JPanel();
		cards.setLayout(new CardLayout());
		
		addElectricityUsagePage = new AddElectricityUsagePage(this);
		cards.add(ADD_ELECTRICITY_USAGE_PAGE_CARD, addElectricityUsagePage);
		
		//change size later
		this.setSize(new Dimension(900,600));
		//not sure about this
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.getContentPane().add(cards);
		showAddElectricityUsagePage();
		this.setVisible(true);
		this.setResizable(true);
	}
	public void showAddElectricityUsagePage()
	{
		((CardLayout)cards.getLayout()).show(cards, ADD_ELECTRICITY_USAGE_PAGE_CARD);
	}
	public void cancel()
	{
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSED));
	}
}

