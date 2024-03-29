// ---------------------------------------------------------------------------
// jWebSocket - < Description/Name of the Module >
// Copyright(c) 2010-212 Innotrade GmbH, Herzogenrath, Germany, jWebSocket.org
// ---------------------------------------------------------------------------
// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published by the
// Free Software Foundation; either version 3 of the License, or (at your
// option) any later version.
// This program is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
// more details.
// You should have received a copy of the GNU Lesser General Public License along
// with this program; if not, see <http://www.gnu.org/licenses/lgpl.html>.
// ---------------------------------------------------------------------------
package org.jwebsocket.watchdog.forms;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javolution.util.FastList;
import org.jwebsocket.watchdog.api.IWatchDogTask;
import org.jwebsocket.watchdog.test.WatchDogTask;
import org.jwebsocket.watchdog.test.WatchDogTest;
import org.jwebsocket.watchdog.test.WatchDogTestService;

/**
 *
 * @author Lester Alfonso Zaila Viejo(telnet_1, UCI, Artemisa)
 */
public class AddTask extends javax.swing.JDialog {

    public IWatchDogTask mWatchDogTask;
    public WatchDogTestService mTestService;
    public DefaultTableModel mModelTest;
    static Boolean mType;

    /**
     * Creates new form AddTask
     */
    public AddTask(java.awt.Frame parent, boolean modal, WatchDogTask aTask, WatchDogTestService aTestService, Boolean atype) throws UnknownHostException {
        super(parent, modal);
        initComponents();
        this.setLocation(200, 200);
        AddTask.mType = atype;
        mWatchDogTask = aTask;
        mTestService = aTestService;
        jPanel1.setVisible(false);


        loadTestTable();
    }

    public AddTask(java.awt.Frame parent, boolean modal, WatchDogTestService aTestService, Boolean atype) throws UnknownHostException {
        super(parent, modal);
        initComponents();
        AddTask.mType = atype;
        mTestService = aTestService;
        jPanel1.setVisible(false);

        loadTestTable();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jtfID = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jcbFrequency = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtTests = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Id");

        jButton1.setText("Accept");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jcbFrequency.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select...", "Every N Minutes", "Every N Hours", "Every N Days" }));
        jcbFrequency.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jcbFrequencyMouseClicked(evt);
            }
        });
        jcbFrequency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbFrequencyActionPerformed(evt);
            }
        });

        jtTests.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jtTests);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel2.setText("Every");

        jSpinner1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jSpinner1MouseClicked(evt);
            }
        });

        jLabel3.setText("Day(s)");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(53, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Exit");
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(7, 7, 7)
                        .addComponent(jtfID, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jcbFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jtfID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(11, 11, 11)
                        .addComponent(jcbFrequency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initTable() {
        //Setting the name of the columns of the Test table
        String[] lColumnsNamesTest = new String[]{"id", "Description", "Implementation Class", "Fatal"};
        mModelTest = new DefaultTableModel(lColumnsNamesTest, 0);
        jtTests.setModel(mModelTest);
//        jTable1.setCellSelectionEnabled(true);
    }

    private void loadTestTable() {
        try {
            initTable();

            List<WatchDogTest> lList = mTestService.list();
            String lFatal = "No";

            for (WatchDogTest i : lList) {
                if (i.isFatal()) {
                    lFatal = "Yes";
                }
                mModelTest.addRow(new String[]{i.getId().toString(), i.getDescription().toString(), i.getImplClass().toString(), lFatal});
                lFatal = "No";
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    if (AddTask.mType) {
        //Creatint the Task
        String lId = jtfID.getText().toString();

        Integer lEveryNMinutes = -1;
        Integer lEveryNHours = -1;
        Integer lEveryNDays = -1;

        String lType = "";

        try {
            List<WatchDogTest> lList = mTestService.list();
            List<String> lList2 = new FastList<String>();

            if (!jtfID.getText().isEmpty()) {
                mWatchDogTask.setId(lId);
            } else {
                throw new Exception("You most enter a valid ID");
            }

            /*
             * Select... Every N Minutes Every N Hours Every N Days
             */
            if (jcbFrequency.getSelectedItem().toString().equals("Select...")) {
                throw new Exception("You Should Select a frequency to Create a TASK");
            }
            if (jcbFrequency.getSelectedItem().toString().equals("Every N Minutes")) {
                lEveryNMinutes = Integer.valueOf(jSpinner1.getValue().toString());
                lType = "m";
            }
            if (jcbFrequency.getSelectedItem().toString().equals("Every N Hours")) {
                lEveryNHours = Integer.valueOf(jSpinner1.getValue().toString());
                lType = "h";
            }
            if (jcbFrequency.getSelectedItem().toString().equals("Every N Days")) {
                lEveryNDays = Integer.valueOf(jSpinner1.getValue().toString());
                lType = "d";
            }
            
            if(Integer.valueOf(jSpinner1.getValue().toString()).equals(0))
            {
                throw new Exception("Zero value is not allowed");
            }
            
            if(Integer.valueOf(jSpinner1.getValue().toString())< 0)
            {
                throw new Exception("Negative value is not allowed");
            }
            
            if(Integer.valueOf(jSpinner1.getValue().toString())< 0)
            {
                throw new Exception("Negative value is not allowed");
            }
            
            int[] lSelected = jtTests.getSelectedRows();
            if (lSelected.length == 0) {
                throw new Exception("You should select at list one test");
            }

            for (int i = 0; i < lSelected.length; i++) {
                mWatchDogTask.getTests().add(lList.get(lSelected[i]));
            }

            mWatchDogTask.setType(lType);
            mWatchDogTask.setEveryNMinutes(lEveryNMinutes);
            mWatchDogTask.setEveryNHours(lEveryNHours);
            mWatchDogTask.setEveryNDays(lEveryNDays);
            mWatchDogTask.setLastExecution(new Date().toString());

            this.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    if (!AddTask.mType) {
        //Updating Task
        String lId = jtfID.getText().toString();

        Integer lEveryNMinutes = -1;
        Integer lEveryNHours = -1;
        Integer lEveryNDays = -1;

        String lType = "";

        try {
            List<WatchDogTest> lList = mTestService.list();
            List<String> lList2 = new FastList<String>();

            if (!jtfID.getText().isEmpty()) {
                mWatchDogTask.setId(lId);
            } else {
                throw new Exception("You most enter a valid ID");
            }
            /*
             * Select... Every N Minutes Every N Hours Every N Days
             */

            if (!jSpinner1.getValue().equals(0)) {
                throw new Exception("0 is not an option, please select a correct time");
            }
            if (jcbFrequency.getSelectedItem().toString().equals("Select...")) {

                throw new Exception("You Should Select a frequency to Create a TASK");
            }
            if (jcbFrequency.getSelectedItem().toString().equals("Every N Minutes")) {
                lEveryNMinutes = Integer.valueOf(jSpinner1.getValue().toString());
                lType = "m";
            }
            if (jcbFrequency.getSelectedItem().toString().equals("Every N Hours")) {
                lEveryNHours = Integer.valueOf(jSpinner1.getValue().toString());
                lType = "h";
            }
            if (jcbFrequency.getSelectedItem().toString().equals("Every N Days")) {
                lEveryNDays = Integer.valueOf(jSpinner1.getValue().toString());
                lType = "d";
            }

            int[] lSelected = jtTests.getSelectedRows();
            if (lSelected.length == 0) {
                throw new Exception("You should select at list one test");
            }

            for (int i = 0; i < lSelected.length; i++) {
                mWatchDogTask.getTests().add(lList.get(lSelected[i]));
            }

            mWatchDogTask.setType(lType);
            mWatchDogTask.setEveryNMinutes(lEveryNMinutes);
            mWatchDogTask.setEveryNHours(lEveryNHours);
            mWatchDogTask.setEveryNDays(lEveryNDays);
            mWatchDogTask.setLastExecution(new Date().toString());

            this.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }

    }

}//GEN-LAST:event_jButton1ActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

    //on forWindows closing
    mWatchDogTask = null;
    this.dispose();
}//GEN-LAST:event_formWindowClosing

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    //on forWindows closing
    mWatchDogTask = null;
    this.dispose();
}//GEN-LAST:event_jButton2ActionPerformed

private void jcbFrequencyMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jcbFrequencyMouseClicked
}//GEN-LAST:event_jcbFrequencyMouseClicked

private void jcbFrequencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbFrequencyActionPerformed
    /*
     * Select... Every N Minutes Every N Hours Every N Days
     */

    if (jcbFrequency.getSelectedItem().toString().equals("Select...")) {
        jPanel1.setVisible(false);
    }

    if (jcbFrequency.getSelectedItem().toString().equals("Every N Minutes")) {
        jPanel1.setVisible(true);
        jLabel3.setText("Minute(s)");
        jPanel1.setSize(240, 79);

    }

    if (jcbFrequency.getSelectedItem().toString().equals("Every N Hours")) {
        jPanel1.setVisible(true);
        jLabel3.setText("Hour(s)");
        jPanel1.setSize(240, 79);

    }

    if (jcbFrequency.getSelectedItem().toString().equals("Every N Days")) {
        jPanel1.setVisible(true);
        jLabel3.setText("Day(s)");
        jPanel1.setSize(240, 79);

    }
}//GEN-LAST:event_jcbFrequencyActionPerformed

private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
// Windos opened

    if (!AddTask.mType) {
        this.setTitle("Update Task");
        jButton1.setText("Update");
        jtfID.setText(mWatchDogTask.getId());



    } else {
        this.setTitle("Add Task");
        jButton1.setText("Add");

    }

}//GEN-LAST:event_formWindowOpened

    private void jSpinner1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSpinner1MouseClicked
       
    }//GEN-LAST:event_jSpinner1MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JComboBox jcbFrequency;
    private javax.swing.JTable jtTests;
    private javax.swing.JTextField jtfID;
    // End of variables declaration//GEN-END:variables
}
