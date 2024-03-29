package replicatorg.app.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import replicatorg.app.Base;
import replicatorg.app.MachineController;
import replicatorg.app.Serial;
import replicatorg.app.TimeoutException;
import replicatorg.drivers.EstimationDriver;
import replicatorg.drivers.UsesSerial;
import replicatorg.drivers.Version;
import replicatorg.machine.MachineListener;
import replicatorg.machine.MachineProgressEvent;
import replicatorg.machine.MachineState;
import replicatorg.machine.MachineStateChangeEvent;
import replicatorg.machine.MachineToolStatusEvent;

/**
 * The MachineStatusPanel displays the current state of the connected machine,
 * or a message informing the user that no connected machine can be found.
 * 
 * @author phooky
 * 
 */
public class MachineStatusPanel extends BGPanel implements MachineListener {
	private static final long serialVersionUID = -6944931245041870574L;

	protected MachineController machine = null;

	protected JLabel label = new JLabel();
	protected JLabel smallLabel = new JLabel();
	protected JLabel tempLabel = new JLabel();

	protected double currentTemperature = -1;
	
	static final private Color BG_NO_MACHINE = new Color(0xff, 0x80, 0x60);

	static final private Color BG_READY = new Color(0x80, 0xff, 0x60);

	// static final private Color BG_WAIT = new Color(0xff,0xff,0x60);

	MachineStatusPanel() {
		Font smallFont = Base.getFontPref("status.font","SansSerif,plain,10");
		smallLabel.setFont(smallFont);
		tempLabel.setFont(smallFont);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		label.setAlignmentX(LEFT_ALIGNMENT);
		add(label);
		smallLabel.setAlignmentX(LEFT_ALIGNMENT);
		{
			Box b = Box.createHorizontalBox();
			b.add(smallLabel);
			b.add(Box.createHorizontalGlue());
			b.add(tempLabel);
			b.setAlignmentX(LEFT_ALIGNMENT);
			tempLabel.setAlignmentX(RIGHT_ALIGNMENT);
			add(b);
		}
		add(Box.createVerticalGlue());

		int height = 40; // TODO: magic number
		setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
		setMinimumSize(new Dimension(Integer.MAX_VALUE, height));
		setPreferredSize(new Dimension(Integer.MAX_VALUE, height));
		setAlignmentX(LEFT_ALIGNMENT);
	}

	/**
	 * Indicate which machine, if any, the status panel should attach to.
	 * 
	 * @param machine
	 *            the machine's controller, or null if no machine is attached.
	 */
	public void setMachine(MachineController machine) {
		System.err.println("Machine set to "+machine);
		if (this.machine == machine)
			return;
		this.machine = machine;
		updateMachineStatus();
	}

	private boolean firmwareWarningIssued = false;

	
	protected String getMachineStateMessage() {
		if (machine == null) { return "No machine selected"; }
		MachineState state = machine.getMachineState();
		if (state.getState() == MachineState.State.NOT_ATTACHED) {
			if (machine.getDriver() == null) {
				return "No machine selected";
			} else if (machine.driver instanceof UsesSerial && 
					((UsesSerial)machine.driver).getSerial() == null) {
				if (Serial.scanSerialNames().size() == 0) {
					return "No serial ports detected";
				} else {
					return "No serial port selected";
				}
			}
		}
		if (state.getState() == MachineState.State.CONNECTING) {
			StringBuffer buf = new StringBuffer("Connecting to "+machine.getName());
			if (machine.driver instanceof UsesSerial) {
				buf.append(" on ");
				buf.append(((UsesSerial)machine.driver).getSerial().getName());
			}
			buf.append("...");
			return buf.toString();
		}
		StringBuffer message = new StringBuffer("Machine "+machine.getName());
		message.append(" ("+machine.getDriver().getDriverName()+") ");
		if (state.getState() == MachineState.State.READY) { message.append("ready"); }
		else if (state.isPaused()) { message.append("paused"); }
		else if (state.isBuilding()) { message.append("building"); }
		return message.toString();
	}
	
	/**
	 * Display the current status of this machine.
	 */
	protected synchronized void updateMachineStatus() {
		// update background to indicate high-level status
		Color bgColor = Color.WHITE;
		String text = getMachineStateMessage();
		if (machine == null || machine.driver == null) {
			bgColor = BG_NO_MACHINE;
		} else {
			if (!machine.isInitialized()) {
				bgColor = BG_NO_MACHINE;
			} else {
				bgColor = BG_READY;
				currentTemperature = -1;
				// Check version
				try {
					Version v = machine.driver.getVersion();
					if (v != null && v.compareTo(machine.driver.getPreferredVersion()) < 0) {
						if (!firmwareWarningIssued) {
							firmwareWarningIssued = true;
							JOptionPane.showMessageDialog(
									this,
									"Firmware version "+v+" was detected on your machine.  Firmware version "+
									machine.driver.getPreferredVersion() + " is recommended.\n" +
									"Please update your firmware and restart ReplicatorG.",
									"Old firmware detected", JOptionPane.WARNING_MESSAGE);
							
						}
					}
				} catch (TimeoutException te) {
					bgColor = BG_NO_MACHINE;
					// TODO: notify 
					text = "Machine connection timed out";
				}
			}
			
		}
		label.setText(text);
		smallLabel.setText(null);
		tempLabel.setText(null);
		setBackground(bgColor);
	}

	public void machineStateChanged(MachineStateChangeEvent evt) {
		updateMachineStatus();
	}

	public void machineProgress(MachineProgressEvent event) {
		double proportion = (double)event.getLines()/(double)event.getTotalLines();
		double remaining = event.getEstimated() * (1.0 - proportion);
		String s = String.format(
				"Commands:  %1$7d / %2$7d  (%3$3.2f%%)   |   Elapsed:  %4$s  |  Estimated Remaining:  %5$s",
				event.getLines(), event.getTotalLines(), 
				Math.round(proportion*10000.0)/100.0,
				EstimationDriver.getBuildTimeString(event.getElapsed(), true),
				EstimationDriver.getBuildTimeString(remaining, true));
		smallLabel.setText(s);
	}

	public void toolStatusChanged(MachineToolStatusEvent event) {
		currentTemperature = event.getTool().getCurrentTemperature();
		if (currentTemperature != -1) {
			tempLabel.setText(String.format("Temp: %1$3.1f\u00B0C", currentTemperature));
		}
	}
}
