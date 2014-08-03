package com.mutableconst.chatclient.gui;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

import com.mutableconst.dashboard_manager.Preferences;
import com.mutableconst.models.Contact;
import com.mutableconst.models.ContactManager;

public  class ContactWindow extends JFrame {

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
		//filterBox.setMinimumSize(new Dimension(getWidth(), 25));
		//filterBox.setMaximumSize(new Dimension(getWidth(), 25));
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

		MigLayout windowLayout = new MigLayout("align left, novisualpadding", "[grow][][]", "[][shrink]");
		MigLayout friendsLayout = new MigLayout("align left, novisualpadding", "[grow][][]", "[][shrink]");
		
		friendsArea = new JPanel(friendsLayout);
		
		setLayout(windowLayout);

		scroll = new JScrollPane(friendsArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		add(filterBox, "wrap, grow, gap 0 0 0 0, hmin 20" );
		add(friendsArea, "wrap, grow, gap 0 0 0 0, hmin 30");

		setVisible(true);

		contacts = getContacts();
		updateContactWindow();
	}

	private void updateContactWindow() {
		String filter = filterBox.getText().toLowerCase();
		friendsArea.removeAll();
		for (int i = 0; i < contacts.length; i++) {
			if (contacts[i].matchesFilter(filter) || filter.length() == 0) {
				friendsArea.add(contacts[i], " dock west, dock north, grow, gap 0 0 0 0, hmin 30");
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
