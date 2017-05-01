package org.otrmessenger.viewer;
// used http://stackoverflow.com/a/10348919 as a resource

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class LandingPage
{
  private JFrame frame;
  private String[] columnNames = { "Group", "Name", "Public Key", "Friend" };
  private Object[][] data = null;
  private TableModel model = new DefaultTableModel(data, columnNames)
  {
    private static final long serialVersionUID = 1L;

    public boolean isCellEditable(int row, int column)
    {
      return column == 1;
    }
  };
  private JTable table = new JTable(model);
  private Host myself = null;
  private FriendsList fl = null;
  
  private void setTableModel(Object d[][], String cnames[]){
      this.model = new DefaultTableModel(d, cnames)
      {
        private static final long serialVersionUID = 1L;

        public boolean isCellEditable(int row, int column)
        {
          return column == 1;
        }
      };
  }
  
public void setFL(FriendsList fl){
    this.fl = fl;
}

public void setHost(Host h){
    this.myself = h;
}

public void draw(){
    data = fl.toObjectArray();
    setTableModel(data, columnNames);
    this.table = new JTable(this.model);
    frame = new JFrame("Home-" + myself.getUsername());
    Dimension size = table.getPreferredSize();
    size.height += 100;
    size.width += 200;
    
    LandingPage l = this;
    
	JButton btnAddFriend = new JButton("AddFriend");
	btnAddFriend.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    AddFriend af = new AddFriend(myself, l);
//		    myself = af.getHost();
//		    fl = new FriendsList(myself.getUsername());
//		    System.out.println(fl);
//		    frame.dispose();
//		    draw();
		}
	});
	btnAddFriend.setFont(new Font("Verdana", Font.PLAIN, 12));
	btnAddFriend.setBounds(10, size.height-50, 100, 50);
	frame.getContentPane().add(btnAddFriend);
	
	JButton btnChangeKey = new JButton("AddFriend");
	btnChangeKey.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    myself.genKeyPair();
//		    myself = af.getHost();
//		    fl = new FriendsList(myself.getUsername());
//		    System.out.println(fl);
//		    frame.dispose();
//		    draw();
		}
	});
	btnChangeKey.setFont(new Font("Verdana", Font.PLAIN, 12));
	btnChangeKey.setBounds(10, size.height-50, 100, 50);
	frame.getContentPane().add(btnChangeKey);
	
    table.getColumnModel().getColumn(1).setCellRenderer(new ClientsTableButtonRenderer());
    table.getColumnModel().getColumn(1).setCellEditor(new ClientsTableRenderer(new JCheckBox()));
    
   
    table.setPreferredScrollableViewportSize(size);
    table.setShowHorizontalLines(true);
    table.setShowVerticalLines(false);
    

    JScrollPane scroll = new JScrollPane(table);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(scroll);
    frame.pack();
    frame.setLocation(300, 300);
    frame.setVisible(true);
}

  public LandingPage(Host m){
    myself = m;
    fl = new FriendsList(m.getUsername());
    draw();
  }
  

  class ClientsTableButtonRenderer extends JButton implements TableCellRenderer
  {
    public ClientsTableButtonRenderer()
    {
      setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
      setForeground(Color.black);
      setBackground(UIManager.getColor("Button.background"));
      setText((value == null) ? "" : value.toString());
      return this;
    }
  }
  public class ClientsTableRenderer extends DefaultCellEditor
  {
    private JButton button;
    private String label;
    private boolean clicked;
    private int row, col;
    private JTable table;
   

    public ClientsTableRenderer(JCheckBox checkBox)
    {
      super(checkBox);
  
      button = new JButton();
      button.setOpaque(true);
      button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          fireEditingStopped();
        }
      });
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
      this.table = table;
      this.row = row;
      this.col = column;

      button.setForeground(Color.black);
      button.setBackground(UIManager.getColor("Button.background"));
      label = (value == null) ? "" : value.toString();
      button.setText(label);
      clicked = true;
      return button;
    }
    public Object getCellEditorValue()
    {
      if (clicked)
      {
          myself.addChat(new Chat(table.getValueAt(row, 1).toString(), myself));
//        JOptionPane.showMessageDialog(button, "Opening chat with : "+table.getValueAt(row, 1));
      }
      clicked = false;
      return new String(label);
      
     }

    public boolean stopCellEditing()
    {
      clicked = false;
      return super.stopCellEditing();
    }

    protected void fireEditingStopped()
    {
      super.fireEditingStopped();
    }
  }

}
