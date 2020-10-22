import javax.swing.*;
import java.awt.event.ActionListener;

/*

*This will be the parent button for most buttons used in this program.

*/
public abstract class EUCButton extends JButton implements ActionListener{
	protected ElectricityUsageCalculator electricityUsageCalculator;

	public EUCButton(final String name, final ElectricityUsageCalculator electricityUsageCalculator){
		super(name);
		this.electricityUsageCalculator = electricityUsageCalculator;
	}
}
