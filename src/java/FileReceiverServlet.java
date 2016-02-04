/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.util.List;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Base64;
import javax.imageio.ImageIO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


/**
 *
 * @author ricardomeira
 */
public class FileReceiverServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        //System.out.println("slice: " + request.);
            
        int slice = 100;//Integer.parseInt(request.getParameter("slice"));
        int direction = 1;
        int operacao = 1;
        short x = 0, y = 0, z = 0;
        BufferedImage img = null;
        boolean temArquivo = false;
        boolean corrigirEndianness = false;
        Volume volume = null;//new Cthead(new DataInputStream(new BufferedInputStream(new FileInputStream(new File("https://s3-us-west-2.amazonaws.com/elasticbeanstalk-us-west-2-737851462034/CThead")))));
        DataInputStream in = null;
        
        
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            
                    
            //Le os itens da requisição
	    for (FileItem item : items) {
                //Se a parte da requisição é diferente do arquivo
                if (item.isFormField()) {
                   String fieldname = item.getFieldName();
                    String fieldvalue = item.getString();
                    switch (fieldname) {
                        case "slice":
                            slice = Integer.parseInt(fieldvalue);
                            break;
                        case "direcao":
                            direction = Integer.parseInt(fieldvalue);
                            break;
                        case "operacao":
                            operacao = Integer.parseInt(fieldvalue);
                            break;
                        case "endianness":
                            corrigirEndianness = Boolean.parseBoolean(fieldvalue);
                            System.out.println("endianness: " + fieldvalue + " : " + corrigirEndianness);
                            break;
                        case "x":
                            x = Short.parseShort(fieldvalue);
                            break;
                        case "y":
                            y = Short.parseShort(fieldvalue);
                            break;
                        case "z":
                            z = Short.parseShort(fieldvalue);
                            break;
                        default:
                            break;
                    }
                }   
                //Se a parte da requisoção é o arquivo
	        else {
	            temArquivo = true;
                    InputStream fileContent = item.getInputStream();
                    File file = File.createTempFile("temp", ".tmp");
                    copyInputStreamToFile(fileContent,file);
                    in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                    //cthead = new Cthead(in);
                    //Gui window = new Gui(cthead);
	         }
            }
            
            volume = new Volume(in, x, y, z, corrigirEndianness);

            switch(operacao) {
                case 1:
                    img = volume.getImage(slice, direction);
                    break;
                case 2:
                    img = volume.resizeImage(slice, direction, 256, direction == 1 ? 256 : 113, 512, 512, Cthead.NEAREST_NEIGHBOUR);
                    break;
                case 3:
                    img = volume.resizeImage(slice, direction, 256, direction == 1 ? 256 : 113, 512, 512, Cthead.BILINEAR_INTERPOLATION);
                    break;
                case 4:
                    img = volume.maximumIntensityProjection(direction);
                    break;
                case 5:
                    VolumeHistogram volumeHistogram = new VolumeHistogram(volume);
                    volumeHistogram.fill();
                    VolumeHistogram mapping = volumeHistogram.histogramEqualization(0, 255);
                    img = volume.equalizeImage(mapping, slice, direction);
                    break;
                default:
                    img = volume.getImage(slice, direction);
                    
            }
            response.setContentType("image/jpeg");
//          
            OutputStream out = response.getOutputStream();
            
            Base64.Encoder b64enc = Base64.getEncoder();
            out = b64enc.wrap(out);

            ImageIO.write(img, "jpg", out);
            
            //out.write(img.);
            out.flush();
            out.close();
        } catch (FileUploadException ex) {
           // Logger.getLogger(FileReceiverServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    	/**
	 * Salva o arquivo na maquina do servidor
	 * @param in : O leitor do arquivo
	 * @param file: O arquivo a ser salvo
	 */
	private void copyInputStreamToFile( InputStream in, File file ) {
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
        
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
