/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jwebsocket.plugins.quota;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.log4j.Logger;
import org.jwebsocket.api.PluginConfiguration;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.config.JWebSocketServerConstants;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.ActionPlugIn;
import org.jwebsocket.token.Token;
import org.jwebsocket.plugins.annotations.Role;
import org.jwebsocket.plugins.quota.api.IQuota;
import org.jwebsocket.plugins.quota.api.IQuotaProvider;
import org.jwebsocket.plugins.quota.api.IQuotaSingleInstance;
import org.jwebsocket.plugins.quota.api.IQuotaStorage;
import org.jwebsocket.plugins.quota.utils.QuotaHelper;
import org.jwebsocket.plugins.quota.utils.QuotaProvider;
import org.jwebsocket.token.JSONToken;
import org.jwebsocket.token.MapToken;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author osvaldo
 */
public class QuotaPlugin extends ActionPlugIn {

    private static Logger mLog = Logging.getLogger();
    public static final String NS =
            JWebSocketServerConstants.NS_BASE + ".plugins.quota";
    private static ApplicationContext mSpringAppContext;
    private IQuotaProvider mQuotaProvider;
    private QuotaHelper mQuotaHelper;

    @Override
    public String getNamespace() {
        return NS;
    }

    public QuotaPlugin(PluginConfiguration aConfiguration) {
        super(aConfiguration);
        setNamespace(NS);

        if (mLog.isDebugEnabled()) {
            mLog.debug("QuotaPlugin successfully instantiated!");
        }

        mSpringAppContext = getConfigBeanFactory();
        mQuotaProvider = (QuotaProvider) mSpringAppContext.getBean("quotaProv"
                + "ider");
    }

    @Role(name = NS + ".quota_create")
    public void registerQuotaAction(WebSocketConnector aConnector, Token aToken) {

        try {
            boolean lHasUuid = aToken.getMap().containsKey("q_uuid");
            String lUuid = "";
            if (lHasUuid) {
                lUuid = aToken.getString("q_uuid");
            }

            String lNS = aToken.getString("q_namespace");
            String lInstance = aToken.getString("q_instance");
            String lInstanceType = aToken.getString("q_instance_type");
            String lQuotaIdentifier = aToken.getString("q_identifier");

            Token lResult = TokenFactory.createToken(getNamespace(),
                    aToken.getType());
            IQuota lQuota = (IQuota) mQuotaProvider.getQuotaByIdentifier(lQuotaIdentifier);
            String lQuotaType = lQuota.getType();

            if (!lHasUuid) {
                lUuid = QuotaHelper.generateQuotaUUID();
                long lValue = Long.parseLong(aToken.getString("q_value"));
                try {
                    lQuota.register(lInstance, lNS, lUuid, lValue,
                            lInstanceType, lQuotaType, lQuotaIdentifier);
                    lResult.setString("message", "Quota created succesfully");
                    getServer().sendToken(aConnector, lResult);
                } catch (Exception aException) {
                    getServer().sendErrorToken(aConnector, aToken, -1,
                            "Error creating the quota: " + aException.getMessage());
                }
            } else {
                try {
                    lQuota.register(lUuid, lInstance, lInstanceType);
                    lResult.setString("message", "Quota created succesfully");
                    getServer().sendToken(aConnector, lResult);
                } catch (Exception aException) {
                    getServer().sendErrorToken(aConnector, aToken, -1,
                            "Error creating the quota: " + aException.getMessage());
                }
            }
        } catch (Exception aException) {
            mLog.error("Error creating"
                    + "Quota" + aException.getMessage());
        }
    }

    @Role(name = NS + ".quota_remove")
    public void unregisterQuotaAction(WebSocketConnector aConnector, Token aToken) {
        try {

            String lUuid = aToken.getString("q_uuid");
            String lQuotaIdentifier = aToken.getString("q_identifier");
            String lInstance = aToken.getString("q_instance").trim();

            IQuota lQuota = (IQuota) mQuotaProvider.getQuotaByIdentifier(lQuotaIdentifier);

            Token lResult = createResponse(aToken);

            try {
                lQuota.unregister(lInstance, lUuid);
                lResult.setString("message", "Quota removed succesfully");
                getServer().sendToken(aConnector, lResult);

            } catch (Exception aException) {
                getServer().sendErrorToken(aConnector, aToken, -1,
                        "Error removing the quota: " + aException.getMessage());
            }
        } catch (Exception aException) {
            mLog.error("Error when unregister the quota"
                    + aException.getMessage());
        }

    }

    @Role(name = NS + ".quota_query")
    public void queryAction(WebSocketConnector aConnector, Token aToken) {
        try {
            Token lResult = createResponse(aToken);
            FastList<Token> lResultList = new FastList<Token>();

            String lNS = aToken.getString("q_namespace").trim();
            String lInstance = aToken.getString("q_instance").trim();
            String lQuotaType = aToken.getString("q_type").trim();
            String lQuotaIdentifier = null;
            lQuotaIdentifier = aToken.getString("q_identifier");
            
            FastList<IQuotaSingleInstance> lQinstanceList = new FastList<IQuotaSingleInstance>();
            if ( lQuotaIdentifier != null ) {
                IQuota lQuota = (IQuota) mQuotaProvider.getQuotaByIdentifier(lQuotaIdentifier);
                addAllQSIList(lQinstanceList, (FastList<IQuotaSingleInstance>)lQuota.getStorage().getQuotasByIdentifier(lQuotaIdentifier));

            } else {
                Map<String, IQuotaStorage> lActiveStorage = mQuotaProvider.getActiveStorages();
                for (Map.Entry<String, IQuotaStorage> entry : lActiveStorage.entrySet()) {
                    String lKey = entry.getKey();
                    IQuotaStorage lQuotaStorage = entry.getValue();
                    if (lInstance.equals("") && lNS.equals("")) {
                       lQinstanceList.addAll((FastList<IQuotaSingleInstance>)lQuotaStorage.getQuotas(lQuotaType));
                    } else {
                        if (!lInstance.equals("") && !lNS.equals("")) {
                            lQinstanceList.addAll((List<IQuotaSingleInstance>)lQuotaStorage.getQuotas(lQuotaType, lNS, lInstance));
                        } else {
                            if (!lInstance.equals("")) {
                                lQinstanceList.addAll((List<IQuotaSingleInstance>)lQuotaStorage.getQuotasByInstance(lQuotaType, lInstance));
                            } else {
                                lQinstanceList.addAll((List<IQuotaSingleInstance>)lQuotaStorage.getQuotasByNs(lQuotaType, lNS));
                            }
                        }
                    }
                }
            }

            for (Iterator<IQuotaSingleInstance> lQuotaIt = lQinstanceList.iterator();
                    lQuotaIt.hasNext();) {

                IQuotaSingleInstance lQuotaSingleInstance = lQuotaIt.next();
                Token lAuxToken = TokenFactory.createToken();

                lQuotaSingleInstance.writeToToken(lAuxToken);
                lResultList.add(lAuxToken);
            }

            lResult.setInteger("totalCount", lQinstanceList.size());
            lResult.setList("data", lResultList);

            getServer().sendToken(aConnector, lResult);

        } catch (Exception aExcep) {
            System.out.println(aExcep.getMessage());
            getServer().sendErrorToken(aConnector, aToken, -1, "The following "
                    + "error was captured in the server: " + aExcep.getMessage());
        }

    }
    
    private void addAllQSIList(
            FastList<IQuotaSingleInstance> aRefList,
            FastList<IQuotaSingleInstance> aPartialList){
        
        for (Iterator<IQuotaSingleInstance> it = aPartialList.iterator(); it.hasNext();) {
            IQuotaSingleInstance iQuotaSingleInstance = it.next();
            aRefList.add(iQuotaSingleInstance);
        }
    }

    @Role(name = NS + ".quota_query")
    public void getQuotaAction(WebSocketConnector aConnector, Token aToken) {
        try {
            Token lResult = createResponse(aToken);
            String lUuid = aToken.getString("q_uuid").trim();
            String lQuotaIdentifier = aToken.getString("q_identifier").trim();
            IQuota lQuota = (IQuota) mQuotaProvider.getQuotaByIdentifier(lQuotaIdentifier);
            long lValue = lQuota.getQuota(lUuid);

            lResult.setLong("value", lValue);
            lResult.setBoolean("success", true);

            getServer().sendToken(aConnector, lResult);

        } catch (Exception aExcep) {
            getServer().sendErrorToken(aConnector, aToken, -1, "The following "
                    + "error was captured in the server: " + aExcep.getMessage());
        }

    }

    @Role(name = NS + ".quota_query")
    public void getActivesQuotaAction(WebSocketConnector aConnector, Token aToken) {
        try {
            Token lResult = createResponse(aToken);
            FastList<Token> lResultList = new FastList<Token>();

            Object[] lActivesQuota = mQuotaProvider.getActiveQuotas().keySet().toArray();
            for (int i = 0; i < lActivesQuota.length; i++) {
                String lIdentifier = lActivesQuota[i].toString();
                Token lToken = TokenFactory.createToken();
                lToken.setString("name", lIdentifier);
                lResultList.add(lToken);
            }

            lResult.setList("data", lResultList);
            getServer().sendToken(aConnector, lResult);

        } catch (Exception aExcep) {
            getServer().sendErrorToken(aConnector, aToken, -1, "The following "
                    + "error was captured in the server: " + aExcep.getMessage());
        }

    }

    @Role(name = NS + ".quota_update")
    public void reduceQuotaAction(WebSocketConnector aConnector, Token aToken) {
        try {
            Token lResult = createResponse(aToken);

            String lUuid = aToken.getString("q_uuid").trim();
            String lQuotaIdentifier = aToken.getString("q_identifier").trim();
            long lReduce = Long.parseLong(aToken.getString("q_value"));

            IQuota lQuota = (IQuota) mQuotaProvider.getQuotaByIdentifier(lQuotaIdentifier);
            long lValue = lQuota.reduceQuota(lUuid, lReduce);
            lResult.setLong("value", lValue);

            getServer().sendToken(aConnector, lResult);

        } catch (Exception aExcep) {
            getServer().sendErrorToken(aConnector, aToken, -1, "The following "
                    + "error was captured in the server: " + aExcep.getMessage());
        }

    }

    @Role(name = NS + ".quota_update")
    public void setQuotaAction(WebSocketConnector aConnector, Token aToken) {
        try {
            Token lResult = createResponse(aToken);

            String lUuid = aToken.getString("q_uuid").trim();
            String lQuotaIdentifier = aToken.getString("q_identifier").trim();
            long lValue = Long.parseLong(aToken.getString("q_value"));

            IQuota lQuota = (IQuota) mQuotaProvider.getQuotaByIdentifier(lQuotaIdentifier);
            lValue = lQuota.setQuota(lUuid, lValue);

            lResult.setLong("value", lValue);

            getServer().sendToken(aConnector, lResult);

        } catch (Exception aExcep) {
            System.out.println(aExcep.getMessage());
            getServer().sendErrorToken(aConnector, aToken, -1, "The following "
                    + "error was captured in the server: " + aExcep.getMessage());
        }

    }

    @Role(name = NS + ".quota_update")
    public void increaseQuotaAction(WebSocketConnector aConnector, Token aToken) {
        try {
            Token lResult = createResponse(aToken);

            String lUuid = aToken.getString("q_uuid").trim();
            String lQuotaIdentifier = aToken.getString("q_identifier").trim();
            long lIncrease = Long.parseLong(aToken.getString("q_value"));

            IQuota lQuota = (IQuota) mQuotaProvider.getQuotaByIdentifier(lQuotaIdentifier);
            long lValue = lQuota.increaseQuota(lUuid, lIncrease);

            lResult.setLong("value", lValue);
            getServer().sendToken(aConnector, lResult);

        } catch (Exception aExcep) {
            System.out.println(aExcep.getMessage());
            getServer().sendErrorToken(aConnector, aToken, -1, "The following "
                    + "error was captured in the server: " + aExcep.getMessage());
        }

    }
}