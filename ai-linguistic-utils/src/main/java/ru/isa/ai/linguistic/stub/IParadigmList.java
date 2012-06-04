
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
 * Dispatch: IParadigmList
 * Description: ??????????? (?????? ????????)
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class IParadigmList extends DispatchPtr {
	public static final GUID DIID = new GUID("{27278eea-d5af-42a7-9F97-262492AF640F}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, IParadigmList.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * IParadigmList (it is required by Jawin for some internal working though).
	 */
	public IParadigmList() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the IParadigmList interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public IParadigmList(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the IParadigmList interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public IParadigmList(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the IParadigmList interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the IParadigmList interface on.
	 */
	public IParadigmList(COMPtr comObject) throws COMException {
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
    
    * @param Source
    * @return void
    **/
    public void CreateParadigms(String Source) throws COMException
    {
      
		invokeN("CreateParadigms", new Object[] {Source});
        
    }
    /**
    *
    * @return void
    **/
    public IParadigm getParadigms(int Index) throws COMException
    {
        IParadigm res = new IParadigm();
          DispatchPtr dispPtr = (DispatchPtr)get("Paradigms", new Integer(Index));
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return int
    **/
    public int getCount() throws COMException
    {
        return ((Integer)get("Count")).intValue();
    }
        
    /**
    *
    
    * @param bstrText
    * @param eTypeGen
    * @param lGramData
    * @return String
    **/
    public String SynthesizeForm(String bstrText,int eTypeGen,int lGramData) throws COMException
    {
      
		return (String)invokeN("SynthesizeForm", new Object[] {bstrText, new Integer(eTypeGen), new Integer(lGramData)});
        
    }
    /**
    *
    
    * @return void
    **/
    public void Terminate() throws COMException
    {
      
		invokeN("Terminate", new Object[] {});
        
    }
}
