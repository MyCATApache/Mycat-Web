/*##########################################################################
_##
_##  $Id$
_##
_##########################################################################*/

package jrds.snmp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.*;
import org.snmp4j.PDU;
import org.snmp4j.asn1.BER;
import org.snmp4j.asn1.BERInputStream;
import org.snmp4j.smi.AbstractVariable;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Opaque;
import org.snmp4j.smi.SMIConstants;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;



/**
 * A extension to a an HashMap, it's main purpose is to be constructed from an snmp pdu and
 * return values as java base objects
 * the key to access a value is it's OID
 * It supports float and double stored in opaque value
 *
 *  @author Fabrice Bacchella
 */
public class SnmpVars extends HashMap<OID, Object> {
	static final private Logger logger = LogManager.getLogger(SnmpVars.class);

	static final private byte TAG1 = (byte) 0x9f;
	static final private byte TAG_FLOAT = (byte) 0x78;
	static final private byte TAG_DOUBLE = (byte) 0x79;

	private final Map<OID, Integer> errors = new HashMap<OID, Integer>(0);

	public SnmpVars(PDU data) {
		super(data.size());
		join(data);
	}

	public SnmpVars(VariableBinding[] newVars) {
		super(newVars.length);
		join(newVars);
	}

	/**
	 *
	 */
	public SnmpVars() {
		super();
	}

	public SnmpVars(int initialCapacity) {
		super(initialCapacity);
	}

	/** Add directly a VariableBinding to the map
	 * it will be stored as a key/value and the original snmp datas will be lost
	 * only not 
	 * @param vb
	 */
	public boolean addVariable(VariableBinding vb)
	{
		boolean retValue = false;
		if(vb == null) {
			logger.error("null variable to add ?");
		}
		else if( ! vb.isException()) {
			OID vbOid = vb.getOid();
			put(vbOid, convertVar(vb.getVariable()));
			retValue = true;
		}
		else {
			errors.put(vb.getOid(), vb.getSyntax());
			int exception = vb.getSyntax();
			String exceptionName = "";
			switch(exception) {
			case SMIConstants.EXCEPTION_END_OF_MIB_VIEW:
				exceptionName = "End of mib view"; break;
			case SMIConstants.EXCEPTION_NO_SUCH_INSTANCE:            
				exceptionName = "No such instance"; break;
			case SMIConstants.EXCEPTION_NO_SUCH_OBJECT: 
				exceptionName = "No such object"; break;
			default: exceptionName = "Unknown exception";break;
			}
			logger.trace("Exception " +  exceptionName + " for " + vb.getOid());
		}
		return retValue;
	}

	public boolean isError(OID tocheck) {
		return errors.containsKey(tocheck);
	}

	public Map<OID, Integer> getErrors() {
		return errors;
	}

	public void join(PDU data)
	{
		for(int i = 0 ; i < data.size() ; i++) {
			VariableBinding vb = data.get(i);
			addVariable(vb);
		}
	}

	public void join(VariableBinding[] newVars)
	{
	    if(newVars == null)
	        return;
		for (int i = 0 ; i < newVars.length ; i++) {
			addVariable(newVars[i])	;
		}
	}

	private Object convertVar(Variable valueAsVar) {
		Object retvalue = null;
		if (valueAsVar != null) {
			int type = valueAsVar.getSyntax();
			if( valueAsVar instanceof OID) {
				retvalue = valueAsVar;
			}
			else if(valueAsVar instanceof UnsignedInteger32) {
				if(valueAsVar instanceof TimeTicks) {
					long epochcentisecond = valueAsVar.toLong();
					retvalue  = new Double(epochcentisecond / 100.0 );
				}
				else
					retvalue  = valueAsVar.toLong();
			}
			else if(valueAsVar instanceof Integer32)
				retvalue  = valueAsVar.toInt();
			else if(valueAsVar instanceof Counter64)
				retvalue  = valueAsVar.toLong();
			else if(valueAsVar instanceof OctetString) {
				if(valueAsVar instanceof Opaque) {
					retvalue  = resolvOpaque((Opaque) valueAsVar);
				}
				else {
					//It might be a C string, try to remove the last 0;
					//But only if the new string is printable
					OctetString octetVar = (OctetString)valueAsVar;
					int length = octetVar.length();
					if(length > 1 && octetVar.get(length - 1 ) == 0) {
						OctetString newVar = octetVar.substring(0, length - 1);
						if(newVar.isPrintable()) {
							valueAsVar = newVar;
							logger.debug("Convertion an octet stream from " + octetVar + " to " + valueAsVar);
						}
					}
					retvalue  = valueAsVar.toString();
				}
			}
			else if(valueAsVar instanceof Null) {
				retvalue  = null;
			}
			else if(valueAsVar instanceof IpAddress) {
				retvalue  = ((IpAddress)valueAsVar).getInetAddress();
			}
			else {
				logger.warn("Unknown syntax " + AbstractVariable.getSyntaxString(type));
			}
		}
		return retvalue;
	}

	private final Object resolvOpaque(Opaque var) {

		//If not resolved, we will return the data as an array of bytes
		Object value = var.getValue();

		try {
			byte[] bytesArray = var.getValue();
			ByteBuffer bais = ByteBuffer.wrap(bytesArray);
			BERInputStream beris = new BERInputStream(bais);
			byte t1 = bais.get();
			byte t2 = bais.get();
			int l = BER.decodeLength(beris);
			if(t1 == TAG1) {
				if(t2 == TAG_FLOAT && l == 4)
					value = new Float(bais.getFloat());
				else if(t2 == TAG_DOUBLE && l == 8)
					value = new Double(bais.getDouble());
			}
		} catch (IOException e) {
			logger.error(var.toString());
		}
		return value;
	}

}
