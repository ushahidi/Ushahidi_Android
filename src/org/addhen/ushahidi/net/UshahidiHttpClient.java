package org.addhen.ushahidi.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import org.addhen.ushahidi.UshahidiService;

public class UshahidiHttpClient {
    
	private static final int IO_BUFFER_SIZE = 512;
    
    final public static List<NameValuePair> blankNVPS = new ArrayList<NameValuePair>();
	
    public static HttpResponse PostURLWithCookies(String URL, List<NameValuePair> data,
			String Username, String Password) throws IOException {
    	while(UshahidiService.httpRunning){
    		try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	UshahidiService.httpRunning = true;
		
    	if ((Username.length() > 0) && (Password.length() > 0)) {
			UshahidiService.httpclient.getCredentialsProvider().setCredentials(
					new AuthScope(null, -1),
					new UsernamePasswordCredentials(Username, Password));
		}
    	
		final HttpGet httpget = new HttpGet(URL);
		 HttpResponse response;
         try {
              response = UshahidiService.httpclient.execute(httpget);
             HttpEntity entity = response.getEntity();
             
              if (entity != null) {
                  entity.consumeContent();
              }
              
         } catch (final ClientProtocolException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}

		final HttpPost httpost = new HttpPost(URL);

		try {
			//NEED THIS NOW TO FIX ERROR 417 
			httpost.getParams().setBooleanParameter( "http.protocol.expect-continue", false );	
			httpost.setEntity(new UrlEncodedFormEntity(data, HTTP.UTF_8));
		} catch (final UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			UshahidiService.httpRunning = false;
			return null;
		}

		// Post, check and show the result (not really spectacular, but works):
		try {
			response =  UshahidiService.httpclient.execute(httpost);
			UshahidiService.httpRunning = false;
			return response;

		} catch (final ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UshahidiService.httpRunning = false;
		return null;
    }
    
    public static HttpResponse GetURL(String URL) throws IOException {
    	UshahidiService.httpRunning = true;
		
		final HttpGet httpget = new HttpGet(URL);
		httpget.addHeader("User-Agent", "Ushahidi-Android/1.0)");

		// Post, check and show the result (not really spectacular, but works):
		try {
			HttpResponse response =  UshahidiService.httpclient.execute(httpget);
			UshahidiService.httpRunning = false;
			
			return response;

		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UshahidiService.httpRunning = false;
		return null;
    }
    
	public static HttpResponse PostURL(String URL, List<NameValuePair> data,
			String Referer) throws IOException {
		UshahidiService.httpRunning = true;

		final HttpPost httpost = new HttpPost(URL);
		//org.apache.http.client.methods.
		if(Referer.length() > 0){
			httpost.addHeader("Referer", Referer);
		}
		if(data != null){
			try {
				//NEED THIS NOW TO FIX ERROR 417
				httpost.getParams().setBooleanParameter( "http.protocol.expect-continue", false ); 
				httpost.setEntity(new UrlEncodedFormEntity(data, HTTP.UTF_8));
			} catch (final UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				UshahidiService.httpRunning = false;
				return null;
			}
		}

		// Post, check and show the result (not really spectacular, but works):
		try {
			HttpResponse response =  UshahidiService.httpclient.execute(httpost);
			UshahidiService.httpRunning = false;
			return response;

		} catch (final Exception e) {

		} 
		UshahidiService.httpRunning = false;
		return null;
	}
	
	public static HttpResponse PostURL(String URL, List<NameValuePair> data) throws IOException {
		return PostURL(URL, data, "");
	}
	
    public static boolean PostFileUpload(String URL, String FileName, String Username, String Password) throws IOException{
        ClientHttpRequest req = null;

        try {
             URL url = new URL(URL);
             req = new ClientHttpRequest(url);
             req.setParameter("media", new File(FileName));
             InputStream serverInput = req.post();
             if(GetText(serverInput).contains("<rsp status=\"ok\">")){
            	 return true;
             }
        } catch (MalformedURLException ex) {
        	//fall through and return false
        }
        return false;
   }
	public static byte[] fetchImage(String address) throws MalformedURLException, IOException {
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new BufferedInputStream(new URL(address).openStream(),
                    IO_BUFFER_SIZE);

            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 4 * 1024);
            copy(in, out);
            out.flush();

            return dataStream.toByteArray();
        } catch (IOException e) {
            //android.util.Log.e("IO", "Could not load buddy icon: " + this, e);
        } finally {
            closeStream(in);
            closeStream(out);
            
        } 
        return null;
		/*final URL url = new URL(address);
		final Object content = url.getContent();
		return content;*/
	}
	/**
     * Copy the content of the input stream into the output stream,
using a temporary
     * byte array buffer whose size is defined by {@link #IO_BUFFER_SIZE}.
     *
     * @param in The input stream to copy from.
     * @param out The output stream to copy to.
     *
     * @throws IOException If any error occurs during the copy.
     */
    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[4 * 1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    /**
     * Closes the specified stream.
     *
     * @param stream The stream to close.
     */
    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                android.util.Log.e("IO", "Could not close stream", e);
            }
        }
    } 
	public static String GetText(HttpResponse response) {
		String text = "";
		try {
			text = GetText(response.getEntity().getContent());
		} catch (final Exception ex) {
		}
		return text;
	}
	public static String GetText(InputStream in) {
		String text = "";
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				in), 1024);
		final StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			text = sb.toString();
		} catch (final Exception ex) {
		} finally {
			try {
				in.close();
			} catch (final Exception ex) {
			}
		}
		return text;
	}
}

