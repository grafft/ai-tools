
package ru.isa.ai.linguistic.stub;

import org.jawin.*;
import org.jawin.constants.*;
import org.jawin.marshal.*;
import org.jawin.io.*;
import java.io.*;
import java.util.Date;

/**
 * Jawin generated code please do not edit
 *
 * Dispatch: ISynWord
 * Description: ????????? ????? (?????????)
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class ISynWord extends DispatchPtr {
	public static final GUID DIID = new GUID("{9a8477fe-e9ab-4880-AD2A-CE5E341039F1}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, ISynWord.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * ISynWord (it is required by Jawin for some internal working though).
	 */
	public ISynWord() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the ISynWord interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public ISynWord(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the ISynWord interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public ISynWord(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the ISynWord interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the ISynWord interface on.
	 */
	public ISynWord(COMPtr comObject) throws COMException {
		super(comObject);
	}

	public int getIIDToken() {
		return IID_TOKEN;
	}
	
	
    /**
    *
    
    * @return void
    **/
    /*public void QueryInterface(Object[] riid,void[] 
        [] ppvObj) throws COMException
    {
      
		invokeN("QueryInterface", new Object[] {riid, ppvObj});
        
    }*/
    /**
    *
    
    * @return int
    **/
    /*public int AddRef() throws COMException
    {
      
		return ((Integer)invokeN("AddRef", new Object[] {})).intValue();
        
    }*/
    /**
    *
    
    * @return int
    **/
    /*public int Release() throws COMException
    {
      
		return ((Integer)invokeN("Release", new Object[] {})).intValue();
        
    }*/
    /**
    *
    
    * @return void
    **/
    /*public void GetTypeInfoCount(int[] pctinfo) throws COMException
    {
      
		invokeN("GetTypeInfoCount", new Object[] {pctinfo});
        
    }*/
    /**
    *
    
    * @param itinfo
    * @param lcid
    * @return void
    **/
    /*public void GetTypeInfo(int itinfo,int lcid,void[] 
        [] pptinfo) throws COMException
    {
      
		invokeN("GetTypeInfo", new Object[] {new Integer(itinfo), new Integer(lcid), pptinfo});
        
    }*/
    /**
    *
    
    * @param cNames
    * @param lcid
    * @return void
    **/
    /*public void GetIDsOfNames(Object[] riid,int[] 
        [] rgszNames,int cNames,int lcid,int[] rgdispid) throws COMException
    {
      
		invokeN("GetIDsOfNames", new Object[] {riid, rgszNames, new Integer(cNames), new Integer(lcid), rgdispid});
        
    }*/
    /**
    *
    
    * @param dispidMember
    * @param lcid
    * @param wFlags
    * @return void
    **/
    /*public void Invoke(int dispidMember,Object[] riid,int lcid,short wFlags,Object[] pdispparams,Variant[] pvarResult,Object[] pexcepinfo,int[] puArgErr) throws COMException
    {
      
		invokeN("Invoke", new Object[] {new Integer(dispidMember), riid, new Integer(lcid), new Short(wFlags), pdispparams, pvarResult, pexcepinfo, puArgErr});
        
    }*/
    /**
    *
    * @return String
    **/
    public String getString() throws COMException
    {
         return (String)get("String");
    }
        
    /**
    *
    * @return int
    **/
    public int getOffset() throws COMException
    {
        return ((Integer)get("Offset")).intValue();
    }
        
    /**
    *
    * @return int
    **/
    public int getLength() throws COMException
    {
        return ((Integer)get("Length")).intValue();
    }
        
    /**
    *
    * @return String
    **/
    public String getSeparator() throws COMException
    {
         return (String)get("Separator");
    }
        
    /**
    *
    * @return String
    **/
    public String getWordGroup() throws COMException
    {
         return (String)get("WordGroup");
    }
        
    /**
    *
    * @return void
    **/
    public ILexeme getLexeme() throws COMException
    {
        ILexeme res = new ILexeme();
          DispatchPtr dispPtr = (DispatchPtr)get("Lexeme");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ISyntaxeme getSyntaxeme() throws COMException
    {
        ISyntaxeme res = new ISyntaxeme();
          DispatchPtr dispPtr = (DispatchPtr)get("Syntaxeme");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ISynWord getParent() throws COMException
    {
        ISynWord res = new ISynWord();
          DispatchPtr dispPtr = (DispatchPtr)get("Parent");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return int
    **/
    public int getSize() throws COMException
    {
        return ((Integer)get("Size")).intValue();
    }
        
    /**
    *
    * @return void
    **/
    public ISynWordIterator getBegin() throws COMException
    {
        ISynWordIterator res = new ISynWordIterator();
          DispatchPtr dispPtr = (DispatchPtr)get("Begin");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ISynWordIterator getEnd() throws COMException
    {
        ISynWordIterator res = new ISynWordIterator();
          DispatchPtr dispPtr = (DispatchPtr)get("End");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ISynWord getPrevHomo() throws COMException
    {
        ISynWord res = new ISynWord();
          DispatchPtr dispPtr = (DispatchPtr)get("PrevHomo");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ISynWord getNextHomo() throws COMException
    {
        ISynWord res = new ISynWord();
          DispatchPtr dispPtr = (DispatchPtr)get("NextHomo");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ISentenceVariant getSentVar() throws COMException
    {
        ISentenceVariant res = new ISentenceVariant();
          DispatchPtr dispPtr = (DispatchPtr)get("SentVar");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return String
    **/
    public String getCanonicalGroup() throws COMException
    {
         return (String)get("CanonicalGroup");
    }
        
    /**
    *
    * @return int
    **/
    public int getWordCount() throws COMException
    {
        return ((Integer)get("WordCount")).intValue();
    }
        
    /**
    *
    
    * @param bstrSynonyms
    * @param bForMainWord
    * @return boolean
    **/
    public boolean AddSynonyms(String bstrSynonyms,int bForMainWord) throws COMException
    {
      
		return ((Boolean)invokeN("AddSynonyms", new Object[] {bstrSynonyms, new Integer(bForMainWord)})).booleanValue();
        
    }
    /**
    *
    * @return void
    **/
    public ISynonymList getSynonyms() throws COMException
    {
        ISynonymList res = new ISynonymList();
          DispatchPtr dispPtr = (DispatchPtr)get("Synonyms");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getIsQuElement() throws COMException
    {
        return ((Boolean)get("IsQuElement")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getIsAnaphor() throws COMException
    {
        return ((Boolean)get("IsAnaphor")).booleanValue();
    }
        
}
