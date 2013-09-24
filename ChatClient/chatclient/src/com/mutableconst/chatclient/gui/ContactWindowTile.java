package com.mutableconst.chatclient.gui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.mutableconst.dashboard_manager.EventManager;
import com.mutableconst.models.Contact;

public class ContactWindowTile extends JPanel {

	Contact contact;

	public ContactWindowTile(final Contact contact) {
		super();
		this.contact = contact;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel name = new JLabel(contact.getName());
		JLabel phoneNumber = new JLabel(contact.getPhoneNumber());

		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
		
		add(name);
		add(phoneNumber);
		
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					EventManager.pingTextWindow(contact.getPhoneNumber(), null);
				}
			}
		});
	}
	
	public Contact getContact() {
		return contact;
	}
	
	public boolean matchesFilter(String filter) {
		String lowerCaseName = contact.getName().toLowerCase();
		String lowerCasePhoneNumber = contact.getPhoneNumber().toLowerCase();
		return lowerCasePhoneNumber.contains(filter) || lowerCaseName.contains(filter);
	}


}
