
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
 * Dispatch: ISemanteme
 * Description: ????????? ????????????? ?????
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class ISemanteme extends DispatchPtr {
	public static final GUID DIID = new GUID("{95ce1e45-a271-458e-9C5F-6AA0C1D13FBC}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, ISemanteme.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * ISemanteme (it is required by Jawin for some internal working though).
	 */
	public ISemanteme() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the ISemanteme interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public ISemanteme(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the ISemanteme interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public ISemanteme(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the ISemanteme interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the ISemanteme interface on.
	 */
	public ISemanteme(COMPtr comObject) throws COMException {
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
    * @return int
    **/
    public int getType() throws COMException
    {
        return ((Integer)get("Type")).intValue();
    }
        
    /**
    *
    * @return String
    **/
    public String getName() throws COMException
    {
         return (String)get("Name");
    }
        
    /**
    *
    * @return void
    **/
    public IIterator getFirstSyntaxeme() throws COMException
    {
        IIterator res = new IIterator();
          DispatchPtr dispPtr = (DispatchPtr)get("FirstSyntaxeme");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return void
    **/
    public IIterator getSecondSyntaxeme() throws COMException
    {
        IIterator res = new IIterator();
          DispatchPtr dispPtr = (DispatchPtr)get("SecondSyntaxeme");
          res.stealUnknown(dispPtr);
          return res;
    }
        
}
