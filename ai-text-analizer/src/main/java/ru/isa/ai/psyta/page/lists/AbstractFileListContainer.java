package ru.isa.ai.psyta.page.lists;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.file.Files;
import org.apache.wicket.util.file.Folder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * User: GraffT
 * Date: 02.11.11
 * Time: 17:00
 */
public abstract class AbstractFileListContainer extends Panel {

   protected Folder filesFolder;
   private WebMarkupContainer fileContainer;
   private WebMarkupContainer fileStubContainer;

   private class FileListView extends ListView<File> {

      public FileListView(String name, final IModel<List<File>> files) {
         super(name, files);
      }

      @Override
      protected void populateItem(ListItem<File> listItem) {
         final File file = listItem.getModelObject();
         listItem.add(new Label("fileName", file.getName()));
         listItem.add(new AjaxLink<Void>("deleteLink") {
            @Override
            public void onClick(final AjaxRequestTarget target) {
               if (Files.remove(file)) {
                  info("Файл удалён: " + file.getName());
                  afterFileDeleting(target);
               }

               target.add(AbstractFileListContainer.this.fileContainer);
               target.add(AbstractFileListContainer.this.fileStubContainer);
            }
         });
         listItem.add(AbstractFileListContainer.this.getWorkPanel("workPanel", file));

         listItem.add(new Label("uploadedDate", new SimpleDateFormat("dd.MM.yyyy HH.mm").format(new Date(file.lastModified()))));
         listItem.add(new Label("uploadedSize", String.format("%7.1fK", (file.length() + 0.0) / 1024)));
      }
   }

   public AbstractFileListContainer(String id, String title, Folder filesFolder) {
      super(id);
      this.filesFolder = filesFolder;
      setOutputMarkupPlaceholderTag(true);
      init(title);
   }

   private void init(String title) {
      add(new Label("title", new Model<String>(title)));

      WebMarkupContainer filesContainer = new WebMarkupContainer("filesContainer") {
         public boolean isVisible() {
            return filesFolder.listFiles().length > 0;
         }
      };
      fileContainer = new WebMarkupContainer("fileContainer");
      FileListView textsListView = new FileListView("fileList", new LoadableDetachableModel<List<File>>() {
         @Override
         protected List<File> load() {
            return Arrays.asList(AbstractFileListContainer.this.filesFolder.listFiles());
         }

      });
      fileContainer.setOutputMarkupPlaceholderTag(true);
      fileContainer.add(textsListView);
      filesContainer.add(fileContainer);
      add(filesContainer);

      fileStubContainer = new WebMarkupContainer("fileStubContainer") {
         public boolean isVisible() {
            return filesFolder.listFiles().length < 1;
         }
      };
      fileStubContainer.setOutputMarkupPlaceholderTag(true);
      add(fileStubContainer);
   }

   public void afterFileDeleting(final AjaxRequestTarget target) {

   }

   protected abstract Panel getWorkPanel(String id, File file);

}
