package com.wizarpos.wizarviewagentassistant.aidl;
interface IAPNManagerService{
	String addByAllArgs(String name, String apn, String mcc, String mnc, String proxy, String port, String MMSProxy, String MMSPort, String userName, String server, String password, String MMSC,
				String authType, String protocol, String roamingProtocol, String type, String bearer, String MVNOType, String MVNOMatchData);
	String add(String name, String apn);
	String addByMCCAndMNC(String name, String apn, String mcc, String mnc);
	boolean setSelected(String name);
	boolean clear();
}