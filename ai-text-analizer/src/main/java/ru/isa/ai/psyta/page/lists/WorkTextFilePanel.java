package ru.isa.ai.psyta.page.lists;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.time.Duration;
import ru.isa.ai.psyta.ACMSession;

import java.io.File;

/**
 * User: GraffT
 * Date: 02.11.11
 * Time: 17:29
 */
public class WorkTextFilePanel extends Panel {

   private File file;
   private Label workHint;
   private AjaxLink<String> workLink;

   private class ProgressBehavior extends AjaxSelfUpdatingTimerBehavior {
      public ProgressBehavior() {
         super(Duration.seconds(5));
      }

      protected void onPostProcessTarget(final AjaxRequestTarget target) {
         ACMSession session = (ACMSession) getSession();
         if (session.getStatus() == ACMSession.WorkStatus.SUCCESS) {
            getComponent().setDefaultModelObject("Проанализировано... 100%");
            markAnalyzingSuccess(target);

            workLink.setVisible(true);
            getComponent().setVisible(false);
            WorkTextFilePanel.this.getTrobbler().setVisible(false);
            this.stop();
            target.add(workLink);
            target.add(WorkTextFilePanel.this.getTrobbler());
         } else if (session.getStatus() == ACMSession.WorkStatus.FAIL) {
            getComponent().setDefaultModelObject("Проанализировано... 100%");
            markAnalyzingFail(target);

            workLink.setVisible(true);
            getComponent().setVisible(false);
            WorkTextFilePanel.this.getTrobbler().setVisible(false);
            this.stop();
            target.add(workLink);
            target.add(WorkTextFilePanel.this.getTrobbler());
         } else if (session.getStatus() == ACMSession.WorkStatus.WORKING) {
            getComponent().setDefaultModelObject(String.format("Анализируется... %.1f", session.getProgress() * 100) + "%");
         }
      }

   }

   public WorkTextFilePanel(String id, File file) {
      super(id);
      this.file = file;
      setRenderBodyOnly(true);
      init();
   }

   private void init() {
      workHint = new Label("workHint", "Анализируется... 0.0%");
      workHint.setOutputMarkupPlaceholderTag(true);
      add(workHint);

      final Label workText = new Label("workText", "Анализировать");
      workLink = new AjaxLink<String>("workLink") {
         @Override
         public void onClick(final AjaxRequestTarget target) {
            workHint.setDefaultModelObject("Анализируется... 0.0%");
            workLink.setVisible(false);
            workHint.setVisible(true);
            WorkTextFilePanel.this.getTrobbler().setVisible(true);
            workHint.add(new ProgressBehavior());
            WorkTextFilePanel.this.startWork(target, file);

            target.add(workLink);
            target.add(workHint);
            target.add(WorkTextFilePanel.this.getTrobbler());
         }

      };
      workLink.setVisible(true);
      workLink.setOutputMarkupPlaceholderTag(true);
      workLink.add(workText);
      add(workLink);

      if (((ACMSession) getSession()).getStatus() == ACMSession.WorkStatus.WORKING &&
              ((ACMSession) getSession()).getAnalyzingFile().equals(file)) {
         workHint.add(new ProgressBehavior());
         workHint.setVisible(true);
         workLink.setVisible(false);

         workHint.setDefaultModelObject(String.format("Анализируется... %.1f", ((ACMSession) getSession()).getProgress() * 100) + "%");
      } else {
         workHint.setVisible(false);
         workLink.setVisible(true);
      }
   }

   protected void startWork(final AjaxRequestTarget target, final File file) {

   }

   public void markAnalyzingSuccess(final AjaxRequestTarget target) {

   }

   public void markAnalyzingFail(final AjaxRequestTarget target) {

   }

   public Component getTrobbler() {
      return null;
   }
}
