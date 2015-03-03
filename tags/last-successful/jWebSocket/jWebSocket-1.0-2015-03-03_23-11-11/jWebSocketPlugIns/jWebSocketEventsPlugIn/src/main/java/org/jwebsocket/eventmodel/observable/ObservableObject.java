//	---------------------------------------------------------------------------
//	jWebSocket - ObservableObject (Community Edition, CE)
//	---------------------------------------------------------------------------
//	Copyright 2010-2014 Innotrade GmbH (jWebSocket.org)
//	Alexander Schulze, Germany (NRW)
//
//	Licensed under the Apache License, Version 2.0 (the "License");
//	you may not use this file except in compliance with the License.
//	You may obtain a copy of the License at
//
//	http://www.apache.org/licenses/LICENSE-2.0
//
//	Unless required by applicable law or agreed to in writing, software
//	distributed under the License is distributed on an "AS IS" BASIS,
//	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//	See the License for the specific language governing permissions and
//	limitations under the License.
//	---------------------------------------------------------------------------
package org.jwebsocket.eventmodel.observable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javolution.util.FastMap;
import javolution.util.FastSet;
import org.jwebsocket.eventmodel.api.IListener;
import org.jwebsocket.eventmodel.api.IObservable;
import org.jwebsocket.eventmodel.util.Util;

/**
 *
 * @author Rolando Santamaria Maso
 */
public abstract class ObservableObject implements IObservable {

	private Integer mMaxExecutionTime = 1; //SECONDS
	private Set<Class<? extends Event>> mEvents = new FastSet();
	private Map<Class<? extends Event>, Set<IListener>> mListeners = new FastMap<Class<? extends Event>, Set<IListener>>().shared();

	private void checkEvent(Class<? extends Event> aEventClass) throws Exception {
		if (!mEvents.contains(aEventClass)) {
			throw new IndexOutOfBoundsException("The event '" + aEventClass + "' is not registered. Add it first!");
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void on(Class<? extends Event> aEventClass, IListener aListener) throws Exception {
		checkEvent(aEventClass);
		if (getListeners().containsKey(aEventClass)) {
			getListeners().get(aEventClass).add(aListener);
		} else {
			getListeners().put(aEventClass, new FastSet<IListener>());
			on(aEventClass, aListener);
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void on(Collection<Class<? extends Event>> aEventClassCollection, IListener aListener) throws Exception {
		for (Class<? extends Event> lClass : aEventClassCollection) {
			on(lClass, aListener);
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void addEvents(Class<? extends Event> aEventClass) {
		// public void addEvents(Event aEventClass) {
		if (!getEvents().contains(aEventClass)) {
			getEvents().add(aEventClass);
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void addEvents(Collection<Class<? extends Event>> aEventClassCollection) {
		for (Class<? extends Event> lClass : aEventClassCollection) {
			addEvents(lClass);
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void removeEvents(Class<? extends Event> aEventClass) {
		if (getEvents().contains(aEventClass)) {
			getEvents().remove(aEventClass);
		}

		if (getListeners().containsKey(aEventClass)) {
			getListeners().remove(aEventClass);
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void removeEvents(Collection<Class<? extends Event>> aEventClassCollection) {
		for (Class<? extends Event> lClass : aEventClassCollection) {
			removeEvents(lClass);
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void un(Class<? extends Event> aEventClass, IListener aListener) {
		if (getListeners().containsKey(aEventClass)) {
			getListeners().get(aEventClass).remove(aListener);
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void un(Collection<Class<? extends Event>> aEventClassCollection, IListener aListener) {
		for (Class<? extends Event> lClass : aEventClassCollection) {
			un(lClass, aListener);
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public ResponseEvent notify(Event aEvent) throws Exception {
		return notify(aEvent, null, false);
	}

	/**
	 * {@inheritDoc }
	 *
	 * @param aUseThreads
	 */
	@Override
	public ResponseEvent notify(Event aEvent, ResponseEvent aResponseEvent, boolean aUseThreads) throws Exception {
		checkEvent((Class<? extends Event>) aEvent.getClass());

		aEvent.setSubject(this);

		if (null == aResponseEvent) {
			aResponseEvent = new ResponseEvent();
			aResponseEvent.setId(aEvent.getId());
		}

		long lInitTime = System.nanoTime();

		if (getListeners().containsKey(aEvent.getClass()) && null != getListeners().get(aEvent.getClass())) {
			if (getListeners().get(aEvent.getClass()).size() > 0) {
				Set<IListener> lCalls = getListeners().get(aEvent.getClass());
				if (true == aUseThreads) {
					ExecutorService lExecutor = Executors.newCachedThreadPool();
					//Running in Threads
					for (IListener lListener : lCalls) {
						lExecutor.submit(new CallableListener(lListener, aEvent, aResponseEvent));
					}
					//Wait for ThreadPool termination
					Util.shutdownThreadPoolAndAwaitTermination(lExecutor, getMaxExecutionTime());
				} else {
					//Iterative execution
					for (IListener lListener : lCalls) {
						ObservableObject.callProcessEvent(lListener, aEvent, aResponseEvent);
					}
				}
			}
		}

		aResponseEvent.setProcessingTime((System.nanoTime() - lInitTime));
		return aResponseEvent;
	}

	/**
	 * Execute the <tt>processEvent</tt> method on listeners according to the
	 * custom event class
	 *
	 * @param aListener The listener that will process the event
	 * @param aEvent The event to process
	 * @param aResponseEvent The response to populate
	 * @throws Exception
	 */
	public static void callProcessEvent(IListener aListener, Event aEvent, ResponseEvent aResponseEvent) throws Exception {
		Class<? extends Event> lEventClass = aEvent.getClass();
		Class<? extends IListener> lListenerClass = aListener.getClass();
		Class<? extends ResponseEvent> lResponseClass = aResponseEvent.getClass();

		try {
			Method lMethod = lListenerClass.getMethod("processEvent", lEventClass, lResponseClass);
			lMethod.invoke(aListener, lEventClass.cast(aEvent), lResponseClass.cast(aResponseEvent));
		} catch (NoSuchMethodException ex) {
			//Calling the base method
			aListener.processEvent(aEvent, aResponseEvent);
		} catch (InvocationTargetException ex) {
			throw (Exception) ex.getTargetException();
		}
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public ResponseEvent notifyUntil(Event aEvent) throws Exception {
		return notifyUntil(aEvent, null);
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public ResponseEvent notifyUntil(Event aEvent, ResponseEvent aResponseEvent) throws Exception {
		checkEvent(aEvent.getClass());

		aEvent.setSubject(this);

		if (null == aResponseEvent) {
			aResponseEvent = new ResponseEvent();
			aResponseEvent.setId(aEvent.getId());
		}

		long lInitTime = System.nanoTime();

		if (getListeners().containsKey(aEvent.getClass()) && null != getListeners().get(aEvent.getClass())) {
			if (getListeners().get(aEvent.getClass()).size() > 0) {
				Set<IListener> lCalls = getListeners().get(aEvent.getClass());

				for (IListener lListener : lCalls) {
					ObservableObject.callProcessEvent(lListener, aEvent, aResponseEvent);

					if (aEvent.isProcessed()) {
						break;
					}
				}
			}
		}

		aResponseEvent.setProcessingTime(System.nanoTime() - lInitTime);
		return aResponseEvent;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public boolean hasListeners(Class<? extends Event> aEventClass) throws Exception {
		checkEvent(aEventClass);
		if (getListeners().containsKey(aEventClass) && getListeners().get(aEventClass).size() > 0) {
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public boolean hasListener(Class<? extends Event> aEventClass, IListener aListener) throws Exception {
		checkEvent(aEventClass);
		if (getListeners().containsKey(aEventClass) && getListeners().get(aEventClass).contains(aListener)) {
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void purgeListeners() {
		getListeners().clear();
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public void purgeEvents() {
		getEvents().clear();
		purgeListeners();
	}

	/**
	 * {@inheritDoc }
	 */
	@Override
	public boolean hasEvent(Class<? extends Event> aEventClass) {
		return getEvents().contains(aEventClass);
	}

	/**
	 *
	 * @return The allowed time to wait for the listeners when execute it in
	 * threads
	 */
	public Integer getMaxExecutionTime() {
		return mMaxExecutionTime;
	}

	/**
	 * @param aMaxExecutionTime The allowed time to wait for the listeners when
	 * execute it in threads
	 */
	public void setMaxExecutionTime(Integer aMaxExecutionTime) {
		this.mMaxExecutionTime = aMaxExecutionTime;
	}

	/**
	 * @return The listeners collection (unmodifiable)
	 */
	public Map<Class<? extends Event>, Set<IListener>> getListeners() {
		return mListeners;
	}

	/**
	 * @return The events collection (unmodifiable)
	 */
	public Set<Class<? extends Event>> getEvents() {
		return mEvents;
	}

	/**
	 * @param aEvents The events collection to set. Don't work as a setter,
	 * internally the events are added to the subject events
	 */
	public void setEvents(Set<Class<? extends Event>> aEvents) {
		addEvents(aEvents);
	}

	/**
	 * @param aEvents The events collection to set. Don't work as a setter,
	 * internally the events are added to the subject events
	 * @throws ClassNotFoundException
	 */
	public void setEventClasses(Set<String> aEvents) throws ClassNotFoundException {
		Set lClasses = new FastSet();
		for (String lEventClass : aEvents) {
			lClasses.add(Class.forName(lEventClass));
		}

		addEvents(lClasses);
	}
}
