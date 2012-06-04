package ru.isa.ai.psyta;

import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

import java.io.File;

/**
 * User: GraffT
 * Date: 03.11.11
 * Time: 11:40
 */
public class ACMSession extends WebSession {

   public enum WorkStatus {
      WORKING, SUCCESS, FAIL
   }

   private volatile WorkStatus status = null;
   private File analyzingFile;
   private volatile double progress;

   public ACMSession(Request request) {
      super(request);
   }

   public WorkStatus getStatus() {
      return status;
   }

   public void setStatus(WorkStatus status) {
      this.status = status;
   }

   public double getProgress() {
      return progress;
   }

   public void setProgress(double progress) {
      this.progress = progress;
   }

   public File getAnalyzingFile() {
      return analyzingFile;
   }

   public void setAnalyzingFile(File analyzingFile) {
      this.analyzingFile = analyzingFile;
   }
}
