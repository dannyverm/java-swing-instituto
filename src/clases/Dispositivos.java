/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package clases;

import PaqFormulario.DialogoCapturarFoto;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.Buffer;
import javax.media.CannotRealizeException;
import javax.media.CaptureDeviceInfo;
import javax.imageio.*;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.cdm.CaptureDeviceManager;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.VideoFormat;
import javax.media.util.BufferToImage;
import javax.swing.JPanel;

/**
 *
 * @author Cmop
 */
public class Dispositivos {

    private DialogoCapturarFoto padre;
    private Player player;

    public Dispositivos(DialogoCapturarFoto padre)
    {
        this.padre=padre;
    }

    public String verInfoDispositivos()
    {
      String rpta="";
      Vector listaDispositivos = null;
      //Buscamos los dispositivos instalados
      listaDispositivos = CaptureDeviceManager.getDeviceList();
      Iterator it = listaDispositivos.iterator();
      while (it.hasNext())
      {
        CaptureDeviceInfo cdi = (CaptureDeviceInfo)it.next();
        rpta+=cdi.getName()+"\n";
        //cdi.getName() --> Obtiene el nombre del Dispositivo Detectado
      }
      if(rpta.compareTo("")!=0)
          rpta="Dispositivos detectados:\n\n"+rpta;
      else
          rpta="Sin Dispositivos Detectados";
      
      return rpta;
    }

    public void MuestraWebCam(JPanel panelCam,String dispositivo,String FormatoColor)
    {
        try {
            if (player != null) {
                return;
            }
            CaptureDeviceInfo dev = CaptureDeviceManager.getDevice(dispositivo);
            //obtengo el locator del dispositivo
            MediaLocator loc = dev.getLocator();
            player = Manager.createRealizedPlayer(loc);
            player.start();
            // esto lo saqué del foro jmf de Sun, hay que "parar un poco la aplicacion"
            Thread.sleep(1000);
            Component comp;
            if ((comp = player.getVisualComponent()) != null) {
                // mostramos visualmente el reproductor
                panelCam.add(comp, BorderLayout.CENTER);
                padre.pack();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Dispositivos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Dispositivos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoPlayerException ex) {
            Logger.getLogger(Dispositivos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CannotRealizeException ex) {
            Logger.getLogger(Dispositivos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void CapturaFoto()
    {
        Image img=null;
        FrameGrabbingControl fgc = (FrameGrabbingControl)
        player.getControl("javax.media.control.FrameGrabbingControl");
        Buffer buf = fgc.grabFrame();
        // creamos la imagen awt
        BufferToImage btoi = new BufferToImage((VideoFormat)buf.getFormat());
        img = btoi.createImage(buf);

        if (img != null)
        {                 
                String imagen = null;
                System.out.println("imagen: "+imagen);
                imagen = "src\\paqImagenes\\foto.JPG";
                System.out.println("imagen:"+imagen);
                File imagenArch = new File(imagen);
                String formato = "JPG";
                 try{
                   ImageIO.write((RenderedImage) img,formato,imagenArch);
                }catch (IOException ioe){System.out.println("Error al guardar la imagen");}
            player.close();
        }
        else
        {
            javax.swing.JOptionPane.showMessageDialog(padre, "A ocurrido un error!!");
        }
        img=null;
        player.close();
     }
}