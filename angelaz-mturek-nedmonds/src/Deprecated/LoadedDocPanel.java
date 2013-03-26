package Deprecated;
//package View;
//
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.ArrayList;
//
//import javax.swing.GroupLayout;
//import javax.swing.JButton;
//import javax.swing.JLabel;
//import javax.swing.JList;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JTextField;
//import javax.swing.SwingConstants;
//
//public class LoadedDocPanel extends JPanel {
//
//    private Editor editor;
//    private static JList docsList;
//    private JButton createButton;
//    private JButton openButton;
//    private JLabel openLabel;
//    private JLabel createLabel;
//    private JLabel orLabel;
//    private JLabel loadingMessageLabel;
//    private JLabel docNameLabel;
//    private JTextField docNameTextField;
//
//    public LoadedDocPanel(Object[] list, final Editor editor) {
//        this.editor = editor;
//
//        GroupLayout layout = new GroupLayout(this);
//        this.setLayout(layout);
//
//        // get some margins around components by default
//        layout.setAutoCreateContainerGaps(true);
//        layout.setAutoCreateGaps(true);
//
//        openLabel = new JLabel("Open");
//
//        createLabel = new JLabel("Create");
//        docNameLabel = new JLabel("Document Name:");
//        orLabel = new JLabel("OR", SwingConstants.CENTER);
//        orLabel.setMinimumSize(new Dimension(100, 20));
//
//        ArrayList<String> docNames = new ArrayList<String>();
//        if (list != null) {
//            for (Object item : list) {
//                String[] elements = ((String) item).split("\\~");
//                System.out.println(elements);
//                docNames.add(elements[1]);
//            }
//            System.out.println(docNames);
//            docsList = new JList(docNames.toArray());
//        } else {
//            String[] blank = { "N/A" };
//            docsList = new JList(blank);
//        }
//
//        docsList.setVisible(true);
//        docsList.setMinimumSize(new Dimension(0, 200));
//
//        docNameTextField = new JTextField();
//        docNameTextField.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ae) {
//                String newName = docNameTextField.getText();
//                docNameTextField.setText("");
//                editor.sendMessage(editor.createControlMessage("requestNew",
//                        -1, newName));
//            }
//        });
//
//        createButton = new JButton();
//        createButton.setText("Create");
//        createButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ae) {
//                String newName = docNameTextField.getText();
//                docNameTextField.setText("");
//                editor.sendMessage(editor.createControlMessage("requestNew",
//                        -1, newName));
//
//            }
//        });
//
//        openButton = new JButton();
//        openButton.setText("Open");
//        openButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ae) {
//                String name = (String) docsList.getSelectedValue();
//                String[] broken = name.split("~");
//
//                if (editor.docs.containsKey(Integer.parseInt(broken[0]))) {
//                    JOptionPane.showMessageDialog(Editor.tabbedPane,
//                            "That document is already open!");
//                    return;
//                }
//
//                editor.getUser().addDocument(Integer.parseInt(broken[0]));
//                DocPanel newDoc = new DocPanel(Integer.parseInt(broken[0]),
//                        broken[1], editor);
//                Editor.tabbedPane.add(broken[1], newDoc);
//                editor.docs.put(Integer.parseInt(broken[0]), newDoc);
//                editor.sendMessage(editor.createControlMessage("load",
//                        Integer.parseInt(broken[0]), broken[1]));
//                Editor.tabbedPane.remove(Editor.tabbedPane.getTabCount() - 2);
//                Editor.tabbedPane.add("+", new NewDocPanel(editor));
//                editor.initTabComponent(Editor.tabbedPane.getTabCount() - 2);
//                Editor.tabbedPane.setSelectedIndex(Editor.tabbedPane.getTabCount()-2);
//
//            }
//        });
//
//        layout.setHorizontalGroup(layout
//                .createSequentialGroup()
//                .addGroup(
//                        layout.createParallelGroup(
//                                GroupLayout.Alignment.LEADING)
//                                .addComponent(openLabel)
//                                .addComponent(docsList, 0,
//                                        GroupLayout.DEFAULT_SIZE,
//                                        Short.MAX_VALUE)
//                                .addComponent(openButton))
//                .addComponent(orLabel)
//                .addGroup(
//                        layout.createParallelGroup(
//                                GroupLayout.Alignment.LEADING)
//                                .addComponent(createLabel)
//                                .addComponent(docNameLabel)
//                                .addComponent(docNameTextField, 0,
//                                        GroupLayout.DEFAULT_SIZE,
//                                        Short.MAX_VALUE)
//                                .addComponent(createButton)));
//
//        layout.setVerticalGroup(layout
//                .createSequentialGroup()
//                .addGroup(
//                        layout.createParallelGroup(
//                                GroupLayout.Alignment.LEADING)
//                                .addComponent(openLabel).addComponent(orLabel)
//                                .addComponent(createLabel))
//                .addGroup(
//                        layout.createParallelGroup(
//                                GroupLayout.Alignment.LEADING)
//                                .addComponent(docsList,
//                                        GroupLayout.DEFAULT_SIZE,
//                                        GroupLayout.DEFAULT_SIZE,
//                                        GroupLayout.DEFAULT_SIZE)
//                                .addGroup(
//                                        layout.createSequentialGroup()
//                                                .addComponent(docNameLabel)
//                                                .addComponent(
//                                                        docNameTextField,
//                                                        GroupLayout.PREFERRED_SIZE,
//                                                        GroupLayout.DEFAULT_SIZE,
//                                                        GroupLayout.PREFERRED_SIZE)
//                                                .addComponent(createButton)))
//                .addGroup(
//                        layout.createParallelGroup(
//                                GroupLayout.Alignment.LEADING)
//                                .addComponent(openButton)
//                                .addComponent(createButton)));
//
//    }
//
//    @Override
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//    }
//
//    @Override
//    public void repaint() {
//        super.repaint();
//    }
//
//}
