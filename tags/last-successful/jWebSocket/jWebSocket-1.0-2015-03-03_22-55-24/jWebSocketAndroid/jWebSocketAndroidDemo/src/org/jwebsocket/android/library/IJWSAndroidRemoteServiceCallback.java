/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: IJWSAndroidRemoteServiceCallback.aidl
 */
package org.jwebsocket.android.library;

import android.os.IBinder;

/**
 *
 * @author Alexander Schulze
 */
public interface IJWSAndroidRemoteServiceCallback extends android.os.IInterface {

	/**
	 * Local-side IPC implementation stub class.
	 */
	public static abstract class Stub extends android.os.Binder implements
			org.jwebsocket.android.library.IJWSAndroidRemoteServiceCallback {

		private static final java.lang.String DESCRIPTOR = "org.jwebsocket.android.library.IJWSAndroidRemoteServiceCallback";

		/**
		 * Construct the stub at attach it to the interface.
		 */
		public Stub() {
			this.attachInterface(this, DESCRIPTOR);
		}

		/**
		 * Cast an IBinder object into an IJWSAndroidRemoteServiceCallback
		 * interface, generating a proxy if needed.
		 *
		 * @param obj
		 * @return
		 */
		public static org.jwebsocket.android.library.IJWSAndroidRemoteServiceCallback asInterface(
				android.os.IBinder obj) {
			if ((obj == null)) {
				return null;
			}
			android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
			if (((iin != null) && (iin instanceof org.jwebsocket.android.library.IJWSAndroidRemoteServiceCallback))) {
				return ((org.jwebsocket.android.library.IJWSAndroidRemoteServiceCallback) iin);
			}
			return new org.jwebsocket.android.library.IJWSAndroidRemoteServiceCallback.Stub.Proxy(
					obj);
		}

		/**
		 *
		 * @return
		 */
		@Override
		public android.os.IBinder asBinder() {
			return this;
		}

		/**
		 *
		 * @param code
		 * @param data
		 * @param reply
		 * @param flags
		 * @return
		 * @throws android.os.RemoteException
		 */
		@Override
		public boolean onTransact(int code, android.os.Parcel data,
				android.os.Parcel reply, int flags)
				throws android.os.RemoteException {
			switch (code) {
				case INTERFACE_TRANSACTION: {
					reply.writeString(DESCRIPTOR);
					return true;
				}
				case TRANSACTION_onError: {
					data.enforceInterface(DESCRIPTOR);
					java.lang.String _arg0;
					_arg0 = data.readString();
					this.onError(_arg0);
					reply.writeNoException();
					return true;
				}
			}
			return super.onTransact(code, data, reply, flags);
		}

		private static class Proxy implements
				org.jwebsocket.android.library.IJWSAndroidRemoteServiceCallback {

			private android.os.IBinder mRemote;

			Proxy(android.os.IBinder remote) {
				mRemote = remote;
			}

			@Override
			public android.os.IBinder asBinder() {
				return mRemote;
			}

			public java.lang.String getInterfaceDescriptor() {
				return DESCRIPTOR;
			}

			@Override
			public void onError(java.lang.String error)
					throws android.os.RemoteException {
				android.os.Parcel _data = android.os.Parcel.obtain();
				android.os.Parcel _reply = android.os.Parcel.obtain();
				try {
					_data.writeInterfaceToken(DESCRIPTOR);
					_data.writeString(error);
					mRemote.transact(Stub.TRANSACTION_onError, _data, _reply, 0);
					_reply.readException();
				} finally {
					_reply.recycle();
					_data.recycle();
				}
			}
		}

		static final int TRANSACTION_onError = (IBinder.FIRST_CALL_TRANSACTION + 0);
	}

	/**
	 *
	 * @param error
	 * @throws android.os.RemoteException
	 */
	public void onError(java.lang.String error)
			throws android.os.RemoteException;
}
