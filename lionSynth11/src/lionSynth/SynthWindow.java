package lionSynth;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class SynthWindow extends JFrame {
	
	JFrame oscilloscopeWindow;
	
	JMenuBar menuBar;
		JMenu fileMenu;
			JMenuItem openPresetMI;
			JMenuItem savePresetMI;
		JMenu toolsMenu;
			JMenuItem oscilloscopeMI;
		JMenu skinMenu;
			JMenuItem nimbusSkinMI;
			JMenuItem swingDefaultSkinMI;
			JMenuItem halfLifeSkinMI;
			JMenuItem windowsSkinMI;

	JPanel northPanel;
		JPanel oscPanel1;
			JComboBox<String> comboWaveshapeOsc1;
			JLabel lOsc1;
			JSpinner o1OctaveSpinner;
			JSpinner o1NoteSpinner;
			//JSpinner o1FineSpinner;
			JComboBox<String> comboWaveshapeOsc2;
			JLabel lOsc2;
			JSpinner o2OctaveSpinner;
			JSpinner o2NoteSpinner;
			//JSpinner o2FineSpinner;
			
		JPanel oscPanel2;
			JComboBox<String> comboWaveshapeOsc3;
			JLabel lOsc3;
			JSpinner o3OctaveSpinner;
			JSpinner o3NoteSpinner;
			//JSpinner o3FineSpinner;
			JComboBox<String> comboWaveshapeOsc4;
			JLabel lOsc4;
			JSpinner o4OctaveSpinner;
			JSpinner o4NoteSpinner;
			//JSpinner o4FineSpinner;
			
			
	JPanel southPanel;
		JPanel subSouthPanel;
			JPanel mixerPanel;
				JPanel upperMixerPanel;
					JLabel lVol1;
					JLabel lVol2;
					JLabel lVolM;
					JLabel lKnee;
				JPanel centerMixerPanel;
					JSlider sInput1Volume;
					JSlider sInput2Volume;
					JSlider sMixerOutputVolume;
					JSlider sMixerKnee;
				JPanel bottomMixerPanel;
					JLabel lVol1Slider;
					JLabel lVol2Slider;
					JLabel lVolMasterSlider;
					JLabel lKneeSlider;
			Keyboard keyboard;
					
			JPanel adsrPanel;
				JPanel upperADSRPanel;
					JLabel lAttack;
					JLabel lDecay;
					JLabel lSustain;
					JLabel lRelease;
				JPanel centerADSRPanel;
					JSlider sAttack;
					JSlider sDecay;
					JSlider sSustain;
					JSlider sRelease;
				JPanel bottomADSRPanel;
					JLabel lAttackValue;
					JLabel lDecayValue;
					JLabel lSustainValue;
					JLabel lReleaseValue;
					
			JPanel optionPanel;
				JButton playButton;
				JPanel hertzPanel;
					JLabel hzLabel;
					JLabel hertzLabel;
					JLabel keySignatureLabel;
					JCheckBox keySignatureNamesCheck;
			
		HashMap<String, WaveShape> stringToWaveshapes;
		HashMap<WaveShape, String> waveshapeToString;
			
		JTabbedPane multiTabPanel; // useless for the time being
		
		// synth components declared to be visible to all methods of the class
		Oscillator o1;
		Oscillator o2;
		Oscillator o3;
		Oscillator o4;
		Envelope mainEnvelope;
		Mixer mainMixer;
		Mixer oscillatorMixer1;
		Mixer oscillatorMixer2;
		Filter mainFilter;
		
		// LOGGER
		private static Logger logger = Logger.getLogger(SynthWindow.class.getName());
		
		
		
		public SynthWindow(){
			
			try (FileInputStream fis = new FileInputStream("logger.properties")) {
	            LogManager.getLogManager().readConfiguration(fis);
	        } catch (IOException e) {
	            logger.log(Level.SEVERE, "Logger config file could not be read.");
	        }
			
			guiComponentDeclaration();
			addComponentsToWindow();
			
			// OSCILLATOR MODULE INITIALIZATION
			o1 = new Oscillator();
			o1.setWaveShape(WaveShape.SAW);
			o2 = new Oscillator();
			o2.setWaveShape(WaveShape.SAW);
			o3 = new Oscillator();
			o3.setWaveShape(WaveShape.SAW);
			o4 = new Oscillator();
			o4.setWaveShape(WaveShape.SAW);
			
			// MIXER INITIALIZATION 
			oscillatorMixer1 = new Mixer();
			o2.setVolume(1);
			o4.setVolume(1);
			oscillatorMixer1.setInput1Gain(o1.getVolume());
			oscillatorMixer1.setInput2Gain(o2.getVolume());
			oscillatorMixer1.setMasterGain(1);
			
			oscillatorMixer2 = new Mixer();
			oscillatorMixer2.setInput1Gain(o3.getVolume());
			oscillatorMixer2.setInput2Gain(o4.getVolume());
			oscillatorMixer2.setMasterGain(1);
			
			mainMixer = new Mixer();
			mainMixer.setInput1Gain(1);
			mainMixer.setInput2Gain(1);
			mainMixer.setMasterGain(1);
			
			// ENVELOPE INITIALIZATION
			mainEnvelope = new Envelope();
			mainEnvelope.setAttack(0);
			mainEnvelope.setDecay(0.2);
			mainEnvelope.setSustain(1);
			mainEnvelope.setRelease(1);
			
			// FILTER INITIALIZATION; it is currently inactive
			mainFilter = new Filter(22000, 0, FilterType.HIGH_CUT);
			
			// SYNTHESIZER'S ROUTING
			LionSynth lionSynth = new LionSynth(o1, o2, o3, o4, mainEnvelope, oscillatorMixer1, oscillatorMixer2, mainMixer, mainFilter);
			//lionSynth.setPlaying(true); // IRT routing
			//lionSynth.processStream(); // not working, btw	
			
			// LISTENERS
			addAllComponentsListeners();
			
			// OSCILLOSCOPE WINDOW
			oscilloscopeMI.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// we will avoid any attempt to create a window if it is already open. If it is, then we will focus it.

					try {
						if (!oscilloscopeWindow.isDisplayable()) {
							oscilloscopeWindow = null;
						}
					} catch (NullPointerException x) {
						// the first time the execution enters this line, the oscilloscope window value is null, so Java immediately throws an exception
						// This issue won't happen if the item was declared, this is, the second time the execution gets into this line.
						// no, I have not come up with a better approach to this. The people of Stack Overflow encourages not to use multiple windows, tabbed panels and so forth, instead.
					} catch (Exception x) {
						System.out.println("Unknokn Exception: ");
						System.out.println(x);
						x.printStackTrace();
					
					} finally {
						if (oscilloscopeWindow == null) {
							oscilloscopeWindow = new OscilloscopeWindow2();
							
						} else {
							oscilloscopeWindow.requestFocus();
							
						}
						
					}

				}
			});

			// PLAY
			playButton.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					
					
					lionSynth.process();
					
				}
			});
			
			
			// PRESETS
			savePresetMI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// declaring the fileChooser we will be working with
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select a preset file");

				// getting into the current directory
				File workingDirectory = new File(System.getProperty("user.dir"));
				fileChooser.setCurrentDirectory(workingDirectory);

				int result = fileChooser.showSaveDialog(menuBar);
				// if the the user does not close the file chooser window
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();

					ObjectOutputStream oos;
					try {
						// then we open a input stream and write the whole lionSynth instance into the specified file
						oos = new ObjectOutputStream(new FileOutputStream(file));
						oos.writeObject(lionSynth);
						oos.close();
					} catch (FileNotFoundException e1) {
						// this exception shouldn't happen since the JFileChooser is supposed to manage the exception in first place
						System.err.println("File not found.");
						e1.printStackTrace();
						
					} catch (IOException e1) {
						System.err.println("IO Exception: ");
						e1.printStackTrace();
					} catch (Exception e1) {
						System.err.println("Unknown Exception: ");
						e1.printStackTrace();
					}

				} else {
					// this happens if the user did not open any file, this is, closed the JFileChooser Window
					System.out.println("File not selected");
				}

			}
			});
			
			openPresetMI.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// declaring the fileChooser we will be working with
					JFileChooser fileChooser = new JFileChooser();
					
					// getting into the current directory
					File workingDirectory = new File(System.getProperty("user.dir"));
					fileChooser.setCurrentDirectory(workingDirectory);
					
					int result = fileChooser.showOpenDialog(menuBar);
					// if the the user does not close the file chooser window
					if (result == JFileChooser.APPROVE_OPTION) {
						try {
							File file = fileChooser.getSelectedFile();
							
							ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
							
							LionSynth l = new LionSynth((LionSynth) ois.readObject());
							
							// this method was created so the preset could be copied without having to set each value individually or deal with scope issues
							o1.cloneFrom(l.o1);	
							o2.cloneFrom(l.o2);
							o3.cloneFrom(l.o3);
							o4.cloneFrom(l.o4);
							mainEnvelope.cloneFrom(l.mainEnvelope);
							mainMixer.cloneFrom(l.mainMixer);
							oscillatorMixer1.cloneFrom(l.oscillatorMixer1);
							oscillatorMixer2.cloneFrom(l.oscillatorMixer2);
							mainFilter.cloneFrom(l.mainFilter);
							
							// now we set the component (labels, slider) values, according to those values we just loaded
							copyValuesToComponents();
							
							
							ois.close();
							
							
						} catch (FileNotFoundException x) {
							System.err.println("No s� c�mo te las has arreglado para borrar un archivo en tan poco tiempo...");
							x.printStackTrace();
						} catch (IOException x) {
							System.err.println("...");
							x.printStackTrace();
						} catch (ClassNotFoundException x) {
							JOptionPane.showMessageDialog(northPanel, "Chosen file does not belong to this program.");
							System.err.println("El archivo elegido no es un preset de este programa");
						} catch (Exception x) {
							System.err.println("Error desconocido");
							x.printStackTrace();
						}
						
					} else {
						System.out.println("File not selected");
					}
					
					
				}
			});
			
			
			setTitle("LionSynth");
			pack();
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			setResizable(false);
			setVisible(true);
		}
		
		// COMPONENTS
		public void guiComponentDeclaration() {
			// MENUS
				fileMenu = new JMenu("File");
				openPresetMI = new JMenuItem("Load preset from file...");
				savePresetMI = new JMenuItem("Save preset to file...");
				skinMenu = new JMenu("Skin");
				swingDefaultSkinMI = new JMenuItem("Swing Default");
				nimbusSkinMI = new JMenuItem("Nimbus (Macintosh)");
				windowsSkinMI = new JMenuItem("Windows");
				halfLifeSkinMI = new JMenuItem("Motif");
				toolsMenu = new JMenu("Tools");
				oscilloscopeMI = new JMenuItem("Oscilloscope");
				menuBar = new JMenuBar();
				
			// PANELS
				oscPanel1 = new JPanel(new GridLayout(2, 1));
				oscPanel1.setBorder(BorderFactory.createTitledBorder("Osc 1 + 2"));
				oscPanel2 = new JPanel(new GridLayout(2, 1));
				oscPanel2.setBorder(BorderFactory.createTitledBorder("Osc 3 + 4"));
				
				mixerPanel = new JPanel(new BorderLayout());
				mixerPanel.setBorder(BorderFactory.createTitledBorder("Mixer"));
				upperMixerPanel = new JPanel(new GridLayout(1, 4));
				centerMixerPanel = new JPanel(new GridLayout(1, 4));
				bottomMixerPanel = new JPanel(new GridLayout(1, 4));

				adsrPanel = new JPanel(new BorderLayout());
				adsrPanel.setBorder(BorderFactory.createTitledBorder("Envelope"));
				upperADSRPanel = new JPanel(new GridLayout(1, 4));
				centerADSRPanel = new JPanel(new GridLayout(1, 4));
				bottomADSRPanel = new JPanel(new GridLayout(1, 4));
				
				keyboard = new Keyboard();
				hertzPanel = new JPanel(new GridLayout(2,2));
				
				northPanel = new JPanel(new GridLayout(1, 2));
				southPanel = new JPanel(new GridLayout(2, 1));
				subSouthPanel = new JPanel(new GridLayout(1, 3));
				optionPanel = new JPanel(new GridLayout(5, 1));
				optionPanel.setBorder(BorderFactory.createTitledBorder("Options"));
				multiTabPanel = new JTabbedPane();
				
			// LABELS
				playButton = new JButton("PLAY");
				lOsc1 = new JLabel("OSC 1");
				lOsc2 = new JLabel("OSC 2");
				lOsc3 = new JLabel("OSC 3");
				lOsc4 = new JLabel("OSC 4");
				lVol1 = new JLabel("In 1");
				lVol1.setHorizontalAlignment(JLabel.CENTER);
				lVol2 = new JLabel("In 2");
				lVol2.setHorizontalAlignment(JLabel.CENTER);
				lVolM = new JLabel("Out");
				lVolM.setHorizontalAlignment(JLabel.CENTER);
				lKnee = new JLabel("Knee");
				lKnee.setHorizontalAlignment(JLabel.CENTER);
				lVol1Slider = new JLabel("100");
				lVol1Slider.setHorizontalAlignment(JLabel.CENTER);
				lVol2Slider = new JLabel("100");
				lVol2Slider.setHorizontalAlignment(JLabel.CENTER);
				lVolMasterSlider = new JLabel("100");
				lVolMasterSlider.setHorizontalAlignment(JLabel.CENTER);
				lKneeSlider = new JLabel("1.0");
				lKneeSlider.setHorizontalAlignment(JLabel.CENTER);
				
			// OPTION PANEL
				hzLabel = new JLabel("Note: ");
				hzLabel.setHorizontalAlignment(JLabel.CENTER);
				hertzLabel = new JLabel( MusicalNote.DO.getName() );
				hertzLabel.setHorizontalAlignment(JLabel.CENTER);
				keySignatureNamesCheck = new JCheckBox();
				keySignatureNamesCheck.setHorizontalAlignment(JLabel.CENTER);
				keySignatureLabel = new JLabel("Do Fixed");
				keySignatureLabel.setHorizontalAlignment(JLabel.CENTER);
				
			// MIXER PANEL COMPONENT 
				sInput1Volume = new JSlider(1, 0, 100, 100); // JSlider(int orientation, int min, int max, int default value)
				sInput2Volume = new JSlider(1, 0, 100, 100);
				sMixerOutputVolume = new JSlider(1, 0, 100, 100);
				sMixerKnee = new JSlider(1, 0, 120, 100);
				
			// ADSR
				sAttack = new JSlider(1, 0, 100, 0);
				sDecay = new JSlider(1, 0, 100, 0);
				sSustain = new JSlider(1, 0, 100, 100);
				sRelease = new JSlider(1, 0, 100, 100);
				lAttack = new JLabel("A");
				lAttack.setHorizontalAlignment(JLabel.CENTER);
				lDecay = new JLabel("D");
				lDecay.setHorizontalAlignment(JLabel.CENTER);
				lSustain = new JLabel("S");
				lSustain.setHorizontalAlignment(JLabel.CENTER);
				lRelease = new JLabel("R");
				lRelease.setHorizontalAlignment(JLabel.CENTER);
				lAttackValue = new JLabel("0");
				lAttackValue.setHorizontalAlignment(JLabel.CENTER);
				lDecayValue = new JLabel("0");
				lDecayValue.setHorizontalAlignment(JLabel.CENTER);
				lSustainValue = new JLabel("1.0"); 
				lSustainValue.setHorizontalAlignment(JLabel.CENTER);
				lReleaseValue = new JLabel("100");
				lReleaseValue.setHorizontalAlignment(JLabel.CENTER);
				
			// OSCILLATOR SPINNERS
				o1OctaveSpinner = new JSpinner(new SpinnerNumberModel(0, -4, 4, 1));
				o2OctaveSpinner = new JSpinner(new SpinnerNumberModel(0, -4, 4, 1));
				o3OctaveSpinner = new JSpinner(new SpinnerNumberModel(0, -4, 4, 1));
				o4OctaveSpinner = new JSpinner(new SpinnerNumberModel(0, -4, 4, 1));
				o1NoteSpinner = new JSpinner(new SpinnerNumberModel(0, -12, 12, 1));
				o2NoteSpinner = new JSpinner(new SpinnerNumberModel(0, -12, 12, 1));
				o3NoteSpinner = new JSpinner(new SpinnerNumberModel(0, -12, 12, 1));
				o4NoteSpinner = new JSpinner(new SpinnerNumberModel(0, -12, 12, 1));
	}
		
		public void addComponentsToWindow() {
				// MENU BAR
					fileMenu.add(openPresetMI);
					fileMenu.add(savePresetMI);
					toolsMenu.add(oscilloscopeMI);
					skinMenu.add(swingDefaultSkinMI);
					skinMenu.add(nimbusSkinMI);
					skinMenu.add(windowsSkinMI);
					skinMenu.add(halfLifeSkinMI);
					menuBar.add(fileMenu);
					menuBar.add(toolsMenu);
					menuBar.add(skinMenu);
					
				// OSCILLATOR PANEL COMPONENTS
					stringToWaveshapes = new HashMap<>();
					waveshapeToString = new HashMap<>();
					stringToWaveshapes.put("SAW", WaveShape.SAW);
					stringToWaveshapes.put("SQUARE", WaveShape.SQUARE);
					stringToWaveshapes.put("PULSE", WaveShape.PULSE);
					stringToWaveshapes.put("TRI", WaveShape.TRIANGLE);
					stringToWaveshapes.put("SINE", WaveShape.SINE);
					waveshapeToString.put(WaveShape.SAW, "SAW");
					waveshapeToString.put(WaveShape.SQUARE, "SQUARE");
					waveshapeToString.put(WaveShape.PULSE, "PULSE");
					waveshapeToString.put(WaveShape.TRIANGLE, "TRI");
					waveshapeToString.put(WaveShape.SINE, "SINE");
					comboWaveshapeOsc1 = new JComboBox<String>(new Vector<String>(stringToWaveshapes.keySet()));
					comboWaveshapeOsc1.setSelectedItem("SAW");
					comboWaveshapeOsc2 = new JComboBox<String>(new Vector<String>(stringToWaveshapes.keySet()));
					comboWaveshapeOsc2.setSelectedItem("SAW");
					comboWaveshapeOsc3 = new JComboBox<String>(new Vector<String>(stringToWaveshapes.keySet()));
					comboWaveshapeOsc3.setSelectedItem("SAW");
					comboWaveshapeOsc4 = new JComboBox<String>(new Vector<String>(stringToWaveshapes.keySet()));
					comboWaveshapeOsc4.setSelectedItem("SAW");
					
					oscPanel1.add(lOsc1);
					oscPanel1.add(o1OctaveSpinner);
					oscPanel1.add(comboWaveshapeOsc1);
					oscPanel1.add(lOsc2);
					oscPanel1.add(o2OctaveSpinner);
					oscPanel1.add(comboWaveshapeOsc2);
					oscPanel2.add(lOsc3);
					oscPanel2.add(o3OctaveSpinner);
					oscPanel2.add(comboWaveshapeOsc3);
					oscPanel2.add(lOsc4);
					oscPanel2.add(o4OctaveSpinner);
					oscPanel2.add(comboWaveshapeOsc4);
					// TODO add note spinners
					
				// MIXER PANEL COMPONENTS
					upperMixerPanel.add(lVol1);
					upperMixerPanel.add(lVol2);
					upperMixerPanel.add(lVolM);
					upperMixerPanel.add(lKnee);
					centerMixerPanel.add(sInput1Volume);
					centerMixerPanel.add(sInput2Volume);
					centerMixerPanel.add(sMixerOutputVolume);
					centerMixerPanel.add(sMixerKnee);
					bottomMixerPanel.add(lVol1Slider);
					bottomMixerPanel.add(lVol2Slider);
					bottomMixerPanel.add(lVolMasterSlider);
					bottomMixerPanel.add(lKneeSlider);
					mixerPanel.add(upperMixerPanel, BorderLayout.NORTH);
					mixerPanel.add(centerMixerPanel, BorderLayout.CENTER);
					mixerPanel.add(bottomMixerPanel, BorderLayout.SOUTH);
					
					
				// ADSR PANEL
					upperADSRPanel.add(lAttack);
					upperADSRPanel.add(lDecay);
					upperADSRPanel.add(lSustain);
					upperADSRPanel.add(lRelease);
					centerADSRPanel.add(sAttack);
					centerADSRPanel.add(sDecay);
					centerADSRPanel.add(sSustain);
					centerADSRPanel.add(sRelease);
					bottomADSRPanel.add(lAttackValue);
					bottomADSRPanel.add(lDecayValue);
					bottomADSRPanel.add(lSustainValue);
					bottomADSRPanel.add(lReleaseValue);
					adsrPanel.add(upperADSRPanel, BorderLayout.NORTH);
					adsrPanel.add(centerADSRPanel, BorderLayout.CENTER);
					adsrPanel.add(bottomADSRPanel, BorderLayout.SOUTH);
					
					
				// OPTION PANEL
					optionPanel.add(playButton);
					hertzPanel.add(hzLabel);
					hertzPanel.add(hertzLabel);
					hertzPanel.add(keySignatureLabel);
					hertzPanel.add(keySignatureNamesCheck);
					optionPanel.add(hertzPanel);
					
				// MAIN PANEL
					this.add(menuBar, BorderLayout.NORTH);
					northPanel.add(oscPanel1);
					northPanel.add(oscPanel2);
					subSouthPanel.add(mixerPanel);
					subSouthPanel.add(adsrPanel);
					subSouthPanel.add(optionPanel);
					southPanel.add(subSouthPanel);
					southPanel.add(keyboard);
					add(northPanel, BorderLayout.CENTER);
					add(southPanel, BorderLayout.SOUTH);
		}
		
		public void copyValuesToComponents() {
			// since the values of the GUI are not refreshed, we will have to take the values from the components to the GUI
			// TODO
			/*
			lDecayValue.setText(Double.toString(mainEnvelope.getDecay()));
			lReleaseValue.setText(Double.toString(mainEnvelope.getRelease()));
			sAttack.setValue(0);
			sDecay.setValue(0);
			sRelease.setValue(0);
			*/
			lAttackValue.setText(Double.toString(mainEnvelope.getAttack()));
			
			double valueD = mainEnvelope.getSustain();
			System.out.println(valueD);
			lSustainValue.setText(Double.toString(valueD));
			sSustain.setValue((int) (valueD * 100));
			
		// OSCILLATORS
			int value;
			value = o1.getOctave();
			o1OctaveSpinner.setValue(value);
			value = o2.getOctave();
			o2OctaveSpinner.setValue(value);
			value = o3.getOctave();
			o3OctaveSpinner.setValue(value);
			value = o4.getOctave();
			o4OctaveSpinner.setValue(value);
			
			WaveShape waveshape = o1.getWaveShape();
			comboWaveshapeOsc1.setSelectedItem(waveshapeToString.get(waveshape));
			waveshape = o2.getWaveShape();
			comboWaveshapeOsc2.setSelectedItem(waveshapeToString.get(waveshape));
			waveshape = o3.getWaveShape();
			comboWaveshapeOsc3.setSelectedItem(waveshapeToString.get(waveshape));
			waveshape = o4.getWaveShape();
			comboWaveshapeOsc4.setSelectedItem(waveshapeToString.get(waveshape));
			
		// MIXER
			value = (int) (mainMixer.getInput1Gain() * 1000);
			sInput1Volume.setValue(value);
			lVol1Slider.setText(Integer.toString(value));
			value = (int) (mainMixer.getInput2Gain() * 1000);
			sInput2Volume.setValue(value);
			lVol2Slider.setText(Integer.toString(value));
			value = (int) (mainMixer.getMasterGain() * 1000);
			sMixerOutputVolume.setValue(value);
			lVolMasterSlider.setText(Integer.toString(value));
			double value2 = mainMixer.getAttenuationRatio();
			sMixerKnee.setValue((int) (value2*100));
			lKneeSlider.setText(Double.toString(value2));
			
		}

		// LISTENERS
		public void addAllComponentsListeners() {
			addOscillatorListeners();
			
			addMainMixerListeners();
			
			addASDRListeners();			
			
			addKeyboardListeners();

			addSkinListeners();
				
			addOscillatorSpinnerListeners();
		}
		
		public void addKeyboardListeners() {
			keyboard.firstKey.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicalNote note;
					if ( keySignatureNamesCheck.isSelected() ) {
						note = MusicalNote.C;	
					} else {
						note = MusicalNote.DO;						
					}
					
					hertzLabel.setText( note.getName() );
					Oscillator.setHz( note.getHz() );
					
					} } );
		
			keyboard.secondKey.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicalNote note;
					if ( keySignatureNamesCheck.isSelected() ) {
						note = MusicalNote.C;	
					} else {
						note = MusicalNote.DO;						
					}
					hertzLabel.setText( note.getNameSharp() );
					Oscillator.setHz( note.getHzSharp() );
					
				}
			});
			keyboard.thirdKey.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicalNote note;
					if ( keySignatureNamesCheck.isSelected() ) {
						note = MusicalNote.D;	
					} else {
						note = MusicalNote.RE;						
					}
					hertzLabel.setText( note.getName() );
					Oscillator.setHz( note.getHz() );
					
				}
			});
			
			keyboard.fourthKey.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicalNote note;
					if ( keySignatureNamesCheck.isSelected() ) {
						note = MusicalNote.D;	
					} else {
						note = MusicalNote.RE;						
					}
					hertzLabel.setText( note.getNameSharp() );
					Oscillator.setHz( note.getHzSharp() );
				}
			});
			
			keyboard.fifthKey.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicalNote note;
					if ( keySignatureNamesCheck.isSelected() ) {
						note = MusicalNote.E;	
					} else {
						note = MusicalNote.MI;						
					}
					hertzLabel.setText( note.getName() ); 
					Oscillator.setHz( note.getHz() );
				}
			});
			
			keyboard.sixthKey.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicalNote note;
					if ( keySignatureNamesCheck.isSelected() ) {
						note = MusicalNote.F;	
					} else {
						note = MusicalNote.FA;						
					}
					hertzLabel.setText( note.getName() ); 
					Oscillator.setHz( note.getHz() );
				}
			});
			
			keyboard.seventhKey.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicalNote note;
					if ( keySignatureNamesCheck.isSelected() ) {
						note = MusicalNote.F;	
					} else {
						note = MusicalNote.FA;
					}
					hertzLabel.setText( note.getNameSharp() ); 
					Oscillator.setHz( note.getHzSharp() );
				}
			});
			// eighth
			keyboard.eighthKey.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicalNote note;
					if ( keySignatureNamesCheck.isSelected() ) {
						note = MusicalNote.G;	
					} else {
						note = MusicalNote.SOL;
					}
					hertzLabel.setText( note.getName() ); 
					Oscillator.setHz( note.getHz() );
				}
			});
			
			keyboard.ninthKey.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicalNote note;
					if ( keySignatureNamesCheck.isSelected() ) {
						note = MusicalNote.G;	
					} else {
						note = MusicalNote.SOL;
					}
					hertzLabel.setText( note.getNameSharp() ); 
					Oscillator.setHz( note.getHzSharp() );
				}
			});
			
			keyboard.tenthKey.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicalNote note;
					if ( keySignatureNamesCheck.isSelected() ) {
						note = MusicalNote.A;	
					} else {
						note = MusicalNote.LA;
					}
					hertzLabel.setText( note.getName() ); 
					Oscillator.setHz( note.getHz() );
				}
			});
			
			keyboard.eleventhKey.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicalNote note;
					if ( keySignatureNamesCheck.isSelected() ) {
						note = MusicalNote.A;	
					} else {
						note = MusicalNote.LA;
					}
					hertzLabel.setText( note.getNameSharp() ); 
					Oscillator.setHz( note.getHzSharp() );
				}
			});
			
			keyboard.twelfthKey.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					MusicalNote note;
					if ( keySignatureNamesCheck.isSelected() ) {
						note = MusicalNote.B;	
					} else {
						note = MusicalNote.SI;
					}
					hertzLabel.setText( note.getName() ); 
					Oscillator.setHz( note.getHz() );
				}
			});
			
			keySignatureNamesCheck.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if ( keySignatureNamesCheck.isSelected() ) {
						keySignatureLabel.setText("German Key");
					} else {
						keySignatureLabel.setText("Fixed Do");
					}
					
				}
			});
		}
		
		public void addOscillatorListeners() {
			comboWaveshapeOsc1.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					o1.setWaveShape(stringToWaveshapes.get(comboWaveshapeOsc1.getSelectedItem())); 
				} } );
			
			comboWaveshapeOsc2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					o2.setWaveShape(stringToWaveshapes.get(comboWaveshapeOsc2.getSelectedItem())); } } );
			
			comboWaveshapeOsc3.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					o3.setWaveShape(stringToWaveshapes.get(comboWaveshapeOsc3.getSelectedItem())); } } );
			
			comboWaveshapeOsc4.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					o4.setWaveShape(stringToWaveshapes.get(comboWaveshapeOsc4.getSelectedItem())); } } );
		}
		
		public void addOscillatorSpinnerListeners() {
			o1OctaveSpinner.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					int value = (int) o1OctaveSpinner.getValue();
					o1.setOctave(value);
					
				}
			});
			
			o2OctaveSpinner.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					int value = (int) o2OctaveSpinner.getValue();
					o2.setOctave(value);
					
				}
			});
			
			o3OctaveSpinner.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					int value = (int) o3OctaveSpinner.getValue();
					o3.setOctave(value);
					
				}
			});

			o4OctaveSpinner.addChangeListener(new ChangeListener() {
	
				@Override
				public void stateChanged(ChangeEvent e) {
					int value = (int) o4OctaveSpinner.getValue();
					o4.setOctave(value);
					
				}
			});
		}
		
		public void addMainMixerListeners() {
			sInput1Volume.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					mainMixer.setInput1Gain((double) sInput1Volume.getValue() / 1000 ); 
					lVol1Slider.setText(String.valueOf(sInput1Volume.getValue()));
				} } );
			
			sInput2Volume.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					mainMixer.setInput2Gain((double) sInput2Volume.getValue() / 1000 );
					lVol2Slider.setText(String.valueOf(sInput2Volume.getValue()));
				} } );
			
			sMixerOutputVolume.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					mainMixer.setMasterGain((double) sMixerOutputVolume.getValue() / 1000 );
					lVolMasterSlider.setText(String.valueOf(sMixerOutputVolume.getValue()));
				} } );
			
			sMixerKnee.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					mainMixer.setAttenuationRatio((double) sMixerKnee.getValue() / 100 ); 
					lKneeSlider.setText(String.valueOf((double) sMixerKnee.getValue() / 100));
				} } );
		}
		
		public void addASDRListeners() {
			sAttack.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					double attackValue = Function.adsrSlider(sAttack.getValue());
					mainEnvelope.setAttack(attackValue * Math.pow(10, -4)); // attack value is set to seconds
					
					// value to text
					if (attackValue >= 10000) {
						attackValue *= Math.pow(10, -4);
						System.out.println(sAttack.getValue());
						System.out.printf("\n%.2f s\n", attackValue);
					} else if (attackValue > 1000) {
						attackValue *= Math.pow(10, -1);
						System.out.println(sAttack.getValue());
						System.out.printf("\n%.0f ms\n", attackValue);
					} else {
						System.out.println(sAttack.getValue());
						attackValue *= Math.pow(10, -1);
						System.out.printf("\n%.1f ms\n", attackValue);
					}

					lAttackValue.setText(String.valueOf(attackValue));
				} } );

			sDecay.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					double decayValue = Function.adsrSlider(sDecay.getValue());
					mainEnvelope.setDecay(decayValue * Math.pow(10, -4)); // attack value is set to seconds

					// value to text
					if (decayValue >= 10000) {
						decayValue *= Math.pow(10, -4);
						System.out.println(sAttack.getValue());
						System.out.printf("\n%.2f s\n", decayValue);
					} else if (decayValue > 1000) {
						decayValue *= Math.pow(10, -1);
						System.out.println(sAttack.getValue());
						System.out.printf("\n%.0f ms\n", decayValue);
					} else {
						System.out.println(sAttack.getValue());
						decayValue *= Math.pow(10, -1);
						System.out.printf("\n%.1f ms\n", decayValue);
					}
					
					lDecayValue.setText(String.valueOf(sDecay.getValue()));
				} } );
			
			sSustain.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					mainEnvelope.setSustain((double) sSustain.getValue() / 100 );
					lSustainValue.setText(String.valueOf(mainEnvelope.getSustain()));
				} } );
			
			sRelease.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					double sustainValue = Function.adsrSlider(sSustain.getValue());
					mainEnvelope.setSustain(sustainValue * Math.pow(10, -4)); // attack value is set to seconds

					// value to text
					if (sustainValue >= 10000) {
						sustainValue *= Math.pow(10, -4);
						System.out.println(sAttack.getValue());
						System.out.printf("\n%.2f s\n", sustainValue);
					} else if (sustainValue > 1000) {
						sustainValue *= Math.pow(10, -1);
						System.out.println(sAttack.getValue());
						System.out.printf("\n%.0f ms\n", sustainValue);
					} else {
						System.out.println(sAttack.getValue());
						sustainValue *= Math.pow(10, -1);
						System.out.printf("\n%.1f ms\n", sustainValue);
					}
					
					lReleaseValue.setText(String.valueOf(sRelease.getValue()));
				} } );
		}
		
		public void addSkinListeners() {
			
			swingDefaultSkinMI.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						PrintStream ps = new PrintStream(new FileOutputStream("lionSynth.init"));
						String skinLine = "skin=SWING";
						logger.log(Level.INFO, "First line written: " + skinLine + " in lionSynth.init");
						JOptionPane.showMessageDialog(null, "Skin set to Swing's Default. Restart needed to be applied.", "Skin set", JOptionPane.INFORMATION_MESSAGE);
						ps.println(skinLine);
						ps.close();
					} catch(Exception e1) {
						System.err.println("Unknown error");
						e1.printStackTrace();
					}
					
				}
			});
			
			nimbusSkinMI.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						PrintStream ps = new PrintStream(new FileOutputStream("lionSynth.init"));
						String skinLine = "skin=NIMBUS";
						logger.log(Level.INFO, "First line written: " + skinLine + " in lionSynth.init");
						JOptionPane.showMessageDialog(null, "Skin set to NIMBUS. Restart needed to be applied.");
						ps.println(skinLine);
						ps.close();
					} catch(Exception e1) {
						System.err.println("Unknown error");
						e1.printStackTrace();
					}
					
				}
			});
			
			windowsSkinMI.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						PrintStream ps = new PrintStream(new FileOutputStream("lionSynth.init"));
						String skinLine = "skin=WINDOWS";
						logger.log(Level.INFO, "First line written: " + skinLine + " in lionSynth.init");
						JOptionPane.showMessageDialog(null, "Skin set to Windows. Restart needed to be applied.");
						ps.println(skinLine);
						ps.close();
					} catch(Exception e1) {
						System.err.println("Unknown error");
						e1.printStackTrace();
					}
					
				}
			});	
			
			halfLifeSkinMI.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						PrintStream ps = new PrintStream(new FileOutputStream("lionSynth.init"));
						String skinLine = "skin=MOTIF";
						logger.log(Level.INFO, "First line written: " + skinLine + " in lionSynth.init");
						JOptionPane.showMessageDialog(null, "Skin set to Motif. Restart needed to be applied.");
						ps.println(skinLine);
						ps.close();
					} catch(Exception e1) {
						System.err.println("Unknown error");
						e1.printStackTrace();
					}
					
				}
			});
			
		}
		
		// MISC
		private static void applySkin() {
			try {
				Scanner sc = new Scanner(new FileInputStream("lionSynth.init"));
				String skinLine = sc.nextLine();
				switch (skinLine) {
				case "skin=SWING": {
					UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
					logger.log(Level.INFO, "First line read: " + skinLine + " in lionSynth.init. Default skin applied.");
					break;
				}
				case "skin=MOTIF": {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
					logger.log(Level.INFO, "First line read: " + skinLine + " in lionSynth.ini. Motif skin applied.");
					break;
				}
				case "skin=NIMBUS": {
					UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
					System.out.println("Classic Macintosh skin applied");
					logger.log(Level.INFO, "First line read: " + skinLine + " in lionSynth.ini. Classic Machintosh skin applied");
					break;
				}
				case "skin=WINDOWS": {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					logger.log(Level.INFO, "First line read: " + skinLine + " in lionSynth.ini. Windows skin applied");
				}
				default:
					System.out.println("Not valid value in init file: " + skinLine);
				}
				
				sc.close();
				
			} catch (FileNotFoundException e) {
				logger.log(Level.FINE, "lionSynth.init file was deleted or does not still exist. Skin won't be applied.");
			} catch (Exception e) {
				System.err.println("Skin could not be applied.");
				logger.log(Level.WARNING, "Skin could not be applied.");
			}
		}
		
		public static void main(String[] args) {
			// TIME DIVISIONS
			//int bpm = 128; // BEATS PER MINUTE
			//double bps = bpm / 60; // BEATS PER SECOND
			//double eighthNoteDuration = bps / 8; // duraci�n de una corchea 
			//double quarterNoteDuration = bps / 4; // duraci�n de una corchea
			
			//double cutoffFrequency = 10000.00;
			
			applySkin();
			
			
			SwingUtilities.invokeLater(new Runnable() {

				
				@Override
				public void run() {
					new SynthWindow();
					
				}
			});
		}
}
