package com.drakkashi.p4;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.File;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class XMLLoader {
    
    private Document xml;

    public XMLLoader(String dir){
        try {
            xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(dir));
            xml.getDocumentElement().normalize();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println(e);
        }
    }
    
    public void toLibrary(SoundLibrary lib){
        System.out.println("Root element: " + xml.getDocumentElement().getNodeName());
        NodeList folders = xml.getElementsByTagName("FOLDER");
        NodeList sounds = xml.getElementsByTagName("SOUND");

        Node node = folders.item(0);
        System.out.println("Current Element: " + node.getNodeName());

        if (node.getNodeType() == Node.ELEMENT_NODE){
            Element folder = (Element) node;
            String dir = folder.getAttribute("dir");
            System.out.println("Directory: " + dir);

            for (int i = 0; i < sounds.getLength(); i++){
                Element sound = (Element)sounds.item(i);
                    
                String category = sound.getAttribute("category");

                if (category.equals("intro"))
                    lib.setIntro(dir + sound.getAttribute("file"));
                else if (category.equals("footstep"))
                    lib.newFootStep(dir + sound.getAttribute("file"));
                else
                    lib.newSound(
                            Integer.parseInt(sound.getAttribute("factor")),
                            dir + sound.getAttribute("file")
                        );
            }
        }
    }
}
