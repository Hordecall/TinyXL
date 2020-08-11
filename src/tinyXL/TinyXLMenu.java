package tinyXL;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Classe gérant le menu de l'application.
 * @author hero
 *
 */
public class TinyXLMenu implements ActionListener{
	
	private JMenuBar menu;
	private JMenu submenu, editmenu;
	private JMenuItem editmode, valuemode, fileImport, fileSave, copy, paste;
	private String mode;
	private TinyXLCore context;
	private File fileToImport;
	private File fileToSave;
	
	TinyXLMenu(TinyXLCore context)
	{
			this.context = context;
			
			menu = new JMenuBar();
			
			submenu = new JMenu("XLApp");
			editmenu = new JMenu("Edition");
			
			editmode = new JMenuItem("Mode Edition");
			valuemode = new JMenuItem("Mode Valeurs");
			fileImport = new JMenuItem("Importer ...");
			fileSave = new JMenuItem("Sauvegarder ...");
			copy = new JMenuItem("Copier");
			paste = new JMenuItem("Coller"); paste.setEnabled(false); // est activé une fois qu'une cellule a été copiée
			
			editmode.addActionListener(this);
			valuemode.addActionListener(this);
			fileImport.addActionListener(this);
			fileSave.addActionListener(this);
			copy.addActionListener(this);
			paste.addActionListener(this);
			
			submenu.add(editmode);
			submenu.add(valuemode);
			submenu.add(fileImport);
			submenu.add(fileSave);
			
			editmenu.add(copy);
			editmenu.add(paste);
			
			menu.add(submenu);
			menu.add(editmenu);
			
			mode = "edit";			
	}

	public JMenuBar getMenuBar() {
		return menu;
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		JMenuItem source = (JMenuItem)(e.getSource());
		System.out.println(source);
		System.out.println(source.getText());
		
		if (source.getText().contains("Mode Edition"))
		{
			mode="edit";
			context.setMode(mode);
			editmenu.setEnabled(true);

			System.out.println("Switching to Edit Mode ...");		
		}
		else if (source.getText().contains("Mode Valeurs"))
		{
			mode="value";
			context.setMode(mode);
			editmenu.setEnabled(false);
			System.out.println("Switching to Value Mode ...");
		}
		else if (source.getText().contains("Importer"))
		{
			System.out.println("Launching import ...");		
			 JFileChooser chooser = new JFileChooser();
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "Simple or rich text file", "txt", "rtf");
			    chooser.setFileFilter(filter);
			    int returnVal = chooser.showOpenDialog(this.context.getWindow());
			    
			    if(returnVal == JFileChooser.APPROVE_OPTION) 
			    {
			       System.out.println("You chose to open this file: " +
			            chooser.getSelectedFile().getName());
			       fileToImport = chooser.getSelectedFile();
			       try {
					context.parseFile(fileToImport);
			       } catch (IOException e1) {
					e1.printStackTrace();
			       } catch (TerribleMistake e1) {
					e1.printStackTrace();
				}
			    }
		}
		else if (source.getText().contains("Sauvegarder"))
		{
			String save = "";
			String br = System.getProperty("line.separator");		
			context.setMode("edit");
			
			for (int i = 1; i < context.getLines().length; i++)
			{
				for (int j = 1; j < context.getLineAt(i).getCells().length; j++)
				{
					String cellContent = "";
					if (!context.getLineAt(i).getCellAt(j).getCell().getText().isEmpty())
					{
						cellContent = context.getLineAt(i).getCellAt(j).getCell().getText();
					}
					
					cellContent += "&";
					save += cellContent;
				}
				save += br;
			}
			
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showSaveDialog(this.context.getWindow());
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				try {
		            FileWriter fw = new FileWriter(chooser.getSelectedFile()+".txt");
		            fw.write(save.toString());
		            fw.close();
		        } catch (Exception ex) {
		            ex.printStackTrace();
		        }
			}
		}
		else if (source.getText().contains("Copier"))
		{
			for (int i = 0; i < context.getLines().length; i++)
			{
				for (int j = 0; j < context.getLineAt(i).getCells().length; j++ )
				{
					if (context.getLineAt(i).getCellAt(j).isFocused())
					{
						Cell selection = context.getLineAt(i).getCellAt(j);
						selection.copy(selection);
						paste.setEnabled(true);
					}
				}
			}
		}
		else if (source.getText().contains("Coller"))
		{
			for (int i = 0; i < context.getLines().length; i++)
			{
				for (int j = 0; j < context.getLineAt(i).getCells().length; j++ )
				{
					if (context.getLineAt(i).getCellAt(j).isFocused())
					{
						Cell selection = context.getLineAt(i).getCellAt(j);
						if (!CopyPaster.clipboard.isEmpty())
							selection.paste();
					}
				}
			}
		}
	
	}
	
	/**
	 * Retourne le mode actuel ("edit" ou "value").
	 * @return le mode en String
	 */
	public String getMode(){	
		return mode;
	}

	/**
	 * Change le mode entre le mode "édition" et le mode "valeurs" (les formules et les résultats).
	 * @param command
	 */
	public void setMode(String command) {
		this.mode = command;
	}
	
	
}
