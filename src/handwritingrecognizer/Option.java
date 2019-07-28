/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Option.java
 *
 * Created on Apr 26, 2014, 12:19:16 PM
 */

package handwritingrecognizer;

/**
 *
 * @author Kanchan
 */
import java.sql.*;
import javax.swing.*;
import java.awt.Color;
import javax.swing.JTabbedPane;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
public class Option extends javax.swing.JPanel implements MouseListener,MouseMotionListener{

    /** Creates new form Option */
    
    public JTabbedPane pane;
    public static Graphics g;
    public static int pointCount=0;
    public static int noUp=0;
    public static int noChar=0;
    public boolean flag=false;
    public Point points[];
    public int up[]=new int[100];
    public int segOfUp[]=new int[100];
    public int segChar[]=new int[100];

    double angle[]=new double[10000];
    int pointer[]=new int[1000];
    int sminx[]=new int[1000];
    int sminy[]=new int[1000];
    int smaxx[]=new int[1000];
    int smaxy[]=new int[1000];
    static int segNo;
    int i,j,h,z,hold,remain,minx,miny,maxx,maxy;
    double angledif,xcentre,ycentre,width,height,slantlength,dn,dk,temp1,temp2,temp3,val1;
    static  double MHP[]=new double[30];
    static  double MVP[]=new double[30];
    static  double Mstraightness[]=new double[30];
    static   double Marcness[]=new double[30];
    static  double MVL1[]=new double[30];
    static  double MVL2[]=new double[30];
    static  double MHL1[]=new double[30];
    static  double MHL2[]=new double[30];
    static   double MPS1[]=new double[30];
    static   double MPS2[]=new double[30];
    static double MNS1[]=new double[30];
    static double MNS2[]=new double[30];
    static double MAL[]=new double[30];
    static  double MUL[]=new double[30];
    static  double MCL[]=new double[30];
    static  double MDL[]=new double[30];
    static  double MOL[]=new double[30];
    static   double MOLL[]=new double[30];
    static  double MOLR[]=new double[30];
    static  double MOLT[]=new double[30];
    static  double MOLB[]=new double[30];
    static  double MHLEN[]=new double[30];
    static  double MVLEN[]=new double[30];
    static  double MSSLEN[]=new double[30];
    static String ch[][]=new String[30][28];
    String remark1[]={"-","L","LC","NL","C","NR","RC","R"};
    String remark2[]={"-","B","BC","NB","C","NT","TC","T"};
    String remark3[]={"-","Z","VVL","VL","L","M","H","VH","VVH","E"};
    String feature[]={"ch1","ch2","ch3","ch4","ch5","ch6","ch7","ch8","ch9","ch10","ch11","ch12","ch13","ch14","ch15","ch16","ch17","ch18","ch19","ch20","ch21","ch22","ch23","ch24"};
    static double pi=4*Math.atan(1);

    Connection connection;

    public Option(JTabbedPane pane) {
        initComponents();
        this.pane=pane;
    }

    void triangle(double tem1,double tem2,double tem3 )
    {
        if((tem3-tem2/2.0)<=tem1&&(tem3+tem2/2.0>=tem1)) val1=1-2*Math.abs((tem1-tem3)/tem2);
        else val1=0;
    }

    String characterRecognizer(int segStart,int segEnd)
    {
        String out="";
        Statement statement=null;
        ResultSet resultset=null;
        try
        {
            statement=connection.createStatement();
            resultset=statement.executeQuery("SELECT * FROM Mixed WHERE total_seg="+(segEnd-segStart+1));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            i=j=0;
            double sum1[]=new double[1000];
            char character[]=new char[1000];
            int code[]=new int[1000];
            double percent[]=new double[1000];
            int curCode,curSeg,curSeg1;
            char curChar;
            int pos=0,pos1=0,pos2=0;
            double max=0.0;
            while(resultset.next())
            {
                curCode=resultset.getInt("code");
                String text=resultset.getString("character");
                curChar=text.charAt(0);
                curSeg1=resultset.getInt("seg_no");
                curSeg=curSeg1+(segStart-1);
                double sum2=0.0;
                for(int k=1;k<=24;k++)
                {
                   text=resultset.getString(feature[k-1]);
                   if(text.equals(ch[curSeg][k])) sum2=sum2+1.0;
                   else
                   {
                       if(k==1)
                       {
                           for(int e=0;e<8;e++)
                           {
                               if(remark1[e].equals(ch[curSeg][k])) pos1=e;
                               if(remark1[e].equals(text)) pos2=e;
                           }

                           double dif=(double)Math.abs(pos1-pos2);
                           if(dif<=5) sum2=sum2+(double)(1.0-(0.2*dif));
                       }
                       else if(k==2)
                       {
                           for(int e=0;e<8;e++)
                           {
                               if(remark2[e].equals(ch[curSeg][k])) pos1=e;
                               if(remark2[e].equals(text)) pos2=e;
                           }

                           double dif=(double)Math.abs(pos1-pos2);
                           if(dif<=5) sum2=sum2+(double)(1.0-(0.2*dif));

                       }
                       else
                       {
                           for(int e=0;e<10;e++)
                           {
                               if(remark3[e].equals(ch[curSeg][k])) pos1=e;
                               if(remark3[e].equals(text)) pos2=e;
                           }

                           double dif=(double)Math.abs(pos1-pos2);
                           if(dif<=5) sum2=sum2+(double)(1.0-(0.2*dif));
                       }
                }

              }
                if(curSeg1==1)
                {
                    sum1[i]=sum2;
                    code[i]=curCode;
                    character[i]=curChar;
                    if(curSeg1==(segEnd-segStart+1))
                    {
                      //  System.out.println("i:"+i+" sum1:"+sum1[i]);
                        percent[i]=(double)((double)(sum1[i]/(double)(24.0*(double)(segEnd-segStart+1)))*100.0);
                       // System.out.println("code: "+code[i]+"   Percentage is: "+percent[i]);
                         System.out.println("1st character: percentage="+percent[i]+" Code="+code[i]);
                        if(percent[i]>max)
                        {
                            max=percent[i];
                            pos=i;
                        }
                        i++;
                    }
                }
                else if(curSeg1<(segEnd-segStart+1))
                {
                    sum1[i]+=sum2;
                }
                else
                {
                    sum1[i]+=sum2;
                    percent[i]=(double)((double)(sum1[i]/(double)(24.0*(double)(segEnd-segStart+1)))*100.0);
                    System.out.println("1st character: percentage="+percent[i]+" Code="+code[i]);
                    if(percent[i]>max)
                    {
                        max=percent[i];
                        pos=i;
                    }
                    i++;
                }

                j++;
            }
            out=out+character[pos];
            System.out.println("No problem: percentage="+percent[pos]+" Code="+code[pos]);
            if(j==0||percent[pos]<=20.0) out=Character.toString((char)115);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, ""+e);
        }
        try
        {
             statement.close();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,""+e);
        }

        return out;
    }

           public void mousePressed(MouseEvent event)
           {
               flag=true;
           }

       public void mouseDragged(MouseEvent event)
           {
               if(pointCount < points.length)
               {
                   points[pointCount] = event.getPoint();
                   g.fillOval(points[pointCount].x, points[pointCount].y, 4, 4);
                   pointCount++;

               }
           }

       public void mouseReleased(MouseEvent event)
           {
               up[noUp]=pointCount;
               noUp++;
               flag=false;
           }
       public void mouseExited(MouseEvent event)
       {
       }

       public void mouseEntered(MouseEvent event)
       {
       }

       public void mouseClicked(MouseEvent event)
       {
       }

       public void mouseMoved(MouseEvent event)
       {
         //  System.out.println("Mouse is moved");
       }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jPanel6 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 24));
        jLabel1.setText("                                  BANGLA SHOULD BE YOUR LANGUAGE FOR SPEAKING WRITING UNDERSTANDING");
        jLabel1.setMaximumSize(new java.awt.Dimension(1366, 17));

        jLabel2.setBackground(new java.awt.Color(240, 204, 240));
        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 24));
        jLabel2.setText("                                   DRAWING  PAD");

        jButton3.setFont(new java.awt.Font("Times New Roman", 1, 24));
        jButton3.setText("OK");
        jButton3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Times New Roman", 1, 24));
        jButton4.setText("CLEAR WINDOW");
        jButton4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jEditorPane1.setBackground(new java.awt.Color(250, 250, 250));
        jEditorPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        jEditorPane1.setMinimumSize(new java.awt.Dimension(608, 339));
        jEditorPane1.setPreferredSize(new java.awt.Dimension(608, 339));
        jEditorPane1.setSelectionColor(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(jEditorPane1);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 288, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 274, Short.MAX_VALUE)
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel3.setFont(new java.awt.Font("Times New Roman", 1, 24));
        jLabel3.setText("Total Segments");

        jTextField1.setBackground(new java.awt.Color(250, 250, 250));
        jTextField1.setFont(new java.awt.Font("Times New Roman", 1, 22)); // NOI18N
        jTextField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 24));
        jButton1.setText("OK");
        jButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Times New Roman", 1, 20));
        jButton5.setText("FEATURES");
        jButton5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Times New Roman", 1, 20));
        jButton6.setText("SEGMENTATION");
        jButton6.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)))
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addGap(107, 107, 107)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(126, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel7.setFont(new java.awt.Font("Times New Roman", 1, 20));
        jLabel7.setText(" RECOGNIZED WORD");

        jButton8.setFont(new java.awt.Font("Times New Roman", 1, 24));
        jButton8.setText("OK");
        jButton8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(250, 250, 250));
        jLabel8.setFont(new java.awt.Font("Siyam Rupali", 1, 22)); // NOI18N
        jLabel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 22));
        jLabel6.setText("  RECOGNITION MODE");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel7))
                    .addComponent(jLabel6)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jButton2.setFont(new java.awt.Font("Times New Roman", 1, 24));
        jButton2.setText("START DRAWING");
        jButton2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 24));
        jLabel4.setText("   LEARNING MODE");

        jLabel5.setFont(new java.awt.Font("Times New Roman", 1, 20));
        jLabel5.setText("  LEARNED CHARACTER");

        jTextField2.setBackground(new java.awt.Color(250, 250, 250));
        jTextField2.setFont(new java.awt.Font("Siyam Rupali", 1, 20));
        jTextField2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton7.setFont(new java.awt.Font("Times New Roman", 1, 24));
        jButton7.setText("LEARN");
        jButton7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                .addGap(73, 73, 73))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(34, 34, 34)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(36, 36, 36)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1719, 1719, 1719)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1406, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4138, 4138, 4138))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(2587, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(443, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(130, 130, 130))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        points=new Point[10000];
        pointCount=0;
        noUp=0;
        flag=false;
        if(g==null)
        {
        g= jEditorPane1.getGraphics();
        g.setColor(Color.BLACK);
        }
        jEditorPane1.addMouseListener(this);
        jEditorPane1.addMouseMotionListener(this);

    }//GEN-LAST:event_jButton2ActionPerformed

    //OK button
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
      System.out.println("Total points: "+pointCount+"\nTotal ups: "+noUp);
      hold=0;
      segNo=0;
      pointer[0]=0;
      for(h=0;h<noUp;h++)
      {
          i=up[h]-1;
          j=hold;
          z=0;
          remain=i-hold;
          while(j<=i)
          {
              if(remain>4)
              {
                  if((points[j+5].x-points[j].x)==0) angle[z]=90;
                  else
                  {
                      angle[z]=Math.toDegrees(Math.atan((double)((double)(points[j+5].y-points[j].y)/(double)(points[j+5].x-points[j].x))));
                      angle[z]=Math.abs(angle[z]);
                  }

                  System.out.print(z+"  "+angle[z]+"  ");

                  if(points[j+5].x<points[j].x&&points[j+5].y>points[j].y) angle[z]=180+angle[z];
                  else if(points[j+5].x<points[j].x&&points[j+5].y<points[j].y) angle[z]=180-angle[z];
                  else if(points[j+5].x>points[j].x&&points[j+5].y<points[j].y) angle[z]=angle[z];
                  else if(points[j+5].x>points[j].x&&points[j+5].y>points[j].y) angle[z]=360-angle[z];

                  System.out.println(""+angle[z]);
                  if(z>0)
                  {
                     angledif=Math.abs(angle[z]-angle[z-1]);
                      if(angledif>180) angledif=angledif-180;
                     if(angledif>=35&&angledif<=145)
                     {
                         System.out.println("Here is one:"+angle[z-1]+"  "+angle[z]);

                         segNo=segNo+1;
                         pointer[segNo]=j+5;
                         z=0;
                         j=j+6;
                         remain=i-j;
                         continue;

                     }
                  }
                  z++;
              }
                   j=j+6;
                  remain=i-j;
          }
          if((i-pointer[segNo])>12)
          {
              segNo++;
              pointer[segNo]=i;
          }
          else
          {
              if(h>0)
              {
                  if(pointer[segNo]==(up[h-1]-1))
                  {
                      segNo++;
                      pointer[segNo]=i;
                  }
                  else   pointer[segNo]=i;
              }
              else
              {
                  if(noUp==1)
                  {
                      segNo++;
                      pointer[segNo]=i;
                  }
                  else pointer[segNo] = i;
              }
          }
          hold=i+1;
          segOfUp[h]=segNo;
      }

      System.out.println("Successful till segmentation");

      minx=maxx=points[0].x;
      miny=maxy=points[0].y;
      for(j=0;j<pointCount;j++)
      {
              if(minx>points[j].x) minx=points[j].x;
              if(points[j].x>maxx) maxx=points[j].x;
              if(points[j].y<miny) miny=points[j].y;
              if(points[j].y>maxy) maxy=points[j].y;

      }

   System.out.println("minx: "+minx+"\nminy: "+miny+"\nmaxx: "+maxx+"\nmaxy: "+maxy);
      pointer[0]=-1;
      for(i=1;i<=segNo;i++)
      {
          for(j=1;j<=24;j++)
              ch[i][j]="-";

          sminx[i]=smaxx[i]=points[pointer[i-1]+1].x;
          sminy[i]=smaxy[i]=points[pointer[i-1]+1].y;
          int ind=1;
          for(j=pointer[i-1]+2;j<=pointer[i];j++)
          {
              if(sminx[i]>points[j].x) sminx[i]=points[j].x;
              if(points[j].x>smaxx[i]) smaxx[i]=points[j].x;
              if(points[j].y<sminy[i]) sminy[i]=points[j].y;
              if(points[j].y>smaxy[i]) smaxy[i]=points[j].y;
          }

          System.out.println("Successfully finished");
          xcentre=(sminx[i]+smaxx[i])/2;
          ycentre=(sminy[i]+smaxy[i])/2;

          if((maxx-minx)==0) MHP[i]=(xcentre-sminx[i])/0.00001;
          else MHP[i]=(xcentre-sminx[i])/(maxx-minx);

          if(MHP[i]<=0) ch[i][1]="L";
          else if(MHP[i]<=0.12&&MHP[i]>0) ch[i][1]="LC";
          else if(MHP[i]<=0.24&&MHP[i]>0.12) ch[i][1]="NL";
          else if(MHP[i]<=0.36&&MHP[i]>0.24) ch[i][1]="C";
          else if(MHP[i]<=0.48&&MHP[i]>0.36) ch[i][1]="NR";
          else if(MHP[i]<=0.6&&MHP[i]>0.48) ch[i][1]="RC";
          else ch[i][1]="R";

          if((maxy-miny)==0) MVP[i]=(ycentre-sminy[i])/0.00001;
          else MVP[i]=(ycentre-sminy[i])/(maxy-miny);

          if(MVP[i]<=0) ch[i][2]="B";
          else if(MVP[i]<=0.12&&MVP[i]>0) ch[i][2]="BC";
          else if(MVP[i]<=0.24&&MVP[i]>0.12) ch[i][2]="NB";
          else if(MVP[i]<=0.36&&MVP[i]>0.24) ch[i][2]="C";
          else if(MVP[i]<=0.48&&MVP[i]>0.36) ch[i][2]="NT";
          else if(MVP[i]<=0.6&&MVP[i]>0.48) ch[i][2]="TC";
          else ch[i][2]="T";

          width=maxx-minx+1;
          if(width==0) width=0.00001;
          height=maxy-miny+1;
          if(height==0) height=0.00001;
          System.out.println("Width="+width+"  Height="+height);
          slantlength=Math.sqrt(width*width+height*height);

          dn=1;
          dk=1;

          for(j=pointer[i-1]+1;j<pointer[i];j++)
          {
               dk=dk+Math.sqrt((((points[j].x-points[j+1].x)*(points[j].x-points[j+1].x))+((points[j].y-points[j+1].y)*(points[j].y-points[j+1].y))));
          }

          dn=Math.sqrt((double)((points[pointer[i-1]+1].x-points[pointer[i]].x)*(points[pointer[i-1]+1].x-points[pointer[i]].x)+(points[pointer[i-1]+1].y-points[pointer[i]].y)*(points[pointer[i-1]+1].y-points[pointer[i]].y)));
          if(dk==0) dk=0.00001;
          Mstraightness[i]=dn/dk;
          Marcness[i]=1-Mstraightness[i];
          System.out.println("i="+i+"  Straightness="+Mstraightness[i]);

              if(Mstraightness[i]<=0.3) ch[i][3]="Z";
              else if(Mstraightness[i]>0.3&&Mstraightness[i]<=0.3875) ch[i][3]="VVL";
              else if(Mstraightness[i]>0.3875&&Mstraightness[i]<=0.475) ch[i][3]="VL";
              else if(Mstraightness[i]>0.475&&Mstraightness[i]<=0.5625) ch[i][3]="L";
              else if(Mstraightness[i]>0.5625&&Mstraightness[i]<=0.65) ch[i][3]="M";
              else if(Mstraightness[i]>0.65&&Mstraightness[i]<=0.7375) ch[i][3]="H";
              else if(Mstraightness[i]>0.7375&&Mstraightness[i]<=0.825) ch[i][3]="VH";
              else if(Mstraightness[i]>0.825&&Mstraightness[i]<=0.9125) ch[i][3]="VVH";
              else ch[i][3]="E";

              if(Marcness[i]<=0) ch[i][4]="Z";
              else if(Marcness[i]>0&&Marcness[i]<=0.1) ch[i][4]="VVL";
              else if(Marcness[i]>0.1&&Marcness[i]<=0.2) ch[i][4]="VL";
              else if(Marcness[i]>0.2&&Marcness[i]<=0.3) ch[i][4]="L";
              else if(Marcness[i]>0.3&&Marcness[i]<=0.4) ch[i][4]="M";
              else if(Marcness[i]>0.4&&Marcness[i]<=0.5) ch[i][4]="H";
              else if(Marcness[i]>0.5&&Marcness[i]<=0.6) ch[i][4]="VH";
              else if(Marcness[i]>0.6&&Marcness[i]<=0.7) ch[i][4]="VVH";
              else ch[i][4]="E";


          if(Mstraightness[i]>0.75)
          {
              if((points[pointer[i]].x-points[pointer[i-1]+1].x)==0) angle[i]=90;
              else
              {
                  angle[i]=Math.toDegrees(Math.atan((double)((double)(points[pointer[i]].y-points[pointer[i-1]+1].y)/(double)(points[pointer[i]].x-points[pointer[i-1]+1].x))));
                  angle[i]=Math.abs(angle[i]);
              }

              if(points[pointer[i]].x<points[pointer[i-1]+1].x&&points[pointer[i]].y>points[pointer[i-1]+1].y)
                  angle[i]=180+angle[i];
              else if(points[pointer[i]].x<points[pointer[i-1]+1].x&&points[pointer[i]].y<points[pointer[i-1]+1].y)
                  angle[i]=180-angle[i];
              else if(points[pointer[i]].x>points[pointer[i-1]+1].x&&points[pointer[i]].y<points[pointer[i-1]+1].y)
                  angle[i]=angle[i];
              else angle[i]=360-angle[i];

              triangle(angle[i],90,90);
              temp1=val1;
              triangle(angle[i],90,270);
              temp2=val1;
              double MVL;
              if(temp1>temp2) MVL=temp1;
              else MVL=temp2;

              if(points[pointer[i-1]+1].y<points[pointer[i]].y)
              {
              MVL2[i]=0.0;
              MVL1[i]=MVL;
              if(MVL1[i]<=0) ch[i][5]="Z";
              else if(MVL1[i]>0&&MVL1[i]<=0.125) ch[i][5]="VVL";
              else if(MVL1[i]>0.125&&MVL1[i]<=0.25) ch[i][5]="VL";
              else if(MVL1[i]>0.25&&MVL1[i]<=0.375) ch[i][5]="L";
              else if(MVL1[i]>0.375&&MVL1[i]<=0.5) ch[i][5]="M";
              else if(MVL1[i]>0.5&&MVL1[i]<=0.625) ch[i][5]="H";
              else if(MVL1[i]>0.625&&MVL1[i]<=0.75) ch[i][5]="VH";
              else if(MVL1[i]>0.75&&MVL1[i]<=0.875) ch[i][5]="VVH";
              else ch[i][5]="E";
              }
              else
              {
                  MVL1[i]=0.0;
                  MVL2[i]=MVL;
                  if(MVL2[i]<=0) ch[i][6]="Z";
                  else if(MVL2[i]>0&&MVL2[i]<=0.125) ch[i][6]="VVL";
                  else if(MVL2[i]>0.125&&MVL2[i]<=0.25) ch[i][6]="VL";
                  else if(MVL2[i]>0.25&&MVL2[i]<=0.375) ch[i][6]="L";
                  else if(MVL2[i]>0.375&&MVL2[i]<=0.5) ch[i][6]="M";
                  else if(MVL2[i]>0.5&&MVL2[i]<=0.625) ch[i][6]="H";
                  else if(MVL2[i]>0.625&&MVL2[i]<=0.75) ch[i][6]="VH";
                  else if(MVL2[i]>0.75&&MVL2[i]<=0.875) ch[i][6]="VVH";
                  else ch[i][6]="E";
              }


              triangle(angle[i],90,0);
              temp1=val1;
              triangle(angle[i],90,180);
              temp2=val1;
              triangle(angle[i],90,360);
              temp3=val1;

              double MHL;
              if(temp1>temp2) MHL=temp1;
              else MHL=temp2;
              if(MHL<=temp3) MHL=temp3;

              if(points[pointer[i-1]+1].x<points[pointer[i]].x)
              {
                  MHL2[i]=0.0;
                  MHL1[i]=MHL;
                  if(MHL1[i]<=0) ch[i][7]="Z";
                  else if(MHL1[i]>0&&MHL1[i]<=0.125) ch[i][7]="VVL";
                  else if(MHL1[i]>0.125&&MHL1[i]<=0.25) ch[i][7]="VL";
                  else if(MHL1[i]>0.25&&MHL1[i]<=0.375) ch[i][7]="L";
                  else if(MHL1[i]>0.375&&MHL1[i]<=0.5) ch[i][7]="M";
                  else if(MHL1[i]>0.5&&MHL1[i]<=0.625) ch[i][7]="H";
                  else if(MHL1[i]>0.625&&MHL1[i]<=0.75) ch[i][7]="VH";
                  else if(MHL1[i]>0.75&&MHL1[i]<=0.875) ch[i][7]="VVH";
                  else ch[i][7]="E";
              }
              else
              {
                  MHL1[i]=0.0;
                  MHL2[i]=MHL;
                  if(MHL2[i]<=0) ch[i][8]="Z";
                  else if(MHL2[i]>0&&MHL2[i]<=0.125) ch[i][8]="VVL";
                  else if(MHL2[i]>0.125&&MHL2[i]<=0.25) ch[i][8]="VL";
                  else if(MHL2[i]>0.25&&MHL2[i]<=0.375) ch[i][8]="L";
                  else if(MHL2[i]>0.375&&MHL2[i]<=0.5) ch[i][8]="M";
                  else if(MHL2[i]>0.5&&MHL2[i]<=0.625) ch[i][8]="H";
                  else if(MHL2[i]>0.625&&MHL2[i]<=0.75) ch[i][8]="VH";
                  else if(MHL2[i]>0.75&&MHL2[i]<=0.875) ch[i][8]="VVH";
                  else ch[i][8]="E";
              }


              triangle(angle[i],90,45);
              temp1=val1;
               triangle(angle[i],90,225);
              temp2=val1;

              double MPS;
              if(temp1>temp2) MPS=temp1;
              else MPS=temp2;

              if(points[pointer[i-1]+1].x<points[pointer[i]].x)
              {
                  MPS2[i]=0.0;
                  MPS1[i]=MPS;
                  if(MPS1[i]<=0) ch[i][9]="Z";
                  else if(MPS1[i]>0&&MPS1[i]<=0.125) ch[i][9]="VVL";
                  else if(MPS1[i]>0.125&MPS1[i]<=0.25) ch[i][9]="VL";
                  else if(MPS1[i]>0.25&&MPS1[i]<=0.375) ch[i][9]="L";
                  else if(MPS1[i]>0.375&&MPS1[i]<=0.5) ch[i][9]="M";
                  else if(MPS1[i]>0.5&&MPS1[i]<=0.625) ch[i][9]="H";
                  else if(MPS1[i]>0.625&&MPS1[i]<=0.75) ch[i][9]="VH";
                  else if(MPS1[i]>0.75&&MPS1[i]<=0.875) ch[i][9]="VVH";
                  else ch[i][9]="E";
              }
              else
              {
                  MPS1[i]=0.0;
                  MPS2[i]=MPS;
                  if(MPS2[i]<=0) ch[i][10]="Z";
                  else if(MPS2[i]>0&&MPS2[i]<=0.125) ch[i][10]="VVL";
                  else if(MPS2[i]>0.125&MPS2[i]<=0.25) ch[i][10]="VL";
                  else if(MPS2[i]>0.25&&MPS2[i]<=0.375) ch[i][10]="L";
                  else if(MPS2[i]>0.375&&MPS2[i]<=0.5) ch[i][10]="M";
                  else if(MPS2[i]>0.5&&MPS2[i]<=0.625) ch[i][10]="H";
                  else if(MPS2[i]>0.625&&MPS2[i]<=0.75) ch[i][10]="VH";
                  else if(MPS2[i]>0.75&&MPS2[i]<=0.875) ch[i][10]="VVH";
                  else ch[i][10]="E";
              }


              triangle(angle[i],90,135);
              temp1=val1;
              triangle(angle[i],90,315);
              temp2=val1;
              double MNS;
              if(temp1>temp2) MNS=temp1;
              else MNS=temp2;

              if(points[pointer[i-1]+1].x<points[pointer[i]].x)
              {
                  MNS2[i]=0.0;
                  MNS1[i]=MNS;
                  if(MNS1[i]<=0) ch[i][11]="Z";
                  else if(MNS1[i]>0&&MNS1[i]<=0.125) ch[i][11]="VVL";
                  else if(MNS1[i]>0.125&MNS1[i]<=0.25) ch[i][11]="VL";
                  else if(MNS1[i]>0.25&&MNS1[i]<=0.375) ch[i][11]="L";
                  else if(MNS1[i]>0.375&&MNS1[i]<=0.5) ch[i][11]="M";
                  else if(MNS1[i]>0.5&&MNS1[i]<=0.625) ch[i][11]="H";
                  else if(MNS1[i]>0.625&&MNS1[i]<=0.75) ch[i][11]="VH";
                  else if(MNS1[i]>0.75&&MNS1[i]<=0.875) ch[i][11]="VVH";
                  else ch[i][11]="E";
              }
              else
              {
                  MNS1[i]=0.0;
                  MNS2[i]=MNS;
                  if(MNS2[i]<=0) ch[i][12]="Z";
                  else if(MNS2[i]>0&&MNS2[i]<=0.125) ch[i][12]="VVL";
                  else if(MNS2[i]>0.125&MNS2[i]<=0.25) ch[i][12]="VL";
                  else if(MNS2[i]>0.25&&MNS2[i]<=0.375) ch[i][12]="L";
                  else if(MNS2[i]>0.375&&MNS2[i]<=0.5) ch[i][12]="M";
                  else if(MNS2[i]>0.5&&MNS2[i]<=0.625) ch[i][12]="H";
                  else if(MNS2[i]>0.625&&MNS2[i]<=0.75) ch[i][12]="VH";
                  else if(MNS2[i]>0.75&&MNS2[i]<=0.875) ch[i][12]="VVH";
                  else ch[i][12]="E";
              }

              MHLEN[i]=dn/width;

              if(MHLEN[i]<=0) ch[i][13]="Z";
              else if(MHLEN[i]>0&&MHLEN[i]<=0.18) ch[i][13]="VVL";
              else if(MHLEN[i]>0.18&MHLEN[i]<=0.36) ch[i][13]="VL";
              else if(MHLEN[i]>0.36&&MHLEN[i]<=0.54) ch[i][13]="L";
              else if(MHLEN[i]>0.54&&MHLEN[i]<=0.72) ch[i][13]="M";
              else if(MHLEN[i]>0.72&&MHLEN[i]<=0.9) ch[i][13]="H";
              else if(MHLEN[i]>0.9&&MHLEN[i]<=1.08) ch[i][13]="VH";
              else if(MHLEN[i]>1.08&&MHLEN[i]<=1.26) ch[i][13]="VVH";
              else ch[i][13]="E";

              MVLEN[i]=dn/height;

              if(MVLEN[i]<=0) ch[i][14]="Z";
              else if(MVLEN[i]>0&&MVLEN[i]<=0.18) ch[i][14]="VVL";
              else if(MVLEN[i]>0.18&MVLEN[i]<=0.36) ch[i][14]="VL";
              else if(MVLEN[i]>0.36&&MVLEN[i]<=0.54) ch[i][14]="L";
              else if(MVLEN[i]>0.54&&MVLEN[i]<=0.72) ch[i][14]="M";
              else if(MVLEN[i]>0.72&&MVLEN[i]<=0.9) ch[i][14]="H";
              else if(MVLEN[i]>0.9&&MVLEN[i]<=1.08) ch[i][14]="VH";
              else if(MVLEN[i]>1.08&&MVLEN[i]<=1.26) ch[i][14]="VVH";
              else ch[i][14]="E";

              MSSLEN[i]=dn/slantlength;

              if(MSSLEN[i]<=0) ch[i][15]="Z";
              else if(MSSLEN[i]>0&&MSSLEN[i]<=0.18) ch[i][15]="VVL";
              else if(MSSLEN[i]>0.18&MSSLEN[i]<=0.36) ch[i][15]="VL";
              else if(MSSLEN[i]>0.36&&MSSLEN[i]<=0.54) ch[i][15]="L";
              else if(MSSLEN[i]>0.54&&MSSLEN[i]<=0.72) ch[i][15]="M";
              else if(MSSLEN[i]>0.72&&MSSLEN[i]<=0.9) ch[i][15]="H";
              else if(MSSLEN[i]>0.9&&MSSLEN[i]<=1.08) ch[i][15]="VH";
              else if(MSSLEN[i]>1.08&&MSSLEN[i]<=1.26) ch[i][15]="VVH";
              else ch[i][15]="E";
          }

          if(Mstraightness[i]<0.83)
          {
              double sum1=0,sum2=0,sum3=0,sum4=0;
              int n=0;
              double a=(points[pointer[i-1]+1].y+points[pointer[i]].y)/2.0;
              double b=(points[pointer[i-1]+1].x+points[pointer[i]].x)/2.0;
              for(j=pointer[i-1]+1;j<=pointer[i];j++)
              {
                  if(points[j].y<a) sum1++;
                  if(points[j].y>a) sum2++;
                  if(points[j].x<b) sum3++;
                  if(points[j].x>b) sum4++;
                  n++;
              }
              sum1=sum1/(double)n;
              sum2=sum2/(double)n;
              sum3=sum3/(double)n;
              sum4=sum4/(double)n;

              if(sum1<1.0) MAL[i]=sum1;
              else MAL[i]=1.0;

              if(MAL[i]<=0) ch[i][16]="Z";
              else if(MAL[i]>0&&MAL[i]<=0.125) ch[i][16]="VVL";
              else if(MAL[i]>0.125&MAL[i]<=0.25) ch[i][16]="VL";
              else if(MAL[i]>0.25&&MAL[i]<=0.375) ch[i][16]="L";
              else if(MAL[i]>0.375&&MAL[i]<=0.5) ch[i][16]="M";
              else if(MAL[i]>0.5&&MAL[i]<=0.625) ch[i][16]="H";
              else if(MAL[i]>0.625&&MAL[i]<=0.75) ch[i][16]="VH";
              else if(MAL[i]>0.75&&MAL[i]<=0.875) ch[i][16]="VVH";
              else ch[i][16]="E";

              if(sum2<1.0) MUL[i]=sum2;
              else MUL[i]=1.0;

              if(MUL[i]<=0) ch[i][17]="Z";
              else if(MUL[i]>0&&MUL[i]<=0.125) ch[i][17]="VVL";
              else if(MUL[i]>0.125&MUL[i]<=0.25) ch[i][17]="VL";
              else if(MUL[i]>0.25&&MUL[i]<=0.375) ch[i][17]="L";
              else if(MUL[i]>0.375&&MUL[i]<=0.5) ch[i][17]="M";
              else if(MUL[i]>0.5&&MUL[i]<=0.625) ch[i][17]="H";
              else if(MUL[i]>0.625&&MUL[i]<=0.75) ch[i][17]="VH";
              else if(MUL[i]>0.75&&MUL[i]<=0.875) ch[i][17]="VVH";
              else ch[i][17]="E";

              if(sum3<1.0) MCL[i]=sum3;
              else MCL[i]=1.0;

              if(MCL[i]<=0) ch[i][18]="Z";
              else if(MCL[i]>0&&MCL[i]<=0.125) ch[i][18]="VVL";
              else if(MCL[i]>0.125&MCL[i]<=0.25) ch[i][18]="VL";
              else if(MCL[i]>0.25&&MCL[i]<=0.375) ch[i][18]="L";
              else if(MCL[i]>0.375&&MCL[i]<=0.5) ch[i][18]="M";
              else if(MCL[i]>0.5&&MCL[i]<=0.625) ch[i][18]="H";
              else if(MCL[i]>0.625&&MCL[i]<=0.75) ch[i][18]="VH";
              else if(MCL[i]>0.75&&MCL[i]<=0.875) ch[i][18]="VVH";
              else ch[i][18]="E";

              if(sum4<1.0) MDL[i]=sum4;
              else MDL[i]=1.0;

              if(MDL[i]<=0) ch[i][19]="Z";
              else if(MDL[i]>0&&MDL[i]<=0.125) ch[i][19]="VVL";
              else if(MDL[i]>0.125&MDL[i]<=0.25) ch[i][19]="VL";
              else if(MDL[i]>0.25&&MDL[i]<=0.375) ch[i][19]="L";
              else if(MDL[i]>0.375&&MDL[i]<=0.5) ch[i][19]="M";
              else if(MDL[i]>0.5&&MDL[i]<=0.625) ch[i][19]="H";
              else if(MDL[i]>0.625&&MDL[i]<=0.75) ch[i][19]="VH";
              else if(MDL[i]>0.75&&MDL[i]<=0.875) ch[i][19]="VVH";
              else ch[i][19]="E";


              double radExp=((smaxx[i]-sminx[i])+(smaxy[i]-sminy[i]))/4.0;
              double dc=0,cnt=0;
              for(j=pointer[i-1]+1;j<=pointer[i];j++)
              {
                  dc=dc+Math.sqrt((xcentre-points[j].x)*(xcentre-points[j].x)+(ycentre-points[j].y)*(ycentre-points[j].y));
                  cnt++;
              }
              double radAct=dc/cnt;
              double dimExp=2.0*radExp;
              double dimAct=dk;
              double fx=dimAct/dimExp;
              double gx=radAct/radExp;
              double MOL1,MOL2;
              if(fx<1) MOL1=fx;
              else MOL1=1.0/fx;
              if(gx<1) MOL2=gx;
              else MOL2=1.0/gx;

              if(MOL1<MOL2) MOL[i]=MOL1;
              else MOL[i]=MOL2;


              if(MOL[i]<=0) ch[i][20]="Z";
              else if(MOL[i]>0&&MOL[i]<=0.075) ch[i][20]="VVL";
              else if(MOL[i]>0.075&MOL[i]<=0.15) ch[i][20]="VL";
              else if(MOL[i]>0.15&&MOL[i]<=0.225) ch[i][20]="L";
              else if(MOL[i]>0.225&&MOL[i]<=0.3) ch[i][20]="M";
              else if(MOL[i]>0.3&&MOL[i]<=0.375) ch[i][20]="H";
              else if(MOL[i]>0.375&&MOL[i]<=0.45) ch[i][20]="VH";
              else if(MOL[i]>0.45&&MOL[i]<=0.525) ch[i][20]="VVH";
              else ch[i][20]="E";

              if(MOL[i]<MUL[i]) MOLT[i]=MOL[i];
              else MOLT[i]=MUL[i];

              if(MOLT[i]<=0) ch[i][21]="Z";
              else if(MOLT[i]>0&&MOLT[i]<=0.075) ch[i][21]="VVL";
              else if(MOLT[i]>0.075&MOLT[i]<=0.15) ch[i][21]="VL";
              else if(MOLT[i]>0.15&&MOLT[i]<=0.225) ch[i][21]="L";
              else if(MOLT[i]>0.225&&MOLT[i]<=0.3) ch[i][21]="M";
              else if(MOLT[i]>0.3&&MOLT[i]<=0.375) ch[i][21]="H";
              else if(MOLT[i]>0.375&&MOLT[i]<=0.45) ch[i][21]="VH";
              else if(MOLT[i]>0.45&&MOLT[i]<=0.525) ch[i][21]="VVH";
              else ch[i][21]="E";

             if(MOL[i]<MAL[i]) MOLB[i]=MOL[i];
             else MOLB[i]=MAL[i];

              if(MOLB[i]<=0) ch[i][22]="Z";
              else if(MOLB[i]>0&&MOLB[i]<=0.075) ch[i][22]="VVL";
              else if(MOLB[i]>0.075&MOLB[i]<=0.15) ch[i][22]="VL";
              else if(MOLB[i]>0.15&&MOLB[i]<=0.225) ch[i][22]="L";
              else if(MOLB[i]>0.225&&MOLB[i]<=0.3) ch[i][22]="M";
              else if(MOLB[i]>0.3&&MOLB[i]<=0.375) ch[i][22]="H";
              else if(MOLB[i]>0.375&&MOLB[i]<=0.45) ch[i][22]="VH";
              else if(MOLB[i]>0.45&&MOLB[i]<=0.525) ch[i][22]="VVH";
              else ch[i][22]="E";

              if(MOL[i]<MDL[i]) MOLL[i]=MOL[i];
              else MOLL[i]=MDL[i];

              if(MOLL[i]<=0) ch[i][23]="Z";
              else if(MOLL[i]>0&&MOLL[i]<=0.075) ch[i][23]="VVL";
              else if(MOLL[i]>0.075&MOLL[i]<=0.15) ch[i][23]="VL";
              else if(MOLL[i]>0.15&&MOLL[i]<=0.225) ch[i][23]="L";
              else if(MOLL[i]>0.225&&MOLL[i]<=0.3) ch[i][23]="M";
              else if(MOLL[i]>0.3&&MOLL[i]<=0.375) ch[i][23]="H";
              else if(MOLL[i]>0.375&&MOLL[i]<=0.45) ch[i][23]="VH";
              else if(MOLL[i]>0.45&&MOLL[i]<=0.525) ch[i][23]="VVH";
              else ch[i][23]="E";

              if(MOL[i]<MCL[i]) MOLR[i]=MOL[i];
              else MOLR[i]=MCL[i];

              if(MOLR[i]<=0) ch[i][24]="Z";
              else if(MOLR[i]>0&&MOLR[i]<=0.075) ch[i][24]="VVL";
              else if(MOLR[i]>0.075&MOLR[i]<=0.15) ch[i][24]="VL";
              else if(MOLR[i]>0.15&&MOLR[i]<=0.225) ch[i][24]="L";
              else if(MOLR[i]>0.225&&MOLR[i]<=0.3) ch[i][24]="M";
              else if(MOLR[i]>0.3&&MOLR[i]<=0.375) ch[i][24]="H";
              else if(MOLR[i]>0.375&&MOLR[i]<=0.45) ch[i][24]="VH";
              else if(MOLR[i]>0.45&&MOLR[i]<=0.525) ch[i][24]="VVH";
              else ch[i][24]="E";
          }

      }    
      


    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        jTextField1.setText(""+segNo);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        g.clearRect(0,0, 605,339);
        g=null;
        jTextField1.setText("");
        jTextField2.setText("");
        jLabel8.setText("");
        points=null;
        pointCount=0;
        noUp=0;
        flag=false;
        System.out.println("After clear:\nTotal points: "+pointCount+"\nTotal ups: "+noUp);

        WelcomePanelClass kk = new WelcomePanelClass(pane);
        pane.remove(this);

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:       
                Feature kk = new Feature(pane);
                pane.addTab("Segment Features", kk);
                pane.remove(this);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        if(g==null)
        {
        g= jEditorPane1.getGraphics();
        g.setColor(Color.BLACK);
        }
         for(int i=1;i<=segNo;i++)
         {
             if(i==1) g.setColor(Color.red);
             else if(i==2) g.setColor(Color.green);
             else if(i==3) g.setColor(Color.cyan);
             else if(i==4) g.setColor(Color.BLUE);
             else if(i==5) g.setColor(Color.MAGENTA);
             else if(i==6) g.setColor(Color.DARK_GRAY);
             else if(i==7) g.setColor(Color.pink);
             else if(i==8) g.setColor(Color.ORANGE);
             else if(i==9) g.setColor(Color.red);
             else if(i==10) g.setColor(Color.GREEN);
             else if(i==11) g.setColor(Color.CYAN);
             else if(i==12) g.setColor(Color.BLUE);
             else if(i==13) g.setColor(Color.MAGENTA);
             else if(i==14) g.setColor(Color.darkGray);
             else if(i==15) g.setColor(Color.orange);
             else if(i==16) g.setColor(Color.pink);
             else if(i==17) g.setColor(Color.YELLOW);
             else g.setColor(Color.BLACK);
             for(int j=pointer[i-1]+1;j<=pointer[i];j++)
             {
                  g.fillOval(points[j].x, points[j].y, 4, 4);
             }
         }

    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        String text=jTextField2.getText();
        char charac=text.charAt(0);
        int code=(int)charac;

       connection=null;
       Statement statement=null;
       ResultSet resultset=null;
        try{
           Class.forName("org.sqlite.JDBC");
           connection=DriverManager.getConnection("jdbc:sqlite:" + DBPath.DATABASE_PATH);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, e);
        }

            try{
                for(j=1;j<=segNo;j++)
                {
                      statement=connection.createStatement();
                      String query="INSERT INTO Mixed(character,code,total_seg,seg_no,ch1,ch2,ch3,ch4,ch5,ch6,ch7,ch8,ch9,ch10,ch11,ch12,ch13,ch14,ch15,ch16,ch17,ch18,ch19,ch20,ch21,ch22,ch23,ch24) VALUES ("
                                   +"'"+charac+"'"+","+code+","+segNo+","+j;
                      for(int k=1;k<=24;k++)
                          query=query+","+"'"+ch[j][k]+"'";
                      query=query+")";
                      statement.execute(query);
                      statement.close();
                }

            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null, e);
            }   

       try
        {
            connection.close();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,""+e);
        }

        JOptionPane.showMessageDialog(null,"Training process completed successfully");  

    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        boolean shift=false;
        connection=null;
        Statement statement=null;
        ResultSet resultset=null;
        try{
           Class.forName("org.sqlite.JDBC");
           connection=DriverManager.getConnection("jdbc:sqlite:" + DBPath.DATABASE_PATH);
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, e);
        }


        noChar=1;
        segChar[noChar]=1;
        int maxXvalue=points[0].x;
        int maxYvalue=points[0].y;
        for(j=1;j<up[0];j++)
        {
            if(points[j].x>maxXvalue) maxXvalue=points[j].x;
            if(points[j].y>maxYvalue) maxYvalue=points[j].y;
        }
        hold=up[0];
        for(h=1;h<noUp;h++)
        {
            if(points[hold].y>(maxYvalue-3)&&points[hold].y>(points[0].y+12))
            {
                System.out.println("Recognizer-1 called before division:"+segChar[noChar]+"  "+segOfUp[h-1]);
                 System.out.println("hold: "+points[hold].x+"Max: "+maxXvalue);
                String out="";
                out=characterRecognizer(segChar[noChar],segOfUp[h-1]);
                if(!out.equals(""))
                {
                    char charac=out.charAt(0);
                    int code=(int)charac;
                    if(code!=97&&code!=100)
                    {
                        noChar++;
                        segChar[noChar]=segOfUp[h-1]+1;
                        maxYvalue=points[0].y;
                        System.out.println("Character No: "+noChar+"  Segment: "+segChar[noChar]+"\n");
                    }
                }
            }

            if((points[hold].x>=(maxXvalue-8)&&points[hold].y>=(points[0].y-10)&&points[hold].y<=(points[0].y+10))||points[hold].x>=(maxXvalue-3))
            {
                if(segChar[noChar]!=(segOfUp[h-1]+1))
                {
                    noChar++;
                    segChar[noChar]=segOfUp[h-1]+1;
                    maxYvalue=points[0].y;
                    System.out.println("Character No: "+noChar+"  Segment: "+segChar[noChar]+"\n");
                }
            }
            else
            {
                if(segChar[noChar]!=(segOfUp[h-1]+1))
                {
                 System.out.println("Recognizer-2 called before division:"+segChar[noChar]+"  "+segOfUp[h-1]);
                 System.out.println("hold: "+points[hold].x+"Max: "+maxXvalue);
                String out="";
                out=characterRecognizer(segChar[noChar],segOfUp[h-1]);
                if(!out.equals(""))
                {
                    char charac=out.charAt(0);
                    int code=(int)charac;
                    if(code==97||code==2495||code==98)
                    {
                        noChar++;
                        segChar[noChar]=segOfUp[h-1]+1;
                        maxYvalue=points[0].y;
                        System.out.println("Character No: "+noChar+"  Segment: "+segChar[noChar]+"\n");
                    }
                }
                }
            }

            
            i=up[h]-1;
            for(j=hold;j<=i;j++)
            {
                if(points[j].x>maxXvalue) maxXvalue=points[j].x;
                if(points[j].y>maxYvalue) maxYvalue=points[j].y;
            }
            hold=i+1;
        }


        System.out.println("No error till character division");

        segChar[noChar+1]=segNo+1;
        String out="",out1="";
        for(int k=1;k<=noChar;k++)
        {
            System.out.println("Called "+k+" times"+segChar[k]+"  "+(segChar[k+1]-1));
            out1=characterRecognizer(segChar[k],(segChar[k+1]-1));
            char charac1=out1.charAt(0);

            if(out.length()>0)
            {
            int code1=(int)charac1;
            int length=out.length();
            char charac2=out.charAt(length-1);
            int code2=(int)charac2;
            if((code1==107||code1==109||code1==103)&&(code2==2495||code2==2503||code2==2504))
            {
                if(length>1)
                {
                    char charac3=out.charAt(length-2);
                    String out2="";
                    for(int e=0;e<out.length()-2;e++)
                       out2=out2+out.charAt(e);
                    out=out2;
                    out=out+charac2+charac3;
                    charac2=out.charAt(length-1);
                    code2=(int)charac2;
                    shift=false;
                }
            }
            if(code1==109&&code2!=2447)
            {
                out1=Character.toString((char)2451);
            }
            if((code1==97&&code2==2494))
            {
                String out2="";
                for(int e=0;e<out.length()-1;e++)
                    out2=out2+out.charAt(e);
                out=out2;
                out1=Character.toString((char)2495);
            }

            if(code1==98)
            {
                  String out2="";
                  for(int e=0;e<out.length()-1;e++)
                    out2=out2+out.charAt(e);
                  out=out2;

                  if(code2==2503) out1=Character.toString((char)2504);
                  if(code2==2494) out1=Character.toString((char)99);
                  if(code2==2489) out1=Character.toString((char)2439);
                  if(code2==101) out1=Character.toString((char)2440);
                  if(code2==2465) out1=Character.toString((char)2441);
                  if(code2==102) out1=Character.toString((char)2442);
                  if(code2==2447) out1=Character.toString((char)2448);
                  if(code2==2451) out1=Character.toString((char)2452);
                  if(code2==2466) out1=Character.toString((char)2463);
                  if(code2==2507) out1=Character.toString((char)2508);
            }

            }

            int code1=(int)out1.charAt(0);
            if(code1==2495||code1==2503||code1==2504) shift=false;
            if(out.length()>0)
            {
            int code2=(int)out.charAt(out.length()-1);

            if(code1==103&&(code2==2476||code2==106||code2==2454))
            {
                String out2="";
                for(int e=0;e<out.length()-1;e++)
                    out2=out2+out.charAt(e);
                out=out2;
                if(code2==2476)
                {
                    code1=2461;
                    out1=Character.toString((char)2461);
                }
                else
                {
                    code1=2443;
                    out1=Character.toString((char)2443);
                }

                if(out.length()>0) code2=(int)out.charAt(out.length()-1);
            }

            if(code1==2494&&(code2==104||code2==111||code2==113||code2==112||code2==97||code2==2437||code2==2495))
            {
                String out2="";
                for(int e=0;e<out.length()-1;e++)
                    out2=out2+out.charAt(e);
                out=out2;
                if(code2==2495)
                {
                    out=out+Character.toString((char)2494);
                    code1=2467;
                    out1=Character.toString((char)2467);
                }
                else if(code2 == 2437)
                {
                    code1=2438;
                    out1=Character.toString((char)2438);
                }
                else if(code2 == 104)
                {
                    code1=2474;
                    out1=Character.toString((char)2474);
                }
                else if(code2==111)
                {
                    code1=2455;
                    out1=Character.toString((char)2455);
                }
                else if(code2==113)
                {
                    code1=2486;
                    out1=Character.toString((char)2486);
                }
                else
                {
                    code1=2467;
                    out1=Character.toString((char)2467);
                }

                if(out.length()>0) code2=(int)out.charAt(out.length()-1);
            }

            if(code1==107&&(code2==2476||code2==2465||code2==2466||code2==2479||code2==110))
            {
                String out2="";
                for(int e=0;e<out.length()-1;e++)
                    out2=out2+out.charAt(e);
                out=out2;
                if(code2==2476)
                {
                    code1=2480;
                    out1=Character.toString((char)2480);
                }
                else if(code2==2465)
                {
                    code1=2524;
                    out1=Character.toString((char)2524);
                }
                else if(code2==2466)
                {
                    code1=2525;
                    out1=Character.toString((char)2525);
                }
                else if(code2==2479)
                {
                    code1=2527;
                    out1=Character.toString((char)2527);
                }
                else
                {
                    code1=2433;
                    out1=Character.toString((char)2433);
                }

                if(out.length()>0) code2=(int)out.charAt(out.length()-1);
            }

            if(code2==108&&(code1==105||code1==108))
            {
                String out2="";
                for(int e=0;e<out.length()-1;e++)
                    out2=out2+out.charAt(e);
                out=out2;
                if(code1==105)
                {
                    code1=2434;
                    out1=Character.toString((char)2434);
                }
                else
                {
                    code1=2435;
                    out1=Character.toString((char)2435);
                }

                if(out.length()>0) code2=(int)out.charAt(out.length()-1);
            }

            if(code2==2447&&(code1==109||code1==2451))
            {
                String out2="";
                for(int e=0;e<out.length()-1;e++)
                    out2=out2+out.charAt(e);
                out=out2;
                code1=2462;
                out1=Character.toString((char)2462);

                if(out.length()>0) code2=(int)out.charAt(out.length()-1);
            }

           if(code2==2495||code2==2503||code2==2504)
            {
                char last=out.charAt(out.length()-1);
                String out2="";
                for(int e=0;e<out.length()-1;e++)
                    out2=out2+out.charAt(e);
                out=out2;
                if(code2 == 2503 && code1 == 2494)
                {
                    code1=2507;
                    out1=Character.toString((char)2507);
                }
                else if(code2==2503&&code1==99)
                {
                    code1=2508;
                    out1=Character.toString((char)2508);
                }
                else if(code1==97||code1==104||code1==106||code1==111||code1==112||code1==113)
                {
                    out=out+last;
                }
                else
                {
                    if(shift==false)
                    {
                        out=out+out1;
                        code1=code2;
                        out1=Character.toString((char)code2);
                        shift=true;
                    }
                    else
                    {
                        out=out+last;
                    }
                }

                if(out.length()>0) code2=(int)out.charAt(out.length()-1);
              // }
            }

            }
           
            out = out+out1;
        }
        jLabel8.setText(out);   

        try
        {
             connection.close();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,""+e);
        }    



    }//GEN-LAST:event_jButton8ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables

}
