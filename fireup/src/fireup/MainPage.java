package fireup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Vector;
import javax.swing.JFileChooser;

/**
 *
 * @author mmp
 */
public class MainPage extends javax.swing.JFrame {

    private static Fireup fireupModel;
    Vector<String> listModel;

    public MainPage() {
        initComponents();
        listModel = new Vector<>();
        procList.setListData(listModel);
        setTitle("Fire up");
        hostInfo.setText("Host Info : { IP : " + fireupModel.getInetAddress() + ", port : " + fireupModel.getrunningPort() + "}");
    }

    public void processAdded() {
        WrappedProcess wp = fireupModel.processes.get(fireupModel.processes.size() - 1);
        String str = wp.procName + "       <" + wp.startTime + ">";
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
        jLabel2 = new javax.swing.JLabel();
        ExecutablePath = new javax.swing.JTextField();
        Browse = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        extCommandLine = new javax.swing.JTextField();
        ExternalProgramRun = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        protocol = new javax.swing.JTextField();
        ProtocolRunner = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        protocolOutput = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        MasterIP = new javax.swing.JTextField();
        MasterPort = new javax.swing.JTextField();
        MasterConnect = new javax.swing.JButton();

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

        jLabel2.setForeground(new java.awt.Color(0, 111, 36));
        jLabel2.setText("Run an external program");

        Browse.setText("Browse");
        Browse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BrowseActionPerformed(evt);
            }
        });

        jLabel5.setText("cmdline");

        ExternalProgramRun.setText("Run");
        ExternalProgramRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExternalProgramRunActionPerformed(evt);
            }
        });

        jLabel3.setText("Choose an executable");

        jLabel4.setForeground(new java.awt.Color(0, 111, 36));
        jLabel4.setText("Execute protocol. (use $ for newline)");

        ProtocolRunner.setText("Run");
        ProtocolRunner.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ProtocolRunnerActionPerformed(evt);
            }
        });

        protocolOutput.setEditable(false);
        protocolOutput.setBackground(new java.awt.Color(0, 0, 0));
        protocolOutput.setColumns(20);
        protocolOutput.setForeground(new java.awt.Color(24, 184, 1));
        protocolOutput.setLineWrap(true);
        protocolOutput.setRows(5);
        protocolOutput.setWrapStyleWord(true);
        jScrollPane2.setViewportView(protocolOutput);

        jLabel6.setForeground(new java.awt.Color(0, 111, 36));
        jLabel6.setText("Connect to master");

        MasterIP.setText("127.0.0.1");

        MasterPort.setText("5001");

        MasterConnect.setText("Connect");
        MasterConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MasterConnectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
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
                                .addComponent(terminateAll))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(protocol, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                    .addComponent(jLabel4)
                                    .addComponent(extCommandLine))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ProtocolRunner)
                                    .addComponent(ExternalProgramRun, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addComponent(jLabel5)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(ExecutablePath)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Browse))
                            .addComponent(jLabel6)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(MasterIP)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(MasterPort, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(MasterConnect)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(viewLog)
                                    .addComponent(viewOutput)
                                    .addComponent(terminate)
                                    .addComponent(terminateAll))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(MasterIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(MasterPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(MasterConnect))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ExecutablePath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Browse))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(extCommandLine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ExternalProgramRun))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(protocol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ProtocolRunner))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
            fireupModel.processes.get(index).kill();
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

    private void BrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BrowseActionPerformed
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            ExecutablePath.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_BrowseActionPerformed

    private void ExternalProgramRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExternalProgramRunActionPerformed
        fireupModel.createProcess(ExecutablePath.getText(), extCommandLine.getText());
    }//GEN-LAST:event_ExternalProgramRunActionPerformed

    private void ProtocolRunnerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ProtocolRunnerActionPerformed
        try {
            Socket sock = new Socket(fireupModel.getInetAddress(), fireupModel.getrunningPort());
            OutputStream os = sock.getOutputStream();
            os.write(protocol.getText().getBytes());
            os.write(-1);
            InputStreamReader isr = new InputStreamReader(sock.getInputStream());
            protocolOutput.append("$: " + protocol.getText() + "\n");
            char buffer[] = new char[1024];
            isr.read(buffer);
            protocolOutput.append("> " + new String(buffer) + "\n");
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }//GEN-LAST:event_ProtocolRunnerActionPerformed

    private void MasterConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MasterConnectActionPerformed
        fireupModel.connectToMaster(MasterIP.getText(), Integer.parseInt(MasterPort.getText()));
    }//GEN-LAST:event_MasterConnectActionPerformed

    private void openEditor(String file) {
        try {
            Runtime.getRuntime().exec(new String[]{"gedit", file});
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
    
    private void showOutput(int index) {
        openEditor(fireupModel.processes.get(index).getOutputFileName());
    }

    private void showError(int index) {
        openEditor(fireupModel.processes.get(index).getErrorFileName());
    }

    public static void main(String args[]) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        try {
            fireupModel = new Fireup();
        } catch (IOException ex) {
            System.err.println(ex);
        }
        MainPage mainPage = new MainPage();
        fireupModel.setOberserver(mainPage);
        new Thread(fireupModel).start();

        java.awt.EventQueue.invokeLater(() -> {
            mainPage.setVisible(true);
        });

    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Browse;
    private javax.swing.JTextField ExecutablePath;
    private javax.swing.JButton ExternalProgramRun;
    private javax.swing.JButton MasterConnect;
    private javax.swing.JTextField MasterIP;
    private javax.swing.JTextField MasterPort;
    private javax.swing.JButton ProtocolRunner;
    private javax.swing.JTextField extCommandLine;
    private javax.swing.JLabel hostInfo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel nProcesses;
    private javax.swing.JList<String> procList;
    private javax.swing.JTextField protocol;
    private javax.swing.JTextArea protocolOutput;
    private javax.swing.JButton terminate;
    private javax.swing.JButton terminateAll;
    private javax.swing.JButton viewLog;
    private javax.swing.JButton viewOutput;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables

}
