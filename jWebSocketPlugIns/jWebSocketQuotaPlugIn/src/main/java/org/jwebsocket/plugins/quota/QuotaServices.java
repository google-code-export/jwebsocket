/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jwebsocket.plugins.quota;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import org.apache.log4j.Logger;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.quota.api.IQuota;
import org.jwebsocket.plugins.quota.api.IQuotaProvider;
import org.jwebsocket.plugins.quota.api.IQuotaSingleInstance;
import org.jwebsocket.plugins.quota.api.IQuotaStorage;
import org.jwebsocket.plugins.quota.utils.QuotaHelper;
import org.jwebsocket.plugins.quota.utils.QuotaProvider;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author osvaldo
 */
public class QuotaServices {

    private static final Logger mLog = Logging.getLogger();
    private final String mNSPluging;
    private static ApplicationContext mSpringAppContext;
    private final IQuotaProvider mQuotaProvider;

    public QuotaServices(String mNSPluging, ApplicationContext aSpringAppContent) {
        this.mNSPluging = mNSPluging;

        mSpringAppContext = aSpringAppContent;
        mQuotaProvider = (QuotaProvider) mSpringAppContext.getBean("quotaProvider");
    }

    public String getNamespace() {
        return mNSPluging;
    }

    public Token createQuotaAction(Token aToken) {

        Token lResult = TokenFactory.createToken(aToken.getMap());
        try {
            String lNS = aToken.getString("namespace");
            String lInstance = aToken.getString("instance");
            String lInstanceType = aToken.getString("instance_type");
            String lQuotaIdentifier = aToken.getString("identifier");
            String lQuotActions = aToken.getString("actions");
            //This parameter is optional
            String lUuid = null;
            if (null != aToken.getString("uuid")) {
                lUuid = aToken.getString("uuid");
            } else {
                lUuid = QuotaHelper.generateQuotaUUID();
            }
            //= aToken.getString("uuid");

            IQuota lQuota = quotaByIdentifier(lQuotaIdentifier);

            String lQuotaType = lQuota.getType();



            long lValue = Long.parseLong(aToken.getString("value"));

            lQuota.create(lInstance, lNS, lUuid, lValue,
                    lInstanceType, lQuotaType, lQuotaIdentifier, lQuotActions);
            lResult.setString("message", "Quota created succesfully");
            lResult.setString("uuid", lUuid);
            lResult.setCode(0);

            return lResult;
        } catch (Exception aException) {
            mLog.error("Error creating"
                    + "Quota");
            return getErrorToken("Error creating the quota: ",
                    lResult);
        }
    }

    public Token registerQuotaAction(Token aToken) {
        try {
            String lUuid = aToken.getString("uuid");
            String lInstance = aToken.getString("instance");
            String lInstanceType = aToken.getString("instance_type");
            String lQuotaIdentifier = aToken.getString("identifier");

            Token lResult = TokenFactory.createToken(aToken.getMap());
            IQuota lQuota = quotaByIdentifier(lQuotaIdentifier);

            lQuota.register(lUuid, lInstance, lInstanceType);
            lResult.setString("message", "Quota register succesfully");
            lResult.setCode(0);

            return lResult;
        } catch (Exception aException) {
            mLog.error("Error creating"
                    + "Quota" + aException.getMessage());
            return getErrorToken("Error register the quota: " + aException.getMessage(),
                    aToken);
        }
    }

    private Token getErrorToken(String aMsg, Token aToken) {

        aToken.setCode(-1);
        aToken.setString("msg", aMsg);

        return aToken;
    }

    private IQuota quotaByIdentifier(String aIdentifier) {
        IQuota lQuota;
        try {
            lQuota = (IQuota) mQuotaProvider.getQuotaByIdentifier(aIdentifier);
        } catch (Exception exp) {
            lQuota = (IQuota) mSpringAppContext.getBean(aIdentifier);
        }
        return lQuota;
    }

    public Token unregisterQuotaAction(Token aToken) {
        try {
            String lUuid = aToken.getString("uuid");
            String lQuotaIdentifier = aToken.getString("identifier");
            String lInstance = aToken.getString("instance").trim();

            IQuota lQuota;
            lQuota = quotaByIdentifier(lQuotaIdentifier);

            Token lResult = TokenFactory.createToken(aToken.getMap());

            try {
                lQuota.unregister(lInstance, lUuid);
                lResult.setString("message", "Quota removed succesfully");
                lResult.setCode(0);
                return lResult;
            } catch (Exception aException) {
                return getErrorToken("Error removing the quota: " + aException.getMessage(),
                        aToken);
            }
        } catch (Exception aException) {
            mLog.error("Error unregistering the quota"
                    + aException.getMessage());
            return getErrorToken("Error removing the quota: " + aException.getMessage(),
                    aToken);
        }

    }

    private void addAllQSIList(FastList<IQuotaSingleInstance> aRefList,
            FastList<IQuotaSingleInstance> aPartialList) {

        for (Iterator<IQuotaSingleInstance> it = aPartialList.iterator(); it.hasNext();) {
            IQuotaSingleInstance iQuotaSingleInstance = it.next();
            aRefList.add(iQuotaSingleInstance);
        }
    }

    public Token queryAction(Token aToken) {
        try {
            Token lResult = TokenFactory.createToken(aToken.getMap());

            FastList<Token> lResultList = new FastList<Token>();

            String lNS = aToken.getString("namespace").trim();
            String lInstance = aToken.getString("instance").trim();
            String lQuotaType = aToken.getString("quotaType").trim();
            String lQuotaIdentifier = aToken.getString("identifier", null);
            FastList<IQuotaSingleInstance> lQinstanceList = new FastList<IQuotaSingleInstance>();

            if (lQuotaIdentifier != null) {
                IQuota lQuota;
                lQuota = quotaByIdentifier(lQuotaIdentifier);
                addAllQSIList(lQinstanceList, (FastList<IQuotaSingleInstance>) lQuota.getStorage().getQuotasByIdentifier(lQuotaIdentifier));
            } else {
                Map<String, IQuotaStorage> lActiveStorage = mQuotaProvider.getActiveStorages();
                for (Map.Entry<String, IQuotaStorage> entry : lActiveStorage.entrySet()) {
                    String lKey = entry.getKey();
                    IQuotaStorage lQuotaStorage = entry.getValue();
                    if (lInstance.equals("") && lNS.equals("")) {
                        lQinstanceList.addAll((FastList<IQuotaSingleInstance>) lQuotaStorage.getQuotas(lQuotaType));
                    } else {
                        if (!lInstance.equals("") && !lNS.equals("")) {
                            lQinstanceList.addAll((List<IQuotaSingleInstance>) lQuotaStorage.getQuotas(lQuotaType, lNS, lInstance));
                        } else {
                            if (!lInstance.equals("")) {
                                lQinstanceList.addAll((List<IQuotaSingleInstance>) lQuotaStorage.getQuotasByInstance(lQuotaType, lInstance));
                            } else {
                                lQinstanceList.addAll((List<IQuotaSingleInstance>) lQuotaStorage.getQuotasByNs(lQuotaType, lNS));
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
            lResult.setCode(0);
            return lResult;

        } catch (Exception aExcep) {
            return getErrorToken("The following "
                    + "error was captured in the server: " + aExcep.getMessage(), aToken);
        }

    }

    public Token getQuotaAction(Token aToken) {
        try {
            Token lResult = TokenFactory.createToken(aToken.getMap());
            String lQuotaIdentifier = aToken.getString("identifier").trim();

            String lNS = aToken.getString("namespace");
            String lInstance = aToken.getString("instance");
            String lInstanceType = aToken.getString("instance_type");
            String lActions = aToken.getString("actions");

            IQuota lQuota;
            lQuota = quotaByIdentifier(lQuotaIdentifier);
            String lUuid = aToken.getString("uuid");


            IQuotaSingleInstance lQuotaSingleInstance;

            if ((null != lNS || !lUuid.equals("")) && (null != lInstance || !lInstance.equals(""))
                    && (null != lInstanceType || !lInstanceType.equals(""))
                    && (null != lActions || !lActions.equals(""))) {

                lQuotaSingleInstance = lQuota.getQuota(lInstance, lNS, lInstanceType, lActions);

            } else {
                lQuotaSingleInstance = lQuota.getQuota(lUuid);
            }
            Token lAuxToken = TokenFactory.createToken();

            if (lQuotaSingleInstance == null) {

                lResult.setCode(-1);
                lResult.setString("msg", "There is not a quota whit the identifier "
                        + lQuotaIdentifier + " for the instance " + lInstance);

                return lResult;
            }

            lQuotaSingleInstance.writeToToken(lAuxToken);

            lResult.setLong("value", lQuotaSingleInstance.getvalue());
            lResult.setString("uuid", lQuotaSingleInstance.getUuid());
            lResult.setToken("quota", lAuxToken);

            lResult.setCode(0);

            return lResult;

        } catch (Exception aExcep) {
            return getErrorToken("The following "
                    + "error was captured in the server: " + aExcep.getMessage(), aToken);
        }
    }

    public Token getActivesQuotaAction(Token aToken) {
        try {

            Token lResult = TokenFactory.createToken(aToken.getMap());
            FastList<Token> lResultList = new FastList<Token>();

            Object[] lActivesQuota = mQuotaProvider.getActiveQuotas().keySet().toArray();
            for (int i = 0; i < lActivesQuota.length; i++) {
                String lIdentifier = lActivesQuota[i].toString();
                Token lToken = TokenFactory.createToken();
                lToken.setString("name", lIdentifier);
                lResultList.add(lToken);
            }

            lResult.setList("data", lResultList);
            lResult.setCode(0);

            return lResult;

        } catch (Exception aExcep) {
            return getErrorToken("The following "
                    + "error was captured in the server: " + aExcep.getMessage(), aToken);
        }

    }

    public Token reduceQuotaAction(Token aToken) {
        try {
            Token lResult = TokenFactory.createToken(aToken.getMap());
            String lQuotaIdentifier = aToken.getString("identifier").trim();

            Integer lReduce = aToken.getInteger("value");

            if (lReduce == null) {
                lReduce = Integer.parseInt(aToken.getString("value"));
            }


            String lNS = aToken.getString("namespace");
            String lInstance = aToken.getString("instance");
            String lInstanceType = aToken.getString("instance_type");
            String lActions = aToken.getString("actions");

            IQuota lQuota;
            lQuota = quotaByIdentifier(lQuotaIdentifier);
            long lValue;

            String lUuid = aToken.getString("uuid");
            if ((null != lNS || !lUuid.equals("")) && (null != lInstance || !lInstance.equals(""))
                    && (null != lInstanceType || !lInstanceType.equals(""))
                    && (null != lActions || !lActions.equals(""))) {

                lValue = lQuota.reduceQuota(lInstance, lNS, lInstanceType,
                        lActions, lReduce);
            } else {
                lValue = lQuota.reduceQuota(lUuid, lReduce);
            }

            if (lValue == -1) {
                return getErrorToken("Acces not allowed due to quota limitation exceed", aToken);
            }

            lResult.setLong("value", lValue);
            lResult.setCode(0);

            return lResult;

        } catch (Exception aExcep) {
            return getErrorToken("The following "
                    + "error was captured in the server: " + aExcep.getMessage(), aToken);
        }

    }

    public Token setQuotaAction(Token aToken) {

        try {
            Token lResult = TokenFactory.createToken(aToken.getMap());
            String lQuotaIdentifier = aToken.getString("identifier");

            Integer lReduce = aToken.getInteger("value");

            if (lReduce == null) {
                lReduce = Integer.parseInt(aToken.getString("value"));
            }

            String lNS = aToken.getString("namespace");
            String lInstance = aToken.getString("instance");
            String lInstanceType = aToken.getString("instance_type");
            String lActions = aToken.getString("actions");

            IQuota lQuota;
            lQuota = quotaByIdentifier(lQuotaIdentifier);
            long lValue;
            String lUuid = aToken.getString("uuid");

            if ((null != lNS || !lUuid.equals("")) && (null != lInstance || !lInstance.equals(""))
                    && (null != lInstanceType || !lInstanceType.equals(""))
                    && (null != lActions || !lActions.equals(""))) {


                lValue = lQuota.setQuota(lInstance, lNS, lInstanceType,
                        lActions, lReduce);

            } else {
                lValue = lQuota.setQuota(lUuid, lReduce);
            }
            lResult.setLong("value", lValue);
            lResult.setCode(0);

            return lResult;

        } catch (Exception aExcep) {
            return getErrorToken("The following "
                    + "error was captured in the server: " + aExcep.getMessage(), aToken);
        }

    }

    public Token increaseQuotaAction(Token aToken) {
        try {
            Token lResult = TokenFactory.createToken(aToken.getMap());

            String lQuotaIdentifier = aToken.getString("identifier").trim();
            Integer lReduce = aToken.getInteger("value");

            if (lReduce == null) {
                lReduce = Integer.parseInt(aToken.getString("value"));
            }

            String lNS = aToken.getString("namespace");
            String lInstance = aToken.getString("instance");
            String lInstanceType = aToken.getString("instance_type");
            String lActions = aToken.getString("actions");

            IQuota lQuota;
            lQuota = quotaByIdentifier(lQuotaIdentifier);
            long lValue;

            String lUuid = aToken.getString("uuid");

            if ((null != lNS || !lUuid.equals("")) && (null != lInstance || !lInstance.equals(""))
                    && (null != lInstanceType || !lInstanceType.equals(""))
                    && (null != lActions || !lActions.equals(""))) {

                lValue = lQuota.increaseQuota(lInstance, lNS,
                        lInstanceType, lActions, lReduce);
            } else {
                lValue = lQuota.increaseQuota(lUuid, lReduce);
            }
            lResult.setLong("value", lValue);
            lResult.setCode(0);

            return lResult;

        } catch (Exception aExcep) {

            return getErrorToken("The following "
                    + "error was captured in the server: " + aExcep.getMessage(), aToken);
        }

    }
}