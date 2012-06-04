package ru.isa.ai.psyta.page.lists;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import java.io.File;

/**
 * User: GraffT
 * Date: 02.11.11
 * Time: 18:08
 */
public class WorkKeywordFilePanel extends Panel {
   private File file;

   public WorkKeywordFilePanel(String id, File file) {
      super(id);
      this.file = file;
      setRenderBodyOnly(true);
      init();
   }

   private void init() {
      final Label workText = new Label("workText", !isUsing(file) ? "Использовать" : "Не использовать");
      workText.setOutputMarkupPlaceholderTag(true);

      AjaxLink<String> workLink = new AjaxLink<String>("workLink") {
         @Override
         public void onClick(final AjaxRequestTarget target) {
            WorkKeywordFilePanel.this.doWork(target, file);
            workText.setDefaultModelObject(!isUsing(file) ? "Использовать" : "Не использовать");
            add(new AttributeModifier("class", isUsing(file) ? "c_post_name add_link" : "c_post_name"));

            target.add(workText);
            target.add(this);
         }

         @Override
         protected IAjaxCallDecorator getAjaxCallDecorator() {
            final String linkId = this.getMarkupId();
            return new IAjaxCallDecorator() {

               @Override
               public CharSequence decorateScript(Component component, CharSequence script) {
                  return "$('#" + linkId + "').hide();" + script;
               }

               @Override
               public CharSequence decorateOnSuccessScript(Component component, CharSequence script) {
                  return "$('#" + linkId + "').show();" + script;
               }

               @Override
               public CharSequence decorateOnFailureScript(Component component, CharSequence script) {
                  return null;
               }
            };
         }
      };
      workLink.setOutputMarkupPlaceholderTag(true);
      workLink.add(workText);
      workLink.add(new AttributeModifier("class", isUsing(file)?"c_post_name add_link":"c_post_name"));
      add(workLink);
   }

   protected void doWork(final AjaxRequestTarget target, final File file) {

   }

   public Component getTrobbler() {
      return null;
   }

   public boolean isUsing(final File file) {
      return false;
   }
}
