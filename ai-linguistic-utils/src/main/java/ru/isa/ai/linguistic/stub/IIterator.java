
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
 * Dispatch: IIterator
 * Description: ????????? ????????? ?? ?????? ??????????
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class IIterator extends DispatchPtr {
	public static final GUID DIID = new GUID("{5aa402c4-9cc5-4745-AD42-F870A46F5622}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, IIterator.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * IIterator (it is required by Jawin for some internal working though).
	 */
	public IIterator() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the IIterator interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public IIterator(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the IIterator interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public IIterator(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the IIterator interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the IIterator interface on.
	 */
	public IIterator(COMPtr comObject) throws COMException {
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
    * @return void
    **/
    public ISynSentenceIterator getSentenceIterator() throws COMException
    {
        ISynSentenceIterator res = new ISynSentenceIterator();
          DispatchPtr dispPtr = (DispatchPtr)get("SentenceIterator");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ISentenceVariantIterator getSentenceVariantIterator() throws COMException
    {
        ISentenceVariantIterator res = new ISentenceVariantIterator();
          DispatchPtr dispPtr = (DispatchPtr)get("SentenceVariantIterator");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public ISynWordIterator getWordIterator() throws COMException
    {
        ISynWordIterator res = new ISynWordIterator();
          DispatchPtr dispPtr = (DispatchPtr)get("WordIterator");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    
    * @return boolean
    **/
    public boolean NextSentence() throws COMException
    {
      
		return ((Boolean)invokeN("NextSentence", new Object[] {})).booleanValue();
        
    }
    /**
    *
    
    * @return boolean
    **/
    public boolean PrevSentence() throws COMException
    {
      
		return ((Boolean)invokeN("PrevSentence", new Object[] {})).booleanValue();
        
    }
    /**
    *
    
    * @return boolean
    **/
    public boolean NextSentenceVariant() throws COMException
    {
      
		return ((Boolean)invokeN("NextSentenceVariant", new Object[] {})).booleanValue();
        
    }
    /**
    *
    
    * @return boolean
    **/
    public boolean PrevSentenceVariant() throws COMException
    {
      
		return ((Boolean)invokeN("PrevSentenceVariant", new Object[] {})).booleanValue();
        
    }
    /**
    *
    
    * @return boolean
    **/
    public boolean NextWord() throws COMException
    {
      
		return ((Boolean)invokeN("NextWord", new Object[] {})).booleanValue();
        
    }
    /**
    *
    
    * @return boolean
    **/
    public boolean PrevWord() throws COMException
    {
      
		return ((Boolean)invokeN("PrevWord", new Object[] {})).booleanValue();
        
    }
}
