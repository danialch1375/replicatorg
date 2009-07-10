/**
 * 
 */
package replicatorg.app.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import replicatorg.app.Base;
import replicatorg.app.Preferences;

/**
 * Edit the major preference settings.
 * @author phooky
 *
 */
public class PreferencesWindow extends JFrame {
	// gui elements

	// the calling editor, so updates can be applied
	MainWindow editor;

	int wide, high;

	JTextField sketchbookLocationField;

	JCheckBox exportSeparateBox;

	JCheckBox sketchPromptBox;

	JCheckBox sketchCleanBox;

	JCheckBox externalEditorBox;

	JCheckBox memoryOverrideBox;

	JTextField memoryField;

	JTextField fontSizeField;

	public PreferencesWindow() {
		super("Preferences");
		setResizable(false);

		Container content = getContentPane();
		content.setLayout(null);

		int top = Preferences.GUI_BIG;
		int left = Preferences.GUI_BIG;
		int right = 0;

		JLabel label;
		JButton button;
		Dimension d, d2;
		int h, vmax;

		// [ ] Prompt for name and folder when creating new sketch

		sketchPromptBox = new JCheckBox(
				"Prompt for name when opening or creating a sketch");
		content.add(sketchPromptBox);
		d = sketchPromptBox.getPreferredSize();
		sketchPromptBox.setBounds(left, top, d.width, d.height);
		right = Math.max(right, left + d.width);
		top += d.height + Preferences.GUI_BETWEEN;

		// [ ] Delete empty sketches on Quit

		sketchCleanBox = new JCheckBox("Delete empty sketches on Quit");
		content.add(sketchCleanBox);
		d = sketchCleanBox.getPreferredSize();
		sketchCleanBox.setBounds(left, top, d.width, d.height);
		right = Math.max(right, left + d.width);
		top += d.height + Preferences.GUI_BETWEEN;

		// Sketchbook location:
		// [...............................] [ Browse ]

		label = new JLabel("Sketchbook location:");
		content.add(label);
		d = label.getPreferredSize();
		label.setBounds(left, top, d.width, d.height);
		top += d.height; // + GUI_SMALL;

		sketchbookLocationField = new JTextField(40);
		content.add(sketchbookLocationField);
		d = sketchbookLocationField.getPreferredSize();

		button = new JButton("Browse");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * JFileChooser fc = new JFileChooser(); fc.setSelectedFile(new
				 * File(sketchbookLocationField.getText()));
				 * fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				 * 
				 * int returned = fc.showOpenDialog(new JDialog()); if (returned ==
				 * JFileChooser.APPROVE_OPTION) { File file =
				 * fc.getSelectedFile();
				 * sketchbookLocationField.setText(file.getAbsolutePath()); }
				 */
				File dflt = new File(sketchbookLocationField.getText());
				File file = Base.selectFolder("Select new sketchbook location",
						dflt, PreferencesWindow.this);
				if (file != null) {
					sketchbookLocationField.setText(file.getAbsolutePath());
				}
			}
		});
		content.add(button);
		d2 = button.getPreferredSize();

		// take max height of all components to vertically align em
		vmax = Math.max(d.height, d2.height);
		// label.setBounds(left, top + (vmax-d.height)/2,
		// d.width, d.height);

		// h = left + d.width + GUI_BETWEEN;
		sketchbookLocationField.setBounds(left, top + (vmax - d.height) / 2,
				d.width, d.height);
		h = left + d.width + Preferences.GUI_SMALL; // GUI_BETWEEN;
		button.setBounds(h, top + (vmax - d2.height) / 2, d2.width, d2.height);

		right = Math.max(right, h + d2.width + Preferences.GUI_BIG);
		top += vmax + Preferences.GUI_BETWEEN;

		// MainWindow font size [ ]

		Container box = Box.createHorizontalBox();
		label = new JLabel("MainWindow font size: ");
		box.add(label);
		fontSizeField = new JTextField(4);
		box.add(fontSizeField);
		label = new JLabel("  (requires restart of ReplicatorG)");
		box.add(label);
		content.add(box);
		d = box.getPreferredSize();
		box.setBounds(left, top, d.width, d.height);
		Font editorFont = Preferences.getFont("editor.font");
		fontSizeField.setText(String.valueOf(editorFont.getSize()));
		top += d.height + Preferences.GUI_BETWEEN;

		// [ ] Use external editor

		externalEditorBox = new JCheckBox("Use external editor");
		content.add(externalEditorBox);
		d = externalEditorBox.getPreferredSize();
		externalEditorBox.setBounds(left, top, d.width, d.height);
		right = Math.max(right, left + d.width);
		top += d.height + Preferences.GUI_BETWEEN;

		// More preferences are in the ...

		label = new JLabel(
				"More preferences can be edited directly in the file");

		content.add(label);
		d = label.getPreferredSize();
		label.setForeground(Color.gray);
		label.setBounds(left, top, d.width, d.height);
		right = Math.max(right, left + d.width);
		top += d.height; // + GUI_SMALL;

		label = new JLabel(Preferences.preferencesFile.getAbsolutePath());
		content.add(label);
		d = label.getPreferredSize();
		label.setBounds(left, top, d.width, d.height);
		right = Math.max(right, left + d.width);
		top += d.height;

		label = new JLabel("(edit only when ReplicatorG is not running)");
		content.add(label);
		d = label.getPreferredSize();
		label.setForeground(Color.gray);
		label.setBounds(left, top, d.width, d.height);
		right = Math.max(right, left + d.width);
		top += d.height; // + GUI_SMALL;

		// [ OK ] [ Cancel ] maybe these should be next to the message?

		button = new JButton("OK");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applyFrame();
				dispose();
			}
		});
		content.add(button);
		d2 = button.getPreferredSize();
		Preferences.BUTTON_HEIGHT = d2.height;

		h = right - (Preferences.BUTTON_WIDTH + Preferences.GUI_SMALL + Preferences.BUTTON_WIDTH);
		button.setBounds(h, top, Preferences.BUTTON_WIDTH, Preferences.BUTTON_HEIGHT);
		h += Preferences.BUTTON_WIDTH + Preferences.GUI_SMALL;

		button = new JButton("Cancel");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		content.add(button);
		button.setBounds(h, top, Preferences.BUTTON_WIDTH, Preferences.BUTTON_HEIGHT);

		top += Preferences.BUTTON_HEIGHT + Preferences.GUI_BETWEEN;

		// finish up

		wide = right + Preferences.GUI_BIG;
		high = top + Preferences.GUI_SMALL;
		// setSize(wide, high);

		// closing the window is same as hitting cancel button

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
			}
		});

		ActionListener disposer = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				dispose();
			}
		};
		Base.registerWindowCloseKeys(getRootPane(), disposer);

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screen.width - wide) / 2,
				(screen.height - high) / 2);

		pack(); // get insets
		Insets insets = getInsets();
		setSize(wide + insets.left + insets.right, high + insets.top
				+ insets.bottom);

		// handle window closing commands for ctrl/cmd-W or hitting ESC.

		content.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				KeyStroke wc = MainWindow.WINDOW_CLOSE_KEYSTROKE;
				if ((e.getKeyCode() == KeyEvent.VK_ESCAPE)
						|| (KeyStroke.getKeyStrokeForEvent(e).equals(wc))) {
					dispose();
				}
			}
		});
	}

	/*
	 * protected JRootPane createRootPane() { System.out.println("creating root
	 * pane esc received");
	 * 
	 * ActionListener actionListener = new ActionListener() { public void
	 * actionPerformed(ActionEvent actionEvent) { //setVisible(false);
	 * System.out.println("esc received"); } };
	 * 
	 * JRootPane rootPane = new JRootPane(); KeyStroke stroke =
	 * KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
	 * rootPane.registerKeyboardAction(actionListener, stroke,
	 * JComponent.WHEN_IN_FOCUSED_WINDOW); return rootPane; }
	 */

	public Dimension getPreferredSize() {
		return new Dimension(wide, high);
	}

	/**
	 * Change internal settings based on what was chosen in the prefs, then send
	 * a message to the editor saying that it's time to do the same.
	 */
	public void applyFrame() {
		// put each of the settings into the table
		Preferences.setBoolean("sketchbook.prompt", sketchPromptBox.isSelected());
		Preferences.setBoolean("sketchbook.auto_clean", sketchCleanBox.isSelected());
		Preferences.set("sketchbook.path", sketchbookLocationField.getText());
		Preferences.setBoolean("editor.external", externalEditorBox.isSelected());

		String newSizeText = fontSizeField.getText();
		try {
			int newSize = Integer.parseInt(newSizeText.trim());
			String fontName = Preferences.get("editor.font");
			if (fontName != null) {
				String pieces[] = fontName.split(",");
				pieces[2] = String.valueOf(newSize);
				StringBuffer buf = new StringBuffer();
				for (String piece : pieces) {
					if (buf.length() > 0) buf.append(",");
					buf.append(piece);
				}
				Preferences.set("editor.font", buf.toString());
			}

		} catch (Exception e) {
			System.err.println("ignoring invalid font size " + newSizeText);
		}
		editor.applyPreferences();
	}

	public void showFrame(MainWindow editor) {
		this.editor = editor;

		// set all settings entry boxes to their actual status
		sketchPromptBox.setSelected(Preferences.getBoolean("sketchbook.prompt"));
		sketchCleanBox.setSelected(Preferences.getBoolean("sketchbook.auto_clean"));
		sketchbookLocationField.setText(Preferences.get("sketchbook.path"));
		externalEditorBox.setSelected(Preferences.getBoolean("editor.external"));

		setVisible(true);
	}

}