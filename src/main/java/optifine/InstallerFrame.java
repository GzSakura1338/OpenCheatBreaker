package optifine;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class InstallerFrame extends JFrame {
    public JLabel ivjLabelOfVersion = null;
    public JLabel ivjLabelMcVersion = null;
    public JPanel ivjPanelCenter = null;
    public JButton ivjButtonInstall = null;
    public JButton ivjButtonClose = null;
    public JPanel ivjPanelBottom = null;
    public JPanel ivjPanelContentPane = null;
    public InstallerFrame.IvjEventHandler ivjEventHandler = new InstallerFrame.IvjEventHandler();
    public JTextArea ivjTextArea = null;
    public JButton ivjButtonExtract = null;
    public JLabel ivjLabelFolder = null;
    public JTextField ivjFieldFolder = null;
    public JButton ivjButtonFolder = null;

    public InstallerFrame() {
        this.initialize();
    }

    public void customInit() {
        try {
            this.pack();
            this.setDefaultCloseOperation(3);
            File workingDirectory = Utils.getWorkingDirectory();
            this.getFieldFolder().setText(workingDirectory.getPath());
            this.getButtonInstall().setEnabled(false);
            this.getButtonExtract().setEnabled(false);
            String optiFineVersion = Installer.getOptiFineVersion();
            Utils.dbg("OptiFine Version: " + optiFineVersion);
            String[] tokenize = Utils.tokenize(optiFineVersion, "_");
            String str = tokenize[1];
            Utils.dbg("Minecraft Version: " + str);
            String optiFineEdition = Installer.getOptiFineEdition(tokenize);
            Utils.dbg("OptiFine Edition: " + optiFineEdition);
            String replace = optiFineEdition.replace("_", " ");
            replace = replace.replace(" U ", " Ultra ");
            replace = replace.replace("L ", "Light ");
            this.getLabelOfVersion().setText("OptiFine " + replace);
            this.getLabelMcVersion().setText("for Minecraft " + str);
            this.getButtonInstall().setEnabled(true);
            this.getButtonExtract().setEnabled(true);
            this.getButtonInstall().requestFocus();
            if (!Installer.isPatchFile()) {
                this.getButtonExtract().setVisible(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] strs) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            InstallerFrame installerFrame = new InstallerFrame();
            Utils.centerWindow(installerFrame, (Component)null);
            installerFrame.show();
        } catch (Exception e) {
            String message = e.getMessage();
            if (message != null && message.equals("QUIET")) {
                return;
            }

            e.printStackTrace();
            String exceptionStackTrace = Utils.getExceptionStackTrace(e);
            exceptionStackTrace = exceptionStackTrace.replace("\t", "  ");
            JTextArea jtextArea = new JTextArea(exceptionStackTrace);
            jtextArea.setEditable(false);
            Font font = jtextArea.getFont();
            Font font2 = new Font("Monospaced", font.getStyle(), font.getSize());
            jtextArea.setFont(font2);
            JScrollPane jscrollPane = new JScrollPane(jtextArea);
            jscrollPane.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog((Component)null, jscrollPane, "Error", 0);
        }
    }

    public void handleException(Throwable throwable) {
        String message = throwable.getMessage();
        if (message == null || !message.equals("QUIET")) {
            throwable.printStackTrace();
            String exceptionStackTrace = Utils.getExceptionStackTrace(throwable);
            exceptionStackTrace = exceptionStackTrace.replace("\t", "  ");
            JTextArea jtextArea = new JTextArea(exceptionStackTrace);
            jtextArea.setEditable(false);
            Font font = jtextArea.getFont();
            Font font2 = new Font("Monospaced", font.getStyle(), font.getSize());
            jtextArea.setFont(font2);
            JScrollPane jscrollPane = new JScrollPane(jtextArea);
            jscrollPane.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog((Component)null, jscrollPane, "Error", 0);
        }
    }

    public JLabel getLabelOfVersion() {
        if (this.ivjLabelOfVersion == null) {
            try {
                this.ivjLabelOfVersion = new JLabel();
                this.ivjLabelOfVersion.setName("LabelOfVersion");
                this.ivjLabelOfVersion.setBounds(2, 5, 385, 42);
                this.ivjLabelOfVersion.setFont(new Font("Dialog", 1, 18));
                this.ivjLabelOfVersion.setHorizontalAlignment(0);
                this.ivjLabelOfVersion.setPreferredSize(new Dimension(385, 42));
                this.ivjLabelOfVersion.setText("OptiFine ...");
            } catch (Throwable e) {
                this.handleException(e);
            }
        }

        return this.ivjLabelOfVersion;
    }

    public JLabel getLabelMcVersion() {
        if (this.ivjLabelMcVersion == null) {
            try {
                this.ivjLabelMcVersion = new JLabel();
                this.ivjLabelMcVersion.setName("LabelMcVersion");
                this.ivjLabelMcVersion.setBounds(2, 38, 385, 25);
                this.ivjLabelMcVersion.setFont(new Font("Dialog", 1, 14));
                this.ivjLabelMcVersion.setHorizontalAlignment(0);
                this.ivjLabelMcVersion.setPreferredSize(new Dimension(385, 25));
                this.ivjLabelMcVersion.setText("for Minecraft ...");
            } catch (Throwable e) {
                this.handleException(e);
            }
        }

        return this.ivjLabelMcVersion;
    }

    public JPanel getPanelCenter() {
        if (this.ivjPanelCenter == null) {
            try {
                this.ivjPanelCenter = new JPanel();
                this.ivjPanelCenter.setName("PanelCenter");
                this.ivjPanelCenter.setLayout((LayoutManager)null);
                this.ivjPanelCenter.add(this.getLabelOfVersion(), this.getLabelOfVersion().getName());
                this.ivjPanelCenter.add(this.getLabelMcVersion(), this.getLabelMcVersion().getName());
                this.ivjPanelCenter.add(this.getTextArea(), this.getTextArea().getName());
                this.ivjPanelCenter.add(this.getLabelFolder(), this.getLabelFolder().getName());
                this.ivjPanelCenter.add(this.getFieldFolder(), this.getFieldFolder().getName());
                this.ivjPanelCenter.add(this.getButtonFolder(), this.getButtonFolder().getName());
            } catch (Throwable e) {
                this.handleException(e);
            }
        }

        return this.ivjPanelCenter;
    }

    public JButton getButtonInstall() {
        if (this.ivjButtonInstall == null) {
            try {
                this.ivjButtonInstall = new JButton();
                this.ivjButtonInstall.setName("ButtonInstall");
                this.ivjButtonInstall.setPreferredSize(new Dimension(100, 26));
                this.ivjButtonInstall.setText("Install");
            } catch (Throwable e) {
                this.handleException(e);
            }
        }

        return this.ivjButtonInstall;
    }

    public JButton getButtonClose() {
        if (this.ivjButtonClose == null) {
            try {
                this.ivjButtonClose = new JButton();
                this.ivjButtonClose.setName("ButtonClose");
                this.ivjButtonClose.setPreferredSize(new Dimension(100, 26));
                this.ivjButtonClose.setText("Cancel");
            } catch (Throwable e) {
                this.handleException(e);
            }
        }

        return this.ivjButtonClose;
    }

    public JPanel getPanelBottom() {
        if (this.ivjPanelBottom == null) {
            try {
                this.ivjPanelBottom = new JPanel();
                this.ivjPanelBottom.setName("PanelBottom");
                this.ivjPanelBottom.setLayout(new FlowLayout(1, 15, 10));
                this.ivjPanelBottom.setPreferredSize(new Dimension(390, 55));
                this.ivjPanelBottom.add(this.getButtonInstall(), this.getButtonInstall().getName());
                this.ivjPanelBottom.add(this.getButtonExtract(), this.getButtonExtract().getName());
                this.ivjPanelBottom.add(this.getButtonClose(), this.getButtonClose().getName());
            } catch (Throwable e) {
                this.handleException(e);
            }
        }

        return this.ivjPanelBottom;
    }

    public JPanel getPanelContentPane() {
        if (this.ivjPanelContentPane == null) {
            try {
                this.ivjPanelContentPane = new JPanel();
                this.ivjPanelContentPane.setName("PanelContentPane");
                this.ivjPanelContentPane.setLayout(new BorderLayout(5, 5));
                this.ivjPanelContentPane.setPreferredSize(new Dimension(394, 203));
                this.ivjPanelContentPane.add(this.getPanelCenter(), "Center");
                this.ivjPanelContentPane.add(this.getPanelBottom(), "South");
            } catch (Throwable e) {
                this.handleException(e);
            }
        }

        return this.ivjPanelContentPane;
    }

    public void initialize() {
        try {
            this.setName("InstallerFrame");
            this.setSize(404, 236);
            this.setDefaultCloseOperation(0);
            this.setResizable(false);
            this.setTitle("OptiFine Installer");
            this.setContentPane(this.getPanelContentPane());
            this.initConnections();
        } catch (Throwable e) {
            this.handleException(e);
        }

        this.customInit();
    }

    public void onInstall() {
        try {
            File file = new File(this.getFieldFolder().getText());
            if (!file.exists()) {
                Utils.showErrorMessage("Folder not found: " + file.getPath());
                return;
            }

            if (!file.isDirectory()) {
                Utils.showErrorMessage("Not a folder: " + file.getPath());
                return;
            }

            Installer.doInstall(file);
            Utils.showMessage("OptiFine is successfully installed.");
            this.dispose();
        } catch (Exception e) {
            this.handleException(e);
        }
    }

    public void onExtract() {
        try {
            File file = new File(this.getFieldFolder().getText());
            if (!file.exists()) {
                Utils.showErrorMessage("Folder not found: " + file.getPath());
                return;
            }

            if (!file.isDirectory()) {
                Utils.showErrorMessage("Not a folder: " + file.getPath());
                return;
            }

            boolean doExtract = Installer.doExtract(file);
            if (doExtract) {
                Utils.showMessage("OptiFine is successfully extracted.");
                this.dispose();
            }
        } catch (Exception e) {
            this.handleException(e);
        }
    }

    public void onClose() {
        this.dispose();
    }

    public void connEtoC1(ActionEvent actionEvent) {
        try {
            this.onInstall();
        } catch (Throwable e) {
            this.handleException(e);
        }
    }

    public void connEtoC2(ActionEvent actionEvent) {
        try {
            this.onClose();
        } catch (Throwable e) {
            this.handleException(e);
        }
    }

    public void initConnections() throws Exception {
        this.getButtonFolder().addActionListener(this.ivjEventHandler);
        this.getButtonInstall().addActionListener(this.ivjEventHandler);
        this.getButtonExtract().addActionListener(this.ivjEventHandler);
        this.getButtonClose().addActionListener(this.ivjEventHandler);
    }

    public JTextArea getTextArea() {
        if (this.ivjTextArea == null) {
            try {
                this.ivjTextArea = new JTextArea();
                this.ivjTextArea.setName("TextArea");
                this.ivjTextArea.setBounds(15, 66, 365, 44);
                this.ivjTextArea.setEditable(false);
                this.ivjTextArea.setEnabled(true);
                this.ivjTextArea.setFont(new Font("Dialog", 0, 12));
                this.ivjTextArea.setLineWrap(true);
                this.ivjTextArea.setOpaque(false);
                this.ivjTextArea.setPreferredSize(new Dimension(365, 44));
                this.ivjTextArea
                    .setText("This installer will install OptiFine in the official Minecraft launcher and will create a new profile \"OptiFine\" for it.");
                this.ivjTextArea.setWrapStyleWord(true);
            } catch (Throwable e) {
                this.handleException(e);
            }
        }

        return this.ivjTextArea;
    }

    public JButton getButtonExtract() {
        if (this.ivjButtonExtract == null) {
            try {
                this.ivjButtonExtract = new JButton();
                this.ivjButtonExtract.setName("ButtonExtract");
                this.ivjButtonExtract.setPreferredSize(new Dimension(100, 26));
                this.ivjButtonExtract.setText("Extract");
            } catch (Throwable e) {
                this.handleException(e);
            }
        }

        return this.ivjButtonExtract;
    }

    public void connEtoC3(ActionEvent actionEvent) {
        try {
            this.onExtract();
        } catch (Throwable e) {
            this.handleException(e);
        }
    }

    public JLabel getLabelFolder() {
        if (this.ivjLabelFolder == null) {
            try {
                this.ivjLabelFolder = new JLabel();
                this.ivjLabelFolder.setName("LabelFolder");
                this.ivjLabelFolder.setBounds(15, 116, 47, 16);
                this.ivjLabelFolder.setPreferredSize(new Dimension(47, 16));
                this.ivjLabelFolder.setText("Folder");
            } catch (Throwable e) {
                this.handleException(e);
            }
        }

        return this.ivjLabelFolder;
    }

    public JTextField getFieldFolder() {
        if (this.ivjFieldFolder == null) {
            try {
                this.ivjFieldFolder = new JTextField();
                this.ivjFieldFolder.setName("FieldFolder");
                this.ivjFieldFolder.setBounds(62, 114, 287, 20);
                this.ivjFieldFolder.setEditable(false);
                this.ivjFieldFolder.setPreferredSize(new Dimension(287, 20));
            } catch (Throwable e) {
                this.handleException(e);
            }
        }

        return this.ivjFieldFolder;
    }

    public JButton getButtonFolder() {
        if (this.ivjButtonFolder == null) {
            try {
                this.ivjButtonFolder = new JButton();
                this.ivjButtonFolder.setName("ButtonFolder");
                this.ivjButtonFolder.setBounds(350, 114, 25, 20);
                this.ivjButtonFolder.setMargin(new Insets(2, 2, 2, 2));
                this.ivjButtonFolder.setPreferredSize(new Dimension(25, 20));
                this.ivjButtonFolder.setText("...");
            } catch (Throwable e) {
                this.handleException(e);
            }
        }

        return this.ivjButtonFolder;
    }

    public void onFolderSelect() {
        File file = new File(this.getFieldFolder().getText());
        JFileChooser jfileChooser = new JFileChooser(file);
        jfileChooser.setFileSelectionMode(1);
        jfileChooser.setAcceptAllFileFilterUsed(false);
        if (jfileChooser.showOpenDialog(this) == 0) {
            File selectedFile = jfileChooser.getSelectedFile();
            this.getFieldFolder().setText(selectedFile.getPath());
        }
    }

    public void connEtoC4(ActionEvent actionEvent) {
        try {
            this.onFolderSelect();
        } catch (Throwable e) {
            this.handleException(e);
        }
    }

    class IvjEventHandler implements ActionListener {
        public IvjEventHandler() {
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (actionEvent.getSource() == InstallerFrame.this.getButtonClose()) {
                InstallerFrame.this.connEtoC2(actionEvent);
            }

            if (actionEvent.getSource() == InstallerFrame.this.getButtonExtract()) {
                InstallerFrame.this.connEtoC3(actionEvent);
            }

            if (actionEvent.getSource() == InstallerFrame.this.getButtonFolder()) {
                InstallerFrame.this.connEtoC4(actionEvent);
            }

            if (actionEvent.getSource() == InstallerFrame.this.getButtonInstall()) {
                InstallerFrame.this.connEtoC1(actionEvent);
            }
        }
    }
}
