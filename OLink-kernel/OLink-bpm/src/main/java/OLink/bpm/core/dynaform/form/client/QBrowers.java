/*
 * Created on 2005-1-20
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.dynaform.form.client;

/**
 * @author ZhouTY
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
//示例代码 3：QBrowser.java

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
public class QBrowers implements ActionListener, Runnable {

	    private JFrame frame;

	    private JButton go;

	    private JEditorPane content;

	    private JTextField url;

	    private JLabel statusLine;


	    // default constructor

	    public QBrowers () {

	        buildBrowserInterface();

	    }


	    private void buildBrowserInterface() {

	        frame = new JFrame("Q's Browser");

	        // on close, exit the application using System.exit(0);

	        frame.setDefaultCloseOperation (3);


	        url = new JTextField("", 25);

	        go = new JButton("Go Get It");

	        go.addActionListener(this);


	        JPanel controls = new JPanel(new FlowLayout ());

	        controls.add(new JLabel("URL:"));

	        controls.add(url);

	        controls.add(go);

	        content = new JEditorPane();

	        content.setEditable(false);

	        // HTML text. Use the kit in the class javax.swing.text.html.HTMLEditorKit, which

	        // provides support for HTML 3.2

	        content.setContentType("text/html");

	        content.setText("<center><h1>Q's Browser</h1><p> Copyright (c) 2002 Qusay H. Mahmoud</center>");

	        statusLine = new JLabel("Initialization Complete");


	        JPanel panel = new JPanel(new BorderLayout (0, 2));

	        frame.setContentPane(panel);


	        panel.add(controls, "North");

	        panel.add(new JScrollPane (content), "Center");

	        panel.add(statusLine, "South");

	        frame.pack();

	        frame.setVisible(true);

	    }


	    /**

	     * You cannot stop a download with QBrowser

	     * The thread allows multiple downloads to start

	     * concurrently in case a download freezes

	     */

	    public void actionPerformed (ActionEvent event) {

	        Thread thread = new Thread(this);

	        thread.start();

	    }

	    // this is the Thread's run method

	    public void run () {

	        try {

	            String str = url.getText();

	            URL url = new URL(str);

	            readURL(url);

	        } catch (IOException ioe) {

	            statusLine.setText("Error: "+ioe.getMessage());

	            showException(ioe);

	        }

	    }


	    private void showException(Exception ex) {

	        StringWriter trace = new StringWriter ();

	        ex.printStackTrace (new PrintWriter (trace));

	        content.setContentType ("text/html");

	        content.setText ("<h1>" + ex + "</h1><pre>" + trace + "</pre>");

	    }


	    /**

	     * The URL class is capable of handling http:// and https:// URLs

	     */

	    private void readURL(URL url) throws IOException {

	        statusLine.setText("Opening " + url.toExternalForm());

	        URLConnection connection = url.openConnection();

	        StringBuffer buffer = new StringBuffer();

	        BufferedReader in=null;

	        try {

	            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

	            String line;

	            while ((line = in.readLine()) != null) {

	                buffer.append(line).append('\n');

	                statusLine.setText("Read " + buffer.length () + " bytes...");

	            }

	        } finally {

	            if(in != null) in.close();

	        }

	        String type = connection.getContentType();

	        if(type == null) type = "text/plain";

	        statusLine.setText("Content type " + type);

	        content.setContentType(type);

	        content.setText(buffer.toString());

	        statusLine.setText("Done");

	    }


	    public static void main (String[] args) {

	    	//QBrowers browser = new QBrowers();

	    }

	}
