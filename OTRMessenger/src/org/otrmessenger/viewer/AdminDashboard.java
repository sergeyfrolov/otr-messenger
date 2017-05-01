package org.otrmessenger.viewer;

import org.otrmessenger.messaging.Messaging;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultCaret;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Vector;

public class AdminDashboard {

    private JFrame frame;
    private Host myself;
    private AllUsers windowAU;
    private OnlineUsers windowOU;
    private PublicKeys windowPK;

    /**
     * Create the application.
     */
    public AdminDashboard(Host h) {
        myself = h;
        initialize();
        frame.setVisible(true);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame("AdminDashboard");
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblCurrentStatus = new JLabel("Current Status: SERVER_LAUNCHED");
        lblCurrentStatus.setBounds(37, 24, 298, 16);
        frame.getContentPane().add(lblCurrentStatus);

        JButton btnLaunch = new JButton("Launch");
        btnLaunch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Messaging.MsgServerToClient response = myself.sendAdminRequest(Messaging.AdminRequest.LAUNCH);
                lblCurrentStatus.setText("Current Status: " + response.getState().name());
            }
        });
        btnLaunch.setBounds(44, 55, 117, 24);
        frame.getContentPane().add(btnLaunch);

        JButton btnStop = new JButton("Stop");
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Messaging.MsgServerToClient response = myself.sendAdminRequest(Messaging.AdminRequest.STOP);
                lblCurrentStatus.setText("Current Status: " + response.getState().name());
            }
        });
        btnStop.setBounds(44, 84, 117, 24);
        frame.getContentPane().add(btnStop);

        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Messaging.MsgServerToClient response = myself.sendAdminRequest(Messaging.AdminRequest.RESET);
                lblCurrentStatus.setText("Current Status: " + response.getState().name());
            }
        });
        btnReset.setBounds(44, 115, 117, 24);
        frame.getContentPane().add(btnReset);

        JButton btnListAllUsers = new JButton("List All Users");
        btnListAllUsers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                windowAU = new AllUsers();
                windowAU.frameAU.setVisible(true);
            }
        });
        btnListAllUsers.setHorizontalAlignment(SwingConstants.LEFT);
        btnListAllUsers.setBounds(44, 166, 171, 24);
        frame.getContentPane().add(btnListAllUsers);

        JButton btnListOnlineUsers = new JButton("List Online Users");
        btnListOnlineUsers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                windowOU = new OnlineUsers();
                windowOU.frameOU.setVisible(true);
            }
        });
        btnListOnlineUsers.setHorizontalAlignment(SwingConstants.LEFT);
        btnListOnlineUsers.setBounds(44, 197, 171, 24);
        frame.getContentPane().add(btnListOnlineUsers);

        JButton btnPublicKeys = new JButton("Public Keys ");
        btnPublicKeys.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                windowPK = new PublicKeys();
                windowPK.framePK.setVisible(true);
            }
        });
        btnPublicKeys.setHorizontalAlignment(SwingConstants.LEFT);
        btnPublicKeys.setBounds(44, 231, 171, 24);
        frame.getContentPane().add(btnPublicKeys);

        JSeparator separator = new JSeparator();
        separator.setBounds(6, 152, 438, 12);
        frame.getContentPane().add(separator);
    }

    public class AllUsers {

        private JFrame frameAU;

        public AllUsers() {
            initialize();
        }

        /**
         * Initialize the contents of the frame.
         */
        private void initialize() {
            frameAU = new JFrame("All Users");
            frameAU.setBounds(200, 100, 450, 300);
            frameAU.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frameAU.getContentPane().setLayout(null);

            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setBounds(17, 16, 416, 245);
            frameAU.getContentPane().add(scrollPane);

            JTextArea textArea = new JTextArea();
            Messaging.MsgServerToClient response = myself.sendAdminRequest(Messaging.AdminRequest.GET_ALL_USERS);
            for (Messaging.ClientInfo ci : response.getUsersList()) {
                textArea.append(ci.getUsername().toStringUtf8() + "\n");
            }
            scrollPane.setColumnHeaderView(textArea);
            textArea.setEditable(false);

            JLabel lblAf = new JLabel("Online: " + Integer.toString(response.getUsersList().size()) + " users");
            lblAf.setHorizontalAlignment(SwingConstants.CENTER);
            lblAf.setBounds(155, 19, 137, 16);
            frameAU.getContentPane().add(lblAf);
        }
    }

    public class OnlineUsers {

        private JFrame frameOU;

        /**
         * Create the application.
         */
        public OnlineUsers() {
            initialize();
        }

        /**
         * Initialize the contents of the frame.
         */
        private void initialize() {
            frameOU = new JFrame("Online Users");
            frameOU.setBounds(300, 100, 450, 300);
            frameOU.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frameOU.getContentPane().setLayout(null);

            JScrollPane scrollPane = new JScrollPane();
            scrollPane.setBounds(21, 47, 402, 199);
            frameOU.getContentPane().add(scrollPane);

            JTextArea textArea = new JTextArea();
            Messaging.MsgServerToClient response = myself.sendAdminRequest(Messaging.AdminRequest.GET_ONLINE_USERS);
            for (Messaging.ClientInfo ci : response.getUsersList()) {
                textArea.append(ci.getUsername().toStringUtf8() + "\n");
            }
            scrollPane.setViewportView(textArea);
            textArea.setEditable(false);

            JLabel lblOf = new JLabel("Online: " + Integer.toString(response.getUsersList().size()) + " users");
            lblOf.setHorizontalAlignment(SwingConstants.CENTER);
            lblOf.setBounds(155, 19, 137, 16);
            frameOU.getContentPane().add(lblOf);
        }
    }

    public class PublicKeys {

        private JFrame framePK;
        private DefaultTableModel model = new DefaultTableModel()//data, columnNames)
        {
            //private static final long serialVersionUID = 1L;

            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };

        public PublicKeys() {
            String[] columnNames = {"Name", "Public Key"};
            model.setColumnIdentifiers(columnNames);
            Messaging.MsgServerToClient response = myself.sendAdminRequest(Messaging.AdminRequest.GET_ALL_KEYS);
            for (Messaging.ClientInfo ci : response.getUsersList()) {
                String[] row = new String[]{ci.getUsername().toStringUtf8(),
                        String.format("%040x", new BigInteger(1, ci.getSignKey().toStringUtf8().getBytes()))};
                model.addRow(row);
            }
            framePK = new JFrame("Public Keys");
            JTable table = new JTable(model);
            Dimension size = table.getPreferredSize();
            size.height += 100;
            size.width += 200;
            //table.setRowHeight(200);

            table.setPreferredScrollableViewportSize(size);
            table.setShowHorizontalLines(true);
            table.setShowVerticalLines(true);
            table.getColumn("Name").setWidth(200);
            table.getColumn("Name").setMaxWidth(300);
            table.getColumn("Public Key").setCellRenderer(new RenderWrappable());

            JScrollPane scroll = new JScrollPane(table);
            framePK.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            framePK.getContentPane().add(scroll);
            framePK.pack();
            framePK.setLocation(400, 100);
            framePK.setVisible(true);
        }


    }

}

final class RenderWrappable extends DefaultTableCellRenderer {

    JTextArea textarea;

    @Override
    public Component getTableCellRendererComponent(
            JTable aTable, Object aNumberValue, boolean aIsSelected,
            boolean aHasFocus, int aRow, int aColumn) {
        // copied  from https://stackoverflow.com/questions/37205455/swing-wrap-text-in-a-tables-cell
        String value = (String) aNumberValue;

        textarea = new JTextArea();
        aTable.add(textarea);
        textarea.setWrapStyleWord(true);
        textarea.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) textarea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        textarea.setText(value);
        aTable.setRowHeight(180);

        if (aNumberValue == null) return this;

        Component renderer = super.getTableCellRendererComponent(
                aTable, aNumberValue, aIsSelected, aHasFocus, aRow, aColumn
        );


        if (value.equals("Me"))
            renderer.setForeground(Color.red);
        else
            renderer.setForeground(Color.black);

        return this;
    }
}
