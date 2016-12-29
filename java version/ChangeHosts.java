import java.net.URL;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.io.File;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.util.Properties;

public class ChangeHosts {

	public static void main(String[] args) throws Exception {
		getAllUrls();
	}

	private static void backupHosts(String filepath) throws Exception {
		try {
			System.out.println("backup old hosts");
			InputStream in = new FileInputStream(filepath);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			StringBuffer sb = new StringBuffer();
			String line = br.readLine();
			while (line != null) {
				sb.append(line).append("\n");
				line = br.readLine();
			}
			String content = sb.toString();
			String oldFilepath = System.getProperty("user.dir") + "\\hosts" + System.currentTimeMillis();
			File file = new File(oldFilepath);
			file.createNewFile();
			PrintWriter out = new PrintWriter(oldFilepath);
			out.println(content);
			out.close();
			System.out.println("old hosts back up at " + oldFilepath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * get all hosts from url and write cotent in file
	 * @throws Exception
	 */
	private static void getAllUrls() throws Exception {
		String filepath = System.getProperty("user.dir") + "\\changehosts.properties";
		InputStream inn = new BufferedInputStream(new FileInputStream(filepath));
		//get propertis in file
		Properties p = new Properties();
		p.load(inn);
		String hostsPath = p.getProperty("hostsPath");
		String proxyHost = p.getProperty("proxyHost");
		int proxyPort = Integer.parseInt(p.getProperty("proxyPort"));
		String hostsUrl = p.getProperty("sourceList");
		String[] urls = hostsUrl.split(";");
		backupHosts(hostsPath);
		String content = "";
		for (int i = 0; i < urls.length; i++) {
			content += getHosts(urls[i], proxyHost, proxyPort);
		}
		try {
			//write new hosts to hosts file
			PrintWriter out = new PrintWriter(hostsPath);
			out.println(content);
			out.close();
			System.out.println("hosts file updated!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * get hosts content from url and use http proxy
	 * @param hostsUrl
	 * @param proxyHost
	 * @param proxyPort
	 * @return
	 * @throws Exception
	 */
	private static String getHosts(String hostsUrl, String proxyHost, int proxyPort) throws Exception {
		URL url = new URL(hostsUrl);
		InetSocketAddress addr = new InetSocketAddress(proxyHost, proxyPort);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
		HttpURLConnection con = (HttpURLConnection) url.openConnection(proxy);
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode();
		System.out.println("\nget hosts from " + url);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append("\n");
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}
}