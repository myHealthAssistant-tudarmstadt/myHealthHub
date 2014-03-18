/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\HieuHa\\AndroidOpenSource\\myHealthHub\\src\\de\\tudarmstadt\\dvs\\myhealthassistant\\myhealthhub\\IMyHealthHubRemoteService.aidl
 */
package de.tudarmstadt.dvs.myhealthassistant.myhealthhub;
public interface IMyHealthHubRemoteService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements de.tudarmstadt.dvs.myhealthassistant.myhealthhub.IMyHealthHubRemoteService
{
private static final java.lang.String DESCRIPTOR = "de.tudarmstadt.dvs.myhealthassistant.myhealthhub.IMyHealthHubRemoteService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an de.tudarmstadt.dvs.myhealthassistant.myhealthhub.IMyHealthHubRemoteService interface,
 * generating a proxy if needed.
 */
public static de.tudarmstadt.dvs.myhealthassistant.myhealthhub.IMyHealthHubRemoteService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof de.tudarmstadt.dvs.myhealthassistant.myhealthhub.IMyHealthHubRemoteService))) {
return ((de.tudarmstadt.dvs.myhealthassistant.myhealthhub.IMyHealthHubRemoteService)iin);
}
return new de.tudarmstadt.dvs.myhealthassistant.myhealthhub.IMyHealthHubRemoteService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getStatus:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getStatus();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements de.tudarmstadt.dvs.myhealthassistant.myhealthhub.IMyHealthHubRemoteService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public int getStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getStatus, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public int getStatus() throws android.os.RemoteException;
}
