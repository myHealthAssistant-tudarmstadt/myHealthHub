/* 
 * Copyright (C) 2014 TU Darmstadt, Hessen, Germany.
 * Department of Computer Science Databases and Distributed Systems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 
 
 /**
 * 
 */
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

/**
 * @author Christian Seeger
 *
 */
public class XMLParser {

	// for debugging
	private static boolean D = false;
	private static String TAG = "XMLParser"; 
	
	public Document getDomElementFromFile(String filename) {
		return getDomElement(readFile(filename));
	}
	
	public Document getDomElement(String xml){
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(xml));
	        doc = db.parse(is); 

	        if(D)Log.d(TAG, "File was parsed successfully.");
	        
			} catch (ParserConfigurationException e) {
				Log.e("ParserConfigurationException Error: ", e.getMessage());
				return null;
			} catch (SAXException e) {
				Log.e("SAXException Error: ", e.getMessage());
	            return null;
			} catch (IOException e) {
				Log.e("IOException Error: ", e.getMessage());
				return null;
			}
                // return DOM
	        return doc;
	}
	
	private String readFile(String filename) {
		StringBuilder filecontent = new StringBuilder();
		File file = new File(filename);
		
		try {
    	    BufferedReader br = new BufferedReader(new FileReader(file));
    	    
    	    if(D)Log.d(TAG, "StartProducer reading file: "+file.toString()+"...");

    	    String line;
            String lineFeed = System.getProperty("line.separator");
    	    while ((line = br.readLine()) != null) {
    	    	filecontent.append(line).append(lineFeed);
    	    }
    	    
    	    if(D)Log.d(TAG, "Reading of file "+file.toString()+" is finished");
    	}
    	catch (IOException e) {
    		Log.e(TAG, "Unable to read file: "+file.toString());
    	}   	
		
		return filecontent.toString();
	}
	
	public String getValue(Element item, String str) {
		NodeList n = item.getElementsByTagName(str);
		return this.getElementValue(n.item(0));
	}

	public final String getElementValue( Node elem ) {
		     Node child;
		     if( elem != null){
		         if (elem.hasChildNodes()){
		             for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
		                 if( child.getNodeType() == Node.TEXT_NODE  ){
		                     return child.getNodeValue();
		                 }
		             }
		         }
		     }
		     return "";
	}
	
	
	
}