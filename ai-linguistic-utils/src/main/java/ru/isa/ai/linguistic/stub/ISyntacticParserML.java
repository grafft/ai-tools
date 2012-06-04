
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
 * Dispatch: ISyntacticParserML
 * Description: ????????? ??????????????? ??????? ?????? ?????? ?????? MultiLingual
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class ISyntacticParserML extends DispatchPtr {
	public static final GUID DIID = new GUID("{75e510e7-da0e-404c-862B-4297F52E9B8F}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, ISyntacticParserML.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * ISyntacticParserML (it is required by Jawin for some internal working though).
	 */
	public ISyntacticParserML() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the ISyntacticParserML interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public ISyntacticParserML(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the ISyntacticParserML interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public ISyntacticParserML(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the ISyntacticParserML interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the ISyntacticParserML interface on.
	 */
	public ISyntacticParserML(COMPtr comObject) throws COMException {
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
    
    * @param bstrText
    * @return void
    **/
    public void Text2Lex(String bstrText,Object[] 
        [] ppLexics) throws COMException
    {
      
		invokeN("Text2Lex", new Object[] {bstrText, ppLexics});
        
    }
    /**
    *
    
    * @param bstrText
    * @return void
    **/
    public void Text2Syn(String bstrText,Object[] 
        [] ppLexics,Object[] 
        [] ppSyntax) throws COMException
    {
      
		invokeN("Text2Syn", new Object[] {bstrText, ppLexics, ppSyntax});
        
    }
    /**
    *
    
    * @return void
    **/
    public void Lex2Syn(Object[] pLexics,Object[] 
        [] ppSyntax) throws COMException
    {
      
		invokeN("Lex2Syn", new Object[] {pLexics, ppSyntax});
        
    }
    /**
    *
    
    * @param bstrText
    * @param _lang
    * @return void
    **/
    public void Text2SynML(String bstrText,Object[] 
        [] ppLexics,Object[] 
        [] ppSyntax,int _lang) throws COMException
    {
      
		invokeN("Text2SynML", new Object[] {bstrText, ppLexics, ppSyntax, new Integer(_lang)});
        
    }
}
