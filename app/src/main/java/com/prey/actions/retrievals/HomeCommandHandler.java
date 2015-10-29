package com.prey.actions.retrievals;

/**
 * Created by oso on 28-10-15.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import android.content.Context;
import android.os.Environment;

import com.prey.PreyLogger;
import android.util.Base64;

public class HomeCommandHandler implements HttpRequestHandler {
    private Context context = null;

    public HomeCommandHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response,
                       HttpContext httpContext) throws HttpException, IOException {


        final String requestLine=request.getRequestLine().toString();

        PreyLogger.i("_______"+requestLine);

        if(requestLine.indexOf("folder")>=0)
            response.setHeader("Content-Type", "text/html");
        else
            response.setHeader("Content-Type", "image/jpeg");

                HttpEntity entity = new EntityTemplate(new ContentProducer() {
                    public void writeTo(final OutputStream outstream) throws IOException {
                        OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8");
                        String resp = responder(requestLine);

                        writer.write(resp);
                        writer.flush();
                    }
                });


        response.setEntity(entity);
    }

    public Context getContext() {
        return context;
    }


    public static String responder(String str) {
        StringBuffer sb = new StringBuffer();
        if(str.indexOf("folder")>=0){
            try {
                int pos = -1;
                try {
                    pos = str.indexOf("?");
                    str = str.substring(pos + 1 + 6 + 1);
                } catch (Exception e) {

                }
                PreyLogger.i("_______" + str);
                try {
                    pos = str.indexOf(" ");
                    str = str.substring(0, pos );
                } catch (Exception e) {

                }
                PreyLogger.i("str_______" + str);

                //str=str.replace("folder=", "");
                //if ("/".equals(str)) {
                File extStore = Environment.getExternalStorageDirectory();
                String path = extStore.getAbsolutePath() + str;
                PreyLogger.i("path_______" + path);
                File tempfile = new File(path);
                File[] files = tempfile.listFiles();


                if (files != null) {
                    for (File checkFile : files) {
                        if (checkFile.isDirectory()) {
                            //  allDirectories.add(checkFile.getName());
                            // listAllDirectories(checkFile.getAbsolutePath());
                            sb.append("<a href=\"?folder=").append(str).append("/").append(checkFile.getName()).append("\">").append(checkFile.getName()).append("</a>/<br>");
                        } else {
                            sb.append("<a href=\"?file=").append(str).append("/").append(checkFile.getName()).append("\">").append(checkFile.getName()).append("</a><br>");
                        }
                    }
                }
            }catch (Exception e){
                PreyLogger.e("Error:"+e.getMessage(),e);
            }

            return sb.toString();
        }else{

            PreyLogger.i("_______" + str);

            int pos = -1;
            try {
                pos = str.indexOf("?");
                str = str.substring(pos + 1 + 4 + 1);
            } catch (Exception e) {

            }
            PreyLogger.i("_______" + str);
            try {
                pos = str.indexOf(" ");
                str = str.substring(0, pos );
            } catch (Exception e) {

            }
            PreyLogger.i("str_______" + str);


            File extStore = Environment.getExternalStorageDirectory();
            String path = extStore.getAbsolutePath()+str;
            File file = new File(path);

            FileInputStream fileInputStream=null;



            byte[] FileBytes = new byte[(int) file.length()];

            try {
                //convert file into array of bytes
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(FileBytes);
                fileInputStream.close();




            }catch(Exception e){
                e.printStackTrace();
            }



            byte[] encodedBytes = Base64.encode(FileBytes, 0);
            String encodedString = new String(encodedBytes);

            return encodedString;
        }
		/*} else {
			return "3456777777777777777777";
		}*/
    }

}
