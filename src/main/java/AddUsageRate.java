import javax.swing.*; 
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowEvent;

public class AddUsageRate extends JFrame {
	
	private static String FRAME_TITLE = "Electricity Usage Calculator";
	private static String ADD_USAGE_RATE_PAGE = "AddUsageRatePage";
	
	private JPanel cards;
	public AddUsageRatePage addUsageRatePage;
	
	public AddUsageRate() {
		super(FRAME_TITLE);
		cards = new JPanel();
		cards.setLayout(new CardLayout());
		
		addUsageRatePage = new AddUsageRatePage(this);
		cards.add(ADD_USAGE_RATE_PAGE, addUsageRatePage);
		
		//change size later
		this.setSize(new Dimension(900,500));
		//not sure about this
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.getContentPane().add(cards);
		showAddUsageRatePage();
		this.setVisible(true);
		this.setResizable(false);
	}
	
	public void showAddUsageRatePage(){
		((CardLayout)cards.getLayout()).show(cards, ADD_USAGE_RATE_PAGE);
	}
	
	public void cancel(){
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSED));
	}
}
