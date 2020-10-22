import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.math.MathContext;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
/*Displays the window for "View Results" */
public class ViewResults extends JFrame
{
    //BigDecimal calculations up to 4 decimals
	private static MathContext mc = new MathContext(4);

	
	//strings
	private static String FRAME_TITLE = "View Results";
	private static String VIEW_RESULTS_PAGE_CARD = "ViewResults";

	//panels
	private JPanel cards;
	private ViewResultsPage viewResultsPage;

	ViewResults()
	{
		super(FRAME_TITLE);
		cards = new JPanel();
		cards.setLayout(new CardLayout());

		viewResultsPage = new ViewResultsPage(this);
		cards.add(VIEW_RESULTS_PAGE_CARD, viewResultsPage);

		this.setSize(new Dimension(700,400));
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.getContentPane().add(cards);

		showViewResultsPage();

		
		this.setResizable(false);

		JFreeChart lineChart = ChartFactory.createLineChart(
		         "All User Electricity Usage",
		         "Days","kWH",
		         createDataset(),
		         PlotOrientation.VERTICAL,
		         true,true,false);

	  ChartPanel chartPanel = new ChartPanel( lineChart );
	  //chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
	  JPanel centerPanel = new JPanel();
	  centerPanel.setLayout(new FlowLayout());
	  centerPanel.add(chartPanel);
	  this.add(centerPanel, BorderLayout.CENTER);
	  
	  JTextPane results = new JTextPane();
	  results.setEditable(false);
	  results.setText(eucCalculations.getDailyCostString(ElectricityUsageCalculator.storage));
	  
	  SimpleAttributeSet attributeSet = new SimpleAttributeSet();
	  attributeSet = new SimpleAttributeSet();  
      StyleConstants.setItalic(attributeSet, true);  
      StyleConstants.setForeground(attributeSet, Color.black);  
      StyleConstants.setBackground(attributeSet, Color.yellow);  

      Document doc = results.getStyledDocument();  
      try {
		doc.insertString(doc.getLength(), "\nTotal Usage: " 
				+ eucCalculations.getTotal(ElectricityUsageCalculator.storage)
				+ "KWH", attributeSet);
	} catch (BadLocationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}  

      JScrollPane scrollPane = new JScrollPane(results);
      this.add(scrollPane, BorderLayout.PAGE_END); 
	  
	  this.pack();
	  this.setVisible(true);
	  //setContentPane( this );
   }

	private DefaultCategoryDataset createDataset() {
	  DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
	  if(ElectricityUsageCalculator.storage.getReads() != null) {
		  DailyElectricityUsage obj = ElectricityUsageCalculator.storage;
		  EucRates[] storeUsage = obj.getReads();
		  for(int i = 0;i<storeUsage.length;i++) {
			  BigDecimal hourDecimal;
			  if (storeUsage[i].getHour() == 0) {
				  hourDecimal = new BigDecimal(1);

			  }else {
				  hourDecimal = new BigDecimal(storeUsage[i].getHour()).divide(new BigDecimal(24), mc);
			  }
			  double daysPlusHour = storeUsage[i].getDayOfYear() + hourDecimal.doubleValue();
			  dataset.addValue( storeUsage[i].getValue() , "Days" , daysPlusHour);
		  }
	  }else {
		  dataset.addValue(0, "Hours", "0");
	  }
	  
	  return dataset;
	}
	public void showViewResultsPage()
	{
		((CardLayout)cards.getLayout()).show(cards, VIEW_RESULTS_PAGE_CARD);
	}
}
