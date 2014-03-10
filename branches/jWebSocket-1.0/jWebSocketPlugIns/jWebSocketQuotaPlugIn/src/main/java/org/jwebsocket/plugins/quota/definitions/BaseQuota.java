/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jwebsocket.plugins.quota.definitions;

import java.util.Iterator;
import java.util.List;
import javolution.util.FastList;
import org.apache.log4j.Logger;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.quota.api.IQuota;
import org.jwebsocket.plugins.quota.api.IQuotaSingleInstance;
import org.jwebsocket.plugins.quota.api.IQuotaStorage;
import org.jwebsocket.plugins.quota.definitions.singleIntance.QuotaBaseInstance;
import org.jwebsocket.plugins.quota.definitions.singleIntance.QuotaChildSI;
import org.jwebsocket.plugins.quota.definitions.singleIntance.QuotaCountdownSI;
import org.jwebsocket.plugins.quota.utils.QuotaHelper;
import org.jwebsocket.plugins.quota.utils.exception.ExceptionQuotaAlreadyExist;
import org.jwebsocket.plugins.quota.utils.exception.ExceptionQuotaNotFound;

/**
 *
 * @author Osvaldo Aguilar Lauzurique
 */
public abstract class BaseQuota implements IQuota {

	/**
	 *
	 */
	protected static Logger mLog = Logging.getLogger();

	/**
	 *
	 */
	protected IQuotaStorage mQuotaStorage;

	/**
	 *
	 */
	protected String mQuotaType;

	/**
	 *
	 */
	protected String mQuotaIdentifier;

	/**
	 *
	 */
	protected long mDefaultReduceValue;

	/**
	 *
	 */
	protected long mDefaultIncrease;

	@Override
	public long getDefaultReduceValue() {
		return mDefaultReduceValue;
	}

	/**
	 *
	 * @return
	 */
	public long getDefaultIncrease() {
		return mDefaultIncrease;
	}

	/**
	 *
	 * @param aDefaultReduceValue
	 */
	public void setDefaultReduceValue(long aDefaultReduceValue) {
		this.mDefaultReduceValue = aDefaultReduceValue;
	}

	/**
	 *
	 * @param mQuotaIdentifier
	 */
	public void setQuotaIdentifier(String mQuotaIdentifier) {
		this.mQuotaIdentifier = mQuotaIdentifier;
	}

	/**
	 *
	 * @param aDefaultIncrease
	 */
	public void setDefaultIncreaseValue(long aDefaultIncrease) {
		this.mDefaultIncrease = aDefaultIncrease;
	}

	/**
	 *
	 * @param aQuotaType
	 */
	public void setQuotaType(String aQuotaType) {
		this.mQuotaType = aQuotaType;
	}

	@Override
	public IQuotaStorage getStorage() {
		return mQuotaStorage;
	}

	@Override
	public String getType() {
		return mQuotaType;
	}

	@Override
	public void setStorage(IQuotaStorage aQuotaStorage) {
		this.mQuotaStorage = aQuotaStorage;
		try {
			this.mQuotaStorage.initialize();
		} catch (Exception e) {
			mLog.error("Error to initialize the quota storage");
		}
	}

	@Override
	public IQuotaSingleInstance getQuota(String aInstance, String aNameSpace, String aInstanceType, String aActions) {

		IQuotaSingleInstance lQResult;
		String lUuid;
		try {
			lUuid = getQuotaUuid(mQuotaIdentifier, aNameSpace, aInstance, aInstanceType, aActions);
		} catch (Exception e) {
			lUuid = "not-found";
		}

		//Asking if the user has as part of a group quota that belong to a group. 
		if (lUuid.equals("not-found") && aInstanceType.equals("User")) {

			List<IQuotaSingleInstance> lQuotasGroup
					= mQuotaStorage.getQuotasByIdentifierNSInstanceType(mQuotaIdentifier, aNameSpace, "Group");
			lQResult = findQuotaByInstance(lQuotasGroup, aInstance, aActions);

			if (lQResult == null) {
				List<IQuotaSingleInstance> lQuotasUser
						= mQuotaStorage.getQuotasByIdentifierNSInstanceType(mQuotaIdentifier, aNameSpace, "User");
				lQResult = findQuotaByInstance(lQuotasUser, aInstance, aActions);
			}
			return lQResult;
		} else {
			return getQuota(lUuid);
		}
	}

	@Override
	public List<IQuotaSingleInstance> getQuotas(String aInstance, String aNameSpace,
			String aInstanceType) {

		FastList<IQuotaSingleInstance> lQuotas = null;

		List<IQuotaSingleInstance> lQuotasNs
				= mQuotaStorage.getQuotasByNs(mQuotaIdentifier, aNameSpace);
		lQuotas = (FastList<IQuotaSingleInstance>) findQuotasByInstance(lQuotasNs, aInstance);

		return lQuotas;
	}

	/**
	 * get a IQuotaSingleInstance list and an string with the instance and
	 * return all quotas about belong to this instance.
	 *
	 * @return
	 */
	private List<IQuotaSingleInstance> findQuotasByInstance(List<IQuotaSingleInstance> aQuotas,
			String aInstance) {

		List<IQuotaSingleInstance> lQResult = new FastList<IQuotaSingleInstance>();

		IQuotaSingleInstance lQtmp = null;
		for (Iterator<IQuotaSingleInstance> it = aQuotas.iterator(); it.hasNext();) {
			IQuotaSingleInstance lQuotaSingle = it.next();
			QuotaChildSI lChild;
			lChild = lQuotaSingle.getChildQuota(aInstance);
			if (null != lChild) {
				if (lQuotaSingle.getInstanceType().equals("Group")) {

					lQtmp
							= QuotaHelper.factorySingleInstance(lChild.getValue(), lChild.getInstance(),
									lChild.getUuid(), lQuotaSingle.getNamespace(), lQuotaSingle.getQuotaType(),
									lQuotaSingle.getQuotaIdentifier(), lChild.getInstanceType(), lQuotaSingle.getActions());
				}

				if (lQuotaSingle.getInstanceType().equals("User")) {

					lQtmp
							= QuotaHelper.factorySingleInstance(lQuotaSingle.getvalue(), lChild.getInstance(),
									lChild.getUuid(), lQuotaSingle.getNamespace(), lQuotaSingle.getQuotaType(),
									lQuotaSingle.getQuotaIdentifier(), lChild.getInstanceType(), lQuotaSingle.getActions());
				}

				if (lQtmp != null) {
					lQResult.add(lQtmp);
				}
			}
		}
		return lQResult;
	}

	/**
	 * get a IQuotaSingleInstance list and an string with the instanceType and
	 * return the IQuotaSingleInstance.
	 *
	 * @return
	 */
	private IQuotaSingleInstance findQuotaByInstance(List<IQuotaSingleInstance> aQuotas,
			String aInstance, String aActions) {

		IQuotaSingleInstance lQResult = null;

		for (Iterator<IQuotaSingleInstance> it = aQuotas.iterator(); it.hasNext();) {
			IQuotaSingleInstance lQuotaSingle = it.next();
			QuotaChildSI lChild;
			lChild = lQuotaSingle.getChildQuota(aInstance);
			if (null != lChild) {
				if (!lQuotaSingle.getActions().equals(aActions)) {
					continue;
				}
				if (lQuotaSingle.getInstanceType().equals("Group")) {

					lQResult
							= QuotaHelper.factorySingleInstance(lChild.getValue(), lChild.getInstance(),
									lChild.getUuid(), lQuotaSingle.getNamespace(), lQuotaSingle.getQuotaType(),
									lQuotaSingle.getQuotaIdentifier(), lChild.getInstanceType(), lQuotaSingle.getActions());
				}

				if (lQuotaSingle.getInstanceType().equals("User")) {

					lQResult
							= QuotaHelper.factorySingleInstance(lQuotaSingle.getvalue(), lChild.getInstance(),
									lChild.getUuid(), lQuotaSingle.getNamespace(), lQuotaSingle.getQuotaType(),
									lQuotaSingle.getQuotaIdentifier(), lChild.getInstanceType(), lQuotaSingle.getActions());
				}

				return lQResult;
			}
		}
		return null;
	}

	@Override
	public IQuotaSingleInstance getQuota(String aUuid) {

		IQuotaSingleInstance lQuotaInstance = (IQuotaSingleInstance) mQuotaStorage.getQuotaByUuid(aUuid);
		return lQuotaInstance;

	}

	@Override
	public IQuotaSingleInstance getQuota(String aUuid, String aInstance) {

		IQuotaSingleInstance lQuotaInstance
				= (IQuotaSingleInstance) mQuotaStorage.getQuotaByUuid(aUuid);

		if (lQuotaInstance.getInstance().equals(aInstance)) {
			return lQuotaInstance;
		} else {

			QuotaChildSI lQuotaChild = lQuotaInstance.getChildQuota(aInstance);
			if (null != lQuotaChild) {

				IQuotaSingleInstance lSingle = new QuotaBaseInstance(lQuotaChild.getValue(),
						aInstance, aUuid, lQuotaInstance.getNamespace(),
						lQuotaInstance.getQuotaType(), lQuotaInstance.getQuotaIdentifier(),
						lQuotaChild.getInstanceType(), lQuotaInstance.getActions());

				return lSingle;
			}
		}
		return null;
	}

	@Override
	public String getIdentifier() {
		return mQuotaIdentifier;
	}

	@Override
	abstract public long reduceQuota(String aUuid, long aAmount);

	@Override
	public long reduceQuota(String aInstance, String aNameSpace,
			String aInstanceType, String aActions, long aAmount) {

		IQuotaSingleInstance lQSingle = getQuota(aInstance, aNameSpace,
				aInstanceType, aActions);
		long lResult = -1;
		if (lQSingle != null) {
			long lReduce = lQSingle.getvalue() - aAmount;
			lResult = setQuota(lQSingle.getInstance(), aNameSpace,
					lQSingle.getInstanceType(), aActions, lReduce);
		}
		return lResult;
	}

	@Override
	public long reduceQuota(String aUuid) {
		return reduceQuota(aUuid, mDefaultReduceValue);
	}

	@Override
	public String getActions(String aUuid) {
		String lActions = this.getStorage().getActions(aUuid);
		return lActions;
	}

	@Override
	public long increaseQuota(String aInstance, String aNameSpace,
			String aInstanceType, String aActions, long aAmount) {
		IQuotaSingleInstance lQSingle = getQuota(aInstance, aNameSpace, aInstanceType, aActions);
		long lResult = -1;
		if (lQSingle != null) {
			long lReduce = lQSingle.getvalue() + aAmount;
			lResult = setQuota(lQSingle.getInstance(), aNameSpace,
					lQSingle.getInstanceType(), aActions, lReduce);
		}
		return lResult;

	}

	@Override
	public long increaseQuota(String aUuid, long aAmount) {
		long lValue = getQuota(aUuid).getvalue();
		return getStorage().update(aUuid, lValue + aAmount);
	}

	@Override
	public long setQuota(String aInstance, String aNameSpace,
			String aInstanceType, String aActions, long aAmount) {

		IQuotaSingleInstance lQSingle = getQuota(aInstance, aNameSpace, aInstanceType, aActions);
		//if the instanceType request is Group then update the father quota.
		if (lQSingle.getInstanceType().equals("Group") && aInstanceType.equals("Group")) {
			return setQuota(lQSingle.getUuid(), aAmount);
		}

		/**
		 * if the quota type of the getQuota method is User, it is possible that
		 * this quota be part of a father quota.
		 */
		if (lQSingle.getInstanceType().equals("User")) {
			if (mQuotaStorage.quotaExist(aNameSpace, mQuotaIdentifier,
					lQSingle.getInstance(), lQSingle.getActions())) {
				return setQuota(lQSingle.getUuid(), aAmount);
			} else {
				IQuotaSingleInstance lSingleInstance = mQuotaStorage.getQuotaByUuid(lQSingle.getUuid());
				QuotaChildSI lQChild = lSingleInstance.getChildQuota(lQSingle.getInstance());
				lQChild.setValue(aAmount);

				if (lSingleInstance.getInstanceType().equals("User")) {
					return mQuotaStorage.update(lSingleInstance.getUuid(), lQChild.getValue());
				}
				return mQuotaStorage.update(lQChild);
			}
		} else {
			return -1;
		}
	}

	@Override
	public long setQuota(String aUuid, long aAmount) {
		return getStorage().update(aUuid, aAmount);
	}

	@Override
	public void register(String aUuid, String aInstance,
			String aInstanceType) throws Exception {

		if (!mQuotaStorage.quotaExist(aUuid)) {
			throw new ExceptionQuotaNotFound(aUuid);
		}
		//Creating the child Quota
		IQuotaSingleInstance lSingleInstance = mQuotaStorage.getQuotaByUuid(aUuid);

		if (lSingleInstance.getInstance().equals(aInstance)) {
			throw new ExceptionQuotaAlreadyExist(aInstance);
		}

		QuotaChildSI lChildQuota = new QuotaChildSI(aInstance, aUuid, aInstanceType);

        //if a register quota occur over a quota with InstanceType user
		//The quota is shared between the users of this quota, by this reason
		//The quota is register to the parent quota with 0 as their own value.
		if (lSingleInstance.getInstanceType().equals("User")) {
			lChildQuota.setValue(0);
		} else {
			lChildQuota.setValue(lSingleInstance.getvalue());
		}
		boolean lResult;

		lResult = lSingleInstance.addChildQuota(lChildQuota);

		if (lResult == true) {
			lResult = mQuotaStorage.save(lChildQuota);

			if (lResult == false) {
				throw new Exception("Error saving the quota.");
			}
		} else {
			throw new ExceptionQuotaAlreadyExist(aInstance);
		}
	}

	@Override
	public void create(String aInstance, String aNameSpace, String aUuid,
			long aAmount, String aInstanceType, String aQuotaType,
			String aQuotaIdentifier, String aActions) throws Exception {

		if (mQuotaStorage.quotaExist(aNameSpace, aQuotaIdentifier, aInstance, aActions)) {

			throw new ExceptionQuotaAlreadyExist(mQuotaStorage.
					getUuid(aQuotaIdentifier, aNameSpace, aInstance,
							aInstanceType, aActions));
		}

		IQuotaSingleInstance lSingleQuota;
		lSingleQuota = new QuotaCountdownSI(aAmount, aInstance, aUuid,
				aNameSpace, aQuotaType, aQuotaIdentifier, aInstanceType, aActions);
		mQuotaStorage.save(lSingleQuota);

	}

	@Override
	public void unregister(String aInstance, String aUuid)
			throws ExceptionQuotaNotFound {

		if (!mQuotaStorage.quotaExist(aUuid)) {
			throw new ExceptionQuotaNotFound(aUuid);
		}
		IQuotaSingleInstance lQSingle = getQuota(aUuid);
		if (lQSingle.getInstance().equals(aInstance)) {
			mQuotaStorage.remove(aUuid, aInstance);
		} else {
			QuotaChildSI lChild = lQSingle.getChildQuota(aInstance);
			if (lChild != null) {
				mQuotaStorage.remove(lChild);
			}

		}

	}

	@Override
	public void unregister(String aInstance,
			String aNameSpace, String aInstanceType, String aActions)
			throws ExceptionQuotaNotFound {

		String lUuid = getQuotaUuid(mQuotaIdentifier, aNameSpace, aInstance, aInstanceType, aActions);

		unregister(lUuid, aInstance);
	}

	@Override
	public List<String> getRegisteredInstances(String aNamespace, String aId) {
		return new FastList<String>();
	}

	@Override
	public List<String> getRegisterdQuotas(String aNamespace) {
		return new FastList<String>();
	}

	@Override
	public String getQuotaUuid(String aQuotaIdentifier, String aNamespace,
			String aInstance, String aInstanceType, String aActions) {

		try {
			return mQuotaStorage.getUuid(aQuotaIdentifier, aNamespace,
					aInstance, aInstanceType, aActions);
		} catch (ExceptionQuotaNotFound ex) {
			return "not-found";
		}
	}
}
