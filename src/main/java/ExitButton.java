import java.awt.event.ActionEvent;

class ExitButton extends EUCButton {
	private static final String BUTTON_NAME = "Exit";
	
	public ExitButton(final ElectricityUsageCalculator electricityUsageCalculator){
		super(BUTTON_NAME, electricityUsageCalculator);
		this.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e){
		electricityUsageCalculator.exit();
	}

}