//////////////////////////////////////////////////
//  Desktop Browser                             //
//  Description:                                //
//     Use Desktop library to launch user's     //
//     default web browser (requires Java 6+).  //
//  http://www.centerkey.com/java/browser       //
//////////////////////////////////////////////////

public class DesktopBrowser {

   public static void main(String[] args) {
      try {
         String url = "http://www.google.com";
         java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
         }
      catch (java.io.IOException e) {
         System.out.println(e.getMessage());
         }
      }

   }
