package volley;

//This class is for storing all URLs as a model of URLs

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.UUID;

import static android.content.Context.TELEPHONY_SERVICE;

public class Config_URL
{
//	private static String base_URL = "http://10.0.2.2:80/";		//Default configuration for WAMP - 80 is default port for WAMP and 10.0.2.2 is localhost IP in Android Emulator
	private static String base_URL = "http://api.sponsorpay.com/feed/v1/offers.json";

    public static String getUrl(String paramString){
        return base_URL+"?"+paramString;
    }

    public static String getUrl(){
        return base_URL;
    }

    // Server user login url
	public static String URL_LOGIN = base_URL+"android_login_api/";

	// Server user register url
	public static String URL_REGISTER = base_URL+"android_login_api/";

	public static String getSha1Hex(String clearString)
	{
		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
			messageDigest.update(clearString.getBytes("UTF-8"));
			byte[] bytes = messageDigest.digest();
			StringBuilder buffer = new StringBuilder();
			for (byte b : bytes)
			{
				buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			}
			return buffer.toString();
		}
		catch (Exception ignored)
		{
			ignored.printStackTrace();
			return null;
		}
	}

    public static String getUUID(Context mContext){
        TelephonyManager teleManager = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
        String tmSerial = teleManager.getSimSerialNumber();
        String tmDeviceId = teleManager.getDeviceId();
        String androidId = android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        if (tmSerial  == null) tmSerial   = "1";
        if (tmDeviceId== null) tmDeviceId = "1";
        if (androidId == null) androidId  = "1";
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDeviceId.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    public static String getLocalIpAddress() throws Exception{
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
             en.hasMoreElements();) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                    return inetAddress.getHostAddress().toString();
                }
            }
        }

        return null;
    }

    public static String getAndroidVersion() {
        String versionName = "Android";

        try {
            versionName = String.valueOf(Build.VERSION.RELEASE);
        } catch (Exception e) {
            ;
        }
        return versionName;
    }
/*
	private static final String URL_JSON_OBJECT = "http://api.androidhive.info/volley/person_object.json";
	private static final String URL_JSON_ARRAY = "http://api.androidhive.info/volley/person_array.json";
	private static final String URL_STRING_REQ = "http://api.androidhive.info/volley/string_response.html";
	private static final String URL_IMAGE = "http://api.androidhive.info/volley/volley-image.jpg";

	//If you need any parameter passed with the URL (GET) - U need to modify this functions
	public static String get_JSON_Object_URL()
	{
		return URL_JSON_OBJECT;
	}

	public static String get_JSON_Array_URL()
	{
		return URL_JSON_ARRAY;
	}

	public static String get_String_URL(String Input)
	{
		if(Input.length()>0) {
			return Input;
		}
		return URL_STRING_REQ;
	}

	public static String get_Image_URL()
	{
		return URL_IMAGE;
	}
	*/
}
