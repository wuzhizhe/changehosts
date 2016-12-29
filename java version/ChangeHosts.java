import java.net.URL;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ChangeHosts {

	private final static String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {
		getAllUrls();
	}

	/**
	 * 去读本地配置文件，并循环获取需要的hosts最终设置到本地hosts中
	 * @throws Exception
	 */
	private static void getAllUrls() throws Exception {
		String filepath = System.getProperty("user.dir") + "\\changehosts.properties";
		InputStream inn = new BufferedInputStream(new FileInputStream(filepath));
		//读取properties文件内容
		Properties p = new Properties();
		p.load(inn);
		String hostsPath = p.getProperty("hostsPath");
		String proxyHost = p.getProperty("proxyHost");
		int proxyPort = Integer.parseInt(p.getProperty("proxyPort"));
		String hostsUrl = p.getProperty("sourceList");
		String[] urls = hostsUrl.split(";");
		String content = "";
		for (int i = 0; i < urls.length; i++) {
			content += getHosts(urls[i], proxyHost, proxyPort);
		}
		try {
			//将content写入hosts文件
			PrintWriter out = new PrintWriter(hostsPath);
			out.println(content);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据hosts源以及设置的proxy获取内容
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
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
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