/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kino.server.structs;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.kino.client.broadcast.NewSendW;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author kirio
 */
public class CheckNewProjectHelper {
    static public class ProjectInitException extends Exception implements IsSerializable{

        public ProjectInitException(String message) {
            super(message);
        }
        
    }
    static public boolean check(NewSendW.NewProject project,StringBuilder filename_hash_mismatch) throws ProjectInitException{
    
        File projectDir = new File(project.project_dir); 
        //String extractProjectName = extractProjectName(projectDir);
        //System.out.println("project name: '"+extractProjectName+"'");
        
        HashMap<String, String> extractFilesAndHashes = extractFilesAndHashes(projectDir);
        for( String filename : extractFilesAndHashes.keySet()){
            if(!new File(project.project_dir,filename).exists())
                throw new ProjectInitException("Файл указанный в PKL не найден :"+filename);
        }
        
        for(Map.Entry<String, String> entrySet : extractFilesAndHashes.entrySet()) {
                String filename = entrySet.getKey();
                String hash = entrySet.getValue();
                File file = new File(projectDir,filename);
                try {
                    System.out.println("begin check file: '"+filename+"'");
                    boolean checkHash = checkHash(file, hash);
                    if(!checkHash){
                        filename_hash_mismatch.append(filename);
                        return false;
                    }
                    System.out.println("hash summ equals: '"+checkHash+"'");
                }
                catch(IOException ex){
                    System.out.println("ERROR Ошибка чтения файла "+ex.getLocalizedMessage());
                    throw new ProjectInitException("Ошибка чтения файла "+file+":"+ex.getLocalizedMessage());
                }
                
         }
         return true; 
    
    }
    
    
    
       static File[] getFilesWithExtention(File dir,final String ext){
        return dir.listFiles(new FilenameFilter() 
        {
                 @Override
                 public boolean accept(File dir, String name) {
                     if(ext==null || ext.isEmpty())
                         return true;
                     return name.endsWith(ext);
                 }
        });
         }
        static File[] getFilesWithName(File dir,final String fname){
        return dir.listFiles(new FilenameFilter() 
        {
                 @Override
                 public boolean accept(File dir, String name) {
                     if(name==null || name.isEmpty())
                         return false;
                     return name.equals(fname);
                 }
        });
        
        
        
    }
      static File[] getFilesWithBaseName(File dir,final String base,final File except  ){
      return dir.listFiles(new FileFilter() 
      {
 
          @Override
          public boolean accept(File pathname) {
                return !pathname.equals(except) && (pathname.getName().equals(base) || pathname.getName().startsWith(base+"."));
          }
              
      });
    }
   static public String extractProjectName(File projectDir) throws ProjectInitException{
        // TODO: transform into SAXparser, because we need only one element;
         File[] pkl_cpl = findFiles_pkl_cpl(projectDir);
        if(pkl_cpl[0]==null)
            throw new ProjectInitException("PKL файл не найден в директории "+projectDir);

        File pklfile = pkl_cpl[0];     
  

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(pklfile);
            doc.getDocumentElement().normalize();
            NodeList elementsByTagName = doc.getElementsByTagName("AnnotationText");
            if(elementsByTagName.getLength()==0)
            {
                throw new ProjectInitException("AnnotationText таг не найден в pkl файле "+pklfile);
            }
            return elementsByTagName.item(0).getTextContent().trim();
	 
        }
        catch(ParserConfigurationException e){
            throw new ProjectInitException(e.getLocalizedMessage());
        }
        catch(IOException e){
            throw new ProjectInitException("Ошибка чтения файла "+pklfile+":"+e.getLocalizedMessage());
        }
        catch(SAXException e){
            throw new ProjectInitException("Ошибка чтения файла "+pklfile+":"+e.getLocalizedMessage());
        }
       
    
    }
    public static Element  getDirectChild(Element parent, String  tagnames)
    {
      
        for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling())
        {
           if(child.getNodeType() == Node.ELEMENT_NODE && tagnames.equals(child.getNodeName())) 
                return (Element) child;
        }
        return null;
    }
 
     public static ArrayList<Element>  getDirectChilds(Element parent, String ... tagnames)
    {
        List<String> tagList = Arrays.asList(tagnames);
        ArrayList<Element> result= new ArrayList<>(tagList.size());
        for(Node child = parent.getFirstChild(); child != null; child = child.getNextSibling())
        {
            
           if(child.getNodeType() == Node.ELEMENT_NODE && tagList.contains(child.getNodeName())) 
               result.add((Element) child);
                 
        }
        return result;
    }
    
   static boolean checkHash(File file,String hash) throws FileNotFoundException, IOException {
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(file)))
        {
            byte[] md51 = org.apache.commons.codec.digest.DigestUtils.sha1(is);
            String hash_of_file = org.apache.commons.codec.binary.Base64.encodeBase64String(md51);
            return hash.equals(hash_of_file);
        }
    }
  
    static HashMap<String, String> extractFilesAndHashes(File projectDir) throws ProjectInitException{
       
        
        File[] pkl_cpl = findFiles_pkl_cpl(projectDir);
        if(pkl_cpl[0]==null)
            throw new ProjectInitException("pkl файл не найден в директории "+projectDir);

        File pklFile = pkl_cpl[0];     

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(pklFile);
            doc.getDocumentElement().normalize();
            
            
            NodeList AssetTagList = doc.getElementsByTagName("Asset");
            if(AssetTagList.getLength()==0){
                throw new ProjectInitException("Теги Asset не найден в pkl файле "+pklFile);
            }
            
            HashMap<String, String> fileAndHashMap = new HashMap<>(AssetTagList.getLength());
            for(int i=0;i<AssetTagList.getLength();i++)
            {
                Node item = AssetTagList.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) 
                {
             
                    Element el = (Element)item;
                    Element filenameEl = getDirectChild(el, "OriginalFileName");
                    
                    Element HashEl = getDirectChild(el, "Hash");
                    
                    Element anatationEl = getDirectChild(el, "AnnotationText");
                    if(filenameEl==null && anatationEl==null)
                            throw new ProjectInitException("Тег OriginalFileName иди AnnotationText не найден в pkl файле "+pklFile);
                            
                    if(HashEl==null)
                        throw new ProjectInitException("Тег Hash не найден  в pkl файле "+pklFile);
                    String filename;
                    if(filenameEl!=null){
                        filename = filenameEl.getTextContent();
                    }
                    else {
                        File[] filesWithBaseName = getFilesWithBaseName(projectDir, anatationEl.getTextContent(),pklFile);
                        if(filesWithBaseName.length==0)
                            throw new ProjectInitException("Не найден файл для с именем "+anatationEl.getTextContent()+".*");
                        if(filesWithBaseName.length!=1)
                            throw new ProjectInitException("Найдено несколько файлов с именем "+anatationEl.getTextContent()+".*");
                        filename = filesWithBaseName[0].getName();
                    }
             
                      fileAndHashMap.put(filename, HashEl.getTextContent());
                    
//                        for(Element firstChildEl:  getDirectChilds((Element) item, "MainPicture","MainSound"))
//                        {
//                            Element AnnotationTextEl = getDirectChild(firstChildEl, "AnnotationText");
//                            Element HashEl = getDirectChild(firstChildEl, "Hash");
//                            if(AnnotationTextEl==null)
//                                throw new ProjectInitException("Тег AnnotationText не найден в cpl файле "+pklFile);
//                            if(HashEl==null)
//                                throw new ProjectInitException("Тег Hash не найден  в cpl файле "+pklFile);
//                            fileAndHashMap.put(AnnotationTextEl.getTextContent(), HashEl.getTextContent());
//                        
//                        }
                 
                }
            }
            return fileAndHashMap;
	 
        }
        catch(ParserConfigurationException e){
            throw new ProjectInitException(e.getLocalizedMessage());
        }
        catch(IOException e){
            throw new ProjectInitException("Ошибка чтения файла "+pklFile+":"+e.getLocalizedMessage());
        }
        catch(SAXException e){
            throw new ProjectInitException("Ошибка чтения файла "+pklFile+":"+e.getLocalizedMessage());
        }
        
    
    
    }
    
   public  static void validateProject(String filename) throws ProjectInitException{
        File dir = new File(filename);
        File[] pkl_cpl = findFiles_pkl_cpl(dir);
        if(pkl_cpl[0]==null)
            throw new ProjectInitException("pkl файл не найден");
        if(pkl_cpl[1]==null)
            throw new ProjectInitException("cpl файл не найден");
        if(!new File(dir,"ASSETMAP").exists())
            throw new ProjectInitException("файл ASSETMAP не найден");
         if(!new File(dir,"VOLINDEX").exists())
            throw new ProjectInitException("файл VOLINDEX не найден");
      
 
    };
   
   
   static File[] findFiles_pkl_cpl(File parent) throws CheckNewProjectHelper.ProjectInitException{
        File pkl = null;
        File cpl = null;
 
        for(File file:CheckNewProjectHelper.getFilesWithExtention(parent, "xml"))
        {
            
             try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(file);
                doc.getDocumentElement().normalize();
                Element root = doc.getDocumentElement();
                 String tagName = root.getTagName();
                if(pkl==null && "PackingList".equals(tagName))
                     pkl = file;
                else if(cpl==null && "CompositionPlayList".equalsIgnoreCase(tagName))
                     cpl = file;
                 if(cpl!=null && pkl!=null)
                 break;
 
             }
        catch(ParserConfigurationException e){
            throw new CheckNewProjectHelper.ProjectInitException(e.getLocalizedMessage());
        }
        catch(IOException e){
            throw new CheckNewProjectHelper.ProjectInitException("Ошибка чтения файла:"+e.getLocalizedMessage());
        }
        catch(SAXException e){
            throw new CheckNewProjectHelper.ProjectInitException("Ошибка чтения файла "+file.getAbsolutePath()+":"+e.getLocalizedMessage());
        }
        }
        return new File[]{pkl,cpl};
        
    }
}
