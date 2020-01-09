package showManifest;

import structs.streamInfo;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class cTimeGUI extends JFrame implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	String url = "";
	ArrayList<JTextArea> qArea = new ArrayList<JTextArea>();
	ArrayList <JScrollPane> qScroll = new ArrayList<JScrollPane>();

	JTextField textUrl;
	JButton btStart, btStop;

	SingleThreadEx2 r = null;
	boolean needAdditionalTextField = true;

	public cTimeGUI(String url) {
		super( "Manifest" );

        setLayout( new FlowLayout() );
        EtchedBorder eborder =  new EtchedBorder();
        JLabel lbl = new JLabel( "Manifest - Chunk Time Viewer" );
        lbl.setBorder(eborder);


		JPanel panelIp = new JPanel();
	    JLabel lbIp = new JLabel("ManifestURL");
	    textUrl = new JTextField(64);
	    textUrl.setText(url);
        panelIp.add(lbIp);
        panelIp.add(textUrl);

        JPanel panelStart = new JPanel();
        btStart = new JButton("Start");
        panelStart.add(btStart);
        btStart.addActionListener(this);

        JPanel panelStop = new JPanel();
        btStop = new JButton("Stop");
        panelStop.add(btStop);
        btStop.addActionListener(this);


        add(panelIp);
        add(panelStart);
        add(panelStop);

        setSize( 1000, 800 );
        setVisible(true);
    }

	public void addTextField(int n) {
		int w = 16;
		int h = 20;
		for(int i = 0; i < n; i++) {
			JTextArea t = new JTextArea(h, w);
			t.setEditable(false);
	        JScrollPane s = new JScrollPane(t);
	        qArea.add(t);
	        qScroll.add(s);
	        add(s);
		}
        setVisible(true);
	}

	public void showCTimes(ArrayList<String> s) {
		int num = s.size();
		if(needAdditionalTextField == true) {
			addTextField(num);
		}
		needAdditionalTextField = false;
		for(int i=0; i<num; i++) {
			qArea.get(i).setText(s.get(i));
		}
		setVisible(true);
	}

	public void showCTimes2(ArrayList<streamInfo> si) {
		int num = si.size();
		if(needAdditionalTextField == true) {
			addTextField(num);
		}
		needAdditionalTextField = false;
		for(int i=0; i<num; i++) {
			qArea.get(i).setText(si.get(i).type);
			qArea.get(i).append(System.lineSeparator());
			qArea.get(i).append(si.get(i).name);
			qArea.get(i).append(System.lineSeparator());
			qArea.get(i).append(si.get(i).time);
		}
		setVisible(true);
	}

	public void showMessage(String msg) {
		if(needAdditionalTextField == true) {
			addTextField(1);
		}
		needAdditionalTextField = false;
		qArea.get(0).setText(msg);
		qArea.get(0).append(System.lineSeparator());
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if((JButton)obj == btStart) {
        	String url = textUrl.getText();
        	//if( r == null) {
        		r = new SingleThreadEx2(url, this);
        		//r.start();
        		Thread t1 = new Thread(r, "A");
        		t1.start();
        	//}
        } else if ((JButton)obj == btStop) {
        	r.stop();
        } else {
            ///lbl.setText("Not supported");
        }
    }
}
