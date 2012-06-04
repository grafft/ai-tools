
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
 * Dispatch: ILexeme
 * Description: ????????? ???????
 * Help file:   
 *
 * @author JawinTypeBrowser
 */

public class ILexeme extends DispatchPtr {
	public static final GUID DIID = new GUID("{1940a41f-fece-4951-9187-9F45524DC8C1}");
	public static final int IID_TOKEN;
	static {
		IID_TOKEN = IdentityManager.registerProxy(DIID, ILexeme.class);
	}

	/**
	 * The required public no arg constructor.
	 * <br><br>
	 * <b>Important:</b>Should never be used as this creates an uninitialized
	 * ILexeme (it is required by Jawin for some internal working though).
	 */
	public ILexeme() {
		super();
	}

	/**
	 * For creating a new COM-object with the given progid and with 
	 * the ILexeme interface.
	 * 
	 * @param progid the progid of the COM-object to create.
	 */
	public ILexeme(String progid) throws COMException {
		super(progid, DIID);
	}

	/**
	 * For creating a new COM-object with the given clsid and with 
	 * the ILexeme interface.
	 * 
	 * @param clsid the GUID of the COM-object to create.
	 */
	public ILexeme(GUID clsid) throws COMException {
		super(clsid, DIID);
	}

	/**
	 * For getting the ILexeme interface on an existing COM-object.
	 * This is an alternative to calling {@link #queryInterface(Class)}
	 * on comObject.
	 * 
	 * @param comObject the COM-object to get the ILexeme interface on.
	 */
	public ILexeme(COMPtr comObject) throws COMException {
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
    public String getDictForm() throws COMException
    {
         return (String)get("DictForm");
    }
        
    /**
    *
    * @return int
    **/
    public int getFullType() throws COMException
    {
        return ((Integer)get("FullType")).intValue();
    }
        
    /**
    *
    * @return int
    **/
    public int getGramData() throws COMException
    {
        return ((Integer)get("GramData")).intValue();
    }
        
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
    * @return int
    **/
    public int getCases() throws COMException
    {
        return ((Integer)get("Cases")).intValue();
    }
        
    /**
    *
    * @return int
    **/
    public int getGenders() throws COMException
    {
        return ((Integer)get("Genders")).intValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getSingular() throws COMException
    {
        return ((Boolean)get("Singular")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getPlural() throws COMException
    {
        return ((Boolean)get("Plural")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getNoCase() throws COMException
    {
        return ((Boolean)get("NoCase")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getImCase() throws COMException
    {
        return ((Boolean)get("ImCase")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getRodCase() throws COMException
    {
        return ((Boolean)get("RodCase")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getDatCase() throws COMException
    {
        return ((Boolean)get("DatCase")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getVinCase() throws COMException
    {
        return ((Boolean)get("VinCase")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getTvorCase() throws COMException
    {
        return ((Boolean)get("TvorCase")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getPredCase() throws COMException
    {
        return ((Boolean)get("PredCase")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getRod2Case() throws COMException
    {
        return ((Boolean)get("Rod2Case")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getPred2Case() throws COMException
    {
        return ((Boolean)get("Pred2Case")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getMale() throws COMException
    {
        return ((Boolean)get("Male")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getFemale() throws COMException
    {
        return ((Boolean)get("Female")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getNeuter() throws COMException
    {
        return ((Boolean)get("Neuter")).booleanValue();
    }
        
    /**
    *
    * @return int
    **/
    public int getTypeGen() throws COMException
    {
        return ((Integer)get("TypeGen")).intValue();
    }
        
    /**
    *
    * @return int
    **/
    public int getID() throws COMException
    {
        return ((Integer)get("ID")).intValue();
    }
        
    /**
    *
    * @return void
    **/
    public ILexWord getWord() throws COMException
    {
        ILexWord res = new ILexWord();
          DispatchPtr dispPtr = (DispatchPtr)get("Word");
          res.stealUnknown(dispPtr);
          return res;
    }
        
    /**
    *
    * @return int
    **/
    public int getTense() throws COMException
    {
        return ((Integer)get("Tense")).intValue();
    }
        
    /**
    *
    * @return int
    **/
    public int getForm() throws COMException
    {
        return ((Integer)get("Form")).intValue();
    }
        
    /**
    *
    * @return int
    **/
    public int getPerson() throws COMException
    {
        return ((Integer)get("Person")).intValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getShort() throws COMException
    {
        return ((Boolean)get("Short")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getReflexive() throws COMException
    {
        return ((Boolean)get("Reflexive")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getComparative() throws COMException
    {
        return ((Boolean)get("Comparative")).booleanValue();
    }
        
    /**
    *
    * @return boolean
    **/
    public boolean getAnimate() throws COMException
    {
        return ((Boolean)get("Animate")).booleanValue();
    }
        
    /**
    *
    * @return int
    **/
    public int getSemClass() throws COMException
    {
        return ((Integer)get("SemClass")).intValue();
    }
        
}
