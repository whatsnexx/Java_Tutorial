Java Example
============

Describes how to send an event to the Whatsnexx TicketBusService RESTful web service from a Java project in the Eclipse IDE. In this example, the request is sent by making an HttpUrlConnection and sending the request through the connections OutputStream. The response is then read using a Scanner and displayed as a String.

<h5>View examples in <a href="https://github.com/whatsnexx/C_Sharp_Tutorial">C#</a> and <a href="https://github.com/whatsnexx/PHP_Tutorial">PHP</a>.</h5>
Getting Started
------------------------------  
The whatsnexx [ticketbus](https://github.com/whatsnexx/whatsnexx.github.com/wiki/7.-Ticket-Bus-Introduction) handles REST and SOAP Web Service request to send events. In this example, we will demonstrate how to send and event to the Whatsnexx ticketbus by creating a REST client using Java. You will need following to send a request to the [ticketbus](https://github.com/whatsnexx/whatsnexx.github.com/wiki/7.-Ticket-Bus-Introduction) service.
<table width="100%" border="1px">
<tr><th align="left">Attribute</th><th align="left">Type</th><th align="left">Description</th></tr>
<tr><td>Account Id</td><td>GUID</td><td> Provided by Whatsnexx.</td></tr>
<tr><td>Username:</td><td>GUID</td><td>Provided by Whatsnexx.</td></tr>
<tr><td>Password:</td><td>GUID</td><td>Provided by Whatsnexx.</td></tr>
<tr><td>TermName:</td><td>string</td><Td>This is the name of the event that is to be triggered by the send event.</td>
<tr><td>SubjectCode:</td><td>string</td><Td>The unique identifier for your [subject](). This usually represents <b>who</b> you would like to send the event to.</td></tr>
<tr><td>SubjectTypeId:</td><td>GUID</td><td>A unique identitfier for the subject type. The subject type defines the context under which events are sent.</td></tr>
<tr><td>ExecutionEnvironment:</td><td>TicketBusService.ExecutionEnvironments</td><td>Specifies the Whatsnexx environment you are sending the event request. A <b>Constellation</b> must exist in the chosen environment for the event to be triggered. The available Environments are: Test, Stage, and Production.</td></tr>
<tr><td>Attributes:</td><td>TicketBusService.Attributes[]</td><Td>A list of attributes that are used by the event.</td></tr>
</table>

Steps
-----------
<b>Important:</b> You will need to insert your accountId, SubjectTypeId and etc in place of the tags marked in the code. The tags are indicated by curly brackets containing the name of the required field. For example, {accountId}, indicates you need to replace this tag with your actual account id.  

<b>Note:</b> This example uses Java 7 with Apache Tomcat 7. If you do not have JDK 7 installed you can download it from <a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">here</a>. After you have installed JDK 7, you can set it as the Eclipse default runtime by following these steps:
<ol type="i">
	<li>Select Windows->Preferences: The <b>Preferences</b> dialog will open.</li>
	<li>In the left column select Java->Installed JREs: A list of installed JREs will appear in the right hand side of the dialog</li>
	<li>Select <b>Add</b>:  An <b>Add JRE</b> dialog box will open</li>
	<li>Click <b>Next</b>.</li>
	<li>Click <b>Directory</b> and select the root directory of the JDK. If you are using Windows, it will likely be in the location <i>C:\Program Files (x86)\Java\jdk1.7.x</i></li>
	<li>Click <b>Finish</b>.</li>
</ol>

###1. Create Project
If you are familiar with Eclipse, Create a <b>Dynamic Web Project</b> using Apache 7 and JDK 7 and add a Main function class. If you need more detail, follow the steps below to create the project.
<ol type="i">
	<li>Right click in <b>Project Explorer</b> and select New->Project</li>
	<li>Select Web->Dynamic Web Project: the <b>New Dynamic Web Project</b> dialog will open.</li>
	<li>Enter "TicketBusExample" in the Name field</li>
	<li>Select Apache Tomcat 7.0 as the target runtime. If you do not have Apache 7.0 installed already, you can learn how to do this <a href="http://www.eclipse.org/webtools/jst/components/ws/M4/tutorials/InstallTomcat.html">here</a>. The <b>New Server Runtime</b> dialog can be opened directly from the <b>New Dynamic Web Project</b> dialog by selecting <b>New Runtime</b>.</li>
	<li>Accept the default configuration by clicking <b>Finish</b>.</li>
	<li>Expand the <b>Java Resources</b> and right click on the <b>src</b> folder.</li>
	<li>Select New->Package and name the package "script".</li>
	<li>Right click on "script" and select New->Class. A <b>New Java Class</b> dialog will open.
		<ul>
			<li>Name the class "Main"</li>
			<li>Under "<i>Which method stubs would you like to create?</i>" select "<i>public static void main(Strings[] args)</i>"</li>
			<li>Click <b>finish</b>.</li>
		</ul>
	</li>
</ol>
###2. Add Required Imports
```java
	import java.io.IOException;
	import java.io.OutputStream;
	import java.net.URL;
	import java.security.KeyManagementException;
	import java.security.NoSuchAlgorithmException;
	import java.security.SecureRandom;
	import java.security.cert.X509Certificate;
	import java.util.Scanner;
	
	import javax.net.ssl.HttpsURLConnection;
	import javax.net.ssl.SSLContext;
	import javax.net.ssl.TrustManager;
	import javax.net.ssl.X509TrustManager;
	
	import org.apache.catalina.util.Base64;
```
###2. Create a TrustManager to ignore certificates
When using Java to connect to a web service, the certificate for the service must be included in the trusted certificates for Java. The Whatsnexx TicketBus uses username and password authentication without certificates. A work around to ignore the certificate in Java, is to create a TrustManager that trusts all certificates.  Add the following in the main(Strings[] args) function.
```java
		
		String response = ""; //Will be used to keep the response string after service is called.
			
		final TrustManager[] trustAllCerts = new X509TrustManager[] { new X509TrustManager() {
	        @Override
	        public void checkClientTrusted( final X509Certificate[] chain, final String authType ) {
	        }
	        @Override
	        public void checkServerTrusted( final X509Certificate[] chain, final String authType ) {
	        }
	        @Override
	        public X509Certificate[] getAcceptedIssuers() {
	            return null;
	        }
	    } };
```  

###3. Create SSLContext with new TrustManager
Import <b>javax.net.ssl.SSLContext</b> and <b>java.security.SecureRandom</b> and add the following to the main function. Eclipse will prompt you to add a Try/Catch block or throws declaration.
```java
		SSLContext sslContext = SSLContext.getInstance("SSL");

		sslContext.init(null, trustAllCerts, new SecureRandom());
```
###4. Open an HttpUrlConnection and set the SocketFactory to the one that you just created
```java
		URL url = new URL("https://ticketbus.whatsnexx.com/Rest/TicketBusService.svcSendEvent/{accountId}/{subjectTypeId}/{termName}/{executionEnvironment}/{subjectCode|");
		
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		
		conn.setSSLSocketFactory(sslContext.getSocketFactory());
```
###5. Add username and password authorization
```java
		String auth = "{username}:{password}";
		byte[] authBytes = auth.getBytes();
		String authEncoded = Base64.encode(authBytes);
		conn.setRequestProperty("Authorization", authEncoded);
```
###6. Setup request properties appropriately
```java
		conn.setDoOutput(true);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "text/xml");
```

###7. Create AttributeList
The <b>AttributeList</b> will makeup the body of the resquest. 
```java
		String input = "<AttributeList xmlns=\"http://schemas.whatsnexx.com/v1/tbx/\"><Attribute><Name>Yo</Name><Value>Dude</Value></Attribute></AttributeList>";
```
###8. Write to and flush the HttpUrlConnection OutputStream to send the request
```java
		OutputStream os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();
```

###9. Retrieve the HttpUrlConnection's InputStream and read the response message
```java
		Scanner scanner;
		
		if (conn.getResponseCode() != 200) {
			response += conn.getResponseCode()
					+ " error returned from server";
			if (conn.getErrorStream() != null) {
				scanner = new Scanner(conn.getErrorStream());
				response += "Error From Server \n\n";
				scanner.useDelimiter("\\Z");
				response += scanner.next();
				scanner.close();
			}
		} else {
			scanner = new Scanner(conn.getInputStream());
			response += "Response From Server \n\n";
			scanner.useDelimiter("\\Z");
			response += scanner.next();
			scanner.close();
		}
		conn.disconnect();
		} catch (NoSuchAlgorithmException | KeyManagementException | IOException e) {
			e.printStackTrace();
		} 
		System.out.println(response);
```

Getting Help
-----------
[Whatsnexx API Documentation](http://whatsnexx.github.com)  


*****
[Top](#)

