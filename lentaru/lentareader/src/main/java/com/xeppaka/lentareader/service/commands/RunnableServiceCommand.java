package com.xeppaka.lentareader.service.commands;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import com.xeppaka.lentareader.service.BundleConstants;
import com.xeppaka.lentareader.service.ServiceResult;
import com.xeppaka.lentareader.utils.LentaConstants;

public abstract class RunnableServiceCommand implements ServiceCommand {
	private ResultReceiver resultReceiver;
	private int requestId;
	private long creationTime;
	private boolean reportError;

	public RunnableServiceCommand(int requestId, ResultReceiver resultReceiver, boolean reportError) {
		if (requestId < NO_REQUEST_ID) {
			throw new IllegalArgumentException("requestId is invalid. Use -1 if there is no request id.");
		}
		
		if (resultReceiver == null) {
			throw new NullPointerException("resultReceiver is null.");
		}
		
		creationTime = System.currentTimeMillis();
		
		this.requestId = requestId;
		this.resultReceiver = resultReceiver;
	}
	
	@Override
	public void run() {
		Log.d(LentaConstants.LoggerServiceTag, "Command started: " + getClass().getSimpleName());

		try {
			execute();
		} catch (Exception e) {
			Log.d(LentaConstants.LoggerServiceTag, "Exception occured during running the command " + getClass().getSimpleName(), e);
			
			if (resultReceiver != null && reportError()) {
				resultReceiver.send(ServiceResult.ERROR.ordinal(), prepareExceptionResult(e));
			}
			
			Log.d(LentaConstants.LoggerServiceTag, "Command finished with exception: " + getClass().getSimpleName());
			return;
		}
		
		if (resultReceiver != null) {
			Bundle result = getResult();
			
			if (result != null) {
				resultReceiver.send(ServiceResult.SUCCESS.ordinal(), result);
			}
		}
		
		Log.d(LentaConstants.LoggerServiceTag, "Command finished successfuly: " + getClass().getSimpleName());
	}
	
	protected abstract Bundle getResult();
	
	protected Bundle prepareExceptionResult(Exception e) {
		Bundle bundle = new Bundle();
		bundle.putInt(BundleConstants.KEY_REQUEST_ID.name(), getRequestId());
		bundle.putSerializable(BundleConstants.KEY_EXCEPTION.name(), e);
		
		return bundle;
	}

	@Override
	public int compareTo(ServiceCommand otherCommand) {
		long time1 = getCreationTime();
		long time2 = otherCommand.getCreationTime();
		
		if (time1 < time2) {
			return 1;
		}
		
		if (time1 > time2) {
			return -1;
		}
		
		return 0;
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}
	
	@Override
	public int getRequestId() {
		return requestId;
	}

	protected ResultReceiver getResultReceiver() {
		return resultReceiver;
	}
	
	protected boolean reportError() {
		return reportError;
	}

	@Override
	public String getCommandName() {
		return getClass().getSimpleName();
	}
}