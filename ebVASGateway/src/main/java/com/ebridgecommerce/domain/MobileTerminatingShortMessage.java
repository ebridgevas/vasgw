package com.ebridgecommerce.domain;

/**
*
* @author DaTekeshe
*/
public class MobileTerminatingShortMessage extends ShortMessage {

   private String id;

   public void setId(String id) {
       this.id = id;
   }
   
   public MobileTerminatingShortMessage() {
   }
   
   public String getId() {
       return id;
   }
   
   @Override
   public String toString() {
       return "MTSM: src = " + getSourceMsisdn() + ", dst = " + getDestinationMsisdn() + ", text = " + this.getText();
   }

   ////////////// TEMPLATES ///////////////
   public static MobileTerminatingShortMessage template() {
       return new MobileTerminatingShortMessage();
   }

   public MobileTerminatingShortMessage destinationMsisdn(String destinationMsisdn) {
       this.setDestinationMsisdn( destinationMsisdn );
       return this;
   }

   public MobileTerminatingShortMessage sourceMsisdn(String sourceMsisdn) {
       this.setSourceMsisdn( sourceMsisdn );
       return this;
   }

   public MobileTerminatingShortMessage text(String text) {
       this.setText( text );
       return this;
   }

   public MobileTerminatingShortMessage status(Status status ) {
       this.setStatus( status );
       return this;
   }

   public MobileTerminatingShortMessage originalShortMessage(String originalShortMessage ) {
       this.setOriginalShortMessage( originalShortMessage );
       return this;
   }

   public MobileTerminatingShortMessage originalSmscId(String originalSmscId ) {
       this.setOriginalSmscId( originalSmscId );
       return this;
   }

   public MobileTerminatingShortMessage forwardingSmscId(String forwardingSmscId ) {
       this.setForwardingSmscId( forwardingSmscId );
       return this;
   }
   public MobileTerminatingShortMessage jsmQueueName(String jsmQueueName ) {
       this.setJmsQueueName( jsmQueueName );
       return this;
   }
}
