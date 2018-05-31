package fireup;

import java.awt.Desktop;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import se.util.Logger;

/**
 *
 * @author mmp
 */
public class MainPage extends javax.swing.JFrame implements Reportable {

    public static NodeManager fireupModel;
    Vector<String> listModel;

    public MainPage() {
        initComponents();
        listModel = new Vector<>();
        procList.setListData(listModel);
        setTitle("Fire up");
        hostInfo.setText(
                " IP : " + fireupModel.getHost() + 
                " ipc-port : " + fireupModel.getIPCPort() + 
                " http-port : " + fireupModel.getHttpPort()
        );
    }

    
    HashMap<Integer, String> pidmapper = new HashMap<>();
    public void processAdded(String pid) {
        WrappedProcess wp = fireupModel.processes.get(pid);
        pidmapper.put(listModel.size(), pid);
        String str = Arrays.toString(wp.cmdline);
        listModel.add(str);
        procList.updateUI();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        jPanel1 = new javax.swing.JPanel();
        hostInfo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        nProcesses = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        procList = new javax.swing.JList<>();
        viewLog = new javax.swing.JButton();
        viewOutput = new javax.swing.JButton();
        terminate = new javax.swing.JButton();
        terminateAll = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        hostInfo.setText("Host Info");

        jLabel1.setText("Number of Processes : ");

        nProcesses.setText("0");

        procList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, procList, org.jdesktop.beansbinding.ObjectProperty.create(), procList, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);

        jScrollPane1.setViewportView(procList);

        viewLog.setText("View Log");
        viewLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewLogActionPerformed(evt);
            }
        });

        viewOutput.setText("View Output");
        viewOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewOutputActionPerformed(evt);
            }
        });

        terminate.setText("Kill");
        terminate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                terminateActionPerformed(evt);
            }
        });

        terminateAll.setText("Kill All");
        terminateAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                terminateAllActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(hostInfo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(nProcesses))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(viewLog)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(viewOutput)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(terminate)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(terminateAll)))
                                .addGap(0, 151, Short.MAX_VALUE)))
                        .addGap(373, 373, 373))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(hostInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nProcesses))
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(viewLog)
                    .addComponent(viewOutput)
                    .addComponent(terminate)
                    .addComponent(terminateAll))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void terminateAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_terminateAllActionPerformed
        for (int i = 0; i < listModel.size(); i++) {
            fireupModel.processes.get(i).kill();
            listModel.remove(i);
        }
    }//GEN-LAST:event_terminateAllActionPerformed

    private void terminateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_terminateActionPerformed
        int index = procList.getSelectedIndex();
        if (index > -1) {
            fireupModel.processes.get(pidmapper.get(index)).kill();
            listModel.remove(index);
        }
    }//GEN-LAST:event_terminateActionPerformed

    private void viewOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewOutputActionPerformed
        int index = procList.getSelectedIndex();
        if (index > -1) {
            showOutput(index);
        }
    }//GEN-LAST:event_viewOutputActionPerformed

    private void viewLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewLogActionPerformed
        int index = procList.getSelectedIndex();
        if (index > -1) {
            showError(index);
        }
    }//GEN-LAST:event_viewLogActionPerformed

    private void openEditor(String file) {
        Desktop dt = Desktop.getDesktop();
        try {
            dt.open(new File(file));
        } catch (Exception e) {
            System.out.println("Couldn't open the file");
        }
    }

    private void showOutput(int index) {
        openEditor(fireupModel.processes.get(pidmapper.get(index)).getOutputFileName());
    }

    private void showError(int index) {
        openEditor(fireupModel.processes.get(pidmapper.get(index)).getErrorFileName());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel hostInfo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel nProcesses;
    private javax.swing.JList<String> procList;
    private javax.swing.JButton terminate;
    private javax.swing.JButton terminateAll;
    private javax.swing.JButton viewLog;
    private javax.swing.JButton viewOutput;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setStatus(String status) {
        Logger.elog(Logger.HIGH, status);
    }

}
