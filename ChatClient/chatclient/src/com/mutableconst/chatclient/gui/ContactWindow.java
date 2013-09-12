package com.mutableconst.chatclient.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

import com.mutableconst.dashboard_manager.Preferences;
import com.mutableconst.dashboard_manager.Preferences;
import com.mutableconst.models.Contact;
import com.mutableconst.models.ContactManager;

public class ContactWindow extends JFrame {

	private static ContactWindow reference;

	private JScrollPane scroll;
	private JTextField filterBox;

	JPanel friendsArea;
	GridBagLayout friendsLayout;
	GridBagConstraints friendsLayoutContrains;

	private Box topArea, windowBox;

	ContactWindowTile[] contacts;

	public static ContactWindow getContactWindow() {
		if (reference == null) {
			reference = new ContactWindow();
		}
		return reference;
	}

	public static void focusContactWindow() {
		getContactWindow().toFront();
		getContactWindow().setExtendedState(JFrame.NORMAL);
	}

	private ContactWindow() {
		super("MutableConst");
		setIconImage(Resources.MCIcon);
		setSize(225, 750);
		setMinimumSize(new Dimension(225, 250));
		if (Preferences.getPreference(Preferences.BUDDYX) != null && Preferences.getPreference(Preferences.BUDDYY) != null) {
			setLocation(new Integer(Preferences.getPreference(Preferences.BUDDYX)), new Integer(Preferences.getPreference(Preferences.BUDDYY)));
		} else {
			setLocation((int) (GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().getWidth() - getWidth()), 0);
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			// Ignore
		}
		filterBox = new JTextField();
		filterBox.setMinimumSize(new Dimension(getWidth(), 25));
		filterBox.setMaximumSize(new Dimension(getWidth(), 25));
		filterBox.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				updateContactWindow();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateContactWindow();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {}
		});

		topArea = Box.createHorizontalBox();
		topArea.add(filterBox);
		
		MigLayout layout = new MigLayout("align left", "[grow][][]", "[][shrink]");
		
		friendsArea = new JPanel(layout);

		scroll = new JScrollPane(friendsArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		windowBox = Box.createVerticalBox();
		windowBox.add(scroll, 0);
		windowBox.add(topArea, 0);

		add(windowBox);

		setVisible(true);

		contacts = getContacts();
		updateContactWindow();
	}

	private void updateContactWindow() {
		String filter = filterBox.getText().toLowerCase();
		friendsArea.removeAll();
		for (int i = 0; i < contacts.length; i++) {
			if (contacts[i].matchesFilter(filter) || filter.length() == 0) {
				friendsArea.add(contacts[i], "wrap, grow, gap 0 0 0 0, hmin 30");
			}
		}
		friendsArea.revalidate();
		friendsArea.repaint();
	}

	private ContactWindowTile[] getContacts() {
		ContactWindowTile[] friends = new ContactWindowTile[9];

		friends[0] = new ContactWindowTile(ContactManager.addContact(new Contact("2629940732", "Casey The Slenderman Slenderman")));
		friends[1] = new ContactWindowTile(ContactManager.addContact(new Contact("6083976053", "Nick McFace")));
		friends[2] = new ContactWindowTile(ContactManager.addContact(new Contact("6085553333")));
		friends[3] = new ContactWindowTile(ContactManager.addContact(new Contact("2629940732", "Casey The Slenderman Slenderman")));
		friends[4] = new ContactWindowTile(ContactManager.addContact(new Contact("6083976053", "Nick McFace")));
		friends[5] = new ContactWindowTile(ContactManager.addContact(new Contact("6085553333")));
		friends[6] = new ContactWindowTile(ContactManager.addContact(new Contact("2629940732", "Casey The Slenderman Slenderman")));
		friends[7] = new ContactWindowTile(ContactManager.addContact(new Contact("6083976053", "Nick McFace")));
		friends[8] = new ContactWindowTile(ContactManager.addContact(new Contact("6085553333")));
		return friends;
	}

}
