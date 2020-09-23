import jxl.write.WriteException;

import java.awt.*;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.JPanel;

import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JProgressBar;


public class Interface extends JFrame {
  // variables
    private JTree arbre;
    private DefaultMutableTreeNode racine;
    private JScrollPane scrollPane;
    private JTextArea txtArea;
    private boolean started = false;
    private int nb_files = 0;
    public JProgressBar progressBar;

    public Interface(){

      // elements
        getContentPane().setBackground(new Color(42, 42, 42));
        this.setSize(1000, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("BioInfo");
        getContentPane().setLayout(null);

        ImageIcon II = new ImageIcon("Images/images.jpeg");
        ImageIcon icon = new ImageIcon(II.getImage().getScaledInstance(10, 10, Image.SCALE_DEFAULT));
        this.setIconImage(icon.getImage());

        JPanel left_panel = new JPanel();
        left_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        left_panel.setBackground(new Color(51, 51, 51));
        left_panel.setBounds(15, 16, 408, 528);
        getContentPane().add(left_panel);
        left_panel.setLayout(null);

        JPanel header_left_panel = new JPanel();
        header_left_panel.setBounds(0, 0, 408, 45);
        header_left_panel.setLayout(null);
        header_left_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        header_left_panel.setBackground(Color.DARK_GRAY);
        left_panel.add(header_left_panel);

        Label header_l_label = new Label("Arborescence des fichiers");
        header_l_label.setForeground(Color.WHITE);
        header_l_label.setFont(new Font("Dialog", Font.BOLD, 20));
        header_l_label.setAlignment(Label.CENTER);
        header_l_label.setBounds(10, 10, 388, 22);
        header_left_panel.add(header_l_label);

        JPanel color_info = new JPanel();
        color_info.setLayout(null);
        color_info.setBorder(new LineBorder(new Color(0, 0, 0)));
        color_info.setBackground(new Color(51, 51, 51));
        color_info.setBounds(0, 38, 408, 54);
        left_panel.add(color_info);

        JLabel green = new JLabel(" termin\u00E9");
        green.setBounds(35, 20, 89, 20);
        color_info.add(green);
        green.setForeground(Color.WHITE);
        green.setIcon(new ImageIcon("Images/green.png"));

        JLabel orange = new JLabel(" actualis\u00E9");
        orange.setBounds(129, 20, 89, 20);
        color_info.add(orange);
        orange.setIcon(new ImageIcon("Images/orange.png"));
        orange.setForeground(Color.WHITE);

        JLabel blue = new JLabel(" cr\u00E9\u00E9");
        blue.setBounds(233, 20, 89, 20);
        color_info.add(blue);
        blue.setIcon(new ImageIcon("Images/blue.png"));
        blue.setForeground(Color.WHITE);

        JLabel red = new JLabel(" erreur");
        red.setBounds(319, 20, 89, 20);
        color_info.add(red);
        red.setIcon(new ImageIcon("Images/red.png"));
        red.setForeground(Color.WHITE);

        JPanel right_top_panel = new JPanel();
        right_top_panel.setLayout(null);
        right_top_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        right_top_panel.setBackground(new Color(51, 51, 51));
        right_top_panel.setBounds(438, 16, 541, 252);
        getContentPane().add(right_top_panel);

        JPanel header_r_t_panel = new JPanel();
        header_r_t_panel.setLayout(null);
        header_r_t_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        header_r_t_panel.setBackground(Color.DARK_GRAY);
        header_r_t_panel.setBounds(0, 0, 541, 45);
        right_top_panel.add(header_r_t_panel);

        Label header_r_t_label = new Label("Informations");
        header_r_t_label.setForeground(Color.WHITE);
        header_r_t_label.setFont(new Font("Dialog", Font.BOLD, 20));
        header_r_t_label.setAlignment(Label.CENTER);
        header_r_t_label.setBounds(10, 10, 521, 22);
        header_r_t_panel.add(header_r_t_label);

        JButton btnNewButton = new JButton("D\u00E9marrer");
        btnNewButton.setBounds(310, 207, 159, 29);
        right_top_panel.add(btnNewButton);


        progressBar = new JProgressBar();
        progressBar.setBounds(80, 112, 389, 38);
        progressBar.setVisible(false);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        right_top_panel.add(progressBar);
        ///
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(started == false) {

                    started = true;
                    AddMsgLog("en cours de démarrage");
                    progressBar.setVisible(true);
                    Thread T = new Thread(new download());
                    T.start();
                    //parcourirNoeud(arbre.getModel().getRoot());
                }

                listRoot();
                arbre.setCellRenderer(new FileTreeCellRenderer());
                arbre.setRootVisible(true);
                // }
            }
        } );

        // elements
        JPanel right_bottom_panel = new JPanel();
        right_bottom_panel.setLayout(null);
        right_bottom_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        right_bottom_panel.setBackground(new Color(51, 51, 51));
        right_bottom_panel.setBounds(438, 284, 541, 260);
        getContentPane().add(right_bottom_panel);

        JPanel header_r_b_panel = new JPanel();
        header_r_b_panel.setLayout(null);
        header_r_b_panel.setBorder(new LineBorder(new Color(0, 0, 0)));
        header_r_b_panel.setBackground(Color.DARK_GRAY);
        header_r_b_panel.setBounds(0, 0, 541, 45);
        right_bottom_panel.add(header_r_b_panel);

        Label header_r_b_label = new Label("Logs");
        header_r_b_label.setForeground(Color.WHITE);
        header_r_b_label.setFont(new Font("Dialog", Font.BOLD, 20));
        header_r_b_label.setAlignment(Label.CENTER);
        header_r_b_label.setBounds(10, 10, 521, 22);
        header_r_b_panel.add(header_r_b_label);

        txtArea = new JTextArea();
        txtArea.setFont(new Font("Tahoma", Font.PLAIN, 16));
        txtArea.setForeground(SystemColor.window);
        txtArea.setBounds(10, 60, 516, 185);
        txtArea.setBackground(new Color(51, 51, 51));
        right_bottom_panel.add(txtArea);
        this.setVisible(true);
        AddMsgLog("Interface fonctionelle") ;



        JScrollPane scrollPane2 = new JScrollPane(txtArea);
        scrollPane2.setFont(new Font("Tahoma", Font.PLAIN, 16));
        scrollPane2.setBorder(BorderFactory.createEmptyBorder());
        scrollPane2.setForeground(SystemColor.window);
        scrollPane2.setBounds(10, 60, 516, 185);
        scrollPane2.setBackground(new Color(51, 51, 51));
        right_bottom_panel.add(scrollPane2);
        this.setVisible(true);

        //tree
        listRoot();
        arbre.setCellRenderer(new FileTreeCellRenderer());
        arbre.setRootVisible(true);

    }

    // to update progressBar
    public void setPercentage(int p) {
        if (p == 100) {
            AddMsgLog("Terminé");
        }
        progressBar.setValue(p);
    }

    // to set a message in logs
    public void AddMsgLog(String msg) {
        txtArea.setText(txtArea.getText() + "Info : "+  msg + "\n");
    }

    //
    void parcourirNoeud(Object r)
    {
        //System.out.println(r.getClass());
        TreeNode root = (TreeNode)r;
        //System.out.println(root);
        for (int i = 0; i < root.getChildCount(); i++)
        {
            if (root.getChildAt(i).isLeaf()) {
                DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) root.getChildAt(i);
                MyFile f = (MyFile) dmtn.getUserObject();
                System.out.println(f.getName());
            }
            else {
                parcourirNoeud(root.getChildAt(i));
            }
        }
    }

    // create tree
    public void listRoot(){
        this.racine = new DefaultMutableTreeNode("GenBank");
        File folder = new File("Results");

        //for(File file : File.listRoots()){
        for (File file : folder.listFiles()) {
            if(file.isDirectory()) {
                DefaultMutableTreeNode lecteur = new DefaultMutableTreeNode(file.getName());
                try {
                    for(File nom : file.listFiles()){
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(nom.getName());
                        lecteur.add(this.listFile(nom, node));
                    }
                } catch (NullPointerException e) {}

                this.racine.add(lecteur);
            }
        }

        UIManager.put("Tree.rendererFillBackground", false);

        arbre = new JTree(this.racine);
        arbre.setBorder(new EmptyBorder(0, 0, 0, 0));
        arbre.setBackground(new Color(51, 51, 51));
        arbre.addTreeSelectionListener(new TreeSelectionListener(){
            public void valueChanged(TreeSelectionEvent event) {
                TreePath tp = event.getNewLeadSelectionPath();
                if(arbre.getLastSelectedPathComponent() != null){

                    Object o = arbre.getLastSelectedPathComponent();
                    //DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) o;

                    //MyFile f = (MyFile) dmtn.getUserObject();

                    //String name = ((MyFile) o.).getName() ;
                    //System.out.println(f.getName());
                    if (o instanceof MyFile) {
                        System.out.println(((MyFile) o).getName());
                    }
                    else {
                        System.out.println(arbre.getLastSelectedPathComponent().toString());
                    }
                }
            }
        });


        // element
        getContentPane().setLayout(null);
        //Que nous pla�ons sur le ContentPane de notre JFrame � l'aide d'un scroll
        scrollPane = new JScrollPane(arbre);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(15, 0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 15));

        scrollPane.setEnabled(false);
        scrollPane.setBackground(new Color(51, 51, 51));
        scrollPane.setBounds(26, 127, 384, 401);
        this.getContentPane().add(scrollPane);
    }



    // create node
    private DefaultMutableTreeNode listFile(File file, DefaultMutableTreeNode node){
        if(file.isFile()) {
            MyFile f = new MyFile(file.getName(), "same");
            nb_files = nb_files + 1;
            return new DefaultMutableTreeNode(f);
        }
        else{
            // Folder
            File[] list = file.listFiles();
            // empty
            if(list == null) {
                return new DefaultMutableTreeNode(file.getName());
            }
            // not empty
            for(File nom : list){
                DefaultMutableTreeNode subNode;
                // new folder -> recursive
                if(nom.isDirectory()){

                    subNode = new DefaultMutableTreeNode(nom.getName());
                    node.add(this.listFile(nom, subNode));
                }else{
                    // file
                    nb_files = nb_files + 1;
                    MyFile f = new MyFile(nom.getName(), "same");
                    subNode = new DefaultMutableTreeNode(f);
                }
                node.add(subNode);
                // }
            }
            return node;
        }
    }

// main
    public static void main(String[] args){
        Interface fen = new Interface();
    }


    // custom tree

    class FileTreeCellRenderer implements TreeCellRenderer {
        private JLabel label;

        FileTreeCellRenderer() {
            label = new JLabel();
            label.setForeground(Color.WHITE);
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                      boolean leaf, int row, boolean hasFocus) {
            Object o = ((DefaultMutableTreeNode) value).getUserObject();
            if (o instanceof MyFile) {
                MyFile f = (MyFile) o;

                ImageIcon imageIcon = null;
                if (f.getStatus().equals("same")) {
                    imageIcon = new ImageIcon("Images/green.png");
                }
                if (f.getStatus().equals("update")) {
                    imageIcon = new ImageIcon("Images/orange.png");
                }
                if (f.getStatus().equals("created")) {
                    imageIcon = new ImageIcon("Images/blue.png");
                }
                if (f.getStatus().equals("error")) {
                    imageIcon = new ImageIcon("Images/red.png");
                }


                if (imageIcon != null) {
                    label.setIcon(imageIcon);
                }

                label.setText(f.getName());
                //label.setForeground(Color.WHITE);
            } else {
                label.setIcon(new ImageIcon("Images/directory.png"));
                label.setText("" + value);
            }
            return label;
        }
    }

    // new class for custom tree
    class MyFile {
        private String name;
        private String status;

        MyFile(String name, String status) {
            this.name = name;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
