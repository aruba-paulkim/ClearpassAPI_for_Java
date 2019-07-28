import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ClearpassAPI {

	private final static String API_BASE = "https://{CLEARPASS URL}/api/";
	private final static String CLIENT_ID = "Your Client ID";
	private final static String CLIENT_SECRET = "Your Client Secret";
	
	private static String accessToken = "";
	private final static String USER_AGENT = "Clearpass API for Java";
	
	public static void main(String[] args) throws Exception {
		System.out.println("1. Get Access Token ==========");
		accessToken = getAccessTokenbyCredentials();			
		System.out.println("==> accessToken : " + accessToken);
		System.out.println("");
		
		System.out.println("2. Create Endpoint ==========");
		createEndpoint("112233445566","Known");
		System.out.println("==> Check Clearpass GUI and Press 'y' key ...");
		Scanner reader = new Scanner(System.in);
		reader.next();
		reader.close();
		
		System.out.println("3. Delete Endpoint ==========");
		deleteEndpoint("112233445566");
		System.out.println("==> Check Clearpass GUI");
		System.out.println("");
	}

	private static void deleteEndpoint(String mac) throws Exception {
		URL url = new URL(API_BASE+"endpoint/mac-address/"+mac);
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		con.setRequestMethod("DELETE");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", accessToken);
		
		int responseCode = con.getResponseCode();
		System.out.println("URL : " + url);
		System.out.println("Response Code : " + responseCode);		
	}

	private static void createEndpoint(String mac, String status) throws Exception {
		
		URL url = new URL(API_BASE+"endpoint");
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		String payload = "{\"mac_address\": \""+mac+"\",\"description\": \"Clearpass API for Java\",\"status\": \""+status+"\"}";
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Authorization", accessToken);

		// Send request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(payload);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.toString());
		System.out.println("==> id : " + jsonObject.get("id"));
		System.out.println("==> mac_address : " + (String) jsonObject.get("mac_address"));
		System.out.println("==> description : " + (String) jsonObject.get("description"));
		System.out.println("==> status : " + (String) jsonObject.get("status"));
	}


	private static String getAccessTokenbyCredentials() throws Exception {
		URL url = new URL(API_BASE+"oauth");
		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		String payload = "{\"grant_type\": \"client_credentials\",\"client_id\":\""+CLIENT_ID+"\",\"client_secret\": \""+CLIENT_SECRET+"\"}";
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Content-Type", "application/json");

		// Send request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(payload);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("URL : " + url);
		System.out.println("Post parameters : " + payload);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.toString());
		String auth = (String) jsonObject.get("token_type") + " "+(String) jsonObject.get("access_token");
		
		return auth;
	}
}
