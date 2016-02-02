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
import java.io.PrintWriter;
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
        
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            
            //Le os itens da requisição
	    for (FileItem item : items) {
                //Se a parte da requisição é diferente do arquivo
                if (item.isFormField()) {
                   PrintWriter out = response.getWriter();
                   out.print("entrou aqui");
                }   
                //Se a parte da requisoção é o arquivo
	        else {
	            InputStream fileContent = item.getInputStream();
	       
	            File file = new File("/Users/guilherme/Desktop/CThead");
	            copyInputStreamToFile(fileContent,file);
                    
                    DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                    Cthead cthead = new Cthead(in);
                    BufferedImage img = cthead.getImage(20, 2);
                    response.setContentType("image/jpeg");
                    OutputStream out = response.getOutputStream();
                    ImageIO.write(img, "jpg", out);
                    //Gui window = new Gui(cthead);
	            }
            }
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
