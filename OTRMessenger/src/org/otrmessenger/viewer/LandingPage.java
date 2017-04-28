package org.otrmessenger.viewer;

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
  private Object[][] data = { { new String("CS-mates"), new String("Ian Martiny"), "Get", "Delete" },{ new String("CS-mates"), new String("Sergey Frolov"), "Get", "Delete" },{ new String("CS-mates"), new String("Shirly Montero"), "Get", "Delete" } };
  private TableModel model = new DefaultTableModel(data, columnNames)
  {
    private static final long serialVersionUID = 1L;

    public boolean isCellEditable(int row, int column)
    {
      return column == 1;
    }
  };
  private JTable table = new JTable(model);
  
  public LandingPage(String name)
  {
	frame = new JFrame("Home-"+name);
	Dimension size = table.getPreferredSize();
	size.height +=100;
	size.width +=200;
	
	
	JButton btnAddFriend = new JButton("AddFriend");
	btnAddFriend.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			//add friend when clicked
		}
	});
	btnAddFriend.setFont(new Font("Verdana", Font.PLAIN, 12));
	btnAddFriend.setBounds(10, size.height-50, 100, 50);
	frame.getContentPane().add(btnAddFriend);
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
  /*

  public static void main(String[] args) throws Exception
  {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        new LandingPage("HostName");
      }
    });
  }
*/
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
        JOptionPane.showMessageDialog(button, "Opening chat with : "+table.getValueAt(row, 1));
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
