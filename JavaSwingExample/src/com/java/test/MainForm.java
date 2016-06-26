package com.java.test;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import javax.swing.JSeparator;

public class MainForm {

	private JFrame frame;
	private JTextField txtName;
	private JTextField txtLastName;
	private JTextField txtAge;
	static DB database;
	static Mongo mongo;
	private JScrollPane scrollPane;
	private static JTable jtUsers;
	private JButton btnDelete;
	private JTextField txtFilterAge;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MainForm window = new MainForm();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mongo = new MongoClient("localhost", 27017);
		database = mongo.getDB("javaDB");
		loadTable(null);
	}

	private static void loadTable(BasicDBObject _filterQuery) {
		DBCollection dbcol = database.getCollection("users");
		DBCursor result = _filterQuery!=null 
				? dbcol.find(_filterQuery) 
				: dbcol.find();
		Object results[][] = new Object[result.count()][];
		int index = 0;
		while (result.hasNext()) {
			DBObject rr = result.next();
			results[index] = new Object[] { rr.get("name").toString(),
					rr.get("lastname").toString(), rr.get("age").toString(),
					rr.get("_id").toString() };
			index++;
		}
		TableModel tablemodel = new DefaultTableModel(results, new Object[] {
				"name", "lastname", "age", "_id" });
		jtUsers.setModel(tablemodel);
		jtUsers.getColumn("_id").setWidth(0);
		jtUsers.getColumn("_id").setMinWidth(0);
		jtUsers.getColumn("_id").setMaxWidth(0);
	}

	public MainForm() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 577, 266);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		txtName = new JTextField();
		txtName.setBounds(100, 11, 93, 20);
		frame.getContentPane().add(txtName);
		txtName.setColumns(10);

		txtLastName = new JTextField();
		txtLastName.setBounds(100, 42, 93, 20);
		frame.getContentPane().add(txtLastName);
		txtLastName.setColumns(10);

		txtAge = new JTextField();
		txtAge.setBounds(100, 74, 93, 20);
		frame.getContentPane().add(txtAge);
		txtAge.setColumns(10);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DBCollection tableUsers = database.getCollection("users");
				BasicDBObject obj = new BasicDBObject()
						.append("name", txtName.getText())
						.append("lastname", txtLastName.getText())
						.append("age", Integer.parseInt(txtAge.getText()));
				WriteResult result = tableUsers.insert(obj);
				loadTable(null);
			}
		});
		btnSave.setBounds(10, 105, 183, 23);
		frame.getContentPane().add(btnSave);

		JLabel lblName = new JLabel("Name :");
		lblName.setHorizontalAlignment(SwingConstants.RIGHT);
		lblName.setBounds(44, 14, 46, 14);
		frame.getContentPane().add(lblName);

		JLabel lblLastName = new JLabel("Last Name :");
		lblLastName.setHorizontalAlignment(SwingConstants.RIGHT);
		lblLastName.setBounds(10, 45, 80, 14);
		frame.getContentPane().add(lblLastName);

		JLabel lblAge = new JLabel("Age :");
		lblAge.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAge.setBounds(44, 77, 46, 14);
		frame.getContentPane().add(lblAge);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(203, 11, 336, 83);
		frame.getContentPane().add(scrollPane);

		jtUsers = new JTable();
		jtUsers.setFillsViewportHeight(true);
		scrollPane.setViewportView(jtUsers);

		btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DBCollection tableUsers = database.getCollection("users");
				Object _id = (Object) jtUsers.getValueAt(
						jtUsers.getSelectedRow(), 3);
				BasicDBObject removeObject = new BasicDBObject("_id",
						new ObjectId(_id.toString()));
				tableUsers.remove(removeObject);
				loadTable(null);
			}
		});
		btnDelete.setBounds(199, 105, 340, 23);
		frame.getContentPane().add(btnDelete);

		JSeparator separator = new JSeparator();
		separator.setBounds(10, 137, 529, 9);
		frame.getContentPane().add(separator);

		JButton btnFilter = new JButton("Filter");
		btnFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadTable(txtFilterAge.getText().trim().length()==0 ? null : new BasicDBObject("age", Integer.parseInt(txtFilterAge.getText())));
			}
		});
		btnFilter.setBounds(169, 148, 89, 23);
		frame.getContentPane().add(btnFilter);

		JLabel label = new JLabel("Age :");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(10, 152, 46, 14);
		frame.getContentPane().add(label);

		txtFilterAge = new JTextField();
		txtFilterAge.setColumns(10);
		txtFilterAge.setBounds(66, 149, 93, 20);
		frame.getContentPane().add(txtFilterAge);
	}
}
