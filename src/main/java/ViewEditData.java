import javax.swing.*; 
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowEvent;
/*Displays the window for "View/Edit Data" */

public class ViewEditData extends JFrame
{
	private static String FRAME_TITLE = "View Data";
	private static String VIEW_EDIT_DATA_PAGE_CARD = "ViewEditDataPage";
	
	private JPanel cards = new JPanel();
	private ViewEditDataPage viewEditDataPage = new ViewEditDataPage(this);
	
	ViewEditData()
	{
		super(FRAME_TITLE);
		cards.setLayout(new CardLayout());
		
		cards.add(VIEW_EDIT_DATA_PAGE_CARD,viewEditDataPage);
		
		this.setSize(new Dimension(700,400));
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);// or DISPOSE_ON_CLOSE
		this.getContentPane().add(cards);
		showViewEditData();
		this.setVisible(true);
		this.setResizable(false);
	}
	public void showViewEditData()
	{
		((CardLayout)cards.getLayout()).show(cards, VIEW_EDIT_DATA_PAGE_CARD);
	}
}
